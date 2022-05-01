package view;

import controller.LearningController;
import controller.Network;
import controller.Subject;
import model.Item;
import model.NetworkNode;
import model.Recipe;
import org.apache.commons.lang3.tuple.Pair;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.layout.Eades84Layout;
import org.graphstream.ui.layout.HierarchicalLayout;
import org.graphstream.ui.layout.Layout;
import org.graphstream.ui.layout.springbox.implementations.LinLog;
import org.graphstream.ui.layout.springbox.implementations.SpringBox;
import org.graphstream.ui.swingViewer.View;
import org.graphstream.ui.swingViewer.Viewer;
import org.graphstream.ui.swingViewer.ViewerListener;
import org.graphstream.ui.swingViewer.ViewerPipe;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.security.Key;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MainView extends JFrame implements Observer{
    private static final String STYLE =
            "node {fill-color: grey; z-index: 0; size: 20;size-mode: fit; shape: box; } node.item {fill-color: green;} edge{shape: freeplane;}";

    private LearningController controller;
    private Graph graph;
    private Viewer viewer;
    private View view;

    private JLabel label;
    private float zoom = 1f;

    private float x = 0f;
    private float y = 0f;
    public MainView(LearningController controller){
        this.controller = controller;
        setLayout(new BorderLayout());
        setTitle("Satisfactory Calculator");
        setSize(720,480);

        System.setProperty("gs.ui.renderer",
                "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        graph = new MultiGraph("embedded");
        Layout layout = new SpringBox(false);
        graph.addSink(layout);
        graph.setAttribute("ui.quality");
        graph.setAttribute("ui.antialias");
        graph.setAttribute("ui.stylesheet", STYLE);
        graph.setAutoCreate(true);
        graph.setStrict(false);
        viewer = new Viewer(graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        viewer.disableAutoLayout();
        view = viewer.addDefaultView(false);
        view.setFocusable(false);
        view.resizeFrame(800, 600);
        view.getCamera().setViewCenter(x, y, 0);
        view.getCamera().setViewPercent(zoom);

        label = new JLabel("score: "+ 0);
        add(label, BorderLayout.SOUTH);
        add(view, BorderLayout.CENTER);
        calculateGraph();

        ViewerPipe fromViewer = viewer.newViewerPipe();
        fromViewer.addViewerListener(new NodeClickListener(fromViewer, view, graph, controller));

        viewer.enableAutoLayout();


        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_O){
                    viewer.disableAutoLayout();
                    calculateGraph();
                    viewer.enableAutoLayout();
                }
                if(e.getKeyCode() == KeyEvent.VK_PLUS){
                    zoom*=0.8f;
                    view.getCamera().setViewPercent(zoom);
                }
                if(e.getKeyCode() == KeyEvent.VK_MINUS){
                    zoom*=1.25f;
                    view.getCamera().setViewPercent(zoom);
                }
                if(e.getKeyCode() == KeyEvent.VK_S){


                    try (PrintWriter out = new PrintWriter(new FileWriter("savefiles/test.json"))) {
                        out.write(controller.getResult().toJSONString());
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
                if(e.getKeyCode() == KeyEvent.VK_P){
                    controller.printResult();
                }
                if(e.getKeyCode() == KeyEvent.VK_R){
                    controller.restart = true;
                }
                if(e.getKeyCode() == KeyEvent.VK_Q){
                    NetworkNode networkNode = controller.getResult().findItemInNetwork(new Item("Desc_Computer_C",null, null, 0));
                    System.out.println(networkNode.getWeightList());
                    controller.feed=true;
                    controller.data = controller.getResult();
                    viewer.disableAutoLayout();
                    calculateGraph();
                    viewer.enableAutoLayout();
                }
                if(e.getKeyCode() == KeyEvent.VK_UP){
                    y+=2*zoom;
                    view.getCamera().setViewCenter(x, y, 0);
                }
                if(e.getKeyCode() == KeyEvent.VK_DOWN){
                    y-=2*zoom;
                    view.getCamera().setViewCenter(x, y, 0);
                }
                if(e.getKeyCode() == KeyEvent.VK_RIGHT){
                    x+=2*zoom;
                    view.getCamera().setViewCenter(x, y, 0);
                }
                if(e.getKeyCode() == KeyEvent.VK_LEFT){
                    x-=2*zoom;
                    view.getCamera().setViewCenter(x, y, 0);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });



    }

    private void calculateGraph(){
        Map<NetworkNode, Float> amountUsedMap = controller.getResult().getAmountUsedMap();
        for(Map.Entry<NetworkNode, Float> entry: amountUsedMap.entrySet()){
            String itemKey = entry.getKey().getItem().getClassName();
            Node graphNode =graph.addNode(itemKey);
            graphNode.setAttribute("ui.label", entry.getKey().getItem().getName());
            graphNode.setAttribute("ui.class", "item");
            List<Recipe> recipeList = entry.getKey().getRecipeList();
            List<Float> weightList = entry.getKey().getWeightList();
            for(int i = 0; i < recipeList.size(); i++){
                String recipeKey = recipeList.get(i).getClassName();
                Node recipeNode = graph.addNode(recipeKey);
                recipeNode.setAttribute("ui.label", recipeList.get(i).getName());
                    Edge edge = graph.addEdge(recipeKey + itemKey, recipeKey, itemKey, true);
                    edge.setAttribute("ui.label", weightList.get(i));
                if(weightList.get(i)==0||entry.getValue()==0)
                    graph.removeEdge(edge);

            }
        }

        for(Map.Entry<NetworkNode, Float> entry: amountUsedMap.entrySet()){
            List<Recipe> recipeList = entry.getKey().getRecipeList();
            List<Float> weightList = entry.getKey().getWeightList();
            for(int i = 0; i < recipeList.size(); i++){
                String recipeKey = recipeList.get(i).getClassName();
                float amount = 1f;
                for(Pair<Item, Float> itemFloatPair: recipeList.get(i).getProducts()){
                    if(itemFloatPair.getLeft().equals(entry.getKey().getItem())){
                        amount = itemFloatPair.getRight();
                    }
                }
                for(Pair<Item, Float> itemFloatPair: recipeList.get(i).getIngredients()){
                    if(!itemFloatPair.getLeft().isRawMaterial()) {
                        String itemKey = itemFloatPair.getKey().getClassName();
                        Edge edge = graph.addEdge(itemKey+recipeKey, itemKey, recipeKey,true);
                        float weight = (entry.getValue()*weightList.get(i)/amount)*itemFloatPair.getValue();
                        edge.setAttribute("ui.label", weight);
                        edge.setAttribute("ui.style", "fill-color: red;");
                        if(weight==0)
                            graph.removeEdge(edge);
                    }
                }

                for(Pair<Item, Float> itemFloatPair: recipeList.get(i).getProducts()){
                    if(!itemFloatPair.getLeft().equals(entry.getKey().getItem())){
                        if(!itemFloatPair.getLeft().isRawMaterial()) {
                            String itemKey = itemFloatPair.getKey().getClassName();
                            Edge edge = graph.addEdge(recipeKey+itemKey+"öpö", recipeKey, itemKey,true);
                            float weight = (entry.getValue()*weightList.get(i)/amount)*itemFloatPair.getValue();
                            edge.setAttribute("ui.label", "                    !"+weight);
                            edge.setAttribute("ui.style", "fill-color: blue;");
                            if(weight==0)
                                graph.removeEdge(edge);
                        }
                    }
                }
            }
        }

    }

    @Override
    public void update(Subject sub) {
        if(sub instanceof LearningController){
            label.setText("score: " + ((LearningController) sub).getScore());
        }
    }
}
