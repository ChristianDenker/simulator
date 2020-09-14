package org.scheduler.agent.dynamics.tanker;


public class TankerInput {

	
	/**
	 * 		% ui      = [ delta_c  n_c h ]'  where
			%
			% delta_c = commanded rudder angle                 (rad)
			% n_c     = commanded shaft velocity               (rpm)
			% h       = water depth, must be larger than draft (m)      - draft is 18.46 m
	 * @param delta_c
	 * @param n_c
	 * @param h
	 */
	public TankerInput(double delta_c, double n_c, double h) {
		super();
		this.delta_c = delta_c;
		this.n_c = n_c /60;
		this.h = h;
	}

	double delta_c;
	double n_c;
	double h;
	
	public double getDelta_c() {
		return delta_c;
	}
	public void setDelta_c(double delta_c) {
		this.delta_c = delta_c;
	}
	public double getN_c() {
		return n_c;
	}
	public void setN_c(double n_c) {
		this.n_c = n_c;
	}
	public double getH() {
		return h;
	}
	public void setH(double h) {
		this.h = h;
	}
	
}
