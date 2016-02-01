package org.jlab.rec.dc.segment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jlab.rec.dc.Constants;
import org.jlab.rec.dc.GeometryLoader;
import org.jlab.rec.dc.cluster.FittedCluster;
import org.jlab.rec.dc.hit.FittedHit;

/**
 * A Segment is a fitted cluster that has been pruned of hits with bad residuals (see Constants)
 * @author ziegler
 *
 */
public class SegmentFinder {

	/**
	 * @param allClusters  the list of fitted clusters
	 * @return the list of segments obtained from the clusters
	 */
	public List<Segment> get_Segments(List<FittedCluster> allClusters, String trkg)
	{
		List<Segment> segList = new ArrayList<Segment>();
		for(FittedCluster fClus : allClusters) {
			
			if(fClus.size()>Constants.MAXCLUSSIZE)
				continue;
			if(fClus.get_TrkgStatus()==-1) {
				System.err.print("Error -- the clusters must be fit prior to making segments");
				return segList;
			}
			
			Segment seg = new Segment(fClus);
			seg.set_fitPlane();	
			
			
			if(trkg.equals("HitBased")) {
				// pass all for now.  Deal with refitting later	
				
				FittedCluster fClus0 = seg.get_fittedCluster();
				
				fClus0.clusterFitter("HitBased", false);
				Segment newSeg = new Segment(fClus0);
				
				newSeg.set_fittedCluster(fClus0);
				newSeg.set_fitPlane();
				
				
				segList.add(newSeg);
			}
			
			if(trkg == "TimeBased") {
				
				//int index=0;
				for(int i =0; i<seg.size(); i++) {
					FittedHit fittedHit =  seg.get_fittedCluster().get(i);
					
					if(Math.abs(fittedHit.get_TimeResidual())>Constants.PASSINGHITRESIDUAL){ 
									
						seg.remove(i);
						//System.out.println("In Segment finder -- removed hit "+fittedHit.printInfo()+" from segment "+i);
						//index++;
					} 
				}
				Segment rSeg = reFit(seg);
				
				for(FittedHit h : rSeg)
					if(h.get_LeftRightAmb()==0)
						System.out.println(" error in setting lr");
				segList.add(rSeg);
			}
		}
		
		for(Segment sgt : segList)
			for(FittedHit fhit : sgt) {
				//double calc_doca = (fhit.get_X()-sgt.get_fittedCluster().get_clusterLineFitSlope()*fhit.get_Z()-sgt.get_fittedCluster().get_clusterLineFitIntercept());
				double x = GeometryLoader.dcDetector.getSector(0).getSuperlayer(fhit.get_Superlayer()-1).getLayer(fhit.get_Layer()-1).getComponent(fhit.get_Wire()-1).getMidpoint().x();
				double z = GeometryLoader.dcDetector.getSector(0).getSuperlayer(fhit.get_Superlayer()-1).getLayer(fhit.get_Layer()-1).getComponent(fhit.get_Wire()-1).getMidpoint().z();
				
				double calc_doca = (x-sgt.get_fittedCluster().get_clusterLineFitSlope()*z-sgt.get_fittedCluster().get_clusterLineFitIntercept());
				
				fhit.set_ClusFitDoca(calc_doca);
			}
		
//		this.setAssociatedID(segList);
		return segList;
		
	}
	
	

	private void outOfTimersRemover(FittedCluster fClus) {
		// remove out of time hits
		for(int i = 0; i<fClus.size(); i++) {			
			if(fClus.get(i).get_OutOfTimeFlag()==true) {
				//fClus.remove(i);
				if(Constants.DEBUGPRINTMODE == true)
					System.out.println(" out of timer ? "+fClus.get(i).printInfo());
			}
		}
	}
	
