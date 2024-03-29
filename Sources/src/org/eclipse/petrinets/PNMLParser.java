package org.eclipse.petrinets;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class PNMLParser 
{
	public List<String> getListOfPlaces() {
		return listOfPlaces;
	}

	public void setListOfPlaces(List<String> listOfPlaces) {
		this.listOfPlaces = listOfPlaces;
	}

	public List<String> getListOfInitialPlaces() {
		return listOfInitialPlaces;
	}

	public void setListOfInitialPlaces(List<String> listOfInitialPlaces) {
		this.listOfInitialPlaces = listOfInitialPlaces;
	}

	public List<String> getListOfTransitions() {
		return listOfTransitions;
	}

	public void setListOfTransitions(List<String> listOfTransitions) {
		this.listOfTransitions = listOfTransitions;
	}

	public List<ArcPair> getListOfArcs() {
		return listOfArcs;
	}

	public void setListOfArcs(List<ArcPair> listOfArcs) {
		this.listOfArcs = listOfArcs;
	}

	public Map<String, String> getMapOfPlacesToNet() {
		return mapOfPlacesToNet;
	}

	public void setMapOfPlacesToNet(Map<String, String> mapOfPlacesToNet) {
		this.mapOfPlacesToNet = mapOfPlacesToNet;
	}

	public Map<String, String> getMapOfTransitionsToNet() {
		return mapOfTransitionsToNet;
	}

	public void setMapOfTransitionsToNet(Map<String, String> mapOfTransitionsToNet) {
		this.mapOfTransitionsToNet = mapOfTransitionsToNet;
	}

	public Map<String, String> getMapOfReferencePlaces() {
		return mapOfReferencePlaces;
	}

	public void setMapOfReferencePlaces(Map<String, String> mapOfReferencePlaces) {
		this.mapOfReferencePlaces = mapOfReferencePlaces;
	}

	public Map<String, String> getMapOfPlaceIDToName() {
		return mapOfPlaceIDToName;
	}

	public void setMapOfPlaceIDToName(Map<String, String> mapOfPlaceIDToName) {
		this.mapOfPlaceIDToName = mapOfPlaceIDToName;
	}

	public Map<String, String> getMapOfTransitionIDToName() {
		return mapOfTransitionIDToName;
	}

	public void setMapOfTransitionIDToName(Map<String, String> mapOfTransitionIDToName) {
		this.mapOfTransitionIDToName = mapOfTransitionIDToName;
	}

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

	public List<String> getListOfReferencePlaces() {
		return listOfReferencePlaces;
	}

	public void setListOfReferencePlaces(List<String> listOfReferencePlaces) {
		this.listOfReferencePlaces = listOfReferencePlaces;
	}

	Map<String, String> mapOfPlacesToNet = new HashMap<String, String>(); // pid -> container
	Map<String, String> mapOfTransitionsToNet = new HashMap<String, String>(); // tid -> container
	
	Map<String, String> mapOfReferencePlaces = new HashMap<String, String>(); // id:key -> ref:value; also places that are not reference places are key:id = value:id
	
	Map<String, String> mapOfPlaceIDToName = new HashMap<String, String>(); // pid -> pname
	Map<String, String> mapOfTransitionIDToName = new HashMap<String, String>(); // tid -> tname
	
	public INet getNetObject() {
		return n;
	}
	
	String getPlaceName(String id) {
		return mapOfPlaceIDToName.get(id);
	}

	String getTransitionName(String id) {
		return mapOfTransitionIDToName.get(id);
	}
	
	public void generatePetriNetSyntax() {
		for(String elm : listOfPlaces) generatePlaces(elm, mapOfPlacesToNet.get(elm));
		
		for(String elm : listOfInitialPlaces) {
			try { n.getPlace(elm).addToken(new IntegerToken(0)); } 
			catch (SimulatorException e) { e.printStackTrace(); }
			n.getPlace(elm).setInitial();
		}
		
		for(String elm : listOfTransitions) generateTransitions(elm, mapOfTransitionsToNet.get(elm));
		for(ArcPair elm : listOfArcs) {
			if(elm.isInhibitor) generateInhibitorArcs(elm.first, elm.second);
			else if(elm.isReset) generateResetArcs(elm.first, elm.second);
			else if(elm.isInputArc) generateInputArcs(elm.first, elm.second);
			else generateOutputArcs(elm.first, elm.second);
		}
	}
	
	public PNMLParser(String pnmlFilePath) 
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
	/*public void reloadPetriNetModel() {
		factory = DocumentBuilderFactory.newInstance();
		try {
			builder = factory.newDocumentBuilder();
			document = builder.parse(new File(FILE_PATH));
			document.getDocumentElement().normalize();
			root = document.getDocumentElement();
			// System.out.println("Root Node: " + root.getNodeName());
			// System.out.println("");
			parseAndGeneratePetriNet();
			//generatePetriNetSyntax();
		} 
		catch(Exception e) { } //e.printStackTrace(); }		
	}*/

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
				String pname = pid; //eElement.getElementsByTagName("text").item(0).getTextContent();
				if(eElement.getElementsByTagName("text").getLength() > 0) 
					pname = eElement.getElementsByTagName("text").item(0).getTextContent();
				listOfPlaces.add(pid);
				mapOfPlaceIDToName.put(pid, pname);
				// TODO handle initial tokens in reference places?
				if(eElement.getElementsByTagName("initialMarking").getLength() > 0) {
					String initial = eElement.getElementsByTagName("initialMarking").item(0).getTextContent().trim(); //replaceAll("[\\n\\r\\t]+", " ");
					int numTokens = Integer.parseInt(initial);
					// TODO does not take multiple initial tokens
					if(numTokens > 0) {
						System.out.println("Place: " + pid +" is INIT: "+initial);
						listOfInitialPlaces.add(pid);
					}
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
				String tname = tid; // eElement.getElementsByTagName("text").item(0).getTextContent();
				if(eElement.getElementsByTagName("text").getLength() > 0)
					tname = eElement.getElementsByTagName("text").item(0).getTextContent();
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
				Element eElement = (Element) node;
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
					
					// Assumption that if there is a type then it is either inhibitor or reset arc
					if(eElement.getElementsByTagName("type").getLength() > 0) {
						//System.out.println("Check: " + eElement.getElementsByTagName("text").item(0).getTextContent());
						if(eElement.getElementsByTagName("text").item(0).getTextContent().equals("inhibitor")) {
							listOfArcs.add(new ArcPair(nid, source, mapOfReferencePlaces.get(target), true, true, false, container));
						}
						if(eElement.getElementsByTagName("text").item(0).getTextContent().equals("reset")) {
							listOfArcs.add(new ArcPair(nid, source, mapOfReferencePlaces.get(target), false, false, true, container));
						}
					}
					else {
						if(listOfPlaces.contains(source)) listOfArcs.add(new ArcPair(nid, mapOfReferencePlaces.get(source), target, true, false, false, container));
						else listOfArcs.add(new ArcPair(nid, source, mapOfReferencePlaces.get(target), false, false, false, container));
					}
				}
				//System.out.println("");
			}
		}
	}

	public void displayParsedContent() 
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
			n.addNewPlace(new Place(pname, mapOfPlaceIDToName.get(pname), Type.INT)); 
		} catch (SimulatorException e1) { e1.printStackTrace();}
	}

	void generateTransitions(String tname, String netName) {
		try { 
			n.addNewTransition(new Transition(tname, mapOfTransitionIDToName.get(tname), Transition_Type.INTERNAL, netName)); 
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
	
	void generateInhibitorArcs(String source, String target) {
		try { 
			// n.addOutputArc(new OutputArc(n.getTransition(source), n.getPlace(target)));
			n.addInhibitorArc(new InhibitorArc(n.getPlace(target), n.getTransition(source)));
		} catch (SimulatorException e1) { e1.printStackTrace(); }
	}
	
	void generateResetArcs(String source, String target) {
		try { 
			// n.addOutputArc(new OutputArc(n.getTransition(source), n.getPlace(target)));
			n.addResetArc(new ResetArc(n.getPlace(target), n.getTransition(source)));
		} catch (SimulatorException e1) { e1.printStackTrace(); }
	}
}

