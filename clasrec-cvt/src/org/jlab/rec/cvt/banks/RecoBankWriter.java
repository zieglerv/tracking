package org.jlab.rec.cvt.banks;

import java.util.ArrayList;
import java.util.List;

import org.jlab.data.io.DataBank;
import org.jlab.evio.clas12.EvioDataBank;
import org.jlab.evio.clas12.EvioDataEvent;
import org.jlab.geom.prim.Vector3D; 
import org.jlab.rec.cvt.Constants;
import org.jlab.rec.cvt.cluster.Cluster;
import org.jlab.rec.cvt.cross.Cross;
import org.jlab.rec.cvt.hit.FittedHit;
import org.jlab.rec.cvt.track.StraightTrack;
import org.jlab.rec.cvt.track.Track;
import org.jlab.rec.cvt.trajectory.Helix;
import org.jlab.rec.cvt.trajectory.StateVec;

import Jama.Matrix;

public class RecoBankWriter {

	/**
	 * 
	 * @param hitlist the list of  hits that are of the type FittedHit.  If the hit has not been fitted, the fitted hit fields are left to
	 * their default values.
	 * @return hits bank
	 *
	 */
	public static EvioDataBank fillSVTHitsBank(EvioDataEvent event, List<FittedHit> hitlist) {
		if(hitlist==null)
			return null;
		if(hitlist.size()==0)
			return null;
		
		EvioDataBank bank = (EvioDataBank)
		          event.getDictionary().createBank("BSTRec::Hits", hitlist.size());

		for(int i =0; i< hitlist.size(); i++) {
			
			bank.setInt("ID", i, hitlist.get(i).get_Id());
			
			bank.setInt("layer",i, hitlist.get(i).get_Layer());
			bank.setInt("sector",i, hitlist.get(i).get_Sector());
			bank.setInt("strip",i, hitlist.get(i).get_Strip().get_Strip());
			
			bank.setDouble("fitResidual",i, hitlist.get(i).get_Residual());
			bank.setInt("trkingStat",i, hitlist.get(i).get_TrkgStatus());
			
			bank.setInt("clusterID", i, hitlist.get(i).get_AssociatedClusterID());
          
		}

		return bank;

	}

	/**
	 * 
	 * @param cluslist the reconstructed list of fitted clusters in the event
	 * @return clusters bank
	 */
	public static EvioDataBank fillSVTClustersBank(EvioDataEvent event, List<Cluster> cluslist) {
		if(cluslist==null)
			return null;
		if(cluslist.size()==0)
			return null;
		
		EvioDataBank bank =  (EvioDataBank) event.getDictionary().createBank("BSTRec::Clusters",cluslist.size());
		int[] hitIdxArray= new int[5];
		
		for(int i =0; i< cluslist.size(); i++) {
			for(int j =0; j<hitIdxArray.length; j++) {
				hitIdxArray[j] = -1;
			}
			bank.setInt("ID",i, cluslist.get(i).get_Id());
			bank.setInt("sector",i, cluslist.get(i).get_Sector());	
			bank.setInt("layer",i, cluslist.get(i).get_Layer());		
			bank.setInt("size", i, cluslist.get(i).size());
			bank.setDouble("ETot", i, cluslist.get(i).get_TotalEnergy());
			bank.setInt("seedStrip", i, cluslist.get(i).get_SeedStrip());
			bank.setDouble("centroid", i, cluslist.get(i).get_Centroid());
			bank.setDouble("seedE", i, cluslist.get(i).get_SeedEnergy());
			
			for(int j = 0; j<cluslist.get(i).size(); j++) {		
				if(j<hitIdxArray.length)
					hitIdxArray[j] = cluslist.get(i).get(j).get_Id();
			}
			
			for(int j =0; j<hitIdxArray.length; j++) {
				String hitStrg = "Hit";
				hitStrg+=(j+1);
				hitStrg+="_ID";
				bank.setInt(hitStrg, i, hitIdxArray[j]);
			}

		}

		return bank;
		
	}
	

