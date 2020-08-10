package org.eclipse.petrinets;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.petrinets.graphs.StreamingGraphGenerator;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.MultiGraph;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ParserMain {
	
	public static void main(String[] args) 
	{
		// TODO get from command line arguments!
		//String FILE_PATH = "C:\\Users\\berad\\Desktop\\JavaWorkspace2020\\wrkspace\\SimpleGraph\\src\\nl\\esi\\petrinets\\yasprFile.pnml";
		String FILE_PATH = "C:\\Users\\berad\\Desktop\\JavaWorkspace2020\\JDK14WorkSpace\\JPNTools\\src\\org\\eclipse\\petrinets\\yasprFile.pnml";
		
		PNMLParser netParser = new PNMLParser(FILE_PATH);
		// Compute locally the data structures representing Yasper Petri net
		netParser.displayParsedContent();
		netParser.generatePetriNetSyntax();

		// Display Structural Analysis Results GUI + Visualization using Graph Stream
		ResultsGUI gui = new ResultsGUI(netParser, true, false); // launches visualization
		gui.computePetriNetGraph(); // constructs the graph.
		
		// gui.analyzePetriNetStructure(); // This should be called on demand by the buttons

		// get the net object
		// INet netObj = netParser.getNetObject();
		
		GraphLogger.getInstance().setBrScnGen(true);
		//GraphLogger.getInstance().addToBrLogs("\nRoot Node: N0\n");
		(new Walker(netParser.getNetObject())).generateRG(netParser.getNetObject().getCurrentState(), 0);
		GraphLogger.getInstance().writeToBrScnFormat();
		GraphLogger.getInstance().setBrScnGen(false);
				
		/*        MANUAL USER SIMULATION         */
		/*int choice = 0;
		Scanner in = new Scanner(System.in);
		while(choice >= 0) {
			try {
				for(String elm : netParser.n.getCurrentStateName())
					System.out.println("Marked-Place: "+ netParser.getPlaceName(elm));
			
				System.out.println("Enabled Transitions:");
			
				ArrayList<Transition> tlist = netParser.n.getEnabledTransitions();
				int idx = 0;
				for(Transition t : tlist) {
					System.out.println(idx + ":" + "Transition: "+ netParser.getTransitionName(t.name) + " in Net: "+ netParser.mapOfTransitionsToNet.get(t.name));
					idx++;
				}
				choice = in.nextInt();
				if(choice >= 0)
					netParser.n.fireEnabledTransition(tlist.get(choice));
			} catch(Exception e) { }
		}
		in.close();*/
	}
}

class PNMLParser 
{
	INet n;
	DocumentBuilderFactory factory;
	DocumentBuilder builder;
	Document document;
	Element root;
	String FILE_PATH;
	
	List<String> listOfPlaces = new ArrayList<String>();
	List<String> listOfInitialPlaces = new ArrayList<String>();
	List<String> listOfTransitions = new ArrayList<String>();
	List<ArcPair> listOfArcs = new ArrayList<ArcPair>();
	List<String> listOfReferencePlaces = new ArrayList<String>();

	Map<String, String> mapOfPlacesToNet = new HashMap<String, String>(); // pid -> container
	Map<String, String> mapOfTransitionsToNet = new HashMap<String, String>(); // tid -> container
	
	Map<String, String> mapOfReferencePlaces = new HashMap<String, String>(); // id:key -> ref:value; also places that are not reference places are key:id = value:id
	
	Map<String, String> mapOfPlaceIDToName = new HashMap<String, String>(); // pid -> pname
	Map<String, String> mapOfTransitionIDToName = new HashMap<String, String>(); // tid -> tname
	
	INet getNetObject() {
		return n;
	}
	
	String getPlaceName(String id) {
		return mapOfPlaceIDToName.get(id);
	}

	String getTransitionName(String id) {
		return mapOfTransitionIDToName.get(id);
	}
	
	void generatePetriNetSyntax() {
		for(String elm : listOfPlaces) generatePlaces(elm, mapOfPlacesToNet.get(elm));
		
		for(String elm : listOfInitialPlaces) {
			try { n.getPlace(elm).addToken(new IntegerToken(0)); } 
			catch (SimulatorException e) { e.printStackTrace(); }
			n.getPlace(elm).setInitial();
		}
		
		for(String elm : listOfTransitions) generateTransitions(elm, mapOfTransitionsToNet.get(elm));
		for(ArcPair elm : listOfArcs) {
			if(elm.isInputArc) generateInputArcs(elm.first, elm.second);
			else generateOutputArcs(elm.first, elm.second);
		}
	}
	
