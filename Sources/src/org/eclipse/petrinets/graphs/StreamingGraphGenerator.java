package org.eclipse.petrinets.graphs;

import java.awt.Component;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.petrinets.IGraphGenerator;
import org.eclipse.petrinets.ValidationType;
import org.eclipse.petrinets.graphs.validators.OPNValidator;
import org.eclipse.petrinets.graphs.validators.SCCValidator;
import org.eclipse.petrinets.graphs.validators.SNetValidator;
import org.eclipse.petrinets.graphs.validators.WTValidator;
import org.eclipse.petrinets.graphs.validators.WfnValidator;
import org.eclipse.petrinets.gui.SimulationStatusWindow;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceDGS;
import org.graphstream.ui.geom.Point2;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.view.Camera;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;

public class StreamingGraphGenerator implements IGraphGenerator {

	Graph graph;
	GraphDS gds;
	SimulationStatusWindow sWindow;
	MouseHandler mh;
	boolean _isDisplayGraph;
	
	Map<String, String> mapOfPlaceIDToName = new HashMap<String, String>(); // pid -> pname
	Map<String, String> mapOfTransitionIDToName = new HashMap<String, String>(); // tid -> tname
	
	public StreamingGraphGenerator(String graph_label, boolean isDisplayGraph, String file_location, 
			Map<String, String> _mapOfPlaceIDToName, Map<String, String> _mapOfTransitionIDToName) 
	{
		System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
		
		mapOfPlaceIDToName = _mapOfPlaceIDToName;
		mapOfTransitionIDToName = _mapOfTransitionIDToName;
		
		graph = new MultiGraph(graph_label);
		
		_isDisplayGraph = isDisplayGraph;
        if(isDisplayGraph) {
			graph.addAttribute("ui.stylesheet", styleSheet);
	        graph.setAutoCreate(true);
	        graph.setStrict(false);
	        graph.addAttribute("ui.quality");
	        graph.addAttribute("ui.antialias");
	        Viewer viewer = graph.display();
	        View view = viewer.getDefaultView();
	        mh = new MouseHandler(graph, view, ExecutionMode.PETRI_NETS);
	        view.addMouseListener(mh);
	        view.addKeyListener(new KeyBoardHandler(view));
	        
	        // handles zooming on mouse wheel
	        ((Component) view).addMouseWheelListener(new MouseWheelListener() {
	            @Override
	            public void mouseWheelMoved(MouseWheelEvent e) {
	                e.consume();
	                int i = e.getWheelRotation();
	                double factor = Math.pow(1.25, i);
	                Camera cam = view.getCamera();
	                double zoom = cam.getViewPercent() * factor;
	                Point2 pxCenter  = cam.transformGuToPx(cam.getViewCenter().x, cam.getViewCenter().y, 0);
	                Point3 guClicked = cam.transformPxToGu(e.getX(), e.getY());
	                double newRatioPx2Gu = cam.getMetrics().ratioPx2Gu/factor;
	                double x = guClicked.x + (pxCenter.x - e.getX())/newRatioPx2Gu;
	                double y = guClicked.y - (pxCenter.y - e.getY())/newRatioPx2Gu;
	                cam.setViewCenter(x, y, 0);
	                cam.setViewPercent(zoom);
	            }
	        });
        }
        
        //if(isLoadInstanceFromFile) loadGraphInstanceFromFile(file_location);
	}

	public void setStatusWindow(SimulationStatusWindow sWindow) {
		this.sWindow = sWindow;
		// 27.09.2020: Added to prevent null pointer exception when no graph is displayed!
		if(_isDisplayGraph) mh.setStatusWindow(sWindow);
	}
	
	
	void loadGraphInstanceFromFile(String file_location) {
		FileSource source = new FileSourceDGS();
		source.addSink( graph );
		try {
			//source.begin("reachabilitygraph.dgs");
			source.begin(file_location);

			while( source.nextStep() ){
				// Do whatever between two events
				sleep();
			}
			source.end();
		} catch (IOException e) { e.printStackTrace();  }
	}
	
