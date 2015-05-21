package org.jlab.rec.bst.cross;

import java.util.ArrayList;
import java.util.List;

import org.jMath.Vector.threeVec;
import org.jlab.rec.bst.Constants;
/**
 * A class with methods used to find lists of crosses.  This is the Pattern Recognition step used in track seeding, to 
 * find the points that are consistent with belonging to the same track.  
 * This step precedes the initial estimates of the track parameters.
 * @author ziegler
 *
 */


public class CrossListFinder  {
	
	
	public CrossListFinder() {					
	}
	
	/**
	 * 
	 * @param crosses the list of crosses in the event
	 * @return the list of crosses determined to be consistent with belonging to a track in the bst
	 */
	public CrossList findCandidateCrossLists(List<Cross> crosses) {
		// 
		ArrayList<ArrayList <Cross> > trkCnds = new ArrayList<ArrayList <Cross> >();
		  
		if(crosses.size()>0) {
			
			ArrayList<Cross> bstcrosslistFirstRg = new ArrayList<Cross>();
		    ArrayList<Cross> bstcrosslistSecondRg = new ArrayList<Cross>();
		    ArrayList<Cross> bstcrosslistThirdRg = new ArrayList<Cross>();
		    ArrayList<Cross> bstcrosslistFourthRg = new ArrayList<Cross>();

		    
			ArrayList<Cross> bstcrosslistRg1 = new ArrayList<Cross>();
		    ArrayList<Cross> bstcrosslistRg2 = new ArrayList<Cross>();
		    ArrayList<Cross> bstcrosslistRg3 = new ArrayList<Cross>();
		    ArrayList<Cross> bstcrosslistRg4 = new ArrayList<Cross>();

		    int NonEmptyReg = 0;
		    for(Cross bst : crosses) {
						 
		    	if(bst.get_Region()==1)
		    		bstcrosslistRg1.add(bst);
		    	if(bst.get_Region()==2)
		    		bstcrosslistRg2.add(bst);
		    	if(bst.get_Region()==3)
		    		bstcrosslistRg3.add(bst);
		    	if(bst.get_Region()==4)
		    		bstcrosslistRg4.add(bst);
		    }
		    
		    if(bstcrosslistRg1.size()>0) {
		    	NonEmptyReg++;
		    	bstcrosslistFirstRg = bstcrosslistRg1;
		    }
		    if(bstcrosslistRg2.size()>0) {
		    	NonEmptyReg++;
		    	if(NonEmptyReg==2)
		    		bstcrosslistSecondRg = bstcrosslistRg2;
		    	if(NonEmptyReg==1)
		    		bstcrosslistFirstRg = bstcrosslistRg2;
		    }
		    if(bstcrosslistRg3.size()>0) {
		    	NonEmptyReg++;
		    	if(NonEmptyReg==3)
		    		bstcrosslistThirdRg = bstcrosslistRg3;
		    	if(NonEmptyReg==2)
		    		bstcrosslistSecondRg = bstcrosslistRg3;
		    	if(NonEmptyReg==1)
		    		bstcrosslistFirstRg = bstcrosslistRg3;
		    }
		    if(bstcrosslistRg4.size()>0) {
		    	NonEmptyReg++;
		    	if(NonEmptyReg==4)
		    		bstcrosslistFourthRg = bstcrosslistRg4;
		    	if(NonEmptyReg==3)
		    		bstcrosslistThirdRg = bstcrosslistRg4;
		    	if(NonEmptyReg==2)
		    		bstcrosslistSecondRg = bstcrosslistRg4;
		    	if(NonEmptyReg==1)
		    		bstcrosslistFirstRg = bstcrosslistRg4;
		    	
		    }
		    
		    int index =0;
		    if(NonEmptyReg<3)
		    	return null;  // need 3 crosses
		    if(bstcrosslistFourthRg.size()==0) {
		    	trkCnds = this.findCandUsingThreeCrosses(bstcrosslistFirstRg,bstcrosslistSecondRg, bstcrosslistThirdRg);
		    }
		    if(bstcrosslistFourthRg.size()!=0) {
		    	ArrayList<ArrayList <Cross> > trkCndsR123 = this.findCandUsingThreeCrosses(bstcrosslistFirstRg,bstcrosslistSecondRg, bstcrosslistThirdRg);
		    	ArrayList<ArrayList <Cross> > trkCndsR1234 = new ArrayList<ArrayList <Cross> >();
		    	if(trkCndsR123!=null) {
		    		trkCndsR1234 = this.linkToFourthRegion(trkCndsR123, bstcrosslistFourthRg); 
		    	}
		    	ArrayList<ArrayList <Cross> > trkCndsR124 = this.findCandUsingThreeCrosses(bstcrosslistFirstRg,bstcrosslistSecondRg, bstcrosslistFourthRg);
		    	ArrayList<ArrayList <Cross> > trkCndsR134 = this.findCandUsingThreeCrosses(bstcrosslistFirstRg,bstcrosslistThirdRg, bstcrosslistFourthRg);
		    	ArrayList<ArrayList <Cross> > trkCndsR234 = this.findCandUsingThreeCrosses(bstcrosslistSecondRg,bstcrosslistThirdRg, bstcrosslistFourthRg);
		    	
		    	//remove overlaps
		    	if(trkCndsR1234!=null) {
			    	for(int i = 0; i<trkCndsR1234.size(); i++) {
			    		for(int j = 0; j<trkCndsR123.size(); j++) {
			    			if(this.Contains(trkCndsR1234.get(i),trkCndsR123.get(j))) {			    				
			    				trkCndsR123.remove(j); 
			    			}
			    		}
			    		for(int j = 0; j<trkCndsR124.size(); j++) {
			    			if(this.Contains(trkCndsR1234.get(i),trkCndsR124.get(j))) {			    				
			    				trkCndsR124.remove(j); 
			    			}
			    		}
			    		if(trkCndsR134!=null) {
				    		for(int j = 0; j<trkCndsR134.size(); j++) {
				    			if(this.Contains(trkCndsR1234.get(i),trkCndsR134.get(j))) {
				    				trkCndsR134.remove(j); 
				    			}
				    		}
			    		}
			    		if(trkCndsR234!=null) {
				    		for(int j = 0; j<trkCndsR234.size(); j++) {
				    			if(this.Contains(trkCndsR1234.get(i),trkCndsR234.get(j))) {
				    				trkCndsR234.remove(j); 
				    			}
				    		}
			    		}
			    	}
		    	}
		    	trkCnds = new ArrayList<ArrayList <Cross> >();
		    	//add lists
		    	if(trkCndsR1234!=null) {
		    		for(int i = 0; i<trkCndsR1234.size(); i++) {
		    			trkCnds.add(index, trkCndsR1234.get(i));
		    			index++;
		    		}
		    	}
		    	if(trkCndsR123!=null) {
		    		for(int i = 0; i<trkCndsR123.size(); i++) {
		    			trkCnds.add(index, trkCndsR123.get(i));
		    			index++;
		    		}
		    	}
		    	if(trkCndsR124!=null) {
		    		for(int i = 0; i<trkCndsR124.size(); i++) {
		    			trkCnds.add(index, trkCndsR124.get(i));
		    			index++;
		    		}
		    	}
		    	if(trkCndsR134!=null) {
		    		for(int i = 0; i<trkCndsR134.size(); i++) {
		    			trkCnds.add(index, trkCndsR134.get(i));
		    			index++;
		    		}
		    	}
		    	if(trkCndsR234!=null) {
		    		for(int i = 0; i<trkCndsR234.size(); i++) {
		    			trkCnds.add(index, trkCndsR234.get(i));
		    			index++;
		    		}
		    	}
		    }
		}
		
		CrossList crossList = new CrossList();
		crossList.addAll(trkCnds);
		return crossList;
	}
	private boolean Contains(ArrayList<Cross> arrayList,
			ArrayList<Cross> arrayList2) {
		
		if(arrayList.size()<arrayList2.size())
			return false;
		
		int array2size = arrayList2.size();
		for(int i =0; i<arrayList.size(); i++) {
			Cross c1 = arrayList.get(i);
			
			for(int j = 0; j<array2size; j++) {
				Cross c2 = arrayList2.get(j);
				
				if(c1.get_Id()==c2.get_Id()) {
					arrayList2.remove(j);
					
					if(array2size>0)
						array2size--;
				}
			}
			
			
			
		}
		if(array2size==0) {
			return true;
		} else {
			return false;
		}
	}

	
		

