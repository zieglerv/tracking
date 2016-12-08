package org.jlab.rec.cvt.services;

import java.util.ArrayList;
import java.util.List;

import org.jlab.clasrec.main.DetectorReconstruction;
import org.jlab.clasrec.utils.ServiceConfiguration;
import org.jlab.evio.clas12.EvioDataBank;
import org.jlab.evio.clas12.EvioDataEvent;
import org.jlab.geom.prim.Point3D;
import org.jlab.rec.cvt.Constants;
import org.jlab.rec.cvt.banks.HitReader;
//import org.jlab.rec.cvt.banks.RecoBankWriter;
import org.jlab.rec.cvt.cluster.Cluster;
import org.jlab.rec.cvt.cluster.ClusterFinder;
import org.jlab.rec.cvt.cross.Cross;
import org.jlab.rec.cvt.cross.CrossList;
import org.jlab.rec.cvt.cross.CrossMaker;
import org.jlab.rec.cvt.cross.StraightTrackCrossListFinder;
import org.jlab.rec.cvt.hit.ADCConvertor;
import org.jlab.rec.cvt.hit.FittedHit;
import org.jlab.rec.cvt.hit.Hit;
import org.jlab.rec.cvt.track.StraightTrack;
import org.jlab.rec.cvt.track.TrackCandListFinder;
import org.jlab.rec.cvt.trajectory.StateVec;
import org.jlab.rec.cvt.trajectory.Trajectory;
import org.jlab.rec.cvt.trajectory.TrkSwimmer;

/**
 * Service to return reconstructed BST track candidates- the output is in Evio format
 * 
 * @author ziegler
 *
 */

public class CVTCosmicsEffAnal extends DetectorReconstruction{

   
    public CVTCosmicsEffAnal() {
    	super("CVTCosmicsEff", "ziegler", "1.0");
    }
   
	
    org.jlab.rec.cvt.bmt.Geometry BMTGeom = new org.jlab.rec.cvt.bmt.Geometry();
    org.jlab.rec.cvt.svt.Geometry SVTGeom = new org.jlab.rec.cvt.svt.Geometry();
    
	private int eventNb = 0;

	private ADCConvertor adcConv = new ADCConvertor();
	
	
	
	public List<StraightTrack> getTracks(EvioDataEvent event, int excludeLayer, int excludeHemisphere) {
		
		HitReader hitRead = new HitReader();
		hitRead.fetch_SVTHits(event,adcConv,excludeLayer,excludeHemisphere);
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
			return null;
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
			return null;
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
			return null ; //exiting
		}
		//clean up svt crosses
		List<Cross> crossesToRm = crossMake.crossLooperCands(crosses);			
		crosses.get(0).removeAll(crossesToRm);
		
		if(org.jlab.rec.cvt.Constants.DEBUGMODE)
			System.out.println("number of reconstructed svt crosses after looper rejection = "+ (crosses.get(0).size()));
		if(crosses.size()==0) {
			return null; //exiting
		}
		// if the looper finder kills all svt crosses save all crosses anyway
		if(crosses.get(0).size()==0) {
			List<ArrayList<Cross>> crosses2 = new ArrayList<ArrayList<Cross>>();
			crosses2.add(0,(ArrayList<Cross>) crossesToRm);
			crosses2.add(1, crosses.get(1));
			
			return null;
		}
		
		//Find cross lists for Cosmics
		//4) make list of crosses consistent with a track candidate
		StraightTrackCrossListFinder crossLister = new StraightTrackCrossListFinder();
		CrossList crosslist = crossLister.findCosmicsCandidateCrossLists(crosses, SVTGeom);
		
		if(crosslist==null || crosslist.size()==0) {
			// create the clusters and fitted hits banks
			
			if(org.jlab.rec.cvt.Constants.DEBUGMODE)
				System.out.println("Saving crosses ... no track candidates found!");
			
			return null;
		}
		if(org.jlab.rec.cvt.Constants.DEBUGMODE)
			System.out.println("looking for trks from cross lists...."+ crosslist.size());
		
		//5) find the list of  track candidates
		List<StraightTrack> cosmics = new ArrayList<StraightTrack>();
		
