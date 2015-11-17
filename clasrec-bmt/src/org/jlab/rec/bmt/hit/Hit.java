package org.jlab.rec.bmt.hit;

import org.jlab.rec.bmt.Constants;
import org.jlab.rec.bmt.Geometry;

/**
 * A  hit characterized by layer, sector, wire number, and Edep.  
 * @author ziegler
 *
 */


public class Hit implements Comparable<Hit>{
	// class implements Comparable interface to allow for sorting a collection of hits by wire number values
	
	
	// constructors 
	
	/**
	 * @param sector (1...3)
	 * @param layer (1...6)
	 * @param strip (detector specific)
	 * @param Edep (for gemc output without digitization)
	 */
	public Hit(int sector, int layer, int strip, double Edep) {
		this._Sector = sector;
		this._Layer = layer;
		this._Strip = strip;
		this._Edep = Edep;	
	}
	
	
	private int _Sector;      							//	   sector
	private int _Layer;    	 							//	   layer 
	private int _Strip;    	 							//	   strip read from daq 
	private int _LCStrip;								//     strip number taking into account Lorentz angle correction (for Z detectors)
	private double _Phi;  								//     for Z-detectors, the azimuth angle at the strip midwidth
	private double _PhiErr;
	private double _Z;    								//     for C-detectors. the z position at the strip midwidth
	private double _ZErr;  
	private double _Edep;      							//     for simulation this corresponds to the energy deposited on the strip, in data it should be an ADC converted value
	private int _Id;									//	   Hit Id
	
	
	/**
	 * 
	 * @return the sector 
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
	 * @return the layer 
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
	 * @return the strip number 
	 */
	public int get_Strip() {
		return _Strip;
	}

	/**
	 * Sets the strip number
	 * @param _Strip
	 */
	public void set_Strip(int _Strip) {
		this._Strip = _Strip;
	}

	public int get_LCStrip() {
		return _LCStrip;
	}

	public void set_LCStrip(int _LCStrip) {
		this._LCStrip = _LCStrip;
	}

	/**
	 * 
	 * @param geo the geometry class
	 * Sets the Lorentz corrected phi and strip number for Z detectors, the z position for C detectors
	 */
	public void calc_StripParams(Geometry geo) {
		
		
		if(this.get_Layer()%2==0) { // C-dtectors
			// set z
			double z = geo.CRCStrip_GetZ(this.get_Sector(),this.get_Layer(), this.get_Strip());
			this.set_Z(z);
			// max z err
			this.set_ZErr(Constants.SigmaMax);
		}
		if(this.get_Layer()%2==1) { // Z-detectors
			double theMeasuredPhi = geo.CRZStrip_GetPhi(this.get_Sector(),this.get_Layer(), this.get_Strip());
			double theLorentzCorrectedAngle = geo.LorentzAngleCorr( theMeasuredPhi, this.get_Layer());
			// set the phi 
			this.set_Phi(theLorentzCorrectedAngle); 
			int theLorentzCorrectedStrip = geo.getZStrip(this.get_Layer(), theLorentzCorrectedAngle);
			// get the strip number after correcting for Lorentz angle
			this.set_LCStrip(theLorentzCorrectedStrip);
			double sigma = Constants.SigmaMax/Math.sqrt(Math.cos(Constants.ThetaL)); // max sigma for drift distance  (hDrift) = total gap from top to mesh
			
			int num_region = (int) (this.get_Layer()+1)/2 - 1; // region index (0...2) 0=layers 1&2, 1=layers 3&4, 2=layers 5&6double Z0=0;
			//max phi err
			double phiErr = (sigma/Math.cos(Constants.ThetaL)
					-(Constants.hDrift-Constants.CRZRADIUS[num_region])*Math.tan(Constants.ThetaL))/Constants.CRZRADIUS[num_region];
			this.set_PhiErr(phiErr);
		}
		
	}
	
	
	/**
	 * 
	 * @return the Edep in MeV
	 */
	public double get_Edep() {
		return _Edep;
	}

	/**
	 * Sets the Edep
	 * @param _Edep
	 */
	public void set_Edep(double _Edep) {
		this._Edep = _Edep;
	}


	public double get_Phi() {
		return _Phi;
	}

	public void set_Phi(double _Phi) {
		this._Phi = _Phi;
	}

	public double get_PhiErr() {
		return _PhiErr;
	}

	public void set_PhiErr(double _PhiErr) {
		this._PhiErr = _PhiErr;
	}

	public double get_Z() {
		return _Z;
	}

	public void set_Z(double _Z) {
		this._Z = _Z;
	}

	public double get_ZErr() {
		return _ZErr;
	}

	public void set_ZErr(double _ZErr) {
		this._ZErr = _ZErr;
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
	 * @return region (1...4)
	 */
    public int get_Region() {
     	return (int) (this._Layer+1)/2;
     }
    /**
     * 
     * @return superlayer 1 or 2 in region (1...4)
     */
    public int get_RegionSlayer() {
    	return (this._Layer+1)%2+1;
	}

    /**
	 * 
	 * @param arg0 the other hit
	 * @return an int used to sort a collection of hits by wire number. Sorting by wire is used in clustering.
	 */
	@Override
	public int compareTo(Hit arg0) {
		if(this._Strip>arg0._Strip) {
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
		String s = "BST Hit: ID "+this.get_Id()+" Sector "+this.get_Sector()+" Layer "+this.get_Layer()+" Strip "+this.get_Strip()+" Edep "+this.get_Edep();
		return s;
	}

	/**
	 * 
	 * @param otherHit
	 * @return a boolean comparing 2 hits based on basic descriptors; 
	 * returns true if the hits are the same
	 */
	public boolean isSameAs(FittedHit otherHit) {
		FittedHit thisHit = (FittedHit) this;
		boolean cmp = false;
		if(thisHit.get_Edep() == otherHit.get_Edep() 
				&& thisHit.get_Sector() == otherHit.get_Sector()
							&& thisHit.get_Layer() == otherHit.get_Layer()
									&& thisHit.get_Strip() == otherHit.get_Strip() )
			cmp = true;
		return cmp;
	}
	

}
