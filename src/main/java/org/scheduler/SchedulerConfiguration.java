package org.scheduler;

/** SchedulerConfiguration
 * 
 * @author chris
 *
 */
public class SchedulerConfiguration {
	
	double freqency_Hz = 1.0d;
	int timeout_s = 15;
	double simulationSpeed = 1.0d;

	/** Ctor
	 * 
	 * @param freqency_Hz
	 */
	public SchedulerConfiguration(double freqency_Hz) {
		super();
		this.freqency_Hz = freqency_Hz;
	}

	
	
	/** Ctor
	 * 
	 * @param freqency_Hz
	 * @param startDate
	 */
	public SchedulerConfiguration(double freqency_Hz, double simulationSpeed) {
		super();
		this.freqency_Hz = freqency_Hz;
		this.simulationSpeed = simulationSpeed;
	}

	public double getFreqency_Hz() {
		return freqency_Hz;
	}

	public int getTimeout_s() {
		return timeout_s;
	}

	/**
	 * 0.5x is half realtime
	 * 1.0x is realtime
	 * 2.0x is double realtime
	 * 
	 * @return
	 */
	public double getSimulationSpeed() {
		return simulationSpeed;
	}

}