	/**
	 * 
	 * @param crosses the reconstructed list of crosses in the event
	 * @return crosses bank
	 */
	public static EvioDataBank fillSVTCrossesBank(EvioDataEvent event, List<ArrayList<Cross>> crosses) {
		if(crosses==null)
			return null;
		if(crosses.get(0).size()==0)
			return null;
		
		EvioDataBank bank =  (EvioDataBank) event.getDictionary().createBank("BSTRec::Crosses",crosses.get(0).size());
    
		int index =0;
		int i =0;
		for(int j =0; j< crosses.get(i).size(); j++) {
			bank.setInt("ID",index, crosses.get(i).get(j).get_Id());
			bank.setInt("sector",index, crosses.get(i).get(j).get_Sector());	
			bank.setInt("region", index, crosses.get(i).get(j).get_Region());
			bank.setDouble("x", index, crosses.get(i).get(j).get_Point().x());
			bank.setDouble("y", index, crosses.get(i).get(j).get_Point().y());
			bank.setDouble("z", index, crosses.get(i).get(j).get_Point().z());
			bank.setDouble("err_x", index, crosses.get(i).get(j).get_PointErr().x());
			bank.setDouble("err_y", index, crosses.get(i).get(j).get_PointErr().y());
			bank.setDouble("err_z", index, crosses.get(i).get(j).get_PointErr().z());
			if( crosses.get(i).get(j).get_Dir()!=null) {
				bank.setDouble("ux", index, crosses.get(i).get(j).get_Dir().x());
				bank.setDouble("uy", index, crosses.get(i).get(j).get_Dir().y());
				bank.setDouble("uz", index, crosses.get(i).get(j).get_Dir().z());
			} else {
				bank.setDouble("ux", index, 0);
				bank.setDouble("uy", index, 0);
				bank.setDouble("uz", index, 0);
			}
			if(crosses.get(i).get(j).get_Cluster1()!=null) 
				bank.setInt("Cluster1_ID", index, crosses.get(i).get(j).get_Cluster1().get_Id());
			if(crosses.get(i).get(j).get_Cluster2()!=null) 
				bank.setInt("Cluster2_ID", index, crosses.get(i).get(j).get_Cluster2().get_Id());
			index++;
		}
		
       
		return bank;
		
	}
	
	public static EvioDataBank fillBMTHitsBank(EvioDataEvent event, List<FittedHit> hitlist) {
		if(hitlist==null)
			return null;
		if(hitlist.size()==0)
			return null;
		
		EvioDataBank bank = (EvioDataBank)
		          event.getDictionary().createBank("BMTRec::Hits", hitlist.size());

		for(int i =0; i< hitlist.size(); i++) {
			
			bank.setInt("ID", i, hitlist.get(i).get_Id());
			
			bank.setInt("layer",i, hitlist.get(i).get_Layer());
			bank.setInt("sector",i, hitlist.get(i).get_Sector());
			bank.setInt("strip",i, hitlist.get(i).get_Strip().get_Strip());
			
			bank.setDouble("fitResidual",i, hitlist.get(i).get_Residual());
			bank.setInt("trkingStat",i, hitlist.get(i).get_TrkgStatus());
			
			bank.setInt("clusterID", i, hitlist.get(i).get_AssociatedClusterID());
          
		}

		return bank;

	}

	/**
	 * 
	 * @param cluslist the reconstructed list of fitted clusters in the event
	 * @return clusters bank
	 */
	public static EvioDataBank fillBMTClustersBank(EvioDataEvent event, List<Cluster> cluslist) {
		if(cluslist==null)
			return null;
		if(cluslist.size()==0)
			return null;
		
		EvioDataBank bank =  (EvioDataBank) event.getDictionary().createBank("BMTRec::Clusters",cluslist.size());
		int[] hitIdxArray= new int[5];
		
		for(int i =0; i< cluslist.size(); i++) {
			for(int j =0; j<hitIdxArray.length; j++) {
				hitIdxArray[j] = -1;
			}
			bank.setInt("ID",i, cluslist.get(i).get_Id());
			bank.setInt("sector",i, cluslist.get(i).get_Sector());	
			bank.setInt("layer",i, cluslist.get(i).get_Layer());		
			bank.setInt("size", i, cluslist.get(i).size());
			bank.setDouble("ETot", i, cluslist.get(i).get_TotalEnergy());
			bank.setInt("seedStrip", i, cluslist.get(i).get_SeedStrip());
			bank.setDouble("centroid", i, cluslist.get(i).get_Centroid());
			bank.setDouble("seedE", i, cluslist.get(i).get_SeedEnergy());
			
			for(int j = 0; j<cluslist.get(i).size(); j++) {		
				if(j<hitIdxArray.length)
					hitIdxArray[j] = cluslist.get(i).get(j).get_Id();
			}
			
			for(int j =0; j<hitIdxArray.length; j++) {
				String hitStrg = "Hit";
				hitStrg+=(j+1);
				hitStrg+="_ID";
				bank.setInt(hitStrg, i, hitIdxArray[j]);
			}

		}

		return bank;
		
	}
	

