package org.jlab.rec.bst.track;

import java.util.ArrayList;
import java.util.List;

import org.jMath.Vector.threeVec;
import org.jlab.geom.prim.Line3D;
import org.jlab.rec.bst.Constants;
import org.jlab.rec.bst.Geometry;
import org.jlab.rec.bst.cross.Cross;
import org.jlab.rec.bst.hit.FittedHit;
import org.jlab.rec.bst.hit.Hit;

import trackfitter.fitter.LineFitPars;
import trackfitter.fitter.LineFitter;

public class CosmicTrack extends ArrayList<Cross> {
		 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// constructor
		public  CosmicTrack(){
	      
	    }
 
	    private int _id;
	    private double _yxchi2;
	    private double _yzchi2;
	    private double _chi2;
	    private double _ndf;
		private double _yxslope;
	    private double _yzslope;
	    private double _yxinterc;
	    private double _yzinterc;
		private double _yxslopeErr;
	    private double _yzslopeErr;
	    private double _yxintercErr;
	    private double _yzintercErr;
	    private double[][][] _trackInterModulePlanes;
	    
	    private ArrayList<double[]> _xzresiduals;
	   
		public int getIdx() {
			return _id;
		}

		
		public void setIdx(int id) {
			this._id = id;
		}
		
	    public double get_yxchi2() {
			return _yxchi2;
		}


		public void set_yxchi2(double _yxchi2) {
			this._yxchi2 = _yxchi2;
		}


		public double get_yzchi2() {
			return _yzchi2;
		}


		public void set_yzchi2(double _yzchi2) {
			this._yzchi2 = _yzchi2;
		}


		public double get_chi2() {
			return _chi2;
		}


		public void set_chi2(double _chi2) {
			this._chi2 = _chi2;
		}


		public double get_ndf() {
			return _ndf;
		}


		public void set_ndf(double _ndf) {
			this._ndf = _ndf;
		}


		public double get_yxslope() {
			return _yxslope;
		}


		public void set_yxslope(double _yxslope) {
			this._yxslope = _yxslope;
		}


		public double get_yzslope() {
			return _yzslope;
		}


		public void set_yzslope(double _yzslope) {
			this._yzslope = _yzslope;
		}


		public double get_yxinterc() {
			return _yxinterc;
		}


		public void set_yxinterc(double _yxinterc) {
			this._yxinterc = _yxinterc;
		}


		public double get_yzinterc() {
			return _yzinterc;
		}


		public void set_yzinterc(double _yzinterc) {
			this._yzinterc = _yzinterc;
		}


		public double get_yxslopeErr() {
			return _yxslopeErr;
		}


		public void set_yxslopeErr(double _yxslopeErr) {
			this._yxslopeErr = _yxslopeErr;
		}


		public double get_yzslopeErr() {
			return _yzslopeErr;
		}


		public void set_yzslopeErr(double _yzslopeErr) {
			this._yzslopeErr = _yzslopeErr;
		}


		public double get_yxintercErr() {
			return _yxintercErr;
		}


		public void set_yxintercErr(double _yxintercErr) {
			this._yxintercErr = _yxintercErr;
		}


		public double get_yzintercErr() {
			return _yzintercErr;
		}


		public void set_yzintercErr(double _yzintercErr) {
			this._yzintercErr = _yzintercErr;
		}


		public ArrayList<double[]> get_xzresiduals() {
			return _xzresiduals;
		}

	    
		private void fitXYProjection(Geometry geo) {
			 ArrayList<Cross> _hitsOnTrack = this;
			 
			 LineFitter linefitYX = new LineFitter();
			 if(_hitsOnTrack!=null) {
				
				double[] X = new double[_hitsOnTrack.size()]; 
				double[] Y = new double[_hitsOnTrack.size()]; 
				
				double[] errX = new double[_hitsOnTrack.size()];
				double[] errY = new double[_hitsOnTrack.size()];
				
				double[][] resid = new double[_hitsOnTrack.size()][2]; 
				
				
				for(int j =0; j< _hitsOnTrack.size(); j++) {
					
					X[j] = _hitsOnTrack.get(j).get_Point().x();
					errX[j] = _hitsOnTrack.get(j).get_PointErr().x();
					Y[j] = _hitsOnTrack.get(j).get_Point().y();
					errY[j] = _hitsOnTrack.get(j).get_PointErr().y();
					
					resid[j][0] = 0; //x residual at y
					
				}
			
			// do the fit
			
		  		boolean linefitresultYX = linefitYX.fitStatus(Y, X, errY, errX, _hitsOnTrack.size());
		  		
		        //  Get the results of the fits
		  		if(linefitresultYX) {
		  			LineFitPars linefitparsYX = linefitYX.getFit();
		  			if(linefitparsYX!=null) {
			  			_yxchi2=linefitparsYX.chisq();
			  			_yxslope=linefitparsYX.slope();
			  			_yxinterc = linefitparsYX.intercept();
			  			
			  			
		  			}
		  		}
		  		
		  		this.update_Crosses(_yxslope, 0, geo);
			 }
			 
		}
		
