package org.eclipse.petrinets;

public class ResetArc {
	Place p;
	Transition t;
	
	public ResetArc(Place p, Transition t) 
	{
		this.p = p;
		this.t = t;
		
		t.addResetPlace(p);
		p.addResetTransition(t);
	}
}
