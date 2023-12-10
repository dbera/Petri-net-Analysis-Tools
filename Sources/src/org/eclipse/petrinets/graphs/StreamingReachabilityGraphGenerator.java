package org.eclipse.petrinets.graphs;

import java.awt.Component;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.eclipse.petrinets.IGraphGenerator;
import org.eclipse.petrinets.ValidationType;
import org.eclipse.petrinets.graphs.validators.SCCValidator;
import org.eclipse.petrinets.graphs.validators.WTValidator;
import org.eclipse.petrinets.gui.SimulationStatusWindow;
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

public class StreamingReachabilityGraphGenerator implements IGraphGenerator {
	
	Graph graph;
	String file_location;
	SimulationStatusWindow sWindow;
	MouseHandler mh;
	
	public StreamingReachabilityGraphGenerator(String graph_label, String file_location) {
		System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
		graph = new MultiGraph(graph_label);
        graph.addAttribute("ui.stylesheet", styleSheet);
        graph.setAutoCreate(true);
        graph.setStrict(false);
        graph.addAttribute("ui.quality");
        graph.addAttribute("ui.antialias");
        //Viewer viewer = new Viewer(graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        //View view = viewer.addDefaultView(false);
        Viewer viewer = graph.display();
        
        // Added 27.09.2020: To support feature that it can be launched from petri net analysis jar file.
        viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.HIDE_ONLY);
        
        View view = viewer.getDefaultView();
        mh = new MouseHandler(graph, view, ExecutionMode.REACHABILITY_GRAPHS);
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
        
