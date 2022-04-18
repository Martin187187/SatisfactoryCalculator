package machinelearning;

import controller.DataController;
import controller.Network;
import model.Item;
import model.NetworkNode;
import model.Recipe;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class MachineLearningText {

    public static void main(String[] args) {


        DataController controller = new DataController();
        List<Item> itemList = controller.getItemList();
        Item water = itemList.stream().filter(x -> x.getClassName().equals("Desc_Water_C")).findFirst().get();
        water.setSinkPoints(0);

        Item item = new Item("Desc_Computer_C", null, null, 1);

        Map<Item, Float> rawResources = controller.getRawItems();

        TreeMap<Float, Network> networkTreeMap = new TreeMap<>();
        for(int i = 0; i < 100; i++) {
            Network network = new Network(controller.createNetworkNodes());
            float value = network.calculateValue(item, rawResources, false);

            networkTreeMap.put(value, network);
        }

        Network result;
        while(true) {
            System.out.println(networkTreeMap.lastKey());

            while (networkTreeMap.entrySet().size()>50) {
                networkTreeMap.pollFirstEntry();
            }
            TreeMap<Float, Network> newTreeMap = new TreeMap<>();
            List<Network> networkList = new ArrayList<>(networkTreeMap.values());

            for (int i = 0; i < networkList.size(); i++) {
                Network n2 = networkList.get(i).createNewNode(1-i/50f);
                newTreeMap.put(networkList.get(i).calculateValue(item, rawResources, false), networkList.get(i));
                newTreeMap.put(n2.calculateValue(item, rawResources, false), n2);
            }
            networkTreeMap = newTreeMap;
            if(networkTreeMap.lastKey()>280f){
                result = networkTreeMap.lastEntry().getValue();
                break;
            }
        }
        result.calculateValue(item, rawResources, true);
        NetworkNode test = result.getNetworkNodeList().stream().filter(x -> x.getItem().equals(item)).findFirst().get();
        System.out.println("___");
        System.out.println(test);
    }
}
