package org.eclipse.petrinets;

import org.eclipse.petrinets.gui.SimulationStatusWindow;

public interface IGraphGenerator {
	
	void resetGraph();
	
	void addEdge(String edgeLabel, String edgeID, String srcNode, String dstNode, String context);
	void addNode(String nodeLabel, String nodeID, String type, String context);
	void removeNode(String nodeID);
	
	// can be called after all nodes have been added to the graph
	// validate the node type is of type place
	// utility function if you want to create local data structures
	void computeDS();
	
	// following two functions are for Petri nets only
	void markNodeAsInitial(String nodeLabel, String nodeID, String type);
	void markNodeAsInterfacePlace(String nodeLabel, String nodeID, String type);
	
	void reloadGraphInstanceFromFile();
	void setStatusWindow(SimulationStatusWindow sWindow); // useful for printing msgs based on interactions of user with graph stream viewer
	
	
	String computeStructuralChecks(ValidationType vtype);
}
