package org.jlab.rec.bst.track;

import org.jMath.Vector.threeVec;
import org.jlab.geom.prim.Point3D;
import org.jlab.geom.prim.Vector3D;
import org.jlab.rec.bst.Constants;
import org.jlab.rec.bst.Geometry;
import org.jlab.rec.bst.cross.Cross;
import org.jlab.rec.bst.trajectory.Trajectory;

import trackfitter.track.Helix;


/**
 * A class representing track candidates in the BST.  A track has a trajectory represented by an ensemble of geometrical state vectors along its path, 
 * a charge and a momentum
 * @author ziegler
 *
 */
public class Track extends Trajectory {

	/**
	 * serialVersionUID
	 */	
	
	
	private static final long serialVersionUID = 1763744434903318419L;

	Track() {		
	}
	
	private int _Q;
	private double _Pt;
	private double _Pz;
	private double _P;
	private Helix _Helix;
	private double _Bz =5; // Default
	private String _PID;
	/**
	 * 
	 * @return the charge
	 */
	public int get_Q() {
		return _Q;
	}
	/**
	 * Sets the charge
	 * @param _Q the charge
	 */
	public void set_Q(int _Q) {
		this._Q = _Q;
	}
	public double get_Pt() {
		return _Pt;
	}
	public void set_Pt(double _Pt) {
		this._Pt = _Pt;
	}
	public double get_Pz() {
		return _Pz;
	}
	public void set_Pz(double _Pz) {
		this._Pz = _Pz;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	/**
	 * 
	 * @return the total momentum value
	 */
	public double get_P() {
		return _P;
	}
	/**
	 * Sets the total momentum value
	 * @param _P the total momentum value
	 */
	public void set_P(double _P) {
		this._P = _P;
	}
	
	
	public Helix get_Helix() {
		return _Helix;
	}
	public void set_Helix(Helix _Helix) {
		this._Helix = _Helix;
	}
	
	
	public void set_HelicalTrack(Helix Helix) {
		
		set_Helix(Helix);
		set_Q(((int) Math.signum(Constants.SOLSCALE)*get_Helix().get_charge()));
		 
		this.set_Bz(this.calc_Field(0, 0, 0));
		
		double calcPt = Constants.LIGHTVEL*get_Helix().radius()*this.get_Bz();
		
		double calcPz =0;
		
	
		calcPz = calcPt*get_Helix().get_tandip();
		
		double calcP = Math.sqrt(calcPt*calcPt+calcPz*calcPz);
		
		set_Pt(calcPt);
		set_Pz(calcPz);
		set_P(calcP);
		
	}
	

	public void update_Crosses(Geometry geo) {
		if(this.get_Helix()!=null) {
			Helix helix = this.get_Helix();
			for (int i =0; i<this.size(); i++) {
					threeVec helixTanVecAtLayer = helix.getTrackDirectionAtRadius(this.get(i).get_Point().rt());
					this.get(i).set_CrossParams(helixTanVecAtLayer, geo);
					if(this.get(i).get_Cluster2().get_Centroid()<=1) {
						//recalculate z using track pars:
						double z = helix.getPointAtRadius(this.get(i).get_Point().rt()).z();
						double x = this.get(i).get_Point().x();
						double y = this.get(i).get_Point().y();
						this.get(i).set_Point(new threeVec(x,y,z));
					}
					
				}
			
		}
		
	}
  
	
  
	private double _circleFitChi2Prob;
	private double _lineFitChi2Prob;
	
	private Point3D _TrackPointAtCTOFRadius;
	private Vector3D _TrackDirAtCTOFRadious;
	private  double _pathLength;
	public boolean passCand;
	
	public double get_circleFitChi2Prob() {
		return _circleFitChi2Prob;
	}
	public void set_circleFitChi2Prob(double _circleFitChi2Prob) {
		this._circleFitChi2Prob = _circleFitChi2Prob;
	}

	public double get_lineFitChi2Prob() {
		return _lineFitChi2Prob;
	}
	public void set_lineFitChi2Prob(double _lineFitChi2Prob) {
		this._lineFitChi2Prob = _lineFitChi2Prob;
	}
	public boolean containsCross(Cross cross) {
		Track cand = this;
		boolean isInTrack = false;
		
		for(int i =0; i<cand.size(); i++) {
			if(cand.get(i).get_Id() == cross.get_Id()) {
				isInTrack = true;
			}
				
		}
		
		return isInTrack;
	}
	public Point3D get_TrackPointAtCTOFRadius() {
		return _TrackPointAtCTOFRadius;
	}
	public void set_TrackPointAtCTOFRadius(Point3D _TrackPointAtCTOFRadius) {
		this._TrackPointAtCTOFRadius = _TrackPointAtCTOFRadius;
	}
	public Vector3D get_TrackDirAtCTOFRadius() {
		return _TrackDirAtCTOFRadious;
	}
	public void set_TrackDirAtCTOFRadius(Vector3D _TrackDirAtCTOFRadious) {
		this._TrackDirAtCTOFRadious = _TrackDirAtCTOFRadious;
	}
	public double get_pathLength() {
		return _pathLength;
	}
	public void set_pathLength(double _pathLength) {
		this._pathLength = _pathLength;
	}
	public double get_Bz() {
		return _Bz;
	}
	public void set_Bz(double _Bz) {
		this._Bz = _Bz;
	}
	public String get_PID() {
		return _PID;
	}
	public void set_PID(String _PID) {
		this._PID = _PID;
	}


}
