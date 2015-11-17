package org.jlab.rec.bmt.cross;

import java.util.ArrayList;
import java.util.List;

import org.jlab.geom.prim.Point3D;

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
	 * @return the list of crosses determined to be consistent with belonging to a track in the bmt
	 */
	public CrossList findCandidateCrossLists(List<Cross> crosses) {
		// 
		ArrayList<ArrayList <Cross> > trkCnds = new ArrayList<ArrayList <Cross> >();
		  
		if(crosses.size()>0) {
			
			ArrayList<Cross> bmtcrosslistFirstRg = new ArrayList<Cross>();
		    ArrayList<Cross> bmtcrosslistSecondRg = new ArrayList<Cross>();
		    ArrayList<Cross> bmtcrosslistThirdRg = new ArrayList<Cross>();
		    
			ArrayList<Cross> bmtcrosslistRg1 = new ArrayList<Cross>();
		    ArrayList<Cross> bmtcrosslistRg2 = new ArrayList<Cross>();
		    ArrayList<Cross> bmtcrosslistRg3 = new ArrayList<Cross>();
		  
		    int NonEmptyReg = 0;
		    for(Cross bmt : crosses) {
						 
		    	if(bmt.get_Region()==1)
		    		bmtcrosslistRg1.add(bmt);
		    	if(bmt.get_Region()==2)
		    		bmtcrosslistRg2.add(bmt);
		    	if(bmt.get_Region()==3)
		    		bmtcrosslistRg3.add(bmt);
		    	
		    }
		    
		    if(bmtcrosslistRg1.size()>0) {
		    	NonEmptyReg++;
		    	bmtcrosslistFirstRg = bmtcrosslistRg1;
		    }
		    if(bmtcrosslistRg2.size()>0) {
		    	NonEmptyReg++;
		    	if(NonEmptyReg==2)
		    		bmtcrosslistSecondRg = bmtcrosslistRg2;
		    	if(NonEmptyReg==1)
		    		bmtcrosslistFirstRg = bmtcrosslistRg2;
		    }
		    if(bmtcrosslistRg3.size()>0) {
		    	NonEmptyReg++;
		    	if(NonEmptyReg==3)
		    		bmtcrosslistThirdRg = bmtcrosslistRg3;
		    	if(NonEmptyReg==2)
		    		bmtcrosslistSecondRg = bmtcrosslistRg3;
		    	if(NonEmptyReg==1)
		    		bmtcrosslistFirstRg = bmtcrosslistRg3;
		    }
		    
		    
		    int index =0;
		 // need 3 crosses
		    if(NonEmptyReg>=3) {
		    	for(Cross c1 : bmtcrosslistFirstRg) { 
					for(Cross c2 : bmtcrosslistSecondRg) { 
						for(Cross c3 : bmtcrosslistThirdRg) {  
							
					        // selection for first 3 hits:	
							//---------------------------	
							 double phi12 = Math.abs(get_PointInfo(c1,c2,null)[2]);
								if(phi12>org.jlab.rec.bst.Constants.phi12cut)
									continue;		
							 double phi13 = Math.abs(get_PointInfo(c1,c3,null)[2]);  
							 if(phi13>org.jlab.rec.bst.Constants.phi13cut)
								 continue; 	
							 				 
							 double drdz1 = get_PointInfo(c1,null,null)[1]/get_PointInfo(c1,null,null)[0];
							 double drdz2 = get_PointInfo(c2,null,null)[1]/get_PointInfo(c2,null,null)[0]; 
							 double drdz3 = get_PointInfo(c3,null,null)[1]/get_PointInfo(c3,null,null)[0];
								 
							 double drdzsum = drdz1 + drdz2 + drdz3;
							 
							 if( Math.abs((drdz1-drdzsum/3.)/(drdzsum/3.)) >org.jlab.rec.bst.Constants.drdzcut)
								 continue; 
							 if( Math.abs((drdz2-drdzsum/3.)/(drdzsum/3.)) >org.jlab.rec.bst.Constants.drdzcut)
								 continue; 
							 if( Math.abs((drdz3-drdzsum/3.)/(drdzsum/3.)) >org.jlab.rec.bst.Constants.drdzcut)
								 continue; 
							
								 	
						 	ArrayList<Cross> ct = new ArrayList<Cross>(3);
						 	c1.set_AssociatedTrackID(index);
							c1.set_AssociatedElementsIDs();
							c2.set_AssociatedTrackID(index);
							c2.set_AssociatedElementsIDs();
							c3.set_AssociatedTrackID(index);
							c3.set_AssociatedElementsIDs();
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
		    }
		}
		CrossList crossList = new CrossList();
		crossList.addAll(trkCnds);
		return crossList;
	}


	private double[] get_PointInfo(Cross bt, Cross bt2, Cross bt3) {
		
		double[] arrayInfo = new double[4];
		if(bt == null)
			return null;
		
		Point3D btp1 = null;
		Point3D btp2 = null;
		Point3D btp3 = null;
		

		if(bt!=null)
			btp1=bt.get_Point();
		if(bt2!=null)
			btp2=bt2.get_Point();
		if(bt3!=null)
			btp3=bt3.get_Point();
	
		
		arrayInfo[0] = btp1.z();
		arrayInfo[1] = btp1.toVector3D().rho();
		double cos_ZDiff = 180;
		if(bt2!=null) {
		cos_ZDiff = btp1.toVector3D().dot(btp2.toVector3D())/(btp1.toVector3D().mag()*btp2.toVector3D().mag());
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
 		     


}
