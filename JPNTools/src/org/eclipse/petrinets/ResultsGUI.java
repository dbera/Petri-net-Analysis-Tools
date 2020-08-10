package org.eclipse.petrinets;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;

import org.eclipse.petrinets.graphs.StreamingGraphGenerator;

// Instantiates streaming graph and GUI Panels
class ResultsGUI extends JFrame 
{
	private static final long serialVersionUID = 1L;
	
	private JFrame jfrmConsole;
	private PNMLParser parserInst; // contains the data structures after parsing PNML file
	private IGraphGenerator graphGen; // Launches Streaming Graph UI + Graph Operations + graph is constructed from this class
	private SimulationStatusWindow sWindow; // Text Window and Buttons for Outcome of Structural Analysis -> Calls graphGen for checks!
	
	private boolean ignoreInterfacePlaces;
	
	public ResultsGUI(PNMLParser _parserInst, boolean isDisplayGraph, boolean _ignoreInterfacePlaces) {
		parserInst = _parserInst;
		ignoreInterfacePlaces = _ignoreInterfacePlaces;
		jfrmConsole = new JFrame("Structural Analysis");
        jfrmConsole.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //jfrmConsole.setLocationRelativeTo(null);
        jfrmConsole.setResizable(true);
        // we are not loading streaming graph from file location! we will construct it.
        graphGen = new StreamingGraphGenerator("Yasper-Petri-Net", isDisplayGraph, "", 
        		parserInst.mapOfPlaceIDToName, parserInst.mapOfTransitionIDToName);
        sWindow = new SimulationStatusWindow(graphGen, this);
        jfrmConsole.getContentPane().add(sWindow);
        jfrmConsole.pack();
        jfrmConsole.setAlwaysOnTop(true);
        jfrmConsole.setVisible(true);
	}
	
	public void reloadPetriNetFromPNMLFile() {
		parserInst.reloadPetriNetModel();
	}
	
	public void computePetriNetGraph() {
		// Add places
		List<String> listOfPlaces = parserInst.listOfPlaces;
		// add to node in graph the attributes place-type and name
		for(String placeID : listOfPlaces) {
			if(parserInst.mapOfPlaceIDToName.containsKey(placeID)) // check to not add reference places because it is fileterd already in this map!
				graphGen.addNode(parserInst.mapOfPlaceIDToName.get(placeID), placeID, "place", parserInst.mapOfPlacesToNet.get(placeID));
		}
		
		List<String> listOfInitialPlaces = parserInst.listOfInitialPlaces;
		for(String placeID : listOfInitialPlaces) {
			if(parserInst.mapOfPlaceIDToName.containsKey(placeID)) // check to not add reference places!
				graphGen.markNodeAsInitial(parserInst.mapOfPlaceIDToName.get(placeID), placeID, "place");
		}
		
		// if a place in keys of mapofplacestonet is present in value of map of referenceplaces then it is an interface place!
		for(String placeID : parserInst.listOfReferencePlaces) {
			graphGen.markNodeAsInterfacePlace(parserInst.mapOfPlaceIDToName.get(placeID), placeID, "place");
		}
		
		List<String> listOfTransitions = parserInst.listOfTransitions;
		// add to node in graph the attributes transition-type and name
		for(String trID : listOfTransitions) {
			graphGen.addNode(parserInst.mapOfTransitionIDToName.get(trID), trID, "transition", parserInst.mapOfTransitionsToNet.get(trID));
		}
		
		// Add edges
		List<ArcPair> listOfArcs = parserInst.listOfArcs;
		for(ArcPair e : listOfArcs)
			graphGen.addEdge("", e.id, e.first, e.second, "NO_CONTEXT");
		
		// Now we will delete interface places if the flag is set.
		if(ignoreInterfacePlaces) removeInterfacePlaces();
		
		// re-compute the data structures corresponding the graph from the graph itself
		graphGen.computeDS();
	}
	
	public void removeInterfacePlaces() {
		for(String intfP : parserInst.listOfReferencePlaces) {
			graphGen.removeNode(intfP);
		}		
	}
}

class SimulationStatusWindow extends JPanel
{
	private static final long serialVersionUID = 1L;
    //protected JTextField textField;
    protected JTextArea textArea;
    protected JTextPane textEditor;
    JScrollPane scrollPane;
    // Use this to get buttons in this window
    SimulatorOptionsButtonPanel simOptionsButtonPanel;
    IGraphGenerator graphGen;
    
    public SimulationStatusWindow(IGraphGenerator g, ResultsGUI _gui) 
    {
    	graphGen = g;
    	
    	this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

    	simOptionsButtonPanel = new SimulatorOptionsButtonPanel(this, graphGen, _gui);
    	textArea = new JTextArea(35, 85);
    	textArea.setEditable(true);

    	//textArea.setPreferredSize(new Dimension(300, 300));
    	//textArea.setMinimumSize(new Dimension(300, 300));
    	//textArea.setBounds(0, 0, 300, 300);

    	textArea.setForeground(Color.BLACK);
    	textArea.setBackground(Color.CYAN);
    	textArea.setFont(new Font("Consolas", Font.PLAIN, 14));

    	//textArea.setLineWrap(true);
    	//textArea.setWrapStyleWord(true);

    	scrollPane = new JScrollPane(textArea);
    	scrollPane.setMaximumSize(scrollPane.getPreferredSize());
    	scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    	scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());

