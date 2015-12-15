package org.jlab.rec.bmt.cluster;

import java.util.ArrayList;

import org.jlab.rec.bmt.hit.FittedHit;
import org.jlab.rec.bmt.hit.Hit;

/**
 *  A cluster consists of an array of hits that are grouped together according to the algorithm of the ClusterFinder class
 * @author ziegler
 *
 */
public class Cluster extends ArrayList<FittedHit> {

	private static final long serialVersionUID = 9153980362683755204L;


	private int _Sector;      							//	    sector[1...3]
	private int _Layer;    	 							//	    layer [1,...6]
	private int _Id;									//		cluster Id

	/**
	 * 
	 * @param sector the sector (1...24)
	 * @param layer the layer (1...8)
	 * @param cid the cluster ID, an incremental integer corresponding to the cluster formed in the series of clusters
	 */
	public Cluster(int sector, int layer, int cid) {
		this._Sector = sector;
		this._Layer = layer;
		this._Id = cid;
		
		
	}
	/**
	 * 
	 * @param hit  the first hit in the list of hits composing the cluster
	 * @param cid  the id of the cluster
	 * @return an array list of hits characterized by its sector, layer and id number.
	 */
	public Cluster newCluster(Hit hit, int cid) {
			return new Cluster(hit.get_Sector(), hit.get_Layer(), cid);
	}

	/**
	 * 
	 * @return the sector of the cluster (1...24)
	 */
	public int get_Sector() {
		return _Sector;
	}
	
	/**
	 * 
	 * @param _Sector  sector of the cluster (1...24)
	 */
	public void set_Sector(int _Sector) {
		this._Sector = _Sector;
	}
	
	/**
	 * 
	 * @return the layer of the cluster (1...8)
	 */
	public int get_Layer() {
		return _Layer;
	}
	
	/**
	 * 
	 * @param _Layer  the layer of the cluster (1...6)
	 */
	public void set_Layer(int _Layer) {
		this._Layer = _Layer;
	}
	
	/**
	 * 
	 * @return the id of the cluster
	 */
	public int get_Id() {
		return _Id;
	}
	
	/**
	 * 
	 * @param _Id  the id of the cluster
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
     * @return cluster info. about location and number of hits contained in it
     */
	public String printInfo() {
		String s = "BMT cluster: ID "+this.get_Id()+" Sector "+this.get_Sector()+" Layer "+this.get_Layer()+" Size "+this.size();
		return s;
	}
	
	
	private double _Centroid;
	private double _TotalEnergy;
	private double _Phi;  // for Z-detectors
	private double _PhiErr; 
	private double _Z;    // for C-detectors
	private double _ZErr;
	
	/**
	 * sets energy-weighted  parameters; these are the strip centroid value, the cog phi for Z detectors and the cog z for C detectors
	 */
	public void calc_CentroidParams() {
		
		double stripNumCent = 0;
		double phiCent = 0;
		double phiErrCent = 0;
		double zCent = 0;
		double zErrCent = 0;
		

		double totEn = 0.;
		double weightedStrp = 0;
		double weightedPhi = 0;
		double weightedPhiErrSq = 0;
		double weightedZ = 0;
		double weightedZErrSq = 0;
		
		int nbhits = this.size();
		
		if(nbhits != 0) {
			
			int layer = this.get_Layer();
			
			for(int i=0;i<nbhits;i++) {
				FittedHit thehit = this.get(i);
				double strpEn = thehit.get_Edep();
				int strpNb = -1;
				
				if(layer%2==0) { // C-detectors
					strpNb = thehit.get_Strip();
					weightedZ+= strpEn*(double)thehit.get_Z();
					weightedZErrSq+= (strpEn*(double)thehit.get_ZErr())*(strpEn*(double)thehit.get_ZErr());
				}
				if(layer%2==1) { // Z-detectors
					//strpNb = thehit.get_LCStrip();
					strpNb = thehit.get_Strip();
					weightedPhi+= strpEn*(double)thehit.get_Phi();
					weightedPhiErrSq+= (strpEn*(double)thehit.get_PhiErr())*(strpEn*(double)thehit.get_PhiErr());
				}
				
				totEn += strpEn;
			    weightedStrp+= strpEn*(double)thehit.get_Strip();	
			    
			}
			if(totEn==0) {
				System.err.println(" Cluster energy is null .... exit");
				return;
			}
			stripNumCent = weightedStrp/totEn;
			phiCent = weightedPhi/totEn;
			zCent = weightedZ/totEn;
			phiErrCent = weightedPhiErrSq/totEn;
			zErrCent = weightedZErrSq/totEn;
		}
		
		_TotalEnergy = totEn;
		_Centroid = stripNumCent;

		if( this.get_Layer()%2==1) {
			
			_Phi = phiCent;
			_PhiErr = phiErrCent;
		}
		if( this.get_Layer()%2==0) {
			_Z = zCent;
			_ZErr = zErrCent;
		}
		
	}
	