	/**
	 * 
	 * @param crosses the reconstructed list of crosses in the event
	 * @return crosses bank
	 */
	public static EvioDataBank fillBMTCrossesBank(EvioDataEvent event, List<ArrayList<Cross>> crosses) {
		if(crosses==null)
			return null;
		if(crosses.get(1).size()==0)
			return null;
		
		EvioDataBank bank =  (EvioDataBank) event.getDictionary().createBank("BMTRec::Crosses", crosses.get(1).size());
    
		int index =0;
		int i = 1;
		for(int j =0; j< crosses.get(i).size(); j++) {
			bank.setInt("ID",index, crosses.get(i).get(j).get_Id());
			bank.setInt("sector",index, crosses.get(i).get(j).get_Sector());	
			bank.setInt("region", index, crosses.get(i).get(j).get_Region());
			bank.setDouble("x", index, crosses.get(i).get(j).get_Point().x());
			bank.setDouble("y", index, crosses.get(i).get(j).get_Point().y());
			bank.setDouble("z", index, crosses.get(i).get(j).get_Point().z());
			bank.setDouble("err_x", index, crosses.get(i).get(j).get_PointErr().x());
			bank.setDouble("err_y", index, crosses.get(i).get(j).get_PointErr().y());
			bank.setDouble("err_z", index, crosses.get(i).get(j).get_PointErr().z());
			if( crosses.get(i).get(j).get_Dir()!=null) {
				bank.setDouble("ux", index, crosses.get(i).get(j).get_Dir().x());
				bank.setDouble("uy", index, crosses.get(i).get(j).get_Dir().y());
				bank.setDouble("uz", index, crosses.get(i).get(j).get_Dir().z());
			} else {
				bank.setDouble("ux", index, 0);
				bank.setDouble("uy", index, 0);
				bank.setDouble("uz", index, 0);
			}
			if(crosses.get(i).get(j).get_Cluster1()!=null) 
				bank.setInt("Cluster1_ID", index, crosses.get(i).get(j).get_Cluster1().get_Id());
			if(crosses.get(i).get(j).get_Cluster2()!=null) 
				bank.setInt("Cluster2_ID", index, crosses.get(i).get(j).get_Cluster2().get_Id());
			index++;
		}
	
       
		return bank;
		
	}
	
	
	/**
	 * 
	 * @param event the event
	 * @param trkcands the list of reconstructed helical tracks
	 * @return track bank
	 */
	public static EvioDataBank fillTracksBank(EvioDataEvent event, List<Track> trkcands) {
		if(trkcands==null)
			return null;
		if(trkcands.size()==0)
			return null;
		
		EvioDataBank bank =  (EvioDataBank) event.getDictionary().createBank("CVTRec::Tracks",trkcands.size());
    	// an array representing the ids of the crosses that belong to the track: for a helical track with the current
		// 4 regions of SVT + 1 region of BMT there can be up to 4 crosses of type SVT and 2 of type BMT (1 for the C detector and 1 for the Z detector)
		int[] crossIdxArray= new int[4+2];

		for(int i =0; i<trkcands.size(); i++) {
			
			for(int j =0; j<crossIdxArray.length; j++) {
				crossIdxArray[j] = -1;
			}
			
			bank.setInt("fittingMethod",i, 1);
			bank.setInt("ID", i, trkcands.get(i).get_Id());
			
			bank.setInt("q", i, trkcands.get(i).get_Q());
			bank.setDouble("p", i, trkcands.get(i).get_P());
			bank.setDouble("pt", i, trkcands.get(i).get_Pt());
			Helix helix = trkcands.get(i).get_helix();
			
			bank.setDouble("phi0", i, helix.get_phi_at_dca());
			bank.setDouble("tandip", i, helix.get_tandip());
			bank.setDouble("z0", i, helix.get_Z0());
			bank.setDouble("d0", i, helix.get_dca());
			
			// this is the format of the covariance matrix for helical tracks
			// cov matrix = 
			// | d_dca*d_dca                   d_dca*d_phi_at_dca            d_dca*d_curvature        0            0             |
			// | d_phi_at_dca*d_dca     d_phi_at_dca*d_phi_at_dca     d_phi_at_dca*d_curvature        0            0             |
			// | d_curvature*d_dca	    d_curvature*d_phi_at_dca      d_curvature*d_curvature         0            0             |
			// | 0                              0                             0                    d_Z0*d_Z0                     |
			// | 0                              0                             0                       0        d_tandip*d_tandip |X

			Matrix covmatrix = helix.get_covmatrix();
			if(covmatrix!= null){
				bank.setDouble("cov_d02", i, covmatrix.get(0, 0));
				bank.setDouble("cov_d0phi0", i, covmatrix.get(0, 1));
				bank.setDouble("cov_d0rho", i, covmatrix.get(0, 2));
				bank.setDouble("cov_phi02", i, covmatrix.get(1, 1));
				bank.setDouble("cov_phi0rho", i, covmatrix.get(1, 2));
				bank.setDouble("cov_rho2", i, covmatrix.get(2, 2));
				bank.setDouble("cov_z02", i, covmatrix.get(3, 3));
				bank.setDouble("cov_tandip2", i, covmatrix.get(4, 4));				
			}  else {
				bank.setDouble("cov_d02",i, -999);
				bank.setDouble("cov_d0phi0",i,  -999);
				bank.setDouble("cov_d0rho",i,  -999);
				bank.setDouble("cov_phi02",i,  -999);
				bank.setDouble("cov_phi0rho",i, -999);
				bank.setDouble("cov_rho2",i, -999);
				bank.setDouble("cov_z02[i]",i, -999);
				bank.setDouble("cov_tandip2",i, -999);
			}		
			bank.setDouble("c_x", i, trkcands.get(i).get_TrackPointAtCTOFRadius().x()/10.); // convert to cm
			bank.setDouble("c_y", i, trkcands.get(i).get_TrackPointAtCTOFRadius().y()/10.); // convert to cm
			bank.setDouble("c_z", i, trkcands.get(i).get_TrackPointAtCTOFRadius().z()/10.); // convert to cm
			bank.setDouble("c_ux", i, trkcands.get(i).get_TrackDirAtCTOFRadius().x());
			bank.setDouble("c_uy", i, trkcands.get(i).get_TrackDirAtCTOFRadius().y());
			bank.setDouble("c_uz", i, trkcands.get(i).get_TrackDirAtCTOFRadius().z());
			bank.setDouble("pathlength",i,trkcands.get(i).get_pathLength()/10); // conversion to cm
			
			// fills the list of cross ids for crosses belonging to that reconstructed track
			
			for(int j = 0; j<trkcands.get(i).size(); j++) {		
				if(trkcands.get(i).get(j).get_Detector()=="SVT")
					crossIdxArray[trkcands.get(i).get(j).get_Region()-1] = trkcands.get(i).get(j).get_Id();
				if(trkcands.get(i).get(j).get_Detector()=="BMT")
					crossIdxArray[trkcands.get(i).get(j).get_Region()-1+Constants.CVTCONFIGSTARTREG] = trkcands.get(i).get(j).get_Id();
			}
			bank.setDouble("circlefit_chi2_per_ndf", i,trkcands.get(i).get_circleFitChi2PerNDF() );
			bank.setDouble("linefit_chi2_per_ndf", i,trkcands.get(i).get_lineFitChi2PerNDF() );
			
			for(int j =0; j<crossIdxArray.length; j++) {
				String hitStrg = "Cross";
				hitStrg+=(j+1);
				hitStrg+="_ID";
				bank.setInt(hitStrg, i, crossIdxArray[j]);
			}

		}
		//bank.show();
		return bank;
		
	}
	/**
	 * 
	 * @param event the event
	 * @param trkcands the list of reconstructed straight tracks
	 * @return cosmic bank
	 */
	public static DataBank fillStraightTracksBank(EvioDataEvent event,
			List<StraightTrack> cosmics) {
		if(cosmics==null)
			return null;
		if(cosmics.size()==0)
			return null;
		
		EvioDataBank bank =  (EvioDataBank) event.getDictionary().createBank("CVTRec::Cosmics",cosmics.size());
		// an array representing the ids of the crosses that belong to the track: for a helical track with the current
		// 4 regions of SVT + 1 region of BMT there can be up to 4*2 (*2: for each hemisphere) crosses of type SVT and 2*2 of type PSEUDOBMT (1 for the C detector and 1 for the Z detector)
		int[] crossIdxArray= new int[8+4];
		
		for(int i =0; i< cosmics.size(); i++) {
			
			for(int j =0; j<crossIdxArray.length; j++) {
				crossIdxArray[j] = -1;
			}
			
			bank.setInt("ID", i, cosmics.get(i).get_Id());
			bank.setDouble("chi2", i, cosmics.get(i).get_chi2());
			bank.setInt("ndf", i, (int) cosmics.get(i).get_ndf());
			bank.setDouble("trkline_yx_slope", i, cosmics.get(i).get_ray().get_yxslope());
			bank.setDouble("trkline_yx_interc", i, cosmics.get(i).get_ray().get_yxinterc());
			bank.setDouble("trkline_yz_slope", i, cosmics.get(i).get_ray().get_yzslope());
			bank.setDouble("trkline_yz_interc", i, cosmics.get(i).get_ray().get_yzinterc());
			
			// get the cosmics ray unit direction vector
			Vector3D u = new Vector3D(cosmics.get(i).get_ray().get_yxslope(), 1, cosmics.get(i).get_ray().get_yzslope()).asUnit();
		    // calculate the theta and phi components of the ray direction vector in degrees
		    bank.setDouble("theta", i, Math.toDegrees(u.theta()));
		    bank.setDouble("phi", i, Math.toDegrees(u.phi()));
		    
		    // the array of cross ids is filled in order of the SVT cosmic region 1 to 8 starting from the bottom-most double layer
			for(int j = 0; j<cosmics.get(i).size(); j++) {	
				if(cosmics.get(i).get(j).get_Detector()=="SVT")
					crossIdxArray[cosmics.get(i).get(j).get_SVTCosmicsRegion()-1] = cosmics.get(i).get(j).get_Id();
			}
			// now add the BMT cross ids
			for(int j = 0; j<cosmics.get(i).size(); j++) {	
				if(cosmics.get(i).get(j).get_Detector()=="BMT")
					if(cosmics.get(i).get(j).get_DetectorType()=="Z")
						crossIdxArray[9] = cosmics.get(i).get(j).get_Id(); 
				if(cosmics.get(i).get(j).get_DetectorType()=="C")
					crossIdxArray[10] = cosmics.get(i).get(j).get_Id(); 
			}
			
			for(int j =0; j<crossIdxArray.length; j++) {
				String hitStrg = "Cross";
				hitStrg+=(j+1);
				hitStrg+="_ID";
				bank.setInt(hitStrg, i, crossIdxArray[j]);
			}
		}
		return bank;	
	}
	
	
	
	
	public static DataBank fillStraightTracksTrajectoryBank(EvioDataEvent event,
			List<StraightTrack> trks) {
		if(trks==null)
			return null;
		if(trks.size()==0)
			return null;
		
		EvioDataBank bank =  (EvioDataBank) event.getDictionary().createBank("CVTRec::Trajectory",20*trks.size()); // 8 SVT layer + 2 BMT layers for each hemisphere
	
		int k =0;
		for(int i =0; i< trks.size(); i++) {
			if(trks.get(i).get_Trajectory()==null)
				continue;
			for(StateVec stVec : trks.get(i).get_Trajectory())  {
		    		
    			bank.setInt("ID", k, stVec.get_ID()); 
    			bank.setInt("LayerTrackIntersPlane", k, stVec.get_SurfaceLayer());
    			bank.setInt("SectorTrackIntersPlane", k, stVec.get_SurfaceSector());
				bank.setDouble("XtrackIntersPlane", k, stVec.x());
				bank.setDouble("YtrackIntersPlane", k, stVec.y());
				bank.setDouble("ZtrackIntersPlane", k, stVec.z());
				bank.setDouble("PhiTrackIntersPlane", k, stVec.get_TrkPhiAtSurface());
				bank.setDouble("ThetaTrackIntersPlane", k, stVec.get_TrkThetaAtSurface());
				bank.setDouble("trkToMPlnAngl", k, stVec.get_TrkToModuleAngle());
				bank.setDouble("CalcCentroidStrip", k, stVec.get_CalcCentroidStrip());
				k++;
		    		
			}
		}
    
	return bank;
	}
	