    	//stateLabel.setFont(new Font("Consolas", Font.BOLD, 16));

    	//this.add(stateLabel, BorderLayout.CENTER);
    	this.add(scrollPane, BorderLayout.CENTER);
    	this.add(simOptionsButtonPanel);
    }
    
    public void addText(String txt) {
    	textArea.setText(textArea.getText() + txt);
    }
}

class SimulatorOptionsButtonPanel extends JPanel implements ActionListener
{
	private static final long serialVersionUID = 1L;
	boolean isTextArea;
	SimulationStatusWindow statusWindow;
	JButton checkSNET = new JButton("Check-SNet");
	JButton checkWFN = new JButton("Check-WFN");
	JButton checkOPN = new JButton("Check-OPN");
	JButton checkWT = new JButton("Check-WT");
	JButton checkSCC = new JButton("Check-SCC");
	JComboBox<String> selectNode; //= new JComboBox();
	IGraphGenerator graphGen;
	ResultsGUI rgui;
	//JLabel stateLabel = new JLabel("CURRENT STATE: ");
	//JLabel stateLabelTxt = new JLabel(" ABC ");
    
	public SimulatorOptionsButtonPanel(SimulationStatusWindow sWindow, IGraphGenerator g, ResultsGUI _rgui) 
	{
		graphGen = g;
		rgui = _rgui;
				
		statusWindow = sWindow;
		this.setLayout(new GridBagLayout());
		
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        
		checkSNET.addActionListener(this);
		checkWFN.addActionListener(this);
		checkWT.addActionListener(this);
		checkOPN.addActionListener(this);
		checkSCC.addActionListener(this);
		
	    String languages[]={"C","C++","C#","Java","PHP"};
	    selectNode = new JComboBox<>(languages);
		
		checkSNET.setEnabled(true);
		checkWFN.setEnabled(true);
		checkWT.setEnabled(false);
		checkOPN.setEnabled(true);
		checkSCC.setEnabled(true);
		
		/*constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.weightx = 0.5;
		stateLabel.setFont(new Font("Consolas", Font.BOLD, 16)); 
		this.add(stateLabel, constraints);*/

		/*constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.weightx = 0.5;
		this.add(stateLabelTxt, constraints);*/
		
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.weightx = 0.5;
		this.add(checkSNET, constraints);

		constraints.gridx = 1;
		constraints.gridy = 1;
		constraints.weightx = 0.5;
		this.add(checkWFN, constraints);

		constraints.gridx = 2;
		constraints.gridy = 1;
		constraints.weightx = 0.5;
		this.add(checkWT, constraints);
		
		constraints.gridx = 3;
		constraints.gridy = 1;
		constraints.weightx = 0.5;
		this.add(checkOPN, constraints);
		
		constraints.gridx = 4;
		constraints.gridy = 1;
		constraints.weightx = 0.5;
		this.add(checkSCC, constraints);
		
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.weightx = 0.5;
		this.add(selectNode, constraints);
		
		this.setBorder(BorderFactory.createTitledBorder(
							BorderFactory.createEtchedBorder(), "Simulator Options"));
	}
	
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		if(e.getActionCommand().equals("Check-SNet")) {
			statusWindow.addText("\n ************************************************** \r\n" + 
					"   Property Check for State Machine Net\r\n" + 
					" ************************************************** \n" + 
				graphGen.computeStructuralChecks(ValidationType.SNET) + "\n");
			statusWindow.addText("\n ************************************************** \r\n");
			
			// To construct skeleton and forget
			rgui.removeInterfacePlaces();
			graphGen.computeDS();
		}
		if(e.getActionCommand().equals("Check-WFN")) {
			statusWindow.addText("\n ************************************************** \r\n" + 
					"   Property Check for Workflow Net\r\n" + 
					" ************************************************** \n" + 
				graphGen.computeStructuralChecks(ValidationType.WFN) + "\n");
			statusWindow.addText("\n ************************************************** \r\n");
			
			// To reload file
			rgui.reloadPetriNetFromPNMLFile();
			rgui.computePetriNetGraph();
		}
		if(e.getActionCommand().equals("Check-OPN")) {
			statusWindow.addText("\n ************************************************** \r\n" + 
					"   Property Check for Open Petri Net\r\n" + 
					" ************************************************** \n" + 
				graphGen.computeStructuralChecks(ValidationType.OPN) + "\n");
			statusWindow.addText("\n ************************************************** \r\n");
		}
		if(e.getActionCommand().equals("Check-WT")) {
			statusWindow.addText("\n ************************************************** \r\n" + 
					"   Property Check for Weak Termination \r\n" + 
					" ************************************************** \n" + 
					graphGen.computeStructuralChecks(ValidationType.WT) + "\n");
			statusWindow.addText("\n ************************************************** \r\n");
		}
		if(e.getActionCommand().equals("Check-SCC")) { 
			statusWindow.addText("\n ************************************************** \r\n" + 
					"   Property Check for Strongly Connected Components \r\n" + 
					" ************************************************** \n" + 
					graphGen.computeStructuralChecks(ValidationType.SCC) + "\n");
			statusWindow.addText("\n ************************************************** \r\n");
		}
	}
}