package org.jlab.rec.dc.services;

import java.util.ArrayList;
import java.util.List;

import org.jlab.evio.clas12.EvioDataBank;
import org.jlab.evio.clas12.EvioDataEvent;
import org.jlab.rec.dc.cluster.Cluster;
import org.jlab.rec.dc.cluster.ClusterCleanerUtilities;
import org.jlab.rec.dc.cluster.ClusterFitter;
import org.jlab.rec.dc.cluster.FittedCluster;
import org.jlab.rec.dc.hit.Hit;

public class Calibration {

	public Calibration() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param allhits the list of unfitted hits
	 * @return layer efficiencies
	 * .....
	 */
	public EvioDataBank getLayerEfficiencies(List<Hit> allhits, ClusterCleanerUtilities ct, EvioDataEvent event, ClusterFitter cf) {

		int[][][] EffArray = new int[6][6][6]; //6 sectors,  6 superlayers, 6 layers
		for(int i=0; i<6; i++)
			for(int j=0; j<6; j++)
				for(int k=0; k<6; k++)
					EffArray[i][j][k]=-1;
		
		for(int rejLy=1; rejLy<=6; rejLy++) {
			
			//fill array of hit
			this.fillHitArray(allhits, rejLy);		
			//find clumps of hits
			List<Cluster> clusters = this.findClumps(allhits, ct);
			// create cluster list to be fitted
			List<FittedCluster> selectedClusList =  new ArrayList<FittedCluster>();
	
			for(Cluster clus : clusters) {
				//System.out.println(" I passed this cluster "+clus.printInfo());
				FittedCluster fclus = new FittedCluster(clus);			
				selectedClusList.add(fclus);
				
			}
	
			
			for(FittedCluster clus : selectedClusList) {
				if(clus!=null) {
					
					int status = 0;
					//fit
					cf.SetFitArray(clus, "TSC");
		            cf.Fit(clus);
		            cf.SetClusterLineParameters(clus.get(0).get_X(), clus) ;
					cf.SetResidualDerivedParams(clus, false, false); //calcTimeResidual=false, resetLRAmbig=false
					
					for(Hit hit : allhits) {
						
						if(hit.get_Sector()!=clus.get_Sector() || hit.get_Superlayer()!=clus.get_Superlayer() || hit.get_Layer()!=rejLy)
							continue;
						
						double locX = hit.calcLocY(hit.get_Layer(), hit.get_Wire());
						double locZ = hit.get_Layer();
						
						double calc_doca = Math.abs(locX-clus.get_clusterLineFitSlope()*locZ-clus.get_clusterLineFitIntercept());
						
						if(calc_doca<2)
							status =1; //found a hit close enough to the track to assume that the layer is live
						
						int sec = clus.get_Sector()-1;
						int slay = clus.get_Superlayer()-1;
						int lay = rejLy -1;
						
						EffArray[sec][slay][lay] = status;
						
					}
				}
			}
		}
		// now fill the bank
		int bankSize =6*6*6;
		EvioDataBank bank =  (EvioDataBank) event.getDictionary().createBank("HitBasedTrkg::LayerEffs",bankSize);
		int bankEntry = 0;
		for(int i=0; i<6; i++)
			for(int j=0; j<6; j++)
				for(int k=0; k<6; k++) {
					bank.setInt("sector",bankEntry, i+1);
					bank.setInt("superlayer",bankEntry, j+1);
					bank.setInt("layer",bankEntry, k+1);
					bank.setInt("status", bankEntry,EffArray[i][j][k]);
					bankEntry++;
				}
		return bank;
		
	}
}
