package com.jilgen.yourface;

public class InternalStats {
	private int _id;
	private double _time;
	private int _batteryStrength;
	private int _signalStrength;
	private int _procCount;
    private int _batteryVoltage;
    private int _batteryTemperature;
    private long _bytes;
	
	public InternalStats() {
	}

    public InternalStats(int _batteryStrength, int _signalStrength, int _procCount, int _batteryVoltage, int _batteryTemperature, long _bytes ) {
        this._batteryStrength=_batteryStrength;
        this._signalStrength=_signalStrength;
        this._procCount=_procCount;
        this._batteryVoltage=_batteryVoltage;
        this._batteryTemperature=_batteryTemperature;
        this._bytes=_bytes;
    }

    public InternalStats(int _batteryStrength, int _signalStrength, int _procCount, int _batteryVoltage, int _batteryTemperature ) {
        this._batteryStrength=_batteryStrength;
        this._signalStrength=_signalStrength;
        this._procCount=_procCount;
        this._batteryVoltage=_batteryVoltage;
        this._batteryTemperature=_batteryTemperature;
    }

	public InternalStats(int _batteryStrength, int _signalStrength, int _procCount) {
		this._batteryStrength=_batteryStrength;
		this._signalStrength=_signalStrength;
		this._procCount=_procCount;
	}
	
	public InternalStats(float _time, int _batteryStrength, int _signalStrength) {
		this._time=_time;
		this._batteryStrength=_batteryStrength;
		this._signalStrength=_signalStrength;
	}	
	
	public int getID() {
		return this._id;
	}
	
	public void setID(int _id) {
		this._id = _id;
	}
	
	public double getTime() {
		return this._time;
	}
	
	public void setTime(long _time) {
		this._time=_time;
	}
	
	public int getBatteryStrength() {
		return this._batteryStrength;
	}
	
	public void setBatteryStrength( int _batteryStrength) {
		this._batteryStrength=_batteryStrength;
	}

    public int getBatteryTemperature() {
        return this._batteryTemperature;
    }

    public void setBatteryTemperature( int _batteryTemperature) {
        this._batteryStrength=_batteryTemperature;
    }

    public int getBatteryVoltage() {
        return this._batteryVoltage;
    }

    public void setBatteryVoltage( int _batteryVoltage) {
        this._batteryStrength=_batteryVoltage;
    }

    public int getProcCount() {
		return this._procCount;
	}
	
	public void setProcCount( int _procCount ) {
		this._procCount=_procCount;
	}
	
	public void setSignalStrength( int _signalStrength ) {
		this._signalStrength=_signalStrength;
	}
	
	public int getSignalStrength( ) {
		return this._signalStrength;
	}

    public void setBytes( long _bytes ) {
        this._bytes=_bytes;
    }

    public int getBytes( ) {
        return this._signalStrength;
    }
}