	public static DataBank fillHelicalTracksTrajectoryBank(EvioDataEvent event,
			List<Track> trks) {
		if(trks==null)
			return null;
		if(trks.size()==0)
			return null;
		
		EvioDataBank bank =  (EvioDataBank) event.getDictionary().createBank("CVTRec::Trajectory",10*trks.size()); // 8 SVT layer + 2 BMT layers 
	
		int k =0;
		for(int i =0; i< trks.size(); i++) {
			if(trks.get(i).get_Trajectory()==null)
				continue;
			for(StateVec stVec : trks.get(i).get_Trajectory())  {
		    		
    			bank.setInt("ID", k, stVec.get_ID()); 
    			bank.setInt("LayerTrackIntersPlane", k, stVec.get_SurfaceLayer());
    			bank.setInt("SectorTrackIntersPlane", k, stVec.get_SurfaceSector());
				bank.setDouble("XtrackIntersPlane", k, stVec.x());
				bank.setDouble("YtrackIntersPlane", k, stVec.y());
				bank.setDouble("ZtrackIntersPlane", k, stVec.z());
				bank.setDouble("PhiTrackIntersPlane", k, stVec.get_TrkPhiAtSurface());
				bank.setDouble("ThetaTrackIntersPlane", k, stVec.get_TrkThetaAtSurface());
				bank.setDouble("trkToMPlnAngl", k, stVec.get_TrkToModuleAngle());
				bank.setDouble("CalcCentroidStrip", k, stVec.get_CalcCentroidStrip());
				k++;
		    		
			}
		}
    
		return bank;
	}

