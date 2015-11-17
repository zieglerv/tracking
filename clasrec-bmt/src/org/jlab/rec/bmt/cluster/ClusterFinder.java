package org.jlab.rec.bmt.cluster;


import java.util.ArrayList;
import java.util.List;

import org.jlab.rec.bmt.Constants;
import org.jlab.rec.bmt.hit.FittedHit;
import org.jlab.rec.bmt.hit.Hit;


/**
 *
 * 
 * */

public class ClusterFinder  { 
	
	public ClusterFinder() {
		
	}
	
	// cluster finding algorithm
	// the loop is done over sectors 

	
	Hit[][] HitArray;
	int nstrip = Constants.CRCNSTRIPS[2]; // max number of strips
	int nlayr = Constants.NREGIONS*2;
	
	public ArrayList<Cluster> findClusters(List<Hit> hits2) // the number of strips depends on the layer 
	{
		ArrayList<Cluster> clusters = new ArrayList<Cluster>();
		
		// a Hit Array is used to identify clusters		
		HitArray = new Hit[nstrip][nlayr] ;
		
		// initializing non-zero Hit Array entries
		// with valid hits
		for(Hit hit : hits2) {
			
			if(hit.get_Strip()==-1) 
				continue;
			
			int w = hit.get_Strip();
			int l = hit.get_Layer();
			
			if(w>0 && w<nstrip)	{						
				HitArray[w-1][l-1] = hit;
			}
			
		}
		int cid = 1;  // cluster id, will increment with each new good cluster
		
		// for each layer and sector, a loop over the strips
		// is done to define clusters in that module's layer
		// clusters are delimited by strips with no hits 
		for(int l=0; l<nlayr; l++)
		{		
			int si  = 0;  // strip index in the loop
			
			// looping over all strips
			while(si<nstrip)
			{
				// if there's a hit, it's a cluster candidate
				if(HitArray[si][l] != null)
				{
					// vector of hits in the cluster candidate
					ArrayList<FittedHit> hits = new ArrayList<FittedHit>();
					
					// adding all hits in this and all the subsequent
					// strip until there's a strip with no hit
					while(HitArray[si][l] != null  && si<nstrip)
					{
						hits.add(new FittedHit(HitArray[si][l].get_Sector(),HitArray[si][l].get_Layer(),HitArray[si][l].get_Strip(),HitArray[si][l].get_Edep()));
						si++;
					}
					
					// define new cluster 
					Cluster this_cluster = new Cluster(1, l+1, cid++); 
					
					
					// add hits to the cluster
					this_cluster.addAll(hits);
					//this_cluster.calc_Centroid();
					//this_cluster.calc_TotalEnergy();
					this_cluster.set_Parameters();
					//make arraylist
					clusters.add(this_cluster);
					
				}
				// if no hits, check for next wire coordinate
					si++;
			}
		}
       
      
		return clusters;
		
	}
	


}
