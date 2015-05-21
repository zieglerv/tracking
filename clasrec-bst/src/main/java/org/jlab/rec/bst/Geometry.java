package org.jlab.rec.bst;

import java.io.FileNotFoundException;

import org.jMath.Vector.threeVec;
import trackfitter.surface.Line;
import trackfitter.track.Helix;

public class Geometry {
	
	public Geometry() {
		
	}
	
    // Comments on the Geometry of the BST 
	//------------------------------------
	// The BST geometry consists of 3 (or 4) superlayers of modules. 
	// Each superlayer contains two layers of modules, labeled A and B. 
	// Layer B corresponds to the top layer as seen from the outside of the detector, 
	// and Layer A to the layer underneath Layer B looking from the outside.  
	// Each module contains 3 sensors (hybrid, intermediate, far).
	// The hybrid, intermediate and far sensors are aligned in the direction of the beam 
	// corresponding to the positive z-axis in the laboratory frame.  
	// The coordinate system in the lab frame (center of the target) is a right handed system, with 
	// the z unit vector in the direction of the beam, and the y unit vector pointing up; 
	// the x unit vector points therefore to the left when looking in the direction of the beam.
	// The numbering convention for the sectors is as follows:
	// sector 1 modules oriented at 90 deg (80 deg) with respect to the y-axis for superlayers 1,2,4 (3); 
	// sector numbers increase in the clockwise direction (viewed in the direction of the beam).  
	// The strips in the hybrid sensor of Layer B are connected to the pitch adapter and 
	// and implanted with 156 micron pitch.  There are 256 strips oriented at graded angle 
	// from 0 to +3 deg with respect to the bottom edge of layer B which corresponds to the z-direction. 
	// Strip number 1 in Layer B is parallel to the bottom of the sensor.  

 
		//for making bst outline fig
		
		public threeVec getPlaneModuleOrigin(int layer, int sector) {
			//shift the local origin to the physical orign instead of active area
			threeVec point0 = new threeVec(0,0,0);
			//point0.set(transformToLabFrame(-(42.000-40.052)/2., 0, layer, sector, 0));
			point0.set(transformToFrame( sector,  layer, -(42.000-40.052), 0, 0, "lab", ""));
			return point0;
		}
		public threeVec getPlaneModuleEnd(int layer, int sector) {
			//shift the local origin to the physical orign instead of active area
			threeVec point0 = new threeVec(0,0,0);
			//point0.set(transformToLabFrame(-(42.000-40.052)/2., 0, layer, sector, 0));
			point0.set(transformToFrame( sector,  layer, 42.000, 0, 0, "lab", ""));
			return point0;
		}
		
		
		
		//*** 
		public  int findSectorFromAngle(int layer, double trackPhiAtLayer) {
			int Sect = Constants.NSECT[layer-1];
			for(int s = 0; s<Constants.NSECT[layer-1]-1; s++) {
				int sector = s+1;
				double phi1 = this.getPlaneModuleOrigin(layer, sector).phi();
				double phi2 = this.getPlaneModuleOrigin(layer, sector+1).phi();
				if(phi1<0)
					phi1+=2.*Math.PI;
				if(phi2<0)
					phi2+=2.*Math.PI;
				
				if(trackPhiAtLayer>=phi1 && trackPhiAtLayer<=phi2)
					Sect = sector;
			}
			return Sect;
		}
		
