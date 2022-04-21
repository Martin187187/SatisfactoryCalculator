package machinelearning;

import controller.DataController;
import controller.LearningController;
import controller.Network;
import model.Item;
import model.NetworkNode;
import model.Recipe;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class MachineLearningText {
    static LearningController learner = new LearningController();
    public static void main(String[] args) {

        JFrame ablak = new JFrame("Snake game");
        ablak.setVisible(true);
        ablak.setSize(new Dimension(600,600));
        ablak.setFocusable(true);
        ablak.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ablak.addKeyListener(new KeyListener(){
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_UP && learner.isLoop()) {
                    learner.exit();
                }
                if(e.getKeyCode() == KeyEvent.VK_R){
                    learner.exit();
                    learner = new LearningController();
                    learner.start();
                }

            }

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
        ablak.setVisible(true);
        learner.start();

    }

}
