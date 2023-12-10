package org.eclipse.petrinets.graphs.validators;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.petrinets.ValidationType;
import org.eclipse.petrinets.graphs.IStreamingGraphValidators;
import org.eclipse.petrinets.gui.SimulationStatusWindow;
import org.eclipse.petrinets.gui.TextType;
import org.graphstream.algorithm.Dijkstra;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

public class WTValidator implements IStreamingGraphValidators {

	SimulationStatusWindow sWindow;
	
	// To Keep Track of duplicate status messages -> especially for boundedness
	ArrayList<String> listOfInfoTextMsgs = new ArrayList<String>();
	
	@Override
	public void setStatusWindowInstance(SimulationStatusWindow sWindow) {
		this.sWindow = sWindow;
	}
	
	@Override
	public void setDataStructures(Map<String, String> mapOfPlaceIDToName, Map<String, String> mapOfTransitionIDToName,
			List<String> listOfInitialNodes, List<String> listOfInterfaceNodes, List<String> listOfPlaces,
			List<String> listOfTransitions, Map<String, ArrayList<String>> mapModuleIdToNodeIds) {
			// This is purely for Petri nets. This check is for Reachability Graphs. Split into RG and PN Validators!
		
	}

	private int checkBoundedness(Graph graph) {
		int maxNumTokens = 0;
		
    	// Parsing the node attr. ui.name (marking info) to deduce number of tokens for safe net checking
    	for(Node node : graph.getNodeSet()) {
			if(node.hasAttribute("ui.name")) {
	    		String nodeLabel = node.getAttribute("ui.name");
		    	String[] lines = nodeLabel.split("\\r?\\n");
		    	for(String line : lines) {
		    		//System.out.println(line);
		    		String[] onTokens = line.split("has Tokens: ");
		    		String[] onPlace = onTokens[0].split("Place: ");
		    		try {
		    			// System.out.println("parsed place: " + onPlace[onPlace.length-1] + " Tok: " + Integer.parseInt(onTokens[onTokens.length-1]));
		    			// if number of tokens is greater than one then it is not a safe net
		    			if(Integer.parseInt(onTokens[onTokens.length-1]) > 1)
		    				addErrorTextToStatusWindow("\n  <!> Place: " + onPlace[onPlace.length-1] + " has " + Integer.parseInt(onTokens[onTokens.length-1]) + " tokens.");
		    			if(maxNumTokens < Integer.parseInt(onTokens[onTokens.length-1])) 
		    				maxNumTokens = Integer.parseInt(onTokens[onTokens.length-1]);
		    		}
		    		catch (NumberFormatException e) { }
		    		// for(String l : lineSplit) System.out.println(" elm: " + l);
		    	}
			}
    	}
		return maxNumTokens;
	}
	
	@Override
	public String checkProperty(Graph graph, ValidationType vtype) {
		String outcome = new String();
		boolean isWT = true;
		
		if(!vtype.equals(ValidationType.WT)) {
			addErrorTextToStatusWindow("<!> Fatal: Property Check of Type: " + vtype.toString() + " is not supported \n");
			return "<!> Fatal: Property Check of Type: " + vtype.toString() + " is not supported \n";
		}
		
		// do the weak termination magic!
		Dijkstra dijkstra_1 = new Dijkstra();
		Dijkstra dijkstra_2 = new Dijkstra();
		dijkstra_1.init(graph);
		dijkstra_2.init(graph);
		
		dijkstra_1.setSource(graph.getNode("N0"));
    	dijkstra_1.compute();
    	
    	for(Node curr_node: graph.getNodeSet()) {
	    	if(dijkstra_1.getPathLength(curr_node) != Double.POSITIVE_INFINITY) {
	    		dijkstra_2.setSource(curr_node);
	    		dijkstra_2.compute();
	    		if(dijkstra_2.getPathLength(graph.getNode("N0")) != Double.POSITIVE_INFINITY) {
	    			
	    		}
	    		else {
	    			// initial is not reachable from curr_node
	    			outcome = "\n   + Initial Node: N0 is not reachable from node: " + curr_node.getId();
	    			addErrorTextToStatusWindow(outcome);
	    			isWT = false;
	    		}
	    	}
	    	else {
	    		// curr_node not reachable from initial
	    		outcome = "\n   + Node: " + curr_node.getId() + "is not reachable from Initial Node: N0";
	    		addErrorTextToStatusWindow(outcome);
	    		isWT = false;
	    	}
    	}
    	
       	// Now check boundedness
    	addHeaderTextToStatusWindow("\n\n > Checking for Boundedness.. ");
    	int kBound = checkBoundedness(graph);
    	
    	addHeaderTextToStatusWindow("\n\n [ Summary ] \n");
    	
    	if(isWT) { outcome = "\n <+> Weak Termination: OK"; addInfoTextToStatusWindow(outcome); }
    	else { outcome = "\n <!> Weak Termination: NOK"; addErrorTextToStatusWindow(outcome); }

    	if(kBound > 1) {
    		addErrorTextToStatusWindow("\n <!> The net is not safe! ");
    		addErrorTextToStatusWindow("k-Bound: " + kBound);
    	} else {
    		addInfoTextToStatusWindow("\n <+> The net is safe!");
    	} 
    	
		return outcome;
	}

	private void addHeaderTextToStatusWindow(String info) { 
			sWindow.addText(info, TextType.HEADER);
	}
	private void addInfoTextToStatusWindow(String info) { 
				sWindow.addText(info, TextType.INFO);
	}
	private void addDetInfoTextToStatusWindow(String info) { sWindow.addText(info, TextType.DETAILED_INFO);}
	private void addErrorTextToStatusWindow(String info) { 
		if(!listOfInfoTextMsgs.contains(info)) {
			listOfInfoTextMsgs.add(info); 
			sWindow.addText(info, TextType.ERROR);
		}
	}
	private void addDetailedErrorTextToStatusWindow(String info) { sWindow.addText(info, TextType.DETAILED_ERROR);}
}
