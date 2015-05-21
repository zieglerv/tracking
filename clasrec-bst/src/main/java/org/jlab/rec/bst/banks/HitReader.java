package org.jlab.rec.bst.banks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jlab.data.io.DataEvent;
import org.jlab.evio.clas12.EvioDataBank;
import org.jlab.rec.bst.Constants;
import org.jlab.rec.bst.hit.Hit;
import org.jlab.rec.bst.hit.MCHit;

/**
 * A class to fill in lists of hits  corresponding to BST reconstructed hits characterized by the strip, its location in the detector 
 * (layer, sector), its reconstructed time.  The class also returns a MC hit, which has truth-information 
 * @author ziegler
 *
 */
public class HitReader {

	private List<Hit> _Hits;
	
	/**
	 *
	 * @return a list of BST hits
	 */
	public List<Hit> get_BSTHits() {
		return _Hits;
	}

	/**
	 *  sets the list of BST hits
	 * @param _BSTHits list of BST hits
	 */
	public void set_BSTHits(List<Hit> _BSTHits) {
		this._Hits = _BSTHits;
	}

	

	
	/**
	 * reads the hits using clas-io methods to get the EvioBank for the BST and fill the values to instanciate the BSThit and MChit classes.
	 * This methods fills the BSThit and MChit list of hits.  If the data is not MC, the MChit list remains empty
	 * @param event DataEvent
	 */
	public void fetch_BSTHits(DataEvent event) {

		if(event.hasBank("BST::dgtz")==false) {
			//System.err.println("there is no BST bank ");
			_Hits= new ArrayList<Hit>();
			
			return;
		}

		List<Hit> hits = new ArrayList<Hit>();
		List<MCHit> mchits = new ArrayList<MCHit>();
		
		EvioDataBank bankDGTZ = (EvioDataBank) event.getBank("BST::dgtz");
        
        int[] sector = bankDGTZ.getInt("sector");
		int[] layer = bankDGTZ.getInt("layer");
		int[] strip = bankDGTZ.getInt("strip");
		int[] ADC = bankDGTZ.getInt("ADC");
			
		int size = layer.length;
		
		if(event.hasBank("BST::true")==true) {
			EvioDataBank bankTRUE = (EvioDataBank) event.getBank("BST::true");
	       
	        double[] Edep = bankTRUE.getDouble("totEdep");
	        double[] x_avg = bankTRUE.getDouble("avgX");
	        double[] y_avg = bankTRUE.getDouble("avgY");
	        double[] z_avg = bankTRUE.getDouble("avgZ");
	        
	        for(int i = 0; i<size; i++){
		        MCHit mchit = new MCHit(sector[i], layer[i], strip[i], Edep[i]);
		        mchit.set_Id(i);
		        mchit.set_x(x_avg[i]);
		        mchit.set_y(y_avg[i]);
		        mchit.set_z(z_avg[i]);
		        
		        mchits.add(mchit);
	        }
	        
		} 
		
		if(event.hasBank("BST::dgtz")==true) {
			for(int i = 0; i<size; i++){
			
		      double ADCtoEdep = converttoDAQ(ADC[i]);
			  
			  //Gemc bug fix
			  if(Constants.isCosmicsData == false && strip[i]==0)
				  strip[i] = -1;
	    		  
			  Hit hit = new Hit(sector[i], layer[i], strip[i], ADCtoEdep);
	    	  
	    	  hit.set_Id(i);
	    	  
	    	  //use only hits with signal on wires
	          if(strip[i]!=-1){
	             hits.add(hit); 
	            
	          }
	         
	      }
	      this.set_BSTHits(hits);

		}
	}	
	
	
	   public static int initThresholds = 40;
	   public static int deltaThresholds = 20;
	    
	    public static void setThesholds(int thr, int del) {
	    	initThresholds = thr;
	    	deltaThresholds = del;
	    }
	    /**
	     * 
	     * @param hit Hit object
	     * @param adc ADC value 
	     * Converts ADC values to DAQ units -- used for BST test stand analysis
	     */
	    static public double converttoDAQ(int adc) {
	    	
	    	if(adc<0 || adc>7) 
	    		return 0;

	    	int START[] = new int[8];
	    	int END[]   = new int[8];
	    	for(int i = 0; i<8; i++) {
	    		START[i] = initThresholds+deltaThresholds*i;
	    		END[i]   = initThresholds+deltaThresholds*(i+1);
	    	}
	    	END[7]=1000;
	    			
	    
	        Random random = new Random();
	        
	        int daq =  returnRandomInteger(START[adc], END[adc], random);
	        
	        if(Constants.isCosmicsData==false)
	        	daq = adc;
	        
	        return (double) daq;
	    }

	    private static int returnRandomInteger(int aStart, int aEnd, Random aRandom){
	        if ( aStart > aEnd ) {
	        	return 0;
	          //throw new IllegalArgumentException("Start cannot exceed End.");
	        }
	        //get the range, casting to long to avoid overflow problems
	        long range = (long)aEnd - (long)aStart + 1;
	        // compute a fraction of the range, 0 <= frac < range
	        long fraction = (long)(range * aRandom.nextDouble());
	        int randomNumber =  (int)(fraction + aStart);    

	        return randomNumber;
	      }
}
