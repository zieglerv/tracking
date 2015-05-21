package org.jlab.rec.bst.track;

import java.util.ArrayList;
import java.util.List;

import org.jMath.Vector.threeVec;
import org.jlab.geom.prim.Line3D;
import org.jlab.geom.prim.Point3D;
import org.jlab.geom.prim.Shape3D;
import org.jlab.rec.bst.Constants;
import org.jlab.rec.bst.Geometry;
import org.jlab.rec.bst.cluster.Cluster;
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
		private double _yxslope;
	    private double _yzslope;
	    private double _yxinterc;
	    private double _yzinterc;
	    private List<Point3D> _trackInterModulePlanes;
	    
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

		public double get_yzchi2() {
			return _yzchi2;
		}

		
		public double get_yxslope() {
			return _yxslope;
		}


		public double get_yzslope() {
			return _yzslope;
		}


		public double get_yxinterc() {
			return _yxinterc;
		}


		public double get_yzinterc() {
			return _yzinterc;
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
			 
			 ArrayList<Cross> _hitsOnTrack = this;
		 
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
	  			}
	  		}
	  		this.update_Crosses(_yxslope, _yzslope, geo);
	  		// set residuals
	  		for(int j =0; j< _hitsOnTrack.size(); j++) {
	  			
	  			resid[j][0] = (X[j]-_yxslope*Y[j]-_yxinterc); //x residual at y	  		
	  			resid[j][1] = (Z[j]-_yzslope*Y[j]-_yzinterc); //z residual at y
				
				double[] pointResid = new double[2];
				pointResid[0] = resid[j][0];
				pointResid[1] = resid[j][1];
				
				xzresid.add(pointResid);
				
			}
				
	  		 _xzresiduals = xzresid; 
		}
			 
			 this.set_trackInterModulePlanes(calc_trackInterModulePlanes())	; 
	 }

	    public List<Point3D> calc_trackInterModulePlanes() {
	    	ArrayList<Point3D> pointsAtPlanes = new ArrayList<Point3D>(16); // 16 layer corresponding to 8 modules
	    	for(int j = -7; j<=0; j++) {
	    		double y = -Constants.MODULERADIUS[-j][0];
	    		double x = this._yxinterc + this._yxslope*y;
				double z = this._yzinterc + this._yzslope*y;
				
				pointsAtPlanes.add(new Point3D(x,y,z));
	    	}
	    	for(int j = 0; j<=7; j++) {
	    		double y = Constants.MODULERADIUS[j][0];
	    		double x = this._yxinterc + this._yxslope*y;
				double z = this._yzinterc + this._yzslope*y;
				
				pointsAtPlanes.add(new Point3D(x,y,z));
	    	}
	    	
			return pointsAtPlanes;
	    }
	    
		public Line3D getLine() {
			
			if(this._yxslope ==0 || this._yzslope==0 )
				return new Line3D();
			
			double y_lineEnd = Constants.MODULERADIUS[7][0];
			double y_lineOrig = -Constants.MODULERADIUS[7][0];
			
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

		
		public void calcHitsSpatialResolution(Geometry geo) {
			ArrayList<Point3D> pointsAtLayers = (ArrayList<Point3D>) this.get_trackInterModulePlanes() ;
			
            for(int crossIdx =0; crossIdx<this.size(); crossIdx++) {
            	List<Cluster> crossClusList = new ArrayList<Cluster>();
            	
            	crossClusList.add(this.get(crossIdx).get_Cluster1());
            	crossClusList.add(this.get(crossIdx).get_Cluster2());
            	
            	for(int clusIdx =0; clusIdx<crossClusList.size(); clusIdx++) {
            		
            		for(int hitIdx =0; hitIdx<crossClusList.get(clusIdx).size(); hitIdx++) {
            			FittedHit stripHit = crossClusList.get(clusIdx).get(hitIdx);
            			stripHit.get_Strip();
            			
            			int LayrIdx = (2*this.get(crossIdx).getCosmicsRegion()+stripHit.get_RegionSlayer()-3);
            			
            			double docaToTrk = geo.getDOCAToStrip( stripHit.get_Sector(), stripHit.get_Layer(), (double)stripHit.get_Strip(), 
            					new threeVec(pointsAtLayers.get(LayrIdx).x(),pointsAtLayers.get(LayrIdx).y(),pointsAtLayers.get(LayrIdx).z()));
            			stripHit.set_docaToTrk(docaToTrk);
            			}
            	}
            }
			
		}
		
		public double calcHitSpatialResolution(Hit stripHit) {
			if(stripHit == null) 
				return Double.NaN;
			
			Line3D fitLine = this.getLine();
            double docaToTrk = 0;
            
		/*	double x1 = Geometry.getEndPoints(stripHit.get_Layer(), stripHit.get_Sector(), stripHit.get_Strip()).getFirstEndPoint().x();
			double y1 = Geometry.getEndPoints(stripHit.get_Layer(), stripHit.get_Sector(), stripHit.get_Strip()).getFirstEndPoint().y();
			double z1 = Geometry.getEndPoints(stripHit.get_Layer(), stripHit.get_Sector(), stripHit.get_Strip()).getFirstEndPoint().z();
			double x2 = Geometry.getEndPoints(stripHit.get_Layer(), stripHit.get_Sector(), stripHit.get_Strip()).getSecondEndPoint().x();
			double y2 = Geometry.getEndPoints(stripHit.get_Layer(), stripHit.get_Sector(), stripHit.get_Strip()).getSecondEndPoint().y();
			double z2 = Geometry.getEndPoints(stripHit.get_Layer(), stripHit.get_Sector(), stripHit.get_Strip()).getSecondEndPoint().z();
			
			Line3D stripLine = new Line3D(x1,y1,z1,x2,y2,z2);
			
			double docaToTrk = fitLine.distance(stripLine).length();
        */    		
			return docaToTrk;
		}
		
	
		private static int[] modulesLayerId; // layer used to id the plane by increasing index from the bottom up
		private static int[] modulesSectorId; // and sector used to id the plane by increasing index from the bottom up
		private void set_modulePlaneIds() {

			if(Constants.TRACKERCOMPOSITION == "Stacked") {// test stand with up to 8 modules parallel
				// layer 8 and sector 13 correspond to the bottom-most module 
				// layer 7 and sector 13 correspond to the next module from the bottom up
				// layer 6 and sector 10 correspond to the next module from the bottom up
				// layer 5 and sector 10 correspond to the next module from the bottom up
				// layer 4 and sector 8 correspond to the next module from the bottom up
				// layer 3 and sector 8 correspond to the next module from the bottom up
				// layer 2 and sector 6 correspond to the next module from the bottom up
				// layer 1 and sector 6 correspond to the next module from the bottom up
				// layer 1 and sector 1 correspond to the next module from the bottom up
				// layer 2 and sector 1 correspond to the next module from the bottom up
				// layer 3 and sector 1 correspond to the next module from the bottom up
				// layer 4 and sector 1 correspond to the next module from the bottom up
				// layer 5 and sector 1 correspond to the next module from the bottom up
				// layer 6 and sector 1 correspond to the next module from the bottom up
				// layer 7 and sector 1 correspond to the next module from the bottom up
				// layer 8 and sector 1 correspond to the next module from the bottom up
				int[] moduleLayerId = {7,6,5,4,3,2,1,0,0,1,2,3,4,5,6,7};
				int[] moduleSectorId = {12,12,9,9,7,7,5,5,0,0,0,0,0,0,0,0};
				
				modulesLayerId = moduleLayerId;
				modulesSectorId = moduleSectorId;
			}
		    
		}
		
		
		public int[] get_LayerEfficiencies(List<Hit> hits) {
			
			Line3D fitLine = this.getLine(); // get the track line
			
			int[] effs = null;
			
			
			this.hitsToArray(hits);
			if(HitArray == null)
				return null;
			
			this.set_modulePlaneIds();			
			
		    effs = new int[modulesLayerId.length];
			//initialize
			for(int i = 0; i<modulesLayerId.length; i++) {
					effs[i] =-1;
			 }
		
			for(int i = 0; i<modulesLayerId.length; i++) {
				
				Shape3D theModule = Constants.MODULEPLANES.get(modulesLayerId[i]).get(modulesSectorId[i]); // layer and sector corresponding to that module 
				
				if(theModule.hasIntersection(fitLine))
					effs[i] =0; // the track intersected the module
				                // now look for a nearby hit
				
				if(HitArray[modulesLayerId[i]][modulesSectorId[i]].length>0){ // looking for hits in that module
					for(int h = 0; h < Constants.NSTRIP; h++) {
						if(HitArray[modulesLayerId[i]][modulesSectorId[i]][h]!=null) {
							Hit theHit = HitArray[modulesLayerId[i]][modulesSectorId[i]][h];
						
							if(calcHitSpatialResolution(theHit)<Double.POSITIVE_INFINITY)
								effs[i] =1;
						}
					}
				}
				//if(effs[i]==0)	
				//	System.out.println(modulesLayerId.length+" layer "+(i+1)+" efficiency "+effs[i]);
			}
			
			return effs;
			
			
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


		public List<Point3D> get_trackInterModulePlanes() {
			return _trackInterModulePlanes;
		}


		public void set_trackInterModulePlanes(
				List<Point3D> _trackInterModulePlanes) {
			this._trackInterModulePlanes = _trackInterModulePlanes;
		}
		
		public void update_Crosses(double s_yxfit, double s_yzslope, Geometry geo) {
			for(Cross c : this) {
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
}
