package machinelearning;

import controller.DataController;
import controller.Network;
import model.Item;
import model.Recipe;
import org.apache.commons.lang3.tuple.Pair;

import java.util.LinkedList;
import java.util.List;

public class MachineLearningText {

    public static void main(String[] args) {


        DataController controller = new DataController();
        List<Item> itemList = controller.getItemList();
        Item water = itemList.stream().filter(x -> x.getClassName().equals("Desc_Water_C")).findFirst().get();
        water.setSinkPoints(0);
        Network network = new Network(controller.createNetworkNodes());

        Item item = new Item("Desc_SpaceElevatorPart_7_C", null, null, 1);

        List<Pair<Item, Float>> rawResources = controller.getRawItems();

        network.calculateValue(item, rawResources);
    }
}