	/**
	 * 
	 * sets the cluster total energy
	 */
	public void calc_TotalEnergy() {

		double clusEn =0;
		
		int nbhits = this.size();
		if(nbhits != 0) {
			
			double totEn = 0.;
			
			for(int i=0;i<nbhits;i++) {
				FittedHit thehit = this.get(i);
				double strpEn = thehit.get_Edep();
				totEn = totEn + strpEn;
			}
			clusEn = totEn;
			
		}
		_TotalEnergy = clusEn;
		
	}

	public double get_Centroid() {
		return _Centroid;
	}
	public void set_Centroid(double _Centroid) {
		this._Centroid = _Centroid;
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
	public double get_TotalEnergy() {
		return _TotalEnergy;
	}
	public void set_TotalEnergy(double _TotalEnergy) {
		this._TotalEnergy = _TotalEnergy;
	}
	
	
	private int _MinStrip;
	private int _MaxStrip;
	private int _SeedStrip;
	private double _SeedEnergy;
	
	public void calc_MinStrip() {
		int n = 100000;
		for(int i = 0; i<this.size(); i++) {
			if(this.get(i).get_Strip()<=n) 
				n = this.get(i).get_Strip();
		}
		set_MinStrip(n);
	}
	
	public void calc_MaxStrip() {
		int n = -1;
		for(int i = 0; i<this.size(); i++) {
			if(this.get(i).get_Strip()>=n) 
				n = this.get(i).get_Strip();
		}
		set_MaxStrip(n);
	}
	
	public void calc_Seed() {
		double e = -1;
		int s = -1;
		for(int i = 0; i<this.size(); i++) {
			if(this.get(i).get_Edep()>=e) {
				e = this.get(i).get_Edep();
				s = this.get(i).get_Strip();
			}
		}
		set_SeedStrip(s);
		set_SeedEnergy(e);
	}
	
	public int get_MinStrip() {
		return _MinStrip;
	}
	public void set_MinStrip(int _MinStrip) {
		this._MinStrip = _MinStrip;
	}
	public int get_MaxStrip() {
		return _MaxStrip;
	}
	public void set_MaxStrip(int _MaxStrip) {
		this._MaxStrip = _MaxStrip;
	}
	
	
	public int get_SeedStrip() {
		return _SeedStrip;
	}
	public void set_SeedStrip(int _SeedStrip) {
		this._SeedStrip = _SeedStrip;
	}
	public double get_SeedEnergy() {
		return _SeedEnergy;
	}
	public void set_SeedEnergy(double _SeedEnergy) {
		this._SeedEnergy = _SeedEnergy;
	}
	public void set_Parameters() {						
		this.calc_CentroidParams();
		this.calc_MinStrip();
		this.calc_MaxStrip();
		this.calc_Seed();
	}

	
	
	
	
    private int _AssociatedCrossID;
    private int _AssociatedTrackID;
	
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
}

	

