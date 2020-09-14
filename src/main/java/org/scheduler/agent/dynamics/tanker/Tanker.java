package org.scheduler.agent.dynamics.tanker;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

import org.scheduler.util.Draw2DObjects;

/**
 * Tanker model
 * originates from T.I. Fossen's MSS Toolsuite tanker.m
 * 
 * tanker is not stable
 * 
 * @author chris
 *
 */
public class Tanker {
	
		
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		TankerState state = new TankerState();
		state.x=500;
		state.y=500;
		//state.psi=0;
		//state.n = 1/60;
		TankerInput ui = new TankerInput(Math.toRadians(10), 60, 100);
		Tanker tanker = new Tanker(state, ui);
		
		ArrayList<Shape> list = new ArrayList<>();
		
		System.out.println(state);
		for(int i = 0; i< 1500; i++) {
			System.out.println(tanker.getTankerState().getX() + ", "+ tanker.getTankerState().getY());
			Ellipse2D point = new Ellipse2D.Double(tanker.getTankerState().getX(),tanker.getTankerState().getY(), 1, 1);
			list.add(point);
			tanker.step();
		}
		ui.setDelta_c(0);
		for(int i = 0; i< 1800; i++) {
			System.out.println(tanker.getTankerState().getX() + ", "+ tanker.getTankerState().getY());
			Ellipse2D point = new Ellipse2D.Double(tanker.getTankerState().getX(),tanker.getTankerState().getY(), 1, 1);
			list.add(point);
			tanker.step();
		}
		System.out.println(tanker.getTankerState());
		
		Draw2DObjects app = new Draw2DObjects(list);
	}
