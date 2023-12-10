package org.eclipse.petrinets.graphs.validators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.petrinets.ValidationType;
import org.eclipse.petrinets.graphs.IStreamingGraphValidators;
import org.eclipse.petrinets.gui.SimulationStatusWindow;
import org.eclipse.petrinets.gui.TextType;
import org.graphstream.algorithm.Dijkstra;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.Path;

enum CommunicationDirection {
	Send, Receive, SendReceive, None
}

public class OPNValidator implements IStreamingGraphValidators {

	List<String> listOfInitialNodes = new ArrayList<String>();
	List<String> listOfInterfaceNodes = new ArrayList<String>();
	List<String> listOfPlaces = new ArrayList<String>();
	List<String> listOfTransitions = new ArrayList<String>();
	Map<String, ArrayList<String>> mapModuleIdToNodeIds = new HashMap<String, ArrayList<String>>();
	Map<String, String> mapOfPlaceIDToName = new HashMap<String, String>();
	Map<String, String> mapOfTransitionIDToName = new HashMap<String, String>();
	
	SimulationStatusWindow sWindow;
	String outcome = new String();
	
	// Local DS to store if a transition is send or receive
	Map<String, CommunicationDirection> mapOfTransitionDirection = new HashMap<String, CommunicationDirection>();
	// Local DS to store results of analyzing each module
	Map<String, Boolean> mapOfModuleOPNOutcome = new HashMap<String, Boolean>();
	Map<String, Boolean> mapOfModuleChoiceOutcome = new HashMap<String, Boolean>();
	Map<String, Boolean> mapOfModuleLegOutcome = new HashMap<String, Boolean>();

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

	private String getNodeName(String id) {
		if(mapOfPlaceIDToName.containsKey(id))
			return mapOfPlaceIDToName.get(id);
		else if(mapOfTransitionIDToName.containsKey(id))
			return mapOfTransitionIDToName.get(id);
		else return id;
	}

	private boolean doPathsFromPlaceSatisfyLegProperty(String mid, Graph graph) {
		boolean isCheckOK = true;
		
		/*if(listOfInterfaceNodes.contains(place_node.getId())) {
			return isCheckOK; // in fact does not really apply to these places
		}
		else {*/
			
			// 1. listOfJoinPlaces: get all (internal) place nodes for a given module that are a join, i.e. preset is greater than 1
			// 2. listOfSplitPlaces: get all (internal) place nodes for a given module that are a split, i.e. postset is greater than 1
			ArrayList<String> listOfJoinPlaces = new ArrayList<String>();
			ArrayList<String> listOfSplitPlaces = new ArrayList<String>();
			
			for(String nid : mapModuleIdToNodeIds.get(mid)) {
				if(!listOfTransitions.contains(nid)) {
					if(!listOfInterfaceNodes.contains(nid)) {
						if(graph.getNode(nid).getOutDegree() > 1)
							listOfSplitPlaces.add(nid);
						if(graph.getNode(nid).getInDegree() > 1)
							listOfJoinPlaces.add(nid);
					}
				}
			}			
			
			// 3. for each split place check if a join place is reachable using Dijkstra
			Dijkstra dijkstra = new Dijkstra(); //Dijkstra.Element.EDGE, null, "length");

			// Compute the shortest paths in g from A to all nodes
			if(listOfSplitPlaces.size() > 0 && listOfJoinPlaces.size() > 0) {
				dijkstra.init(graph);
				
				for(String split_place : listOfSplitPlaces) {
					dijkstra.setSource(graph.getNode(split_place));
					dijkstra.compute();
					// Now which ever path has length != Double.POSITIVE_INFINITY
					for(String join_place : listOfJoinPlaces) {
							if(dijkstra.getPathLength(graph.getNode(join_place)) != Double.POSITIVE_INFINITY) {
							//System.out.println("Module: " + mid + " Split: " + getNodeName(split_place) + " Join: " + getNodeName(join_place));
							//System.out.printf("%s->%s:%10.2f%n",dijkstra.getSource(), join_place,
									//dijkstra.getPathLength(graph.getNode(join_place)));
							
							// special case is when split place and join place are the same.
							// go one node ahead: for each transition in postset of split place - find all path to join place. 
							if(split_place.equals(join_place)) {
								for(Edge e : graph.getNode(join_place).getEnteringEdgeSet()) {
									Node transition = e.getSourceNode();
									Iterator<Path> pathIterator = dijkstra.getAllPathsIterator(transition);
									if(checkForCommunicationDirection(pathIterator, mid)) {
										// outcome += "\n <!> Leg Property is satisfied in Module: " + mid + "\n";
									} else isCheckOK = false;
								}
							}
							else {
								Iterator<Path> pathIterator = dijkstra.getAllPathsIterator(graph.getNode(join_place));
								while (pathIterator.hasNext()) {
									//System.out.println("[C] " + pathIterator.next());
									if(checkForCommunicationDirection(pathIterator, mid)) {
										// Leg property is satisfied
									} else  isCheckOK = false;
								}
							}
						}
					} 	
				}		
			}
		
		return isCheckOK;
	}
	
