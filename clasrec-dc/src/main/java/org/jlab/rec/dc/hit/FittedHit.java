package org.jlab.rec.dc.hit;

import static java.lang.Math.cos;
import org.jlab.rec.dc.Constants;
import org.jlab.rec.dc.GeometryLoader;
/**
 * A hit that was used in a fitted cluster.  It extends the Hit class and contains local and sector coordinate information at the MidPlane.  
 * An estimate for the Left-right Ambiguity is assigned based on the linear fit to the wire position residual.
 * 
 * @author ziegler
 *
 */

public class FittedHit extends Hit implements Comparable<Hit> {


	/**
	 * 
	 * @param sector  (1...6)
	 * @param superlayer (1...6)
	 * @param layer (1...6)
	 * @param wire (1...112)
	 * @param time (for gemc output without digitization)
	 */
	
	public FittedHit(int sector, int superlayer, int layer, int wire,
			double time, double timeEr, int id) {
		super(sector, superlayer, layer, wire, time, timeEr, id);
		
		this.set_lX(layer);
		this.set_lY(layer, wire);
	}

	private double _X;              // X at the MidPlane in sector coord. system
	private double _Z;              // Z at the MidPlane in the sector coord. system
	private double _lX;				// X in local coordinate system used in hit-based fit to cluster line
	private double _lY;				// Y in local coordinate system used in hit-based fit to cluster line
	private double _Residual;		// cluster line  to the wire position resid
	private double _TimeResidual=0;	// cluster line  to the wire position time-resid
	private int _LeftRightAmb;		// Left-Right Ambiguity value	-1 --> y-fit <0 --> to the left of the wire ==> y = y-_leftRight*TimeToDist
	
	private double _QualityFac ;	
	private int _TrkgStatus = -1 ;	//  TrkgStatusFlag factor (-1: no fit; 0: hit-based trking fit; 1: time-based trking fit)
	private double _ClusFitDoca;
	private double _TrkFitDoca;
	private double _TimeToDistance =0;
	
	/**
	 * 
	 * @return the local hit x-position in the local superlayer coordinate system;
	 * used in cluster-finding algo. to fit the hit-based wire positions
	 */
	public double get_lX() {
		return _lX;
	}

	/**
	 * 
	 * @param layerValue layer number from 1 to 6
	 */
	public void set_lX(double layerValue) {
		this._lX = layerValue;
	}

	/**
	 * 
	 * @return the local hit y-position in the local superlayer coordinate system;
	 * used in cluster-finding algo. to fit the hit-based wire positions
	 */
	public double get_lY() {
		return _lY;
	}
	
	/**
	 * 
	 * @param layer layer number from 1 to 6
	 * @param wire wire number from 1 to 112 
	 * sets the center of the cell as a function of wire number in the local superlayer coordinate system.
	 */
	public void set_lY(int layer, int wire) {
		double y = this.calcLocY(layer, wire);
		this._lY = y;
	}
	/**
	 * 
	 * @param layer layer number from 1 to 6
	 * @param wire wire number from 1 to 112 
	 * calculates the center of the cell as a function of wire number in the local superlayer coordinate system.
	 *//*
	public double calcLocY(int layer, int wire) {
		
		// in old mc, layer 1 is closer to the beam than layer 2, in hardware it is the opposite
		double  brickwallPattern = GeometryLoader.dcDetector.getSector(0).getSuperlayer(0).getLayer(1).getComponent(1).getMidpoint().x()
				- GeometryLoader.dcDetector.getSector(0).getSuperlayer(0).getLayer(0).getComponent(1).getMidpoint().x();
		
		double brickwallSign = Math.signum(brickwallPattern);
		System.out.println(" breick wall "+brickwallSign+"  simul "+Constants.isSimulation);
		//center of the cell asfcn wire num
		double y= (double)wire*(1.+0.25*Math.sin(Math.PI/3.)/(1.+Math.sin(Math.PI/6.)));
		
		if(layer%2==1) {
			y = y-brickwallSign*Math.sin(Math.PI/3.)/(1.+Math.sin(Math.PI/6.));
		}
		return y;
	}*/
	
	/**
	 * 
	 * @return The approximate uncertainty on the hit position using the inverse of the gemc smearing function
	 */
	public double get_PosErr() {
		
		double err = this.get_CellSize()/Math.sqrt(12.);
		
		if(this._TrkgStatus!=-1 && this.get_TimeToDistance()!=0) {
			
			err = this.get_TimeErr()*Constants.TIMETODIST[this.get_Region()-1]; 
			
		}
		
		return err/100.;
	}
	