		public void fitCosmicTrack(Geometry geo) {
			 fitXYProjection( geo);
			 
			 ArrayList<Cross> _hitsOnTrack = new ArrayList<Cross>();
		 
			 for(int j =0; j< this.size(); j++) {
				 if(this.get(j).get_Cluster1().get_Centroid()>2 &&  this.get(j).get_Cluster2().get_Centroid()>2) 
					 _hitsOnTrack.add(this.get(j));			 
			 }
			 LineFitter linefitYX = new LineFitter();
			 LineFitter linefitYZ = new LineFitter();
		
			 
			 if(_hitsOnTrack!=null) {
				
				double[] X = new double[_hitsOnTrack.size()]; 
				double[] Y = new double[_hitsOnTrack.size()]; 
				double[] Z = new double[_hitsOnTrack.size()];
				double[] errX = new double[_hitsOnTrack.size()];
				double[] errY = new double[_hitsOnTrack.size()];
				double[] errZ = new double[_hitsOnTrack.size()];
				
				double[][] resid = new double[_hitsOnTrack.size()][2]; 
				ArrayList<double[]> xzresid = new ArrayList<double[]>();
				
				
				for(int j =0; j< _hitsOnTrack.size(); j++) {
					
					X[j] = _hitsOnTrack.get(j).get_Point().x();
					errX[j] = _hitsOnTrack.get(j).get_PointErr().x();
					Y[j] = _hitsOnTrack.get(j).get_Point().y();
					errY[j] = _hitsOnTrack.get(j).get_PointErr().y();
					Z[j] = _hitsOnTrack.get(j).get_Point().z();
					errZ[j] = _hitsOnTrack.get(j).get_PointErr().z();
					resid[j][0] = 0; //x residual at y
					resid[j][1] = 0; //z residual at y
			}
			
		// do the fits
		
	  		boolean linefitresultYX = linefitYX.fitStatus(Y, X, errY, errX, _hitsOnTrack.size());
	  		
	        //  Get the results of the fits
	  		if(linefitresultYX) {
	  			LineFitPars linefitparsYX = linefitYX.getFit();
	  			if(linefitparsYX!=null) {
		  			_yxchi2=linefitparsYX.chisq();
		  			_yxslope=linefitparsYX.slope();
		  			_yxinterc = linefitparsYX.intercept();
		  			_yxslopeErr = linefitparsYX.slopeErr();
		  			_yxintercErr = linefitparsYX.interceptErr();		
	  			}
	  		}
	  		
	  		boolean linefitresultYZ = linefitYZ.fitStatus(Y, Z, errY, errZ, _hitsOnTrack.size());
	        //  Get the results of the fits
	  		if(linefitresultYZ) {
	  			LineFitPars linefitparsYZ = linefitYZ.getFit();
	  			if(linefitparsYZ!=null) {
	  				_yzchi2=linefitparsYZ.chisq();
	  				_yzslope=linefitparsYZ.slope();
	  				_yzinterc= linefitparsYZ.intercept(); 
	  				_yzslopeErr = linefitparsYZ.slopeErr();
		  			_yzintercErr = linefitparsYZ.interceptErr();	
	  			}
	  		}
	  		this.update_Crosses(_yxslope, _yzslope, geo);
	  		
	  		 _xzresiduals = xzresid; 
		}
			
	 }

