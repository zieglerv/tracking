package org.jlab.rec.bst.trajectory;

import java.util.ArrayList;

import org.jlab.rec.bst.Constants;
import org.jlab.rec.bst.cross.Cross;

/**
 * The trajectory is a set of state vectors at BST planes along the particle path.  * A StateVec describes a cross measurement in the BST.  It is characterized by a point in the DC
 * tilted coordinate system at each wire plane (i.e. constant z) and by unit tangent vectors in the x and y 
 * directions in that coordinate system.  
 * @author ziegler
 *
 */
public class Trajectory extends ArrayList<Cross> {

	public Trajectory() {
		
	}

	// Sector for each region
	private int[] _Sector = new int[Constants.NLAYR];      							
	private int _Id;									   					
	private ArrayList<StateVec> _Trajectory;
	
	
	public int[] get_Sector() {
		return _Sector;
	}


	public void set_Sector(int[] _Sector) {
		this._Sector = _Sector;
	}


	public int get_Id() {
		return _Id;
	}


	public void set_Id(int _Id) {
		this._Id = _Id;
	}


	
	public ArrayList<StateVec> get_Trajectory() {
		return _Trajectory;
	}


	public void set_Trajectory(ArrayList<StateVec> _Trajectory) {
		this._Trajectory = _Trajectory;
	}



	/**
	 * 
	 */
	private static final long serialVersionUID = 358913937206455870L;
	

	public BSTSwimmer bstSwim = new BSTSwimmer();
	
	public double calc_Field(double x_cm, double y_cm, double z_cm) {

		if(bstSwim.Bfield(x_cm, y_cm, z_cm)!=null) {
			double B = bstSwim.Bfield(0, 0, 10).z();
			return Math.abs(B);
		} else {
			System.err.println("FATAL ERROR MAG FIELD NOT FOUND!!!");
			return Double.NaN; // Default
		}
	}


}
