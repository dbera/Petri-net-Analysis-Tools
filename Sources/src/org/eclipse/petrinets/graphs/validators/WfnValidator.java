package org.eclipse.petrinets.graphs.validators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.petrinets.ValidationType;
import org.eclipse.petrinets.graphs.IStreamingGraphValidators;
import org.eclipse.petrinets.gui.SimulationStatusWindow;
import org.eclipse.petrinets.gui.TextType;
import org.graphstream.algorithm.AStar;
import org.graphstream.algorithm.TarjanStronglyConnectedComponents;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

public class WfnValidator implements IStreamingGraphValidators {

	List<String> listOfInitialNodes = new ArrayList<String>();
	List<String> listOfInterfaceNodes = new ArrayList<String>();
	List<String> listOfPlaces = new ArrayList<String>();
	List<String> listOfTransitions = new ArrayList<String>();
	List<String> listOfFinalNodes = new ArrayList<String>();
	
	Map<String, ArrayList<String>> mapModuleIdToNodeIds = new HashMap<String, ArrayList<String>>();
	Map<String, String> mapOfPlaceIDToName = new HashMap<String, String>();
	Map<String, String> mapOfTransitionIDToName = new HashMap<String, String>();
	
	SimulationStatusWindow sWindow;
	String outcome = new String();
	
	@Override
	public void setStatusWindowInstance(SimulationStatusWindow sWindow) {
		this.sWindow = sWindow;
	}
	
	private String getNodeName(String id) {
		if(mapOfPlaceIDToName.containsKey(id))
			return mapOfPlaceIDToName.get(id);
		else if(mapOfTransitionIDToName.containsKey(id))
			return mapOfTransitionIDToName.get(id);
		else return id;
	}

	private boolean checkInitialAndFinalPlaces(Graph graph) {
		boolean isInitialAndFinalOK = true;
		if(mapModuleIdToNodeIds.keySet().size() != listOfInitialNodes.size()) {
			outcome = "\n <!> [Violation] Each component does not have one initial place!" + "\n";
			addErrorTextToStatusWindow(outcome);
			isInitialAndFinalOK = false;
		}
		else {
			outcome = "\n Each module has one initial place: OK" + "\n";
			addInfoTextToStatusWindow(outcome);
		}

		if(mapModuleIdToNodeIds.keySet().size() != listOfFinalNodes.size()) {
			outcome = "\n <!> [Violation] Each component does not have one final place!" + "\n";
			addErrorTextToStatusWindow(outcome);
			isInitialAndFinalOK = false;
		}
		else {
			outcome = "\n Each module has one final place: OK" + "\n";
			addInfoTextToStatusWindow(outcome);
		}

		if(true) {
			
			for(String mid : mapModuleIdToNodeIds.keySet()) {
				if(!checkInitialPlaceConnections(graph, mid))
					isInitialAndFinalOK = false;
			}
		}
		return isInitialAndFinalOK;
	}
	
	private boolean checkInitialPlaceConnections(Graph graph, String mid) {
		boolean isInitialAndFinalOK = true;
		for(String n : mapModuleIdToNodeIds.get(mid)) {
			if(listOfInitialNodes.contains(n)) {
				if(graph.getNode(n).getInDegree() > 0) {
					// the place p with initial token is not an initial place!
					outcome = "\n <!> In component: "+mid + "\n";
					addInfoTextToStatusWindow(outcome);
					outcome = "   + [Violation] The place " + getNodeName(n) + " with initial token has incoming arcs!" + "\n";
					addErrorTextToStatusWindow(outcome);
					isInitialAndFinalOK = false;
				}
			}
		}
		return isInitialAndFinalOK;
	}
	