		// clean this up....
		public void refitCosmicTrack(Geometry geo) {
			CosmicTrack trk = this;
			KalFitCosmics kf = new KalFitCosmics(trk, geo);
			kf.runKalFit(trk, geo);
			this.update_Crosses(_yxslope, _yzslope, geo);
			
			ArrayList<Cross> _hitsOnTrack = this;
	  		//set trajectory
	  		double[][][] trajResults = this.calc_trackInterModule(geo);
			this.set_trackInterModulePlanes(trajResults)	; 
			
	  		for(int j =0; j< _hitsOnTrack.size(); j++) {
	  			
	  			int l1 = _hitsOnTrack.get(j).get_Cluster1().get_Layer();
	  			int s = _hitsOnTrack.get(j).get_Cluster1().get_Sector();
	  			double s1 = _hitsOnTrack.get(j).get_Cluster1().get_Centroid();
	  			int l2 = _hitsOnTrack.get(j).get_Cluster2().get_Layer();
	  			double s2 = _hitsOnTrack.get(j).get_Cluster2().get_Centroid();
	  			
	  			double trajX1 = trajResults[l1-1][s-1][0] ;
				double trajY1 = trajResults[l1-1][s-1][1] ;
				double trajZ1 = trajResults[l1-1][s-1][2] ;
				double trajX2 = trajResults[l2-1][s-1][0] ;
				double trajY2 = trajResults[l2-1][s-1][1] ;
				double trajZ2 = trajResults[l2-1][s-1][2] ;
				
				if(trajX1 == -999 || trajX2 == -999)
					continue;
				
				threeVec LocPoint1 = geo.transformToFrame(s, l1, trajX1, trajY1, trajZ1, "local", "");	
				threeVec LocPoint2 = geo.transformToFrame(s, l2, trajX2, trajY2, trajZ2, "local", "");	
				double m = (LocPoint1.x() - LocPoint2.x())/(LocPoint1.z() - LocPoint2.z());
				double b = LocPoint1.x() - m*LocPoint1.z();
				
				double ialpha1 = (s1-1)*Constants.STEREOANGLE/(double) (Constants.NSTRIP-1); 
				//the active area starts at the first strip 	
				double interc1 = (s1)*Constants.PITCH;
				double ialpha2 = (s2-1)*Constants.STEREOANGLE/(double) (Constants.NSTRIP-1); 
				//the active area starts at the first strip 	
				double interc2 = (s2)*Constants.PITCH;

				// Equation for strip line is x = mz + b [i.e. z is the direction of the length of the module]
				// -------------------------------------
				double m1 = -Math.tan(ialpha1);
				double m2 =  Math.tan(ialpha2);
				double b1 = Constants.ACTIVESENWIDTH - interc1 ;
				double b2 = interc2;
				
				double z1 = (b-b1)/(m1-m);
				double x1 = m1*z1 +b1;
				double z2 = (b-b2)/(m2-m);
				double x2 = m2*z2 +b2;
				
				threeVec Point1 = geo.transformToFrame(s, l1, x1, 0, z1, "lab", "");
				threeVec Point2 = geo.transformToFrame(s, l2, x2, 0, z2, "lab", "");
				// unit vec along dir of track
				threeVec t = Point2.diff(Point1);
				t.multi(1./t.len());
				//normal to plane of module
				threeVec n = geo.findBSTPlaneNormal(s, l1);
				//path length tranversed inbetween modules
				double l = (Constants.MODULERADIUS[l2-1][0]-Constants.MODULERADIUS[l1-1][0])/(n.dot(t));
				//Point inbetween the modules			
				threeVec Point = Point1.add(t.mult(l/2));
				
				//set the cross to that point
				//System.out.println(" trajX1 "+trajX1+" trajY1 "+trajY1+" trajZ1 "+trajZ1+" trajX2 "+trajX2+" trajY2 "+trajY2+" trajZ2 "+trajZ2);
				_hitsOnTrack.get(j).set_Point(Point);
				
				double tx = _yxslope/Math.sqrt(_yxslope*_yxslope+_yzslope*_yzslope+1);
				double ty = 1/Math.sqrt(_yxslope*_yxslope+_yzslope*_yzslope+1);
				double tz = _yzslope/Math.sqrt(_yxslope*_yxslope+_yzslope*_yzslope+1);
				
				_hitsOnTrack.get(j).set_Dir(new threeVec(tx,ty,tz));
				
				for(FittedHit hit : _hitsOnTrack.get(j).get_Cluster1()) {
					double doca1 = geo.getDOCAToStrip(s, l1, hit.get_Strip() , new threeVec(trajX1,trajY1, trajZ1));
					double sigma1 = geo.getSingleStripResolution(l1, hit.get_Strip(),trajZ1);
					hit.set_stripResolutionAtDoca(sigma1);
					hit.set_docaToTrk(doca1);
					hit.set_TrkgStatus(2);
					
	  			}
	  			for(FittedHit hit : _hitsOnTrack.get(j).get_Cluster2()) {
					double doca2 = geo.getDOCAToStrip(s, l2, hit.get_Strip(), new threeVec(trajX2,trajY2, trajZ2));
					double sigma2 = geo.getSingleStripResolution(l2, hit.get_Strip(),trajZ2);
					hit.set_stripResolutionAtDoca(sigma2);
					hit.set_docaToTrk(doca2);
					hit.set_TrkgStatus(2);
	  			}
				 
	  		}
	 
				
		}
		
		
	    private double[][][] calc_trackInterModule(Geometry geo) {
	    	//[l][s], [0,1,2,3,4]=x,y,z,phi,theta,estimated centroid strip
	    	double[][][] result = new double[Constants.NLAYR][Constants.NSECT[Constants.NLAYR-1]][7];
	    	for(int l =0; l< Constants.NLAYR; l++) {
		    	for (int s = 0; s<Constants.NSECT[l]; s++) {
		    		result[l][s][0] = -999;
	    		    result[l][s][1] = -999;
	    		    result[l][s][2] = -999;
	    		    result[l][s][3] = -999;
	    		    result[l][s][4] = -999;
		    	}
	    	}
	    	//Layer 1-8:
	    	for(int l =0; l< Constants.NLAYR; l++) {
	    	
		    	for (int s = 0; s<Constants.NSECT[l]; s++) {
		    		
		    		//double delta_phi = (double)s*2.*Math.PI/Constants.NSECT[l];
		    				    		
		    		double[] trkIntersInf=this.getIntersectionTrackWithModule(s,l,this._yxinterc , this._yxslope, this._yzinterc , this._yzslope,geo);
		    		
		    		threeVec p =  new threeVec(trkIntersInf[0],trkIntersInf[1],trkIntersInf[2]);
		    		
		    		if(p.len()==0)
		    			continue;
		    		   		
		    		if( (p.rt()<=Math.sqrt(0.25*Constants.ACTIVESENLEN*Constants.ACTIVESENWIDTH+Constants.MODULERADIUS[l][0]*Constants.MODULERADIUS[l][0])) ){
		    			      
		    		    //double phi = Math.acos((-this._yxslope*Math.cos(delta_phi)-Math.sin(delta_phi))/Math.sqrt(1+this._yxslope*this._yxslope));
		    		    
		    		    //threeVec n =  new threeVec(-Math.sin(delta_phi), Math.cos(delta_phi), 0);
		    		    //threeVec ui =  new threeVec(Math.cos(delta_phi), Math.sin(delta_phi), 0); //longitudinal vector along the local x direction of the module
		    			
		    			threeVec n =  geo.findBSTPlaneNormal(s+1, l+1);
		    		    threeVec ui =  new threeVec(n.y(), -n.x(), 0); //longitudinal vector along the local x direction of the module
		    			
		    		    threeVec uj =  ui.cross(n); //longitudinal vector along the local z direction of the module
		    		    
		    		    double norm = Math.sqrt(this._yxslope*this._yxslope+this._yzslope*this._yzslope+1);
					    
					    threeVec u = new threeVec(this._yxslope/norm, 1/norm, this._yzslope/norm);
					    
					    if(p.y()<0)
					    	u = new threeVec(-this._yxslope/norm, -1/norm, -this._yzslope/norm);
					    
		    		   double trkToMPlnAngl = Math.acos(u.dot(ui));
		    		    
					    double zl = u.dot(n);
		    		    double xl = u.dot(ui);
		    		    double yl = u.dot(uj);
		    		    
		    		    double phi = Math.atan2(yl, xl);
		    		    double theta = Math.acos(zl);
		    		    
		    		    
		    		    result[l][s][0] = p.x();
		    		    result[l][s][1] = p.y();
		    		    result[l][s][2] = p.z();
		    		    result[l][s][3] = Math.toDegrees(phi);
		    		    result[l][s][4] = Math.toDegrees(theta);   
		    		    result[l][s][5] = Math.toDegrees(trkToMPlnAngl);  
		    		    result[l][s][6] = trkIntersInf[3];
		    		}	
		    	}
	    	}
			return result;	
		}


