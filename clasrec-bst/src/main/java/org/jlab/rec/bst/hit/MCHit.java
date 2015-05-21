package org.jlab.rec.bst.hit;
/**
 * A MC hit defined as in data with additional truth information 
 * @author ziegler
 *
 */
public class MCHit extends Hit{

	/**
	 * 
	 * @param sector  (1...24)
	 * @param layer (1...8)
	 * @param strip (1...256)
	 * @param Edep(for gemc output without digitization)
	 */
	public MCHit(int sector, int layer, int strip, double Edep) {
		super(sector, layer, strip, Edep);
		
	}

	private double _x;
	private double _y;
	private double _z;
	
	public double get_x() {
		return _x;
	}
	public void set_x(double x_avg) {
		this._x = x_avg;
	}
	public double get_y() {
		return _y;
	}
	public void set_y(double _y) {
		this._y = _y;
	}
	public double get_z() {
		return _z;
	}
	public void set_z(double _z) {
		this._z = _z;
	}
	
}
