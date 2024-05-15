package org.scheduler.agent.dynamics.nomoto;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.scheduler.Scheduler;
import org.scheduler.SchedulerConfiguration;
import org.scheduler.agent.Agent;
import org.scheduler.agent.behaviour.LinearDrivingBehaviour;
import org.scheduler.agent.behaviour.NmeaPosReportUdpOutputBehaviour;
import org.scheduler.agent.state.ShipState;
import org.scheduler.util.Draw2DObjects;

public class Nomoto {
	public static void main(String[] args) {
		
		GeometryFactory geoFactory = new GeometryFactory();
		Coordinate[] coordinates = new Coordinate[] {new Coordinate(-2,1),new Coordinate(2,1)};
		Geometry line = geoFactory.createLineString(coordinates);
		
		Geometry pointGeo = geoFactory.createPoint(new Coordinate(0, -1));
//		pointGeo = pointGeo.buffer(0);
		
		
		double out = line.distance(pointGeo);
		System.out.println("Distance: " + out);
		
		pointGeo.getCoordinate().setY(0);
		out = line.distance(pointGeo);
		System.out.println("Distance: " + out);
		
		ArrayList<Shape> shapeList = new ArrayList<>();
		
		//double[] xdot = new double[] {input[0], Kcurr,  x[0], x[1] , u, v, time, Kurs, steps_per_second};
        
		double[] xdot = new double[] {30, 0,  500, 500 , 4 , 0, 0, 0, 1};
		
//		double[] out = funcKT(xdot);
//		
//		double[] out2 = funcKT(out);
//		
//		double[] out3 = funcKT(out2);
		
		double[] out4 = funcKT(xdot);
		System.out.println(out4[2]+";"+out4[3]);
		for(int i = 0; i<100; i++) {
			out4 = funcKT(out4);
			System.out.println(out4[2]+";"+out4[3]);
			
			Ellipse2D point = new Ellipse2D.Double(out4[2],out4[3], 1, 1);
			shapeList.add(point);
			
//			if(i == 20) {
//				out4[0] = 0;
//			}
		}
		
		Ellipse2D point = new Ellipse2D.Double(500,500, 1, 1);
		shapeList.add(point);
		Draw2DObjects app = new Draw2DObjects(shapeList);
		
		System.out.println("## Ship Simulation ##");

		double freq = 1;
		double simulationSpeed = 1;
		Scheduler scheduler = new Scheduler(new SchedulerConfiguration(freq, simulationSpeed));

//		Point startPoint = new GeometryBuilder(DefaultGeographicCRS.WGS84).createPoint(8, 54);
		ShipState shipStateA = new ShipState(111111111, new Point2D.Double(8, 54), 90, 10);
		scheduler.registerAgent(new Agent(scheduler, new LinearDrivingBehaviour(shipStateA),
				new NmeaPosReportUdpOutputBehaviour(shipStateA, 2947)));

		scheduler.start();

		try {
			TimeUnit.SECONDS.sleep(15);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		scheduler.stop();
	}

	/**
	 *  
	 * @param input - xdot = [input(1), Kcurr,  x(1),x(2) , u, v, time, Kurs, steps_per_second];
	 * @return
	 */
	public static double[] funcKT(double[] input) {
		double steps_per_second = input[8];
		// Parameters estimated for Fossen's container model
		double K = 4.0 * Math.min(Math.abs(input[0]),45) / 45 ;
		double T = 8;
		double model_speed_kn = 13.6;
		double speed_kn = 13.6;
		double speed_ms = speed_kn * 0.51444444444;
		
		// adjust KT to speed
		K= K * speed_kn/model_speed_kn ;
		T= T * model_speed_kn/speed_kn ;
		
		double[] x = new double[] { input[2], input[3] }; //position vector
		
		double Kinc = K/T;
		double Kcurr = input[1]; //current yaw rate (deg), psi
		double Kurs = input[7];
		
		double time = Math.min(input[6] + (1.0/steps_per_second),T);
        double delta = ( (Kinc * time/T) * Math.signum(input[0]) )   ;
        double temp = Kcurr + delta;
        
        //lower limit is -K
        if (Kcurr < 0 && Math.abs(Kcurr) > K)
            if (Math.signum(delta) < 0)
                temp = Kcurr;
        
        //upper limit is +K
        if (Kcurr > 0 && Math.abs(Kcurr) > K)
            if(Math.signum(delta) > 0)
                temp = Kcurr;
		
        Kcurr = temp;
        
        double abweichung = 0.00/steps_per_second; //0.02 //(deg)
        Kurs = Kurs + Kcurr + abweichung;
        
        double U = speed_ms / steps_per_second ; // m/s
        double u = input[4]/U; // u = surge velocity (m/s)
		double v = input[5]/U; // v = sway velocity (m/s)
        double psi = Math.toRadians(Kurs)+(Kcurr + abweichung) * Math.PI/180; // Kurs / yaw angle (rad)
        double phi = 0; // kein roll
        x[0] = input[2]+(Math.cos(psi)*u-Math.sin(psi)*Math.cos(phi)*v)*U;  
        x[1] = input[3]+(Math.sin(psi)*u+Math.cos(psi)*Math.cos(phi)*v)*U;
            
        double[] xdot = new double[] {input[0], Kcurr,  x[0], x[1] , u*U, v*U, time, Kurs, steps_per_second};
        
		return xdot;
	}

}