		private double[] getIntersectionTrackWithModule(int s, int l,
				double _yxinterc2, double _yxslope2, double _yzinterc2,
				double _yzslope2, Geometry geo) {
			// array [][][][] =[x][y][z][stripCentroid]
			double[] inters = new double[4];
			inters[0]=Double.NaN;
    		inters[1]=Double.NaN;
    		inters[2]=Double.NaN;
    		inters[3]=Double.NaN;		
    		
			double epsilon=1e-6;
			//double angle = 2.*Math.PI*((double) s/(double)Constants.NSECT[l]);
			
		   //threeVec n =  new threeVec(-Math.sin(angle), Math.cos(angle), 0);

		    //double dot = -Math.sin(angle)*_yxslope2+Math.cos(angle);
		    
		    
		    threeVec n = geo.findBSTPlaneNormal(s+1, l+1);
  			
  			double dot = (n.x()*_yxslope2+n.y());
  			
		    
		    if(Math.abs(dot)>epsilon) {
		    	//threeVec w = new threeVec(_yxinterc2+Constants.MODULERADIUS[l][0]*Math.sin(angle), -Constants.MODULERADIUS[l][0]*Math.cos(angle), _yzinterc2);
		    	threeVec w = new threeVec(_yxinterc2-Constants.MODULERADIUS[l][0]*n.x(), -Constants.MODULERADIUS[l][0]*n.y(), _yzinterc2);
		    	double y = -(n.x()*w.x()+n.y()*w.y()+n.z()*w.z())/dot;
		    	//threeVec Delt = new threeVec(y*_yxslope2+_yxinterc2+Constants.MODULERADIUS[l][0]*Math.sin(angle),y-Constants.MODULERADIUS[l][0]*Math.cos(angle),0);
		    	threeVec Delt = new threeVec(y*_yxslope2+_yxinterc2-Constants.MODULERADIUS[l][0]*n.x(),y-Constants.MODULERADIUS[l][0]*n.y(),0);
		    	
		    	if(Delt.len()<Constants.ACTIVESENWIDTH/2+Constants.TOLTOMODULEEDGE) {
		    		inters[0]=y*_yxslope2+_yxinterc2;
		    		inters[1]=y;
		    		inters[2]=y*_yzslope2+_yzinterc2;
		    		inters[3]=geo.calcNearestStrip(inters[0], inters[1], inters[2], l+1, s+1);		
		    		}
		    	return inters;
		    }
		    
		    return inters;
		}


		
	    
