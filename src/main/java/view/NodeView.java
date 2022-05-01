package view;

import controller.LearningController;
import controller.Network;
import model.Item;
import model.NetworkNode;
import model.Recipe;
import org.graphstream.graph.Node;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.List;

public class NodeView extends JFrame {

    public NodeView(Node node, LearningController controller){
        setTitle(node.getId());
        setLayout(new FlowLayout());
        setSize(320, 240);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
        NetworkNode networkNode = controller.getResult().findItemInNetwork(new Item(node.getId(),null, null, 0));

        if(networkNode!=null) {
            List<Recipe> recipeList = networkNode.getRecipeList();
            List<Float> weightList = networkNode.getWeightList();
            JPanel panel = new JPanel();
            GridLayout layout = new GridLayout(recipeList.size(), 2);
            panel.setLayout(layout);

            JTextField[] fields = new JTextField[recipeList.size()];
            for (int i = 0; i < recipeList.size(); i++) {

                JLabel label = new JLabel(recipeList.get(i).getName());
                fields[i] = new JTextField(Float.toString(weightList.get(i)));

                panel.add(label);
                panel.add(fields[i]);
            }
            add(panel);

            JButton button = new JButton("save");
            button.addActionListener(e -> {
                List<Float> result = new LinkedList<>();
                for (JTextField field : fields) {
                    result.add(Float.parseFloat(field.getText()));
                }
                networkNode.setWeightList(result);
            });
            add(button);

        }
    }
}
