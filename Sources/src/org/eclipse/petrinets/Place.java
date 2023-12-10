package org.eclipse.petrinets;

import java.util.ArrayList;

public class Place 
{
	String name;
	String identifier; // added 12.08.2020 to add info for RG marking in graph stream - used by walker!
	Type type;
	boolean isInitial;
	ArrayList<Token> listOfTokens;
	ArrayList<Transition> preset;
	ArrayList<Transition> postset;
	// added 29.08.2020 support for inhibitor and reset arcs
	ArrayList<Transition> inhibitorTransition;
	ArrayList<Transition> resetTransition;
	
	public void addInhibitorTransition(Transition t) {
		inhibitorTransition.add(t);
	}

	public void addResetTransition(Transition t) {
		resetTransition.add(t);
	}
	
	Place(String name, String identifier, Type type, Token tok) throws SimulatorException {
		this.name = name;
		this.identifier = identifier;
		this.type = type;
		this.isInitial = false;
		listOfTokens = new ArrayList<Token>();
		this.preset = new ArrayList<Transition>();
		this.postset = new ArrayList<Transition>();
		this.inhibitorTransition = new ArrayList<Transition>();
		this.resetTransition = new ArrayList<Transition>();
		this.addToken(tok);
	}
	
	Place(String name, String identifier, Type type) {
		this.name = name;
		this.identifier = identifier;
		this.type = type;
		this.isInitial = false;
		this.listOfTokens = new ArrayList<Token>();
		this.preset = new ArrayList<Transition>();
		this.postset = new ArrayList<Transition>();
		this.inhibitorTransition = new ArrayList<Transition>();
		this.resetTransition = new ArrayList<Transition>();
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	
	public void setInitial() {
		this.isInitial = true;
	}
	
	public boolean isInitialPlace() {
		return isInitial;
	}
	
	public String getName() {
		return name;
	}
	
	public Type getType() {
		return type;
	}
	
	public ArrayList<Token> getTokens() {
		return listOfTokens;
	}
	
	public void addToken(Token tok) throws SimulatorException{
		// Check if token type matches the place type
		if(tok.getTokenType() == this.type) {
			this.listOfTokens.add(tok);
		}
		else throw new SimulatorException("The Token Type does not match the Place Type.");
	}
	
	// Remove specific token
	public void removeToken(Token tok) throws SimulatorException {
		if(this.listOfTokens.size() > 0) {
			if(this.listOfTokens.contains(tok)) {
				this.listOfTokens.remove(tok);
			}
			else throw new SimulatorException("The specified token does not exist.");
		}
	}
	
	//Remove first token
	public void removeToken() {
		if(this.listOfTokens.size() > 0) {
			this.listOfTokens.remove(0);
		}
	}
	
	public void clearAllTokens() {
		this.listOfTokens.clear();
	}
	
	public void addPreTransition(Transition t) {
		preset.add(t);
	}
	
	public void addPostTransition(Transition t) {
		postset.add(t);
	}
}
