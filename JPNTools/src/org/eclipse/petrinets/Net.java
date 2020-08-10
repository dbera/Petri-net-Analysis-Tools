package org.eclipse.petrinets;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;

public class Net implements INet
{
	public String name;
	public ArrayList<Transition> listOfTransitions;
	public ArrayList<Place> listOfPlaces;
	public ArrayList<InputArc> listOfInputArcs;
	public ArrayList<OutputArc> listOfOutputArcs;
	private NetState current_state;

	Net(String name) {
		this.name = name;
		this.listOfTransitions = new ArrayList<Transition>();
		this.listOfPlaces = new ArrayList<Place>();
		this.listOfInputArcs = new ArrayList<InputArc>();
		this.listOfOutputArcs = new ArrayList<OutputArc>();
		current_state = new NetState();
	}

	/*public void displayStateMachineVars() {
		current_state.smVarContainer.displaySMVars();
	}*/

	public Place getPlace(String place_name) {
		Place p = listOfPlaces.get(0);
		Iterator<Place> iterP = listOfPlaces.iterator();
		while (iterP.hasNext()) {
			p = iterP.next();
			if(p.name.equals(place_name))
				return p;
		}
		return p;
	}
	
	public Transition getTransition(String transition_name) {
		Transition t = listOfTransitions.get(0);
		Iterator<Transition> iterT = listOfTransitions.iterator();
		while (iterT.hasNext()) {
			t = iterT.next();
			if(t.name.equals(transition_name))
				return t;
		}
		return t;
	}

	public void addNewTransition(Transition t) throws SimulatorException {
		Iterator<Transition> iterT = listOfTransitions.iterator();
		while (iterT.hasNext()) {
			Transition t1 = iterT.next();
			if(t1.name == t.name)
				throw new SimulatorException("Such a Transition already Exists.");
		}
		listOfTransitions.add(t);
	}

	public void addNewPlace(Place p) throws SimulatorException {
		Iterator<Place> iterP = listOfPlaces.iterator();
		while (iterP.hasNext()) {
			Place p1 = iterP.next();
			if(p1.name == p.name)
				throw new SimulatorException("Such a Place already Exists.");
		}
		listOfPlaces.add(p);
	}
	
	public void addInputArc(InputArc ia) throws SimulatorException {
		Iterator<InputArc> iterIA = listOfInputArcs.iterator();
		while(iterIA.hasNext()) {
			InputArc ia1 = iterIA.next();
			if((ia1.p.name == ia.p.name) && (ia1.t.name == ia.t.name))
				throw new SimulatorException("An Input Arc already Exists.");
		}
		listOfInputArcs.add(ia);
	}
	
	public void addOutputArc(OutputArc oa) throws SimulatorException {
		Iterator<OutputArc> iterOA = listOfOutputArcs.iterator();
		while(iterOA.hasNext()) {
			OutputArc oa1 = iterOA.next();
			if((oa1.p.name == oa.p.name) && (oa1.t.name == oa.t.name))
				throw new SimulatorException("An Output Arc already Exists.");
		}		
		listOfOutputArcs.add(oa);
	}

	// Looks at the number of tokens in each place and updates it.
	private void determineCurrentMarking() {
		int j = 0;
		while(listOfPlaces.size() > j) {
			current_state.m.put(listOfPlaces.get(j).name, listOfPlaces.get(j).listOfTokens.size());
			j++;
		}
	}
	
	private Marking getCurrentMarking() {
		determineCurrentMarking();
		return current_state.m;
	}
	
	private void setCurrentMarking(Marking m) {
		//current_state.m = m;
		
		// Reset the tokens in all places to Zero
		Iterator<Place> iterP = listOfPlaces.iterator();
		while (iterP.hasNext())
			iterP.next().clearAllTokens();
		
		// Update the token in place according to Marking
		   Iterator<Entry<String, Integer>> it = m.entrySet().iterator();
		   while (it.hasNext()) {
		       Marking.Entry<String, Integer> pair = (Marking.Entry<String, Integer>)it.next();
		       // TODO Handle more than one token!
		       if(pair.getValue() > 0) {
		       	int numTok = pair.getValue();
		       	while(numTok > 0) {
		       		Token tok = new IntegerToken(0);
		       		try { getPlace(pair.getKey()).addToken(tok); } catch (SimulatorException e) { e.printStackTrace(); }
		       		numTok--;
		       	}
		       }	
		   }
		   
		   // TODO: Assert that the outcome is Correct
		   determineCurrentMarking();
	}
	
	public ArrayList<String> getCurrentStateName() throws SimulatorException {
		ArrayList<String> listOfPlacesWithTokens = new ArrayList<String>();
		determineCurrentMarking();
		Iterator<Entry<String, Integer>> it = current_state.m.entrySet().iterator();
		while (it.hasNext()) {
			Marking.Entry<String, Integer> pair = (Marking.Entry<String, Integer>)it.next();
		    if(pair.getValue() > 0) 
		       listOfPlacesWithTokens.add(pair.getKey());
		    	//return pair.getKey();
		}
		if(listOfPlacesWithTokens.size() < 1) {
			System.out.println("ERROR: No Tokens in Net!");
		   	throw new SimulatorException("There are no Tokens in Net!");
		}
		else return listOfPlacesWithTokens;
	}
	
