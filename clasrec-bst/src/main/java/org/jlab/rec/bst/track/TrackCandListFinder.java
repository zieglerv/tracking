package org.jlab.rec.bst.track;

import java.util.ArrayList;
import java.util.List;

import trackfitter.fitter.HelicalTrackFitter;

import org.jMath.Vector.threeVec;
import org.jlab.rec.bst.Constants;
import org.jlab.rec.bst.Geometry;
import org.jlab.rec.bst.cross.Cross;
import org.jlab.rec.bst.cross.CrossList;

import org.jlab.rec.bst.trajectory.Trajectory;
import org.jlab.rec.bst.trajectory.TrajectoryFinder;

/**
 * A class with a method implementing an algorithm that finds lists of track candidates in the BST
 * @author ziegler
 *
 */

public class TrackCandListFinder {
	
	public TrackCandListFinder() {					
	}

	/**
	 * 
	 * @param crossList the input list of crosses
	 * @return an array list of track candidates in the DC
	 */
	public ArrayList<Track> getTrackCands(CrossList crossList, Geometry geo) {
		
		ArrayList<Track> cands = new ArrayList<Track>();
		
		if(crossList.size()==0) {
			System.err.print("Error in estimating track candidate trajectory: less than 3 crosses found");
			return cands;
		}
		
		HelicalTrackFitter fitTrk = new HelicalTrackFitter();
		
		int shift =0;
		if(Constants.trk_comesfrmOrig)
			shift =1;

		int Max_Number_Of_Iterations = Constants.BSTTRKINGNUMBERITERATIONS;
		
		for(int i = 0; i<crossList.size(); i++) {
			int Number_Of_Iterations = 0;
			
			ArrayList<Cross> crossesInTrk = crossList.get(i);
						
			if(crossesInTrk.size()>=3) { //should always be the case 
				
				while(Number_Of_Iterations<=Max_Number_Of_Iterations) {
					Number_Of_Iterations++;
					fitTrk = new HelicalTrackFitter();
					
					Track cand = new Track();
					cand.addAll(crossesInTrk);
					double[] X = new double[crossesInTrk.size()+shift];
					double[] Y = new double[crossesInTrk.size()+shift];
					double[] Z = new double[crossesInTrk.size()+shift];
					double[] ErrRho = new double[crossesInTrk.size()+shift];
					double[] ErrZ = new double[crossesInTrk.size()+shift];
					
					if(shift ==1) {
						X[0] = 0;
						Y[0] = 0;
						Z[0] = 0;
						ErrRho[0] = Constants.RHOVTXCONSTRAINT;
						ErrZ[0] = Constants.ZVTXCONSTRAINT;
						
					}
					
					for(int j= shift; j<shift+crossesInTrk.size(); j++) {
						
						X[j] = crossesInTrk.get(j-shift).get_Point().x();
						Y[j] = crossesInTrk.get(j-shift).get_Point().y();
						Z[j] = crossesInTrk.get(j-shift).get_Point().z();
						ErrRho[j] = crossesInTrk.get(j-shift).get_PointErr().rt();
						ErrZ[j] = crossesInTrk.get(j-shift).get_PointErr().z();		
						
						}
					
					if(Constants.ignoreErr==true) 
						for(int j= 0; j<shift+crossesInTrk.size(); j++) {	
							
							ErrRho[j] = 1;
							ErrZ[j] = 1;										
						}
					
					fitTrk.fit(X, Y, Z, ErrRho, ErrZ);
					
					if(fitTrk.get_helix()==null) {
						//System.err.println("Error in Helical Track fitting -- helix not found -- trying to refit using the uncorrected crosses...");
						for(int j= shift; j<shift+crossesInTrk.size(); j++) {
							X[j] = crossesInTrk.get(j-shift).get_Point0().x();
							Y[j] = crossesInTrk.get(j-shift).get_Point0().y();
							Z[j] = crossesInTrk.get(j-shift).get_Point0().z();
							ErrRho[j] = crossesInTrk.get(j-shift).get_PointErr0().rt();
							ErrZ[j] = crossesInTrk.get(j-shift).get_PointErr0().z();										
						}
						fitTrk.fit(X, Y, Z, ErrRho, ErrZ);
						Number_Of_Iterations=Max_Number_Of_Iterations+1;
						//if(fitTrk.get_helix()==null) 
							//System.err.println("Error in Helical Track fitting -- helix not found -- refit FAILED");
					}
					if(fitTrk.get_helix()!=null && fitTrk.getFit()!=null) {	
						
						cand.set_circleFitChi2Prob(fitTrk.get_chisq()[0]);
						cand.set_lineFitChi2Prob(fitTrk.get_chisq()[1]);
														
						cand.set_HelicalTrack(fitTrk.get_helix());
						
						cand.update_Crosses(geo);
						
						if(Number_Of_Iterations==Max_Number_Of_Iterations) {
							
							cands.add(cand); // dump the cand							
						} 
					}
					
				}
			}
		}
		
		ArrayList<Track> passedcands = new ArrayList<Track>();
		if(Constants.removeClones ==false)
			passedcands = cands;
		
		if(Constants.removeClones ==true)
			if(cands.size()>0) {
				// clean up duplicates:
				for(int k = 0; k< cands.size(); k++) {
					for(int k2 = 0; k2< cands.size(); k2++) {
						if(k2==k)
							continue;
						int overlaps =0;
						for(int k3 =0; k3<cands.get(k).size(); k3++) {
							if(cands.get(k2).containsCross(cands.get(k).get(k3))) {
								overlaps++;
								
							}
						}
						if(overlaps>1) {
							if( (cands.get(k2).get_circleFitChi2Prob()+cands.get(k2).get_lineFitChi2Prob()) > (cands.get(k).get_circleFitChi2Prob()+cands.get(k).get_lineFitChi2Prob())) {
								
								cands.get(k2).set_Id(-999);
								
							}
						}
					}
				}
				for(int k = 0; k< cands.size(); k++) {
					if(cands.get(k).get_Id()!=-999) {
						passedcands.add(cands.get(k));
						
					}
				}
				
			}
		
		for(int ic = 0; ic< passedcands.size(); ic++) {
			
			TrajectoryFinder trjFind = new TrajectoryFinder(passedcands.get(ic).get_Helix());
			
			Trajectory traj = trjFind.findTrajectory(passedcands.get(ic),geo);				
				
			passedcands.get(ic).set_Trajectory(traj.get_Trajectory());												
			
			passedcands.get(ic).set_Id(ic);
		
		}
		
		return passedcands;
	}



