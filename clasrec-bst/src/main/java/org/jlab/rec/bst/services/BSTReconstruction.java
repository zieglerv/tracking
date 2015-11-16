package org.jlab.rec.bst.services;

import java.util.ArrayList;
import java.util.List;

import org.jlab.clasrec.main.DetectorReconstruction;
import org.jlab.clasrec.utils.ServiceConfiguration;
import org.jlab.data.io.DataBank;
import org.jlab.evio.clas12.EvioDataBank;
import org.jlab.evio.clas12.EvioDataEvent;
import org.jlab.rec.bst.banks.HitReader;
import org.jlab.rec.bst.banks.RecoBankWriter;
import org.jlab.rec.bst.Constants;
import org.jlab.rec.bst.Geometry;
import org.jlab.rec.bst.cluster.ClusterFinder;
import org.jlab.rec.bst.cluster.Cluster;
import org.jlab.rec.bst.cross.Cross;
import org.jlab.rec.bst.cross.CrossList;
import org.jlab.rec.bst.cross.CrossListFinder;
import org.jlab.rec.bst.cross.CrossMaker;
import org.jlab.rec.bst.hit.FittedHit;
import org.jlab.rec.bst.hit.Hit;
import org.jlab.rec.bst.track.CosmicTrack;
import org.jlab.rec.bst.track.Track;
import org.jlab.rec.bst.track.TrackCandListFinder;
import org.jlab.rec.bst.track.TrackListFinder;
import org.jlab.rec.bst.trajectory.BSTSwimmer;



/**
 * Service to return reconstructed BST track candidates- the output is in Evio format
 * 
 * @author ziegler
 *
 */

public class BSTReconstruction extends DetectorReconstruction{

   
    public BSTReconstruction() {
    	super("BST", "ziegler", "2.0");

    }
	
    Geometry geo = new Geometry();
	private int eventNb = 0;
	private int genTrackEff =0;
	private int recTrackEff =0;
	private static boolean debugMode = false;
	