	private ArrayList<ArrayList <Cross> > linkToFourthRegion(ArrayList<ArrayList<Cross>> trkCndsR123,
			ArrayList<Cross> bstcrosslistFourthRg) {
		
		ArrayList<ArrayList <Cross> > trkCnds = new ArrayList<ArrayList <Cross> >();
		// using the existing cross list with 3 crosses try to see if 4 fourth one can be linked
		int index = 0;
		
		for(int i = 0; i<trkCndsR123.size(); i++) {
			Cross c1 = trkCndsR123.get(i).get(0);
			Cross c2 = trkCndsR123.get(i).get(1);
			Cross c3 = trkCndsR123.get(i).get(2);
		
			for(Cross c4 : bstcrosslistFourthRg) { // loop over 4th region
				ArrayList<Cross> ct = new ArrayList<Cross>(4);
				double drdz1 = get_PointInfo(c1,null,null)[1]/get_PointInfo(c1,null,null)[0];
				double drdz2 = get_PointInfo(c2,null,null)[1]/get_PointInfo(c2,null,null)[0]; 
				double drdz3 = get_PointInfo(c3,null,null)[1]/get_PointInfo(c3,null,null)[0];
				double drdzsum = drdz1 + drdz2 + drdz3	;
				//check the fourth cross
				double phi14 = Math.abs(get_PointInfo(c1,c4,null)[2]);
				double rad234 = get_PointInfo(c2,c3,c4)[3];  
				double drdz4 = get_PointInfo(c4,null,null)[1]/get_PointInfo(c4,null,null)[0];						 
				drdzsum += drdz4;
				 if(phi14<=Constants.phi14cut && Math.abs(rad234)>=Constants.radcut 
						 && Math.abs((drdz4-drdzsum/4.)/(drdzsum/4.))<=Constants.drdzcut) { //pass
					 ct.add(c1);
					 ct.add(c2);
					 ct.add(c3);
					 ct.add(c4);
					 if(!(trkCnds.containsAll(ct))) {					   	   				
	   	   				trkCnds.add(index, ct);	
	   	   				index++;
		   	   		}	
				 }
			}
		}
		return trkCnds;

	}

