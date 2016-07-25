package org.jlab.rec.cvt.services;

import java.util.ArrayList;
import java.util.List;

import org.jlab.clasrec.main.DetectorReconstruction;
import org.jlab.clasrec.utils.ServiceConfiguration;
import org.jlab.evio.clas12.EvioDataEvent;
import org.jlab.rec.cvt.Constants;
import org.jlab.rec.cvt.banks.HitReader;
import org.jlab.rec.cvt.banks.RecoBankWriter;
import org.jlab.rec.cvt.cluster.Cluster;
import org.jlab.rec.cvt.cluster.ClusterFinder;
import org.jlab.rec.cvt.cross.Cross;
import org.jlab.rec.cvt.cross.CrossList;
import org.jlab.rec.cvt.cross.CrossMaker;
import org.jlab.rec.cvt.cross.HelixCrossListFinder;
import org.jlab.rec.cvt.hit.ADCConvertor;
import org.jlab.rec.cvt.hit.FittedHit;
import org.jlab.rec.cvt.hit.Hit;
import org.jlab.rec.cvt.track.Track;
import org.jlab.rec.cvt.track.TrackCandListFinder;
import org.jlab.rec.cvt.track.TrackListFinder;
import org.jlab.rec.cvt.trajectory.TrkSwimmer;

/**
 * Service to return reconstructed BST track candidates- the output is in Evio format
 * 
 * @author ziegler
 *
 */

public class CVTReconstruction extends DetectorReconstruction{

   
    public CVTReconstruction() {
    	super("CVT", "ziegler", "2.0");
    }
   
	
    org.jlab.rec.cvt.bmt.Geometry BMTGeom = new org.jlab.rec.cvt.bmt.Geometry();
    org.jlab.rec.cvt.svt.Geometry SVTGeom = new org.jlab.rec.cvt.svt.Geometry();
    
	private int eventNb = 0;

	private ADCConvertor adcConv = new ADCConvertor();
	public void processEvent(EvioDataEvent event) {
		eventNb++;
		if(org.jlab.rec.cvt.Constants.DEBUGMODE)
			System.out.println("Event Number = "+eventNb);
		
		HitReader hitRead = new HitReader();
		hitRead.fetch_SVTHits(event,adcConv,-1,-1);
		hitRead.fetch_BMTHits(event, adcConv, BMTGeom);
		
		List<Hit> hits = new ArrayList<Hit>();
		//I) get the hits
		List<Hit>  svt_hits = hitRead.get_SVTHits();
		if(svt_hits.size()>0)
			hits.addAll(svt_hits);
		
		List<Hit>  bmt_hits = hitRead.get_BMTHits();
		if(bmt_hits.size()>0)
			hits.addAll(bmt_hits);
		
		if(org.jlab.rec.cvt.Constants.DEBUGMODE)
			System.out.println("number of reconstructed SVT hits = "+svt_hits.size()+" BMT hits "+bmt_hits.size());
		
		
		//II) process the hits		
		List<FittedHit> SVThits = new ArrayList<FittedHit>();
		List<FittedHit> BMThits = new ArrayList<FittedHit>();
		//1) exit if hit list is empty
		if(hits.size()==0 ) {
			return;
		}
		
		List<Cluster> clusters = new ArrayList<Cluster>();
		List<Cluster> SVTclusters = new ArrayList<Cluster>();
		List<Cluster> BMTclusters = new ArrayList<Cluster>();
		
		//2) find the clusters from these hits
		ClusterFinder clusFinder = new ClusterFinder();
		clusters = clusFinder.findClusters(hits);
		if(org.jlab.rec.cvt.Constants.DEBUGMODE)
			System.out.println("number of reconstructed clusters = "+clusters.size());
		if(clusters.size()==0) {
			return;
		}
		
		// fill the fitted hits list.
		if(clusters.size()!=0) {   			
   			for(int i = 0; i<clusters.size(); i++) {
   				if(clusters.get(i).get_Detector()=="SVT") {
   					SVTclusters.add(clusters.get(i));
   					SVThits.addAll(clusters.get(i));
   				}
   				if(clusters.get(i).get_Detector()=="BMT") {
   					BMTclusters.add(clusters.get(i));
   					BMThits.addAll(clusters.get(i));
   				}
   			}
		}
		List<ArrayList<Cross>> crosses = new ArrayList<ArrayList<Cross>>();
		
		//3) find the crosses
		CrossMaker crossMake = new CrossMaker();

		crosses = crossMake.findCrosses(clusters,SVTGeom);
		if(org.jlab.rec.cvt.Constants.DEBUGMODE)
			System.out.println("number of reconstructed svt crosses before looper rejection = "+ (crosses.get(0).size()));
		
		if(clusters.size()==0 ) {
			
			return; //exiting
		}
		//clean up svt crosses
		List<Cross> crossesToRm = crossMake.crossLooperCands(crosses);			
		crosses.get(0).removeAll(crossesToRm);
		
		if(org.jlab.rec.cvt.Constants.DEBUGMODE)
			System.out.println("number of reconstructed svt crosses after looper rejection = "+ (crosses.get(0).size()));
		if(crosses.size()==0) {
			// create the clusters and fitted hits banks
			RecoBankWriter.appendCVTBanks((EvioDataEvent) event, SVThits, BMThits, SVTclusters, BMTclusters, null, null);
			
			return; //exiting
		}
		// if the looper finder kills all svt crosses save all crosses anyway
		if(crosses.get(0).size()==0) {
			List<ArrayList<Cross>> crosses2 = new ArrayList<ArrayList<Cross>>();
			crosses2.add(0,(ArrayList<Cross>) crossesToRm);
			crosses2.add(1, crosses.get(1));
			RecoBankWriter.appendCVTBanks((EvioDataEvent) event, SVThits, BMThits, SVTclusters, BMTclusters, crosses2, null);
			
			return;
		}
		//Find cross lists for Helix
		//4) make list of crosses consistent with a track candidate
		HelixCrossListFinder crossLister = new HelixCrossListFinder();
		CrossList crosslist = crossLister.findCandidateCrossLists(crosses);
		
		if(crosslist.size()==0) {
			RecoBankWriter.appendCVTBanks((EvioDataEvent) event, SVThits, BMThits, SVTclusters, BMTclusters, crosses, null);
			
			return;
		}
		if(org.jlab.rec.cvt.Constants.DEBUGMODE)
			System.out.println("looking for trks from cross lists...."+ crosslist.size());

		//5) find the list of  track candidates
		TrackCandListFinder trkcandFinder = new TrackCandListFinder();
		List<Track> trkcands = new ArrayList<Track>();
		
		trkcands = trkcandFinder.getHelicalTrack(crosslist, SVTGeom, BMTGeom); 
			
		if(trkcands.size()==0) {
			// create the clusters and fitted hits banks
			RecoBankWriter.appendCVTBanks((EvioDataEvent) event, SVThits, BMThits, SVTclusters, BMTclusters, crosses, null);
			if(org.jlab.rec.cvt.Constants.DEBUGMODE)
				System.out.println("Saving crosses ... no track candidates found!");
			
			return;
		}
		
		
		//This last part does ELoss C
		TrackListFinder trkFinder = new TrackListFinder();
		List<Track> trks = new ArrayList<Track>();
		trks = trkFinder.getTracks(trkcands, SVTGeom) ;
		if(trks.size()>0) {
			RecoBankWriter.appendCVTBanks((EvioDataEvent) event, SVThits, BMThits, SVTclusters, BMTclusters, crosses, trks);
			
			if(org.jlab.rec.cvt.Constants.DEBUGMODE)
				System.out.println("Saving tracks !");
		}
		
		
		
	}
	@Override
	public void init() {
		// Load the Constants
		if (Constants.areConstantsLoaded == false) {
			Constants.Load();
		}
		// Load the fields
		if (TrkSwimmer.areFieldsLoaded == false) {
			TrkSwimmer.getMagneticFields();
		}		
	}
	@Override
	
