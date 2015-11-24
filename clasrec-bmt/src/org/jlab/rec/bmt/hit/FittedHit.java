package org.jlab.rec.bmt.hit;


/**
 * A hit that was used in a fitted track.
 * 
 * @author ziegler
 *
 */

public class FittedHit extends Hit implements Comparable<Hit> {


	/**
	 * 
	 * @param sector  (1...24)
	 * @param layer (1...6)
	 * @param strip (1...256)
	 * @param Edep (for gemc output without digitization)
	 */
	
	public FittedHit(int sector,int layer, int strip,
			double Edep) {
		super(sector, layer, strip, Edep);

	}
	
	private double _docaToTrk;              // 3-D distance of closest approach of the helix to the wire 
	private double _stripResolutionAtDoca;  // position resolution at distance of closest approach of the helix to the wire 
	
	private int _TrkgStatus = -1 ;			//  TrkgStatusFlag factor (-1: no fit; 0: global helical fit; 1: KF fit)


	public double get_docaToTrk() {
		return _docaToTrk;
	}
	public void set_docaToTrk(double _docaToTrk) {
		this._docaToTrk = _docaToTrk;
	}

	public double get_stripResolutionAtDoca() {
		return _stripResolutionAtDoca;
	}

	public void set_stripResolutionAtDoca(double _stripResolutionAtDoca) {
		this._stripResolutionAtDoca = _stripResolutionAtDoca;
	}

	/**
	 * 
	 * @return an integer representative of the stage of the pattern recognition and subsequent KF fit 
	 * for that hit.  -1: no fit; 0: global helical fit; 1: KF fit
	 */
	public int get_TrkgStatus() {
		return _TrkgStatus;
	}
	/**
	 * 
	 * @param trkgStatus is an integer representative of the stage of the pattern recognition and subsequent KF fit 
	 * for that hit. -1: no fit; 0: global helical fit; 1: KF fit
	 */
	public void set_TrkgStatus(int trkgStatus) {
		_TrkgStatus = trkgStatus;
	}

	
	/**
	 * 
	 * @param arg0 the other hit
	 * @return an int used to sort a collection of hits by layer number
	 */
	public int compareTo(FittedHit arg0) {
		if(this.get_Layer()>arg0.get_Layer()) {
			return 1;
		} else {
			return 0;
		}
	}
	
	public double _QualityFac;
	
	public double get_QualityFac() {
		return _QualityFac;
	}
	public void set_QualityFac(double QF) {
		_QualityFac = QF;
	}
	
	
	public double get_Residual() {
		if(get_stripResolutionAtDoca()==0)
			return Double.NaN;
		return get_docaToTrk()/get_stripResolutionAtDoca();
	}
	
	private int _AssociatedClusterID;
    private int _AssociatedCrossID;
    private int _AssociatedTrackID;
	
	public int get_AssociatedClusterID() {
		return _AssociatedClusterID;
	}

	public void set_AssociatedClusterID(int _AssociatedClusterID) {
		this._AssociatedClusterID = _AssociatedClusterID;
	}

	public int get_AssociatedCrossID() {
		return _AssociatedCrossID;
	}

	public void set_AssociatedCrossID(int _AssociatedCrossID) {
		this._AssociatedCrossID = _AssociatedCrossID;
	}

	public int get_AssociatedTrackID() {
		return _AssociatedTrackID;
	}

	public void set_AssociatedTrackID(int _AssociatedTrackID) {
		this._AssociatedTrackID = _AssociatedTrackID;
	}
	public void set_ParametersOfHit(Hit hit) {
		this.set_Id(hit.get_Id());
		if(this.get_Layer()%2==0) { // C-dtectors
			// set z
			this.set_Z(hit.get_Z());
			//  z err
			this.set_ZErr(hit.get_ZErr());
			// strip number taking into account Lorentz angle correction (for Z detectors)
			this.set_LCStrip(hit.get_LCStrip());
		}
		if(this.get_Layer()%2==1) { // Z-dtectors
			//set phi
			this.set_Phi(hit.get_Phi());
			//set err on phi
			this.set_PhiErr(hit.get_PhiErr());
		}
	}
	
	
	
}
