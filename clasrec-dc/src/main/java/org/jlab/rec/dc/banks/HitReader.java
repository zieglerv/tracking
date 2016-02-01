package org.jlab.rec.dc.banks;

import java.util.ArrayList;
import java.util.List;

import org.jlab.data.io.DataEvent;
import org.jlab.evio.clas12.EvioDataBank;
import org.jlab.rec.dc.hit.FittedHit;
import org.jlab.rec.dc.hit.Hit;
import org.jlab.rec.dc.hit.SmearDCHit;
import org.jlab.rec.dc.Constants;

import cnuphys.snr.NoiseReductionParameters;
import cnuphys.snr.clas12.Clas12NoiseAnalysis;
import cnuphys.snr.clas12.Clas12NoiseResult;

/**
 * A class to fill in lists of hits  corresponding to DC reconstructed hits characterized by the wire, its location in the detector (superlayer,
 * layer, sector), its reconstructed time.  The class also returns a MC hit, which has truth-information (i.e. Left-Right ambiguity)
 * @author ziegler
 *
 */
public class HitReader {

	private List<Hit> _DCHits;

	private List<FittedHit> _HBHits; //hit-based tracking hit information
	
	/**
	 *
	 * @return a list of DC hits
	 */
	public List<Hit> get_DCHits() {
		return _DCHits;
	}

	/**
	 *  sets the list of DC hits
	 * @param _DCHits list of DC hits
	 */
	public void set_DCHits(List<Hit> _DCHits) {
		this._DCHits = _DCHits;
	}

	
	/**
	 *
	 * @return list of DCHB hits 
	 */
	public List<FittedHit> get_HBHits() {
		return _HBHits;
	}

	/**
	 * sets the list of HB DC hits 
	 * @param _HBHits list of DC hits 
	 */
	public void set_HBHits(List<FittedHit> _HBHits) {
		this._HBHits = _HBHits;
	}
	/**
	 * reads the hits using clas-io methods to get the EvioBank for the DC and fill the values to instantiate the DChit and MChit classes.
	 * This methods fills the DChit and MChit list of hits.  If the data is not MC, the MChit list remains empty
	 * @param event DataEvent
	 */
	public void fetch_DCHits(DataEvent event, Clas12NoiseAnalysis noiseAnalysis,NoiseReductionParameters parameters,
			Clas12NoiseResult results, SmearDCHit smear) {

		if(event.hasBank("DC::dgtz")==false) {
			//System.err.println("there is no dc bank ");
			_DCHits= new ArrayList<Hit>();
			
			return;
		}
 
				
		
		EvioDataBank bankDGTZ = (EvioDataBank) event.getBank("DC::dgtz");
        
        int[] sector = bankDGTZ.getInt("sector");
		int[] slayer = bankDGTZ.getInt("superlayer");
		int[] layer = bankDGTZ.getInt("layer");
		int[] wire = bankDGTZ.getInt("wire");
		double[] doca = null ;
		double[] time = null ;
		int[] tdc = null;
		
		if(Constants.isSimulation ==true) {
			doca = bankDGTZ.getDouble("doca");
			time = bankDGTZ.getDouble("time");
		}
		
		if(Constants.isSimulation == false) {		
			tdc = bankDGTZ.getInt("tdc");
		}
		
		if(Constants.useNoiseAlgo == true) {
			results.clear();
			noiseAnalysis.clear();
			
			noiseAnalysis.findNoise(sector, slayer, layer, wire, results);
		
		}
		
		
		int size = layer.length;

		List<Hit> hits = new ArrayList<Hit>();
		
		for(int i = 0; i<size; i++) {

			double timeError = Constants.CELLRESOL/Constants.TIMETODIST[(int) (slayer[i]+1)/2-1];  // this is the error put into the simulation 
			
			double smearedTime = 0;
			
			if(Constants.isSimulation == true) {
				if(Constants.smearDocas) {
					double invTimeToDist = Constants.TIMETODIST[(int) (slayer[i]+1)/2-1];
					smearedTime = smear.smearedTime(invTimeToDist, doca[i], slayer[i]);
					timeError = smear.smearedTimeSigma(invTimeToDist, doca[i], slayer[i]);
				} else {
					smearedTime = time[i];
				}
			} 
			if(Constants.isSimulation == false) {
				if(tdc!=null) {
					smearedTime = (double) tdc[i];
				}
			}
			
			Hit hit = new Hit(sector[i], slayer[i], layer[i], wire[i], smearedTime,timeError, i);
			
			//use only hits with signal on wires
			
			if(Constants.useNoiseAlgo == true)
				if(wire[i]!=-1 && results.noise[i]==false){		
					hit.set_Id(hits.size());
					hits.add(hit); 
				}	
			
			if(Constants.useNoiseAlgo == false)
				if(wire[i]!=-1){
					hit.set_Id(hits.size());
					hits.add(hit); 
				}
				
			}
			
			this.set_DCHits(hits);

		}
		

	/**
	 * Reads HB DC hits written to the DC bank
	 * @param event
	 */
	public void read_HBHits(DataEvent event) {
		
		if(event.hasBank("HitBasedTrkg::HBHits")==false) {
			//System.err.println("there is no HB dc bank ");
			_HBHits = new ArrayList<FittedHit>();
			return;
		}
 
		EvioDataBank bank = (EvioDataBank) event.getBank("HitBasedTrkg::HBHits");
        
		int[]  id = bank.getInt("id");
		
        int[] sector = bank.getInt("sector");
		int[] slayer = bank.getInt("superlayer");
		int[] layer = bank.getInt("layer");
		int[] wire = bank.getInt("wire");
		double[] time = bank.getDouble("time");
		double[] timeErr = bank.getDouble("timeError");
		int[] LR = bank.getInt("LR");		
		int[] clusterID = bank.getInt("clusterID");
		
		int size = layer.length;

		List<FittedHit> hits = new ArrayList<FittedHit>();
		for(int i = 0; i<size; i++) {
			//use only hits that have been fit to a track
			if(clusterID[i]==-1)
				continue;
			
			FittedHit hit = new FittedHit(sector[i], slayer[i], layer[i], wire[i], time[i]-Constants.T0, timeErr[i], id[i]);

			hit.set_AssociatedClusterID(clusterID[i]);
			
			hit.set_LeftRightAmb(LR[i]);
			hit.set_TrkgStatus(0);
			hits.add(hit);
			
		}
		
		
		
		this.set_HBHits(hits);
	}
	

   
}
