package org.jlab.rec.bmt.banks;

import java.util.List;

import org.jlab.evio.clas12.EvioDataBank;
import org.jlab.evio.clas12.EvioDataEvent;
import org.jlab.rec.bmt.cluster.Cluster;
import org.jlab.rec.bmt.cross.Cross;
import org.jlab.rec.bmt.hit.FittedHit;

public class RecoBankWriter {

	/**
	 * 
	 * @param hitlist the list of DC hits that are of the type FittedHit.  If the hit has not been fitted, the fitted hit fields are left to
	 * their default values.
	 * @return hits bank
	 *
	 */
	/**
	 * 
	 * @param hitlist the list of hits that are of the type FittedHit.  If the hit has not been fitted, the fitted hit fields are left to
	 * their default values.
	 * @return hits bank
	 *
	 */
	public static EvioDataBank fillHitsBank(EvioDataEvent event, List<FittedHit> hitlist) {

		EvioDataBank bank = (EvioDataBank)
		          event.getDictionary().createBank("BMTRec::Hits", hitlist.size());

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
	 * @param crosslist the reconstructed list of crosses in the event
	 * @return crosses bank
	 */
	public static EvioDataBank fillCrossesBank(EvioDataEvent event, List<Cross> crosslist) {

		EvioDataBank bank =  (EvioDataBank) event.getDictionary().createBank("BMTRec::Crosses",crosslist.size());
    
		for(int i =0; i< crosslist.size(); i++) {
			System.out.println(" fill cross "+crosslist.get(i).printInfo());
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
	

}