	private ArrayList<ArrayList <Cross> > findCandUsingThreeCrosses(
			ArrayList<Cross> bstcrosslistFirstRg,
			ArrayList<Cross> bstcrosslistSecondRg,
			ArrayList<Cross> bstcrosslistThirdRg) {
		int index =0;
		ArrayList<Cross> ct = new ArrayList<Cross>(3);
		ArrayList<ArrayList <Cross> > trkCnds = new ArrayList<ArrayList <Cross> >();
		for(Cross c1 : bstcrosslistFirstRg) { 
			for(Cross c2 : bstcrosslistSecondRg) { 
				for(Cross c3 : bstcrosslistThirdRg) {  
					
			        // selection for first 3 hits:	
					//---------------------------	
					 double phi12 = Math.abs(get_PointInfo(c1,c2,null)[2]);
						if(phi12>Constants.phi12cut)
							continue;				 
					 double phi13 = Math.abs(get_PointInfo(c1,c3,null)[2]);  
					 if(phi13>Constants.phi13cut)
						 continue; 	
					 double rad123 = get_PointInfo(c1,c2,c3)[3]; 
					 if(Math.abs(rad123)<Constants.radcut)
						 continue; 		
					 				 
					 double drdz1 = get_PointInfo(c1,null,null)[1]/get_PointInfo(c1,null,null)[0];
					 double drdz2 = get_PointInfo(c2,null,null)[1]/get_PointInfo(c2,null,null)[0]; 
					 double drdz3 = get_PointInfo(c3,null,null)[1]/get_PointInfo(c3,null,null)[0];
						 
					 double drdzsum = drdz1 + drdz2 + drdz3;
					 
					 if( Math.abs((drdz1-drdzsum/3.)/(drdzsum/3.)) >Constants.drdzcut)
						 continue;
					 if( Math.abs((drdz2-drdzsum/3.)/(drdzsum/3.)) >Constants.drdzcut)
						 continue;
					 if( Math.abs((drdz3-drdzsum/3.)/(drdzsum/3.)) >Constants.drdzcut)
						 continue;
			 
					ct = new ArrayList<Cross>(3);
					ct.add(c1);
					ct.add(c2);
					ct.add(c3);
					if(!(trkCnds.containsAll(ct))) {					   	   				
	   	   				trkCnds.add(index, ct);	
	   	   				index++;
	   	   			}		
				}
			}
		}
		return trkCnds;
	}

