package org.scheduler;

import org.scheduler.agent.Agent;

public class TickAnswer {

	Tick tick = null;
	Agent agent = null;
	
	/** Ctor
	 * 
	 * @param tick
	 * @param agent
	 */
	public TickAnswer(Tick tick, Agent agent) {
		this.tick = tick;
		this.agent = agent;
	}

}
