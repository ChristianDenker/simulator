package org.scheduler.agent.dynamics.tanker;


public class TankerState {
	
	/*
	 *  x = [ u v r x y psi delta n ]
	 */
	/**
			% u     = surge velocity, must be positive  (m/s)         - design speed u = 8.23 m/s
			% v     = sway velocity                     (m/s)
			% r     = yaw velocity                      (rad/s)
			% x     = position in x-direction           (m)
			% y     = position in y-direction           (m)
			% psi   = yaw angle                         (rad)
			% delta = actual rudder angle               (rad)
			% n     = actual shaft velocity             (rpm)          - nominal propeller 80 rpm
	*/
	public TankerState(double u, double v, double r, double x, double y, double psi, double delta, double n) {
		super();
		this.u = u;
		this.v = v;
		this.r = r;
		this.x = x;
		this.y = y;
		this.psi = psi;
		this.delta = delta;
		this.n = n/60;
	}
	
	/**
	 * Ctor
	 */
	public TankerState() {
		
	}
	
	double u = 0;
	double v = 0;
	double r = 0;
	double x = 0;
	double y = 0;
	double psi = 0;
	double delta = 0;
	double n = 0;
	
	
	public double getU() {
		return u;
	}
	public void setU(double u) {
		this.u = u;
	}
	public double getV() {
		return v;
	}
	public void setV(double v) {
		this.v = v;
	}
	public double getR() {
		return r;
	}
	public void setR(double r) {
		this.r = r;
	}
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}
	public double getPsi() {
		return psi;
	}
	public void setPsi(double psi) {
		this.psi = psi;
	}
	public double getDelta() {
		return delta;
	}
	public void setDelta(double delta) {
		this.delta = delta;
	}
	public double getN() {
		return n;
	}
	public void setN(double n) {
		this.n = n;
	}

	/**
	 * calculates speed
	 * U     = sqrt(x(1)^2 + x(2)^2);
	 * @return
	 */
	public double calcSpeed() {
		return Math.sqrt((Math.pow(getU(), 2))+ Math.pow(getV(), 2));
	}

	@Override
	public String toString() {
		return "TankerState [u=" + u + ", v=" + v + ", r=" + r + ", x=" + x + ", y=" + y + ", psi=" + psi + ", delta="
				+ delta + ", n=" + n + "]";
	}

	
}
