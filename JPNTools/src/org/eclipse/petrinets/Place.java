package org.eclipse.petrinets;

import java.util.ArrayList;

public class Place 
{
	String name;
	Type type;
	boolean isInitial;
	ArrayList<Token> listOfTokens;
	ArrayList<Transition> preset;
	ArrayList<Transition> postset;
	
	Place(String name, Type type, Token tok) throws SimulatorException {
		this.name = name;
		this.type = type;
		this.isInitial = false;
		listOfTokens = new ArrayList<Token>();
		this.preset = new ArrayList<Transition>();
		this.postset = new ArrayList<Transition>();
		this.addToken(tok);
	}
	
	Place(String name, Type type) {
		this.name = name;
		this.type = type;
		this.isInitial = false;
		this.listOfTokens = new ArrayList<Token>();
		this.preset = new ArrayList<Transition>();
		this.postset = new ArrayList<Transition>();
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
