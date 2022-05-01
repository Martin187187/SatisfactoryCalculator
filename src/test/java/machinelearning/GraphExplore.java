package machinelearning;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.swingViewer.Viewer;

import java.util.Iterator;

public class GraphExplore {
    public static void main(String args[]) {
        new GraphExplore();
    }

    public GraphExplore() {
        Graph graph = new SingleGraph("tutorial 1");

        graph.setAttribute("ui.stylesheet", styleSheet);
        graph.setAutoCreate(true);
        graph.setStrict(false);
        Viewer viewer = graph.display(true);


        graph.addEdge("AB", "A", "B");
        graph.addEdge("BC", "B", "C");
        graph.addEdge("CA", "C", "A");
        graph.addEdge("AD", "A", "D");
        graph.addEdge("DE", "D", "E");
        graph.addEdge("DF", "D", "F");
        graph.addEdge("EF", "E", "F");

        for (Node node : graph) {
            node.setAttribute("ui.label", node.getId());
        }
        for (Edge edge: graph.getEachEdge()){
            edge.setAttribute("ui.style", "size: 5px;");
        }

        explore(graph.getNode("A"));
    }

    public void explore(Node source) {
        Iterator<? extends Node> k = source.getBreadthFirstIterator();

        while (k.hasNext()) {
            Node next = k.next();
            next.setAttribute("ui.class", "a");
            sleep();
        }
    }

    protected void sleep() {
        try { Thread.sleep(1000); } catch (Exception e) {}
    }

    protected String styleSheet =
            "node {fill-color: black;} node.a {fill-color: red;} edge{fill-color: red;}";
}