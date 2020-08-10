package org.eclipse.petrinets;

public class InputArc 
{
	Place p;
	Transition t;
	
	public InputArc(Place p, Transition t) 
	{
		this.p = p;
		this.t = t;
		t.addPrePlace(p);
		p.addPostTransition(t);
	}
}
