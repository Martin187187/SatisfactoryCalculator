package controller;

import model.Item;
import model.NetworkNode;
import model.Recipe;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONArray;
import org.json.JSONString;

import java.util.*;

public class Network implements JSONString {


    private final List<NetworkNode> networkNodeList;
    private final Map<NetworkNode, Float> amountUsedMap = new HashMap<>();

    public Network(List<NetworkNode> networkNodeList){
        this.networkNodeList = networkNodeList;
    }

    // TODO: item should be a list
    public float calculateValue(Item item, List<Pair<Item, Float>> hasToProduce, Map<Item, Float> resources, boolean print){

        Map<NetworkNode, Float> availableResources = new HashMap<>();
        Map<NetworkNode, Float> rawResourcesMandatory = new HashMap<>();
        Map<NetworkNode, Float> amountUsedMap = new HashMap<>();
        for(Pair<Item, Float> itemFloatPair: hasToProduce){
            Map<NetworkNode, Float> bla = calculateResourceConsumption(itemFloatPair, availableResources, amountUsedMap);

            for(Map.Entry<NetworkNode, Float> entry: bla.entrySet()){
                if(rawResourcesMandatory.containsKey(entry.getKey())){
                    rawResourcesMandatory.put(entry.getKey(), entry.getValue() +rawResourcesMandatory.get(entry.getKey()));
                } else {
                    rawResourcesMandatory.put(entry.getKey(), entry.getValue());
                }
            }

        }
        if(print)
            System.out.println("raw"+rawResourcesMandatory);
        Map<NetworkNode, Float> amountUsedScaleMap = new HashMap<>();

        if(print){
            System.out.println("wasted resources1: "+ availableResources);
        }
        availableResources.clear();
        Map<NetworkNode, Float> rawResourcesOptional = calculateResourceConsumption(new ImmutablePair<>(item, 1f), availableResources, amountUsedScaleMap);



        if(print){
            System.out.println("wasted resources2: "+ availableResources);
        }
        //remove mandatory resources
        if(print)
        System.out.println("before"+resources);

        float minValue = Float.MAX_VALUE;
        Map<Item, Float> newResources = new HashMap<>();
        for(Map.Entry<Item, Float> entry: resources.entrySet()){

            NetworkNode node = findItemInNetwork(entry.getKey());
            if(rawResourcesMandatory.containsKey(node)){
                float amount = rawResourcesMandatory.get(node);
                newResources.put(entry.getKey(), entry.getValue()- amount);

                float val = entry.getValue()/amount;
                if(val<minValue)
                    minValue = val;
                if(print)
                    System.out.println("mand : "+ amount + "->"+entry.getKey());
            } else {
                newResources.put(entry.getKey(), entry.getValue());
            }
        }
        if(print)
        System.out.println("after"+newResources);

        if(print)
            System.out.println("degree: "+minValue);
        //calculate
        float result = Float.MAX_VALUE;
        for(Map.Entry<NetworkNode, Float> entry: rawResourcesOptional.entrySet()){

            Item i = entry.getKey().getItem();
            if(newResources.containsKey(i)){
                float val = newResources.get(i)/entry.getValue();

                if(val<result)
                    result = val;
                if(print)
                    System.out.println("val: "+ val + "->"+entry.getKey().getItem());
            }
        }
        //combine amountUsedMaps
        this.amountUsedMap.clear();
        for(NetworkNode node: networkNodeList){
            Float n1 = amountUsedMap.get(node);
            Float n2 = amountUsedScaleMap.get(node);

            if(n1!=null&&n2!=null){
                this.amountUsedMap.put(node, n1+n2*result);
            } else if(n1!=null){
                this.amountUsedMap.put(node, n1);
            } else if(n2!=null){
                this.amountUsedMap.put(node, n2*result);
            }
        }

        return Math.min(minValue,1)<1 ? result*0.1f: result;
    }
    private Map<NetworkNode, Float> calculateResourceConsumption(Pair<Item, Float> itemFloatPair, Map<NetworkNode, Float> availableResources, Map<NetworkNode, Float> amountUsedMap){
        Map<NetworkNode, Float> workerQueue = new HashMap<>();
        Map<NetworkNode, Float> rawResources = new HashMap<>();

        NetworkNode node = findItemInNetwork(itemFloatPair.getLeft());
        workerQueue.put(node, itemFloatPair.getRight());

        while(!workerQueue.isEmpty()){
            NetworkNode networkNode = workerQueue.keySet().iterator().next();
            float n = workerQueue.get(networkNode);
            workerQueue.remove(networkNode);

            if(availableResources.containsKey(networkNode)){
                float availableAmount = availableResources.get(networkNode);


                if(availableAmount>=n){
                    availableResources.put(networkNode, availableAmount- n);
                    continue;
                } else {
                    availableResources.put(networkNode, 0f);
                    n = n - availableAmount;
                }


            }
            //insert all produced items
            if(amountUsedMap.containsKey(networkNode)){
                amountUsedMap.put(networkNode, amountUsedMap.get(networkNode)+n);
            } else {
                amountUsedMap.put(networkNode, n);
            }
            List<Recipe> recipes = networkNode.getRecipeList();
            List<Float> weights = networkNode.getWeightList();
            for(int i = 0; i < recipes.size(); i++){

                Recipe recipe = recipes.get(i);
                float weight = weights.get(i);

                // calculate product amount
                float productAmount = 1;
                for(Pair<Item, Float> itemIntegerPair: recipe.getProducts()){
                    float amount = itemIntegerPair.getRight();
                    if(itemIntegerPair.getLeft().equals(networkNode.getItem())){
                        productAmount = amount;
                    }
                }
                // calculate left over products and mark them as available

                for(Pair<Item, Float> itemIntegerPair: recipe.getProducts()){
                    float amount = itemIntegerPair.getRight();
                    if(!itemIntegerPair.getLeft().equals(networkNode.getItem())){
                        NetworkNode currentNode = findItemInNetwork(itemIntegerPair.getLeft());
                        if(availableResources.containsKey(currentNode)){
                            float currentAmount = availableResources.get(currentNode);
                            availableResources.put(currentNode, currentAmount + n*amount*weight/productAmount);
                        } else {
                            availableResources.put(currentNode, n*amount*weight/productAmount);
                        }
                    }
                }



                for(Pair<Item, Float> itemIntegerPair: recipe.getIngredients()){

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
        return rawResources;
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


    public Network createNewNode(){
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
            NetworkNode newNode = new NetworkNode(node.getItem(), node.getRecipeList(), rdm.nextFloat()<0.1f ? weightList2 : node.getWeightList());
            nodeList.add(newNode);
        }
        return new Network(nodeList);
    }

    public List<NetworkNode> getNetworkNodeList() {
        return networkNodeList;
    }

    public Map<NetworkNode, Float> getAmountUsedMap() {
        return amountUsedMap;
    }

    public List<Recipe> getUsedRecipes(){
        List<Recipe> recipeList = new LinkedList<>();

        for(Map.Entry<NetworkNode, Float> entry: amountUsedMap.entrySet()){
            List<Recipe> entryRecipeList = entry.getKey().getRecipeList();
            for(Recipe recipe: entryRecipeList){
                if(!recipeList.contains(recipe)){
                    recipeList.add(recipe);
                }
            }
        }

        return  recipeList;
    }

    @Override
    public String toString() {
        return "Network{" +
                "networkNodeList=" + amountUsedMap +
                '}';
    }

    @Override
    public String toJSONString() {
        JSONArray arr = new JSONArray();
        for(NetworkNode node: networkNodeList){
            arr.put(node.toJSONString());
        }
        return arr.toString();
    }

    public Network clone(){
        List<NetworkNode> result = new LinkedList<>();
        for(NetworkNode node: networkNodeList){
            result.add(node.clone());
        }
        return new Network(result);
    }
}
