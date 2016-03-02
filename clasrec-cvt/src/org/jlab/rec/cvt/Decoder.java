package org.jlab.rec.cvt;

import java.io.FileNotFoundException;
import java.util.List;

import org.jlab.clas.detector.DetectorType;
import org.jlab.clas12.detector.DetectorCounter;
import org.jlab.clas12.detector.EventDecoder;
import org.jlab.clas12.detector.FADCMaxFinder;
import org.jlab.coda.jevio.EvioException;
import org.jlab.data.io.DataEvent;
import org.jlab.evio.clas12.*;

	
/**
 * A class to decode and translate raw evio data into sector, layer, strip, adc 
 * @author ziegler
 *
 */
public class Decoder {

	EventDecoder decoder = new EventDecoder();
	/**
	 * The fitter type is fADC peak finder
	 */
	public Decoder() {
		decoder.addFitter(DetectorType.BMT,
    	        new FADCMaxFinder());
		/*decoder.addFitter(DetectorType.BMT,
		        new FADCBasicFitter(   0, // first bin for pedestal
		                              16, // last bin for pedestal
		                               0, // first bin for pulse integral
		                              16  // last bin for pulse integral
		                              )); */
	}
	
	/**
	 * 
	 * @param de data event
	 * @param writer write event to evio bank
	 */
	public void processEvent(DataEvent de, EvioDataSync writer) {
        EvioDataEvent event = (EvioDataEvent) de;
        EvioDataEvent decodedEvent = EvioFactory.createEvioEvent();
       
        if(event.hasBank("BMT::dgtz")==false){ //decoding BMT
        	// decode the event 
        	decoder.decode(event);
        	List<DetectorCounter> counters =  decoder.getDetectorCounters(DetectorType.BMT);
        	// create the "digitize" bank
        	EvioDataBank bank = EvioFactory.createBank("BMT::dgtz", counters.size());
        	// loop over the counters     
    		for(int loop = 0; loop < counters.size();loop++){
        		DetectorCounter cnt = counters.get(loop);       	
        		// get the sector, layer, components
				int sector = cnt.getDescriptor().getSector();
				int layer  = cnt.getDescriptor().getLayer(); // starts at layer 5 for region 3 MM ring
				if(layer == 1)
				  layer = 6;
				if(layer == 2)
				  layer =5;
				  
				int strip  = cnt.getDescriptor().getComponent();				 
				int adc    = cnt.getChannels().get(0).getADC().get(0);
				 // fill the bank with layer, sector, strip, adc and hit number for each hit
				 int hitno = loop + 1;
				 bank.setInt("layer", loop, layer);
				 bank.setInt("sector", loop, sector);
				 bank.setInt("strip", loop, strip);
				 bank.setInt("ADC", loop, adc);
				 // bank.setDouble("Edep", loop, Edep);
				 bank.setInt("hitn", loop, hitno);
        	}
    		// if the bank has entries append it to the evio event
    		if(bank.getInt("layer").length>0)
    			decodedEvent.appendBank(bank);
        }
    	if(event.hasBank("BST::dgtz")==false){ //decoding SVT
        	// same for the BST, if the dgtz bank does not exist, it is raw data and is decoded
        	decoder.decode(event);
        	List<DetectorCounter> counters2 =  decoder.getDetectorCounters(DetectorType.SVT);
        	
        	EvioDataBank bank2 = EvioFactory.createBank("BST::dgtz", counters2.size());
        	
        	for(int loop = 0; loop < counters2.size();loop++){
        		DetectorCounter cnt = counters2.get(loop);
        		//System.out.println(cnt);
        		int sector = cnt.getDescriptor().getSector();
        		int layer  = cnt.getDescriptor().getLayer();
        		int strip  = cnt.getDescriptor().getComponent();
	    	    int adc    = cnt.getChannels().get(0).getADC().get(0);
	    	    int hitno = loop + 1;
	    	      
	    	    bank2.setInt("layer", loop, layer);
	    	    bank2.setInt("sector", loop, sector);
	    	    bank2.setInt("strip", loop, strip);
	    	    bank2.setInt("ADC", loop, adc);
	    	    bank2.setInt("bco", loop, 1);
	    	    bank2.setInt("hitn", loop, hitno);
        	}
        	if(bank2.getInt("layer").length>0)
        		decodedEvent.appendBank(bank2);
    	}
    	if(decodedEvent!=null)
        	writer.writeEvent(decodedEvent);
        
	}
		
		 public static void main(String[] args) throws FileNotFoundException, EvioException{
			 
			//String inputFile = "//Users/ziegler/workspace/coatjava-2.3/svtmvt_000126.evio";
			 //String outputFile = "/Users/ziegler/workspace/coatjava-2.4/bmt_decodedtest.ev";
			String inputFile = args[0];
			String outputFile = args[1];
			
			System.err.println(" \n[PROCESSING FILE] : " + inputFile);

			Decoder dc = new Decoder(); 
			EvioSource reader = new EvioSource();
			EvioDataSync writer = new EvioDataSync();
			writer.open(outputFile);
			
			int counter = 0;
			
			reader.open(inputFile);
			
			while(reader.hasEvent()){
				
				counter++;
				EvioDataEvent event = (EvioDataEvent) reader.getNextEvent();
				dc.processEvent(event, writer);
				
				if(counter%100==0)
					System.out.println("decoded "+counter+" events");
				
			}
				
			
		 }


		
}