		public Line3D getLine() {
			
			if(this._yxslope ==0 || this._yzslope==0 )
				return new Line3D();
			
			double y_lineEnd = 1000;
			double y_lineOrig = -1000;
			
			double[] x = new double[2];
			double[] y = new double[2];
			double[] z = new double[2];
			
			y[0] = y_lineOrig;
			y[1] = y_lineEnd;
			
			for(int i = 0; i<2; i++) {
				x[i] = (y[i] - this._yxinterc)/this._yxslope;
				z[i] = (y[i] - this._yzinterc)/this._yzslope;
			}
			
			Line3D result = new Line3D();
			result.setOrigin(x[0], y[0], z[0]);
			result.setEnd(x[1], y[1], z[1]);
			
			return result;
		}

		private Hit[][][] HitArray;
		public void hitsToArray(List<Hit> hits2) {
			if(hits2 == null)
				return;
			HitArray = new Hit[Constants.NLAYR][Constants.NSECT[Constants.NLAYR-1]][Constants.NSTRIP] ;
			
			// initializing non-zero Hit Array entries
			// with valid hits
			for(Hit hit : hits2) {
				
				int w = hit.get_Strip();
				int l = hit.get_Layer();
				int s = hit.get_Sector();
								
				HitArray[l-1][s-1][w-1] = hit;
				
			}	
		}


		

		public void update_Crosses(double s_yxfit, double s_yzslope, Geometry geo) {
			for(Cross c : this) {
				if(c.get_Region()<5)
					update_Cross(c, s_yxfit, s_yzslope, c.get_Sector(), c.get_Region(), geo);
			}
		}
		
		public void update_Cross(Cross cross, double s_yxfit, double s_yzslope, int sector, int region, Geometry geo) {
			
			double x = s_yxfit/Math.sqrt(s_yxfit*s_yxfit+s_yzslope*s_yzslope+1);
			double y = 1/Math.sqrt(s_yxfit*s_yxfit+s_yzslope*s_yzslope+1);
			double z = s_yzslope/Math.sqrt(s_yxfit*s_yxfit+s_yzslope*s_yzslope+1);
					
			threeVec trkDir = new threeVec(x,y,z);
			
			if(trkDir!=null) {			
				cross.set_CrossParams(trkDir, geo);				
			}
		}


		public double[][][] get_trackInterModulePlanes() {
			return _trackInterModulePlanes;
		}


		public void set_trackInterModulePlanes(double[][][] _trackInterModulePlanes) {
			this._trackInterModulePlanes = _trackInterModulePlanes;
		}
}