		//***
		public threeVec findBSTPlaneNormal(int sector, int layer) {
			
			double angle = 2.*Math.PI*((sector-1)/(double)Constants.NSECT[layer-1]);
			
		    return new threeVec(-Math.sin(angle), Math.cos(angle), 0);
		}
		//***
		public  double[] getLocCoord(double s1, double s2) { //2 top, 1 bottom
			
			double[] X = new double[2];
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
			
			double z = (b2-b1)/(m1-m2);
			double x = m1*z +b1;
			X[0] = x;
			X[1] = z;
			
			return X;

		}
		public double[] getLocCoordErrs(int lay1, int lay2, double s1, double s2, double z) {
			double[] Xerr = new double[2];
			
			double sigma1 = getSingleStripResolution( lay1, (int) s1, z);
			double sigma2 = getSingleStripResolution( lay2, (int) s2, z);
			
			
			Xerr[0] = Math.sqrt(sigma1*sigma1+sigma2*sigma2);
			Xerr[1] = (getLocCoord( s1-0.5,  s2-0.5)[1]+
					-getLocCoord( s1+0.5,  s2+0.5)[1]);
			//Xerr[0] = (getLocCoord( s1-0.5,  s2-0.5)[0]+
			//		-getLocCoord( s1+0.5,  s2+0.5)[0]);
			
			return Xerr;
			
		}
		//***
		public  threeVec transformToFrame(int sector, int layer, double x, double y, double z, String frame, String MiddlePlane) {
			
			// global rotation angle
			double Glob_rangl = ((double) (sector-1)/(double) Constants.NSECT[layer-1])*2.*Math.PI + Constants.PHI0[layer-1];
			// angle to rotate to global frame
			double Loc_to_Glob_rangl = Glob_rangl-Constants.LOCZAXISROTATION;

			double gap = 0;
			if(MiddlePlane == "middle") {
				if((layer-1)%2==0) { // for a cross take the bottom layer
					gap = Constants.MODULERADIUS[layer][sector-1]-Constants.MODULERADIUS[layer-1][sector-1];
				}
			}
			double lTx = (Constants.MODULERADIUS[layer-1][sector-1]+0.5*gap)*Math.cos(Glob_rangl);
			double lTy = (Constants.MODULERADIUS[layer-1][sector-1]+0.5*gap)*Math.sin(Glob_rangl); 
			double lTz = Constants.Z0[layer-1]; 

			
			//rotate and translate
			double cosRotation = Math.cos(Loc_to_Glob_rangl);
			double sinRotation = Math.sin(Loc_to_Glob_rangl);

			double xt =0;
			double yt =0;
			double zt =0;
			
			if(frame == "lab") {
				 xt = (x-0.5*Constants.ACTIVESENWIDTH)*cosRotation - y*sinRotation + lTx;
				 yt = (x-0.5*Constants.ACTIVESENWIDTH)*sinRotation + y*cosRotation + lTy;
				 zt = z + lTz;
			}
			if(frame == "local") {
				 xt=  (x-lTx)*cosRotation +(y-lTy)*sinRotation  + 0.5*Constants.ACTIVESENWIDTH;
				 yt= -(x-lTx)*sinRotation +(y-lTy)*cosRotation  ;
				 zt = z - lTz ;
			}
			return new threeVec(xt,yt,zt);
		}
		//*** point and its error
		public  double[] getCrossPars(int sector, int upperlayer, double s1, double s2, String frame, threeVec trkDir) {
			double[] vals = new double[6];
			double z0 = getLocCoord(s1,s2)[1];
			
			// if first iteration trkDir == null
			double s2corr = s2;
			
			if(trkDir!=null) { // for some reason the algorithm is not working for s =1 ... may the digi has an issue
				s2corr = s2 + (double)getStripIndexShift( sector,  upperlayer,  trkDir, s2, z0);
				//update using corrected z
				z0 = getLocCoord(s1,s2corr)[1];
				s2corr = s2 + (double)getStripIndexShift( sector,  upperlayer,  trkDir, s2, z0);
			}
			
			 if(s2 == 1)
				 s2corr =s2;
			double zf = z0 = getLocCoord(s1,s2corr)[1];
			
			
			if(upperlayer%2!=0) // should not happen as is upper layer...but just in case
				s2corr =s2;
			
			double[] LC = getLocCoord(s1,s2corr);
			double LC_x = LC[0];
			double LC_z = LC[1];
			
			threeVec crPoint = transformToFrame( sector,  upperlayer-1, LC_x, 0, LC_z, "lab", "middle");
			
			vals[0] = crPoint.x();
			vals[1] = crPoint.y();
			vals[2] = crPoint.z();
			
			
			double [] LCErr = getLocCoordErrs(upperlayer-1, upperlayer, s1, s2corr, zf);
			double LCErr_x = LCErr[0];
			double LCErr_z = LCErr[1];
			
			
			 // global rotation angle to get the error in the lab frame
			int layerIdx = upperlayer-1;
	        double Glob_rangl = ((double) (sector-1)/(double) Constants.NSECT[layerIdx])*2.*Math.PI;
	        // angle to rotate to global frame
	        double Loc_to_Glob_rangl = Glob_rangl-Constants.PHI0[layerIdx];
	       
	        double cosRotation = Math.cos(Loc_to_Glob_rangl);
	        double sinRotation = Math.sin(Loc_to_Glob_rangl);	        

	        double yerr = Math.abs(cosRotation*LCErr_x);
	        double xerr = Math.abs(sinRotation*LCErr_x);
		   
			vals[3] = xerr;
			vals[4] = yerr;
			vals[5] = LCErr_z;
			
			return vals;
			
			
			
			
		}
		
		
		//****
		public  double getSingleStripResolution(int lay, int strip, double Z) { // as a function of local z
			double Strip = (double) strip;
			double StripUp = Strip+1;
			if(strip == Constants.NSTRIP)
				StripUp = (double) Constants.NSTRIP; //edge strip
			double StripDown = Strip-1;
			if(strip ==1)
				StripDown = 1; //edge strip
		
			
			double pitchToNextStrp = Math.abs(getXAtZ(lay, (double) StripUp,Z)-getXAtZ(lay, (double) Strip,Z)); // this is P- in the formula below
			double pitchToPrevStrp = Math.abs(getXAtZ(lay, (double) StripDown,Z)-getXAtZ(lay, (double) Strip,Z)); // this is P+ in the formula below 

			
			// For a given strip (for which we estimate the resolution), P+ is the pitch to the strip above (at z position) and P- to that below in the local coordinate system of the module.
			// The current design of the BST is simulated in gemc such that each strip provides hit-no-hit information, and the single strip resolution is 
			// therefore given by the variance,
			// sigma^2 = (2/[P+ + P-]) {integral_{- P-/2}^{P+/2) x^2 dx -[integral_{- P-/2}^{P+/2) x dx]^2}
			//this gives, sigma^2 = [1/(P+ + P-)]*[ (P+^3 + P-^3)/12 - (P+^2 - P-^2)^2/[32(P+ + P-)] ]

			double Pp2 = pitchToNextStrp*pitchToNextStrp;
			double Pp3 = pitchToNextStrp*Pp2;
			double Pm2 = pitchToPrevStrp*pitchToPrevStrp;
			double Pm3 = pitchToPrevStrp*Pm2;
			double Psum = pitchToNextStrp+pitchToPrevStrp;
			double invPsum = 1./Psum;
			double firstTerm = (Pp3+Pm3)/12.;
			double secondTerm = ((Pp2-Pm2)*(Pp2-Pm2)*invPsum)/32.;
			double strip_sigma_sq = (firstTerm-secondTerm)*invPsum;

			double strip_sigma = Math.sqrt(strip_sigma_sq);

			return strip_sigma;
		}
		//****
		public double getDOCAToStrip(int sector, int layer, double centroidstrip, threeVec point0) {
			
			// local angle of  line graded from 0 to 3 deg.
			double ialpha = (centroidstrip-1)*Constants.STEREOANGLE/(double) (Constants.NSTRIP-1); 
			//the active area starts at the first strip 	
			double interc = (centroidstrip)*Constants.PITCH;		

			// Equation for strip line is x = mz + b [i.e. z is the direction of the length of the module]
			// -------------------------------------
			double m1 = -Math.tan(ialpha);
			double m2 =  Math.tan(ialpha);
			double b1 = Constants.ACTIVESENWIDTH - interc;
			double b2 = interc;

			threeVec vecAlongStrip = new threeVec();
			threeVec pointOnStrip = new threeVec();
			
			
			if(layer%2==0) { //layers 2,4,6 == top ==j ==>(2) : regular configuration
				vecAlongStrip = transformToFrame( sector,  layer, m2, 0, 1, "lab", ""); 
				pointOnStrip = transformToFrame( sector,  layer, b2, 0, 0, "lab", "");					
			}
			if(layer%2==1) { //layers 1,3,5 == bottom ==i ==>(1) : regular configuration
				vecAlongStrip = transformToFrame( sector,  layer, m1, 0, 1, "lab", ""); 
				pointOnStrip = transformToFrame( sector,  layer, b1, 0, 0, "lab", "");		
			}	

			if(vecAlongStrip.len()>0)
			vecAlongStrip.multi(1./vecAlongStrip.len());

			Line stripLine = new Line(pointOnStrip,vecAlongStrip);
			
			return stripLine.distanceFromPoint(point0);
			
		}
		//****
		// in the local coordinate system 
		public  double getXAtZ(int layer, double centroidstrip, double Z) {
			double X =0;
			// local angle of  line graded from 0 to 3 deg.
			double ialpha = (centroidstrip-1)*Constants.STEREOANGLE/(double) (Constants.NSTRIP-1); 
			//the active area starts at the first strip 	
			double interc = (centroidstrip)*Constants.PITCH;		

			// Equation for strip line is x = mz + b [i.e. z is the direction of the length of the module]
			// -------------------------------------
			double m1 = -Math.tan(ialpha);
			double m2 =  Math.tan(ialpha);
			double b1 = Constants.ACTIVESENWIDTH - interc;
			double b2 = interc;

			
			if(layer%2==0) { //layers 2,4,6 == top ==j ==>(2) : regular configuration
				
				X = m2*Z + b2;						
			}
			if(layer%2==1) { //layers 1,3,5 == bottom ==i ==>(1) : regular configuration
							
				X = m1*Z + b1;
			}	


			return X;
		}

		
		
