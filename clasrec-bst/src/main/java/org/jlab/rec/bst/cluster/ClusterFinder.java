package org.jlab.rec.bst.cluster;


import java.util.ArrayList;
import java.util.List;

import org.jlab.rec.bst.Constants;
import org.jlab.rec.bst.hit.FittedHit;
import org.jlab.rec.bst.hit.Hit;


/**
 *
 * 
 * */

public class ClusterFinder  { 
	
	public ClusterFinder() {
		
	}
	
	// cluster finding algorithm
	// the loop is done over sectors 

	int[] nsectors = Constants.NSECT;
	
	int nlayr = Constants.NLAYR;
	int nstrip = Constants.NSTRIP;
	
	Hit[][][] HitArray;
	public ArrayList<Cluster> findClusters(List<Hit> hits2)
	{
		ArrayList<Cluster> clusters = new ArrayList<Cluster>();
		
		// a Hit Array is used to identify clusters
		// last layer is the one with max number of sectors
		HitArray = new Hit[nstrip][nlayr][nsectors[nlayr-1]] ;
		
		// initializing non-zero Hit Array entries
		// with valid hits
		for(Hit hit : hits2) {
			
			if(hit.get_Edep()<Constants.edep_min) 
				continue;
			
			int w = hit.get_Strip();
			int l = hit.get_Layer();
			int s = hit.get_Sector();
			
			if(w>0 && w<=nstrip && l>0 && l<=nlayr)	{	
				if(s>0 && s<=nsectors[l-1]) {
					HitArray[w-1][l-1][s-1] = hit;
				}
				
			}
			
		}
		int cid = 1;  // cluster id, will increment with each new good cluster
		
		// for each layer and sector, a loop over the strips
		// is done to define clusters in that module's layer
		// clusters are delimited by strips with no hits 
		for(int l=0; l<nlayr; l++)
		{
			for(int s=0; s<nsectors[l]; s++)
			{			
				int si  = 0;  // strip index in the loop
				
				// looping over all strips
				while(si<nstrip)
				{
					// if there's a hit, it's a cluster candidate
					if(HitArray[si][l][s] != null)
					{
						// vector of hits in the cluster candidate
						ArrayList<FittedHit> hits = new ArrayList<FittedHit>();
						
						// adding all hits in this and all the subsequent
						// strip until there's a strip with no hit
						
						try {
							while(HitArray[si][l][s] != null  && si<nstrip)
							{   
								FittedHit clusteredHit = new FittedHit(HitArray[si][l][s].get_Sector(),HitArray[si][l][s].get_Layer(),HitArray[si][l][s].get_Strip(),HitArray[si][l][s].get_Edep());
								clusteredHit.set_Id(HitArray[si][l][s].get_Id());
								hits.add(clusteredHit);
								si++;
							}
						}
						catch(ArrayIndexOutOfBoundsException exception) {
						    continue;
						}	
						// define new cluster 
						Cluster this_cluster = new Cluster(s+1, l+1, cid++); 
						
						
						// add hits to the cluster
						this_cluster.addAll(hits);
						this_cluster.calc_Centroid();
						this_cluster.calc_TotalEnergy();
						this_cluster.set_Parameters();
						//make arraylist
						
						for(FittedHit hit : this_cluster) {
							hit.set_AssociatedClusterID(this_cluster.get_Id());
						}
						clusters.add(this_cluster);
						
					}
					// if no hits, check for next wire coordinate
					si++;
				}
			}
		}
       
      
		return clusters;
		
	}
	


}