	public void processEvent(EvioDataEvent event) {
		
		eventNb++;
		
		if(debugMode)
			System.out.println("Event Number = "+eventNb);
		List<FittedHit> fhits = new ArrayList<FittedHit>();
		List<Cluster> clusters = new ArrayList<Cluster>();
		List<Cross> crosses = new ArrayList<Cross>();
		List<Track> trkcands = new ArrayList<Track>();
		List<Track> trks = new ArrayList<Track>();
		List<CosmicTrack> cosmics = new ArrayList<CosmicTrack>();

		
		
		HitReader hitRead = new HitReader();
		hitRead.fetch_BSTHits(event);

		List<Hit> hits = new ArrayList<Hit>();
		//I) get the hits
		hits = hitRead.get_BSTHits();
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
		crosses = crossMake.findCrosses(clusters,geo);
		List<Cross> crossesToRm = crossMake.crossLooperCands(crosses);
		
		for(int j =0; j< crosses.size(); j++) {
			for(int j2 =0; j2< crossesToRm.size(); j2++) {
				if(crosses.get(j).get_Id()==crossesToRm.get(j2).get_Id())
					crosses.remove(j);
				
			}
		}
		
		if(debugMode)
			System.out.println("number of reconstructed crosses = "+ (crosses.size()+ crossesToRm.size()));
				
		
		if(crosses.size()==0 ) {
			// create the clusters and fitted hits banks
			DataBank bank1 = RecoBankWriter.fillHitsBank((EvioDataEvent) event, fhits);		
			DataBank bank2 = RecoBankWriter.fillClustersBank((EvioDataEvent) event, clusters);
			if(crossesToRm.size()==0)
				event.appendBanks(bank1,bank2);
			if(crossesToRm.size()!=0) {
				DataBank bank3 = RecoBankWriter.fillCrossesBank((EvioDataEvent) event, crossesToRm);
				event.appendBanks(bank1,bank2,bank3);
			}
			return; //exiting
		}
		
		//Find cross lists for Cosmics
		if(Constants.isCosmicsData == true) {
			//4) make list of crosses consistent with a track candidate
			CrossListFinder crossLister = new CrossListFinder();
			CrossList crosslist = crossLister.findCosmicsCandidateCrossLists(crosses, geo);
			
			
			if(crosslist.size()==0) {
				// create the clusters and fitted hits banks
				DataBank bank1 = RecoBankWriter.fillHitsBank((EvioDataEvent) event, fhits);		
				DataBank bank2 = RecoBankWriter.fillClustersBank((EvioDataEvent) event, clusters);				
				// found crosses 
				crosses.addAll(crossesToRm);
				DataBank bank3 = RecoBankWriter.fillCrossesBank((EvioDataEvent) event, crosses);
				if(debugMode)
					System.out.println("Saving crosses ... no track candidates found!");
				event.appendBanks(bank1,bank2,bank3);
				return;
			}
			if(debugMode)
				System.out.println("looking for trks from cross lists...."+ crosslist.size());
			
			//5) find the list of  track candidates
			TrackCandListFinder trkcandFinder = new TrackCandListFinder();
			cosmics = trkcandFinder.getCosmicsTracks(crosslist,geo,Constants.BSTEXCLUDEDFITREGION) ;
			
			
			if(cosmics.size()==0) {
				DataBank bank1 = RecoBankWriter.fillHitsBank((EvioDataEvent) event, fhits);		
				DataBank bank2 = RecoBankWriter.fillClustersBank((EvioDataEvent) event, clusters);				
				// found crosses 
				DataBank bank3 = RecoBankWriter.fillCrossesBank((EvioDataEvent) event, crosses);
				if(debugMode)
					System.out.println("Saving crosses ... no track candidates found!");
				event.appendBanks(bank1,bank2,bank3);
				return;
			}
				
			if(debugMode){ 
					System.out.println("number of reconstructed tracks = "+cosmics.size());
				
			}
				// create the clusters and fitted hits banks
			DataBank bank1 = RecoBankWriter.fillHitsBank((EvioDataEvent) event, fhits);		
			DataBank bank2 = RecoBankWriter.fillClustersBank((EvioDataEvent) event, clusters);				
			// found crosses 
			DataBank bank3 = RecoBankWriter.fillCrossesBank((EvioDataEvent) event, crosses);
			
			DataBank bank4 = RecoBankWriter.fillCosmicTracksBank((EvioDataEvent) event, cosmics);
			DataBank bank5 = RecoBankWriter.fillTrajectoryBank((EvioDataEvent) event, cosmics);
			
			//4)  ---  write out the banks			
			event.appendBanks(bank1,bank2,bank3, bank4, bank5);
			if(debugMode)
				System.out.println("    All Cosmic data banks saved !!!!");
		}
		if(Constants.isCosmicsData == false) {
			//4) make list of crosses consistent with a track candidate
			CrossListFinder crossLister = new CrossListFinder();
			CrossList crosslist = crossLister.findCandidateCrossLists(crosses);
			if(crosslist == null || crosslist.size()==0) {
				// create the clusters and fitted hits banks
				DataBank bank1 = RecoBankWriter.fillHitsBank((EvioDataEvent) event, fhits);		
				DataBank bank2 = RecoBankWriter.fillClustersBank((EvioDataEvent) event, clusters);				
				// found crosses 
				DataBank bank3 = RecoBankWriter.fillCrossesBank((EvioDataEvent) event, crosses);
				
				event.appendBanks(bank1,bank2,bank3);
				return;
			}
			if(debugMode)
				System.out.println("found "+ crosslist.size()+" candidates");
			//5) find the list of  track candidates
			TrackCandListFinder trkcandFinder = new TrackCandListFinder();
			trkcands = trkcandFinder.getTrackCands(crosslist,geo) ;
			
			
			
			if(trkcands.size()==0) {
				// create the clusters and fitted hits banks
				DataBank bank1 = RecoBankWriter.fillHitsBank((EvioDataEvent) event, fhits);		
				DataBank bank2 = RecoBankWriter.fillClustersBank((EvioDataEvent) event, clusters);				
				// found crosses 
				DataBank bank3 = RecoBankWriter.fillCrossesBank((EvioDataEvent) event, crosses);
				
				event.appendBanks(bank1,bank2,bank3);
				return;
			}
			
			
			//This last part does ELoss C
			TrackListFinder trkFinder = new TrackListFinder();
			trks = trkFinder.getTracks(trkcands, geo) ;
			
			
			
			// create the clusters and fitted hits banks
			DataBank bank1 = RecoBankWriter.fillHitsBank((EvioDataEvent) event, fhits);		
			DataBank bank2 = RecoBankWriter.fillClustersBank((EvioDataEvent) event, clusters);				
			// found crosses 
			DataBank bank3 = RecoBankWriter.fillCrossesBank((EvioDataEvent) event, crosses);
			//found tracks
			DataBank bank4 = RecoBankWriter.fillTracksBank((EvioDataEvent) event, trks);
			
			//4)  ---  write out the banks
			
			event.appendBanks(bank1,bank2,bank3,bank4);
			
			if(debugMode) {
				
				double[] p_rec = new double[trks.size()];	
				double[] theta_rec = new double[trks.size()];	
				double[] phi_rec = new double[trks.size()];	
				
			
				for(int i =0; i<trks.size(); i++) {
					
					p_rec[i] = trks.get(i).get_P();
					phi_rec[i] = trks.get(i).get_Helix().get_phi_at_dca();
					theta_rec[i] = Math.acos(trks.get(i).get_Helix().costheta());
				}
				
				if(event.hasBank("GenPart::true")==true) {
					EvioDataBank bankTRUE = (EvioDataBank) event.getBank("GenPart::true");
			       
			        double[] px = bankTRUE.getDouble("px");
			        double[] py = bankTRUE.getDouble("py");
			        double[] pz = bankTRUE.getDouble("pz");
			        int[] pid = bankTRUE.getInt("pid");
			       // double[] vx = bankTRUE.getDouble("vx");
			       // double[] vy = bankTRUE.getDouble("vy");
			       // double[] vz = bankTRUE.getDouble("vz");
			        
			        for(int i = 0; i<px.length; i++){
			        	double p = Math.sqrt(px[i]*px[i]+py[i]*py[i]+pz[i]*pz[i])/1000.;
			        	double phi = Math.toDegrees(Math.atan2(py[i],px[i]));
			        	double theta = Math.toDegrees(Math.acos((pz[i]/1000.)/p));
			        	if(theta<45 || theta>120 || pid[i]==22 || p<0.5)
			        		continue;
			        	genTrackEff++;
			        	
			        	System.out.println(" Gen p "+p+" phi "+phi+" theta "+theta+"     ==>> PID "+pid[i]);
			        	for(int j =0; j<p_rec.length; j++) {
			        		double pxrec = p_rec[j]*Math.sin(theta_rec[j])*Math.cos(phi_rec[j]);
			        		double pyrec = p_rec[j]*Math.sin(theta_rec[j])*Math.sin(phi_rec[j]);
			        		double pzrec = p_rec[j]*Math.cos(theta_rec[j]);
			        		
			        		double cosAng = (pxrec*px[i]+pyrec*py[i]+pzrec*pz[i])/(p_rec[j]*Math.sqrt(px[i]*px[i]+py[i]*py[i]+pz[i]*pz[i]));
			        		
			        		System.out.println(" Rec p "+p_rec[j]+" phi "+Math.toDegrees(phi_rec[j]) +" theta "+Math.toDegrees(theta_rec[j])+" angle "+
			        		cosAng);
			        		if(Math.abs((p_rec[j]-p)*100/p)<10  ) {
			        			recTrackEff++;
			        		}
			        		
			        	}
			        }
			        if(genTrackEff>0)
						System.out.println("Eff "+(100.*((float) recTrackEff/(float)genTrackEff)));
					
				}
			}
		}

		 
	}
	@Override
	public void init() {
		// Load the Constants
		if (Constants.areConstantsLoaded == false) {
			Constants.Load();
		}
		// Load the fields
		if (BSTSwimmer.areFieldsLoaded == false) {
			BSTSwimmer.getMagneticFields();
		}		
	}
	@Override
	
