package org.jlab.rec.dc.cluster;

import java.util.ArrayList;
import java.util.Collections;

import org.jlab.geom.prim.Line3D;
import org.jlab.geom.prim.Point3D;
import org.jlab.rec.dc.Constants;
import org.jlab.rec.dc.hit.FittedHit;

import trackfitter.fitter.LineFitPars;
import trackfitter.fitter.LineFitter;

/**
 * A fitted cluster in the DC consists of an array of hits that are grouped together according to the algorithm of the ClusterFinder class and have been fit using the wire position information and subsequent time-based information (at the midplane)
 * @author ziegler
 *
 */
public class FittedCluster extends ArrayList<FittedHit> implements Comparable<FittedCluster>{

	private static final long serialVersionUID = 7240609802152999866L;

	/**
	 * 
	 * @param rawCluster a  Cluster fit using hit-based tracking information
	 */
	public FittedCluster(Cluster rawCluster) {
		this._Sector = rawCluster.get_Sector();
		this._Superlayer = rawCluster.get_Superlayer();
		this._Id = rawCluster.get_Id();
		
		// adding the hits to the defined cluster
		for(int i = 0; i<rawCluster.size(); i++) {
			FittedHit fhit = new FittedHit(rawCluster.get(i).get_Sector(), rawCluster.get(i).get_Superlayer(),
					rawCluster.get(i).get_Layer(),rawCluster.get(i).get_Wire(), rawCluster.get(i).get_Time(),rawCluster.get(i).get_TimeErr(),rawCluster.get(i).get_Id());
			fhit.set_Id(rawCluster.get(i).get_Id());
			this.add(fhit);
		}
	}

	private int _Sector;      							//	    sector[1...6]
	private int _Superlayer;    	 					//	    superlayer [1,...6]
	private int _Id;									//		cluster Id

	
	private Line3D _clusLine;
	private double _fitProb = -1;
	private double _Chisq = Double.POSITIVE_INFINITY; ;
	
	private double _clusterLineFitSlope;
	private double _clusterLineFitintercept;
	private double _clusterLineFitSlopeErr;
	private double _clusterLineFitinterceptErr;

	/**
	 * 
	 * @return the sector (1...6)
	 */
	public int get_Sector() {
		return _Sector;
	}
	
	/**
	 * Sets the sector
	 * @param _Sector the sector (1...6)
	 */
	public void set_Sector(int _Sector) {
		this._Sector = _Sector;
	}
	
	/**
	 * 
	 * @return the superlayer (1...6)
	 */
	public int get_Superlayer() {
		return _Superlayer;
	}
	
	/**
	 * Sets the superlayer
	 * @param _Superlayer the superlayer (1...6)
	 */
	public void set_Superlayer(int _Superlayer) {
		this._Superlayer = _Superlayer;
	}
	
	/**
	 * 
	 * @return the cluster ID (index in the sequence of formed clusters)
	 */
	public int get_Id() {
		return _Id;
	}
	
	/**
	 * Sets the cluster ID, which is theindex (in the sequence of formed clusters)
	 * @param _Id the cluster ID
	 */
	public void set_Id(int _Id) {
		this._Id = _Id;
	}

	/**
	 * 
	 * @return the line corresponding to the linear fit to the cluster hits
	 */
	public Line3D get_clusLine() {
		return _clusLine;
	}
	
	/**
	 * Sets the cluster line defined as a point on the line and a unit direction vector along the line
	 * @param _clusLine the cluster line
	 */
	public void set_clusLine(Line3D _clusLine) {
		this._clusLine = _clusLine; 
	}

	private Line3D _clusLineErr;
	/**
	 * 
	 * @return the line corresponding to the linear fit to the cluster hits
	 */
	public Line3D get_clusLineErr() {
		return _clusLineErr;
	}
	
	/**
	 * Sets the error on the cluster line - where the line is defined as a point on the line and a unit direction vector along the line
	 * @param _clusLineErr the cluster line error
	 */
	public void set_clusLineErr(Line3D _clusLineErr) {
		this._clusLineErr = _clusLineErr; 
	}

	/**
	 * 
	 * @return the linear fit chi^2 probability
	 */
	public double get_fitProb() {
		return _fitProb;
	}

	/**
	 * Sets the fit chi^2 prob
	 * @param _fitChisq the chi^2 prob
	 */
	public void set_fitProb(double _fitChisq) {
		this._fitProb = _fitChisq;
	}

	

