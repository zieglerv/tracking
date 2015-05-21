package org.jlab.rec.bst.signal;

/**
 * A DC signal charaterized by crate, slot, channel and ADC value. This is not used for simulated events
 * @author ziegler
 *
 */
public class Signal {
	
	public Signal(int crate, int slot, int channel, int ADC) {
		this._Channel = channel;
		this._Crate = crate;
		this._Slot = slot;
		this._ADC = ADC;
	}
	
	private int _Crate;
	private int _Slot;
	private int _Channel;
	private int _ADC;

	public int get_Crate() {
		return _Crate;
	}
	public void set_Crate(int _Crate) {
		this._Crate = _Crate;
	}
	public int get_Slot() {
		return _Slot;
	}
	public void set_Slot(int _Slot) {
		this._Slot = _Slot;
	}
	public int get_Channel() {
		return _Channel;
	}
	public void set_Channel(int _Channel) {
		this._Channel = _Channel;
	}
	public int get_ADC() {
		return _ADC;
	}
	public void set_ADC(int _ADC) {
		this._ADC = _ADC;
	}


	public double get_Sector(int crate, int slot, int channel) {
		return -1;
	}
	
	public int get_Superlayer(int crate, int slot, int channel) {
		return -1;
	}
	
	public int get_Layer(int crate, int slot, int channel) {
		return -1;
	}
	
	public int get_Wire(int crate, int slot, int channel) {
		return -1;
	}
	
	public double get_Time(int ADC) {
		return -1;
	}
	

}
