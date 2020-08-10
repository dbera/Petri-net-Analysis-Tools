package org.eclipse.petrinets;

@SuppressWarnings("serial")
public class SimulatorException extends Exception
{

	private String err_msg;
	
	public SimulatorException(String err_msg) {
		this.setErr_msg(err_msg);
	}

	public String getErr_msg() {
		return err_msg;
	}

	public void setErr_msg(String err_msg) {
		this.err_msg = err_msg;
	}
}
