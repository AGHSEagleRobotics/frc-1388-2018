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
	
	/*
	 * Configuration used on Arduino which produced best results, untested
	 * 
	public void configure() {
		write(0x02, 0x00);
		write(0x04, 0x08);
		write(0x1c, 0xb0);
	}
	*/
	
	/**
	 * Writes a byte to the selected register on the LIDAR
	 * <p>
	 * Sends 7-bit data command through I2C bus to the selected register, using declared I2C
	 * device address
	 * 
	 * @param register Register to write data to
	 * @param data Data to write to register
	 * 
	 * @return if the transaction was successful
	 */
	private boolean write(int register, int data)
	{
		return i2c.write(register, data);
	}
	
	/**
	 * Reads a byte from the selected register on the LIDAR
	 * <p>
	 * Writes to the register to select it, then does a readOnly() and places an unsigned
	 * version of the byte read into the buffer
	 * 
	 * @param register Register to read data from
	 * 
	 * @return unsigned byte read from register as integer
	 */
	private int read(int register) {
		i2c.writeBulk(new byte[] {(byte) register});
		i2c.readOnly(buffer, 1);
		int result = buffer[0] & 0xff;
		return result;
	}
	
	/**
	 * Configure the LIDAR to begin measuring
	 * <p>
	 * Sets the outer-loop-count option to free-running mode, meaning device re-trigger measurement
	 * process infinitely, then begin taking measurements
	 */
	public void startMeasuring() {
		write(0x11, 0xff);
		write(0x00, 0x04);
	}
	
	/**
	 * Configure the LIDAR to stop measuring
	 * <p>
	 * Set outer-loop-count to 1, which re-triggers the measurement process one more time
	 */
	public void stopMeasuring() {
		write(0x11, 0x01);
	}
	
	/**
	 * Ping the LIDAR via I2C
	 * <p>
	 * Print out a boolean representing whether or not LIDAR is found on the I2C bus
	 */
	public void check() {
		System.out.println("LiDAR ping: " + !i2c.addressOnly());
	}
	
	/**
	 * Get the distance measurement read by the LIDAR in centimeters
	 * <p>
	 * Read two bytes from register 0x0f and its increment, 0x10, and combine them to fom a 16-bit int distance
	 * 
	 * @return Integer distance in centimeters
	 */
	public int getDistance() {
		
		return (read(0x0f) << 8) + (read(0x10));
	}
	
	
}
