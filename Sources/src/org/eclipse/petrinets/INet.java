package org.eclipse.petrinets;

import java.util.ArrayList;

public interface INet {

	void displayStateMachineVars();
	Place getPlace(String place_name);
	Transition getTransition(String transition_name);
	void addNewTransition(Transition t) throws SimulatorException;
	void addNewPlace(Place p) throws SimulatorException;
	void addInputArc(InputArc ia) throws SimulatorException;
	void addInhibitorArc(InhibitorArc ia) throws SimulatorException;
	void addResetArc(ResetArc ra) throws SimulatorException;
	void addOutputArc(OutputArc oa) throws SimulatorException;
	ArrayList<String> getCurrentStateName() throws SimulatorException;
	String getCurrentStateLabelWithTokens() throws SimulatorException;
	NetState getCurrentState();
	void setCurrentState(NetState s);
	ArrayList<Transition> getEnabledTransitions(); //InputParametersContainer c);
	String getEnablingPlaceForTransition(Transition t); // this must be refactored to return list TODO
	void setBoundsOfOmegaMarkings(int bound);
	Marking fireEnabledTransition(Transition t) throws SimulatorException; //, InputParametersContainer c
}
