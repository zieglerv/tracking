package org.jlab.rec.dc.track;

import java.util.ArrayList;
import java.util.List;

import org.jlab.geom.prim.Point3D;
import org.jlab.rec.dc.Constants;
import org.jlab.rec.dc.cross.Cross;
import org.jlab.rec.dc.cross.CrossList;
import org.jlab.rec.dc.trajectory.DCSwimmer;
import org.jlab.rec.dc.trajectory.StateVec;
import org.jlab.rec.dc.trajectory.Trajectory;
import org.jlab.rec.dc.trajectory.TrajectoryFinder;

/**
 * A class with a method implementing an algorithm that finds lists of track candidates in the DC
 * @author ziegler
 *
 */

public class TrackCandListFinder {

	/**
	 * the tracking status = HitBased or TimeBased
	 */
	private String trking;
	
	public TrackCandListFinder(String stat) {
		trking = stat;
	}
	public DCSwimmer dcSwim = new DCSwimmer();
	/**
	 * 
	 * @param crossList the input list of crosses
	 * @return a list of track candidates in the DC
	 */
	public List<Track> getTrackCands(CrossList crossList) {
		List<Track> cands = new ArrayList<Track>();
		if(crossList.size()==0) {
			System.err.print("Error no tracks found");
			return cands;
		}
		if(Constants.DEBUGPRINTMODE)
			System.out.println(" looking for tracks ....");
		
		for(int i = 0; i<crossList.size(); i++) {
			Track cand = new Track();
			List<Cross> crossesInTrk = crossList.get(i);
			TrajectoryFinder trjFind = new TrajectoryFinder();
			
			Trajectory traj = trjFind.findTrajectory(crossesInTrk);
            if(traj == null) 
            	continue;
            
			if(crossesInTrk.size()==3) {
							
				cand.addAll(crossesInTrk);
				
				cand.set_Sector(crossesInTrk.get(0).get_Sector());
				
				cand.set_Region3CrossPoint();
				cand.set_Region3CrossDir();
				
				cand.set_Trajectory(traj.get_Trajectory());
				cand.set_IntegralBdl(traj.get_IntegralBdl());
				

				if(cand.size()==3) {
					double theta3 = Math.atan(cand.get(2).get_Segment2().get_fittedCluster().get_clusterLineFitSlope());
			        double theta1 = Math.atan(cand.get(0).get_Segment2().get_fittedCluster().get_clusterLineFitSlope());
			        
			        double deltaTheta = theta3-theta1; 
			       
			        double iBdl = traj.get_IntegralBdl(); 
			        
			        double pxz = Math.abs(Constants.LIGHTVEL*iBdl/deltaTheta);
			        double thX = (cand.get(0).get_Dir().x()/cand.get(0).get_Dir().z());
			        double thY = (cand.get(0).get_Dir().y()/cand.get(0).get_Dir().z());
			        double py = Math.sqrt( (thX*thX+thY*thY+1)/(thX*thX+1) - 1 )*pxz;
			          
			        //positive charges bend outward for nominal GEMC field configuration
					int q = (int) Math.signum(deltaTheta); 
					
					if(Constants.TORSCALE == 1) {
						q*=-1;						
					}
					if(iBdl == 0 || (deltaTheta== 0)) {
						System.err.print("Error in estimating track candidate trajectory: integral_B_dl not found, no trajectory...");
					}

					if(iBdl != 0 || (deltaTheta != 0)) {
						
						double p = Math.sqrt(pxz*pxz+py*py);
						
						if(p>Constants.MAXTRKMOM || p< Constants.MINTRKMOM)
							continue;
						
						int totNbOfIterations = 30;
						int iterationNb = 0;
						int printiterationNb = 0;
						cand.set_Q(q);
						// momentum correction using the swam trajectory iBdl
						cand.set_P(p);
						
						double fitChisq = Double.POSITIVE_INFINITY ;
						
						StateVec VecAtReg3MiddlePlane = new StateVec(cand.get(2).get_Point().x(),cand.get(2).get_Point().y(),
								cand.get(2).get_Dir().x()/cand.get(2).get_Dir().z(), cand.get(2).get_Dir().y()/cand.get(2).get_Dir().z());
						
						StateVec VecAtReg1MiddlePlane = new StateVec(cand.get(0).get_Point().x(),cand.get(0).get_Point().y(),
								cand.get(0).get_Dir().x()/cand.get(0).get_Dir().z(), cand.get(0).get_Dir().y()/cand.get(0).get_Dir().z());
												
						cand.set_StateVecAtReg1MiddlePlane(VecAtReg1MiddlePlane); 	
						
						if(trking == "TimeBased" && Constants.useKalmanFilter) {
							
							while(iterationNb < totNbOfIterations) {
								
								KalFit kf = new KalFit(cand, "wires");
								if(kf.KalFitFail==true) {
									break;
								}
								
								kf.runKalFit(); 
																
								if(kf.chi2>fitChisq || kf.chi2>Constants.MAXCHI2+1 || Math.abs(kf.chi2-fitChisq)<0.0000001) {
									iterationNb = totNbOfIterations;
									continue;
								}
								if(!Double.isNaN(kf.KF_p) && kf.KF_p>Constants.MINTRKMOM) {
									cand.set_P(kf.KF_p);								
									cand.set_Q(kf.KF_q);
									cand.set_CovMat(kf.covMat);
									
									VecAtReg3MiddlePlane = new StateVec(kf.stateVec[0],kf.stateVec[1],kf.stateVec[2],kf.stateVec[3]);
									
									
								}
								fitChisq = kf.chi2;
								iterationNb++;
								printiterationNb++;
								cand.set_FitChi2(fitChisq); 
							}
								
						}	
						this.setTrackPars(cand, traj, trjFind, VecAtReg3MiddlePlane, cand.get(2).get_Point().z());
						
						if(cand.fit_Successful==false)
							continue;
						
						if((iterationNb>0 && cand.get_FitChi2()>Constants.MAXCHI2) || 
								(iterationNb!=0 && cand.get_FitChi2()==0))
							continue; // fails if after KF chisq exceeds cutoff or if KF fails 
						if(Constants.DEBUGPRINTMODE)
							System.out.println(" fit chis "+cand.get_FitChi2() + " at iteration "+printiterationNb);
						cand.set_Id(cands.size());
						
							cands.add(cand);
						
					}
				}
			}
		}
		//this.setAssociatedIDs(cands);
		return cands;
	}
	

	
	public void setTrackPars(Track cand, Trajectory traj, TrajectoryFinder trjFind, StateVec stateVec, double z) {
		double pz = cand.get_P() / Math.sqrt(stateVec.tanThetaX()*stateVec.tanThetaX() + stateVec.tanThetaY()*stateVec.tanThetaY() + 1);
		
		
		dcSwim.SetSwimParameters(stateVec.x(),stateVec.y(),z,
				-pz*stateVec.tanThetaX(),-pz*stateVec.tanThetaY(),-pz,
				 -cand.get_Q());
		
		double[] VecAtTar = dcSwim.SwimToPlane(0);
		
		if(VecAtTar==null) {
			cand.fit_Successful=false;
			return;
		}
		double totPathLen = VecAtTar[6];
		if(totPathLen<cand.get(1).get_Point().z()) {
			cand.fit_Successful=false;
			return;
		}
		
		double xOr = VecAtTar[0];
		double yOr = VecAtTar[1];
		double zOr = VecAtTar[2];
		double pxOr = -VecAtTar[3];
		double pyOr = -VecAtTar[4];
		double pzOr = -VecAtTar[5];
		
		if(traj!=null && trjFind!=null)
			traj.set_Trajectory(trjFind.getStateVecsAlongTrajectory(xOr, yOr, pxOr/pzOr, pyOr/pzOr, cand.get_P(),cand.get_Q()));
		
		Point3D trakOrigTiltSec = new Point3D(xOr,yOr,zOr);
		Point3D pAtOrigTiltSec = new Point3D(pxOr,pyOr,pzOr);
		
		cand.set_Vtx0_TiltedCS(trakOrigTiltSec);
		cand.set_pAtOrig_TiltedCS(pAtOrigTiltSec.toVector3D());
		
		Cross crossAtOrig = new Cross(cand.get(0).get_Sector(), cand.get(0).get_Region(), -1);
		
		Point3D trakOrig = crossAtOrig.getCoordsInLab(trakOrigTiltSec.x(),trakOrigTiltSec.y(),trakOrigTiltSec.z());
		Point3D pAtOrig = crossAtOrig.getCoordsInLab(pAtOrigTiltSec.x(),pAtOrigTiltSec.y(),pAtOrigTiltSec.z());
		
		if(z==cand.get(2).get_Point().z())
			cand.set_TotPathLen(totPathLen);
		
		cand.set_Vtx0(trakOrig);
		cand.set_pAtOrig(pAtOrig.toVector3D());
		
		cand.fit_Successful=true;
		cand.set_TrackingInfoString(trking);
	}



}
