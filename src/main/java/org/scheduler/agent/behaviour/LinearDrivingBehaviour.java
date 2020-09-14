package org.scheduler.agent.behaviour;

import java.awt.geom.Point2D;
import java.time.LocalTime;

import org.geotools.referencing.GeodeticCalculator;
import org.scheduler.Tick;
import org.scheduler.agent.Agent;
import org.scheduler.agent.state.ShipState;

/** LinearDrivingBehaviour
 * 
 * Sailes ship as set in given shipState
 * 
 * @author chris
 *
 */
public class LinearDrivingBehaviour implements ITickExecution {

	private ShipState shipState = null;
	
	private GeodeticCalculator geoCalc = new GeodeticCalculator();

	@SuppressWarnings("unused")
	private Agent agent = null;

	public LinearDrivingBehaviour(ShipState shipState) {
		this.shipState  = shipState;
	}

	@Override
	public void execute(Tick tick) {
		geoCalc.setStartingGeographicPoint(shipState.getPoint());
		LocalTime lt = LocalTime.of(0,0,0,0);
		lt = (LocalTime) tick.getTemporalAdjuster().adjustInto(lt);
		long milliOfDay = lt.toNanoOfDay() / 1_000_000;
		
		double distance_per_s =  (shipState.getSpeed_kn() * 1.852 / 3.6 );
		double distance_per_time = distance_per_s  * milliOfDay/1000;
		geoCalc.setDirection(shipState.getHeading_deg(), distance_per_time);
		
		//System.out.println("distance_per_s: " + distance_per_s +" > distance per time: " + distance_per_time);

		Point2D destination = geoCalc.getDestinationGeographicPoint();
		shipState.getPoint().setLocation(destination);
		//shipState.setPoint(destination);
		
		//System.out.println(destination);
	}

	@Override
	public void setAgent(Agent agent) {
		this.agent  = agent;
	}

}