	private double[] get_PointInfo(Cross bt, Cross bt2, Cross bt3) {
		
		double[] arrayInfo = new double[4];
		if(bt == null)
			return null;
		
		threeVec btp1 = null;
		threeVec btp2 = null;
		threeVec btp3 = null;
		

		if(bt!=null)
			btp1=bt.get_Point();
		if(bt2!=null)
			btp2=bt2.get_Point();
		if(bt3!=null)
			btp3=bt3.get_Point();
	
		
		arrayInfo[0] = btp1.z();
		arrayInfo[1] = btp1.rt();
		double cos_ZDiff = 180;
		if(bt2!=null) {
		cos_ZDiff = btp1.dot(btp2)/(btp1.len()*btp2.len());
		}
		double radiusOfCurv = 0;
		if(btp3!=null) {
			if ( Math.abs(btp2.x()-btp1.x())>1.0e-9 && Math.abs(btp3.x()-btp2.x())>1.0e-9) {
		        // Find the intersection of the lines joining the innermost to middle and middle to outermost point
		        double ma   = (btp2.y()-btp1.y())/(btp2.x()-btp1.x());
		        double mb   = (btp3.y()-btp2.y())/(btp3.x()-btp2.x());
		       
		        if (Math.abs(mb-ma)>1.0e-9) {
		        double xcen = 0.5*(ma*mb*(btp1.y()-btp3.y()) + mb*(btp1.x()+btp2.x()) -ma*(btp2.x()+btp3.x()))/(mb-ma);
		        double ycen = (-1./mb)*(xcen - 0.5*(btp2.x()+btp3.x())) + 0.5*(btp2.y()+btp3.y());

		        radiusOfCurv = Math.sqrt(Math.pow((btp1.x()-xcen),2)+Math.pow((btp1.y()-ycen), 2));
		        }
			}
		}
	
		arrayInfo[2] = Math.acos(cos_ZDiff)*180./Math.PI;
		arrayInfo[3] = radiusOfCurv;
		
		return arrayInfo;
	}
 		     

