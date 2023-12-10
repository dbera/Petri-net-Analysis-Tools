package org.eclipse.petrinets.launch;

import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.eclipse.petrinets.gui.RGGUI;

public class LaunchReachabilityGraphAnalysis {

	static void setGUILookAndFeel() {
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel"); 
		} catch (Exception e) { 
			System.err.println("Error: " + e.getMessage());
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			} catch (InstantiationException e1) {
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				e1.printStackTrace();
			} catch (UnsupportedLookAndFeelException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	static void displaySS() {
	      JWindow window = new JWindow();
	        window.getContentPane().add(
	        		new JLabel("", new ImageIcon(LaunchReachabilityGraphAnalysis.class.getResource("/resources/pnat.png")),  SwingConstants.CENTER));
	        window.setBounds(500, 150, 340, 145);
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
		fc.setFileFilter(new FileNameExtensionFilter("DGS File (*.dgs)", "dgs"));
		fc.setDialogTitle("Select a Reachability Graph..");
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
	
	public static void main(String[] args) {	
		setGUILookAndFeel();
		// displaySS();
		String path = "reachability_graph.dgs";
		new RGGUI(path);
		//new RGGUI(selectFileFromUser());
	}
}