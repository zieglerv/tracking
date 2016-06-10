package org.clas.detector.cvt;

import java.util.List;

import org.jlab.clas.detector.DetectorType;
import org.jlab.clas12.detector.DetectorCounter;
import org.jlab.clas12.detector.EventDecoder;
import org.jlab.evio.clas12.EvioDataBank;
import org.jlab.evio.clas12.EvioDataEvent;
import org.jlab.evio.clas12.EvioFactory;

public class Decoder {

	public Decoder() {
		// TODO Auto-generated constructor stub
	}
	EvioDataEvent DecodeEvent(EvioDataEvent event, EventDecoder decoder) {
		EvioDataEvent decodedEvent = EvioFactory.createEvioEvent();
	    if(event.hasBank("BMT::dgtz")==false){
	    	//System.out.println("====================== Decoding BMT ===================");       	
	    	decoder.decode(event);
	    	
	    	List<DetectorCounter> counters =  decoder.getDetectorCounters(DetectorType.BMT);
	    	EvioDataBank bank = EvioFactory.createBank("BMT::dgtz", counters.size());
	    	
	    	for(int loop = 0; loop < counters.size();loop++){
	    		DetectorCounter cnt = counters.get(loop);
	    		int sector = cnt.getDescriptor().getSector();
	    		int layer  = cnt.getDescriptor().getLayer(); // starts at layer 5 for region 3 MM ring
	    		if(layer == 1)
	    			layer = 6;
	    		if(layer == 2)
	    			layer =5;
    	      
	    		int strip  = cnt.getDescriptor().getComponent();   	     
	    		int adc    = cnt.getChannels().get(0).getADC().get(0);
	    		double Edep = (double) adc;
	    		int hitno = loop + 1;
	    	    bank.setInt("layer", loop, layer);
	    	    bank.setInt("sector", loop, sector);
	    	    bank.setInt("strip", loop, strip);
	    	    bank.setInt("ADC", loop, adc);
	    	    bank.setDouble("Edep", loop, Edep);
	    	    bank.setInt("hitn", loop, hitno);
	    	}
	    	
	    	decodedEvent.appendBank(bank);
	    	//decodedEvent.show();
	    }  else {
	    	decodedEvent.appendBank(event.getBank("BMT::dgtz"));
	    }
        if(event.hasBank("BST::dgtz")==false){
        	//System.out.println("==================== Decoding SVT =====================");
        	decoder.decode(event);
        	List<DetectorCounter> counters =  decoder.getDetectorCounters(DetectorType.SVT);
        	EvioDataBank bank = EvioFactory.createBank("BST::dgtz", counters.size());
        	//for(DetectorCounter cnt : counters){
        	for(int loop = 0; loop < counters.size();loop++){
        		DetectorCounter cnt = counters.get(loop);
        	      //System.out.println(cnt);
        	      int sector = cnt.getDescriptor().getSector();
        	      int layer  = cnt.getDescriptor().getLayer();
        	      int strip  = cnt.getDescriptor().getComponent();
        	      int adc    = cnt.getChannels().get(0).getADC().get(0);
        	      int hitno = loop + 1;
        	      
        	      bank.setInt("layer", loop, layer);
        	      bank.setInt("sector", loop, sector);
        	      bank.setInt("strip", loop, strip);
        	      bank.setInt("ADC", loop, adc);
        	      bank.setInt("bco", loop, 1);
        	      bank.setInt("hitn", loop, hitno);
        	}
        	decodedEvent.appendBank(bank);
        } else {
	    	decodedEvent.appendBank(event.getBank("BST::dgtz"));
	    }
	    
		return decodedEvent;
	}
}