	public Segment reFit(Segment seg) {
		FittedCluster fClus = seg.get_fittedCluster();
		
		this.outOfTimersRemover(fClus);
		fClus.clusterFitter("TimeBased",false);
		// determine the size of the array of clusters to be refit with multiple candidates
		int index=0;
		for(FittedHit hit : fClus) {
			if(Math.abs(hit.get_TimeResidual())>hit.get_TimeToDistance()/2) {
				
				hit.set_LeftRightAmb(0);				
			}
			if(hit.get_LeftRightAmb()==0)
				index++;
			
		}
		
		if(index> 0) {
			double cosTrkAngle = Math.cos(Math.atan(fClus.get_clusterLineFitSlope()));
			
			int arraySize = (int)Math.pow(2, (double)index) ; 
			ArrayList<FittedCluster> arrayOfClus = new ArrayList<FittedCluster>(arraySize);
		
		
			//pass all acceptable clusters
			FittedCluster okClus = new FittedCluster(fClus.getBaseCluster());
			for(FittedHit hit : fClus) {
				if(hit.get_LeftRightAmb()!=0)
					okClus.add(hit);
			}
			//filter all other clusters
			FittedCluster notLRClus = new FittedCluster(fClus.getBaseCluster());
			for(FittedHit hit : fClus) {
				if(hit.get_LeftRightAmb()==0)
					notLRClus.add(hit);
			}
			//make combinatorials
			
			FittedCluster totNotLRClus = new FittedCluster(fClus.getBaseCluster());
			FittedCluster posNotLRClus = new FittedCluster(fClus.getBaseCluster());
			FittedCluster negNotLRClus = new FittedCluster(fClus.getBaseCluster());
			
			
			for(FittedHit hit : notLRClus) {

				FittedHit newhitPos = new FittedHit(hit.get_Sector(), hit.get_Superlayer(), hit.get_Layer(), hit.get_Wire(),
						hit.get_Time(), hit.get_TimeErr(), hit.get_Id()) ;
				
				newhitPos.set_Id(hit.get_Id());
				newhitPos.set_TrkgStatus(0);
				
				newhitPos.set_LeftRightAmb(1);
				newhitPos.updateHitPositionWithTime(cosTrkAngle);
				
				newhitPos.set_AssociatedClusterID(hit.get_AssociatedClusterID());
				
				FittedHit newhitNeg = new FittedHit(hit.get_Sector(), hit.get_Superlayer(), hit.get_Layer(), hit.get_Wire(),
						hit.get_Time(), hit.get_TimeErr(), hit.get_Id()) ;
				newhitNeg.set_Id(hit.get_Id());
				newhitNeg.set_TrkgStatus(0);
				
				newhitNeg.set_LeftRightAmb(-1);
				newhitNeg.updateHitPositionWithTime(cosTrkAngle);
				
				newhitNeg.set_AssociatedClusterID(hit.get_AssociatedClusterID());
				
				totNotLRClus.add(newhitNeg);
				totNotLRClus.add(newhitPos);
				
				posNotLRClus.add(newhitPos);
				negNotLRClus.add(newhitNeg);
			}
			
			Collections.sort(totNotLRClus);
			
			if(index==1) {
				arrayOfClus.add(posNotLRClus);
				arrayOfClus.add(negNotLRClus);
			}
			if(index==2) {
				for(int i1 = 0; i1<totNotLRClus.size(); i1++) {					
					for(int i2 = 2; i2<totNotLRClus.size(); i2++) {
						if(totNotLRClus.get(i1).get_Id()==totNotLRClus.get(i2).get_Id())
							continue;
						FittedCluster newClus = new FittedCluster(fClus.getBaseCluster());
						newClus.add(totNotLRClus.get(i1));
						newClus.add(totNotLRClus.get(i2));
						arrayOfClus.add(newClus);
					}
				}
			}
			
			if(index==3) {
				for(int i1 = 0; i1<totNotLRClus.size(); i1++) {					
					for(int i2 = 2; i2<totNotLRClus.size(); i2++) {
						for(int i3 = 4; i3<totNotLRClus.size(); i3++) {
							if( (totNotLRClus.get(i1).get_Id()==totNotLRClus.get(i2).get_Id()) 
									|| (totNotLRClus.get(i1).get_Id()==totNotLRClus.get(i3).get_Id()) 
									|| (totNotLRClus.get(i2).get_Id()==totNotLRClus.get(i3).get_Id()) )
								continue;
							FittedCluster newClus = new FittedCluster(fClus.getBaseCluster());
							newClus.add(totNotLRClus.get(i1));
							newClus.add(totNotLRClus.get(i2));
							newClus.add(totNotLRClus.get(i3));
							arrayOfClus.add(newClus);
						}
					}
				}
			}
			
			if(index==4) {
				for(int i1 = 0; i1<totNotLRClus.size(); i1++) {					
					for(int i2 = 2; i2<totNotLRClus.size(); i2++) {
						for(int i3 = 4; i3<totNotLRClus.size(); i3++) {
							for(int i4 = 6; i4<totNotLRClus.size(); i4++) {
								if( 	(totNotLRClus.get(i1).get_Id()==totNotLRClus.get(i2).get_Id()) 
									 || (totNotLRClus.get(i1).get_Id()==totNotLRClus.get(i3).get_Id()) 
									 || (totNotLRClus.get(i1).get_Id()==totNotLRClus.get(i4).get_Id()) 
									 ||	(totNotLRClus.get(i2).get_Id()==totNotLRClus.get(i3).get_Id()) 
									 || (totNotLRClus.get(i2).get_Id()==totNotLRClus.get(i4).get_Id()) 
									 || (totNotLRClus.get(i3).get_Id()==totNotLRClus.get(i4).get_Id()) 
										)
									continue;
								FittedCluster newClus = new FittedCluster(fClus.getBaseCluster());
								newClus.add(totNotLRClus.get(i1));
								newClus.add(totNotLRClus.get(i2));
								newClus.add(totNotLRClus.get(i3));
								newClus.add(totNotLRClus.get(i4));
								arrayOfClus.add(newClus);
							}
						}
					}
				}
			}
			
			if(index==5) {
				for(int i1 = 0; i1<totNotLRClus.size(); i1++) {					
					for(int i2 = 2; i2<totNotLRClus.size(); i2++) {
						for(int i3 = 4; i3<totNotLRClus.size(); i3++) {
							for(int i4 = 6; i4<totNotLRClus.size(); i4++) {
								for(int i5 = 8; i5<totNotLRClus.size(); i5++) {
									if( 	(totNotLRClus.get(i1).get_Id()==totNotLRClus.get(i2).get_Id()) 
										 || (totNotLRClus.get(i1).get_Id()==totNotLRClus.get(i3).get_Id()) 
										 || (totNotLRClus.get(i1).get_Id()==totNotLRClus.get(i4).get_Id())
										 || (totNotLRClus.get(i1).get_Id()==totNotLRClus.get(i5).get_Id())
										 ||	(totNotLRClus.get(i2).get_Id()==totNotLRClus.get(i3).get_Id()) 
										 || (totNotLRClus.get(i2).get_Id()==totNotLRClus.get(i4).get_Id()) 
										 || (totNotLRClus.get(i2).get_Id()==totNotLRClus.get(i5).get_Id()) 
										 || (totNotLRClus.get(i3).get_Id()==totNotLRClus.get(i4).get_Id()) 										 
										 || (totNotLRClus.get(i3).get_Id()==totNotLRClus.get(i5).get_Id()) 
										 || (totNotLRClus.get(i4).get_Id()==totNotLRClus.get(i5).get_Id()) 
											)
										continue;
									FittedCluster newClus = new FittedCluster(fClus.getBaseCluster());
									newClus.add(totNotLRClus.get(i1));
									newClus.add(totNotLRClus.get(i2));
									newClus.add(totNotLRClus.get(i3));
									newClus.add(totNotLRClus.get(i4));
									newClus.add(totNotLRClus.get(i5));
									arrayOfClus.add(newClus);
								}
							}
						}
					}
				}
			}
			
			if(index==6) {
				for(int i1 = 0; i1<totNotLRClus.size(); i1++) {					
					for(int i2 = 2; i2<totNotLRClus.size(); i2++) {
						for(int i3 = 4; i3<totNotLRClus.size(); i3++) {
							for(int i4 = 6; i4<totNotLRClus.size(); i4++) {
								for(int i5 = 8; i5<totNotLRClus.size(); i5++) {
									for(int i6 = 10; i6<totNotLRClus.size(); i6++) {
										if( 	(totNotLRClus.get(i1).get_Id()==totNotLRClus.get(i2).get_Id()) 
											 || (totNotLRClus.get(i1).get_Id()==totNotLRClus.get(i3).get_Id()) 
											 || (totNotLRClus.get(i1).get_Id()==totNotLRClus.get(i4).get_Id())
											 || (totNotLRClus.get(i1).get_Id()==totNotLRClus.get(i5).get_Id())
											 || (totNotLRClus.get(i1).get_Id()==totNotLRClus.get(i6).get_Id())
											 ||	(totNotLRClus.get(i2).get_Id()==totNotLRClus.get(i3).get_Id()) 
											 || (totNotLRClus.get(i2).get_Id()==totNotLRClus.get(i4).get_Id()) 
											 || (totNotLRClus.get(i2).get_Id()==totNotLRClus.get(i5).get_Id()) 
											 || (totNotLRClus.get(i2).get_Id()==totNotLRClus.get(i6).get_Id()) 
											 || (totNotLRClus.get(i3).get_Id()==totNotLRClus.get(i4).get_Id()) 										 
											 || (totNotLRClus.get(i3).get_Id()==totNotLRClus.get(i5).get_Id()) 
											 || (totNotLRClus.get(i3).get_Id()==totNotLRClus.get(i6).get_Id()) 
											 || (totNotLRClus.get(i4).get_Id()==totNotLRClus.get(i5).get_Id())
											 || (totNotLRClus.get(i4).get_Id()==totNotLRClus.get(i6).get_Id())
											 || (totNotLRClus.get(i5).get_Id()==totNotLRClus.get(i6).get_Id())
												)
											continue;
										FittedCluster newClus = new FittedCluster(fClus.getBaseCluster());
										newClus.add(totNotLRClus.get(i1));
										newClus.add(totNotLRClus.get(i2));
										newClus.add(totNotLRClus.get(i3));
										newClus.add(totNotLRClus.get(i4));
										newClus.add(totNotLRClus.get(i5));
										arrayOfClus.add(newClus);
									}
								}
							}
						}
					}
				}
			}
			
			FittedCluster bestClus = new FittedCluster(fClus.getBaseCluster());
			double bestChi2 = Double.POSITIVE_INFINITY;
			
			for(int j =0; j<arrayOfClus.size(); j++) {
				arrayOfClus.get(j).addAll(okClus);
				
				arrayOfClus.get(j).clusterFitter("TimeBased",true);
				
				if(arrayOfClus.get(j).get_Chisq()<bestChi2) {
					bestChi2 = arrayOfClus.get(j).get_Chisq();
					bestClus = arrayOfClus.get(j);
					
				}
				
					
			}
			
			
			fClus = bestClus;
			//put this in a method -- todo
			cosTrkAngle = Math.cos(Math.atan(fClus.get_clusterLineFitSlope()));
			for(FittedHit h : fClus) {
				h.updateHitPositionWithTime(cosTrkAngle);
			}
		}
		
		fClus.clusterFitter("TimeBased",true);
		Segment newSeg = new Segment(fClus);
		newSeg.set_fittedCluster(fClus);
		newSeg.set_fitPlane();
		for(FittedHit h : newSeg) {
			
			if(h.get_LeftRightAmb()==0)
				System.err.println(" error left-right assigned not done for "+h.printInfo());
		}
		return newSeg;
	}
	

	

}
