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
	
	public void configure() {
		write(0x02, 0x00);
		write(0x04, 0x08);
		write(0x1c, 0xb0);
	}
	
	public boolean write(int register, int data)
	{
		return i2c.write(register, data);
	}
	
	public int read(int register) {
		i2c.writeBulk(new byte[] {(byte) register});
		i2c.readOnly(buffer, 1);
		int result = buffer[0] & 0xff;
		return result;
	}
	
	public void startMeasuring() {
		write(0x11, 0xff);
		write(0x00, 0x04);
	}
	
	public void stopMeasuring() {
		write(0x11, 0x01);
	}
	
	public void check() {
		System.out.println("LiDAR ping: " + !i2c.addressOnly());
	}
	public int getDistance() {
		
		return (read(0x0f) << 8) + (read(0x10));

	}
	
	
}
