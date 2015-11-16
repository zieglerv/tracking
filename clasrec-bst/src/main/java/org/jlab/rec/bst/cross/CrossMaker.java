package org.jlab.rec.bst.cross;

import java.util.ArrayList;
import java.util.List;

import org.jlab.rec.bst.Constants;
import org.jlab.rec.bst.Geometry;
import org.jlab.rec.bst.cluster.Cluster;

/**
 * Driver class to make BST crosses
 * @author ziegler
 *
 */

public class CrossMaker {
	
	public CrossMaker() {
		
	}
	
	public ArrayList<Cross> findCrosses(List<Cluster> clusters, Geometry geo) {

		// first separate the segments according to layers
		ArrayList<Cluster> allinnerlayrclus = new ArrayList<Cluster>();
		ArrayList<Cluster> allouterlayrclus = new ArrayList<Cluster>();

		// Sorting by layer first:
		for (Cluster theclus : clusters){
			if(theclus.get_Layer()%2==0) { 
				allouterlayrclus.add(theclus); 
			} 
			if(theclus.get_Layer()%2==1) { 
				allinnerlayrclus.add(theclus);
			}
		}

		ArrayList<Cross> crosses = new ArrayList<Cross>();

		int rid =0;
		for(Cluster inlayerclus : allinnerlayrclus){
			for(Cluster outlayerclus : allouterlayrclus){
				if(outlayerclus.get_Layer()-inlayerclus.get_Layer()!=1)
					continue;
				if(outlayerclus.get_Sector()!=inlayerclus.get_Sector())
					continue;
				
				if( (inlayerclus.get_MinStrip()+outlayerclus.get_MinStrip() > Constants.sumStpNumMin) 
						&& (inlayerclus.get_MaxStrip()+outlayerclus.get_MaxStrip() < Constants.sumStpNumMax) ) { // the intersection is valid
					
					// define new cross 
					Cross this_cross = new Cross(inlayerclus.get_Sector(), inlayerclus.get_Region(),rid++);
					this_cross.set_Cluster1(inlayerclus);
					this_cross.set_Cluster2(outlayerclus);
					this_cross.set_Id(rid);
					this_cross.set_CrossParams(null, geo);
					if(this_cross.get_Point0()!=null)
					//make arraylist
						crosses.add(this_cross);
				}
			}
		}
		return crosses;
	}

	public List<Cross> crossLooperCands(List<Cross> crosses) {
		
		int nlayr = Constants.NLAYR;
		
		ArrayList<ArrayList<ArrayList<Cross>>> secList = new ArrayList<ArrayList<ArrayList<Cross>>>();
		
		//initialize
		for(int i = 0; i< nlayr; i++) {
			secList.add(i, new ArrayList<ArrayList<Cross>>());	
			for(int j = 0; j<Constants.NSECT[i]; j++) {
				secList.get(i).add(j,new ArrayList<Cross>());
			}
		}
		
		
		for(Cross c : crosses) {
					
			int l = c.get_Region()*2;
			int s = c.get_Sector();
			
			secList.get(l-1).get(s-1).add(c);
		}
		ArrayList<Cross> listOfCrossesToRm = new ArrayList<Cross>();
		
		for(int i = 0; i< nlayr; i++) {			
			for(int j = 0; j<Constants.NSECT[i]; j++) {
				
				if(secList.get(i).get(j).size()>Constants.MAXNUMCROSSESINMODULE)
					listOfCrossesToRm.addAll(secList.get(i).get(j));
			}
			
		}
		
		return listOfCrossesToRm;
		
	}
	
	
}