	/**
	 * 
	 * @param crosses the list of crosses in the event
	 * @return the list of crosses determined to be consistent with belonging to a track in the bst
	 */
	public CrossList findCosmicsCandidateCrossLists(List<Cross> crosses) {
		
		/// The principle of Hough Transform in pattern recognition is as follows.
		/// For every point (rho, z) on a line there exists an infinite number of
		/// lines that go through this point.  Each such line can be parametrized
		/// with parameters (r, theta) such that r = rho * cos(theta) + z * sin(theta),
		/// where theta is the polar angle of the line which is perpendicular to it
		/// and intersects the origin, and r is the distance between that line and
		/// the origin, for a given theta.
		/// Hence a point in (rho, z) parameter space corresponds to a sinusoidal
		/// curve in (r, theta) parameter space, which is the so-called Hough-
		/// transform space.
		/// Points that fall on a line in (rho, z) space correspond to sinusoidal
		/// curves that intersect at a common point in Hough-transform space.
		/// Remapping this point in (rho, z) space yields the line that Contains
		/// the points in the original line.
		/// This method is a pattern recognition tool used to select groups of points
		/// belonging to the same shape, such as a line or a circle.
		/// To find the ensemble of points belonging to a line in the original space
		/// the Hough transform algorithm makes use of an array, called an accumulator.
		/// The principle of the accumulator is a counting method.
		/// The dimensions of the array is equal to the number of parameters in
		/// Hough transform space, which is 2 corresponding to the (r, theta) pair
		/// in our particular case.
		/// The bin size in the array are finite intervals in r and theta, which are
		/// called accumulator cells.
		/// The bin content of cells along the curve of discretized (r, theta) values
		/// get incremented.  The cell with the highest count corresponds to the
		/// intersection of the curves.  This is a numerical method to find
		/// the intersection of any number of curves.
		/// Once the accumulator array has been filled with all (r, theta) points
		/// peaks and their associated (rho, z) points are determined.
		/// From these, sets of points belonging to common lines can be
		/// determined.
		/// This is a preliminary pattern recognition method used to identify
		/// reconstructed hits belonging to the same track-segment.

		
		// From this calculate the bin size in the theta accumulator array
		double ThetaMin = 0.;
		double ThetaMax = 360.;
		
		// Define the dimension of the r accumulator array
		int N_t = 90;
		int N_r = 9;
		// From this calculate the bin size in the theta accumulator array
		double RMin = -180;
		double RMax =  180;

		double SizeThetaBin   = (ThetaMax-ThetaMin)/((double) N_t);

		
		int[][] R_Z_Accumul;
		R_Z_Accumul = new int[N_r][N_t];

		// cache the cos and sin theta values [for performance improvement]
		double[] cosTheta_Rz_array;
		double[] sinTheta_Rz_array;

		// the values corresponding to the peaks in the array
		double[] binrMaxR_Z;
		double[] bintMaxR_Z;
		binrMaxR_Z = new double[N_r*N_t];
		bintMaxR_Z = new double[N_r*N_t];

		cosTheta_Rz_array = new double[N_t];
		sinTheta_Rz_array = new double[N_t];

		for(int j_t=0; j_t<N_t; j_t++)  {
			// theta_j in the middle of the bin :
			//System.out.println(" bin "+j_t);
			double theta_j = ThetaMin + (0.5 + j_t)*SizeThetaBin;
			cosTheta_Rz_array[j_t] = Math.cos(Math.toRadians(theta_j));
			sinTheta_Rz_array[j_t] = Math.sin(Math.toRadians(theta_j));
			//System.out.println("             theta "+theta_j+"  cos "+Math.cos(Math.toRadians(theta_j))+" sin "+Math.cos(Math.toRadians(theta_j)));
		}


		// loop over points to fill the accumulator arrays
		for(int i = 0; i < crosses.size(); i++) {
			
			double rho = crosses.get(i).get_Point().y();
			double z = crosses.get(i).get_Point().x();
			
			// fill the accumulator arrays
			for(int j_t=0; j_t<N_t; j_t++) {
				// cashed theta_j in the middle of the bin :
				//double theta_j   = ThetaMin + (0.5 + j_t)*SizeThetaBin;
				// r_j corresponding to that theta_j:
				double r_j  = rho*cosTheta_Rz_array[j_t] + z*sinTheta_Rz_array[j_t];
				// this value of r_j falls into the following bin in the r array:
				int j_r = (int) Math.floor(N_r*(r_j - RMin)/(float) (RMax - RMin));
				//System.out.println("check range "+RMin+" [ "+r_j +" --> "+j_r+" ] "+RMax);
				// increase this accumulator cell:
				R_Z_Accumul[j_r][j_t]++;
				//if(R_Z_Accumul[j_r][j_t]>=1)
					//System.out.println(" accumulator value at (x, y ) = ("+r_j+", "+(ThetaMin + (0.5 + j_t)*SizeThetaBin) +") falls in bin ["+j_r+" ] ["+j_t+" ] = "+R_Z_Accumul[j_r][j_t]);
			}

		}
		
		// loop over accumulator array to find peaks (allows for more than one peak for multiple tracks)
		// The accumulator cell count must be at least half the total number of hits
		// Make binrMax, bintMax arrays to allow for more than one peak

		int thresholdMin = 3; 
		int threshold = thresholdMin; // minimum number of crosses requirement
		int nbPeaksR_Z = 0;


		// 1st find the peaks in the R_Z accumulator array
		for(int ibinr1=0;ibinr1<N_r;ibinr1++) {
			for(int ibint1=0;ibint1<N_t;ibint1++) {
				//find the peak
				
				if(R_Z_Accumul[ibinr1][ibint1]>=thresholdMin ) {
					
					if(R_Z_Accumul[ibinr1][ibint1]>threshold)
						threshold = R_Z_Accumul[ibinr1][ibint1];

					binrMaxR_Z[nbPeaksR_Z] = ibinr1;
					bintMaxR_Z[nbPeaksR_Z] = ibint1;
					nbPeaksR_Z++;

				}
			}
		}

		// For a given Maximum value of the accumulator, find the set of points associated with it;
		//  for this, begin again loop over all the points
		ArrayList<ArrayList <Cross> > crossLists = new ArrayList<ArrayList <Cross> >();
		int index =0;

		for(int p = nbPeaksR_Z-1; p>-1; p--) {
			// Make a new list
			ArrayList<Cross> crossList = new ArrayList<Cross>();
			
			
			for(int i = 0; i < crosses.size(); i++) {
				
				double rho = crosses.get(i).get_Point().y();
				double z = crosses.get(i).get_Point().x();
				
				for(int j_t=0; j_t<N_t; j_t++) {
					// theta_j in the middle of the bin :
					//double theta_j   = ThetaMin + (0.5 + j_t)*SizeThetaBin;
					// r_j corresponding to that theta_j:
					double r_j = rho*cosTheta_Rz_array[j_t] + z*sinTheta_Rz_array[j_t];
					// this value of r_j falls into the following bin in the r array:
					int j_r = (int) Math.floor( N_r*(r_j - RMin)/(float) (RMax - RMin));

					// match bins:
					if(j_r == binrMaxR_Z[p] && j_t == bintMaxR_Z[p]) {
						crossList.add(crosses.get(i));  // add this hit
						
					}
				}
			}
			int[] theRegionsCount = new int[8];
			boolean passList = true;
			
			for(Cross thecross : crossList) {
				
				theRegionsCount[thecross.getCosmicsRegion()-1]++;
				
				if(crossList.size()==3 && theRegionsCount[thecross.getCosmicsRegion()-1]>1)
					passList = false;
				if(crossList.size()>3 && theRegionsCount[thecross.getCosmicsRegion()-1]>2)
					passList = false;
			}
			if(passList) {
				if(!crossLists.contains(crossList)) {					
					crossLists.add(index, crossList);
					
					index++;
				}				
			}
		}
		
		//remove duplicate lists
		for(int i = crossLists.size()-1; i>-1; i--) {
			for(int j = crossLists.size()-1; j>-1; j--) {
				if(i==j)
					continue;
				if(crossLists.get(i)==null || crossLists.get(j)==null)
					continue;
				if(crossLists.get(i).size()<crossLists.get(j).size()) 
					continue;
				if(crossLists.get(i).size()>0 && crossLists.get(j).size()>0) {
					this.Contains(crossLists.get(i), crossLists.get(j));
					
				}
			}
		}
		
		ArrayList<ArrayList <Cross> > newcrossLists = new ArrayList<ArrayList <Cross> >();
		int newListIndex =0;
		for(int i = 0; i<crossLists.size(); i++) {
			if(crossLists.get(i).size()>0) {
				newcrossLists.add(newListIndex, crossLists.get(i));
				
				newListIndex++;
			}
		}
		
		CrossList crossListFinal = new CrossList();
		crossListFinal.addAll(newcrossLists);
		
        return crossListFinal;
	
	}

}
