package org.eclipse.petrinets.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.eclipse.petrinets.IGraphGenerator;
import org.eclipse.petrinets.ValidationType;

public class SimulationStatusWindow extends JPanel
{
	private static final long serialVersionUID = 1L;
    //protected JTextField textField;
    //private JTextArea textArea;
    private JTextPane textPane;
    private JScrollPane scrollPane;
    // Use this to get buttons in this window
    private SimulatorOptionsButtonPanel simOptionsButtonPanel;
    private IGraphGenerator graphGen;

    private void addFormattedText(JTextPane pane, String text, Color color, TextType txtType) {
        StyledDocument doc = pane.getStyledDocument();

        Style style = pane.addStyle("Color Style", null);
        StyleConstants.setForeground(style, color);
        
        if(txtType.equals(TextType.HEADER)) {
        	
        }
        
        if(txtType.equals(TextType.INFO)) {
        	
        }

        if(txtType.equals(TextType.DEFAULT)) {
        	
        }

        try {
            doc.insertString(doc.getLength(), text, style);
        } 
        catch (BadLocationException e) {
            e.printStackTrace();
        }           
    }
    
    
    public SimulationStatusWindow(IGraphGenerator g, RGGUI _rgui) 
    {
    	// Reachability specific
    	graphGen = g;
    	
    	this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    	
       	simOptionsButtonPanel = new SimulatorOptionsButtonPanel(this, graphGen, _rgui);
    	
       	//textArea = new JTextArea(35, 85);
    	//textArea.setPreferredSize(new Dimension(300, 300));
    	//textArea.setMinimumSize(new Dimension(300, 300));
    	//textArea.setBounds(0, 0, 300, 300);
       	
       	textPane = new JTextPane();
       	textPane.setPreferredSize(new Dimension(700, 500));       	
       	textPane.setEditable(false);
       	Font font = new Font("consolas", Font.PLAIN, 14);
        textPane.setFont(font);
    	
    	/*textArea.setForeground(Color.BLACK);
    	textArea.setBackground(new Color(255,229,204));
    	textArea.setFont(new Font("Consolas", Font.PLAIN, 14));*/
    	
    	// scrollPane = new JScrollPane(textArea);
       	scrollPane = new JScrollPane(textPane);
    	scrollPane.setMaximumSize(scrollPane.getPreferredSize());
    	scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    	scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
    	
      	this.add(scrollPane, BorderLayout.CENTER);
    	this.add(simOptionsButtonPanel);
    }
    
    public SimulationStatusWindow(IGraphGenerator g, PNGUI _rgui) 
    {
    	// Petri net specific
    	graphGen = g;
    	
    	this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

    	simOptionsButtonPanel = new SimulatorOptionsButtonPanel(this, graphGen, _rgui);
       	
    	//textArea = new JTextArea(35, 85);
    	//textArea.setPreferredSize(new Dimension(300, 300));
    	//textArea.setMinimumSize(new Dimension(300, 300));
    	//textArea.setBounds(0, 0, 300, 300);

       	textPane = new JTextPane();
       	textPane.setPreferredSize(new Dimension(700, 500));       	
       	textPane.setEditable(false);
       	Font font = new Font("consolas", Font.PLAIN, 14);
        textPane.setFont(font);
        
    	/*textArea.setForeground(Color.BLACK);
    	textArea.setBackground(new Color(255,229,204));
    	textArea.setFont(new Font("Consolas", Font.PLAIN, 14));*/

    	//textArea.setLineWrap(true);
    	//textArea.setWrapStyleWord(true);

    	// scrollPane = new JScrollPane(textArea);
       	scrollPane = new JScrollPane(textPane);
    	scrollPane.setMaximumSize(scrollPane.getPreferredSize());
    	scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    	scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());

    	//stateLabel.setFont(new Font("Consolas", Font.BOLD, 16));

