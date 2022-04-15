package controller;

import model.Item;
import model.NetworkNode;
import model.Recipe;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

public class Network {


    private List<NetworkNode> networkNodeList;

    public Network(List<NetworkNode> networkNodeList){

        this.networkNodeList = networkNodeList;

    }

    // TODO: item should be a list
    public float calculateValue(Item item, List<Pair<Item, Float>> resources){

        Queue<Pair<NetworkNode, Float>> nodesToVisit = new LinkedList<>();
        List<Pair<NetworkNode, Float>> nodesVisited = new LinkedList<>();

        nodesToVisit.add(new ImmutablePair<>(findItemInNetwork(item),1f));

        while(!nodesToVisit.isEmpty()){
            Pair<NetworkNode, Float> visitPair = nodesToVisit.poll();
            NetworkNode visitNode = visitPair.getKey();
            float amount = visitPair.getValue();

            List<Recipe> recipeList = visitNode.getRecipeList();
            List<Float> weights = visitNode.getWeightList();
            if(nodesVisited.stream().anyMatch(x -> x.getKey().equals(visitPair.getLeft()))){

                //update numbers to produce for current item
                for(Pair<NetworkNode, Float> networkNodeFloatPair: nodesVisited){
                    if(networkNodeFloatPair.getLeft().equals(visitNode)){
                        float n = networkNodeFloatPair.getValue();
                        networkNodeFloatPair.setValue(n + amount);
                        break;
                    }
                }
            } else {

                nodesVisited.add(visitPair);
                if(!visitNode.getItem().isRawMaterial()) {

                    for (int i = 0; i < recipeList.size(); i++) {
                        Recipe recipe = recipeList.get(i);
                        float weight = weights.get(i);

                        for (Pair<Item, Integer> ingredient : recipe.getIngredients()) {
                            float n = ingredient.getValue() * weight;
                            NetworkNode newNetworkNode = findItemInNetwork(ingredient.getKey());
                            nodesToVisit.add(new MutablePair<>(newNetworkNode, n));
                        }
                    }
                }
            }
        }

        //count raw materials
        List<Pair<NetworkNode, Float>> rawNodes = nodesVisited.stream().filter(x -> x.getKey().getItem().isRawMaterial()).collect(Collectors.toList());
        System.out.println(rawNodes);
        return 0;
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


}