	public List<CosmicTrack> getCosmicsTracks(CrossList crossList, Geometry geo, int excludeRegion) {
		ArrayList<CosmicTrack> cands = new ArrayList<CosmicTrack>();
		
		int index =-1;  
		
		if(crossList.size()==0) {
			System.err.print("Error in estimating track candidate trajectory: less than mininum required number of crosses found");
			return cands;
		}
		
		for(int j = 0; j<crossList.size(); j++) {
			ArrayList<Cross> cosmicsCrosses = crossList.get(j);
			ArrayList<Cross> excludedCrosses = new ArrayList<Cross>();
			
			CosmicTrack ctrk = new CosmicTrack();
			// remove the crosses from the exluded region to fit the track
			for(Cross crossInTrackToFit : cosmicsCrosses) {
				if(crossInTrackToFit.get_Region()==excludeRegion) {
					excludedCrosses.add(crossInTrackToFit);
				}
			}
			
			removeCrosses(cosmicsCrosses, excludedCrosses, geo);

			ctrk.addAll(cosmicsCrosses);
			ctrk.fitCosmicTrack(geo);
			
            ArrayList<Cross> cosmicsCrossesToRemove = new ArrayList<Cross>();
            //System.out.println(" track siz "+ctrk.size());
            for(int i = 0; i<ctrk.get_xzresiduals().size(); i++) {
            	// throw out the outliers
                if(Math.abs(ctrk.get_xzresiduals().get(i)[0])>Constants.COSMICSMINRESIDUAL ) 
                        cosmicsCrossesToRemove.add(cosmicsCrosses.get(i));   
            }
           
            removeCrosses(cosmicsCrosses, cosmicsCrossesToRemove, geo);
            ctrk = new CosmicTrack();
			ctrk.addAll(cosmicsCrosses);
			
            if(cosmicsCrosses.size()<3)
                    continue;
            
            cosmicsCrossesToRemove = new ArrayList<Cross>();
            //refit
            ctrk.fitCosmicTrack(geo);
            
            // update crosses with trakdir            
            for(int i = 0; i<ctrk.size(); i++) {
            	ctrk.update_Crosses(ctrk.get_yxslope(),ctrk.get_yzslope(), geo) ;
            	
            }	
           //refit
            ctrk.fitCosmicTrack(geo);
            
            for(int i = 0; i<ctrk.get_xzresiduals().size(); i++) {
            	// throw out the outliers
            	//System.out.println("z resolution for " +cosmicsCrosses.get(i).printInfo() +" = "+ ctrk.get_xzresiduals().get(i)[1]);
                    if(Math.abs(ctrk.get_xzresiduals().get(i)[0])>Constants.COSMICSMINRESIDUAL || 
                    		Math.abs(ctrk.get_xzresiduals().get(i)[1])>Constants.COSMICSMINRESIDUALZ) 
                            cosmicsCrossesToRemove.add(cosmicsCrosses.get(i));
            
            }
            
            removeCrosses(cosmicsCrosses, cosmicsCrossesToRemove, geo);
            ctrk = new CosmicTrack();
			ctrk.addAll(cosmicsCrosses);
			
            if(cosmicsCrosses.size()<3)
                    continue;
           
           //refit
            ctrk.fitCosmicTrack(geo);
            
            // update crosses with trakdir            
            for(int i = 0; i<ctrk.size(); i++) {
            	ctrk.update_Crosses(ctrk.get_yxslope(),ctrk.get_yzslope(), geo) ;
            }
          //refit
            ctrk.fitCosmicTrack(geo);
            ctrk.refitCosmicTrack(geo);
            index++;
            ctrk.setIdx(index);
            
        
           // now make the list
          if(!(cands.containsAll(ctrk))) {                                                                                      
        	  cands.add(index, ctrk);       
        	  
          }
		}
		
		return cands;
	}

	private void removeCrosses(ArrayList<Cross> cosmicsCrosses,
			ArrayList<Cross> cosmicsCrossesToRemove, Geometry geo) {
		for(int i = 0; i<cosmicsCrosses.size(); i++) {
			for(int j = 0; j<cosmicsCrossesToRemove.size(); j++) {
				if(cosmicsCrosses.get(i).get_Id()==cosmicsCrossesToRemove.get(j).get_Id()) {
					cosmicsCrosses.remove(i);
					resetUnusedCross(cosmicsCrossesToRemove.get(j), geo);
					
				}
			}
		}
		for(int i = 0; i<cosmicsCrosses.size(); i++) {
			if(cosmicsCrosses.size()<3)
				resetUnusedCross(cosmicsCrosses.get(i), geo);
		}
	}

	private void resetUnusedCross(Cross cross, Geometry geo) {
		
		cross.set_Dir(new threeVec(0,0,0));
		
		cross.set_CrossParams(null, geo);
	}

		
}
