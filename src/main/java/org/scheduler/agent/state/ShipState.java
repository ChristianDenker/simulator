package org.scheduler.agent.state;

import java.awt.geom.Point2D;
import java.util.Random;

/** ShipState
 * 
 * The state of the ship represented as point2d, heading and speed_kn
 * 
 * @author chris
 *
 */
public class ShipState implements IState {

	private int MMSI = new Random().nextInt(900000000) + 100000;
	private Point2D point_current = null;
	private double heading_commanded_deg = 0.0d;
	private double speed_commanded_kn = 0.0d;
	
	/** Ctor
	 * 
	 * @param point_current
	 * @param heading_commanded_deg
	 * @param speed_commanded_kn
	 */
	public ShipState(Point2D point_current, double heading_commanded_deg, double speed_commanded_kn) {
		super();
		this.point_current = point_current;
		this.heading_commanded_deg = heading_commanded_deg;
		this.speed_commanded_kn = speed_commanded_kn;
	}
	
	
	/** Ctor
	 * 
	 * @param mmsi
	 * @param point_current
	 * @param heading_commanded_deg
	 * @param speed_commanded_kn
	 */
	public ShipState(int mmsi, Point2D point_current, double heading_commanded_deg, double speed_commanded_kn) {
		super();
		MMSI = mmsi;
		this.point_current = point_current;
		this.heading_commanded_deg = heading_commanded_deg;
		this.speed_commanded_kn = speed_commanded_kn;
	}



	public int getMMSI() {
		return MMSI;
	}

	public void setMMSI(int mMSI) {
		MMSI = mMSI;
	}

	public Point2D getPoint() {
		return point_current;
	}

	public void setPoint(Point2D startPosition) {
		this.point_current = startPosition;
	}

	public double getHeading_deg() {
		return heading_commanded_deg;
	}

	public void setHeading_deg(double heading_deg) {
		this.heading_commanded_deg = heading_deg;
	}

	public double getSpeed_kn() {
		return speed_commanded_kn;
	}

	public void setSpeed_kn(double speed_kn) {
		this.speed_commanded_kn = speed_kn;
	}

}