	private boolean checkForCommunicationDirection(Iterator<Path> pathIterator, String mid) {
		while (pathIterator.hasNext()) {
			Path path = pathIterator.next();
			// System.out.println("[E] " + path);
			/*for(Node node : path.getEachNode()) {
				System.out.println("[Node] " + node.getId());
			}*/
			
			boolean isSendFound = false;
			boolean isRecvFound = false;
			ArrayList<String> path_str = new ArrayList<String>();

			for(Node node : path.getEachNode()) {
				path_str.add(getNodeName(node.getId()));
				// check for transitions with different directions
				if(mapOfTransitionIDToName.containsKey(node.getId())) {
					if(getTransitionDirection(node).equals(CommunicationDirection.Send)) isSendFound = true;
					if(getTransitionDirection(node).equals(CommunicationDirection.Receive)) isRecvFound = true;
				}
			}
			
			if(isSendFound && isRecvFound) {
				return true;
				// leg property is satisfied!
				// outcome += "\n <!> Leg Property is satisfied in Module: " + mid + "\n";
			}
			else {
				// outcome += "\n <!> Leg Property Violation in Module: " + mid + "\n";
				addErrorTextToStatusWindow("\n <!> Leg Property Violation in Module: " + mid + "\n");
				// outcome += "   + Violating Path: "+ path_str + "\n";
				addErrorTextToStatusWindow("   + Violating Path: "+ path_str + "\n");
				return false;
			}
		}
		return false;
	}

	private boolean doesTransitionHaveMoreThanOneInterfacePlace(String mid, Node nid) {
		// String outcome = new String();
		boolean isCheckOK = true;
		
		ArrayList<String> inputPlaces = new ArrayList<String>();
		ArrayList<String> outputPlaces = new ArrayList<String>();
		
		for(String intfp : listOfInterfaceNodes) { 
			if(nid.hasEdgeToward(intfp)) {
				outputPlaces.add(intfp);
			}
			if(nid.hasEdgeFrom(intfp)) {
				inputPlaces.add(intfp);
			}
		}
		
		// Debug Statements
		/*outcome += "\n <!> In component: "+mid + " transition " + getNodeName(nid.getId());
		for(String ip : inputPlaces) {
			outcome += "\n input place: " + getNodeName(ip);
		}
		for(String ip : outputPlaces) {
			outcome += "\n output place: " + getNodeName(ip);
		}*/		
		
		if(outputPlaces.size() > 1) {
			outcome = "\n <!> In component: "+mid +  "\n" +
					  "   + transition "+ getNodeName(nid.getId()) + " has multiple output channels\n";
			addErrorTextToStatusWindow(outcome);
			isCheckOK = false;
		}
		if(inputPlaces.size() > 1) {
			outcome = "\n <!> In component: "+ mid + "\n" +
					  "   + transition "+ getNodeName(nid.getId()) + " has multiple input channels\n";
			addErrorTextToStatusWindow(outcome);
			isCheckOK = false;
		}
		if(outputPlaces.size() > 0 && inputPlaces.size() > 0) {
			outcome = "\n <!> In component: "+mid +  "\n" +
					  "   + transition "+ getNodeName(nid.getId()) + " has more than one direction\n";
			addErrorTextToStatusWindow(outcome);
			isCheckOK = false;
		}
		
		if(inputPlaces.size() == 1 && outputPlaces.size() < 1)
			mapOfTransitionDirection.put(nid.getId(), CommunicationDirection.Receive);
		if(outputPlaces.size() == 1 && inputPlaces.size() < 1)
			mapOfTransitionDirection.put(nid.getId(), CommunicationDirection.Send);
		if(outputPlaces.size() > 0 && inputPlaces.size() > 0)
			mapOfTransitionDirection.put(nid.getId(), CommunicationDirection.SendReceive);
		
		return isCheckOK;
	}

