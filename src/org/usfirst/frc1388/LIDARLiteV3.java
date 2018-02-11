package org.usfirst.frc1388;

import edu.wpi.first.wpilibj.I2C;

public class LIDARLiteV3 { 
	
	private I2C i2c;
	private byte[] buffer;
	private static final int k_deviceAddress = 0x62;
   
	public LIDARLiteV3(I2C.Port port) {
		i2c = new I2C(port, k_deviceAddress);
		buffer = new byte[2];
	}
	
	public void configure() {
		i2c.write(0x11, 0x32);
	}
	
	public boolean read(int address) {
		boolean ret = !i2c.read(address, 1, buffer);
		System.out.println(address + ": " + buffer[0] + "     " + ret);
		return ret;
	}
	
	public int getMeasurement() {
		return buffer[0];
	}
	
	
}
