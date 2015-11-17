package org.jlab.rec.bmt.banks;

import java.util.ArrayList;
import java.util.List;

import org.jlab.data.io.DataEvent;
import org.jlab.evio.clas12.EvioDataBank;
import org.jlab.rec.bmt.Geometry;
import org.jlab.rec.bmt.hit.Hit;

/**
 * A class to fill in lists of hits  corresponding to BMT reconstructed hits characterized by the strip, its location in the detector 
 * (layer, sector), its reconstructed time.  The class also returns a MC hit, which has truth-information 
 * @author ziegler
 *
 */
public class HitReader {

	private List<Hit> _Hits;
	
	/**
	 *
	 * @return a list of BMT hits
	 */
	public List<Hit> get_BMTHits() {
		return _Hits;
	}

	/**
	 *  sets the list of BMT hits
	 * @param _BMTHits list of BMT hits
	 */
	public void set_BMTHits(List<Hit> _BMTHits) {
		this._Hits = _BMTHits;
	}


	/**
	 * reads the hits using clas-io methods to get the EvioBank for the BMT and fill the values to instantiate the BMThit and MChit classes.
	 * This methods fills the BMThit and MChit list of hits.  If the data is not MC, the MChit list remains empty
	 * @param event DataEvent
	 */
	public void fetch_BMTHits(DataEvent event, Geometry geo) {

		if(event.hasBank("BMT::dgtz")==false) {
			//System.err.println("there is no BMT bank ");
			_Hits= new ArrayList<Hit>();
			
			return;
		}

		List<Hit> hits = new ArrayList<Hit>();
		
		EvioDataBank bankDGTZ = (EvioDataBank) event.getBank("BMT::dgtz");
        
        int[] sector = bankDGTZ.getInt("sector");
		int[] layer = bankDGTZ.getInt("layer");
		int[] strip = bankDGTZ.getInt("strip");
		double[] Edep = bankDGTZ.getDouble("Edep");
		
		int size = layer.length;
		
		if(event.hasBank("BMT::dgtz")==true) {
			for(int i = 0; i<size; i++){
			
	    	  Hit hit = new Hit(sector[i], layer[i], strip[i], Edep[i]);
	    	  hit.set_Id(i);
	    	  
	    	  //use only hits with signals
	          if(strip[i]!=-1){
	        	  hit.calc_StripParams(geo); // for Z detectors the Lorentz angle shifts the strip measurement; calc_Strip corrects for this effect
	              hits.add(hit); 
	          }
	         
	      }
	      this.set_BMTHits(hits);

		}
	}	
	
	
}
