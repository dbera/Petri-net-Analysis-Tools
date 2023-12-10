package org.eclipse.petrinets;

public class InhibitorArc {
	Place p;
	Transition t;
	
	public InhibitorArc(Place p, Transition t) 
	{
		this.p = p;
		this.t = t;
		
		t.addInhibitorPlace(p);
		p.addInhibitorTransition(t);
	}
}
