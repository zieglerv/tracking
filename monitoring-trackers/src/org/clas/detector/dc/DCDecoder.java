package org.clas.detector.dc;

import java.io.FileNotFoundException;
import java.util.List;

import org.jlab.clas12.raw.RawDataEntry;
import org.jlab.coda.jevio.EvioException;
import org.jlab.evio.clas12.*;
import org.jlab.io.decode.*;

	
public class DCDecoder {

	int[] cableid = {0,0,0,1,7, 13,19,25,31,37,0,0,43,49,55,61,67,73,79,0,
	          		0,0,0,2,8, 14,20,26,32,38,0,0,44,50,56,62,68,74,80,0,
	        		0,0,0,3,9, 15,21,27,33,39,0,0,45,51,57,63,69,75,81,0,
	        		0,0,0,4,10,16,22,28,34,40,0,0,46,52,58,64,70,76,82,0,
	        		0,0,0,5,11,17,23,29,35,41,0,0,47,53,59,65,71,77,83,0,
	        		0,0,0,6,12,18,24,30,36,42,0,0,48,54,60,66,72,78,84,0};
	        		
	double[] T0sRun128 = {897.569,897.587,896.293,895.218,898.598,898.016,917.667,915.907,917.472,917.369,918.017,919.303,
			914.938,916.718,916.189,920.772,921.514,921.587,919.464,920.053,919.817,923.588,921.585,921.474,904.707,905.766,906.004,905.008,
        	906.682,908.475,907.341,905.975,905.921,907.455,907.969,908.014,916.248,917.239,916.821,916.963,917.283,918.335,
        	900.69,900.85,898.881,899.386,900.11,901.551,918.435,917.921,919.078,918.082,918.551,918.12,920.407,921.269,
        	921.343,921.867,922.295,923.899,908.817,909.221,910.183,910.806,910.163,911.027,912.8,913.475,914.689,916.22,
        	915.77,916.301,912.604,912.382,912.362,912.8,914.819,913.879,909.414,910.177,909.593,911.028,909.485,911.728}; 
	
		public DCDecoder() {
			// TODO Auto-generated constructor stub
		}
	    
		EvioDataEvent DecodeEvent(EvioDataEvent event, EvioRawEventDecoder decoder, DCTranslationTable table) {
			
			//EvioDataEvent decodedEvent = EvioFactory.createEvioEvent();
			EvioDataEvent decodedEvent = event;
			if(event.hasBank("DC::dgtz")==false){
				//System.out.println(" DECODING DATA");
				List<RawDataEntry> dataEntries = decoder.getDataEntries(event);
				List<RawDataEntry>  dcdata = decoder.getDecodedData(dataEntries,table);
				int hitId = 0;
				int bankSize = dcdata.size()/2;
				EvioDataBank dcBank = EvioFactory.createBank("DC::dgtz", dcdata.size()/2);
				  
				//for(RawDataEntry  entry : dcdata){  
				for(int i =0; i<bankSize; i++) {
					RawDataEntry  entry = dcdata.get(i);
					
					int connector = (int)(entry.getChannel()/16)+1; 
					int icableID = (connector-1)*20+entry.getSlot()-1; //20 slots
					int cable_id=cableid[icableID];
					
					int sector = entry.getSector();
					int superlayer = (int) ((entry.getLayer()-1)/6) +1;
					int layer = entry.getLayer() - (superlayer-1)*6;
					int wire = entry.getComponent();
					int tdc = entry.getTDC();
					dcBank.setInt("sector", hitId, sector);
			        dcBank.setInt("superlayer", hitId, superlayer);
			        dcBank.setInt("layer", hitId, layer);
			        dcBank.setInt("wire", hitId, wire);
			        dcBank.setInt("tdc", hitId, tdc-(int)T0sRun128[cable_id-1]);
			        
			        hitId++;
				}
				decodedEvent.appendBank(dcBank);
		    	decodedEvent.show();
		    }  else {
		    	decodedEvent.appendBank(event.getBank("DC::dgtz"));
		    }
			return decodedEvent;
			
		}
		
		
		
		
		
		 public static void main(String[] args) throws FileNotFoundException, EvioException{
			 
			
		 }


		
}
