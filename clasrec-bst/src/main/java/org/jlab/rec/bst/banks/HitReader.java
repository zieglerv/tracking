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
			  if(event.hasBank("GenPart::true")==true)
				  Constants.isSimulation=true;
			  
			  //Gemc bug fix
			  if(Constants.isCosmicsData == false && strip[i]==0)
				  strip[i] = -1;
	    		  
			  int theSector = sector[i];
			    
			  Hit hit = new Hit(theSector, layer[i], strip[i], ADCtoEdep);
	    	  
	    	  hit.set_Id(i+1);
	    	  
	    	  //use only hits with signal on wires
	          if(strip[i]!=-1){
	             hits.add(hit); 
	            
	          }
	         
	      }
			
	      this.set_BSTHits(hits);

		}
	}	
	
	
	   public static int initThresholds = 30;
	   public static int deltaThresholds = 15;
	    
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
	     public double converttoDAQ(int adc) {
	    	
	    	if(adc<0 || adc>7) 
	    		return 0;

	    	int START[] = new int[8];
	    	int END[]   = new int[8];
	    	for(int i = 0; i<8; i++) {
	    		START[i] = initThresholds+deltaThresholds*i;
	    		END[i]   = initThresholds+deltaThresholds*(i+1);
	    	}
	    	END[7]=1000; //overflow
	    			
	    
	        Random random = new Random();
	        
	        int daq =  returnRandomInteger(START[adc], END[adc], random);
	        
	        double value = (double) daq;
	        
	        if(Constants.isSimulation==true) {
	        	//This is how GEMC sets the adc:
	        	// the energy deposited from a mip is 80 KeV
	        	// The max value of the ADC is 2.5V
	        	// We set for now 3 values of mip inside the 2.5V.
	        	// So ~250 KeV = 2.5V, or 0.10 MeV = 1 Volt.
	        	//double maxV = 2.5;
	        	//double etoV = 0.1;
	        	//double vout = tInfos.eTot/etoV;
	        	//double vrat = vout / maxV;
	        	//int adc     = floor(vrat*8);
	        	//if(adc >7) adc = 7;
	        	double maxV = 2.5;
	        	double etoV = 0.1;
	        	
	        	value = ((double)(adc+0.5)*etoV*maxV/8); // center of bin to avoid zero value
	        }
	        return value;
	    }

	    private  int returnRandomInteger(int aStart, int aEnd, Random aRandom){
	        if ( aStart > aEnd ) {
	        	return 0;
	          //throw new IllegalArgumentException("Start cannot exceed End.");
	        }
	        /*
	        //get the range, casting to long to avoid overflow problems -- for a flat distribution
	        long range = (long)aEnd - (long)aStart + 1;
	        // compute a fraction of the range, 0 <= frac < range
	        double x = aRandom.nextDouble();
	        long fraction = (long)(range * x);
	        int randomNumber =  (int)(fraction + aStart);  
	        */
	       double landauC = -1;
	        while(landauC>aEnd || landauC<aStart) {
	        	double landau = randomLandau(100, 5, aRandom);
	        	landauC = (41*aRandom.nextGaussian() +landau);
	        }
	     
	        int randomNumber = (int) landauC;
	        
	        return randomNumber;
	      }
	    
	////////////////////////////////////////////////////////////////////////////////
	 /// Generate a random number following a Landau distribution
	 /// The Landau random number generation is implemented using the
	 /// function landau_quantile(x,sigma), which provides
	 /// the inverse of the landau cumulative distribution.
	 /// landau_quantile has been converted from CERNLIB ranlan(G110).
	 
	  private double randomLandau(double mu, double sigma, Random aRandom) {
		  
	    if (sigma <= 0) return 0;
	    
	    double res = mu + landau_quantile(aRandom.nextDouble(), sigma);
	    return res;
	  }
		    
	  private double landau_quantile(double z, double xi) {
	    	// LANDAU quantile : algorithm from CERNLIB G110 ranlan
	    	// with scale parameter xi
	    	
	    	if (xi <= 0) return 0;
	    	if (z <= 0) return Double.NEGATIVE_INFINITY;
	    	if (z >= 1) return Double.POSITIVE_INFINITY;

	    	double ranlan, u, v;
	    	u = 1000*z;
	    	int i = (int) u;
	    	u -= i;
	    	if (i >= 70 && i < 800) {
	    	ranlan = Constants.f[i-1] + u*(Constants.f[i] - Constants.f[i-1]);
	    	} else if (i >= 7 && i <= 980) {
	    		ranlan =  Constants.f[i-1] + u*(Constants.f[i]-Constants.f[i-1]-0.25*(1-u)*(Constants.f[i+1]-Constants.f[i]-Constants.f[i-1]+Constants.f[i-2]));
	    	} else if (i < 7) {
		    	v = Math.log(z);
		    	u = 1/v;
		    	ranlan = ((0.99858950+(3.45213058E1+1.70854528E1*u)*u)/
		    	(1         +(3.41760202E1+4.01244582  *u)*u))*
		    	(-Math.log(-0.91893853-v)-1);
	    	} else {
		    	u = 1-z;
		    	v = u*u;
		    	if (z <= 0.999) {
		    		ranlan = (1.00060006+2.63991156E2*u+4.37320068E3*v)/
		    				((1         +2.57368075E2*u+3.41448018E3*v)*u);
		    	} else {
		    		ranlan = (1.00001538+6.07514119E3*u+7.34266409E5*v)/
		    				((1         +6.06511919E3*u+6.94021044E5*v)*u);
		    	}
	    	}
	    	return xi*ranlan;
	    }
	    
	 
	  private int RemapSector(int sector, int layer) {
			
		    float secAngleDivPi = ((float)sector -1)*2/(float)Constants.NSECT[layer-1];
			// remap
		    secAngleDivPi+=1;
		    
		    if(secAngleDivPi>=2)
		    	secAngleDivPi-=2;
		    
		    
		    int rmpSec = (int) (secAngleDivPi*Constants.NSECT[layer-1]/2 + 1);
			System.out.println(" layer "+layer +" old sec "+sector+" new sec "+rmpSec);
			return rmpSec;
		}

}
