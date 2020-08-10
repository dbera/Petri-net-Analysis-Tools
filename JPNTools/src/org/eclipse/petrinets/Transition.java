package org.eclipse.petrinets;

import java.util.ArrayList;
import java.util.Iterator;


public class Transition 
{
	public String name;
	public Transition_Type type;
	public String machineName;
	public ArrayList<Place> preset;
	public ArrayList<Place> postset;
	
	public Transition(String name, Transition_Type type, String netName) {
		this.name = name;
		this.type = type;
		this.machineName = netName;
		this.preset = new ArrayList<Place>();
		this.postset = new ArrayList<Place>();
	}
	
	public void addPrePlace(Place p) {
		preset.add(p);
	}
	
	public void addPostPlace(Place p) {
		postset.add(p);
	}

	// This is the guard. This has to be implemented by the subclass.
	public boolean isEnabled(String current_state) { return true; } //, NetVariables smVarContainer, InputParametersContainer c);
	
	// Computations on State Machine vars and Input Params
	public void executeActions(String current_state) {  //, NetVariables smVarContainer, InputParametersContainer c); 
		GraphLogger.getInstance().addToLog("Signal", name, "IVacuum2StateMachine");
	} 
}