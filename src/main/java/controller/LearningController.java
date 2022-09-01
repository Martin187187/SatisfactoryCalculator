package controller;

import model.Item;
import model.NetworkNode;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class LearningController extends Subject{

    private int counter = 0;
    public boolean restart = false;
    public boolean running = false;
    public boolean feed = false;

    public Network data = null;
    private Network result = null;
    private final TreeMap<Float, Network> networkTreeMap = new TreeMap<>();
    //Item list
    private final List<Pair<Item, Float>> item = List.of(
            new ImmutablePair<>(new Item("Desc_SpaceElevatorPart_7_C", null, null, 1),1f)
    );





    public final List<Pair<Item, Float>> hasToProduce = new LinkedList<>();
    private final Map<Item, Float> rawResources;
    private DataController controller = new DataController();
    public LearningController(DataController dataController, boolean createNew) {
        this.controller = dataController;
        List<Item> itemList = controller.getItemList();
        Item water = itemList.stream().filter(x -> x.getClassName().equals("Desc_Water_C")).findFirst().get();
        water.setSinkPoints(0);



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






        rawResources = controller.getRawItems();

        if(createNew){
            restart();
        } else {
            Network loadedNetwork = controller.loadNetworkFromFile();
            feed(loadedNetwork);
        }
    }

    public void feed(Network network){
        float loadedValue = network.calculateValue(item, hasToProduce, rawResources, false);

        networkTreeMap.clear();
        for (int i = 0; i < 99; i++) {

            Network n = network.createNewNode();
            networkTreeMap.put(n.calculateValue(item, hasToProduce, rawResources, false), n);
        }
        networkTreeMap.put(loadedValue, network);
        result = networkTreeMap.lastEntry().getValue();
        result.calculateValue(item, hasToProduce, rawResources, true);
        notifyObservers();
    }

    public void restart(){
        networkTreeMap.clear();
        for(int i = 0; i < 100; i++) {
            Network network = new Network(controller.createNetworkNodes());
            float value = network.calculateValue(item, hasToProduce, rawResources, false);

            networkTreeMap.put(value, network);
        }
        result = networkTreeMap.lastEntry().getValue();
    }


    public void calculate(){
        System.out.println(counter);
        if (restart) {
            counter = 0;
            restart = false;
            restart();
        }
        if(feed) {
            counter = 0;
            feed = false;
            feed(data);
        }
        while (networkTreeMap.entrySet().size() > 50) {
            networkTreeMap.pollFirstEntry();
        }
        Map<Float, Network> newTreeMap = Collections.synchronizedMap(new HashMap<>());
        Set<Map.Entry<Float, Network>> set = networkTreeMap.entrySet();
        Stream<Map.Entry<Float, Network>> stream = set.parallelStream();
        stream.forEach(entry -> {
            Network network = entry.getValue();
            Network n2 = network.createNewNode();
            newTreeMap.put(network.calculateValue(item, hasToProduce, rawResources, false), network);
            newTreeMap.put(n2.calculateValue(item, hasToProduce, rawResources, false), n2);
        });

        networkTreeMap.clear();
        networkTreeMap.putAll(newTreeMap);
        result = networkTreeMap.lastEntry().getValue();
        counter++;
        if(counter>1000 && networkTreeMap.lastEntry().getKey()<282f) {
            restart();
            counter = 0;
        }

        if(counter>10000000){
            try (PrintWriter out = new PrintWriter(new FileWriter("savefiles/good"+networkTreeMap.lastEntry().getKey()+".json"))) {
                out.write(getResult().toJSONString());
            } catch (Exception e2) {
                e2.printStackTrace();
            }

            restart();
            counter = 0;
        }

        notifyObservers();
    }



    public void printResult(){
        synchronized (result) {
            result.calculateValue(item, hasToProduce, rawResources, true);
            System.out.println("___");
            System.out.println(result);
        }
    }

    public float getScore(){
        synchronized (result){
            return result.calculateValue(item, hasToProduce, rawResources, false);
        }
    }
    public Network getResult(){
        synchronized (result){
            return result;
        }
    }
}