	PNMLParser(String pnmlFilePath) 
	{
		FILE_PATH = pnmlFilePath;
		factory = DocumentBuilderFactory.newInstance();
		try {
			builder = factory.newDocumentBuilder();
			document = builder.parse(new File(pnmlFilePath));
			document.getDocumentElement().normalize();
			root = document.getDocumentElement();
			// System.out.println("Root Node: " + root.getNodeName());
			// System.out.println("");
			parseAndGeneratePetriNet();
		} 
		catch(Exception e) { e.printStackTrace(); }
	}
	
	// Assumes FILE_PATH is set. Always used after creation.
	void reloadPetriNetModel() {
		factory = DocumentBuilderFactory.newInstance();
		try {
			builder = factory.newDocumentBuilder();
			document = builder.parse(new File(FILE_PATH));
			document.getDocumentElement().normalize();
			root = document.getDocumentElement();
			// System.out.println("Root Node: " + root.getNodeName());
			// System.out.println("");
			parseAndGeneratePetriNet();
		} 
		catch(Exception e) { e.printStackTrace(); }		
	}

	void parseAndGeneratePetriNet() {
		parsePlaces();
		parseTransitions();
		parseArcs();
		
		n =  new Net("YasperNet");
	}
	
	void parsePlaces() {
		NodeList nList = document.getElementsByTagName("place");
		for (int temp = 0; temp < nList.getLength(); temp++)
		{
			Node node = nList.item(temp);
			if (node.getNodeType() == Node.ELEMENT_NODE)
			{
				Element eElement = (Element) node;
				//System.out.println("Place ID: " +node.getAttributes().getNamedItem("id").getNodeValue());
				String pid = node.getAttributes().getNamedItem("id").getNodeValue();
				//System.out.println("Name : "  + eElement.getElementsByTagName("text").item(0).getTextContent());
				String pname = eElement.getElementsByTagName("text").item(0).getTextContent();
				listOfPlaces.add(pid);
				mapOfPlaceIDToName.put(pid, pname);
				// TODO handle initial tokens in reference places?
				if(eElement.getElementsByTagName("initialMarking").getLength() > 0) {
					String initial = eElement.getElementsByTagName("initialMarking").item(0).getTextContent();
					//System.out.println("Place: " + pid +" is INIT: "+initial);
					listOfInitialPlaces.add(pid);
				}

				Node pnode = node.getParentNode();
				if(node.getNodeType() == Node.ELEMENT_NODE) {
					Element pElement = (Element) pnode;
					//System.out.println("parent : "    + pElement.getElementsByTagName("text").item(0).getTextContent());
					String container = pElement.getElementsByTagName("text").item(0).getTextContent();
					mapOfPlacesToNet.put(pid, container);
					mapOfReferencePlaces.put(pid, pid);
				}
				//System.out.println("");
			}
		}
		// pre-condition: Function requires list of places to be populated!
		parseReferencePlaces();
	}

	void parseReferencePlaces() {
		NodeList nList = document.getElementsByTagName("referencePlace");
		for (int temp = 0; temp < nList.getLength(); temp++)
		{
			Node node = nList.item(temp);
			if (node.getNodeType() == Node.ELEMENT_NODE)
			{
				// Element eElement = (Element) node;
				// System.out.println("Reference Place ID: " +node.getAttributes().getNamedItem("id").getNodeValue());
				String _id = node.getAttributes().getNamedItem("id").getNodeValue();
				// System.out.println("Reference Place REF : "  + node.getAttributes().getNamedItem("ref").getNodeValue());
				String _ref = node.getAttributes().getNamedItem("ref").getNodeValue();
				listOfPlaces.add(_id);
				listOfReferencePlaces.add(_ref);
				mapOfReferencePlaces.put(_id, _ref);
				//mapOfPlaceIDToName.put(_id, mapOfPlaceIDToName.get(_ref));
			}
		}
	}
	
	void parseTransitions() {
		NodeList nList = document.getElementsByTagName("transition");
		for (int temp = 0; temp < nList.getLength(); temp++)
		{
			Node node = nList.item(temp);
			if (node.getNodeType() == Node.ELEMENT_NODE)
			{
				Element eElement = (Element) node;
				//System.out.println("Transition ID : "    + eElement.getAttribute("id"));
				String tid = eElement.getAttribute("id");
				//System.out.println("Name : "  + eElement.getElementsByTagName("text").item(0).getTextContent());
				String tname = eElement.getElementsByTagName("text").item(0).getTextContent();
				listOfTransitions.add(tid);
				mapOfTransitionIDToName.put(tid, tname);
				
				Node pnode = node.getParentNode();
				if(node.getNodeType() == Node.ELEMENT_NODE) {
					Element pElement = (Element) pnode;
					//System.out.println("parent : "    + pElement.getElementsByTagName("text").item(0).getTextContent());
					String container = pElement.getElementsByTagName("text").item(0).getTextContent();
					mapOfTransitionsToNet.put(tid, container);
					//System.out.println("container-type: " + pElement.getNodeName());
				}
				//System.out.println("");
			}
		}		
	}
	
