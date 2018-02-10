package org.usfirst.frc1388;

import edu.wpi.first.wpilibj.I2C;

public class LIDARLiteV3 { 
	
	private I2C i2c;
	private static final int k_deviceAddress = 0x62;
   
	public LIDARLiteV3(I2C.Port port) {
		i2c = new I2C(port, k_deviceAddress);
	}
	
	
}
