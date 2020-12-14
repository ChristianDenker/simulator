package org.scheduler;

import java.awt.geom.Point2D;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.scheduler.agent.Agent;
import org.scheduler.agent.behaviour.LinearDrivingBehaviour;
import org.scheduler.agent.behaviour.NmeaPosReportUdpOutputBehaviour;
import org.scheduler.agent.state.ShipState;

/** Scheduler
 * 
 * @author chris
 *
 */
public class Scheduler implements Runnable {
	
	public static void main(String[] args) {
		System.out.println("## Ship Simulation ##");

		double freq = 1;
		double simulationSpeed = 1;
		Scheduler scheduler = new Scheduler(new SchedulerConfiguration(freq, simulationSpeed));

//		Point startPoint = new GeometryBuilder(DefaultGeographicCRS.WGS84).createPoint(8, 54);
		ShipState shipStateA = new ShipState(111111111, new Point2D.Double(8,54), 90, 10);
		scheduler.registerAgent(new Agent(scheduler, new LinearDrivingBehaviour(shipStateA), new NmeaPosReportUdpOutputBehaviour(shipStateA, 2947)));

		ShipState shipStateB = new ShipState(222222222, new Point2D.Double(8.16,53.9), 0, 10);
		scheduler.registerAgent(new Agent(scheduler, new LinearDrivingBehaviour(shipStateB), new NmeaPosReportUdpOutputBehaviour(shipStateB, 2947)));
		
		scheduler.start();
		
		try {
			TimeUnit.SECONDS.sleep(15);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		scheduler.stop();
	}

	//private EventBus eventBus = EventBus.getDefault();
	private ExecutorService executorService = null;
	private EventBus eventBus = null;

	private List<Agent> registeredAgents = Collections.synchronizedList(new ArrayList<Agent>());
	private SchedulerConfiguration schedulerConfiguration = null;
	private int timeToAdd_ns;

	private volatile CountDownLatch latch = null;
	private AtomicBoolean RUNNING = new AtomicBoolean(true);
	
	private Thread worker = null;
	
	private LocalDateTime localDateTime_start = LocalDateTime.of(2020, Month.JANUARY, 1, 8, 0, 0);
	private LocalDateTime localDateTime_current = LocalDateTime.of(2020, Month.JANUARY, 1, 8, 0, 0);

	/**
	 * start the scheduler
	 */
	public void start() {
		RUNNING.set(true);
		worker = new Thread(this);
		worker.start();
	}

	

	@Override
	public void run() {
		
		
		System.out.println("Start: \t" + localDateTime_start);
		/*
		 * loop
		 */
		while (RUNNING.get()) {
			
			Instant start = Instant.now();
			latch = new CountDownLatch(registeredAgents.size());
			Tick tick = new Tick(timeToAdd_ns, ChronoUnit.NANOS);
			this.eventBus.post(tick);
			try {
				latch.await(schedulerConfiguration.timeout_s, TimeUnit.SECONDS);
				localDateTime_current = localDateTime_current.with(tick.getTemporalAdjuster());
				
				long time_left_ns = (Instant.now().toEpochMilli() - start.toEpochMilli()) * 1_000_000;
				if(schedulerConfiguration.getSimulationSpeed() < 1000 && (timeToAdd_ns - time_left_ns) > 0) {
					TimeUnit.NANOSECONDS.sleep( (long)((timeToAdd_ns - time_left_ns) / schedulerConfiguration.getSimulationSpeed()) );
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("End: \t" + localDateTime_current);
		
	}
	
	/**
	 * stop the scheduler
	 */
	public void stop() {
		RUNNING.set(false);
		registeredAgents.forEach(agent -> agent.stop());
		executorService.shutdown();
	}

	@Subscribe
	public void receiveTickAnswer(TickAnswer tickAnswer) {
		if(registeredAgents.contains(tickAnswer.agent)) {
			latch.countDown();		
		} else {
			System.err.println("Received TickAnswer from unregistred Agent " + tickAnswer.agent);
		}
	}

	/** register an {@link Agent} with this Scheduler
	 * 
	 * @param agent
	 */
	public void registerAgent(Agent agent) {
		registeredAgents.add(agent);
	}

	/**
	 * Ctor
	 * 
	 * @param config
	 */
	public Scheduler(SchedulerConfiguration config) {
		this.schedulerConfiguration = config;
		this.timeToAdd_ns = (int)(1_000_000_000 / this.schedulerConfiguration.freqency_Hz);
		
		this.executorService = Executors.newCachedThreadPool();
		this.eventBus = EventBus.builder().executorService(executorService).installDefaultEventBus();
		
		
		this.eventBus.register(this);
	}


	/**
	 * returns this Scheduler's {@link SchedulerConfiguration}
	 * @return
	 */
	public SchedulerConfiguration SchedulerConfiguration() {
		return schedulerConfiguration;
	}
	
	


}

