

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.Iterator;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceDGS;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;

public class GraphExplore {
    public static void main(String args[]) {
    	System.setProperty("gs.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
    	new GraphExplore();
    }

    void getGraphInstanceFromFile(Graph graph) {
		FileSource source = new FileSourceDGS();
		source.addSink( graph );
		try {
			source.begin("reachabilitygraph.dgs");

			while( source.nextStep() ){
			  // Do whatever between two events
				sleep();
			}
			source.end();
		
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public GraphExplore() {
        Graph graph = new MultiGraph("tutorial 1");
        graph.addAttribute("ui.stylesheet", styleSheet);
        graph.setAutoCreate(true);
        graph.setStrict(false);
        graph.addAttribute("ui.quality");
        graph.addAttribute("ui.antialias");
        //graph.display();
        Viewer viewer = graph.display();
        View view = viewer.getDefaultView();
        //view.addMouseListener(new MouseHandler(view));
        //view.addKeyListener(new KeyBoardHandler(view));
        
        // Gets it from file!
        getGraphInstanceFromFile(graph); 
        
        /*graph.addEdge("AB", "A", "B", true);
        graph.addEdge("BC", "B", "C", true);
        graph.addEdge("CA", "C", "A", true);
        graph.addEdge("AD", "A", "D", true);
        graph.addEdge("DE", "D", "E", true);
        graph.addEdge("DF", "D", "F", true);
        graph.addEdge("FG", "F", "G", true);
        graph.addEdge("FH", "F", "H", true);
        graph.addEdge("FI", "F", "I", true);
        graph.addEdge("HK", "H", "K", true);
        graph.addEdge("KM", "K", "M", true);
        graph.addEdge("MK", "M", "K", true);
        graph.addEdge("XY", "X", "Y", true);
        graph.addEdge("MX", "M", "X", true);
        graph.addEdge("YZ", "Y", "Z", true);
        graph.addEdge("PQ", "P", "Q", true);
        graph.addEdge("YP", "Y", "P", true);
        graph.addEdge("ZP", "Z", "P", true);
        graph.addEdge("QR", "Q", "R", true);*/

        // Add label to Graph nodes
        /*for (Node node : graph) {
            node.addAttribute("ui.label", "Node "+node.getId());
        }*/

        //explore(graph.getNode("A"));
    }
    
    public void explore(Node source) {
        Iterator<? extends Node> k = source.getDepthFirstIterator();//getBreadthFirstIterator();

        while (k.hasNext()) {
            Node next = k.next();
            next.setAttribute("ui.class", "marked");
            sleep();
        }
    }

    protected void sleep() {
        try { Thread.sleep(2000); } catch (Exception e) {}
    }

    /*
        "   stroke-mode: plain;" +
    	"   stroke-color: red;" +
    */
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
        "node.marked {" +
    	"	text-color: black;" +
        "	fill-color: green, red;" +
        "	fill-mode: gradient-horizontal;" +
        "	text-background-mode: none;" +
        "	text-padding: 5;" +
        "   text-alignment: under;" +
        "   text-size: 10;" +
        "}" +
    	"node:selected {\r\n" + 
    	"	fill-color: magenta;\r\n" + 
    	"}";
}