	/**
	 * 
	 * @return the cell size in a given superlayer
	 *//*
	public double get_CellSize() {
		
		double cellSize  = GeometryLoader.dcDetector.getSector(0).getSuperlayer(this.get_Superlayer()-1).getLayer(1).getComponent(10).getMidpoint().z()
	                     - GeometryLoader.dcDetector.getSector(0).getSuperlayer(this.get_Superlayer()-1).getLayer(0).getComponent(10).getMidpoint().z();
		
		return (cellSize/2.);
	}*/

	/**
	 * 
	 * @return the time residual  |fit| - |y| from the fit to the wire positions in the superlayer
	 */
	public double get_TimeResidual() {
		return _TimeResidual;
	}
	
	/**
	 * 
	 * @param _TimeResidual  the residual |fit| - |y| from the fit to the hit positions in the superlayer
	 */
	public void set_TimeResidual(double _TimeResidual) {
		this._TimeResidual = _TimeResidual;
	}
	
	/**
	 * 
	 * @return the residual from the fit to the wire positions in the superlayer
	 */
	public double get_Residual() {
		return _Residual;
	}
	
	/**
	 * 
	 * @param _Residual  the residual from the fit to the hit positions in the superlayer
	 */
	public void set_Residual(double _Residual) {
		this._Residual = _Residual;
	}

	/**
	 * 
	 * @return an integer representative of the estimate of the left-right ambiguity obtained from
	 * pattern recognition. -1(+1): the track went to the left(right) of the wire; 0: the left-right 
	 * ambiguity could not be resolved.
	 */
	public int get_LeftRightAmb() {
		return _LeftRightAmb;
	}

	/**
	 * 
	 * @param leftRightAmb  an integer representative of the estimate of the left-right ambiguity obtained from
	 * pattern recognition. -1(+1): the track went to the left(right) of the wire; 0: the left-right 
	 * ambiguity could not be resolved.
	 */
	public void set_LeftRightAmb(int leftRightAmb) {
		this._LeftRightAmb =leftRightAmb;		
	}

	/**
	 * 
	 * @return a quality factor representative of the quality of the fit to the hit
	 */
	public double get_QualityFac() {
		return _QualityFac;
	}

	/**
	 * 
	 * @param _QualityFac is a quality factor representative of the quality of the fit to the hit
	 */
	public void set_QualityFac(double _QualityFac) {
		this._QualityFac = _QualityFac;
	}

	/**
	 * 
	 * @return an integer representative of the stage of the pattern recognition and subsequent KF fit 
	 * for that hit. -1: the hit has not yet been fit and is the input of hit-based tracking; 0: the hit
	 * has been successfully involved in hit-based tracking and has a well-defined time-to-distance value;
	 * 1: the hit has been successfully involved in track fitting.
	 */
	public int get_TrkgStatus() {
		return _TrkgStatus;
	}
	/**
	 * 
	 * @param trkgStatus is an integer representative of the stage of the pattern recognition and subsequent KF fit 
	 * for that hit. -1: the hit has not yet been fit and is the input of hit-based tracking; 0: the hit
	 * has been successfully involved in hit-based tracking and has a well-defined time-to-distance value;
	 * 1: the hit has been successfully involved in track fitting.
	 */
	public void set_TrkgStatus(int trkgStatus) {
		_TrkgStatus = trkgStatus;
	}

	/**
	 * 
	 * @return the calculated distance (in cm) from the time (in ns)
	 */
	public double get_TimeToDistance() {
		return _TimeToDistance;
	}


	/**
	 * sets the calculated distance (in cm) from the time (in ns)
	 */
	
	public void set_TimeToDistance(double cosTrkAngle) {
		
		double d =0;
		int regionIdx = this.get_Region()-1;
		if(_TrkgStatus!=-1) 
			d = Constants.TIMETODIST[regionIdx]/cos(Math.toRadians(6.));
		//	d = Constants.TIMETODIST[regionIdx]/cos(Math.toRadians(6.))/cosTrkAngle;
		//	d = Constants.TIMETODIST[regionIdx]/cosTrkAngle;
		this._TimeToDistance = d*this.get_Time();
	}


	public double get_ClusFitDoca() {
		return _ClusFitDoca;
	}

	public void set_ClusFitDoca(double _ClusFitDoca) {
		this._ClusFitDoca = _ClusFitDoca;
	}

	public double get_TrkFitDoca() {
		return _TrkFitDoca;
	}

	public void set_TrkFitDoca(double _TrkFitDoca) {
		this._TrkFitDoca = _TrkFitDoca;
	}

	public void fix_TimeToDistance(double cellSize) {
		this._TimeToDistance = cellSize;
	}
	/**
	 * 
	 * @return the hit x-position at the mid-plane (y=0) in the tilted sector coordinate system
	 */
	public double get_X() {
		return _X;
	}

	/**
	 * 
	 * @param _X is the hit x-position at the mid-plane (y=0) in the tilted sector coordinate system
	 */
	public void set_X(double _X) {
		this._X = _X;
	}

