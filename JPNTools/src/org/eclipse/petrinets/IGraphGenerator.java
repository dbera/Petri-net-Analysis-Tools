package org.eclipse.petrinets;

public interface IGraphGenerator {
	
	void addEdge(String edgeLabel, String edgeID, String srcNode, String dstNode, String context);
	void addNode(String nodeLabel, String nodeID, String type, String context);
	void removeNode(String nodeID);
	
	// can be called after all nodes have been added to the graph
	// validate the node type is of type place
	void computeDS();
	void markNodeAsInitial(String nodeLabel, String nodeID, String type);
	void markNodeAsInterfacePlace(String nodeLabel, String nodeID, String type);
	
	String computeStructuralChecks(ValidationType vtype);
}
