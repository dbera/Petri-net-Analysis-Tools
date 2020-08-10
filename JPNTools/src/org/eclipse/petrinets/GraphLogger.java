package org.eclipse.petrinets;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

public class GraphLogger 
{	
	private static GraphLogger graphLoggerInst;
	private static PrintWriter writer;

	private static ArrayList<String> brScenarioLogs;
	private static ArrayList<String> brScenarioStepLogs;
	
	private static ArrayList<String> visitedNodes;
	private static int edgeIdx;

	private static int stepNo;
	private static int brScnIdx;
	private static boolean brScnGen;

	// master boolean to start-stop logging
	private static boolean stopLogging;

	private static final String initStreamGraphTxt = "DGS004\r\n" + "\"reachabilitygraph.dgs\" 0 0";

	private GraphLogger() {}

	public static GraphLogger getInstance() {
		if (graphLoggerInst == null) {
			graphLoggerInst = new GraphLogger();
			stepNo = 0;
			brScnIdx = 0;
			brScnGen = false;
			stopLogging = false;
			brScenarioLogs = new ArrayList<String>();
			brScenarioStepLogs = new ArrayList<String>();
			visitedNodes = new ArrayList<String>();
			edgeIdx = 0;
		}
		return graphLoggerInst;
	}

	public void resetLogs() {
		stepNo = 0;
		brScnIdx = 0;
		brScnGen = false;
		stopLogging = false;
		brScenarioLogs = new ArrayList<String>();
		brScenarioStepLogs = new ArrayList<String>();
		visitedNodes = new ArrayList<String>();
		edgeIdx = 0;
	}

	// invariant: stopLogging = false; TODO Throw exception
	public void incrementStepCount() { 
		stepNo++;
	}

	// Called directly by graph walker when it has all the information about a Node with outgoing edges!
	public void addToBrLogs(String elm) { brScenarioLogs.add(elm); }
	
	public void setBrScnIdx(int idx) { brScnIdx = idx; }
	public void setBrScnGen(boolean value) { brScnGen = value; }
	
	// life time of brScenarioStepLogs is in scope of for-loop in walker - enb. transitions of a marking
	public void resetBrStepLog() { brScenarioStepLogs = new ArrayList<String>(); }
	
	// all information about firing a transition is present here!
	public void addToBrStepLog(String elm) { brScenarioStepLogs.add(elm); }
	/*public String generateTxtFromBrStepLogs() { 
		String txt = new String();
		txt += "\t[\n";
		for(String elm : brScenarioStepLogs) {
			txt += "\t" + elm + "\n";
		}
		txt += "\t" + " nodeID: N"+brScnIdx+"]; \n";
		return txt;
	}*/

	// we need the above function but also with information about the source node
	// The walker takes information and puts it in brScenariologs after all transitions of marking have been visited, i.e. after for loop is done
	public String generateTxtFromBrStepLogs(String sourceNodeID) { 
		String txt = new String();

		// each element of brScenarioStepLogs is an outgoing edge from the sourceNode
		// an A label:"Node A"
		String source_node_label = sourceNodeID; //"Node " + 
		if(!visitedNodes.contains(source_node_label)) {
			//txt += "an " + source_node_label + " label: \"" + source_node_label + "\"" + " \n";
			//visitedNodes.add("an " + source_node_label + " label: \"" + source_node_label + "\"" + " \n");
			visitedNodes.add(source_node_label);
		}
		String dst_node_label = "N" + brScnIdx;
		if(!visitedNodes.contains(dst_node_label)) {
			//txt += "an " + dst_node_label + " label: \"" + dst_node_label + "\"" + " \n";
			//visitedNodes.add("an " + dst_node_label + " label: \"" + dst_node_label + "\"" + " \n");
			visitedNodes.add(dst_node_label);
		}
		// ae AB A > B label:"AB"
		String edge_label = brScenarioStepLogs.get(0); //strong assumption that a transition was executed before calling this function
		//"N" + sourceNodeID + "_N" + brScnIdx + " \n ";
		txt += "ae " + "e"+edgeIdx + " " + source_node_label + " > " + dst_node_label + " label: \"" + edge_label + "\"" + "\n";
		edgeIdx++;

		/*txt += "\t[\n";
		for(String elm : brScenarioStepLogs) {
			txt += "\t" + elm + "\n";
		}
		txt += "\t" + " nodeID: N"+brScnIdx+"]; \n";*/

		return txt;
	}

	// Called by walker
	public String generateTxtForClosureTransitions(String sourceNodeID, String edge_label, String dstNodeID) { 
		String txt = new String();
		String source_node_label = "" + sourceNodeID;
		if(!visitedNodes.contains(source_node_label)) {
			//txt += "an " + source_node_label + " label: \"" + source_node_label + "\"" + " \n";
			//visitedNodes.add("an " + source_node_label + " label: \"" + source_node_label + "\"" + " \n");
			visitedNodes.add(source_node_label);
		}
		String dst_node_label = "" + dstNodeID;
		if(!visitedNodes.contains(dst_node_label)) {
			//txt += "an " + dst_node_label + " label: \"" + dst_node_label + "\"" + " \n";
			//visitedNodes.add("an " + dst_node_label + " label: \"" + dst_node_label + "\"" + " \n");
			visitedNodes.add(dst_node_label);
		}
		txt += "ae " + "e"+edgeIdx + " " + source_node_label + " > " + dst_node_label + " label: \"" + edge_label + "\"" + "\n";
		edgeIdx++;
		return txt;
	}
	
	public String generateEdgeLabel(String eventType, String eventName) 
	{
		return eventName;
		//String trace_tag = eventName;
		//trace_tag += "tr: "+ eventName + " \n ";
		//return trace_tag;
	}

	public void addToLog(String eventType, String eventName, String machineID) 
	{
		if(!stopLogging) {
			if(brScnGen) addToBrStepLog(generateEdgeLabel(eventType, eventName));
		}
	}

	public void writeToBrScnFormat()
	{
		// File file = new File("reachabilitygraph.dgs");
		// file.getParentFile().mkdirs();
		try { writer = new PrintWriter("reachabilitygraph"+ ".dgs", "UTF-8"); } 
		catch (FileNotFoundException | UnsupportedEncodingException e) { e.printStackTrace(); }
		writer.println(initStreamGraphTxt + "\n");
		
		for(String elm : visitedNodes) writer.println("an " + elm + " label: \"" + elm + "\"" + " \n");
		for(String elm : brScenarioLogs) writer.println(elm + "\n");

		writer.close();
		resetLogs();
	}

}