    	//this.add(stateLabel, BorderLayout.CENTER);
    	this.add(scrollPane, BorderLayout.CENTER);
    	this.add(simOptionsButtonPanel);
    }
    
    public void addText(String txt, TextType txtType) {
    	// HEADER, FOOTER, ERROR, INFO, DETAILED_INFO, DETAILED_ERROR, DEFAULT
    	if(txtType.equals(TextType.DEFAULT)) 
    		addFormattedText(textPane, txt, Color.DARK_GRAY, TextType.DEFAULT);
    	else if(txtType.equals(TextType.HEADER)) 
    		addFormattedText(textPane, txt, Color.blue, TextType.HEADER);
    	else if(txtType.equals(TextType.FOOTER)) 
    		addFormattedText(textPane, txt, Color.blue, TextType.FOOTER);
    	else if(txtType.equals(TextType.ERROR)) 
    		addFormattedText(textPane, txt, new Color(139,0,0), TextType.ERROR);
    	else if(txtType.equals(TextType.INFO)) 
    		addFormattedText(textPane, txt, new Color(72,61,139), TextType.INFO);
    	else if(txtType.equals(TextType.DETAILED_INFO)) 
    		addFormattedText(textPane, txt, new Color(106,90,205), TextType.DETAILED_INFO);
    	else if(txtType.equals(TextType.DETAILED_ERROR)) 
    		addFormattedText(textPane, txt, new Color(139,0,0), TextType.DETAILED_ERROR);
    	else 
    		addFormattedText(textPane, txt, Color.DARK_GRAY, TextType.DEFAULT);

    }
}

class SimulatorOptionsButtonPanel extends JPanel implements ActionListener
{
	private static final long serialVersionUID = 1L;
	boolean isTextArea;
	SimulationStatusWindow statusWindow;
	// Petri net Checks
	JButton checkSNET = new JButton("Check-SNet");
	JButton checkWFN = new JButton("Check-WFN");
	JButton checkOPN = new JButton("Check-OPN");
	JButton checkSCCPN = new JButton("Check-SCC-PN");
	JButton checkSCCRG = new JButton("Check-SCC-RG");
	
	// RG Checks
	JButton checkWT = new JButton("Check-WT");
	JButton checkDeadlock = new JButton("check-deadlock");
	JButton checkBoundedNess = new JButton("check-boundedness");
	
	//JComboBox<String> selectNode; //= new JComboBox();
	JButton computeSkeleton = new JButton("compute-skeleton");
	JButton reloadNet = new JButton("reload-net");
	JButton reloadGraph = new JButton("reload-graph");
	JButton computeReachabilityGraph = new JButton("compute-reachability-graph");
	
	IGraphGenerator graphGen;
	PNGUI pngui;
	RGGUI rgui;
	// reloadPetriNetFromPNMLFile: Stopping here. 20.51 on 11.08.2020
	//JLabel stateLabel = new JLabel("CURRENT STATE: ");
	//JLabel stateLabelTxt = new JLabel(" ABC ");
    
