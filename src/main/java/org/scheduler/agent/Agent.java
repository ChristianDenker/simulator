package org.scheduler.agent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.scheduler.Scheduler;
import org.scheduler.Tick;
import org.scheduler.TickAnswer;
import org.scheduler.agent.behaviour.ITickExecution;
import org.scheduler.agent.behaviour.NmeaPosReportUdpOutputBehaviour;

/** Agent
 * 
 * @author chris
 *
 */
public class Agent {

	private Scheduler scheduler = null;
	private List<ITickExecution> executionList = Collections.synchronizedList(new ArrayList<ITickExecution>());
	
	/** Ctor
	 * 
	 */
	public Agent() {
		EventBus.getDefault().register(this);
	}
	
	/** Ctor
	 * 
	 * @param tickExecutions - the {@link ITickExecution} to add to the "executionList"
	 */
	public Agent(Scheduler scheduler, ITickExecution... tickExecutions) {
		this.scheduler = scheduler;
		EventBus.getDefault().register(this);
		Arrays.asList(tickExecutions).forEach(tickExecution -> tickExecution.setAgent(this));
		this.executionList.addAll(Arrays.asList(tickExecutions));
	}


	/** receives the tick from eventBus and processes the {@link ITickExecution} in the "executionList"
	 * 
	 * @param tick
	 */
	@Subscribe()
	public void receiveTick(Tick tick) {
		this.executionList.forEach(execution -> execution.execute(tick));
		answerTick(tick);
	}


	/**
	 * make sure, that this is called after compute is done
	 * @param tick
	 */
	private void answerTick(Tick tick) {
		TickAnswer tickAnswer = new TickAnswer(tick, this);
		EventBus.getDefault().post(tickAnswer);
	}

	/**
	 * returns the Scheduler this Agent was added to
	 * @return
	 */
	public Scheduler getScheduler() {
		return scheduler;
	}

	public void stop() {
		executionList.forEach( tickExecution -> {
			if (tickExecution instanceof NmeaPosReportUdpOutputBehaviour) {
				((NmeaPosReportUdpOutputBehaviour)tickExecution).stop();
			}
		});
	}

	
}

