package org.eclipse.petrinets.gui;

import java.util.List;

import javax.swing.JFrame;

import org.eclipse.petrinets.ArcPair;
import org.eclipse.petrinets.GraphLogger;
import org.eclipse.petrinets.IGraphGenerator;
import org.eclipse.petrinets.PNMLParser;
import org.eclipse.petrinets.Walker;
import org.eclipse.petrinets.graphs.StreamingGraphGenerator;

// Instantiates streaming graph and GUI Panels
public class PNGUI extends JFrame 
{
	private static final long serialVersionUID = 1L;
	
	private JFrame jfrmConsole;
	private PNMLParser parserInst; // contains the data structures after parsing PNML file
	private IGraphGenerator graphGen; // Launches Streaming Graph UI + Graph Operations + graph is constructed from this class
	private SimulationStatusWindow sWindow; // Text Window and Buttons for Outcome of Structural Analysis -> Calls graphGen for checks!
	
	private boolean ignoreInterfacePlaces;
	private String FILE_PATH;
	
	public PNGUI(PNMLParser parserInst, String FILE_PATH, boolean isDisplayGraph, boolean _ignoreInterfacePlaces) {
		this.parserInst = parserInst;
		this.FILE_PATH = FILE_PATH;
		ignoreInterfacePlaces = _ignoreInterfacePlaces;
		jfrmConsole = new JFrame("Structural Analysis");
        jfrmConsole.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //jfrmConsole.setLocationRelativeTo(null);
        jfrmConsole.setResizable(true);
        // we are not loading streaming graph from file location! we will construct it.
        graphGen = new StreamingGraphGenerator("Yasper-Petri-Net", isDisplayGraph, "", 
        		parserInst.getMapOfPlaceIDToName(), parserInst.getMapOfTransitionIDToName());
        sWindow = new SimulationStatusWindow(graphGen, this);
        graphGen.setStatusWindow(sWindow);
        jfrmConsole.getContentPane().add(sWindow);
        jfrmConsole.pack();
        jfrmConsole.setAlwaysOnTop(true);
        jfrmConsole.setVisible(true);
	}
	
	public void reloadPetriNetFromPNMLFile() {
		//parserInst.reloadPetriNetModel();
		//parserInst.generatePetriNetSyntax();
		parserInst = new PNMLParser(FILE_PATH);
		parserInst.displayParsedContent();
		parserInst.generatePetriNetSyntax();
	}

	public void loadPetriNetFromPNMLFile(String path) {
		//parserInst.reloadPetriNetModel();
		//parserInst.generatePetriNetSyntax();
		parserInst = new PNMLParser(path);
		parserInst.displayParsedContent();
		parserInst.generatePetriNetSyntax();
	}
	
	public void resetStreamingGraph() {
		graphGen.resetGraph();
	}
	
	// This is Streaming Graph Construction. 
	// Note Petri net simulator instance has already been created by PNMLParser -> generatePetriNetSyntax
	public void computePetriNetGraph() {
		// Add places
		List<String> listOfPlaces = parserInst.getListOfPlaces();
		// add to node in graph the attributes place-type and name
		for(String placeID : listOfPlaces) {
			if(parserInst.getMapOfPlaceIDToName().containsKey(placeID)) // check to not add reference places because it is filterd already in this map!
				graphGen.addNode(parserInst.getMapOfPlaceIDToName().get(placeID), placeID, "place", parserInst.getMapOfPlacesToNet().get(placeID));
		}
		
		List<String> listOfInitialPlaces = parserInst.getListOfInitialPlaces();
		for(String placeID : listOfInitialPlaces) {
			if(parserInst.getMapOfPlaceIDToName().containsKey(placeID)) // check to not add reference places!
				graphGen.markNodeAsInitial(parserInst.getMapOfPlaceIDToName().get(placeID), placeID, "place");
		}
		
		// if a place in keys of mapofplacestonet is present in value of map of referenceplaces then it is an interface place!
		for(String placeID : parserInst.getListOfReferencePlaces()) {
			graphGen.markNodeAsInterfacePlace(parserInst.getMapOfPlaceIDToName().get(placeID), placeID, "place");
		}
		
		List<String> listOfTransitions = parserInst.getListOfTransitions();
		// add to node in graph the attributes transition-type and name
		for(String trID : listOfTransitions) {
			graphGen.addNode(parserInst.getMapOfTransitionIDToName().get(trID), trID, "transition", parserInst.getMapOfTransitionsToNet().get(trID));
		}
		
		// Add edges
		List<ArcPair> listOfArcs = parserInst.getListOfArcs();
		for(ArcPair e : listOfArcs)
			graphGen.addEdge("", e.getId(), e.getFirst(), e.getSecond(), "NO_CONTEXT");
		
		// Now we will delete interface places if the flag is set.
		if(ignoreInterfacePlaces) removeInterfacePlaces();
		
		// re-compute the data structures corresponding the graph from the graph itself
		graphGen.computeDS();
	}
	
	public void removeInterfacePlaces() {
		for(String intfP : parserInst.getListOfReferencePlaces()) {
			graphGen.removeNode(intfP);
		}		
	}
	
	public void generateReachabilityGraph(int depth) {
		GraphLogger.getInstance().setBrScnGen(true);
		//GraphLogger.getInstance().addToBrLogs("\nRoot Node: N0\n");
		// TODO pass depth of search as parameter!		
		(new Walker(parserInst.getNetObject())).generateRG(parserInst.getNetObject().getCurrentState(), 0, depth);
		GraphLogger.getInstance().writeToBrScnFormat();
		GraphLogger.getInstance().setBrScnGen(false);
	}
}