	/**
	 * 
	 * @return the hit z-position at the mid-plane (y=0) in the tilted sector coordinate system
	 */
	public double get_Z() {
		return _Z;
	}

	/**
	 * 
	 * @param _Z is the hit z-position at the mid-plane (y=0) in the tilted sector coordinate system
	 */
	public void set_Z(double _Z) {
		this._Z = _Z;
	}

	/**
	 * A method to update the hit position information after the fit to the local coord.sys. wire positions 
	 */
	public void updateHitPosition() {		
		
		double x = GeometryLoader.dcDetector.getSector(0).getSuperlayer(this.get_Superlayer()-1).getLayer(this.get_Layer()-1).getComponent(this.get_Wire()-1).getMidpoint().x();
		double z = GeometryLoader.dcDetector.getSector(0).getSuperlayer(this.get_Superlayer()-1).getLayer(this.get_Layer()-1).getComponent(this.get_Wire()-1).getMidpoint().z();
		
		this.set_X(x);
		this.set_Z(z);
	
	}

	/**
	 * A method to update the hit position information after the fit to the wire positions employing 
	 * hit-based tracking algorithms has been performed.
	 */
	public void updateHitPositionWithTime(double cosTrkAngle) {
		if(this.get_Time()>0)
			this.set_TimeToDistance(cosTrkAngle);
		
		if(Constants.TIMETODIST[this.get_Region()-1]*this.get_Time()>this.get_CellSize()*1.5 ) {
			//this.fix_TimeToDistance(this.get_CellSize()/cos(Math.toRadians(6.)));
			this.set_OutOfTimeFlag(true);
    	}
		double x = GeometryLoader.dcDetector.getSector(0).getSuperlayer(this.get_Superlayer()-1).getLayer(this.get_Layer()-1).getComponent(this.get_Wire()-1).getMidpoint().x();
		double z = GeometryLoader.dcDetector.getSector(0).getSuperlayer(this.get_Superlayer()-1).getLayer(this.get_Layer()-1).getComponent(this.get_Wire()-1).getMidpoint().z();
		
		this.set_X(x+this._LeftRightAmb*this._TimeToDistance);
		this.set_Z(z);
	}

	
	/**
	 * 
	 * @param otherHit
	 * @return a boolean comparing 2 hits based on basic descriptors; 
	 * returns true if the hits are the same
	 */
	public boolean isSameAs(FittedHit otherHit) {
		FittedHit thisHit = this;
		boolean cmp = false;
		if(thisHit.get_Time() == otherHit.get_Time() 
				&& thisHit.get_Sector() == otherHit.get_Sector()
					&& thisHit.get_Superlayer() == otherHit.get_Superlayer()
							&& thisHit.get_Layer() == otherHit.get_Layer()
									&& thisHit.get_Wire() == otherHit.get_Wire() )
			cmp = true;
		return cmp;
	}
	

	/**
	 * 
	 * @param arg0 the other hit
	 * @return an int used to sort a collection of hits by layer number
	 */
	public int compareTo(FittedHit arg0) {
		if(this.get_Layer()>arg0.get_Layer()) {
			return 1;
		} else {
			return -1;
		}
	}
	
	
	private int _AssociatedClusterID = -1;
    
	public int get_AssociatedClusterID() {
		return _AssociatedClusterID;
	}

	public void set_AssociatedClusterID(int _AssociatedClusterID) {
		this._AssociatedClusterID = _AssociatedClusterID;
	}

	
	/**
	 * Sets the time using TOF
	 */
	public void set_Time(double t) {
		super.set_Time(t);
	}
	/**
	 * identifying outoftimehits;
	 */
	private boolean _OutOfTimeFlag;
	
	public void set_OutOfTimeFlag(boolean b) {
		_OutOfTimeFlag =b;
	}
	public boolean get_OutOfTimeFlag() {
		return _OutOfTimeFlag;
	}
	
	public String printInfo() {
		//double xr = this._X*Math.cos(Math.toRadians(25.))+this._Z*Math.sin(Math.toRadians(25.));		
		//double zr = this._Z*Math.cos(Math.toRadians(25.))-this._X*Math.sin(Math.toRadians(25.));
		String s = "DC Fitted Hit: ID "+this.get_Id()+" Sector "+this.get_Sector()+" Superlayer "+this.get_Superlayer()+" Layer "+this.get_Layer()+" Wire "+this.get_Wire()+" Time "+this.get_Time()
				+"  LR "+this.get_LeftRightAmb()+" doca "+this.get_TimeToDistance()*Math.cos(Math.toRadians(6.))+" updated pos  "+this._X+" clus "+
				this._AssociatedClusterID;
		return s;
	}

	
	
}
