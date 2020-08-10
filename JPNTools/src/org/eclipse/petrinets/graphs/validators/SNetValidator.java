package org.eclipse.petrinets.graphs.validators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.petrinets.ValidationType;
import org.eclipse.petrinets.graphs.IStreamingGraphValidators;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

public class SNetValidator implements IStreamingGraphValidators {

	List<String> listOfInitialNodes = new ArrayList<String>();
	List<String> listOfInterfaceNodes = new ArrayList<String>();
	List<String> listOfPlaces = new ArrayList<String>();
	List<String> listOfTransitions = new ArrayList<String>();
	Map<String, ArrayList<String>> mapModuleIdToNodeIds = new HashMap<String, ArrayList<String>>();
	Map<String, String> mapOfPlaceIDToName = new HashMap<String, String>();
	Map<String, String> mapOfTransitionIDToName = new HashMap<String, String>();
	
	public String getNodeName(String id) {
		if(mapOfPlaceIDToName.containsKey(id))
			return mapOfPlaceIDToName.get(id);
		else if(mapOfTransitionIDToName.containsKey(id))
			return mapOfTransitionIDToName.get(id);
		else return id;
	}
	
	@Override
	public String checkProperty(Graph graph, ValidationType vtype) {
		String outcome = new String();
		boolean isSatisfied = true;
		if(!vtype.equals(ValidationType.SNET)) 
			return "<!> Fatal: Property Check of Type: " + vtype.toString() + " is not supported \n";
		
		Map<String, Boolean> mapOfModuleSNETProperty = new HashMap<String, Boolean>();
		for(String mid : mapModuleIdToNodeIds.keySet()) {
			mapOfModuleSNETProperty.put(mid, true);
			for(String nid : mapModuleIdToNodeIds.get(mid)) {
				if(!(listOfInterfaceNodes.contains(nid))) {
					Node node = graph.getNode(nid);
					if(node.getAttribute("ui.class").equals("transition")) {
						// System.out.println("The node: " + node.getId() + " is a Transition");
						if(node.getOutDegree() > 1) {
							isSatisfied = false;
							mapOfModuleSNETProperty.put(mid, false);
							outcome += "\n <!> In component: "+mid;
							outcome += "\n   + < Transition: " + getNodeName(node.getId()) + " has more than one outgoing arc! >"  + "\n";
						}
						if(node.getInDegree() > 1) {
							isSatisfied = false;
							mapOfModuleSNETProperty.put(mid, false);
							outcome += "\n <!> In component: "+mid;
							outcome += "\n   + < Transition: " + getNodeName(node.getId()) + " has more than one incoming arc! >" + "\n";
						}
					}
				}
			}
		}

		// summarize here

		if(mapOfModuleSNETProperty.keySet().size() > 0)
			outcome += "\n\n [ Summary ] \n";
		for(String mid : mapOfModuleSNETProperty.keySet()) {
			if(mapOfModuleSNETProperty.get(mid))
				outcome += "\n <+> The net in module: " + mid + " is a S-Net: OK" + "\n";
			else
				outcome += "\n <!> The net in module: " + mid + " is a S-Net: NOK" + "\n";
		}			
		
		// if(isSatisfied) outcome = "\n <+> State Machine Net: OK" + outcome;
		// else outcome = "\n <!> State Machine Net: NOK" + outcome;
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
