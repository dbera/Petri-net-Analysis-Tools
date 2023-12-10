package org.eclipse.petrinets;

import java.util.ArrayList;
import java.util.Iterator;


public class Transition 
{
	public String name;
	String identifier; // added 12.08.2020 to add info for RG marking in graph stream - used by walker!
	public Transition_Type type;
	public String machineName;
	public ArrayList<Place> preset;
	public ArrayList<Place> postset;
	// added 29.08.2020 -> support for inhibitor and reset arcs
	public ArrayList<Place> inhibitorPlaces;
	public ArrayList<Place> resetPlaces;

	
	public void addInhibitorPlace(Place p) {
		inhibitorPlaces.add(p);
	}

	public void addResetPlace(Place p) {
		resetPlaces.add(p);
	}
	
	public Transition(String name, String identifier, Transition_Type type, String netName) {
		this.name = name;
		this.identifier = identifier;
		this.type = type;
		this.machineName = netName;
		this.preset = new ArrayList<Place>();
		this.postset = new ArrayList<Place>();
		this.inhibitorPlaces = new ArrayList<Place>();
		this.resetPlaces = new ArrayList<Place>();
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
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
		// GraphLogger.getInstance().addToLog("Signal", name, "StateMachine");
		GraphLogger.getInstance().addToLog("Signal", identifier, "StateMachine");
	}
}