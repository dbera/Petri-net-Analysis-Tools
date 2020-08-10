package org.eclipse.petrinets.graphs;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import org.graphstream.ui.view.View;

public class KeyBoardHandler implements KeyListener {
	View view;
	double zoomSteps = 0.2;
	double baseZoomLevel = 1.0;
	double currentZoomLevel = 1.0;

	public KeyBoardHandler(View v) {
		view = v;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// just use page-up|down and arrow keys to pan!
		System.out.println("Key Pressed: "+e.getKeyChar());
		if(e.getKeyChar() == 'a') { 
			currentZoomLevel += zoomSteps;
			view.getCamera().setViewPercent(currentZoomLevel);
		}
		if(e.getKeyChar() == 'z') { 
			currentZoomLevel -= zoomSteps;
			view.getCamera().setViewPercent(currentZoomLevel);
		}
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

}
