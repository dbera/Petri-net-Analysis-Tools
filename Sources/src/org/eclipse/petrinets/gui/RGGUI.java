package org.eclipse.petrinets.gui;

import javax.swing.JFrame;

import org.eclipse.petrinets.IGraphGenerator;
import org.eclipse.petrinets.graphs.StreamingReachabilityGraphGenerator;

public class RGGUI {
	private static final long serialVersionUID = 1L;
	
	private JFrame jfrmConsole;
	//private PNMLParser parserInst; // contains the data structures after parsing PNML file
	private IGraphGenerator graphGen; // Launches Streaming Graph UI + Graph Operations + graph is constructed from this class
	private SimulationStatusWindow sWindow; // Text Window and Buttons for Outcome of Structural Analysis -> Calls graphGen for checks!
	
	//private boolean ignoreInterfacePlaces;
	
	public RGGUI(String path) {
		//parserInst = _parserInst;
		//ignoreInterfacePlaces = _ignoreInterfacePlaces;
		jfrmConsole = new JFrame("Reachability Analysis");
        jfrmConsole.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); //JFrame.DISPOSE_ON_CLOSE
        //jfrmConsole.setLocationRelativeTo(null);
        jfrmConsole.setResizable(true);

        graphGen = new StreamingReachabilityGraphGenerator("Reachability-Graph", path);
        sWindow = new SimulationStatusWindow(graphGen, this);
        graphGen.setStatusWindow(sWindow);
        
        jfrmConsole.getContentPane().add(sWindow);
        jfrmConsole.pack();
        jfrmConsole.setAlwaysOnTop(true);
        jfrmConsole.setVisible(true);
        //new GraphSwing().display();
	}
	
	public void reloadGraphInstanceFromFile() {
		graphGen.reloadGraphInstanceFromFile();
	}
	
	public void resetStreamingGraph() {
		graphGen.resetGraph();
	}
}


// Example of how to integrate Graph Stream Viewer into your own JFrame
/*class GraphSwing {

    public static void main(String args[]) {
        EventQueue.invokeLater(new GraphSwing()::display);
    }

    public void display() {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel(new GridLayout()){
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(640, 480);
            }
        };
        panel.setBorder(BorderFactory.createLineBorder(Color.blue, 5));
        Graph graph = new SingleGraph("Tutorial", false, true);
        graph.addEdge("AB", "A", "B");
        Node a = graph.getNode("A");
        a.setAttribute("xy", 1, 1);
        Node b = graph.getNode("B");
        b.setAttribute("xy", -1, -1);
        Viewer viewer = new Viewer(graph, Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        viewer.enableAutoLayout();
        ViewPanel viewPanel = viewer.addDefaultView(false);
        panel.add(viewPanel);
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}*/
