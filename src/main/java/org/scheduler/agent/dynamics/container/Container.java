package org.scheduler.agent.dynamics.container;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

/**
 * Container model
 * originates from T.I. Fossen's MSS Toolsuite container.m
 * 
 * double delta_max  = 10;             //% max rudder angle (deg)
 * double Ddelta_max = 5;              //% max rudder rate (deg/s)
 * double n_max      = 160;            //% max shaft velocity (rpm)
 * 
 * @author chris
 *
 */
public class Container {
	
		
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		ContainerState state = new ContainerState();
		state.x=1500;
		state.y=1500;
//		state.u = 0.0001;

		ContainerInput ui = new ContainerInput(Math.toRadians(10), 160);
		Container container = new Container(state, ui);
		
		ArrayList<Shape> list = new ArrayList<>();
		
		System.out.println(state);
		double pixelFactor = 5;
		for(int i = 0; i< 100; i++) {
			System.out.println(container.getContainerState().getX() + ", "+ container.getContainerState().getY());
			Ellipse2D point = new Ellipse2D.Double(container.getContainerState().getX()/pixelFactor,container.getContainerState().getY()/pixelFactor, 1, 1);
			list.add(point);
			container.step();
		}
		ui.setDelta_c(Math.toRadians(-10));
		ui.setN_c(100);
		for(int i = 0; i< 3800; i++) {
			System.out.println(container.getContainerState().getX() + ", "+ container.getContainerState().getY());
			Ellipse2D point = new Ellipse2D.Double(container.getContainerState().getX()/pixelFactor,container.getContainerState().getY()/pixelFactor, 1, 1);
			list.add(point);
			container.step();
		}
		System.out.println(container.getContainerState());
		
//		Draw2DObjects app = new Draw2DObjects(list);
	}