		//***
		public  double getStripIndexShift(int sector, int layer, threeVec trkDir, double s2, double z) {
			double tx = trkDir.x();
			double ty = trkDir.y();
			threeVec trkDir_t = new threeVec(tx/Math.sqrt(tx*tx+ty*ty),ty/Math.sqrt(tx*tx+ty*ty),0);
			threeVec n = findBSTPlaneNormal(sector, layer);
			
			int sign = 1;
			if(Math.acos(n.dot(trkDir_t))>Math.PI/2)
				sign = -1;
		   // int shift = (int)((Constants.LAYRGAP*n.cross(trkDir_t).z())/Constants.PITCH);
		    //
		    //correction to the pitch to take into account the grading of the angle -- get the upper or lower strip depending on the trkdir
			double pitchcorr = Constants.PITCH;
			/*
			int sign = (int) Math.signum(n.cross(trkDir_t).z());
			if(sign>0)
				pitchcorr+=z*(Math.tan((s2)*Constants.STEREOANGLE/(double) (Constants.NSTRIP-1))-
					Math.tan((s2-1)*Constants.STEREOANGLE/(double) (Constants.NSTRIP-1)));		
			if(sign<0)
				pitchcorr+=z*(Math.tan((s2-1)*Constants.STEREOANGLE/(double) (Constants.NSTRIP-1))-
					Math.tan((s2-2)*Constants.STEREOANGLE/(double) (Constants.NSTRIP-1)));		
			 */
			
			//	pitchcorr+=0.5*z*(Math.tan((s2)*Constants.STEREOANGLE/(double) (Constants.NSTRIP-1))-
			//			Math.tan((s2-2)*Constants.STEREOANGLE/(double) (Constants.NSTRIP-1)));
			double layerGap = Constants.MODULERADIUS[1][0]-Constants.MODULERADIUS[0][0];
			double shift = ((sign*layerGap*n.cross(trkDir_t).z())/pitchcorr);
			
			return shift;
		}
		//***
	    public double planeNormDotTrkDir(int sector, int layer, threeVec trkDir, double s2, double z) {
			double tx = trkDir.x();
			double ty = trkDir.y();
			threeVec trkDir_t = new threeVec(tx/Math.sqrt(tx*tx+ty*ty),ty/Math.sqrt(tx*tx+ty*ty),0);
			threeVec n = findBSTPlaneNormal(sector, layer);
			
			return Math.abs(n.dot(trkDir_t));
	    }
			
		
		//***
		public  threeVec intersectionOfHelixWithPlane(int layer, int sector, Helix helix) {		    
			
			int nstep = 1;
			double stepSize = 0.001;
			
			double Theta = Math.atan2((Constants.ACTIVESENWIDTH/2),Constants.MODULERADIUS[layer-1][sector-1]);
			double RMin = Constants.MODULERADIUS[layer-1][sector-1]; 
			double RMax = RMin/Math.cos(Theta);
			double R = RMin;
			
			threeVec InterPoint = helix.getPointAtRadius(R);
			
			double minDelta = RMax-RMin;
			
			while(R<RMax) {
				
				threeVec I = helix.getPointAtRadius(R);
				threeVec Inorm = I.mult(1./I.len());
				
				double Rinter =  RMin/findBSTPlaneNormal( sector, layer).dot(Inorm);
				
				if(Math.abs(Rinter-I.rt())<minDelta) {
					InterPoint = I;
					minDelta = Math.abs(Rinter-I.rt());
				}
				R+=nstep*stepSize;
				nstep++;
				
					
			}
			return InterPoint;
			
							
		}
		
		
	
	
    
	

		
	      public static void main (String arg[]) throws FileNotFoundException {
	    	 
	    	  Constants.Load();
	    	  
	    	  
	    		
	    	  //System.out.println(Constants.MODULEPLANES.get(0).get(0).hasIntersection(1));
	    	  /*
	    		PrintWriter pw = new PrintWriter(new File("/Users/ziegler/bst_new_strips.txt"));
	    		
	    		pw.println("! layer sector strip localx1 localy1 localz1 localx2 localy2 localz2 x1 y1 z1 x2 y2 z2");
	    		
	    		for(int layer = 1; layer <=8; layer ++){
	    			for(int sector = 1; sector <= Constants.NSECT[layer-1]; sector ++){
	    				
	    				for(int strip = 1; strip <= Constants.NSTRIP; strip++){
	    					
	    					LineSegment line =getEndPoints(layer, sector, strip);
	    					LineSegment lineLCS =getLocalCoordinates(layer, strip);
	    					threeVec localp1 = lineLCS.getFirstEndPoint();
	    					threeVec localp2 = lineLCS.getSecondEndPoint();
	    					threeVec p1 = line.getFirstEndPoint();
	    					threeVec p2 = line.getSecondEndPoint();
	    					pw.printf("%d %d %d %f %f %f %f %f %f %f %f %f %f %f %f\n", layer, sector, strip,
	    							localp1.x(), localp1.y(), localp1.z(), localp2.x(), localp2.y(), localp2.z(),
	    							p1.x(), p1.y(), p1.z(), p2.x(), p2.y(), p2.z());
	    					
	    				}
	    			}
	    		}
	    	
	    		pw.close();
	    		*/
	    	  ///
	    	  /*
	    		PrintWriter pw = new PrintWriter(new File("/Users/ziegler/bst_geo.txt"));
	    		
	    		pw.println("! layer sector x0 y0 x1 y1 z0 z1 z2 z3 z4 z5 ");
	    		
	    		for(int layer = 1; layer <=8; layer ++){
	    			for(int sector = 1; sector <= Constants.NSECT[layer-1]; sector ++){
	    				System.out.println("sector "+sector +" layer " + layer 
	    						+" x0 " +getPlaneModuleOrigin( layer,  sector).x()+" y0 " +getPlaneModuleOrigin( layer,  sector).y()
	    						+" x1 "+getPlaneModuleEndPts(layer, sector).x()+" y1 "+getPlaneModuleEndPts(layer, sector).y()
	    						+" z0 "+getPlaneModuleOrigin( layer,  sector).z()
	    						+" z1 "+(getPlaneModuleOrigin( layer,  sector).z()+Constants.ACTIVESENLEN)
	    						+" z2 "+(getPlaneModuleOrigin( layer,  sector).z()+Constants.ACTIVESENLEN+1*Constants.DEADZNLEN)
	    						+" z3 "+(getPlaneModuleOrigin( layer,  sector).z()+2*Constants.ACTIVESENLEN+1*Constants.DEADZNLEN)
	    						+" z4 "+(getPlaneModuleOrigin( layer,  sector).z()+2*Constants.ACTIVESENLEN+2*Constants.DEADZNLEN)
	    						+" z5 "+(getPlaneModuleOrigin( layer,  sector).z()+3*Constants.ACTIVESENLEN+2*Constants.DEADZNLEN)
	    						
	    						);
	    				
	    					
	    				pw.printf("%d %d %f %f %f %f %f %f %f %f %f %f\n", layer, sector,
	    				getPlaneModuleOrigin( layer,  sector).x(),getPlaneModuleOrigin( layer,  sector).y(),
						getPlaneModuleEndPts(layer, sector).x(),getPlaneModuleEndPts(layer, sector).y(),
						getPlaneModuleOrigin( layer,  sector).z(),
						(getPlaneModuleOrigin( layer,  sector).z()+Constants.ACTIVESENLEN),
						(getPlaneModuleOrigin( layer,  sector).z()+Constants.ACTIVESENLEN+1*Constants.DEADZNLEN),
						(getPlaneModuleOrigin( layer,  sector).z()+2*Constants.ACTIVESENLEN+1*Constants.DEADZNLEN),
						(getPlaneModuleOrigin( layer,  sector).z()+2*Constants.ACTIVESENLEN+2*Constants.DEADZNLEN),
						(getPlaneModuleOrigin( layer,  sector).z()+3*Constants.ACTIVESENLEN+2*Constants.DEADZNLEN));
	    					
	    				
	    			}
	    		}
	    		pw.close();
	    		*/
	    	 
	  	}
		
}
