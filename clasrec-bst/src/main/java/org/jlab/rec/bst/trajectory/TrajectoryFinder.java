package org.jlab.rec.bst.trajectory;

import java.util.ArrayList;

import org.jMath.Vector.threeVec;

import trackfitter.track.Helix;

import org.jlab.rec.bst.Constants;
import org.jlab.rec.bst.Geometry;
import org.jlab.rec.bst.cluster.Cluster;
import org.jlab.rec.bst.cross.Cross; 

/**
 * A driver class to find the trajectory of a track candidate.  NOTE THAT THE PATH TO FIELD MAPS IS SET BY THE CLARA_SERVICES ENVIRONMENT VAR.
 * 
 * @author ziegler
 *
 */
public class TrajectoryFinder {
	
	public TrajectoryFinder(Helix helix) {
		this.set_helix(helix);
	}
	private Helix _helix;
	
	
	/**
	 * 
	 * @param candCrossList the input list of crosses used in determining a trajectory
	 * @return a trajectory object
	 */
	public Trajectory findTrajectory(ArrayList<Cross> candCrossList, Geometry geo) {
		Trajectory traj = new Trajectory();
		if(candCrossList.size()==0) {
			System.err.print("Trajectory Error:  cross list is empty");
			return traj;
		}
		traj.addAll(candCrossList);
		int[] Sectors = new int[Constants.NLAYR];
		for(int k = 0; k< candCrossList.size(); k++) {
			int l = candCrossList.get(k).get_Region()*2-1;
			Sectors[l-1] = candCrossList.get(k).get_Sector();
			Sectors[l] = candCrossList.get(k).get_Sector();			
		}
		
		for (int a = 0; a<Sectors.length; a++) {
			if(Sectors[a]==0) {

				threeVec I = _helix.getPointAtRadius(Constants.MODULERADIUS[a][0]);
				
				double trackPhiAtLayer = I.phi();
				if(trackPhiAtLayer<0)
					trackPhiAtLayer+=2.*Math.PI;
				
				int sec = geo.findSectorFromAngle(a+1,trackPhiAtLayer);
				Sectors[a] = sec;
				
				
			}
			
		}
		traj.set_Sector(Sectors);
		
		ArrayList<StateVec> stateVecs = new ArrayList<StateVec>();
		
		for(int l = 0; l< Constants.NLAYR; l++) {
			int layer = l+1;
			int sector = Sectors[l];
			
			threeVec helixInterWithBstPlane = geo.intersectionOfHelixWithPlane(layer, sector, _helix);

			threeVec trkDir = _helix.getTrackDirectionAtRadius(helixInterWithBstPlane.rt());
			
			StateVec stVec = new StateVec(helixInterWithBstPlane.x(), helixInterWithBstPlane.y(),helixInterWithBstPlane.z(),
					trkDir.x(),trkDir.y(),trkDir.z());
			
			stateVecs.add(stVec);		
		}
		
		traj.set_Trajectory(stateVecs);
		
		updateFittedHitList(traj, geo);
		
		setCrossDirVecsFromTrajectory(traj);
		
		return traj;
	}
	
	private void setCrossDirVecsFromTrajectory(Trajectory traj) {
		
		for(int k = 0; k< traj.size(); k++) {
			int l = traj.get(k).get_Region()*2-1;
			threeVec tanDir = new threeVec(traj.get_Trajectory().get(l).ux(),
					traj.get_Trajectory().get(l).uy(),
					traj.get_Trajectory().get(l).uz());
			traj.get(k).set_Dir(tanDir);
		}
	}
	
	private void updateFittedHitList(Trajectory traj, Geometry geo) {
		if(traj == null)
			return;
		//update FittedHits list
		for(int l = 0; l< Constants.NLAYR; l++) {
		
			Cluster clsOnTrk = null;
			for(int c = 0; c< traj.size(); c++) {
				if(traj.get(c) == null)
					return;
				
				if(l%2==0)
					clsOnTrk = traj.get(c).get_Cluster1();
				if(l%2==1)
					clsOnTrk = traj.get(c).get_Cluster2();
				
				
				if(clsOnTrk!= null) {
					double approxZ = traj.get(c).get_Point().z();
					for(int cs = 0; cs< clsOnTrk.size(); cs++) {
						double stripResol = geo.getSingleStripResolution(clsOnTrk.get(cs).get_Layer(), clsOnTrk.get(cs).get_Strip(),approxZ);
								
						
						clsOnTrk.get(cs).set_docaToTrk(geo.getDOCAToStrip(clsOnTrk.get(cs).get_Sector(), clsOnTrk.get(cs).get_Layer(), clsOnTrk.get(cs).get_Strip(), 
								new threeVec(traj.get_Trajectory().get(l).x(),traj.get_Trajectory().get(l).y(),traj.get_Trajectory().get(l).z())));
						
						clsOnTrk.get(cs).set_stripResolutionAtDoca(stripResol);
					}
				}
			}
		}
	}
	

	public Helix get_helix() {
		return _helix;
	}




	public void set_helix(Helix _helix) {
		this._helix = _helix;
	}

	

	

}
