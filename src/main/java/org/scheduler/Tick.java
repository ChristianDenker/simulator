package org.scheduler;

import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalUnit;

public class Tick {
	/*
	 * the global tick counter is unique
	 */
	private static int GLOBAL_TICK_COUNTER = 0;
	
	/*
	 * current tick is local, so globaltick can be > currentTick
	 */
	private int currentTick = 0;
	private TemporalAdjuster temporalAdjuster = null;

	/** Ctor
	 * 
	 * @param amountToAdd
	 * @param temporalUnit
	 */
	public Tick(int amountToAdd, TemporalUnit temporalUnit) {

		this.temporalAdjuster = new TemporalAdjuster() {

			@Override
			public Temporal adjustInto(Temporal temporal) {
				Temporal t = temporal.plus(amountToAdd, temporalUnit);
				return t;
			}
			
		};
		
		GLOBAL_TICK_COUNTER++;
		this.currentTick = GLOBAL_TICK_COUNTER;
	}

	public int getCurrentTick() {
		return currentTick;
	}

	public TemporalAdjuster getTemporalAdjuster() {
		return temporalAdjuster;
	}

}
