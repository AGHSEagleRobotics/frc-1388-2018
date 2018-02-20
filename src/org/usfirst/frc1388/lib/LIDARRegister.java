package org.usfirst.frc1388.lib;

public enum LIDARRegister {
	ACQ_COMMAND(0x00),
	OUTER_LOOP_COUNT(0x11),
	MEASURE_DELAY(0x45),
	ACQ_CONFIG_REG(0x04),
	REF_COUNT_VAL(0x12),
	DISTANCE(0x8f);
	
	private int register;
	
	private LIDARRegister(int register) {
		this.register = register;
	}
	
	public int getValue() {
		return register;
	}
}