	private boolean isCommunicatingTransition(Node transition_node) {
		//System.out.println("Checking Comm Tr: " + getNodeName(transition_node.getId()));
		for(String commTr : mapOfTransitionDirection.keySet()) {
			//System.out.println("Key:  " + getNodeName(commTr));
			if(transition_node.getId().equals(commTr)) {
				//mapOfTransitionDirection.get(commTr);
				return true;
			}
		}
		return false;
	}
	
	private CommunicationDirection getTransitionDirection(Node transition_node) {
		for(String commTr : mapOfTransitionDirection.keySet()) {
			if(transition_node.getId().equals(commTr)) {
				if(mapOfTransitionDirection.get(transition_node.getId()).equals(CommunicationDirection.Receive)) 
					return CommunicationDirection.Receive;
				if(mapOfTransitionDirection.get(transition_node.getId()).equals(CommunicationDirection.Send)) 
					return CommunicationDirection.Send;
				if(mapOfTransitionDirection.get(transition_node.getId()).equals(CommunicationDirection.SendReceive)) 
					return CommunicationDirection.SendReceive;
			}
		}
		return CommunicationDirection.None;	
	}
	
	// CHECK FOR CHOICE PROPERTY
	private boolean doesPlaceSatisfyChoiceProperty(String mid, Node place_node) {
		//String outcome = new String();
		boolean isCheckOK = true;
		
		ArrayList<String> postTransitions = new ArrayList<String>();
		int numSendTransitions = 0;
		int numReceiveTransitions = 0;
		
		// we only want to check internal places, not interface places!
		if(listOfInterfaceNodes.contains(place_node.getId())) {
			return isCheckOK; // in fact does not really apply to these places
		}
		else {
			// outcome += "\n checking internal place: "+ getNodeName(place_node.getId()) + " in component: "+ mid;
			for(Edge arc : place_node.getEachLeavingEdge()) {
				postTransitions.add(arc.getTargetNode().getId()); // debug DS
				if(isCommunicatingTransition(arc.getTargetNode())) {
					 //outcome += " \n is comm tr: " + getNodeName(arc.getTargetNode().getId());
					if(getTransitionDirection(arc.getTargetNode()).equals(CommunicationDirection.Send)) {
						numSendTransitions++;
					}
					else if(getTransitionDirection(arc.getTargetNode()).equals(CommunicationDirection.Receive)) {
						numReceiveTransitions++;
					}
					else { // we dont care! send-receive is an error anyways! 
					}
				} //else outcome += " \n is NOT comm tr: " + getNodeName(arc.getTargetNode().getId()); 
				// TODO should we report this, otherwise it comes true in non-OPN, i.e. opn with no interface places
				else {
					outcome = "\n <!> In component: " + mid + " transition: " + getNodeName(arc.getTargetNode().getId()) + " is not communicating \n";
					addErrorTextToStatusWindow(outcome);
					isCheckOK = false;
				}
			}
		}
		
		// outcome += "\n <!> In component: "+ mid + " place " + getNodeName(place_node.getId()) + " has post-transitions \n";
		// for(String tr : postTransitions) {
			//outcome += getNodeName(tr) +"\n";
		// }
		
		// does postset transitions of place_node have a direction send or receive	
		if(numReceiveTransitions > 0 && numSendTransitions > 0) {
			// violation of choice property
			outcome = "\n <!> In component: "+ mid + "\n   + place: " + getNodeName(place_node.getId()) + " violates choice property \n" ;
			addErrorTextToStatusWindow(outcome);
			isCheckOK = false;
		}

		return isCheckOK;
	}
	
