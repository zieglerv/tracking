package org.jlab.rec.fmt.services;

import java.util.ArrayList;
import java.util.List;

import org.jlab.clasrec.main.DetectorReconstruction;
import org.jlab.clasrec.utils.ServiceConfiguration;
import org.jlab.data.io.DataBank;
import org.jlab.evio.clas12.EvioDataEvent;
import org.jlab.rec.fmt.banks.HitReader;
import org.jlab.rec.fmt.banks.RecoBankWriter;
import org.jlab.rec.fmt.Constants;
import org.jlab.rec.fmt.cluster.ClusterFinder;
import org.jlab.rec.fmt.cluster.Cluster;
import org.jlab.rec.fmt.cross.Cross;
import org.jlab.rec.fmt.cross.CrossList;
import org.jlab.rec.fmt.cross.CrossListFinder;
import org.jlab.rec.fmt.cross.CrossMaker;
import org.jlab.rec.fmt.hit.FittedHit;
import org.jlab.rec.fmt.hit.Hit;



/**
 * Service to return reconstructed fmt track candidates- the output is in Evio format
 * 
 * @author ziegler
 *
 */

public class FMTReconstruction extends DetectorReconstruction{

   
    public FMTReconstruction() {
    	super("FMT", "ziegler", "1.0");
	

    }
	
    
	private int eventNb = 0;
	private static boolean debugMode = true;
	
	public void processEvent(EvioDataEvent event) {
		//if(eventNb > 70)
		//	return;
		eventNb++;
		
		if(debugMode)
			System.out.println("Event Number = "+eventNb);
		List<FittedHit> fhits = new ArrayList<FittedHit>();
		List<Cluster> clusters = new ArrayList<Cluster>();
		List<Cross> crosses = new ArrayList<Cross>();
		HitReader hitRead = new HitReader();
		hitRead.fetch_FMTHits(event);

		List<Hit> hits = new ArrayList<Hit>();
		//I) get the hits
		hits = hitRead.get_FMTHits();
		if(debugMode)
			System.out.println("number of reconstructed hit = "+hits.size());
		//II) process the hits
		
		//1) exit if hit list is empty
		if(hits.size()==0 ) {
			return;
		}
		
		//2) find the clusters from these hits
		ClusterFinder clusFinder = new ClusterFinder();
		clusters = clusFinder.findClusters(hits);
		if(debugMode)
			System.out.println("number of reconstructed clusters = "+clusters.size());
		if(clusters.size()==0) {
			return;
		}
		
		// fill the fitted hits list.
		if(clusters.size()!=0) {   			
   			for(int i = 0; i<clusters.size(); i++) {
   				fhits.addAll(clusters.get(i));
   			}
		}
		
		
		
		//3) find the crosses
		CrossMaker crossMake = new CrossMaker();
		crosses = crossMake.findCrosses(clusters);
		if(debugMode)
			System.out.println("number of reconstructed crosses = "+crosses.size());
				
		
		if(crosses.size()==0 || crosses.size()>Constants.MAX_NB_CROSSES) {
			// create the clusters and fitted hits banks
			DataBank bank1 = RecoBankWriter.fillHitsBank((EvioDataEvent) event, fhits);		
			DataBank bank2 = RecoBankWriter.fillClustersBank((EvioDataEvent) event, clusters);
			
			event.appendBanks(bank1,bank2);
			return; //exiting
		}
		
		// create the clusters and fitted hits banks
			DataBank bank1 = RecoBankWriter.fillHitsBank((EvioDataEvent) event, fhits);		
			DataBank bank2 = RecoBankWriter.fillClustersBank((EvioDataEvent) event, clusters);
				
		//4) make list of crosses consistent with a track candidate
		CrossListFinder crossLister = new CrossListFinder();
		CrossList crosslist = crossLister.findCandidateCrossLists(crosses);
		if(crosslist.size()==0) {
			// found crosses 
			DataBank bank3 = RecoBankWriter.fillCrossesBank((EvioDataEvent) event, crosses);
		
			event.appendBanks(bank1,bank2,bank3);
			return;
		}
		
		// found crosses 
		DataBank bank3 = RecoBankWriter.fillCrossesBank((EvioDataEvent) event, crosses);
			
		event.appendBanks(bank1,bank2,bank3);
		 
	}
	@Override
	public void init() {
		// Load the Constants
		if (Constants.areConstantsLoaded == false) {
			Constants.Load();
		}
				
	}
	@Override
	
	public void configure(ServiceConfiguration config) {
		System.out.println(" CONFIGURING SERVICE FMT ************************************** ");
		
		if(config.hasItem("MAG", "solenoid")) {
			Constants.FieldConfig="variable";
			String SolenoidScale = config.asString("MAG", "solenoid");
			double scale = Double.parseDouble(SolenoidScale);
			
			Constants.SOLSCALE = scale;

		}
		if(config.hasItem("MAG", "fields")) {
			String FieldsConf = config.asString("MAG", "fields");
			Constants.FieldConfig = FieldsConf;
			
		}
		
	}
	

	
}
