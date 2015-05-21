package org.jlab.rec.bst.track;

import java.util.ArrayList;
import java.util.List;

import org.jlab.geom.prim.Point3D;
import org.jlab.geom.prim.Vector3D;
import org.jlab.rec.bst.Constants;
import org.jlab.rec.bst.Geometry;
import org.jlab.rec.bst.track.Track;
import org.jlab.rec.bst.trajectory.BSTSwimmer;

public class TrackListFinder {

	private BSTSwimmer bstSwim = new BSTSwimmer();

	
	public TrackListFinder() {
		// TODO Auto-generated constructor stub
	}
	public List<Track> getTracks(List<Track> cands,Geometry geo) {
		List<Track> tracks = new ArrayList<Track>();
		if(cands.size()==0) {
			System.err.print("Error no tracks found");
			return cands;
		}
		
		for(Track trk : cands) {
			EnergyLossCorr kf = new EnergyLossCorr(trk);
			//EnergyLossCorr kf = new EnergyLossCorr();
		    
		    //if(trk.get_circleFitChi2Prob()>200 || trk.get_lineFitChi2Prob()>500)
		    //	continue;
		    
			int charge = trk.get_Q();
			double maxPathLength =5.0 ;//very loose cut 
			bstSwim.SetSwimParameters(trk.get_Helix(), maxPathLength, charge,trk.get_P());
			
			double[] pointAtCylRad = bstSwim.SwimToCylinder(Constants.CTOFINNERRADIUS);
			trk.set_TrackPointAtCTOFRadius(new Point3D(pointAtCylRad[0],pointAtCylRad[1],pointAtCylRad[2]));
			trk.set_TrackDirAtCTOFRadius(new Vector3D(pointAtCylRad[3],pointAtCylRad[4],pointAtCylRad[5]));
			
			//System.out.println("******* before EL "+trk.get_P());
			kf.doCorrection(trk, geo);
			//System.out.println("*******  after EL "+trk.get_P());
			
			trk.set_pathLength(bstSwim.swamPathLength);
			trk.set_P(trk.get_P()*Constants.BFCorrFac);
			trk.set_Pt(trk.get_Pt()*Constants.BFCorrFac);
			trk.set_Pz(trk.get_Pz()*Constants.BFCorrFac);
			tracks.add(trk);
		}
		return tracks;
	}
}
