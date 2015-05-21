package org.jlab.rec.bst.cross;

import java.util.ArrayList;

import org.jMath.Vector.threeVec;
import org.jlab.rec.bst.Constants;
import org.jlab.rec.bst.Geometry;
import org.jlab.rec.bst.cluster.Cluster;

/**
 * The crosses are objects used to find tracks and are characterized by a 3-D point and a direction unit vector.
 * @author ziegler
 *
 */
public class Cross extends ArrayList<Cluster> implements Comparable<Cross> {

	/**
	 * serial id
	 */
	private static final long serialVersionUID = 5317526429163382618L;

	/**
	 * 
	 * @param sector the sector (1...6)
	 * @param region the region (1...3)
	 * @param rid the cross ID (if there are only 3 crosses in the event, the ID corresponds to the region index
	 */
	public Cross(int sector, int region, int rid) {
		this._Sector = sector;
		this._Region = region;
		this._Id = rid;
	}
	
	private int _Sector;      							//	    sector[1...6]
	private int _Region;    		 					//	    region [1,...3]
	private int _Id;									//		cross Id

	// point parameters:
	private threeVec _Point;
	private threeVec _PointErr;
	private threeVec _Point0;
	private threeVec _PointErr0;
	private threeVec _Dir;
	private threeVec _DirErr;
	
	/**
	 * 
	 * @return the sector of the cross
	 */
	public int get_Sector() {
		return _Sector;
	}

	/**
	 * Sets the sector
	 * @param _Sector  the sector of the cross
	 */
	public void set_Sector(int _Sector) {
		this._Sector = _Sector;
	}

	/**
	 * 
	 * @return the region of the cross
	 */
	public int get_Region() {
		return _Region;
	}

	/**
	 * Sets the region
	 * @param _Region  the region of the cross
	 */
	public void set_Region(int _Region) {
		this._Region = _Region;
	}

	/**
	 * 
	 * @return the id of the cross
	 */
	public int get_Id() {
		return _Id;
	}

	/**
	 * Sets the cross ID
	 * @param _Id  the id of the cross
	 */
	public void set_Id(int _Id) {
		this._Id = _Id;
	}

	/**
	 * 
	 * @return a 3-D point characterizing the position of the cross in the tilted coordinate system.
	 */
	public threeVec get_Point0() {
		return _Point0;
	}

	/**
	 * Sets the cross 3-D point 
	 * @param _Point  a 3-D point characterizing the position of the cross in the tilted coordinate system.
	 */
	public void set_Point0(threeVec _Point) {
		this._Point0 = _Point;
	}

	/**
	 * 
	 * @return a 3-dimensional error on the 3-D point characterizing the position of the cross in the tilted coordinate system.
	 */
	public threeVec get_PointErr0() {
		return _PointErr0;
	}

	/**
	 * Sets a 3-dimensional error on the 3-D point 
	 * @param _PointErr a 3-dimensional error on the 3-D point characterizing the position of the cross in the tilted coordinate system.
	 */
	public void set_PointErr0(threeVec _PointErr) {
		this._PointErr0 = _PointErr;
	}


	/**
	 * 
	 * @return a 3-D point characterizing the position of the cross in the tilted coordinate system.
	 */
	public threeVec get_Point() {
		return _Point;
	}

	/**
	 * Sets the cross 3-D point 
	 * @param _Point  a 3-D point characterizing the position of the cross in the tilted coordinate system.
	 */
	public void set_Point(threeVec _Point) {
		this._Point = _Point;
	}

	/**
	 * 
	 * @return a 3-dimensional error on the 3-D point characterizing the position of the cross in the tilted coordinate system.
	 */
	public threeVec get_PointErr() {
		return _PointErr;
	}

	/**
	 * Sets a 3-dimensional error on the 3-D point 
	 * @param _PointErr a 3-dimensional error on the 3-D point characterizing the position of the cross in the tilted coordinate system.
	 */
	public void set_PointErr(threeVec _PointErr) {
		this._PointErr = _PointErr;
	}

	/**
	 * 
	 * @return the cross unit direction vector
	 */
	public threeVec get_Dir() {
		return _Dir;
	}

