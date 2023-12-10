package org.eclipse.petrinets;

public class ArcPair 
{
	public String getId() {
		return id;
	}

	public String getFirst() {
		return first;
	}

	public String getSecond() {
		return second;
	}

	final String id;
	final String first;
	final String second;
	final boolean isInputArc;
	final String container;
	final boolean isInhibitor;
	final boolean isReset;
	
	ArcPair(String _id, String _f, String _s, boolean _isInputArc, boolean _isInhibitor, boolean _isReset, String _container) { 
		id = _id; first = _f; second = _s; 
		isInputArc = _isInputArc; container = _container;	
		isInhibitor = _isInhibitor; isReset = _isReset;
	}
}
