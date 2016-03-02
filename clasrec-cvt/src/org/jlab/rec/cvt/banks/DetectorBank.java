package org.jlab.rec.cvt.banks;


import java.util.ArrayList;
import java.util.List;

public class DetectorBank {

	private List<Integer>  layer   = new ArrayList<Integer>();
    private List<Integer>  sector   = new ArrayList<Integer>();
    private List<Integer>  strip = new ArrayList<Integer>();
    
	public DetectorBank() {
		// TODO Auto-generated constructor stub
	}

	@EvioDataType(parent=110,tag=111,num=2,type="int32")
    public List get_Layer(){
        //System.out.println(" method called ");
        return this.layer;
    }
	@EvioDataType(parent=110,tag=111,num=1,type="int32")
    public List get_Sector(){
        //System.out.println(" method called ");
        return this.sector;
    }
	@EvioDataType(parent=110,tag=111,num=3,type="int32")
    public List get_Strip(){
        //System.out.println(" method called ");
        return this.strip;
    }
	
	@Override
    public String toString(){
        StringBuilder str = new StringBuilder();
        str.append(" sector : ");
        for(Integer value : this.sector){
            str.append(value);
            str.append(" ");
        }
        str.append("\n");
        
        str.append(" layer : ");
        for(Integer value : this.layer){
            str.append(value);
            str.append(" ");
        }
        str.append("\n");
        str.append(" strip : ");
        for(Integer value : this.strip){
            str.append(value);
            str.append(" ");
        }
        str.append("\n");
        return str.toString();
    }
}