	public NetState getCurrentState() {
		determineCurrentMarking();
		return current_state;
	}
	
	public void setCurrentState(NetState s) {
		setCurrentMarking(s.m);
		//current_state.smVarContainer = new NetVariables(s.smVarContainer);
	}
	
	// Enabling Condition
	public ArrayList<Transition> getEnabledTransitions() { //InputParametersContainer c) {
		// For each t in listOfTransitions, check if all its pre-places have at least one token
		ArrayList<Transition> listT = new ArrayList<Transition>();
		Iterator<Transition> iterT = listOfTransitions.iterator();
		determineCurrentMarking();
		while (iterT.hasNext()) {
			Transition t = iterT.next();
			// Checking Token Enabled and Data Enabled!
			//String current_state_name = "";
			//try { current_state_name = getCurrentStateName(); } catch (SimulatorException e) { e.printStackTrace(); }
			//current_state_name = getEnablingPlaceForTransition(t);
			//if(isTransitionEnabled(t) && t.isEnabled(current_state_name, current_state.smVarContainer, c)) {
			//System.out.println("0 Check for transition "+t.name+ " has enabling place "+getEnablingPlaceForTransition(t));
			if(isTransitionEnabled(t)) {
				//System.out.println("1 Check for transition "+t.name+ " has enabling place "+getEnablingPlaceForTransition(t));
				if(t.isEnabled(getEnablingPlaceForTransition(t))) { //, current_state.smVarContainer, c)) {
					//System.out.println("2 Check for transition "+t.name+ " has enabling place "+getEnablingPlaceForTransition(t));
					listT.add(t);
				}
			}
		}
		return listT;
	}
	
	private boolean isTransitionEnabled(Transition t) {
		// iterating the preset of the transition
		Iterator<Place> iterP = t.preset.iterator();
		while(iterP.hasNext()) {
			Place p = iterP.next();
			//System.out.println(current_state.m.get(p));
			if(current_state.m.containsKey(p.name)) {
				// At least one place does not have a token
				if(current_state.m.get(p.name) == 0)
					return false;
			}
			else return false; // There is an error, Raise Exception!
		}
		// transition has no pre-place OR has enough tokens, so enabled
		return true;
	}

	public String getEnablingPlaceForTransition(Transition t) {
		String enablingPlace = new String();
		Iterator<Place> iterP = t.preset.iterator();
		determineCurrentMarking();
		while(iterP.hasNext()) {
			Place p = iterP.next();
			if(current_state.m.containsKey(p.name)) {
				// At least one place does not have a token
				if(current_state.m.get(p.name) > 0) {
					enablingPlace = p.name;
				}
			}			
		}
		return enablingPlace;
	}
	
	// Firing
	public Marking fireEnabledTransition(Transition t) throws SimulatorException { //, InputParametersContainer c) throws SimulatorException {
		String current_state_name = getEnablingPlaceForTransition(t); //getCurrentStateName();
		// Check if transition t is enabled
		ArrayList<Transition> enbT = getEnabledTransitions();
		if(enbT.contains(t)) {
			// for each place p in preset of t, remove one token.
			Iterator<Place> iterPre = t.preset.iterator();
			while(iterPre.hasNext()) {
				Place p = iterPre.next();
				if(current_state.m.containsKey(p.name)) {
					if(current_state.m.get(p.name) > 0) {
						// remove a token
						//int numTok = current_state.m.get(p);
						//System.out.println(p); System.out.println(numTok);
						//current_state.m.put(p, numTok-1);
						//System.out.println(current_state.m.get(p));
						p.removeToken();
					} else throw new SimulatorException("Transition is enabled but missing token in its pre-place!!");
				} else throw new SimulatorException("Transition is enabled but Marking is Missing a Place!");
			}
			
			// Execute the Clause of the chosen transition
			t.executeActions(current_state_name); //, current_state.smVarContainer, c);
			
			// for each place p in postset of t, add one token.
			Iterator<Place> iterPost = t.postset.iterator();
			while(iterPost.hasNext()) {
				Place p = iterPost.next();
				if(current_state.m.containsKey(p.name)) {
					// add a token
					// int numTok = current_state.m.get(p);
					// System.out.println(p); System.out.println(numTok);
					// current_state.m.put(p, numTok+1);
					// System.out.println(current_state.m.get(p));
					Token tok = new IntegerToken(0);
					p.addToken(tok);
				} else throw new SimulatorException("Transition is enabled but Marking is Missing a Place!");
			}
		} else throw new SimulatorException("The Transition is Not Enabled!");
		
		// compute the current marking and return it.
		return getCurrentMarking();
	}

	@Override
	public void displayStateMachineVars() {
		// TODO Auto-generated method stub
		
	}
}