/*
	function [xdot,U] = container(x,ui)
	% [xdot,U] = container(x,ui) returns the speed U in m/s (optionally) and the 
	% time derivative of the state vector: x = [ u v r x y psi p phi delta n ]'  for
	% a container ship L = 175 m, where
	%
	% u     = surge velocity          (m/s)
	% v     = sway velocity           (m/s)
	% r     = yaw velocity            (rad/s)
	% x     = position in x-direction (m)
	% y     = position in y-direction (m)
	% psi   = yaw angle               (rad)
	% p     = roll velocity           (rad/s)
	% phi   = roll angle              (rad)
	% delta = actual rudder angle     (rad)
	% n     = actual shaft velocity   (rpm)
	%
	% The input vector is :
	%
	% ui      = [ delta_c n_c ]'  where
	%
	% delta_c = commanded rudder angle   (rad)
	% n_c     = commanded shaft velocity (rpm)  
	%
	% Reference:  Son og Nomoto (1982). On the Coupled Motion of Steering and 
	%             Rolling of a High Speed Container Ship, Naval Architect of Ocean Engineering,
	%             20: 73-83. From J.S.N.A. , Japan, Vol. 150, 1981.
	% 
	% Author:    Trygve Lauvdal
	% Date:      12th May 1994
	% Revisions: 18th July 2001 (Thor I. Fossen): added output U, changed order of x-vector
	%            20th July 2001 (Thor I. Fossen): changed my = 0.000238 to my = 0.007049	
*/
	
	
   /** Ctor
    * 
    * @param state
    * @param ui
    */
   public Container(ContainerState state, ContainerInput ui) {
	   this.state = state;
	   this.ui = ui;
   }
   
   

   private ContainerState state;
   private ContainerInput ui;
	
   
   public void step() {
	   //if U <= 0,error('The ship must have speed greater than zero');end
	   //if x(10) <= 0,error('The propeller rpm must be greater than zero');end
	   if(state.u == 0)
		   state.u = 0.00000000000000000001;
	   
	   //% Normalization variables
	   double L = 175;                     //% length of ship (m)
	   double U = Math.sqrt(Math.pow(state.u, 2) + Math.pow(state.v, 2));   //% service speed (m/s)
	   
	   
	   
	   double delta_max  = 10;             //% max rudder angle (deg)
	   double Ddelta_max = 5;              //% max rudder rate (deg/s)
	   double n_max      = 160;            //% max shaft velocity (rpm)
	   
	   //% Non-dimensional states and inputs
	   double delta_c = ui.delta_c; 
	   double n_c     = ui.n_c/60*L/U;  
	   
	   if(Double.isInfinite(n_c)) {
		   n_c = 0;
	   }

	   //x = [ u v r x y psi p phi delta n ]
	   //	   1 2 3 4 5 6   7 8   9     10
	   
	   double u     = state.u/U;   double v   = state.v/U;  
	   double p     = state.p*L/U; double r   = state.r*L/U; 
	   double phi   = state.phi;     double psi = state.psi; 
	   double delta = state.delta;     double n   = state.n/60*L/U;
	   
	   if(U == 0) {
		   u = 0; v = 0;
		   p = 0; r = 0;
		   n = 0;
	   }
	   
	   //% Parameters, hydrodynamic derivatives and main dimensions
	   double m  = 0.00792;    double mx     = 0.000238;   double my = 0.007049;
	   double Ix = 0.0000176;  double alphay = 0.05;       double lx = 0.0313;
	   double ly = 0.0313;     /*double Ix     = 0.0000176;*/ double Iz = 0.000456;
	   double Jx = 0.0000034;  double Jz     = 0.000419;   /*double xG = 0;*/

	   /*double B     = 25.40;   double dF = 8.00; */    double g     = 9.81;
	   /*double dA    = 9.00;    double d  = 8.50; */   double nabla = 21222; 
	   /*double KM    = 10.39;   double KB = 4.6154; */  double AR    = 33.0376;
	   double Delta = 1.8219;  double D  = 6.533;   double GM    = 0.3/L;
	   double rho   = 1025;    double t  = 0.175;   double T     = 0.0005; 
	    
	   double W     = rho*g*nabla/(rho*Math.pow(L,2)*Math.pow(U,2)/2);
	   if(Double.isInfinite(W))
		   W = 0;

	   double Xuu      = -0.0004226;  double Xvr    = -0.00311;    double Xrr      = 0.00020; 
	   double Xphiphi  = -0.00020;    double Xvv    = -0.00386;

	   double Kv       =  0.0003026;  double Kr     = -0.000063;   double Kp       = -0.0000075; 
	   double Kphi     = -0.000021;   double Kvvv   =  0.002843;   double Krrr     = -0.0000462; 
	   double Kvvr     = -0.000588;   double Kvrr   =  0.0010565;  double Kvvphi   = -0.0012012; 
	   double Kvphiphi = -0.0000793;  double Krrphi = -0.000243;   double Krphiphi =  0.00003569;

	   double Yv       = -0.0116;     double Yr     =  0.00242;    double Yp       =  0; 
	   double Yphi     = -0.000063;   double Yvvv   = -0.109;      double Yrrr     =  0.00177; 
	   double Yvvr     =  0.0214;     double Yvrr   = -0.0405;     double Yvvphi   =  0.04605;
	   double Yvphiphi =  0.00304;    double Yrrphi =  0.009325;   double Yrphiphi = -0.001368;

	   double Nv       = -0.0038545;  double Nr     = -0.00222;    double Np       =  0.000213; 
	   double Nphi     = -0.0001424;  double Nvvv   =  0.001492;   double Nrrr     = -0.00229; 
	   double Nvvr     = -0.0424;     double Nvrr   =  0.00156;    double Nvvphi   = -0.019058; 
	   double Nvphiphi = -0.0053766;  double Nrrphi = -0.0038592;  double Nrphiphi =  0.0024195;

	   double kk     =  0.631;  double epsilon =  0.921;  double xR    = -0.5;
	   double wp     =  0.184;  double tau     =  1.09;   double xp    = -0.526; 
	   double cpv    =  0.0;    double cpr     =  0.0;    double ga    =  0.088; 
	   double cRr    = -0.156;  double cRrrr   = -0.275;  double cRrrv =  1.96; 
	   double cRX    =  0.71;   double aH      =  0.237;  double zR    =  0.033;
	   double xH     = -0.48;  

	   //% Masses and moments of inertia
	   double m11 = (m+mx);
	   double m22 = (m+my);
	   double m32 = -my*ly;
	   double m42 = my*alphay;
	   double m33 = (Ix+Jx);
	   double m44 = (Iz+Jz);

	   //% Rudder saturation and dynamics
	   if (Math.abs(delta_c) >= delta_max*Math.PI/180)
	      delta_c = Math.signum(delta_c)*delta_max*Math.PI/180;


	   double delta_dot = delta_c - delta;
	   if (Math.abs(delta_dot) >= Ddelta_max*Math.PI/180)
	      delta_dot = Math.signum(delta_dot)*Ddelta_max*Math.PI/180;

	   //% Shaft velocity saturation and dynamics
	   n_c = n_c*U/L;
	   n   = n*U/L;
	   if (Math.abs(n_c) >= n_max/60)
	      n_c = Math.signum(n_c)*n_max/60;
	   
	   double Tm = 0;
	   if (n > 0.3)
		   Tm=5.65/n;
	   else
		   Tm=18.83;      
	   double n_dot = 1/Tm*(n_c-n)*60;

	   //% Calculation of state derivatives
	   double vR     = ga*v + cRr*r + cRrrr*Math.pow(r,3) + cRrrv*Math.pow(r,2)*v;
	   double uP     = Math.cos(v)*((1 - wp) + tau*(Math.pow((v + xp*r),2) + cpv*v + cpr*r));
	   double  J     = uP*U/(n*D);
	  
	   double KT     = 0.527 - 0.455*J; 
	   double uR     = uP*epsilon*Math.sqrt(1 + 8*kk*KT/(Math.PI*Math.pow(J,2)));
	   double alphaR = delta + Math.atan(vR/uR);
	   double FN     = - ((6.13*Delta)/(Delta + 2.25))*(AR/Math.pow(L,2))*(Math.pow(uR,2) + Math.pow(vR,2))*Math.sin(alphaR);
	   T      = 2*rho*Math.pow(D,4)/(Math.pow(U,2)*Math.pow(L,2)*rho)*KT*n*Math.abs(n);
	   
	   if(Double.isNaN(J)) {
		   J = 0;
		   uR = 0;
		   KT = 0;
		   alphaR = 0;
		   FN = 0;
		   T = 0;
	   }

	   //% Forces and moments
	   double X    = Xuu*Math.pow(u,2) + (1-t)*T + Xvr*v*r + Xvv*Math.pow(v,2) + Xrr*Math.pow(r,2) + Xphiphi*Math.pow(phi,2) +
	            cRX*FN*Math.sin(delta) + (m + my)*v*r;
	   if(Double.isNaN(X))
			X = 0;
	     
	   double Y    = Yv*v + Yr*r + Yp*p + Yphi*phi + Yvvv*Math.pow(v,3) + Yrrr*Math.pow(r,3) + Yvvr*Math.pow(v,2)*r + 
	            Yvrr*v*Math.pow(r,2) + Yvvphi*Math.pow(v,2)*phi + Yvphiphi*v*Math.pow(phi,2) + Yrrphi*Math.pow(r,2)*phi + 
	            Yrphiphi*r*Math.pow(phi,2) + (1 + aH)*FN*Math.cos(delta) - (m + mx)*u*r;
	   if(Double.isNaN(Y))
			Y = 0;
	   
	   double K    = Kv*v + Kr*r + Kp*p + Kphi*phi + Kvvv*Math.pow(v,3) + Krrr*Math.pow(r,3) + Kvvr*Math.pow(v,2)*r + 
	            Kvrr*v*Math.pow(r,2) + Kvvphi*Math.pow(v,2)*phi + Kvphiphi*v*Math.pow(phi,2) + Krrphi*Math.pow(r,2)*phi + 
	            Krphiphi*r*Math.pow(phi,2) - (1 + aH)*zR*FN*Math.cos(delta) + mx*lx*u*r - W*GM*phi;
	   if(Double.isNaN(K))
			K = 0;
	   
	   double N    = Nv*v + Nr*r + Np*p + Nphi*phi + Nvvv*Math.pow(v,3) + Nrrr*Math.pow(r,3) + Nvvr*Math.pow(v,2)*r + 
	            Nvrr*v*Math.pow(r,2) + Nvvphi*Math.pow(v,2)*phi + Nvphiphi*v*Math.pow(phi,2) + Nrrphi*Math.pow(r,2)*phi + 
	            Nrphiphi*r*Math.pow(phi,2) + (xR + aH*xH)*FN*Math.cos(delta);
	   if(Double.isNaN(N))
			N = 0;
	   
	   //% Dimensional state derivatives  xdot = [ u v r x y psi p phi delta n ]'
	   double detM = m22*m33*m44-Math.pow(m32,2)*m44-Math.pow(m42,2)*m33;
		   
	   
	   
	   ///----
		//% Dimensional state derivative
	   double[] result = new double[] {  X*(Math.pow(U,2)/L)/m11,
		          -((-m33*m44*Y+m32*m44*K+m42*m33*N)/detM)*(Math.pow(U,2)/L),
		           ((-m42*m33*Y+m32*m42*K+N*m22*m33-N*Math.pow(m32,2))/detM)*(Math.pow(U,2)/Math.pow(L,2)),
		                   (Math.cos(psi)*u-Math.sin(psi)*Math.cos(phi)*v)*U,
		                   (Math.sin(psi)*u+Math.cos(psi)*Math.cos(phi)*v)*U ,
		                   Math.cos(phi)*r*(U/L) ,               
		           ((-m32*m44*Y+K*m22*m44-K*Math.pow(m42,2)+m32*m42*N)/detM)*(Math.pow(U,2)/Math.pow(L,2)),
		                                p*(U/L),
		                              delta_dot ,
		                                n_dot          };
		
		// integrate
		state.u += result[0];
		state.v += result[1];
		state.r += result[2];
		state.x += result[3];
		state.y += result[4];
		state.psi += result[5];
		state.p = result[6];
		state.phi = result[7];
		state.delta += result[8];
		state.n += result[9];
		
	}

   public ContainerState getContainerState() {
	   return state;
   }
}