	public SimulatorOptionsButtonPanel(SimulationStatusWindow sWindow, IGraphGenerator g, RGGUI _rgui) 
	{
		// RG Specific stuff
		graphGen = g;
		rgui = _rgui;
		
		statusWindow = sWindow;
		this.setLayout(new GridBagLayout());
		
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        
        checkWT.addActionListener(this);
        reloadGraph.addActionListener(this);
        checkSCCRG.addActionListener(this);
        
        checkWT.setEnabled(true);
        reloadGraph.setEnabled(true);
        checkSCCRG.setEnabled(true);

        // comment out
        // checkWT.setEnabled(false);
        // checkSCCRG.setEnabled(false);

        
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.weightx = 0.5;
		this.add(checkWT, constraints);
		
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.weightx = 0.5;
		this.add(reloadGraph, constraints);
	
		constraints.gridx = 2;
		constraints.gridy = 0;
		constraints.weightx = 0.5;
		this.add(checkSCCRG, constraints);
		
		
		this.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "State Space Analysis"));
	}
	
	public SimulatorOptionsButtonPanel(SimulationStatusWindow sWindow, IGraphGenerator g, PNGUI _pngui) 
	{
		graphGen = g;
		pngui = _pngui;
				
		statusWindow = sWindow;
		this.setLayout(new GridBagLayout());
		
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        
		checkSNET.addActionListener(this);
		checkWFN.addActionListener(this);
		checkOPN.addActionListener(this);
		checkSCCPN.addActionListener(this);
		computeReachabilityGraph.addActionListener(this);
		
	    //String languages[]={"C","C++","C#","Java","PHP"};
	    //selectNode = new JComboBox<>(languages);
	    computeSkeleton.addActionListener(this);
	    reloadNet.addActionListener(this);
		
		checkSNET.setEnabled(true);
		checkWFN.setEnabled(true);
		checkOPN.setEnabled(true);
		checkSCCPN.setEnabled(true);
		computeSkeleton.setEnabled(true);
		// computeSkeleton.setEnabled(false);
		reloadNet.setEnabled(true);
		computeReachabilityGraph.setEnabled(true);
		
		//checkSNET.setEnabled(true);
		//checkWFN.setEnabled(true);
		// comment out
		/*checkOPN.setEnabled(false);
		checkSCCPN.setEnabled(false);
		computeSkeleton.setEnabled(false);
		reloadNet.setEnabled(false);
		computeReachabilityGraph.setEnabled(true);*/
		
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
		this.add(checkOPN, constraints);
		
		constraints.gridx = 3;
		constraints.gridy = 1;
		constraints.weightx = 0.5;
		this.add(checkSCCPN, constraints);
		
		/*constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.weightx = 0.5;
		this.add(selectNode, constraints);*/
		
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.weightx = 0.5;
		this.add(computeSkeleton, constraints);

		constraints.gridx = 1;
		constraints.gridy = 2;
		constraints.weightx = 0.5;
		this.add(reloadNet, constraints);
		
		constraints.gridx = 2;
		constraints.gridy = 2;
		constraints.gridwidth = 2;
		constraints.weightx = 0.5;	
		this.add(computeReachabilityGraph, constraints);
		
		this.setBorder(BorderFactory.createTitledBorder(
							BorderFactory.createEtchedBorder(), "Structural Analysis"));
	}
	
	public void setHeaderTxt(String value) {
		statusWindow.addText("\n ************************************************** \r\n" + 
				"   " + value + "\r\n" + 
				" ************************************************** \n", TextType.HEADER);
	}

	public void setFooterTxt() {
		statusWindow.addText("\n ************************************************** \r\n", TextType.FOOTER);
	}

	
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		if(e.getActionCommand().equals("compute-skeleton")) {
			setHeaderTxt(
					"\n   > Computing Skeleton... \r\n");
			pngui.removeInterfacePlaces();
			graphGen.computeDS(); 
			setHeaderTxt("   > Done. \n");	
		}
		if(e.getActionCommand().equals("reload-net")) {
			setHeaderTxt(
					"\n   > Reloading Net from PNML File... \r\n");
			pngui.reloadPetriNetFromPNMLFile();
			pngui.resetStreamingGraph();
			pngui.computePetriNetGraph();
			setHeaderTxt("   > Done.\n");
		}
		if(e.getActionCommand().equals("reload-graph")) {
			setHeaderTxt(
					"\n   > Reloading Reachability Graph from DGS File... \r\n");
			rgui.resetStreamingGraph();
			rgui.reloadGraphInstanceFromFile();
			setHeaderTxt("   > Done.\n");
		}
		if(e.getActionCommand().equals("compute-reachability-graph")) {
			// get depth of search from user//
            JPanel panel = new JPanel();
            panel.add(new JLabel("Exploration Depth:"));
            SpinnerModel value =  
                    new SpinnerNumberModel(16, //initial value  
                       0, //minimum value  
                       106, //maximum value  
                       2); //step  
            JSpinner spinner = new JSpinner(value);   
           	spinner.setBounds(100,100,50,30);    
                   
            panel.add(spinner);
            panel.setVisible(true);

            int result = JOptionPane.showConfirmDialog(statusWindow, panel, "Depth Selection", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            switch (result) {
                case JOptionPane.OK_OPTION:
                    System.out.println("You selected " + spinner.getValue());
                    setHeaderTxt(
        					"\n   > Generating Reachability Graph, please wait.. \r\n");
        			pngui.generateReachabilityGraph((Integer)spinner.getValue());
        			setHeaderTxt( "   > Finished.\n");
        			
        			// 27.09.2020 Integration of Reachability Graph Analysis with Petri net Analysis.
        			try { Thread.sleep(1000); } catch (InterruptedException e1) { e1.printStackTrace(); } // Lets wait for the file to be ready.
        			new RGGUI("reachabilitygraph.dgs"); //selectFileFromUser()
                    break;
            }
            
            /*try {
				Runtime.getRuntime().exec("java -jar " + "LaunchReachabilityGraphAnalysis.jar");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
            try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
            System.exit(0);*/
		}
		if(e.getActionCommand().equals("Check-SNet")) {
			setHeaderTxt("Property Check for State Machine Net");
			//statusWindow.addText("\n ************************************************** \r\n" + 
			//		"   Property Check for State Machine Net\r\n" + 
			//		" ************************************************** \n" + 
			//statusWindow.addText(
			graphGen.computeStructuralChecks(ValidationType.SNET); // + "\n", TextType.DEFAULT);
			setFooterTxt(); // statusWindow.addText("\n ************************************************** \r\n", TextType.DEFAULT);
		}
		if(e.getActionCommand().equals("Check-WFN")) {
			setHeaderTxt("Property Check for Workflow Net");
			//statusWindow.addText("\n ************************************************** \r\n" + 
			//		"   Property Check for Workflow Net\r\n" + 
			//		" ************************************************** \n" + 
			//statusWindow.addText(
			graphGen.computeStructuralChecks(ValidationType.WFN); // + "\n", TextType.DEFAULT);
			setFooterTxt(); // statusWindow.addText("\n ************************************************** \r\n", TextType.DEFAULT);
		}
		if(e.getActionCommand().equals("Check-OPN")) {
			setHeaderTxt("Property Check for OPN and Portnet");
			//statusWindow.addText("\n ************************************************** \r\n" + 
			//		"   Property Check for OPN and Portnet\r\n" + 
			//		" ************************************************** \n" + 
			//statusWindow.addText(graphGen.computeStructuralChecks(ValidationType.OPN) + "\n", TextType.DEFAULT);
			graphGen.computeStructuralChecks(ValidationType.OPN);
			setFooterTxt(); // statusWindow.addText("\n ************************************************** \r\n", TextType.DEFAULT);
		}
		// this is a behavioral property
		if(e.getActionCommand().equals("Check-WT")) {
			setHeaderTxt("Property Check for Weak Termination");
			//statusWindow.addText("\n ************************************************** \r\n" + 
			//		"   Property Check for Weak Termination \r\n" + 
			//		" ************************************************** \n" + 
			//statusWindow.addText(
			graphGen.computeStructuralChecks(ValidationType.WT); //+ "\n", TextType.DEFAULT);
			setFooterTxt(); // statusWindow.addText("\n ************************************************** \r\n", TextType.DEFAULT);
		}
		if(e.getActionCommand().equals("Check-SCC-PN")) { 
			setHeaderTxt("Property Check for Strongly Connected Components");
			//statusWindow.addText("\n ************************************************** \r\n" + 
			//		"   Property Check for Strongly Connected Components \r\n" + 
			//		" ************************************************** \n" + 
			//statusWindow.addText(
			graphGen.computeStructuralChecks(ValidationType.SCC_PN); // + "\n", TextType.DEFAULT);
			setFooterTxt(); // statusWindow.addText("\n ************************************************** \r\n", TextType.DEFAULT);
		}
		if(e.getActionCommand().equals("Check-SCC-RG")) { 
			setHeaderTxt("Property Check for Strongly Connected Components");
			//statusWindow.addText("\n ************************************************** \r\n" + 
			//		"   Property Check for Strongly Connected Components \r\n" + 
			//		" ************************************************** \n" + 
			//statusWindow.addText(
			graphGen.computeStructuralChecks(ValidationType.SCC_RG); // + "\n", TextType.DEFAULT);
			setFooterTxt(); // statusWindow.addText("\n ************************************************** \r\n", TextType.DEFAULT);
		}
	}
}