	public double get_Chisq() {
		return _Chisq;
	}

	public void set_Chisq(double _Chisq) {
		this._Chisq = _Chisq;
	}

	/**
	 * Cluster comparator based on number of hits in the cluster
	 */
	@Override
	public int compareTo(FittedCluster o) {
		if(this.size()>o.size()) {
		return 1;
		} else {
			return 0;
		}
	}


	/**
	 * 
	 * @return region (1...3)
	 */
    public int get_Region() {
     	return (int) (this._Superlayer+1)/2;
     }
    /**
     * 
     * @return superlayer 1 or 2 in region (1...3)
     */
    public int get_RegionSlayer() {
    	return (this._Superlayer+1)%2+1;
	}
    
  /**
   * 
   * @return average wire position in a cluster
   */
	public double getAvgwire() {
		double avewire = 0;
		int hSize = this.size();
			for (int h =0; h< hSize; h++) {
				avewire += this.get(h).get_Wire();
			}
			return ((double)avewire/hSize);
	}
    
	/**
	 * 
	 * @return the slope of the line fitted to the cluster
	 */
	public double get_clusterLineFitSlope() {
	return _clusterLineFitSlope;
	}
	
	/**
	 * Sets the slope of the line fitted to the cluster
	 * @param _clusterLineFitSlope the slope of the line fitted to the cluster
	 */
	public void set_clusterLineFitSlope(double _clusterLineFitSlope) {
		this._clusterLineFitSlope = _clusterLineFitSlope;
	}
	
	/**
	 * 
	 * @return the intercept of the line fitted to the cluster
	 */
	public double get_clusterLineFitIntercept() {
		return _clusterLineFitintercept;
	}
	
	/**
	 * Sets the intercept of the line fitted to the cluster
	 * @param _clusterLineFitIntercept the intercept of the line fitted to the cluster
	 */
	public void set_clusterLineFitIntercept(double _clusterLineFitIntercept) {
		this._clusterLineFitintercept = _clusterLineFitIntercept;
	}
	
	
	public double get_clusterLineFitSlopeErr() {
		return _clusterLineFitSlopeErr;
	}

	public void set_clusterLineFitSlopeErr(double _clusterLineFitSlopeErr) {
		this._clusterLineFitSlopeErr = _clusterLineFitSlopeErr;
	}

	
	
	public double get_clusterLineFitInterceptErr() {
		return _clusterLineFitinterceptErr;
	}
	
	
	public void set_clusterLineFitInterceptErr(double _clusterLineFitinterceptErr) {
		this._clusterLineFitinterceptErr = _clusterLineFitinterceptErr;
	}
	