	@Override
	public String checkProperty(Graph graph, ValidationType vtype) {
		
		outcome = new String();
		if(!vtype.equals(ValidationType.WFN)) {
			addErrorTextToStatusWindow("<!> Fatal: Property Check of Type: " + vtype.toString() + " is not supported \n");
			return "<!> Fatal: Property Check of Type: " + vtype.toString() + " is not supported \n";
		}

		/// Check WFN property /// 
		/// 1. Exists one initial and one final place in a module
		for(String mid : mapModuleIdToNodeIds.keySet()) {
			for(String n : mapModuleIdToNodeIds.get(mid)) {
				if(graph.getNode(n).getOutDegree() < 1) {
					if(listOfPlaces.contains(n) && !listOfInterfaceNodes.contains(n))
						listOfFinalNodes.add(n);
				}
			}
		}
		
		// for each module make a function to find initial and final place
		/*for(String n : listOfInterfaceNodes) {
			System.out.println(" \n Final Node " + n + "\n ");
		}*/
		
		boolean isInitialAndFinalOK = checkInitialAndFinalPlaces(graph);
		
		Map<String, Boolean> mapOfModulePathProperty = new HashMap<String, Boolean>();
		if(true) {
			/// 2. All nodes are on a path from initial to final place
			// start with initial node of a module
			for(String mid : mapModuleIdToNodeIds.keySet()) {
				mapOfModulePathProperty.put(mid, true);
				// check paths in module mid
				List<String> initP = getIdlePlaces(mid);
				List<String> finP = getFinalPlaces(mid);
				
				if(initP.size() == 1 && finP.size() == 1) {
					outcome = "\n\n Checking path property of component: " + mid + " \n";
					addInfoTextToStatusWindow(outcome);
					AStar astar = new AStar(graph);				
					for(String nid : mapModuleIdToNodeIds.get(mid)) {
						if(!(listOfInterfaceNodes.contains(nid))) {
							if(initP.get(0).equals(nid) || finP.get(0).equals(nid)) { }
							else {
								// if nid belongs to the same module as place
								//outcome += "\n Module: " + mid + " Node: "+ nid + " Initial: " + initP.get(0) + " Final: " + finP.get(0);
								astar.compute(initP.get(0), nid);
								if(astar.getShortestPath() == null) {
									mapOfModulePathProperty.put(mid, false);
									outcome = "\n   + [Violation] There is no path to "+ getNodeName(nid) + " from " + getNodeName(initP.get(0)) + "\n";
									addErrorTextToStatusWindow(outcome);
								}
								//else
									//outcome += "\n Path: " + astar.getShortestPath() + "\n";
								astar.compute(nid, finP.get(0));
								if(astar.getShortestPath() == null) {
									mapOfModulePathProperty.put(mid, false);
									outcome = "\n   + [Violation] There is no path from "+ getNodeName(nid) + " to " + getNodeName(initP.get(0)) + "\n";
									addErrorTextToStatusWindow(outcome);
								}
								//else
									//outcome += "\n Path: " + astar.getShortestPath() + "\n";
							}
						}
					}
				}
				else {
					mapOfModulePathProperty.put(mid, false);
					outcome = "\n\n Skipping path property check on component: " + mid + " \n";
					addErrorTextToStatusWindow(outcome);
				}
			}
			
			if(mapOfModulePathProperty.keySet().size() > 0) {
				outcome = "\n\n [ Summary ] \n";
				addHeaderTextToStatusWindow(outcome);
			}
			for(String mid : mapOfModulePathProperty.keySet()) {
				if(mapOfModulePathProperty.get(mid)) {
					outcome = "\n <+> The net in module: " + mid + " is a WFN: OK" + "\n";
					addInfoTextToStatusWindow(outcome);
				}
				else {
					outcome = "\n <!> The net in module: " + mid + " is a WFN: NOK" + "\n";
					addErrorTextToStatusWindow(outcome);
				}
			}			
		}
		
		return outcome;
	}

	// for each module make a function to find initial and final place
	public List<String> getIdlePlaces(String mid) {
		List<String> listOfIdlePlaces = new ArrayList<String>();
		for(String n : mapModuleIdToNodeIds.get(mid)) {
			if(listOfInitialNodes.contains(n)) {
				listOfIdlePlaces.add(n);
				//System.out.println("Module: "+mid+" has inital place: "+n);
			}
		}
		return listOfIdlePlaces;
	}

	public List<String> getFinalPlaces(String mid) {
		List<String> listOfFinalPlaces = new ArrayList<String>();
		for(String n : mapModuleIdToNodeIds.get(mid)) {
			if(listOfFinalNodes.contains(n)) {
				listOfFinalPlaces.add(n);
				//System.out.println("Module: "+mid+" has final place: "+n);
			}
		}
		return listOfFinalPlaces;
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
