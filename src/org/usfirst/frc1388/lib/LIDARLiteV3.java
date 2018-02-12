package org.usfirst.frc1388.lib;

import edu.wpi.first.wpilibj.I2C;

public class LIDARLiteV3 { 
	
	private I2C i2c;
	private byte[] buffer;
	private static final int k_deviceAddress = 0x62;
   
	public LIDARLiteV3(I2C.Port port) {
		i2c = new I2C(port, k_deviceAddress);
		buffer = new byte[2];
	}
	
	public boolean write(LIDARRegister register, int data)
	{
		return i2c.write(register.getValue(), data);
	}
	
	public int read(LIDARRegister register) {
		i2c.writeBulk(new byte[] {(byte) register.getValue()});
		i2c.readOnly(buffer, 1);
		int result = buffer[0] & 0xff;
		return result;
	}
	
	public void startMeasuring() {
		write(LIDARRegister.ACQ_COMMAND, 0x04);
	}
	
	public void stopMeasuring() {
		write(LIDARRegister.OUTER_LOOP_COUNT, 0x01);
	}
	
	public void check() {
		System.out.println("LiDAR ping: " + !i2c.addressOnly());
	}
	public void getDistance() {
		//nothing yet
	}
	
	
}