	void parseArcs() {
		NodeList nList = document.getElementsByTagName("arc");
		for (int temp = 0; temp < nList.getLength(); temp++)
		{
			Node node = nList.item(temp);
			if (node.getNodeType() == Node.ELEMENT_NODE)
			{
				//Element eElement = (Element) node;
				//System.out.println("arc ID: " +node.getAttributes().getNamedItem("id").getNodeValue());
				//System.out.println("source: " +node.getAttributes().getNamedItem("source").getNodeValue());
				//System.out.println("target: " +node.getAttributes().getNamedItem("target").getNodeValue());
				//System.out.println("parent ID: " +node.getParentNode().getAttributes().getNamedItem("id").getNodeValue());
				String nid = node.getAttributes().getNamedItem("id").getNodeValue();
				String source = node.getAttributes().getNamedItem("source").getNodeValue();
				String target = node.getAttributes().getNamedItem("target").getNodeValue();

				Node pnode = node.getParentNode();
				if(node.getNodeType() == Node.ELEMENT_NODE) {
					Element pElement = (Element) pnode;
					//System.out.println("parent : "    + pElement.getElementsByTagName("text").item(0).getTextContent());
					String container = pElement.getElementsByTagName("text").item(0).getTextContent();
					if(listOfPlaces.contains(source)) listOfArcs.add(new ArcPair(nid, mapOfReferencePlaces.get(source), target, true, container));
					else listOfArcs.add(new ArcPair(nid, source, mapOfReferencePlaces.get(target), false, container));
				}
				//System.out.println("");
			}
		}
	}

	void displayParsedContent() 
	{
		System.out.println(" ========== List Of Places ==============");
		for(String elm : listOfPlaces) {
			System.out.println("Place: "+elm);
			if(listOfInitialPlaces.contains(elm)) 
				System.out.println(" Initial Token is Present! ");
		}
		System.out.println(" ========== List Of Transitions ==============");
		for(String elm : listOfTransitions)
			System.out.println("Transition: "+elm);

		System.out.println(" ========== List Of Arcs ==============");
		for(ArcPair elm : listOfArcs)
			System.out.println("Arc id: " + elm.id + " from "+ elm.first + " to " + elm.second );

		
		System.out.println(" ========== Map Of Places To Net ==============");
		for(String elm : mapOfPlacesToNet.keySet())
			System.out.println("ID: " + elm + " Name: " + mapOfPlacesToNet.get(elm));
		System.out.println(" ========== Map Of Transitions To Net ==============");
		for(String elm : mapOfTransitionsToNet.keySet())
			System.out.println("ID: " + elm + " Name: " + mapOfTransitionsToNet.get(elm));
		
		System.out.println(" ========== Map Of Reference Places ==============");
		for(String elm : mapOfReferencePlaces.keySet())
			System.out.println("ID: " + elm + " Name: " + mapOfReferencePlaces.get(elm));
		
		System.out.println(" ========== Map Of Place ID to Name ==============");
		for(String elm : mapOfPlaceIDToName.keySet())
			System.out.println("ID: " + elm + " Name: " + mapOfPlaceIDToName.get(elm));
		System.out.println(" ========== Map Of Transition ID to Name ==============");
		for(String elm : mapOfTransitionIDToName.keySet())
			System.out.println("ID: " + elm + " Name: " + mapOfTransitionIDToName.get(elm));
	}
	
	
	// Net Generation //
	void generatePlaces(String pname, String netName) {
		try { 
			n.addNewPlace(new Place(pname, Type.INT)); 
		} catch (SimulatorException e1) { e1.printStackTrace();}
	}

	void generateTransitions(String tname, String netName) {
		try { 
			n.addNewTransition(new Transition(tname, Transition_Type.INTERNAL, netName)); 
		} catch (SimulatorException e1) { e1.printStackTrace();}
	}

	
	void generateInputArcs(String source, String target) {
		try { 
			n.addInputArc(new InputArc(n.getPlace(source), n.getTransition(target))); 
		} catch (SimulatorException e1) { e1.printStackTrace(); }
	}

	void generateOutputArcs(String source, String target) {
		try { 
			n.addOutputArc(new OutputArc(n.getTransition(source), n.getPlace(target))); 
		} catch (SimulatorException e1) { e1.printStackTrace(); }
	}
}

class ArcPair 
{
	final String id;
	final String first;
	final String second;
	final boolean isInputArc;
	final String container;
	
	ArcPair(String _id, String _f, String _s, boolean _isInputArc, String _container) { 
		id = _id; first = _f; second = _s; 
		isInputArc = _isInputArc; container = _container;		
	}
}
