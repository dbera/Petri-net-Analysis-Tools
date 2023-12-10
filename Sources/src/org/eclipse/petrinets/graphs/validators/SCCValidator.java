package org.eclipse.petrinets.graphs.validators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.petrinets.ValidationType;
import org.eclipse.petrinets.graphs.IStreamingGraphValidators;
import org.eclipse.petrinets.gui.SimulationStatusWindow;
import org.eclipse.petrinets.gui.TextType;
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

	SimulationStatusWindow sWindow;
	
	// General Idea
	// mapModuleIdToNodeIds
	// for each module ID
	// if all nodes of a module belong to a SCC ID
	// 1.a are there other nodes as well?
	// If yes then check if all nodes of other modules are also in same SCC ID
	// If no then module ID is SCC
	// mapOfSCCNodes

	@Override
	public void setStatusWindowInstance(SimulationStatusWindow sWindow) {
		this.sWindow = sWindow;
	}
	
	public String checkPropertyForRG(Graph graph) {
		
		String outcome = new String();
		TarjanStronglyConnectedComponents tscc = new TarjanStronglyConnectedComponents();
		
		tscc.init(graph);
		tscc.setSCCIndexAttribute("node.scc");
		tscc.setIndexGenerator(new TarjanStronglyConnectedComponents.IntegerIndexGenerator());
		tscc.compute();
		
		Map<Integer, List<String>> mapOfSCCNodes = new HashMap<Integer, List<String>>();
		for (Node node : graph.getEachNode()) {
			if(!mapOfSCCNodes.containsKey(node.getAttribute(tscc.getSCCIndexAttribute()))) {
				ArrayList<String> lst = new ArrayList<String>();
				lst.add(node.getId());
				mapOfSCCNodes.put(node.getAttribute(tscc.getSCCIndexAttribute()), lst);
			}
			else
				mapOfSCCNodes.get(node.getAttribute(tscc.getSCCIndexAttribute())).add(node.getId());			
		}
		
		for(Integer idx : mapOfSCCNodes.keySet()) {
			outcome = "\n   > SCC ClusterID: " + idx  + " contains " + mapOfSCCNodes.get(idx);
			addInfoTextToStatusWindow(outcome);
		}
		return outcome;
	}
	
	@Override
	public String checkProperty(Graph graph, ValidationType vtype) {

		String outcome = new String();
		if(vtype.equals(ValidationType.SCC_PN) || vtype.equals(ValidationType.SCC_RG)) {} 
		else {
			addErrorTextToStatusWindow("<!> Fatal: Property Check of Type: " + vtype.toString() + " is not supported \n");
			return "<!> Fatal: Property Check of Type: " + vtype.toString() + " is not supported \n";
		}
		
		if(vtype.equals(ValidationType.SCC_PN)) {
			TarjanStronglyConnectedComponents tscc = new TarjanStronglyConnectedComponents();
			tscc.init(graph);
			tscc.setSCCIndexAttribute("node.scc");
			tscc.setIndexGenerator(new TarjanStronglyConnectedComponents.IntegerIndexGenerator());
			tscc.compute();
			
			Map<Integer, List<String>> mapOfSCCNodes = new HashMap<Integer, List<String>>(); // map of SCC ID -> List of Nodes
			Map<Integer, HashSet<String>> mapOfSCCModules = new HashMap<Integer, HashSet<String>>(); // map of SCC ID -> List of Modules
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
				mapOfSCCModules.put(sccID, getModuleSetofSCC(sccID, mapOfSCCNodes, graph));
			}
			
			for(Integer idx : mapOfSCCModules.keySet()) {
				outcome = "\n   > SCC ID: " + idx  + " has nodes from modules: " + mapOfSCCModules.get(idx);
				addDetInfoTextToStatusWindow(outcome);
				//for(String mid : mapOfSCCModules.get(idx)) {
					//outcome += " has nodes from module: " + mid + "\n";
				//}
			}
			
			//outcome += "\n <+> No. of Strongly Connected Components: " + mapOfSCCModules.keySet().size() + "\n";
			outcome = "\n";
			addInfoTextToStatusWindow(outcome);
			
			
			boolean noModuleContainedInSCC = true;
			for(Integer sccID : mapOfSCCModules.keySet()) {
				for(String mid : mapOfSCCModules.get(sccID)) {
					if(isModuleInSCC(mid, mapOfSCCNodes.get(sccID))) {
						outcome = "\n   + Module: " + mid + " is fully present in SCC-ID: " + sccID + "\n";
						addInfoTextToStatusWindow(outcome);
						noModuleContainedInSCC = false;
					}
				}
			}
			
			if(noModuleContainedInSCC) {
				outcome = "\n   + No modules were fully contained in any SCC!\n";
				addErrorTextToStatusWindow(outcome);
			}
			
			/*for(Integer sccID : mapOfSCCNodes.keySet()) {
				outcome += "\n<!> SCC-ID: "+ sccID + "\n";
				for(String nodeID : mapOfSCCNodes.get(sccID)) {
					outcome += " + NodeID: " + nodeID + "\n";
				}
			}*/
		}
		else outcome = checkPropertyForRG(graph);
		
		return outcome;
	}
	
	private HashSet<String> getModuleSetofSCC(Integer sccID, Map<Integer, List<String>> mapOfSCCNodes, Graph graph) {
		HashSet<String> setOfModules = new HashSet<String>();
		for(String nodeID : mapOfSCCNodes.get(sccID)) {
			ArrayList<String> containers = graph.getNode(nodeID).getAttribute("node.containers");
			setOfModules.add(containers.get(0));
		}
		return setOfModules;
	}

	private boolean isModuleInSCC(String mid, List<String> listOfSCCNodes) {
		List<String> listOfNodesInModule = mapModuleIdToNodeIds.get(mid);
		// check if listOfNodesInModule is contained in listOfNodesInModule
		for(String elm : listOfNodesInModule) {
			if(!listOfSCCNodes.contains(elm)) {
				return false;
			}
		}
		return true;
	}

	public <T> Set<T> convertListToSet(List<T> list) { 
        // create an empty set 
        Set<T> set = new HashSet<>();   
        // Add each element of list into the set 
        for (T t : list) 
            set.add(t);   
        // return the set 
        return set; 
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

	private void addHeaderTextToStatusWindow(String info) { sWindow.addText(info, TextType.HEADER); }
	private void addInfoTextToStatusWindow(String info) { sWindow.addText(info, TextType.INFO);}
	private void addDetInfoTextToStatusWindow(String info) { sWindow.addText(info, TextType.DETAILED_INFO);}
	private void addErrorTextToStatusWindow(String info) { sWindow.addText(info, TextType.ERROR);}
	private void addDetailedErrorTextToStatusWindow(String info) { sWindow.addText(info, TextType.DETAILED_ERROR);}
}
