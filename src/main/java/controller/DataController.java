package controller;

import model.Item;
import model.NodeItem;
import model.NodeRecipe;
import model.Recipe;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DataController {

    private GameLoader loader;

    private List<Item> itemList;
    private List<Recipe> recipeList;

    public DataController(){
        loader = new GameLoader("update4.json");
        itemList = loader.getItemList();
        recipeList = loader.getRecipeList();

        Item water = itemList.stream().filter(x -> x.getClassName().equals("Desc_Water_C")).findFirst().get();
        water.setSinkPoints(water.getSinkPoints()/1000);
        Item liquidOil = itemList.stream().filter(x -> x.getClassName().equals("Desc_LiquidOil_C")).findFirst().get();
        liquidOil.setSinkPoints(liquidOil.getSinkPoints()/1000);
        Item heavy = itemList.stream().filter(x -> x.getClassName().equals("Desc_HeavyOilResidue_C")).findFirst().get();
        heavy.setSinkPoints(heavy.getSinkPoints()/1000);
        Item fuel = itemList.stream().filter(x -> x.getClassName().equals("Desc_LiquidFuel_C")).findFirst().get();
        fuel.setSinkPoints(fuel.getSinkPoints()/1000);
        Item bioFuel = itemList.stream().filter(x -> x.getClassName().equals("Desc_LiquidBiofuel_C")).findFirst().get();
        bioFuel.setSinkPoints(bioFuel.getSinkPoints()/1000);
        Item turboFuel = itemList.stream().filter(x -> x.getClassName().equals("Desc_LiquidTurboFuel_C")).findFirst().get();
        turboFuel.setSinkPoints(turboFuel.getSinkPoints()/1000);
        Item aluminiumSol = itemList.stream().filter(x -> x.getClassName().equals("Desc_AluminaSolution_C")).findFirst().get();
        aluminiumSol.setSinkPoints(aluminiumSol.getSinkPoints()/1000);
        Item sulfuricAcid = itemList.stream().filter(x -> x.getClassName().equals("Desc_SulfuricAcid_C")).findFirst().get();
        sulfuricAcid.setSinkPoints(sulfuricAcid.getSinkPoints()/1000);
        Item nitricAcid = itemList.stream().filter(x -> x.getClassName().equals("Desc_NitricAcid_C")).findFirst().get();
        nitricAcid.setSinkPoints(nitricAcid.getSinkPoints()/1000);
        Item nitrogenGas = itemList.stream().filter(x -> x.getClassName().equals("Desc_NitrogenGas_C")).findFirst().get();
        nitrogenGas.setSinkPoints(nitrogenGas.getSinkPoints()/1000);

    }
    public Pair<Map<Item, Float>, Pair<NodeRecipe, Float>> getRawItems(Item item){
        return getRawItemsRec(new NodeItem(item, null), 1, getListOfRecipesProducts(item));
    }
    public Pair<Map<Item, Float>, Pair<NodeRecipe, Float>> getRawItems(Item item, Recipe startRecipe){
        List<Recipe> recipeList = new LinkedList<>();

        if(startRecipe==null){
            recipeList = getListOfRecipesProducts(item);
        } else {
            recipeList.add(startRecipe);
        }
        return getRawItemsRec(new NodeItem(item, null), 1, recipeList);
    }
    private Pair<Map<Item, Float>, Pair<NodeRecipe, Float>> getRawItemsRec(NodeItem nodeItem, float amount, List<Recipe> recipeList){
        Item item = nodeItem.getItem();
        Map<Item, Float> hashMap = new HashMap<>();
        NodeRecipe bestRecipe = null;
        float bestValue = Float.MAX_VALUE;
        for(Recipe r: recipeList){
            Pair<Item, Integer> product = r.getProducts().stream().filter(x -> x.getKey()!=null).filter(x -> x.getKey().getClassName().equals(item.getClassName())).findFirst().get();

            Map<Item, Float> combinedMap = new HashMap<>();
            NodeRecipe newRecipe = new NodeRecipe(r, product,null);
            for (Pair<Item, Integer> integerPair : r.getIngredients()) {
                if(integerPair.getKey()==null) {
                    continue;
                }
                Map<Item, Float> currentMap = new HashMap<>();
                NodeItem newNodeItem = new NodeItem(integerPair.getKey(), nodeItem);
                //isRawMaterial?
                if(integerPair.getKey().isRawMaterial()){
                    combinedMap.put(integerPair.getKey(), (float)integerPair.getValue()/product.getValue()*amount);
                } else if(newNodeItem.isNewElement()) {
                    nodeItem.addLeaf(newNodeItem);
                    Pair<Map<Item, Float>, Pair<NodeRecipe, Float>> pair = getRawItemsRec(newNodeItem, (float) integerPair.getValue() / product.getValue() * amount, getListOfRecipesProducts(newNodeItem.getItem()));
                    currentMap = pair.getKey();
                    newRecipe.addLeaf(pair.getValue().getKey());
                } else {
                    Item endTerminator = new Item("null", null,null, 1000000);
                    combinedMap.put(endTerminator, (float)r.getIngredients().get(0).getValue()/product.getValue()*amount);

                }
                    for (Map.Entry<Item, Float> entry : currentMap.entrySet()) {
                        float count = combinedMap.containsKey(entry.getKey()) ? combinedMap.get(entry.getKey()) : 0;
                        combinedMap.put(entry.getKey(), count + entry.getValue());
                }
            }
            float value = 0;
            for (Map.Entry<Item, Float> entry : combinedMap.entrySet()) {
                value += (float)entry.getKey().getSinkPoints() * entry.getValue();
            }

            if (value <= bestValue) {
                hashMap = combinedMap;
                bestValue = value;
                bestRecipe = newRecipe;
            }

        }
        return new ImmutablePair<>(hashMap, new ImmutablePair<>(bestRecipe, bestValue));
    }

    public List<Recipe> getListOfRecipesIngredient(Item item){
        List<Recipe> result = new LinkedList<>();
        for( Recipe recipe: recipeList){
            if(recipe.getIngredients().stream().anyMatch(x -> x.getKey().equals(item))){
                result.add(recipe);
            }
        }
        return result;
    }

    public List<Recipe> getListOfRecipesProducts(Item item){
        Item acid = new Item("Desc_SulfuricAcid_C", null, null, 0);
        List<Recipe> result = new LinkedList<>();
        for( Recipe recipe: recipeList){
            if(recipe.getProducts().stream().filter(x -> x.getKey()!=null).anyMatch(x-> x.getKey().equals(item))){
                result.add(recipe);
            }
        }
        return result;
    }

    public List<Item> getItemList() {
        return itemList;
    }

    public List<Recipe> getRecipeList() {
        return recipeList;
    }
}