	@Override
	public void addEdge(String edgeLabel, String edgeID, String srcNode, String dstNode, String context) {
		Edge e = graph.addEdge(edgeID, srcNode, dstNode, true);
		e.addAttribute("ui.label", edgeLabel);
	}

	@Override
	public void addNode(String nodeLabel, String nodeID, String type, String context) {
		Node n = graph.addNode(nodeID);
	    n.addAttribute("ui.label", nodeLabel);
	    n.addAttribute("ui.class", type);
	    ArrayList<String> containers = new ArrayList<String>();
	    containers.add(context);
	    n.addAttribute("ui.name", nodeID);
	    n.addAttribute("node.containers", containers);
	    n.addAttribute("node.idle", "NO");
	    n.addAttribute("node.interface", "NO");
	    n.addAttribute("node.scc", 0); // updated by the SCC algorithm
	}

	@Override
	public void removeNode(String nodeID) {
		graph.removeNode(nodeID);
		
	}
	
	@Override
	public void computeDS() {
		gds = new GraphDS();
		gds.computeDS(graph);
	}
	
	@Override
	public String computeStructuralChecks(ValidationType vtype) {
		if(vtype.equals(ValidationType.SNET)) {
			SNetValidator snv = new SNetValidator();
			snv.setDataStructures(mapOfPlaceIDToName, mapOfTransitionIDToName, 
					gds.listOfInitialNodes, gds.listOfInterfaceNodes, 
					gds.listOfPlaces, gds.listOfTransitions, gds.mapModuleIdToNodeIds);
			snv.setStatusWindowInstance(sWindow);
			return snv.checkProperty(graph, ValidationType.SNET);
		}
		else if(vtype.equals(ValidationType.WFN)) {
			WfnValidator wfnv = new WfnValidator();
			wfnv.setDataStructures(mapOfPlaceIDToName, mapOfTransitionIDToName, 
					gds.listOfInitialNodes, gds.listOfInterfaceNodes, 
					gds.listOfPlaces, gds.listOfTransitions, gds.mapModuleIdToNodeIds);
			wfnv.setStatusWindowInstance(sWindow);
			return wfnv.checkProperty(graph, ValidationType.WFN);
		}
		else if(vtype.equals(ValidationType.SCC_PN)) {
			SCCValidator sccv = new SCCValidator();
			sccv.setDataStructures(mapOfPlaceIDToName, mapOfTransitionIDToName, 
					gds.listOfInitialNodes, gds.listOfInterfaceNodes, 
					gds.listOfPlaces, gds.listOfTransitions, gds.mapModuleIdToNodeIds);
			sccv.setStatusWindowInstance(sWindow);
			return sccv.checkProperty(graph, ValidationType.SCC_PN);
		}
		else if(vtype.equals(ValidationType.OPN)) {
			OPNValidator opnv = new OPNValidator();
			opnv.setDataStructures(mapOfPlaceIDToName, mapOfTransitionIDToName, 
					gds.listOfInitialNodes, gds.listOfInterfaceNodes, 
					gds.listOfPlaces, gds.listOfTransitions, gds.mapModuleIdToNodeIds);
			opnv.setStatusWindowInstance(sWindow);
			return opnv.checkProperty(graph, ValidationType.OPN);
		}
		/*else if(vtype.equals(ValidationType.WT)) {
			WTValidator wtv = new WTValidator();
			wtv.setDataStructures(mapOfPlaceIDToName, mapOfTransitionIDToName, 
					gds.listOfInitialNodes, gds.listOfInterfaceNodes, 
					gds.listOfPlaces, gds.listOfTransitions, gds.mapModuleIdToNodeIds);
			return wtv.checkProperty(graph, ValidationType.WT);
		}*/
		else
			return "<!> Fatal: Property Check of Type: " + vtype.toString() + " is not supported \n";
	}

	@Override
	public void markNodeAsInitial(String nodeLabel, String nodeID, String type) {
		Node n = graph.getNode(nodeID);
		n.setAttribute("node.idle", "YES");
	}

