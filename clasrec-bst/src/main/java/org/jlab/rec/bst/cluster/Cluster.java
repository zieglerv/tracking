package org.jlab.rec.bst.cluster;


import java.util.ArrayList;

import org.jlab.rec.bst.Geometry;
import org.jlab.rec.bst.hit.FittedHit;
import org.jlab.rec.bst.hit.Hit;

/**
 *  A cluster in the BST consists of an array of hits that are grouped together according to the algorithm of the ClusterFinder class
 * @author ziegler
 *
 */
public class Cluster extends ArrayList<FittedHit> {

	private static final long serialVersionUID = 9153980362683755204L;


	private int _Sector;      							//	    sector[1...24]
	private int _Layer;    	 							//	    layer [1,...8]
	private int _Id;									//		cluster Id

	/**
	 * 
	 * @param sector the sector (1...24)
	 * @param superlayer the layer (1...8)
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
	 * @param _Superlayer  the layer of the cluster (1...6)
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
		String s = "BST cluster: ID "+this.get_Id()+" Sector "+this.get_Sector()+" Superlayer "+this.get_Layer()+" Size "+this.size();
		return s;
	}
	
	
	private double _Centroid;
	private double _TotalEnergy;

	
	/**
	 * 
	 * @return the energy-weighted  strip number 
	 */
	public void calc_Centroid() {
		
		double stripNumCent = 0;
		
		int nbhits = this.size();
		if(nbhits != 0) {
			
			double totEn = 0.;
			double weightedStrp = 0;
			
			for(int i=0;i<nbhits;i++) {
				Hit thehit = this.get(i);
				double strpEn = thehit.get_Edep();
				double strpNb = thehit.get_Strip();
				totEn = totEn + strpEn;
			    weightedStrp = weightedStrp + strpEn*strpNb;	
			}
			
		stripNumCent = weightedStrp/totEn;
		}
		
		_Centroid = stripNumCent;
		
	}
	
	/**
	 * 
	 * @return the cluster total energy
	 */
	public void calc_TotalEnergy() {

		double clusEn =0;
		
		int nbhits = this.size();
		if(nbhits != 0) {
			
			double totEn = 0.;
			
			for(int i=0;i<nbhits;i++) {
				Hit thehit = this.get(i);
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
		int n = 1000;
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
		this.calc_Centroid();
		this.calc_TotalEnergy();
		this.calc_MinStrip();
		this.calc_MaxStrip();
		this.calc_Seed();
	}

	
	
	/**
	 * 
	 * @param Z  z-coordinate of a point in the local coordinate system of a module
	 * @return the average resolution for a group of strips in a cluster
	 * 
	 */
	public double get_ResolutionAlongZ(double Z, Geometry geo) { 
		// returns the total resolution for a group of strips in a cluster
		// the single strip resolution varies at each point along the strip as a function of Z (due to the graded angle of the strips) and 
		// is smallest at the pitch implant at which is is simply Pitch/sqrt(12)
		int nbhits = this.size();
		if(nbhits == 0) 
			return 0;
		
		// average
		double res = 0;
		
		for(int i=0;i<nbhits;i++) {
			double rstrp = geo.getSingleStripResolution(this.get(i).get_Layer(), this.get(i).get_Strip(), Z);
			res += rstrp*rstrp;
		}
		return Math.sqrt(res);
	}

}

	