	/**
	 * Sets the cross unit direction vector
	 * @param _Dir the cross unit direction vector
	 */
	public void set_Dir(threeVec _Dir) {
		this._Dir = _Dir;
	}

	/**
	 * 
	 * @return the cross unit direction vector
	 */
	public threeVec get_DirErr() {
		return _DirErr;
	}

	/**
	 * Sets the cross unit direction vector
	 * @param _DirErr the cross unit direction vector
	 */
	public void set_DirErr(threeVec _DirErr) {
		this._DirErr = _DirErr;
	}

	/**
	 * 
	 * @return serialVersionUID
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}


	/**
	 * Sorts crosses by azimuth angle values
	 */
	@Override
	public int compareTo(Cross arg) {
		
		if(this.get_Point().phi()<arg.get_Point().phi()) {
			return 1;
		} else {
			return 0;
		}
	}

	private Cluster _clus1;
	private Cluster _clus2;
	
	/**
	 * Set the first cluster (corresponding to the first superlayer in a region)
	 * @param seg1 the Cluster (in the first superlayer) which is used to make a cross
	 */
	public void set_Cluster1(Cluster seg1) {
		this._clus1 = seg1;
	}

	/**
	 * Set the second Cluster (corresponding to the second superlayer in a region)
	 * @param seg2 the Cluster (in the second superlayer) which is used to make a cross
	 */
	public void set_Cluster2(Cluster seg2) {
		this._clus2 = seg2;
	}

	/**
	 * 
	 * @return he Cluster (in the first superlayer) which is used to make a cross
	 */
	public Cluster get_Cluster1() {
		return _clus1;
	}
	
	/**
	 * 
	 * @return the Cluster (in the second superlayer) which is used to make a cross
	 */
	public Cluster get_Cluster2() {
		return _clus2;
	}

	/**
	 * Sets the cross parameters: the position and direction unit vector
	 */
	public void set_CrossParams(threeVec dirAtBstPlane, Geometry geo) {
		
		Cluster inlayerclus = this.get_Cluster1();
		Cluster outlayerclus = this.get_Cluster2();

		double[] Params = geo.getCrossPars(outlayerclus.get_Sector(), outlayerclus.get_Layer(), 
				inlayerclus.get_Centroid(), outlayerclus.get_Centroid(), "lab", dirAtBstPlane);
		
		threeVec interPoint = new threeVec(Params[0], Params[1], Params[2]);
				
		threeVec interPointErr = new threeVec(Params[3], Params[4], Params[5]);
			
		if(dirAtBstPlane==null) {
			this.set_Point0(interPoint);		
			this.set_PointErr0(interPointErr);
		}
		this.set_Point(interPoint);	
		this.set_Dir(dirAtBstPlane);
		this.set_PointErr(interPointErr);
		
	}

	
  
	/**
	 * 
	 * @return the track info.
	 */
	public String printInfo() {
		String s = "BST cross: ID "+this.get_Id()+" Sector "+this.get_Sector()+" Region "+this.get_Region()
				+" Point "+this.get_Point().toString();
		return s;
	}

	public int getCosmicsRegion() {
		
		int theRegion = 0;
		
		if(this.get_Point0().rt()-(Constants.MODULERADIUS[6][0]+Constants.MODULERADIUS[7][0])*0.5<15) {
			if(this.get_Point0().y()>0) {
				theRegion = 8;
			} else {
				theRegion =1;
			}	
		}
		
		if(this.get_Point0().rt()-(Constants.MODULERADIUS[4][0]+Constants.MODULERADIUS[5][0])*0.5<15) {
			if(this.get_Point0().y()>0) {
				theRegion = 7;
			} else {
				theRegion =2;
			}	
		}
		
		if(this.get_Point0().rt()-(Constants.MODULERADIUS[2][0]+Constants.MODULERADIUS[3][0])*0.5<15) {
			if(this.get_Point0().y()>0) {
				theRegion = 6;
			} else {
				theRegion =3;
			}	
		}
		
		if(this.get_Point0().rt()-(Constants.MODULERADIUS[0][0]+Constants.MODULERADIUS[1][0])*0.5<15) {
			if(this.get_Point0().y()>0) {
				theRegion = 5;
			} else {
				theRegion =4;
			}	
		}
	
		return theRegion;
	}
	
}