	public static void appendCVTBanks(EvioDataEvent event,
			List<FittedHit> sVThits, List<FittedHit> bMThits,
			List<Cluster> sVTclusters, List<Cluster> bMTclusters,
			List<ArrayList<Cross>> crosses, List<Track> trks) {
		List<DataBank> svtbanks = new ArrayList<DataBank>();
		List<DataBank> bmtbanks = new ArrayList<DataBank>();
		List<DataBank> cvtbanks = new ArrayList<DataBank>();
		
		DataBank bank1 = RecoBankWriter.fillSVTHitsBank((EvioDataEvent) event, sVThits);	
		if(bank1!=null)
			svtbanks.add(bank1);
		
		DataBank bank2 = RecoBankWriter.fillBMTHitsBank((EvioDataEvent) event, bMThits);	
		if(bank2!=null)
			bmtbanks.add(bank2);
		
		DataBank bank3 = RecoBankWriter.fillSVTClustersBank((EvioDataEvent) event, sVTclusters);
		if(bank3!=null)
			svtbanks.add(bank3);
		
		DataBank bank4 = RecoBankWriter.fillBMTClustersBank((EvioDataEvent) event, bMTclusters);
		if(bank4!=null)
			bmtbanks.add(bank4);
		
		DataBank bank5 = RecoBankWriter.fillSVTCrossesBank((EvioDataEvent) event, crosses);
		if(bank5!=null)
			svtbanks.add(bank5);
		
		DataBank bank6 = RecoBankWriter.fillBMTCrossesBank((EvioDataEvent) event, crosses);
		if(bank6!=null)
			bmtbanks.add(bank6);
		
		//found tracks
		DataBank bank7 = RecoBankWriter.fillTracksBank((EvioDataEvent) event, trks);
		if(bank7!=null)
			cvtbanks.add(bank7);
		
		//found trajectories
		DataBank bank8 = RecoBankWriter.fillHelicalTracksTrajectoryBank((EvioDataEvent) event, trks);
		if(bank8!=null)
			cvtbanks.add(bank8);
		
		
	
		if(svtbanks.size()==3)
			event.appendBanks(svtbanks.get(0),svtbanks.get(1), svtbanks.get(2));
		if(svtbanks.size()==2)
			event.appendBanks(svtbanks.get(0),svtbanks.get(1));
		if(svtbanks.size()==1)
			event.appendBanks(svtbanks.get(0));
		if(bmtbanks.size()==3)
			event.appendBanks(bmtbanks.get(0),bmtbanks.get(1), bmtbanks.get(2));
		if(bmtbanks.size()==2)
			event.appendBanks(bmtbanks.get(0),bmtbanks.get(1));
		if(bmtbanks.size()==1)
			event.appendBanks(bmtbanks.get(0));
		if(cvtbanks.size()==2)
			event.appendBanks(cvtbanks.get(0),cvtbanks.get(1));
		if(cvtbanks.size()==1)
			event.appendBanks(cvtbanks.get(0));
		
		//event.show();
		
	}

