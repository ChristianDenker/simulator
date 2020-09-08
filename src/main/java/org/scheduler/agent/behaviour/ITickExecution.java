package org.scheduler.agent.behaviour;

import org.scheduler.Tick;
import org.scheduler.agent.Agent;

public interface ITickExecution {
	
	public void execute(Tick tick);
	
	/**
	 * set the agent, which is owning this ITickExecution instance
	 * @param agent
	 */
	public void setAgent(Agent agent);

}
