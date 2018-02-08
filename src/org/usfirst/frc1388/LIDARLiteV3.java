package org.usfirst.frc1388;

import java.nio.ByteBuffer;

import edu.wpi.first.wpilibj.I2C.Port;
import edu.wpi.first.wpilibj.hal.I2CJNI;

public class LIDARLiteV3 {
	
	// default address of the slave device (LIDAR SENSOR) as defined in data sheet
	private static final byte k_slaveAddress = 0x62;
	
	// byte representation of port on roboRIO
	private final byte m_port;
	
	// ByteBuffer with size of 2, where the 1st byte is the address and the 2nd byte is the value
	private final ByteBuffer m_buffer = ByteBuffer.allocateDirect(2);
	
	// public constructor for use in RobotMap
	public LIDARLiteV3(Port port) {
		m_port = (byte) port.value;
	}
	
	// initial setup and measuring command
	public void startMeasuring() {
		
		// configure bit 5 using bitwise OR to read 1
		// this configures the LIDAR sensor to use a custom MEASURE_DELAY as opposed to default value
		writeRegister(0x04, 0x08 | 0x20);
		
		writeRegister(0x45, 0x50);
		// register 0x11 configures # of times device retriggers self
		// meaning 0x11 sets the # of times a measurement upon being called
		// seting this register to the maximum value enables free running mode, which
		// tells the device to take infinite measurements at a rate specificed by MEASURE_DELAY (100Hz default)
		writeRegister(0x11, 0xff);
		
		// take distance measurement with receiver bias correction
		// bias correction accounts for some estimate error (device will be more accurate), yet
		// this may add some processing time and thus may cause slower measurement rate
		writeRegister(0x00, 0x04);
	}

	public void stopMeasuring() {
		
		// set retrigger count to 1; the device will take one more measurement then stop
		writeRegister(0x11, 0x00);
	}

	public int getDistance() {
		// address 0x8f is not defined in the datasheet explicitly, it is
		// the address 0x0f with its most significant bit set to 1, which
		// triggers automatic incrementing of the address (to read the high and low)
		return readShort(0x8f);
	}

	private int writeRegister(int address, int value) {
		// stores the address and value in the buffer
		m_buffer.put(0, (byte) address);
		m_buffer.put(1, (byte) value);

		// uses the wrapper class to write the data to the address, lack of documentation
		// for significance of value the method returns (I'd imagine it's the byte written or an acknowledgement byte)
		return I2CJNI.i2CWrite(m_port, k_slaveAddress, m_buffer, (byte) 2);
	}

	/*
	 * Constructs a 16-bit short value from two addresses: the address provided in @param address,
	 * and that byte incremented once, with the high byte being the former and the low byte being the latter
	 * 
	 * @param address to be read from
	 */
	private short readShort(int address) {

		m_buffer.put(0, (byte) address);
		I2CJNI.i2CWrite(m_port, k_slaveAddress, m_buffer, (byte) 1);
		I2CJNI.i2CRead(m_port, k_slaveAddress, m_buffer, (byte) 2);
		return m_buffer.getShort(0);
	}

}
