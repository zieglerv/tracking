package org.jlab.rec.fmt;

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
 * @author defurne
 *
 */

public class Decoder {

	EventDecoder decoder = new EventDecoder();
	/**
	 * The fitter type is fADC peak finder
	 */
	public Decoder() {
		decoder.addFitter(DetectorType.FMT,
				new FADCMaxFinder());
	}	
	
	
	/**
	 * 
	 * @param de data event
	 * @param writer write event to evio bank
	 */
	public void processEvent(DataEvent de, EvioDataSync writer) {
        EvioDataEvent event = (EvioDataEvent) de;
        EvioDataEvent decodedEvent = (EvioDataEvent) de;
        		//EvioFactory.createEvioEvent();
       	
    	if(event.hasBank("FMT::dgtz")==false){ //decoding FMT
        	// decode the event 
        	decoder.decode(event);
        	
        	List<DetectorCounter> counters =  decoder.getDetectorCounters(DetectorType.FMT);
        	
        	// create the "digitize" bank
        	EvioDataBank bank = EvioFactory.createBank("FMT::dgtz", counters.size());
        	// loop over the counters     
    		for(int loop = 0; loop < counters.size();loop++){
        		DetectorCounter cnt = counters.get(loop);       	
        		// get the sector, layer, components
				int sector = cnt.getDescriptor().getSector();
				int layer  = cnt.getDescriptor().getLayer(); // starts at layer 5 for region 3 MM ring
								  
				int strip  = cnt.getDescriptor().getComponent();				 
				int adc    = cnt.getChannels().get(0).getADC().get(0);
				//System.out.println("ADC and layer: "+adc+" "+layer);
				 // fill the bank with layer, sector, strip, adc and hit number for each hit
				 int hitno = loop + 1;
				 bank.setInt("layer", loop, layer);
				 bank.setInt("sector", loop, sector);
				 bank.setInt("strip", loop, strip);
				 bank.setInt("ADC", loop, adc);
				 // bank.setDouble("Edep", loop, Edep);
				 bank.setInt("hitn", loop, hitno);
				 //if (layer!=1) System.out.println("layer= "+layer+" sector= "+sector+" strip= "+strip+" ADC= "+adc);
        	}
    		// if the bank has entries append it to the evio event
    		if(bank.getInt("layer").length>0)
    			decodedEvent.appendBank(bank);
        }
    	if(decodedEvent!=null)
        	writer.writeEvent(decodedEvent);
        
	}
	
	public static void main(String[] args) throws FileNotFoundException, EvioException{
		 
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