/*
		% function [xdot,U] = tanker(x,ui)
		% [xdot,U] = tanker(x,ui) returns the speed U in m/s (optionally) and the 
		% time derivative of the state vector: x = [ u v r x y psi delta n ]'  for
		% a large tanker L = 304.8 m where:
		%
		% u     = surge velocity, must be positive  (m/s)         - design speed u = 8.23 m/s
		% v     = sway velocity                     (m/s)
		% r     = yaw velocity                      (rad/s)
		% x     = position in x-direction           (m)
		% y     = position in y-direction           (m)
		% psi   = yaw angle                         (rad)
		% delta = actual rudder angle               (rad)
		% n     = actual shaft velocity             (rpm)          - nominal propeller 80 rpm
		% 
		% The input vector is :
		%
		% ui      = [ delta_c  n_c h ]'  where
		%
		% delta_c = commanded rudder angle                 (rad)
		% n_c     = commanded shaft velocity               (rpm)
		% h       = water depth, must be larger than draft (m)      - draft is 18.46 m			
*/
	
	double L   =  304.8;         // % length of ship (m)
	double g   =  9.8;           // % acceleration of gravity (m/s^2)
	
	TankerInput ui = null;
	TankerState state = null;
	
	double delta_max  = 10;      // % max rudder angle      (deg)
	double Ddelta_max = 2.33;    // % max rudder derivative (deg/s)
	double n_max      = 80;      // % max shaft velocity    (rpm)
	
	double t   =  0.22;
	double Tm  =  50;
	double T   =  18.46;

	double cun =  0.605;  
	double cnn =  38.2;

	double Tuu = -0.00695;
	double Tun = -0.00063;
	double Tnn =  0.0000354;

	double m11 =  1.050;  //        % 1 - Xudot
	double m22 =  2.020;  //        % 1 - Yvdot
	double m33 =  0.1232; //        % kz^2 - Nrdot

	double d11 =  2.020;  //        % 1 + Xvr
	double d22 = -0.752;  //        % Yur - 1
	double d33 = -0.231;  //        % Nur - xG 

	double Xuuz   = -0.0061;   double YT     =  0.04;   double NT      = -0.02;
	double Xuu    = -0.0377;   double Yvv    = -2.400;  double Nvr     = -0.300;
	double Xvv    =  0.3;      double Yuv    = -1.205;  double Nuv     = -0.451;   
	double Xudotz = -0.05;     double Yvdotz = -0.387;  double Nrdotz  = -0.0045;
							   double Yurz   =  0.182;  double Nurz    = -0.047;
	double Xvrz   =  0.387;    double Yvvz   = -1.5;    double Nvrz    = -0.120;
	double Xccdd  = -0.093;    double Yuvz   =  0;      double Nuvz    = -0.241;
	double Xccbd  =  0.152;    double Yccd   =  0.208;  double Nccd    = -0.098;
	double Xvvzz  =  0.0125;   double Yccbbd = -2.16;   double Nccbbd  =  0.688;
							   double Yccbbdz= -0.191;  double Nccbbdz =  0.344;
	
   /** Ctor
    * 
    * @param state
    * @param ui
    */
   public Tanker(TankerState state, TankerInput ui) {
	   this.state = state;
	   this.ui = ui;
   }
   
   public void step() {
	   for(int i = 0; i<10; i++) {
		   step(0.2);
	   }
   }
   
   /**
    * 
    * @param sampleTime_sec - the sample time (sec)
    */
   public void step(double sampleTime_sec) {
		//% Additional terms in shallow water
		double z = T/(ui.getH() - T);
		if (ui.getH()<18.5)
			System.err.println("the depth must be larger than the draft (18.5 m)");
		if (z >= 0.8)
			Yuvz = -0.85*(1-0.8/z);
		//double delta_c = 0;
		//% Rudder saturation and dynamics
		if (Math.abs(ui.getDelta_c()) >= delta_max*Math.PI/180)
			ui.delta_c = Math.signum(ui.delta_c)*delta_max*Math.PI/180;
		double delta_dot = 0;
		delta_dot = ui.delta_c - state.delta;
		if (Math.abs(delta_dot) >= Ddelta_max*Math.PI/180)
		   delta_dot = Math.signum((delta_dot)*Ddelta_max*Math.PI/180);
		
		//% Shaft saturation and dynamics
		if (Math.abs(ui.n_c) >= n_max/60)
		   ui.n_c = Math.signum(ui.n_c)*n_max/60;            

		double n_dot = 1/Tm*(ui.n_c-state.n)*60;
		
		//% Forces and moments
//		if (state.u<=0)
//			System.err.println("u must be larger than zero");
		
		double beta = state.v/state.u;
		if(Double.isNaN(beta))
			beta = 0;
		double gT   = (1/L*Tuu*Math.pow(state.u,2) + Tun*state.u*state.n + L*Tnn*Math.abs(state.n)*state.n);
		double c    = Math.sqrt(cun*state.u*state.n + cnn*Math.pow(state.n,2));

		double gX   = 1/L*(Xuu*Math.pow(state.u,2) + L*d11*state.v*state.r + Xvv*Math.pow(state.v,2) + Xccdd*Math.abs(c)*c*Math.pow(state.delta,2)
		     + Xccbd*Math.abs(c)*c*beta*state.delta + L*gT*(1-t) + Xuuz*Math.pow(state.u,2)*z 
		     + L*Xvrz*state.v*state.r*z + Xvvzz*Math.pow(state.v,2)*Math.pow(z,2));
		if(Double.isNaN(gX))
			gX = 0;
		
		double gY   = 1/L*(Yuv*state.u*state.v + Yvv*Math.abs(state.v)*state.v + Yccd*Math.abs(c)*c*state.delta + L*d22*state.u*state.r 
			 + Yccbbd*Math.abs(c)*c*Math.abs(beta)*beta*Math.abs(state.delta) + YT*gT*L 
			 + L*Yurz*state.u*state.r*z + Yuvz*state.u*state.v*z + Yvvz*Math.abs(state.v)*state.v*z 
		     + Yccbbdz*Math.abs(c)*c*Math.abs(beta)*beta*Math.abs(state.delta)*z);     
		if(Double.isNaN(gY))
			gY = 0;
		
		double gLN  = Nuv*state.u*state.v + L*Nvr*Math.abs(state.v)*state.r + Nccd*Math.abs(c)*c*state.delta +L*d33*state.u*state.r
		     + Nccbbd*Math.abs(c)*c*Math.abs(beta)*beta*Math.abs(state.delta) + L*NT*gT
		     + L*Nurz*state.u*state.r*z + Nuvz*state.u*state.v*z + L*Nvrz*Math.abs(state.v)*state.r*z 
		     + Nccbbdz*Math.abs(c)*c*Math.abs(beta)*beta*Math.abs(state.delta)*z;
		if(Double.isNaN(gLN))
			gLN = 0;
		
		m11 = (m11 - Xudotz*z);
		m22 = (m22 - Yvdotz*z);
		m33 = (m33 - Nrdotz*z);
		   
		//% Dimensional state derivative
		double[] result = new double[] {  gX/m11,
		    gY/m22,
		    gLN/(Math.pow(L,2)*m33),
		    Math.cos(state.psi)*state.u-Math.sin(state.psi)*state.v,
		    Math.sin(state.psi)*state.u+Math.cos(state.psi)*state.v,
		    state.r,
		    delta_dot,
		    n_dot         };
		

//		% u     = surge velocity, must be positive  (m/s)         - design speed u = 8.23 m/s
//		% v     = sway velocity                     (m/s)
//		% r     = yaw velocity                      (rad/s)
//		% x     = position in x-direction           (m)
//		% y     = position in y-direction           (m)
//		% psi   = yaw angle                         (rad)
//		% delta = actual rudder angle               (rad)
//		% n     = actual shaft velocity             (rpm)          - nominal propeller 80 rpm
		
		state.u += (result[0] * sampleTime_sec);
		state.v += (result[1] * sampleTime_sec);
		state.r += (result[2] * sampleTime_sec);
		state.x += (result[3] * sampleTime_sec);
		state.y += (result[4] * sampleTime_sec);
		state.psi += (result[5] * sampleTime_sec);
		state.delta += (result[6] * sampleTime_sec);
		state.n += (result[7] * sampleTime_sec);
		
	}

   public TankerState getTankerState() {
	   return state;
   }
}