		TrackCandListFinder trkcandFinder = new TrackCandListFinder();
		cosmics = trkcandFinder.getStraightTracks(crosslist, crosses.get(1), SVTGeom, BMTGeom);
		
				
		if(cosmics.size()==0) {
			
			if(org.jlab.rec.cvt.Constants.DEBUGMODE)
				System.out.println("Saving crosses ... no track candidates found!");
		
			return null;
		}


		
		return cosmics;
		
	}
	
	public void processEvent(EvioDataEvent event) {
		eventNb++;
		//if(org.jlab.rec.cvt.Constants.DEBUGMODE)
			System.out.println("Event Number = "+eventNb);
			
		List<StraightTrack> tracks = new ArrayList<StraightTrack>();
		List<Cluster> clusters = new ArrayList<Cluster>();
		
		int[][][] EffArray    = new int[24][8][2]; 			    //24 (max)sectors, 8*2 layers
		double[][][] ResArray = new double[24][8][2];    	    //24 (max)sectors, 8*2 layers
		
		for(int i=0; i<24; i++) {
			for(int j=0; j<8; j++) {
				for(int k=0; k<2; k++) {
					EffArray[i][j][k]=-1;
					ResArray[i][j][k]=-999;
				}
			}
		}
		
		// get all clusters
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
		
		
		
		//2) find the clusters from these hits
		ClusterFinder clusFinder = new ClusterFinder();
		clusters = clusFinder.findClusters(hits);
		if(org.jlab.rec.cvt.Constants.DEBUGMODE)
			System.out.println("number of reconstructed clusters = "+clusters.size());
		if(clusters.size()==0) {
			return;
		}
		
		//EvioDataBank effbank = (EvioDataBank) event.getDictionary().createBank("CVTRec::LayerEffs",0);
		
		for(int layIdx =0; layIdx<8; layIdx++) {
			for(int hemisIdx=0; hemisIdx<2; hemisIdx++) {
		
			tracks = this.getTracks(event, layIdx+1, (int)-Math.pow(-1, hemisIdx));
			if(tracks == null)
				continue;
			
			
				for(Cluster cls : clusters) {
					
					if(cls.get_Layer()!=layIdx+1)
						continue;
					for(int i =0; i< tracks.size(); i++) {
							if(((Trajectory) tracks.get(i)).get_Trajectory()==null)
								continue;
							for(StateVec stVec : ((Trajectory) tracks.get(i)).get_Trajectory())  {
								
								if(stVec.get_SurfaceLayer() == layIdx+1 && stVec.get_SurfaceLayer() == cls.get_Layer() && stVec.get_SurfaceSector() == cls.get_Sector()) {
					
									if(Math.abs(cls.get_Centroid()-stVec.get_CalcCentroidStrip())<20) { 
				    					EffArray[cls.get_Sector()-1][cls.get_Layer()-1][hemisIdx] = 1;
				    					Point3D trkPos = new Point3D(stVec.x(), stVec.y(), stVec.z());
				    					double residual = SVTGeom.getDOCAToStrip(cls.get_Sector(), cls.get_Layer(), cls.get_Centroid(), trkPos);
				    					ResArray[cls.get_Sector()-1][cls.get_Layer()-1][hemisIdx] = residual; 
				    				} else {
				    					EffArray[cls.get_Sector()-1][cls.get_Layer()-1][hemisIdx] = 0;
				    				}						    				
								}
							}
					}
				}
			}
		}
		
		
		// now fill the bank
		int bankSize =0;
		for(int i=0; i<24; i++)		
			for(int j=0; j<8; j++)
				for(int k=0; k<2; k++)
					if(EffArray[i][j][k]!=-1)
						bankSize++;
		
		if(bankSize==0)
			return;
		
		EvioDataBank bank =  (EvioDataBank) event.getDictionary().createBank("CVTRec::LayerEffs",bankSize);
		int bankEntry = 0;
		for(int i=0; i<24; i++)		
			for(int j=0; j<8; j++) 
				for(int k=0; k<2; k++) { 
					if(EffArray[i][j][k]==-1)
						continue;
					
					bank.setInt("sector",bankEntry, i+1);
					bank.setInt("layer",bankEntry, (j+1)*(int)-Math.pow(-1, k));
					bank.setInt("status", bankEntry,EffArray[i][j][k]);
					bank.setDouble("residual", bankEntry,ResArray[i][j][k]);
					bankEntry++;
			}
				
		event.appendBank(bank);
				//bank.show();
							
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