	public void configure(ServiceConfiguration config) {
		System.out.println(" CONFIGURING SERVICE CVT ************************************** ");
		if(config.hasItem("SVT", "OrigConstraint")) {
			String OFlag = config.asString("SVT", "OrigConstraint");
			boolean kFlag = Boolean.parseBoolean(OFlag);
			Constants.trk_comesfrmOrig = kFlag;
			System.out.println("\n\n********** Track constrained to come from Origin ? " + kFlag + "  *************");

		}
		if(config.hasItem("SVT", "newGeometry")) {
			String CosmicFlag = config.asString("SVT", "newGeometry");
			boolean kFlag = Boolean.parseBoolean(CosmicFlag);
			org.jlab.rec.cvt.svt.Constants.newGeometry = kFlag;
			System.out.println("\n\n********** New Geometry ? " + kFlag + "  *************");

		}
		if(config.hasItem("SVT", "excludeRegion")) {
			String ExReg = config.asString("SVT", "excludeRegion");
			int exR = Integer.parseInt(ExReg);
			org.jlab.rec.cvt.svt.Constants.BSTEXCLUDEDFITREGION = exR;
			System.out.println("\n\n********** Region excluded from fit " + exR + "  *************");

		}
		
		if(config.hasItem("MAG", "solenoid")) {
			
			String SolenoidScale = config.asString("MAG", "solenoid");
			double scale = Double.parseDouble(SolenoidScale);
			
			Constants.SOLSCALE = scale;

		}
		if(config.hasItem("CVT", "SVT")) {
			String SVTonly = config.asString("CVT", "SVT");
			boolean kFlag = Boolean.parseBoolean(SVTonly);
			org.jlab.rec.cvt.Constants.SVTOnly = kFlag;
			System.out.println("\n\n********** SVT Only " + kFlag + "  *************");
		}
		if(config.hasItem("CVT", "debug")) {
			String DB = config.asString("CVT", "debug");
			boolean kFlag = Boolean.parseBoolean(DB);
			org.jlab.rec.cvt.Constants.DEBUGMODE = kFlag;
		}
		if(config.hasItem("SVT", "LayerEffs")) {
			String DB = config.asString("SVT", "LayerEffs");
			boolean kFlag = Boolean.parseBoolean(DB);
			org.jlab.rec.cvt.svt.Constants.LAYEREFFS= kFlag;
		}
	}
	

	
}
