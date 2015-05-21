package org.jlab.rec.dc.hit;

/**
 * A DC hit characterized by superlayer, layer, sector, wire number, and time.  The TDC to time conversion has been done.
 * @author ziegler
 *
 */


public class Hit implements Comparable<Hit>{
	// class implements Comparable interface to allow for sorting a collection of hits by wire number values
	
	
	// constructors 
	
	/**
	 * 
	 * @param sector  (1...6)
	 * @param superlayer (1...6)
	 * @param layer (1...6)
	 * @param wire (1...112)
	 * @param time (for gemc output without digitization)
	 * @param timeEr the error on the time
	 */
	public Hit(int sector, int superlayer, int layer, int wire, double time, double timeEr, int Id) {
		this._Sector = sector;
		this._Superlayer = superlayer;
		this._Layer = layer;
		this._Wire = wire;
		this._Time = time;
		this._TimeErr = timeEr; 	// Time set to a constant in unit of ns		
		this._Id = Id; 	// Time set to a constant in unit of ns		
		
	}
	
	
	private int _Sector;      							//	   sector[1...6]
	private int _Superlayer;    	 					//	   superlayer [1,...6]
	private int _Layer;    	 							//	   layer [1,...6]
	private int _Wire;    	 							//	   wire [1...112]

	private double _Time;      							//	   Reconstructed time, for now it is the gemc time
	private double _TimeErr;      						//	   Error on time, for now it is a constant
	
	private int _Id;									//		Hit Id
	
	public int _lr;
	
	/**
	 * 
	 * @return the sector (1...6)
	 */
	public int get_Sector() {
		return _Sector;
	}

	/**
	 * Sets the sector 
	 * @param _Sector
	 */
	public void set_Sector(int _Sector) {
		this._Sector = _Sector;
	}

	/**
	 * 
	 * @return the superlayer (1...6)
	 */
	public int get_Superlayer() {
		return _Superlayer;
	}

	/**
	 * Sets the superlayer
	 * @param _Superlayer
	 */
	public void set_Superlayer(int _Superlayer) {
		this._Superlayer = _Superlayer;
	}

	/**
	 * 
	 * @return the layer (1...6)
	 */
	public int get_Layer() {
		return _Layer;
	}

	/**
	 * Sets the layer
	 * @param _Layer
	 */
	public void set_Layer(int _Layer) {
		this._Layer = _Layer;
	}

	/**
	 * 
	 * @return the wire number (1...112)
	 */
	public int get_Wire() {
		return _Wire;
	}

	/**
	 * Sets the wire number
	 * @param _Wire
	 */
	public void set_Wire(int _Wire) {
		this._Wire = _Wire;
	}

	/**
	 * 
	 * @return the time in ns
	 */
	public double get_Time() {
		return _Time;
	}

	/**
	 * Sets the time
	 * @param _Time
	 */
	public void set_Time(double _Time) {
		this._Time = _Time;
	}

	/**
	 * 
	 * @return error on the time in ns (4ns time window used by default in reconstructing simulated data)
	 */
	public double get_TimeErr() {
		return _TimeErr;
	}

	/**
	 * Sets the time
	 * @param _TimeErr
	 */
	public void set_TimeErr(double _TimeErr) {
		this._TimeErr = _TimeErr;
	}


	/**
	 * 
	 * @return the ID
	 */	
	public int get_Id() {
		return _Id;
	}

	/**
	 * Sets the hit ID.  The ID corresponds to the hit index in the EvIO column.
	 * @param _Id
	 */
	public void set_Id(int _Id) {
		this._Id = _Id;
	}


	/**
	 * 
	 * @return region (1...3)
	 */
    public int get_Region() {
     	return (int) (this._Superlayer+1)/2;
     }
    /**
     * 
     * @return superlayer 1 or 2 in region (1...3)
     */
    public int get_RegionSlayer() {
    	return (this._Superlayer+1)%2+1;
	}

    /**
	 * 
	 * @param arg0 the other hit
	 * @return an int used to sort a collection of hits by wire number. Sorting by wire is used in clustering.
	 */
	@Override
	public int compareTo(Hit arg0) {
		if(this._Wire>arg0._Wire) {
			return 1;
		} else {
			return 0;
		}
	}
		
	/**
	 * 
	 * @return print statement with hit information
	 */
	public String printInfo() {
		String s = "DC Hit: ID "+this.get_Id()+" Sector "+this.get_Sector()+" Superlayer "+this.get_Superlayer()+" Layer "+this.get_Layer()+" Wire "+this.get_Wire()+" Time "+this.get_Time();
		return s;
	}

}
