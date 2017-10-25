package com.example.utils;

public class CustomerInformation {

	private volatile static CustomerInformation insatance;
	// 状态
	private boolean State;

	private CustomerInformation() {

	}

	public static CustomerInformation getCarInfoInstance() {
		if (insatance == null) {
			synchronized (CustomerInformation.class) {
				if (insatance == null) {
					insatance = new CustomerInformation();
				}
			}
		}
		return insatance;
	}

	public boolean getState() {
		return State;
	}

	public void setState(boolean state) {
		State = state;
	}


}
