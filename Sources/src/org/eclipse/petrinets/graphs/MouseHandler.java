package org.eclipse.petrinets.graphs;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import org.eclipse.petrinets.gui.SimulationStatusWindow;
import org.eclipse.petrinets.gui.TextType;
import org.graphstream.algorithm.Dijkstra;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.Path;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.view.View;

public class MouseHandler  implements MouseListener, MouseWheelListener  {
	
	View view;
	SimulationStatusWindow sWindow;
	ExecutionMode execMode;
	Graph graph;
	
	MouseHandler(Graph graph, View view, ExecutionMode execMode) {
		this.graph = graph;
		this.view = view;
		this.execMode = execMode;
	}
	
	public void setStatusWindow(SimulationStatusWindow sWindow) {
		this.sWindow = sWindow;
	}
	
	@Override
	public void mouseClicked(MouseEvent me) {
		
		GraphicElement element = view.findNodeOrSpriteAt(me.getX(), me.getY());
        if(element != null){
            // Random r = new Random();
            // element.setAttribute("ui.style", "fill-color: rgb("+r.nextInt(256)+","+r.nextInt(256)+","+r.nextInt(256)+");");
            //System.out.println("Mouse clicked on Node: " + element.getLabel() + " ID: " + element.getId() + " ui.name: " + element.getAttribute("ui.name"));
            if(execMode == ExecutionMode.PETRI_NETS) {
            	sWindow.addText("\n\n > You Selected Node: " + element.getLabel() + " with identifier: " + element.getAttribute("ui.name") + "\n", TextType.HEADER);
            }
            if(execMode == ExecutionMode.REACHABILITY_GRAPHS) {
            	sWindow.addText("\n\n > You Selected Node: " + element.getLabel() + " with Marking: " + element.getAttribute("ui.name") + "\n", TextType.HEADER);

            	Node clickedNode = graph.getNode(element.getLabel());
            	Dijkstra dijkstra_1 = new Dijkstra();
            	Dijkstra dijkstra_2 = new Dijkstra();
            	dijkstra_1.init(graph);
            	dijkstra_2.init(graph);
            	//dijkstra.setSource(clickedNode);
            	dijkstra_1.setSource(graph.getNode("N0"));
            	dijkstra_1.compute();
            	
               	//for(Node curr_node: graph.getNodeSet()) {
        	    	if(dijkstra_1.getPathLength(clickedNode) != Double.POSITIVE_INFINITY) {
        	    		
        	    		for(Edge e : graph.getEdgeSet()) e.setAttribute("ui.class", "unmarked");
                		// for(Edge e : dijkstra.getPathEdges(clickedNode)) e.setAttribute("ui.class", "marked");
                		
                		Iterator<Path> pathIterator = dijkstra_1.getAllPathsIterator(clickedNode);
                		while (pathIterator.hasNext()) {
    						//System.out.println("[C] " + pathIterator.next());
                			Path path = pathIterator.next();
                			sWindow.addText("\n   > Path to Selected Node: \n", TextType.INFO);
                			for(Edge e : path.getEachEdge()) {
                				e.setAttribute("ui.class", "marked");
                				sWindow.addText("      + "+ e.getLabel("label") +"\n", TextType.DETAILED_INFO);
                			}
                		}
        	    		
        	    		dijkstra_2.setSource(clickedNode);
        	    		dijkstra_2.compute();
        	    		if(dijkstra_2.getPathLength(graph.getNode("N0")) != Double.POSITIVE_INFINITY) {
                      		Iterator<Path> _pathIterator = dijkstra_2.getAllPathsIterator(graph.getNode("N0"));
                    		while (_pathIterator.hasNext()) {
        						//System.out.println("[C] " + pathIterator.next());
                    			Path path = _pathIterator.next();
                    			sWindow.addText("\n   > Path back to Initial Node: \n", TextType.INFO);
                    			for(Edge e : path.getEachEdge()) {
                    				e.setAttribute("ui.class", "markedB");
                    				sWindow.addText("      + "+ e.getLabel("label") +"\n", TextType.DETAILED_INFO);
                    			}
                    		}
        	    		}
        	    		else {
        	    			// initial is not reachable from curr_node
        	    			// outcome += "\n   <!> Initial Node: N0 is not reachable from node: " + curr_node.getId();
        	    			sWindow.addText("\n\n   <!> The Initial Node is unreachable from Selected Node! \n", TextType.ERROR ); 
        	    		}
        	    	}
        	    	else {
        	    		// curr_node not reachable from initial
        	    		// outcome += "\n   <!> Node: " + curr_node.getId() + "is not reachable from Initial Node: N0";
        	    		sWindow.addText("\n\n   <!> The Selected Node is unreachable from Initial Node! \n", TextType.ERROR ); 
        	    	}
            	//}
            	
            	
            	//if(dijkstra_1.getPathLength(clickedNode) != Double.POSITIVE_INFINITY) {
            		
            		// Highlight the edge in this case!
            		/*for(Edge e : graph.getEdgeSet()) e.setAttribute("ui.class", "unmarked");
            		// for(Edge e : dijkstra.getPathEdges(clickedNode)) e.setAttribute("ui.class", "marked");
            		
            		Iterator<Path> pathIterator = dijkstra_1.getAllPathsIterator(clickedNode);
            		while (pathIterator.hasNext()) {
						//System.out.println("[C] " + pathIterator.next());
            			Path path = pathIterator.next();
            			sWindow.addText("\n   > Path to Selected Node: \n");
            			for(Edge e : path.getEachEdge()) {
            				e.setAttribute("ui.class", "marked");
            				sWindow.addText("      + "+ e.getLabel("label") +"\n");
            			}
            		}*/
            		// Iterator<Edge> pathIterator = dijkstra.getPathEdgesIterator(clickedNode);
	            	/*while (pathIterator.hasNext()) {
		            		// System.out.println(" Edge: " + pathIterator.next().getLabel("label"));
		            		// sWindow.addText(" Transition: " + pathIterator.next().getLabel("label"));
	            	}*/
            	//} else sWindow.addText("\n\n <!> The Clicked Node is unreachable from initial Marking! \n" ); 
            		//System.out.println(" <!> Clicked Node is unreachable from initial Marking! " );
            	
            	// sWindow.addText("     	with Marking: " + element.getAttribute("ui.name") + "\n");
            	
            	// split the string to get place and number of tokens
            	//String[] parts = ((String) element.getAttribute("ui.name")).split("_");
            	
            	//insert "1" in atom-atom boundry 
            	/*String name = ((String) element.getAttribute("ui.name")); //.replaceAll("(?<=[A-Z])(?=[A-Z])|(?<=[a-z])(?=[A-Z])|(?<=\\D)$", "1");
            	System.out.println(" \n NAME: " + name);
            	//split at letter-digit or digit-letter boundry
            	String regex =  "(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)";
            	String[] parts = name.split(regex);
            	
            	String[] a = new String[ parts.length/2 ];
            	int[] n = new int[ parts.length/2 ];

            	for(int i = 0 ; i < a.length ; i++) {
            	    a[i] = parts[i*2];
            	    n[i] = Integer.parseInt(parts[i*2+1]);
            	    sWindow.addText("  + place: " + a[i] + " has " + n[i] + " tokens\n");
            	}*/
            	
            	/*for (int i = 1; i < parts.length-1; i+=2) {
            		sWindow.addText("\n	place: " + parts[i] + " has tokens " + parts[i+1]);
            		sWindow.addText("\n");
            	}*/
            	
            }
        	// sWindow.addText("\n Mouse clicked on Node: " + element.getLabel() + " ID: " + element.getId() + " ui.name: " + element.getAttribute("ui.name"));
            
        }		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		//System.out.println("Mouse pressed; # of clicks: " + e.getClickCount());
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		//System.out.println("Mouse Released; # of clicks: " + e.getClickCount());
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		//System.out.println("Mouse Entered; # of clicks: " + e.getClickCount());
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		//System.out.println("Mouse Exited; # of clicks: " + e.getClickCount());
		
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		// DOES NOT WORK! NOT IMPLEMENTED BY VIEWER
		String message;
		int notches = e.getWheelRotation();
	    if (notches < 0) {
	    	message = "Mouse wheel moved UP "
	                    + -notches + " notch(es)" + "\n";
	    	view.getCamera().setViewPercent(0.5);
	    } else {
	        message = "Mouse wheel moved DOWN "
	                    + notches + " notch(es)" + "\n";
	        view.getCamera().setViewPercent(1.5);
	    }
	    System.out.println("check: "+ message);
	}
}
