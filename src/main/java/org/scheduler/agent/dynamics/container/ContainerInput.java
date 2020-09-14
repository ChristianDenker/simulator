package org.scheduler.agent.dynamics.container;


public class ContainerInput {

	
	/**
	  		% ui      = [ delta_c n_c ]'  where
			%
			% delta_c = commanded rudder angle   (rad)
			% n_c     = commanded shaft velocity (rpm)  
	 * @param delta_c
	 * @param n_c
	 */
	public ContainerInput(double delta_c, double n_c) {
		super();
		this.delta_c = delta_c;
		this.n_c = n_c;
	}

	double delta_c;
	double n_c;
	
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
	
}