	public static void appendCVTCosmicsBanks(EvioDataEvent event,
			List<FittedHit> sVThits, List<FittedHit> bMThits,
			List<Cluster> sVTclusters, List<Cluster> bMTclusters,
			List<ArrayList<Cross>> crosses, List<StraightTrack> trks) {
		List<DataBank> svtbanks = new ArrayList<DataBank>();
		List<DataBank> bmtbanks = new ArrayList<DataBank>();
		List<DataBank> cvtbanks = new ArrayList<DataBank>();
		
		DataBank bank1 = RecoBankWriter.fillSVTHitsBank((EvioDataEvent) event, sVThits);	
		if(bank1!=null)
			svtbanks.add(bank1);
		
		DataBank bank2 = RecoBankWriter.fillBMTHitsBank((EvioDataEvent) event, bMThits);	
		if(bank2!=null)
			bmtbanks.add(bank2);
		
		DataBank bank3 = RecoBankWriter.fillSVTClustersBank((EvioDataEvent) event, sVTclusters);
		if(bank3!=null)
			svtbanks.add(bank3);
		
		DataBank bank4 = RecoBankWriter.fillBMTClustersBank((EvioDataEvent) event, bMTclusters);
		if(bank4!=null)
			bmtbanks.add(bank4);
		
		DataBank bank5 = RecoBankWriter.fillSVTCrossesBank((EvioDataEvent) event, crosses);
		if(bank5!=null)
			svtbanks.add(bank5);
		
		DataBank bank6 = RecoBankWriter.fillBMTCrossesBank((EvioDataEvent) event, crosses);
		if(bank6!=null)
			bmtbanks.add(bank6);
		
		//found tracks
		DataBank bank7 = RecoBankWriter.fillStraightTracksBank((EvioDataEvent) event, trks);
		if(bank7!=null)
			cvtbanks.add(bank7);
		
		//found trajectories
		DataBank bank8 = RecoBankWriter.fillStraightTracksTrajectoryBank((EvioDataEvent) event, trks);
		if(bank8!=null)
			cvtbanks.add(bank8);
				
		
		if(svtbanks.size()==3)
			event.appendBanks(svtbanks.get(0),svtbanks.get(1), svtbanks.get(2));
		if(svtbanks.size()==2)
			event.appendBanks(svtbanks.get(0),svtbanks.get(1));
		if(svtbanks.size()==1)
			event.appendBanks(svtbanks.get(0));
		if(bmtbanks.size()==3)
			event.appendBanks(bmtbanks.get(0),bmtbanks.get(1), bmtbanks.get(2));
		if(bmtbanks.size()==2)
			event.appendBanks(bmtbanks.get(0),bmtbanks.get(1));
		if(bmtbanks.size()==1)
			event.appendBanks(bmtbanks.get(0));
		if(cvtbanks.size()==2)
			event.appendBanks(cvtbanks.get(0),cvtbanks.get(1));
		if(cvtbanks.size()==1)
			event.appendBanks(cvtbanks.get(0));
		
		
		
		
	}

	
	
}