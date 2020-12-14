package simulator;


import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.ml.distance.EuclideanDistance;

/** Track
 *  
 * This Track Class is composed of two 2D Vectors: 
 * <li>position vector (expressed the cartesian coordinate system in meters) </li>
 * <li>velocity vector (vector of [speed_ms, 0] rotated by [course_deg]; unit is meters per second) </li>
 * 
 * Furthermore this class contains a method to calculate tCPA between two Tracks
 *
 */
public class Track {

	/** position vector **/
	Vector2D position = null;
	/** velocity vector **/
	Vector2D velocity = null;

	/**
	 * Track
	 * 
	 * @param x_cartesian - x in the cartesian coordinate system in meters
	 * @param y_cartesian - y in the cartesian coordinate system in meters
	 * @param speed_ms    - speed in meters per second
	 * @param course_deg  - course in degrees
	 */
	public Track(double x_cartesian, double y_cartesian, double speed_ms, double course_deg) {

		/**
		 * Rotate speed vector. For details see:
		 * https://matthew-brett.github.io/teaching/rotation_2d.html
		 **/
		double advance = (Math.cos(Math.toRadians(course_deg)) * speed_ms) - (Math.sin(Math.toRadians(course_deg)) * 0);
		double transfer = (Math.sin(Math.toRadians(course_deg)) * speed_ms)
				+ (Math.cos(Math.toRadians(course_deg)) * 0);
		velocity = new Vector2D(advance, transfer);

		position = new Vector2D(x_cartesian, y_cartesian);

	}

	/**
	 * returns the position vector in the cartesian coordinate system; unit is
	 * meters
	 * 
	 * @return
	 */
	public Vector2D getPosition() {
		return position;
	}

	/**
	 * returns the vector of [speed_ms, 0] rotated by [course_deg]
	 * 
	 * @return - the vector; unit is meters per second
	 */
	public Vector2D getVector() {
		return velocity;
	}

	@Override
	public String toString() {
		return "Track [position=" + position + ", vector=" + velocity + "]";
	}

	
	/**
	 * calculates the CPA Time for two tracks
	 * 
	 * @param track1 - the track No.1
	 * @param track2 - the track No.2
	 * @return - the CPA Time in seconds
	 */
	public static double cpaTime(Track track1, Track track2) {
		/** calculate velocity difference **/
		Vector2D dv = track1.getVector().subtract(track2.getVector());

		/** squared velocity difference **/
		double dv2 = dv.dotProduct(dv);

		/** if the tracks are almost parallel and in opposite direction, then cpa time is 0.0 **/
		double SMALL_NUM = 0.00000001;
		if (dv2 < SMALL_NUM) {
			return 0.0;
		}

		/** calculate position difference **/
		Vector2D w0 = track1.getPosition().subtract(track2.getPosition());
		
		/** multiply position difference by dv/dv2 **/
		double cpatime = -w0.dotProduct(dv) / dv2;
		/** time of CPA **/
		return cpatime;
	}
	

	/**
	 * 
	 * @param track1
	 * @param track2
	 * @return
	 */
	public static double cpaDistance(Track track1, Track track2) {
		double cpaTime = cpaTime(track1, track2);
		Vector2D position1 = track1.position;
		Vector2D velocity1 = track1.velocity;
		Vector2D newPos1 = position1.add(velocity1.scalarMultiply(cpaTime));
		
		Vector2D position2 = track2.position;
		Vector2D velocity2 = track2.velocity;
		Vector2D newPos2 = position2.add(velocity2.scalarMultiply(cpaTime));
		
		EuclideanDistance edist = new EuclideanDistance();
		return edist.compute(newPos1.toArray(), newPos2.toArray());
	}
}