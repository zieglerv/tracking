package org.jlab.rec.bst.banks;

import java.util.List;

import org.jMath.Vector.threeVec;
import org.jlab.data.io.DataBank;
import org.jlab.evio.clas12.EvioDataBank;
import org.jlab.evio.clas12.EvioDataEvent;
import org.jlab.rec.bst.Constants;
import org.jlab.rec.bst.cluster.Cluster;
import org.jlab.rec.bst.cross.Cross;
import org.jlab.rec.bst.hit.FittedHit;
import org.jlab.rec.bst.track.CosmicTrack;
import org.jlab.rec.bst.track.Track;

import trackfitter.track.Helix;
import Jama.Matrix;

public class RecoBankWriter {

	/**
	 * 
	 * @param hitlist the list of hits that are of the type FittedHit.  If the hit has not been fitted, the fitted hit fields are left to
	 * their default values.
	 * @return hits bank
	 *
	 */
	public static EvioDataBank fillHitsBank(EvioDataEvent event, List<FittedHit> hitlist) {

		EvioDataBank bank = (EvioDataBank)
		          event.getDictionary().createBank("BSTRec::Hits", hitlist.size());

		for(int i =0; i< hitlist.size(); i++) {
			
			bank.setInt("ID", i, hitlist.get(i).get_Id());
			bank.setInt("layer",i, hitlist.get(i).get_Layer());
			bank.setInt("sector",i, hitlist.get(i).get_Sector());
			bank.setInt("strip",i, hitlist.get(i).get_Strip());
			
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
	public static EvioDataBank fillClustersBank(EvioDataEvent event, List<Cluster> cluslist) {

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
	 * @param crosslist the reconstructed list of crosses in the event
	 * @return crosses bank
	 */
	public static EvioDataBank fillCrossesBank(EvioDataEvent event, List<Cross> crosslist) {

		EvioDataBank bank =  (EvioDataBank) event.getDictionary().createBank("BSTRec::Crosses",crosslist.size());
    
		for(int i =0; i< crosslist.size(); i++) {
			bank.setInt("ID",i, crosslist.get(i).get_Id());
			bank.setInt("sector",i, crosslist.get(i).get_Sector());	
			bank.setInt("region", i, crosslist.get(i).get_Region());
			bank.setDouble("x", i, crosslist.get(i).get_Point().x());
			bank.setDouble("y", i, crosslist.get(i).get_Point().y());
			bank.setDouble("z", i, crosslist.get(i).get_Point().z());
			bank.setDouble("err_x", i, crosslist.get(i).get_PointErr().x());
			bank.setDouble("err_y", i, crosslist.get(i).get_PointErr().y());
			bank.setDouble("err_z", i, crosslist.get(i).get_PointErr().z());
			if( crosslist.get(i).get_Dir()!=null) {
				bank.setDouble("ux", i, crosslist.get(i).get_Dir().x());
				bank.setDouble("uy", i, crosslist.get(i).get_Dir().y());
				bank.setDouble("uz", i, crosslist.get(i).get_Dir().z());
			} else {
				bank.setDouble("ux", i, 0);
				bank.setDouble("uy", i, 0);
				bank.setDouble("uz", i, 0);
			}
			bank.setInt("Cluster1_ID", i, crosslist.get(i).get_Cluster1().get_Id());
			bank.setInt("Cluster2_ID", i, crosslist.get(i).get_Cluster2().get_Id());
		}
       
		return bank;
		
	}
	
	/**
	 * 
	 * @param seglist the reconstructed list of fitted segments in the event
	 * @return segments bank
	 */
	public static EvioDataBank fillTracksBank(EvioDataEvent event, List<Track> trkcands) {

		EvioDataBank bank =  (EvioDataBank) event.getDictionary().createBank("BSTRec::Tracks",trkcands.size());
    	
		int[] crossIdxArray= new int[4];

		for(int i =0; i<trkcands.size(); i++) {
			
			for(int j =0; j<crossIdxArray.length; j++) {
				crossIdxArray[j] = -1;
			}
			
			bank.setInt("fittingMethod",i, 1);
			bank.setInt("ID", i, trkcands.get(i).get_Id());
			
			bank.setInt("q", i, trkcands.get(i).get_Q());
			bank.setDouble("p", i, trkcands.get(i).get_P());
			bank.setDouble("pt", i, trkcands.get(i).get_Pt());
			Helix helix = trkcands.get(i).get_Helix();
			
			bank.setDouble("phi0", i, helix.get_phi_at_dca());
			bank.setDouble("tandip", i, helix.get_tandip());
			bank.setDouble("z0", i, helix.get_Z0());
			bank.setDouble("d0", i, helix.get_dca());
			
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
			
			
			for(int j = 0; j<trkcands.get(i).size(); j++) {		
				
				crossIdxArray[trkcands.get(i).get(j).get_Region()-1] = trkcands.get(i).get(j).get_Id();
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

	public static DataBank fillCosmicTracksBank(EvioDataEvent event,
			List<CosmicTrack> cosmics) {
		
		EvioDataBank bank =  (EvioDataBank) event.getDictionary().createBank("BSTRec::Cosmics",cosmics.size());
		int[] crossIdxArray= new int[8];
		
		for(int i =0; i< cosmics.size(); i++) {
			
			for(int j =0; j<crossIdxArray.length; j++) {
				crossIdxArray[j] = -1;
			}
			
			bank.setInt("ID", i, cosmics.get(i).getIdx());
			bank.setDouble("KF_chi2", i, cosmics.get(i).get_chi2());
			bank.setInt("KF_ndf", i, (int) cosmics.get(i).get_ndf());
			bank.setDouble("trkline_yx_slope", i, cosmics.get(i).get_yxslope());
			bank.setDouble("trkline_yx_interc", i, cosmics.get(i).get_yxinterc());
			bank.setDouble("trkline_yz_slope", i, cosmics.get(i).get_yzslope());
			bank.setDouble("trkline_yz_interc", i, cosmics.get(i).get_yzinterc());
			
			double norm = Math.sqrt(cosmics.get(i).get_yxslope()*cosmics.get(i).get_yxslope()+cosmics.get(i).get_yzslope()*cosmics.get(i).get_yzslope()+1);		    
		    threeVec u = new threeVec(cosmics.get(i).get_yxslope()/norm, 1/norm, cosmics.get(i).get_yzslope()/norm);
		    
		    bank.setDouble("theta", i, Math.toDegrees(u.theta()));
		    bank.setDouble("phi", i, Math.toDegrees(u.phi()));
		    
			for(int j = 0; j<cosmics.get(i).size(); j++) {		
				
				crossIdxArray[cosmics.get(i).get(j).getCosmicsRegion()-1] = cosmics.get(i).get(j).get_Id();
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
	
	public static DataBank fillTrajectoryBank(EvioDataEvent event,
			List<CosmicTrack> cosmics) {
		
		EvioDataBank bank =  (EvioDataBank) event.getDictionary().createBank("BSTRec::Trajectory",16*cosmics.size());
	
		int k =0;
		for(int i =0; i< cosmics.size(); i++) {
			if(cosmics.get(i).get_trackInterModulePlanes()==null)
				continue;
			
			double[][][] result = cosmics.get(i).get_trackInterModulePlanes();
			
			
			for(int l =0; l< Constants.NLAYR; l++) {
		    	for (int s = 0; s<Constants.NSECT[l]; s++) {
		    		
		    		if(result[l][s][0] != -999) {
		    			
		    			bank.setInt("ID", k, cosmics.get(i).getIdx());
		    			bank.setInt("LayerTrackIntersPlane", k, (l+1));
		    			bank.setInt("SectorTrackIntersPlane", k, (s+1));
						bank.setDouble("XtrackIntersPlane", k, result[l][s][0]);
						bank.setDouble("YtrackIntersPlane", k, result[l][s][1]);
						bank.setDouble("ZtrackIntersPlane", k, result[l][s][2]);
						bank.setDouble("PhiTrackIntersPlane", k, result[l][s][3]);
						bank.setDouble("ThetaTrackIntersPlane", k, result[l][s][4]);
						bank.setDouble("trkToMPlnAngl", k, result[l][s][5]);
						bank.setDouble("CalcCentroidStrip", k, result[l][s][6]);
						k++;
		    		}	
		    	}
			}
		}
    
	return bank;
	}
}