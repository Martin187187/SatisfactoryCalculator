package controller;

import model.Item;
import model.NetworkNode;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class LearningController extends Thread{

    private boolean loop = true;
    public void run(){
        loop = true;
        DataController controller = new DataController();
        List<Item> itemList = controller.getItemList();
        Item water = itemList.stream().filter(x -> x.getClassName().equals("Desc_Water_C")).findFirst().get();
        water.setSinkPoints(0);

        Item item = new Item("Desc_SpaceElevatorPart_7_C", null, null, 1);
        System.out.println(item);



        List<Pair<Item, Float>> hasToProduce = new LinkedList<>();
        Item item2 = new Item("Desc_NuclearFuelRod_C", null, null, 1);
        hasToProduce.add(new ImmutablePair<>(item2, 50.4f));


        Item item3 = new Item("Desc_Silica_C", null, null, 1);
        hasToProduce.add(new ImmutablePair<>(item3, 1260f));
        Item item4 = new Item("Desc_NitricAcid_C", null, null, 1);
        hasToProduce.add(new ImmutablePair<>(item4, 756000f));
        Item item5 = new Item("Desc_SulfuricAcid_C", null, null, 1);
        hasToProduce.add(new ImmutablePair<>(item5, 756000f));
        Item item6 = new Item("Desc_Cement_C", null, null, 1);
        hasToProduce.add(new ImmutablePair<>(item6, 1512f));
        Item item7 = new Item("Desc_SteelPlate_C", null, null, 1);
        hasToProduce.add(new ImmutablePair<>(item7, 226.8f));
        Item item8 = new Item("Desc_ElectromagneticControlRod_C", null, null, 1);
        hasToProduce.add(new ImmutablePair<>(item8, 75.6f));
        Item item9 = new Item("Desc_AluminumPlateReinforced_C", null, null, 1);
        hasToProduce.add(new ImmutablePair<>(item9, 236f));






        Map<Item, Float> rawResources = controller.getRawItems();

        TreeMap<Float, Network> networkTreeMap = new TreeMap<>();
        for(int i = 0; i < 100; i++) {
            Network network = new Network(controller.createNetworkNodes());
            float value = network.calculateValue(item, hasToProduce, rawResources, false);

            networkTreeMap.put(value, network);
        }

        Network result = null;

        while(loop) {
            System.out.println(networkTreeMap.lastKey());

            while (networkTreeMap.entrySet().size()>50) {
                networkTreeMap.pollFirstEntry();


            }
            Map<Float, Network> newTreeMap = Collections.synchronizedMap(new HashMap<>());
            Set<Map.Entry<Float, Network>> set = networkTreeMap.entrySet();
            Stream<Map.Entry<Float, Network>> stream = StreamSupport.stream(set.spliterator(), true);
            stream.forEach(entry -> {
                Network network = entry.getValue();
                Network n2 = network.createNewNode();
                newTreeMap.put(network.calculateValue(item, hasToProduce, rawResources, false), network);
                newTreeMap.put(n2.calculateValue(item, hasToProduce, rawResources, false), n2);
            });

            networkTreeMap.clear();
            networkTreeMap.putAll(newTreeMap);
            result = networkTreeMap.lastEntry().getValue();
        }
        result.calculateValue(item, hasToProduce, rawResources, true);
        System.out.println("___");
        System.out.println(result);
    }

    public void exit(){
        loop = false;
    }
    public boolean isLoop(){
        return loop;
    }
}
