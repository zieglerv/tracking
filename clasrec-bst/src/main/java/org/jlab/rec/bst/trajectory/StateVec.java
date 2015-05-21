package org.jlab.rec.bst.trajectory;

import Jama.*;
/**
 * A StateVec describes a cross measurement in the BST.  It is characterized by a point in the lab
 *  coordinate system at each module plane  and by unit tangent vectors to the track at the state-vec point.
 * @author ziegler
 *
 */
public class StateVec extends Matrix {
	
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1874984192960130771L;


	/**
	 * Instantiates a new  vec.
	 */
	public StateVec() {
		super(6,1);
	}
	
	
	
	/**
	 * Sets the.
	 *
	 * @param V the v
	 */
	public void set(StateVec V) {
		set(0,0,V.x());
		set(1,0,V.y());
		set(2,0,V.z());
		set(3,0,V.ux());
		set(4,0,V.uy());
		set(5,0,V.uz());
	}
	
	

	private int _planeIdx;
	/**
	 * 
	 * @return the wire plane index in the series of planes used in the trajectory
	 */
	public int get_planeIdx() {
		return _planeIdx;
	}

	/**
	 * Sets the wire plane index in the series of planes used in the trajectory
	 * @param _planeIdx wire plane index in the series of planes used in the trajectory
	 */
	public void set_planeIdx(int _planeIdx) {
		this._planeIdx = _planeIdx;
	}

	/**
	 * Sets the stateVec 
	 * @param y the _y
	 * @param x the _x
	 * @param z the _z
	 * @param ux the x-component of the tangent to the helix at point (x,y,z) 
	 * @param uy the y-component of the tangent to the helix at point (x,y,z) 
	 * @param uz the z-component of the tangent to the helix at point (x,y,z) 
	 */
	public void set(double x, double y, double z, double ux, double uy, double uz) {
		set(0,0,x);
		set(1,0,y);
		set(2,0,z);  
		set(3,0,ux); 
		set(4,0,uy); 
		set(5,0,uz); 
	}

	/**
	 * Instantiates a new stateVec
	 * @param y the _y
	 * @param x the _x
	 * @param z the _z
	 * @param ux the x-component of the tangent to the helix at point (x,y,z) 
	 * @param uy the y-component of the tangent to the helix at point (x,y,z) 
	 * @param uz the z-component of the tangent to the helix at point (x,y,z) 
	 */
	public StateVec(double x, double y, double z, double ux, double uy, double uz) {
		super(6,1);
		set(0,0,x);
		set(1,0,y);
		set(2,0,z);  
		set(3,0,ux); 
		set(4,0,uy); 
		set(5,0,uz); 
	}
    
	/**
	 * Instantiates a new stateVec
	 *
	 * @param v the v
	 */
	public StateVec(StateVec v) {
		super(6,1);
		set(0,0,v.x());
		set(1,0,v.y());
		set(2,0,v.z());  
		set(3,0,v.ux()); 
		set(4,0,v.uy()); 
		set(5,0,v.uz()); 
	}


	/**
	 * Instantiates a new  StateVec.
	 *
	 * @param m the m
	 */
	private StateVec(Matrix m) { //needed since Jama.Matrix cannot be casted into StateVec		
		super(4,1);
		set(0,0,m.get(0, 0));
		set(1,0,m.get(1, 0));
		set(2,0,m.get(2, 0));
		set(3,0,m.get(3, 0));
		set(4,0,m.get(4, 0));
		set(5,0,m.get(5, 0));
	}


	/**
	 * Description of x().
	 *
	 * @return the x component
	 */
	public double x() {
		return(get(0,0));
	}

	/**
	 * Description of y().
	 *
	 * @return the y component
	 */ 

	public double y() {
		return(get(1,0));
	}

	/**
	 * Description of z().
	 *
	 * @return the z component
	 */ 
	public double z() {
		return(get(2,0));
	}

	/**
	 * Description of ux().
	 *
	 * @return the ux component
	 */ 
	public double ux() {
		return(get(3,0));
	}
	
	/**
	 * Description of uy().
	 *
	 * @return the uy component
	 */ 
	public double uy() {
		return(get(4,0));
	}
	
	/**
	 * Description of uz().
	 *
	 * @return the uz component
	 */ 
	public double uz() {
		return(get(5,0));
	}

}
