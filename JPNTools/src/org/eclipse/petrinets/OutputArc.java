package org.eclipse.petrinets;

public class OutputArc 
{
	Place p;
	Transition t;
	
	public OutputArc(Transition t, Place p) 
	{
		this.p = p;
		this.t = t;
		t.addPostPlace(p);
		p.addPreTransition(t);
	}
	
	// This function operates on State Machine Vars as well as input params of transition
	// public abstract void executeActions();
}
