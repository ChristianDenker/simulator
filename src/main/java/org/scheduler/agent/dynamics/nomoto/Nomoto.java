package org.scheduler.agent.dynamics.nomoto;

import java.awt.geom.Point2D;
import java.util.concurrent.TimeUnit;

import org.scheduler.Scheduler;
import org.scheduler.SchedulerConfiguration;
import org.scheduler.agent.Agent;
import org.scheduler.agent.behaviour.LinearDrivingBehaviour;
import org.scheduler.agent.behaviour.NmeaPosReportUdpOutputBehaviour;
import org.scheduler.agent.state.ShipState;

public class Nomoto {
	public static void main(String[] args) {
		
		//double[] xdot = new double[] {input[0], Kcurr,  x[0], x[1] , u, v, time, Kurs, steps_per_second};
        
		double[] xdot = new double[] {30, 0,  0, 0 , 5.14 , 0, 0, 0, 1};
		
		double[] out = funcKT(xdot);
		
		double[] out2 = funcKT(out);
		
		double[] out3 = funcKT(out2);
		
		double[] out4 = funcKT(out3);
		for(int i = 0; i<100; i++) {
			out4 = funcKT(out4);
			System.out.println(out4[2]+";"+out4[3]);
		}
		
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
		double K = 0.486 * Math.min(Math.abs(input[0]),90) / 90 ;
		double T = 16 ;
		double model_speed_kn = 13.6;
		double speed_kn = 13.6;
		double speed_ms = speed_kn * 0.51444444444;
		
		// adjust KT to speed
		K= K * speed_kn/model_speed_kn;
		T= T * model_speed_kn/speed_kn;
		
		double[] x = new double[] { input[2], input[3] }; //position vector
		
		double Kinc = K/T ;
		double Kcurr = input[1]; //current yaw rate (deg), psi
		double Kurs = input[7];
		
		double time = Math.min(input[6] + (1.0/steps_per_second),T);
        double delta = ( (Kinc * time/T) * Math.signum(input[0]) )  ;
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
        
        double abweichung = 0.02/steps_per_second; //(deg)
        Kurs = Kurs + Kcurr + abweichung;
        
        double U = speed_ms / steps_per_second; // m/s
        double u = input[3]/U; // u = surge velocity (m/s)
		double v = input[4]/U; // v = sway velocity (m/s)
        double psi = (Kcurr + abweichung) * Math.PI/180; // Kurs / yaw angle (rad)
        double phi = 0; // kein roll
        x[0] = (Math.cos(psi)*u-Math.sin(psi)*Math.cos(phi)*v)*U ;  
        x[1] = (Math.sin(psi)*u+Math.cos(psi)*Math.cos(phi)*v)*U ;
            
        double[] xdot = new double[] {input[0], Kcurr,  x[0], x[1] , u, v, time, Kurs, steps_per_second};
        
		return xdot;
	}

}
