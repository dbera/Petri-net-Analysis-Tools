package org.eclipse.petrinets.graphs;

import java.io.IOException;

import javax.swing.JPanel;

import org.eclipse.petrinets.IGraphGenerator;
import org.eclipse.petrinets.ValidationType;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceDGS;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;

public class StreamingReachabilityGraphGenerator implements IGraphGenerator {
	
	Graph graph;
	
	public StreamingReachabilityGraphGenerator(String graph_label, String file_location) {
		System.setProperty("gs.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
		graph = new MultiGraph(graph_label);
        graph.addAttribute("ui.stylesheet", styleSheet);
        graph.setAutoCreate(true);
        graph.setStrict(false);
        graph.addAttribute("ui.quality");
        graph.addAttribute("ui.antialias");
        Viewer viewer = graph.display();
        View view = viewer.getDefaultView();
        view.addMouseListener(new MouseHandler(view));
        view.addKeyListener(new KeyBoardHandler(view));
        loadGraphInstanceFromFile(file_location);
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
		} catch (IOException e) { e.printStackTrace(); }
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
		// TODO Auto-generated method stub
		return null;
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