	@Override
	// assumption that sWindow has been set. will not check for null.
	public String checkProperty(Graph graph, ValidationType vtype) {
		//String outcome = new String();
		if(!vtype.equals(ValidationType.OPN)) {
			// return " <!> Fatal: Property Check of Type: " + vtype.toString() + " is not supported \n";
			sWindow.addText(" <!> Fatal: Property Check of Type: " + vtype.toString() + " is not supported \n", TextType.ERROR);
			return " <!> Fatal: Property Check of Type: " + vtype.toString() + " is not supported \n";
		}
		
		// 1. OPN Property is that a transition is not both a send and receive or multiple send - receive
		//String outcome = new String();
		for(String mid : mapModuleIdToNodeIds.keySet()) {
			for(String nid : mapModuleIdToNodeIds.get(mid)) {
				if(listOfTransitions.contains(nid)) {
					if(doesTransitionHaveMoreThanOneInterfacePlace(mid, graph.getNode(nid))) {
						if(!mapOfModuleOPNOutcome.containsKey(mid)) // ignore if outcome is present, update only negative outcomes thereon
							mapOfModuleOPNOutcome.put(mid, true);
					}
					else mapOfModuleOPNOutcome.put(mid, false);
					// is any transition in this module not communicating, report not an OPN
					if(!isCommunicatingTransition(graph.getNode(nid))) {
						mapOfModuleOPNOutcome.put(mid, false);
					}
				}
			}
		}

		
		// 2. Choice Property Check
		for(String mid : mapModuleIdToNodeIds.keySet()) {
			for(String nid : mapModuleIdToNodeIds.get(mid)) {
				if(!listOfTransitions.contains(nid)) {
					// we are assuming nid is a place! This is ok for now..
					// Check Portnet Property: CHoice and Leg Property
					// requires a map prepared by first check
					if(doesPlaceSatisfyChoiceProperty(mid, graph.getNode(nid))) {
						if(!mapOfModuleChoiceOutcome.containsKey(mid)) 
							mapOfModuleChoiceOutcome.put(mid, true);
					} else mapOfModuleChoiceOutcome.put(mid, false);
					
				}
			}
		}

		// 3. leg property Check
		for(String mid : mapModuleIdToNodeIds.keySet()) {
			if(doPathsFromPlaceSatisfyLegProperty(mid, graph)) {
				if(!mapOfModuleLegOutcome.containsKey(mid)) 
					mapOfModuleLegOutcome.put(mid, true);
			} else mapOfModuleLegOutcome.put(mid, false);
		}
		
		/*System.out.println(mapOfModuleOPNOutcome);
		System.out.println(mapOfModuleChoiceOutcome);
		System.out.println(mapOfModuleLegOutcome);*/
		
		if(mapOfModuleOPNOutcome.keySet().size() > 0)
			// outcome += "\n\n [ Summary ] \n";
			addHeaderTextToStatusWindow("\n\n [ Summary ] \n");
		for(String mid : mapOfModuleOPNOutcome.keySet()) {
			// outcome += "\n <+> Module: " + mid + "\n";
			addInfoTextToStatusWindow("\n <+> Module: " + mid + "\n");
			if(mapOfModuleOPNOutcome.get(mid))
				// outcome += "\n   + is a OPN: OK" + "\n";
				addDetInfoTextToStatusWindow("\n   + is a OPN: OK" + "\n");
			else
				// outcome += "\n   + is a OPN: NOK" + "\n";
				addErrorTextToStatusWindow("\n   + is a OPN: NOK" + "\n");
			
			if(mapOfModuleChoiceOutcome.get(mid))
				// outcome += "\n   + satisfies choice property: OK" + "\n";
				addDetInfoTextToStatusWindow("\n   + satisfies choice property: OK" + "\n");
			else
				// outcome += "\n   + satisfies choice property: NOK" + "\n";
				addErrorTextToStatusWindow("\n   + satisfies choice property: NOK" + "\n");
			
			if(mapOfModuleLegOutcome.get(mid))
				// outcome += "\n   + satisfies leg property: OK" + "\n";
				addDetInfoTextToStatusWindow("\n   + satisfies leg property: OK" + "\n");
			else
				// outcome += "\n   + satisfies leg property: NOK" + "\n";
				addErrorTextToStatusWindow("\n   + satisfies leg property: NOK" + "\n");
		}
		/*for(String mid : mapOfModuleChoiceOutcome.keySet()) {
			if(mapOfModuleChoiceOutcome.get(mid))
				outcome += "\n <+> The net in module: " + mid + " satisfies choice property: OK" + "\n";
			else
				outcome += "\n <!> The net in module: " + mid + " satisfies choice property: NOK" + "\n";
		}
		for(String mid : mapOfModuleLegOutcome.keySet()) {
			if(mapOfModuleLegOutcome.get(mid))
				outcome += "\n <+> The net in module: " + mid + " satisfies leg property: OK" + "\n";
			else
				outcome += "\n <!> The net in module: " + mid + " satisfies leg property: NOK" + "\n";
		}*/
		
		return outcome;
	}

	private void addHeaderTextToStatusWindow(String info) { sWindow.addText(info, TextType.HEADER); }
	private void addInfoTextToStatusWindow(String info) { sWindow.addText(info, TextType.INFO);}
	private void addDetInfoTextToStatusWindow(String info) { sWindow.addText(info, TextType.DETAILED_INFO);}
	private void addErrorTextToStatusWindow(String info) { sWindow.addText(info, TextType.ERROR);}
	private void addDetailedErrorTextToStatusWindow(String info) { sWindow.addText(info, TextType.DETAILED_ERROR);}
	
	@Override
	public void setStatusWindowInstance(SimulationStatusWindow sWindow) {
		this.sWindow = sWindow;
	}

}
