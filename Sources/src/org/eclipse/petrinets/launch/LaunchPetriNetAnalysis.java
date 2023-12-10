package org.eclipse.petrinets.launch;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.FileDialog;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

import org.eclipse.petrinets.PNMLParser;
import org.eclipse.petrinets.gui.PNGUI;

public class LaunchPetriNetAnalysis {
	
	static void setGUILookAndFeel() {
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel"); 
			// com.sun.java.swing.plaf.windows.WindowsLookAndFeel"); 
			// "javax.swing.plaf.metal.MetalLookAndFeel"
			// UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		} catch (Exception e) { 
			System.err.println("Error: " + e.getMessage());
			try {
				// fall back option
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InstantiationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (UnsupportedLookAndFeelException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	static void displaySS() {
	      JWindow window = new JWindow();
	      window.getContentPane().setBackground(new Color(233,233,233));
	        window.getContentPane().add(
	        		new JLabel("", new ImageIcon(LaunchPetriNetAnalysis.class.getResource("/resources/pnat.png")),  SwingConstants.CENTER));
	        window.setBounds(500, 150, 331, 138); // 331, 138
	        window.setVisible(true);
	        try {
	            Thread.sleep(2000);
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
	        window.setVisible(false);
	        window.dispose();
	}
	
	static String selectFileFromUser() {
		String FILE_PATH = new String();
		JFileChooser fc = new JFileChooser(".\\"); //FileSystemView.getFileSystemView().getHomeDirectory());
		fc.setFileFilter(new FileNameExtensionFilter("Yasper File (*.pnml)", "pnml"));
		fc.setDialogTitle("Select a Yasper File..");
		fc.setAcceptAllFileFilterUsed(false);
		fc.setVisible(true);
		int returnValue = fc.showOpenDialog(null);
		if(returnValue == JFileChooser.CANCEL_OPTION) System.exit(0);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fc.getSelectedFile();
			System.out.println(selectedFile.getAbsolutePath());
			FILE_PATH = selectedFile.getAbsolutePath();
		}
		return FILE_PATH;
	}
	
	public static void main(String[] args) 
	{
		// TODO get from command line arguments!
		// String FILE_PATH = "C:\\Users\\berad\\Desktop\\JavaWorkspace2020\\wrkspace\\SimpleGraph\\src\\nl\\esi\\petrinets\\yasprFile.pnml";
		// String FILE_PATH = "C:\\Users\\berad\\Desktop\\JavaWorkspace2020\\JDK14WorkSpace\\JPNTools\\src\\org\\eclipse\\petrinets\\modelingProblem.pnml";
		// String FILE_PATH = "C:\\Users\\berad\\Desktop\\JavaWorkspace2020\\JDK14WorkSpace\\JPNTools\\src\\org\\eclipse\\petrinets\\yasprFile.pnml";
		// String FILE_PATH = "RaceConditionMirrored.pnml";
		// String FILE_PATH = "workshopDemoModel.pnml";
		// String FILE_PATH = "PN_MODEL.pnml";
		// String FILE_PATH = "yasprFile.pnml";
		
		setGUILookAndFeel();
		displaySS();
		String FILE_PATH = selectFileFromUser();
				
		//new java.awt.FileDialog((java.awt.Frame) null).setVisible(true);
		/*
		 FileDialog dialog = new FileDialog((java.awt.Frame) null, "Select a Yasper File");
		// dialog.Filter =  "Image files (*.jpg, *.jpeg, *.jpe, *.jfif, *.png) | *.jpg; *.jpeg; *.jpe; *.jfif; *.png";
	    dialog.setMode(FileDialog.LOAD);
	    dialog.setVisible(true);
	    dialog.setFile("*.pnml;*.pnml");
	    String FILE_PATH = dialog.getFile();
	    System.out.println(FILE_PATH + " was chosen by the user. Loading... ");
	    */
	    
	    /*pngui.loadPetriNetFromPNMLFile(file);
		pngui.resetStreamingGraph();
		pngui.computePetriNetGraph();*/
		
		PNMLParser netParser = new PNMLParser(FILE_PATH);
		// Compute locally the data structures representing Yasper Petri net
		netParser.displayParsedContent();
		netParser.generatePetriNetSyntax();

		// Display Structural Analysis Results GUI + Visualization using Graph Stream
		// Changed displayGraph boolean to false. No one is using the Petri nets. Changed on 27.09.2020
		PNGUI gui = new PNGUI(netParser, FILE_PATH, false, false); // launches visualization // FILE_PATH is needed to reload from PNML file
		//gui.resetStreamingGraph();
		gui.computePetriNetGraph(); // constructs the graph.
		
		// gui.analyzePetriNetStructure(); // This should be called on demand by the buttons

		// get the net object
		// INet netObj = netParser.getNetObject();
		
		/*GraphLogger.getInstance().setBrScnGen(true);
		//GraphLogger.getInstance().addToBrLogs("\nRoot Node: N0\n");
		(new Walker(netParser.getNetObject())).generateRG(netParser.getNetObject().getCurrentState(), 0);
		GraphLogger.getInstance().writeToBrScnFormat();
		GraphLogger.getInstance().setBrScnGen(false);*/
				
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