        //jfrmConsole.getContentPane().add((Component) view);
        loadGraphInstanceFromFile(file_location);
	}

	// Can be called after graph construction. 
	// Used on mouse click to compute and highlight path from initial node.
	private void markDeadlockStates() {
		for(Node node : graph.getNodeSet()) {
			if(node.getOutDegree() < 1)
				node.addAttribute("ui.class", "marked");
			//else node.addAttribute("ui.class", "unmarked"); // removed 16.11.2020 to accomodate difference graphs, 
			// In any case unmarked was not defined in stylesheet
		}
	}
	
	private void addAttributesToNodes() {
		for(Node node : graph.getNodeSet()) {
			node.addAttribute("node.scc", 0); // updated by the SCC algorithm
		}
	}
	
	@Override
	public void resetGraph() {
		graph.clear();
		graph.addAttribute("ui.stylesheet", styleSheet);
        graph.setAutoCreate(true);
        graph.setStrict(false);
        graph.addAttribute("ui.quality");
        graph.addAttribute("ui.antialias");
	}
	
	public void setStatusWindow(SimulationStatusWindow sWindow) {
		this.sWindow = sWindow;
		mh.setStatusWindow(sWindow);
	}
	
	@Override
	public void reloadGraphInstanceFromFile() {
		// graph.clear();
		FileSource source = new FileSourceDGS();
		//source.removeSink(graph);
		source.addSink( graph );
		try {
			//source.begin("reachabilitygraph.dgs");
			source.begin(file_location);

			while( source.nextStep() ){
				// Do whatever between two events
				sleep();
			}
			source.end();
		} catch (IOException e) { e.printStackTrace(); }
		
		markDeadlockStates();
		addAttributesToNodes();
	}
	
	void loadGraphInstanceFromFile(String _file_location) {
		file_location = _file_location;
		FileSource source = new FileSourceDGS();
		//source.removeSink(graph);
		source.addSink( graph );
		try {
			//source.begin("reachabilitygraph.dgs");
			source.begin(file_location);

			while( source.nextStep() ){
				// Do whatever between two events
				sleep();
			}
			source.end();
		} catch (IOException e) { e.printStackTrace(); }
		
		markDeadlockStates();
		addAttributesToNodes();
	}
	
	@Override
	public void addEdge(String edgeLabel, String edgeID, String srcNode, String dstNode, String context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addNode(String nodeLabel, String nodeID, String type, String context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String computeStructuralChecks(ValidationType vtype) {
		if(vtype.equals(ValidationType.SCC_RG)) {
			SCCValidator sccv = new SCCValidator();
			sccv.setStatusWindowInstance(sWindow);
			return sccv.checkProperty(graph, ValidationType.SCC_RG);
		}
		else if(vtype.equals(ValidationType.WT)) {
			WTValidator wtv = new WTValidator();
			wtv.setStatusWindowInstance(sWindow);
			return wtv.checkProperty(graph, ValidationType.WT);
		}
		else
			return " <!> Fatal: Property Check of Type: " + vtype.toString() + " is not supported \n";
	}

	@Override
	public void markNodeAsInitial(String nodeLabel, String nodeID, String type) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void markNodeAsInterfacePlace(String nodeLabel, String nodeID, String type) {
		// TODO Auto-generated method stub
		
	}


	protected void sleep() {
		try { Thread.sleep(2000); } catch (Exception e) {}
	}
	
    /*protected String styleSheet =
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
            "	fill-mode: gradient-horizontal;" +
            "	text-background-mode: none;" +
            "	text-padding: 5;" +
            "   text-alignment: under;" +
            "   text-size: 10;" +
            "	z-index: 0;" +
            "}" +
            "node.transition {" +
        	"	text-color: black;" +
            "	fill-color: green, red;" +
            "	fill-mode: gradient-horizontal;" +
            "	text-background-mode: none;" +
            "	text-padding: 5;" +
            "   text-alignment: under;" +
            "   text-size: 10;" +
            "}" +
        	"node:selected {" + 
            "	shape: rounded-box;" +
            "	size: 10;" +
            "	fill-mode: plain;" +
            "	fill-color: magenta;" +
            "	stroke-mode: plain;" +
        	"	stroke-color: blue;" + 
        	"}";*/
	
    protected String styleSheet =
        	"edge {" +
        	"	text-color: #9c9c9c;" +
        	"	text-background-mode: plain;" +
        	"	text-background-color: white;" +
        	"	text-padding: 1;" +
        	"   text-alignment: center;" +
        	"   text-size: 10;" +
        	"	z-index: 0;" +
        	"	fill-color: #41AE7E;" +
        	"	arrow-size: 5;" +
            "}" +
           	"edge.markedAE {" +
        	"	text-color: magenta;" +
        	"	text-background-mode: plain;" +
        	"	text-background-color: white;" +
        	"	text-padding: 1;" +
        	"   text-alignment: center;" +
        	"   text-size: 10;" +
        	"	z-index: 0;" +
        	"	fill-color: magenta;" +
        	"	arrow-size: 8;" +
            "}" +
            "edge.markedRE {" +
        	"	text-color: red;" +
        	"	text-background-mode: plain;" +
        	"	text-background-color: white;" +
        	"	text-padding: 1;" +
        	"   text-alignment: center;" +
        	"   text-size: 10;" +
        	"	z-index: 0;" +
        	"	fill-color: red;" +
        	"	arrow-size: 8;" +
            "}" +
           	"edge.marked {" +
        	"	text-color: #8B008B;" +
        	"	text-background-mode: plain;" +
        	"	text-background-color: white;" +
        	"	text-padding: 1;" +
        	"   text-alignment: center;" +
        	"   text-size: 10;" +
        	"	z-index: 0;" +
        	"	fill-color: #8B008B;" +
        	"	arrow-size: 5;" +
            "}" +
          	"edge.markedB {" +
        	"	text-color: red;" +
        	"	text-background-mode: plain;" +
        	"	text-background-color: white;" +
        	"	text-padding: 1;" +
        	"   text-alignment: center;" +
        	"   text-size: 10;" +
        	"	z-index: 0;" +
        	"	fill-color: red;" +
        	"	arrow-size: 5;" +
            "}" +
        	"node {" +
            "	fill-color: #DCDCDC, #778899;" + // cyan, blue //green, #31ce31;
            "	fill-mode: gradient-horizontal;" +
            "	text-background-mode: none;" +
            "	text-padding: 5;" +
            "   text-alignment: under;" +
            "   text-size: 10;" +
            "	z-index: 0;" +
            "}" +
            "node.marked {" +
        	"	text-color: #8B0000;" +
            "	fill-color: #FA8072, #B22222;" +
            "	fill-mode: gradient-horizontal;" +
            "	text-background-mode: none;" +
            "	text-padding: 5;" +
            "   text-alignment: under;" +
            "   text-size: 10;" +
            "}" +
            "node.diffAN {" +
        	"	text-color: black;" +
            "	fill-mode: plain;" +
            "	fill-color: magenta;" +
            "	text-background-mode: none;" +
            "	text-padding: 5;" +
            "   text-alignment: under;" +
            "   text-size: 10;" +
            "}" +
            "node.diffRN {" +
        	"	text-color: black;" +
            "	fill-mode: plain;" +
            "	fill-color: red;" +
            "	text-background-mode: none;" +
            "	text-padding: 5;" +
            "   text-alignment: under;" +
            "   text-size: 10;" +
            "}" +
            "node.diffUN {" +
        	"	text-color: black;" +
            "	fill-mode: plain;" +
            "	fill-color: green;" +
            "	text-background-mode: none;" +
            "	text-padding: 5;" +
            "   text-alignment: under;" +
            "   text-size: 10;" +
            "}" +
            "node#N0 {" +
        	"	text-color: #8B008B;" +
            "	fill-color: #5F9EA0, #483D8B;" +
            "	fill-mode: gradient-horizontal;" +
            "	text-background-mode: none;" +
            "	text-padding: 5;" +
            "   text-alignment: under;" +
            "   text-size: 10;" +
            "}" +
        	"node:selected {" + 
            "	shape: rounded-box;" +
            "	size: 10;" +
            "	fill-mode: plain;" +
            "	fill-color: magenta;" +
            "	stroke-mode: plain;" +
        	"	stroke-color: blue;" + 
        	"}";

	@Override
	public void computeDS() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeNode(String nodeID) {
		// TODO Auto-generated method stub
		
	}

}
