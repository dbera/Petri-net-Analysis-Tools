package org.eclipse.petrinets.graphs.validators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.petrinets.ValidationType;
import org.eclipse.petrinets.graphs.IStreamingGraphValidators;
import org.graphstream.algorithm.TarjanStronglyConnectedComponents;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

public class SCCValidator implements IStreamingGraphValidators {

	List<String> listOfInitialNodes = new ArrayList<String>();
	List<String> listOfInterfaceNodes = new ArrayList<String>();
	List<String> listOfPlaces = new ArrayList<String>();
	List<String> listOfTransitions = new ArrayList<String>();
	Map<String, ArrayList<String>> mapModuleIdToNodeIds = new HashMap<String, ArrayList<String>>();
	Map<String, String> mapOfPlaceIDToName = new HashMap<String, String>();
	Map<String, String> mapOfTransitionIDToName = new HashMap<String, String>();
	
	
	
	@Override
	public String checkProperty(Graph graph, ValidationType vtype) {

		String outcome = new String();
		if(!vtype.equals(ValidationType.SCC)) 
			return "<!> Fatal: Property Check of Type: " + vtype.toString() + " is not supported \n";
		
		TarjanStronglyConnectedComponents tscc = new TarjanStronglyConnectedComponents();
		tscc.init(graph);
		tscc.setSCCIndexAttribute("node.scc");
		tscc.setIndexGenerator(new TarjanStronglyConnectedComponents.IntegerIndexGenerator());
		tscc.compute();
		
		Map<Integer, List<String>> mapOfSCCNodes = new HashMap<Integer, List<String>>(); // map of SCC ID -> List of Nodes
		for (Node node : graph.getEachNode()) {
			// outcome += "\n" + tscc.getSCCIndexAttribute() + " -> sccindex attr";
			// outcome += "\n" + n.getAttribute(tscc.getSCCIndexAttribute()) + " -> node.scc"; 
			// outcome += "\n" + n.getId() + " -> nodeID";
			if(!mapOfSCCNodes.containsKey(node.getAttribute(tscc.getSCCIndexAttribute()))) {
				ArrayList<String> lst = new ArrayList<String>();
				lst.add(node.getId());
				mapOfSCCNodes.put(node.getAttribute(tscc.getSCCIndexAttribute()), lst);
			}
			else
				mapOfSCCNodes.get(node.getAttribute(tscc.getSCCIndexAttribute())).add(node.getId());
		}
		
		for(Integer sccID : mapOfSCCNodes.keySet()) {
			outcome += "\n<!> SCC-ID: "+ sccID + "\n";
			for(String nodeID : mapOfSCCNodes.get(sccID)) {
				outcome += " + NodeID: " + nodeID + "\n";
			}
		}
		return outcome;
	}

	@Override
	public void setDataStructures(Map<String, String> _mapOfPlaceIDToName, Map<String, String> _mapOfTransitionIDToName,
			List<String> _listOfInitialNodes, List<String> _listOfInterfaceNodes, List<String> _listOfPlaces,
			List<String> _listOfTransitions, Map<String, ArrayList<String>> _mapModuleIdToNodeIds) {
		
		mapOfPlaceIDToName = _mapOfPlaceIDToName;
		mapOfTransitionIDToName = _mapOfTransitionIDToName;
		listOfInitialNodes = _listOfInitialNodes;
		listOfInterfaceNodes = _listOfInterfaceNodes;
		listOfPlaces = _listOfPlaces;
		listOfTransitions = _listOfTransitions;
		mapModuleIdToNodeIds = _mapModuleIdToNodeIds;
	}

}
