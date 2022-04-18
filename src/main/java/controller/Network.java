package controller;

import model.Item;
import model.NetworkNode;
import model.Recipe;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class Network {


    private List<NetworkNode> networkNodeList;

    public Network(List<NetworkNode> networkNodeList){
        this.networkNodeList = networkNodeList;

    }

    // TODO: item should be a list
    public float calculateValue(Item item, Map<Item, Float> resources, boolean print){


        Map<NetworkNode, Float> workerQueue = new HashMap<>();
        Map<NetworkNode, Float> rawResources = new HashMap<>();

        NetworkNode node = findItemInNetwork(item);
        workerQueue.put(node, 1f);

        while(!workerQueue.isEmpty()){
            NetworkNode networkNode = workerQueue.keySet().iterator().next();
            float n = workerQueue.get(networkNode);
            workerQueue.remove(networkNode);

            List<Recipe> recipes = networkNode.getRecipeList();
            List<Float> weights = networkNode.getWeightList();
            for(int i = 0; i < recipes.size(); i++){

                Recipe recipe = recipes.get(i);
                float weight = weights.get(i);

                float productAmount = 1;
                for(Pair<Item, Integer> itemIntegerPair: recipe.getProducts()){
                    if(itemIntegerPair.getLeft().equals(networkNode.getItem())){
                        productAmount = itemIntegerPair.getRight();
                    }
                }
                for(Pair<Item, Integer> itemIntegerPair: recipe.getIngredients()){

                    Item currentItem = itemIntegerPair.getLeft();
                    float amount = itemIntegerPair.getRight();

                    NetworkNode currentNode = findItemInNetwork(currentItem);

                    if(itemIntegerPair.getLeft().isRawMaterial()){

                        if(rawResources.containsKey(currentNode)){
                            float currentAmount = rawResources.get(currentNode);
                            rawResources.put(currentNode, currentAmount + n*amount*weight/productAmount);
                        } else {
                            rawResources.put(currentNode, n*amount*weight/productAmount);
                        }

                    }else {
                        if(workerQueue.containsKey(currentNode)){
                            float currentAmount = workerQueue.get(currentNode);
                            workerQueue.put(currentNode, currentAmount + n*amount*weight/productAmount);
                        } else {
                            workerQueue.put(currentNode, n*amount*weight/productAmount);
                        }
                    }
                }
            }
        }

        float result = Float.MAX_VALUE;
        NetworkNode best = null;
        for(Map.Entry<NetworkNode, Float> entry: rawResources.entrySet()){
            Item i = entry.getKey().getItem();


            if(resources.containsKey(i)){
                float val = resources.get(i)/entry.getValue();

                if(val<result) {
                    result = val;
                    best = entry.getKey();
                }

                if(print){
                    System.out.println("val: "+ val + "->"+entry.getKey());
                }
            }
        }


        return result;
    }

    public NetworkNode findItemInNetwork(Item item){
        return findItemInList(item, networkNodeList);
    }

    private NetworkNode findItemInList(Item item, List<NetworkNode> list){
        for(NetworkNode node: list){
            if(node.getItem().equals(item))
                return node;
        }
        return null;
    }

    public Network createNewNode(float i){
        Random rdm = new Random();
        float r = rdm.nextFloat();

        List<NetworkNode> nodeList = new LinkedList<>();
        for(NetworkNode node: networkNodeList){

            List<Float> weightList = new LinkedList<>();
            float sum = 0;

            for(Float weight: node.getWeightList()){
                float newWeight = rdm.nextBoolean() ? weight - r : weight + r;
                newWeight = Math.max(0, Math.min(1, newWeight));
                sum+=newWeight;
                weightList.add(newWeight);
            }
            List<Float> weightList2 = new LinkedList<>();
            for(Float weight: weightList){
                float newWeight;
                if(sum==0){
                    newWeight = 1f/weightList.size();
                } else {

                    newWeight = weight/sum;
                }
                weightList2.add(newWeight);
            }
            NetworkNode newNode = new NetworkNode(node.getItem(), node.getRecipeList(), weightList2);
            nodeList.add(newNode);
        }
        return new Network(nodeList);
    }

    public List<NetworkNode> getNetworkNodeList() {
        return networkNodeList;
    }

    @Override
    public String toString() {
        return "Network{" +
                "networkNodeList=" + networkNodeList +
                '}';
    }
}