	public void configure(ServiceConfiguration config) {
		System.out.println(" CONFIGURING SERVICE BST ************************************** ");
		if(config.hasItem("SVT", "cosmics")) {
			String CosmicFlag = config.asString("SVT", "cosmics");
			boolean kFlag = Boolean.parseBoolean(CosmicFlag);
			Constants.isCosmicsData = kFlag;
			System.out.println("\n\n********** RUNNING COSMICS RECONSTRUCTION ? " + kFlag + "  *************");

		}
		if(config.hasItem("SVT", "excludeRegion")) {
			String ExReg = config.asString("SVT", "excludeRegion");
			int exR = Integer.parseInt(ExReg);
			Constants.BSTEXCLUDEDFITREGION = exR;
			System.out.println("\n\n********** Region excluded from fit " + exR + "  *************");

		}
		
		if(config.hasItem("MAG", "solenoid")) {
			Constants.FieldConfig="variable";
			String SolenoidScale = config.asString("MAG", "solenoid");
			double scale = Double.parseDouble(SolenoidScale);
			
			Constants.SOLSCALE = scale;

		}
		if(config.hasItem("MAG", "fields")) {
			String FieldsConf = config.asString("BST", "fields");
			Constants.FieldConfig = FieldsConf;
			
		}
		
	}
	

	
}
