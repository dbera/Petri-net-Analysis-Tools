package org.eclipse.petrinets.graphs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.petrinets.ValidationType;
import org.graphstream.graph.Graph;

public interface IStreamingGraphValidators {

	public void setDataStructures(
			Map<String, String> mapOfPlaceIDToName, 
			Map<String, String> mapOfTransitionIDToName,
			List<String> listOfInitialNodes,
			List<String> listOfInterfaceNodes,
			List<String> listOfPlaces,
			List<String> listOfTransitions,
			Map<String, ArrayList<String>> mapModuleIdToNodeIds);

	public String checkProperty(Graph graph, ValidationType vtype);
}
