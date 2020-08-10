

import java.io.IOException;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceDGS;

public class DemoViewer {

	public static void main(String[] args) {
		Graph graph = new SingleGraph("Demo Graph");
		graph.display();
		FileSource source = new FileSourceDGS();
		source.addSink( graph );
		try {
			source.begin("demo.dgs");

			while( source.nextEvents() ){
			  // Do whatever between two events
				sleep();
			}
			source.end();
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected static void sleep() {
		try { Thread.sleep(1000); } catch (Exception e) {}
	}

}
