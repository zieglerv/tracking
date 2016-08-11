package org.clas.detector.dc;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import org.jlab.io.decode.*;

/**
 * Converts DC readout board (crate, slot, channel) to DC wire hit (sector, layer, wire)
 * where crate runs from 67 to 67+17, active readout slots are 4 to 10 and 13 to 19,
 * and DCRB channels run from 0 to 95. 
 * Sector runs from 1 to 6, layer from 1 to 36, and wire from 1 to 112
 *
 */
public class DCTranslationTable extends AbsDetectorTranslationTable {

	// the sector served by the 1st to the 18th crate
	// likewise for the region
  int[] crate_sector = {1,2,3,4,5,6,1,2,3,4,5,6,1,2,3,4,5,6};
  int[] crate_region = {1,2,3,1,2,3,1,2,3,1,2,3,1,2,3,1,2,3};
  	// the local layer corresponding to channel number from 0 to 95
  int[] chan_loclayer ={2,4,6,1,3,5,2,4,6,1,3,5,2,4,6,1,3,5,
      2,4,6,1,3,5,2,4,6,1,3,5,2,4,6,1,3,5,2,4,6,1,3,5,
      2,4,6,1,3,5,2,4,6,1,3,5,2,4,6,1,3,5,2,4,6,1,3,5,
      2,4,6,1,3,5,2,4,6,1,3,5,2,4,6,1,3,5,2,4,6,1,3,5,
      2,4,6,1,3,5};
  	// the local wire number on one STB board corresponding to channel number from 0 to 95
  int[] chan_locwire = {1,1,1,1,1,1,2,2,2,2,2,2,3,3,3,3,3,3,
      4,4,4,4,4,4,5,5,5,5,5,5,6,6,6,6,6,6,7,7,7,7,7,7,
      8,8,8,8,8,8,9,9,9,9,9,9,10,10,10,10,10,10,
      11,11,11,11,11,11,12,12,12,12,12,12,13,13,13,13,13,13,
      14,14,14,14,14,14,15,15,15,15,15,15,16,16,16,16,16,16};
  // the STB corresponding to DCRB slots 
  int[] slot_stb = {0,0,0,1,2,3,4,5,6,7,0,0,1,2,3,4,5,6,7,0};
  //the local superlayer corresponding to STB slots 
  int[] slot_locsuplayer = {0,0,0,1,1,1,1,1,1,1,0,0,2,2,2,2,2,2,2,0};


  public DCTranslationTable(){
    super("DC",100); // This defines name of the detector and tag=900 for final bank
    // TAG is uded by automated convertor to create event with proper structure
  }

  /**
   * crate : the crate number (67...84)
   * slot : the slot number (4...10, 13...19)
   * channel : the channel on one DCRB (0...95)
   * 
   * returns : the sector (1...6)
   */
  @Override
  public Integer getSector(int crate, int slot, int channel){
	 int crateIdx = crate - 67;
     return crate_sector[crateIdx];
  }

  /**
   * crate : the crate number (67...84)
   * slot : the slot number (4...10, 13...19)
   * channel : the channel on one DCRB (0...95)
   * 
   * returns : the layer (1...36)
   */
  @Override
  public Integer getLayer(int crate, int slot, int channel){
    
//	System.out.println( " crate " + crate + " slot " + slot + " channel " + channel);
	int slotIdx = slot -1;
	int crateIdx = crate - 67;
	int channelIdx = channel; // channel runs from 0 to 95
	
    int region = crate_region[crateIdx];
    int loclayer=chan_loclayer[channelIdx];
    int locsuplayer=slot_locsuplayer[slotIdx];
    int suplayer=(region-1)*2 + locsuplayer;
    int layer=(suplayer-1)*6 + loclayer;
    
   // System.out.println("   -->    region "+region + " loclayer " + loclayer + " locsuplayer " + locsuplayer + " suplayer " + suplayer+" layer "+layer);
    
    return layer;
    //return 1;
  }

  /**
   * crate : the crate number (67...84)
   * slot : the slot number (4...10, 13...19); 
   * channel : the channel on one DCRB (0...95)
   * 
   * returns : the wire (1...112)
   */
  @Override
  public Integer getComponent(int crate, int slot, int channel){
	 int channelIdx = channel; // channel runs from 0 to 95
	 int slotIdx = slot -1;
	 int locwire = chan_locwire[channelIdx];
     int nstb=slot_stb[slotIdx];
     int wire=(nstb-1)*16+locwire;
     return wire;
     //return 0;
  }
  
  public static void main (String arg[]) throws FileNotFoundException {
	  DCTranslationTable tran = new DCTranslationTable();
		/*
		 *  Crate number can be only 1-18 (1 crate per chamber)
		 *  Slot number can be only the following: (4-10 or 13-19)
		 *       Slots 1,2,3, 11, 12 and 20 are empty/unused
		 *       Slots 4-10 are for one superlayer, 13-19 is for the other
		 *  Channel # goes from 0 to 95, and we get the connector # as 
		 *    follows (see below too):  connector = (int)(Channel/16)+1; 
		 *       Channels 0-15 in a module/slot are connected through connector 1
		 *       Channels 16-31 in the same module/slot are connected through connector 2
		 *         and so on.
		 */      


	  PrintWriter pw = new PrintWriter(new File("/Users/ziegler/DC/DC.table"));
	  int order =0;
	  for(int crate =67; crate<85; crate++)
		  for(int slot =1; slot<20; slot++)
			  for(int channel =0; channel<96; channel++) {
				    int crateIdx = crate - 67;
				    int sector = tran.crate_sector[crateIdx];
				    int slotIdx = slot -1;
				  
				    int channelIdx = channel; // channel runs from 0 to 95
					
				    int region = tran.crate_region[crateIdx];
				    int loclayer=tran.chan_loclayer[channelIdx];
				    int locsuplayer=tran.slot_locsuplayer[slotIdx];
				    int suplayer=(region-1)*2 + locsuplayer;
				    int layer=(suplayer-1)*6 + loclayer;
				  
				     
					 int locwire = tran.chan_locwire[channelIdx];
				     int nstb=tran.slot_stb[slotIdx];
				     int wire=(nstb-1)*16+locwire;
				  
				     if(sector<=0 || layer<=0 || wire<=0) 
				    	 continue;
				    
				     pw.printf("DC\t\t %d\t\t %d\t\t %d\t\t %d\t\t %d\t\t %d\t\t %d\t\t\n", crate, slot, channel, sector, layer, wire, order);
				  System.out.println((crate)+"  "+(slot)+"  "+(channel)+"  "+sector
						  +"  "+layer+"  "+wire);
			  }
	  pw.close();
  }
  

}