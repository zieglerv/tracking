package org.jlab.rec.fmt.banks;

import java.util.List;


import org.jlab.evio.clas12.EvioDataBank;
import org.jlab.evio.clas12.EvioDataEvent;
import org.jlab.rec.fmt.cluster.Cluster;
import org.jlab.rec.fmt.cross.Cross;
import org.jlab.rec.fmt.hit.FittedHit;

public class RecoBankWriter {

	/**
	 * 
	 * @param hitlist the list of DC hits that are of the type FittedHit.  If the hit has not been fitted, the fitted hit fields are left to
	 * their default values.
	 * @return hits bank
	 *
	 */
	public static EvioDataBank fillHitsBank(EvioDataEvent event, List<FittedHit> hitlist) {

		EvioDataBank bank = (EvioDataBank)
		          event.getDictionary().createBank("FMTRec::Hits", hitlist.size());

		for(int i =0; i< hitlist.size(); i++) {
			bank.setInt("layer",i, hitlist.get(i).get_Layer());
			bank.setInt("sector",i, hitlist.get(i).get_Sector());
			bank.setInt("strip",i, hitlist.get(i).get_Strip());
			bank.setInt("clusterID", i, hitlist.get(i).get_AssociatedClusterID());
			bank.setInt("crossID", i, hitlist.get(i).get_AssociatedCrossID());
			bank.setInt("trackID", i, hitlist.get(i).get_AssociatedTrackID());
          
		}

		return bank;

	}

	/**
	 * 
	 * @param cluslist the reconstructed list of fitted clusters in the event
	 * @return clusters bank
	 */
	public static EvioDataBank fillClustersBank(EvioDataEvent event, List<Cluster> cluslist) {

		EvioDataBank bank =  (EvioDataBank) event.getDictionary().createBank("FMTRec::Clusters",cluslist.size());
    
		for(int i =0; i< cluslist.size(); i++) {
			bank.setInt("ID",i, cluslist.get(i).get_Id());
			bank.setInt("sector",i, cluslist.get(i).get_Sector());	
			bank.setInt("layer",i, cluslist.get(i).get_Layer());		
			bank.setInt("size", i, cluslist.get(i).size());
			bank.setDouble("ETot", i, cluslist.get(i).get_TotalEnergy());
			bank.setDouble("seedE", i, cluslist.get(i).get_SeedEnergy());
			bank.setInt("crossID", i, cluslist.get(i).get_AssociatedCrossID());
			bank.setInt("trackID", i, cluslist.get(i).get_AssociatedTrackID());

		}
		//System.out.println("********************** Filled Cluster Bank ************************");
		return bank;
		
	}
	

	/**
	 * 
	 * @param crosslist the reconstructed list of crosses in the event
	 * @return crosses bank
	 */
	public static EvioDataBank fillCrossesBank(EvioDataEvent event, List<Cross> crosslist) {

		EvioDataBank bank =  (EvioDataBank) event.getDictionary().createBank("FMTRec::Crosses",crosslist.size());
		
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
			bank.setInt("trackID", i, crosslist.get(i).get_AssociatedTrackID());

		}
		//System.out.println("********************** Filled Crosses Bank ************************");
		return bank;
		
	}
	


	
}
