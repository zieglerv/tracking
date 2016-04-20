package org.jlab.rec.fmt.banks;

import java.util.ArrayList;
import java.util.List;

import org.jlab.data.io.DataEvent;
import org.jlab.evio.clas12.EvioDataBank;
import org.jlab.rec.fmt.hit.Hit;

/**
 * A class to fill in lists of hits  corresponding to FMT reconstructed hits characterized by the strip, its location in the detector 
 * (layer, sector), its reconstructed time.  The class also returns a MC hit, which has truth-information 
 * @author ziegler
 *
 */
public class HitReader {

	private List<Hit> _Hits;
	
	/**
	 *
	 * @return a list of FMT hits
	 */
	public List<Hit> get_FMTHits() {
		return _Hits;
	}

	/**
	 *  sets the list of FMT hits
	 * @param _FMTHits list of FMT hits
	 */
	public void set_FMTHits(List<Hit> _FMTHits) {
		this._Hits = _FMTHits;
	}


	/**
	 * reads the hits using clas-io methods to get the EvioBank for the FMT and fill the values to instanciate the FMThit and MChit classes.
	 * This methods fills the FMThit and MChit list of hits.  If the data is not MC, the MChit list remains empty
	 * @param event DataEvent
	 */
	public void fetch_FMTHits(DataEvent event) {

		if(event.hasBank("FMT::dgtz")==false) {
			//System.err.println("there is no FMT bank ");
			_Hits= new ArrayList<Hit>();
			
			return;
		}

		List<Hit> hits = new ArrayList<Hit>();
		
		EvioDataBank bankDGTZ = (EvioDataBank) event.getBank("FMT::dgtz");
        
        int[] sector = bankDGTZ.getInt("sector");
		int[] layer = bankDGTZ.getInt("layer");
		int[] strip = bankDGTZ.getInt("strip");
		int[] ADC = bankDGTZ.getInt("ADC");
		//int ADC =1;	
		int size = layer.length;
		
		
		
		if(event.hasBank("FMT::dgtz")==true) {
			for(int i = 0; i<size; i++){
			
	    	  Hit hit = new Hit(sector[i], layer[i], strip[i], (double)ADC[i]);
	    	  hit.set_Id(i);
	    	  //System.out.println("FMT hit "+hit.printInfo());
	    	  //use only hits with signal on wires
	          if(strip[i]!=-1){
	             hits.add(hit); 
	          }
	         
	      }
	      this.set_FMTHits(hits);

		}
	}	
	
	
}
