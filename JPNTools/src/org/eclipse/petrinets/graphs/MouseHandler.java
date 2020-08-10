package org.eclipse.petrinets.graphs;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Random;

import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.view.View;

public class MouseHandler  implements MouseListener, MouseWheelListener  {
	
	View view;
	
	MouseHandler(View v) {
		view = v;
	}
	
	@Override
	public void mouseClicked(MouseEvent me) {
		
		GraphicElement element = view.findNodeOrSpriteAt(me.getX(), me.getY());
        if(element != null){
            //Random r = new Random();
            //element.setAttribute("ui.style", "fill-color: rgb("+r.nextInt(256)+","+r.nextInt(256)+","+r.nextInt(256)+");");
            System.out.println("Mouse clicked on Node: " + element.getLabel() + " ID: " + element.getId());
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