	@Override
	public void markNodeAsInterfacePlace(String nodeLabel, String nodeID, String type) {
		Node n = graph.getNode(nodeID);
		n.setAttribute("node.interface", "YES");
	}
	
	protected void sleep() {
		try { Thread.sleep(2000); } catch (Exception e) {}
	}

    protected String styleSheet =
        	"edge {" +
        	"	text-color: grey;" +
        	"	text-background-mode: plain;" +
        	"	text-background-color: white;" +
        	"	text-padding: 1;" +
        	"   text-alignment: center;" +
        	"   text-size: 10;" +
        	"	z-index: 0;" +
        	"	fill-color: #41AE7E;" +
        	"	arrow-size: 6;" +
            "}" +
        	"node {" +
            "	fill-color: cyan, blue;" +
            "	size: 10;" +
            "	fill-mode: gradient-horizontal;" +
            "	text-background-mode: none;" +
            "	text-padding: 5;" +
            "   text-alignment: under;" +
            "   text-size: 10;" +
            "	z-index: 0;" +
            "}" +
            "node.transition {" +
            "	shape: rounded-box;" +
            "	size: 15;" +
        	"	text-color: black;" +
            "	fill-color: green, red;" +
            "	fill-mode: gradient-horizontal;" +
            "	text-background-mode: none;" +
            "	text-padding: 5;" +
            "   text-alignment: under;" +
            "   text-size: 10;" +
            "}" +
        	"node:selected {" + 
            "	size: 10;" +
            "	fill-mode: plain;" +
            "	fill-color: magenta;" +
            "	stroke-mode: plain;" +
        	"	stroke-color: blue;" + 
        	"}";

	@Override
	public void reloadGraphInstanceFromFile() {
		// TODO Auto-generated method stub
		// This graph is created by PNML Parser by calling edges and nodes. 
		// Not directly by graph streaming libs
		
	}

	@Override
	public void resetGraph() {
		// TODO Auto-generated method stub
		graph.clear();
		graph.addAttribute("ui.stylesheet", styleSheet);
        graph.setAutoCreate(true);
        graph.setStrict(false);
        graph.addAttribute("ui.quality");
        graph.addAttribute("ui.antialias");
	}
}


class GraphDS 
{
	List<String> listOfInitialNodes = new ArrayList<String>();
	List<String> listOfInterfaceNodes = new ArrayList<String>();
	List<String> listOfPlaces = new ArrayList<String>();
	List<String> listOfTransitions = new ArrayList<String>();
	Map<String, ArrayList<String>> mapModuleIdToNodeIds = new HashMap<String, ArrayList<String>>();
	
	public void computeDS(Graph graph) {
		for(Node node: graph.getEachNode()) {
			if(node.hasAttribute("node.idle")) {
				if(node.getAttribute("node.idle").equals("YES")) {
					//outcome += "node " + node.getId() + " is initial! \n";
					listOfInitialNodes.add(node.getId());
				}
			}
			// whether place or transition
			if(node.hasAttribute("ui.class")) {
				if(node.getAttribute("ui.class").equals("place")) 
					listOfPlaces.add(node.getId());
				else 
					listOfTransitions.add(node.getId());
			}
			
			// add to map: module id -> list of place ids
			if(node.hasAttribute("node.containers")) {
				ArrayList<String> containers = node.getAttribute("node.containers");
				for(String container : containers) {
					if(mapModuleIdToNodeIds.containsKey(container)) {
						ArrayList<String> nlist = mapModuleIdToNodeIds.get(container);
						nlist.add(node.getId());
						mapModuleIdToNodeIds.put(container, nlist);
					}
					else {
						ArrayList<String> nlist = new ArrayList<String>();
						nlist.add(node.getId());
						mapModuleIdToNodeIds.put(container, nlist);
					}
				}
			}

			if(node.hasAttribute("node.interface")) {
				String isInterface = node.getAttribute("node.interface");
				if (isInterface.equals("YES")) {
					listOfInterfaceNodes.add(node.getId());
					//outcome += "node " + node.getId() + " is interface place! \n";
					
				}
			}
		}		
	}
}