	/**
	 * Fits a cluster to a line
	 *
	 */
	public void clusterFitter(String trking, boolean LRassigned) {
		FittedCluster clus = this;
		Collections.sort(clus);
		
		int clusterSize = clus.size();
		double[] x = new double[clusterSize];
		double[] y = new double[clusterSize];
		double[] ex = new double[clusterSize];
		double[] ey = new double[clusterSize];
		
		for(int i = 0; i < clusterSize; i++) {
		
			FittedHit hit = clus.get(i); 
			if(clus.get_TrkgStatus()==-1) {
				x[i] = hit.get_lX();
				ex[i] = 0;
				y[i] = hit.get_lY();
				ey[i] = 1;
			} 
			if(clus.get_TrkgStatus()==0) {
				
				x[i] = hit.get_Z();
				ex[i] = 0;
				y[i] = hit.get_X();
				ey[i]= hit.get_PosErr(); 
			}			
		}
		LineFitter linefit = new LineFitter();
		boolean linefitstatusOK = linefit.fitStatus(x, y, ex, ey, clusterSize);
		
		if(linefitstatusOK) {
		      //  Get the results of the fits
		        LineFitPars linefitpars = linefit.getFit();
		       
		        double fit_slope = linefitpars.slope();
		        double fit_interc = linefitpars.intercept();

                 // nominal line:
		        double the_slope = fit_slope;
		        double the_interc = fit_interc;
		        
                Point3D pointOnLine = get_PointOnLine(x[0], the_slope, the_interc);
                Point3D dirOfLine = get_DirOnLine(the_slope, the_interc);
		        
		        clus.set_clusLine(new Line3D(pointOnLine, dirOfLine));
		        
		        
		        Point3D pointOnLineErr = get_PointOnLine(x[0], linefitpars.slopeErr(), linefitpars.interceptErr());
		        Point3D dirOfLineErr = get_DirOnLine(linefitpars.slopeErr(), linefitpars.interceptErr());
		        
		        clus.set_clusLineErr(new Line3D(pointOnLineErr, dirOfLineErr));
		        
		        double chi2 =0;
		        for(int i = 0; i < clusterSize; i++) {
		        	
		        	double residual = (y[i]-fit_slope*x[i]-fit_interc);
		        	
		        	if(trking=="TimeBased") {		        	
		        		double timeResidual = Math.abs(fit_slope*x[i]+fit_interc) - Math.abs(y[i]);
		        		//System.out.println("In Cluster Finder at point "+x[i]+" , "+y[i]+" fit = "+(fit_slope*x[i]+fit_interc)+
		        		//		" with residual "+timeResidual);
		        		clus.get(i).set_TimeResidual(timeResidual);
		        	}
		        	
		        	chi2=chi2+residual*residual;
					clus.get(i).set_Residual(residual);
					
					if(this.get_TrkgStatus()==-1 && residual<0) {
						clus.get(i).set_LeftRightAmb(1);
					}
					if(this.get_TrkgStatus()==-1 && residual>0) {
						clus.get(i).set_LeftRightAmb(-1);
					}
					
					if( (trking.equals("HitBased") && Math.abs((y[i]-fit_slope*x[i]-fit_interc))<0.01 ) ||(trking.equals("TimeBased") && LRassigned==false &&
						 Constants.TIMETODIST[clus.get(i).get_Region()-1]*clus.get(i).get_Time()/clus.get(i).get_CellSize() < 0.4)  ) { //  DOCA require to be larger than 40% of cell size for hit-based tracking LR assignment 
						clus.get(i).set_LeftRightAmb(0);
					}
		        }
		        clus.set_clusterLineFitSlope(fit_slope); 
		        clus.set_clusterLineFitSlopeErr(linefitpars.slopeErr());
		        clus.set_clusterLineFitIntercept(fit_interc);
		        clus.set_clusterLineFitInterceptErr(linefitpars.interceptErr());
		        clus.set_fitProb(linefitpars.getProb());
		        clus.set_Chisq(linefitpars.chisq());
		        
		}
		
	}


	/**
	 * 
	 * @return a tracking status: -1 (no fits yet), 0 (hit-based tracking done), 1 (time-based tracking done)
	 */
	public int get_TrkgStatus() {
		FittedCluster clus = this;
		boolean postHitBasedFit = true;
		boolean postTimeBasedFit = true;
		for(FittedHit fhit : clus) {
			if(fhit.get_TrkgStatus()==-1) {
				postHitBasedFit = false;
				postTimeBasedFit = false;
			}
			if(fhit.get_TrkgStatus()==0) {
				postTimeBasedFit = false;
			}
		}
		int value = -1;
		if(postHitBasedFit)
			value = 0;
		if(postTimeBasedFit)
			value = 1;
		return value;
	}
	
	/**
	 * 
	 * @param the_slope the cluster fitted-line slope
	 * @param the_interc the fitted-line intercept
	 * @return the cluster fitted-line unit direction vector
	 */
	private Point3D get_DirOnLine(double the_slope, double the_interc) {
		return new Point3D(the_slope/Math.sqrt(1.+the_slope*the_slope),0,1./Math.sqrt(1.+the_slope*the_slope));
	}
	
	/**
	 * 
	 * @param d the point z coordinate
	 * @param the_slope the cluster fitted-line slope
	 * @param the_interc the fitted-line intercept
	 * @return  point (the_slope*d+the_interc,0,d) on the fitted cluster line
	 */
	private Point3D get_PointOnLine(double d, double the_slope,
			double the_interc) {
		
		return new Point3D(the_slope*d+the_interc,0,d);
	}
	
	
	
	/**
	 * 
	 * @return fitted cluster info.
	 */
	public String printInfo() {
		String s = "Fitted DC cluster: ID "+this.get_Id()+" Sector "+this.get_Sector()+" Superlayer "+this.get_Superlayer()+" Size "+this.size()+ " fit chi2 "+this.get_fitProb();
		return s;
	}
	
	
	/**
	 * 
	 * @return the cluster from which the fitted cluster was created
	 */
	public Cluster getBaseCluster() {
		Cluster baseClus = new Cluster(this.get_Sector(), this.get_Superlayer(), this.get_Id());
		
		return baseClus;
	}

	

	
	

}
