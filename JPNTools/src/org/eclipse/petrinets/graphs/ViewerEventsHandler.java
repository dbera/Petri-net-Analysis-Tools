package org.eclipse.petrinets.graphs;

import org.graphstream.graph.Graph;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;

public class ViewerEventsHandler implements ViewerListener {

	ViewerPipe fromViewer;
	
	public ViewerEventsHandler(Viewer viewer, Graph graph) {
		fromViewer = viewer.newViewerPipe();
		fromViewer.addViewerListener(this);
		fromViewer.addSink(graph);
	}
	
	@Override
	public void buttonPushed(String id) {
		System.out.println("Button pushed on node "+id);
		
	}

	@Override
	public void buttonReleased(String id) {
		System.out.println("Button released on node "+id);
		
	}

	@Override
	public void viewClosed(String id) {
		System.out.println("View Closed "+id);
		
	}

}
