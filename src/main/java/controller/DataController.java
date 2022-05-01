package controller;

import model.*;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Node;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class DataController {

    private GameLoader loader;

    private List<Item> itemList;
    private List<Recipe> recipeList;

    public DataController() {
        loader = new GameLoader("update4.json");
        itemList = loader.getItemList();
        recipeList = loader.getRecipeList();

        Item water = itemList.stream().filter(x -> x.getClassName().equals("Desc_Water_C")).findFirst().get();
        water.setSinkPoints(water.getSinkPoints() / 1000);
        Item liquidOil = itemList.stream().filter(x -> x.getClassName().equals("Desc_LiquidOil_C")).findFirst().get();
        liquidOil.setSinkPoints(liquidOil.getSinkPoints() / 1000);
        Item heavy = itemList.stream().filter(x -> x.getClassName().equals("Desc_HeavyOilResidue_C")).findFirst().get();
        heavy.setSinkPoints(heavy.getSinkPoints() / 1000);
        Item fuel = itemList.stream().filter(x -> x.getClassName().equals("Desc_LiquidFuel_C")).findFirst().get();
        fuel.setSinkPoints(fuel.getSinkPoints() / 1000);
        Item bioFuel = itemList.stream().filter(x -> x.getClassName().equals("Desc_LiquidBiofuel_C")).findFirst().get();
        bioFuel.setSinkPoints(bioFuel.getSinkPoints() / 1000);
        Item turboFuel = itemList.stream().filter(x -> x.getClassName().equals("Desc_LiquidTurboFuel_C")).findFirst().get();
        turboFuel.setSinkPoints(turboFuel.getSinkPoints() / 1000);
        Item aluminiumSol = itemList.stream().filter(x -> x.getClassName().equals("Desc_AluminaSolution_C")).findFirst().get();
        aluminiumSol.setSinkPoints(aluminiumSol.getSinkPoints() / 1000);
        Item sulfuricAcid = itemList.stream().filter(x -> x.getClassName().equals("Desc_SulfuricAcid_C")).findFirst().get();
        sulfuricAcid.setSinkPoints(sulfuricAcid.getSinkPoints() / 1000);
        Item nitricAcid = itemList.stream().filter(x -> x.getClassName().equals("Desc_NitricAcid_C")).findFirst().get();
        nitricAcid.setSinkPoints(nitricAcid.getSinkPoints() / 1000);
        Item nitrogenGas = itemList.stream().filter(x -> x.getClassName().equals("Desc_NitrogenGas_C")).findFirst().get();
        nitrogenGas.setSinkPoints(nitrogenGas.getSinkPoints() / 1000);

        Item custom = new Item("custom", "custom", "nan", 543632);
        itemList.add(custom);

        //remove dumb recipes
        List<Recipe> newList = new LinkedList<>();
        for(Recipe r: recipeList){
            String classname = r.getClassName();

            switch (classname){
                case "Recipe_UnpackageBioFuel_C":
                case "Recipe_UnpackageFuel_C":
                case "Recipe_UnpackageOil_C":
                case "Recipe_UnpackageOilResidue_C":
                case "Recipe_UnpackageWater_C":
                case "Recipe_UnpackageAlumina_C":
                case "Recipe_UnpackageTurboFuel_C":
                case "Recipe_UnpackageSulfuricAcid_C":
                case "Recipe_UnpackageNitrogen_C":
                case "Recipe_UnpackageNitricAcid_C":
                case "Recipe_Alternate_Plastic_1_C":
                case "Recipe_Alternate_RecycledRubber_C":
                    break;
                case "Recipe_UraniumCell_C":
                    List<Pair<Item, Float>> ingredientList = r.getIngredients();
                    List<Pair<Item, Float>> productList = r.getProducts();

                    List<Pair<Item, Float>> newProductList = new LinkedList<>();
                    Pair<Item, Float> sulfurAcid = null;
                    for(Pair<Item, Float> ing: productList){
                        if(ing.getLeft().getClassName().equals("Desc_SulfuricAcid_C")){
                            sulfurAcid = ing;
                        } else {
                            newProductList.add(ing);
                        }
                    }

                    List<Pair<Item, Float>> newIngredientList = new LinkedList<>();
                    for(Pair<Item, Float> ing: ingredientList){
                        if(ing.getLeft().getClassName().equals("Desc_SulfuricAcid_C")){
                            newIngredientList.add(new ImmutablePair<>(ing.getLeft(), ing.getValue() - sulfurAcid.getValue()));
                        } else {
                            newIngredientList.add(ing);
                        }
                    }
                    r.setIngredients(newIngredientList);
                    r.setProducts(newProductList);
                default:
                    newList.add(r);
                    break;
            }

            }
        this.recipeList = newList;

        //add recipes for custom item
        for(int i = 1; i <= 9; i++){
            String number = Integer.toString(i);
            Recipe r = new Recipe("r"+number, "r"+number, 0, null);
            Item space = itemList.stream().filter(x -> x.getClassName().equals("Desc_SpaceElevatorPart_"+number+"_C")).findFirst().get();
            r.addIngredient(space, custom.getSinkPoints()/ space.getSinkPoints());
            r.addProduct(custom, 1f);
            recipeList.add(r);
        }
        Recipe r = new Recipe("rub", "rub", 0, null);
        Recipe p = new Recipe("plas", "plas", 0, null);
        Item rubber = itemList.stream().filter(x -> x.getClassName().equals("Desc_Rubber_C")).findFirst().get();
        r.addIngredient(fuel, 18000f);
        r.addProduct(rubber, 18f);
        recipeList.add(r);
        Item plastic = itemList.stream().filter(x -> x.getClassName().equals("Desc_Plastic_C")).findFirst().get();
        p.addIngredient(fuel, 18000f);
        p.addProduct(plastic, 18f);
        recipeList.add(p);


    }


    public List<NetworkNode> createNetworkNodes(){

        List<NetworkNode> result = new LinkedList<>();
        for(Item item: itemList){
            NetworkNode node = new NetworkNode(item);
            List<Recipe> recipesOfItem = getListOfRecipesProducts(item);

            for(Recipe recipe: recipeList){

                if(recipesOfItem.contains(recipe)){
                    node.addRecipe(recipe);
                }
            }
            node.calculateWeights();
            result.add(node);

        }

        return result;

    }

    public Map<Item, Float> getRawItems(){
        Map<Item, Float> result = new HashMap<>();

        for(Item item: itemList){
            switch (item.getClassName()){
                case "Desc_Stone_C":
                    result.put(item, 52860f);
                    break;
                case "Desc_OreIron_C":
                    result.put(item, 70380f);
                    break;
                case "Desc_OreCopper_C":
                    result.put(item, 28860f);
                    break;
                case "Desc_OreGold_C":
                    result.put(item, 11040f);
                    break;
                case "Desc_Coal_C":
                    result.put(item, 30900f);
                    break;
                case "Desc_RawQuartz_C":
                    result.put(item, 10500f);
                    break;
                case "Desc_Sulfur_C":
                    result.put(item, 8040f);
                    break;
                case "Desc_OreBauxite_C":
                    result.put(item, 9780f);
                    break;
                case "Desc_SAM_C":
                    result.put(item, 5400f);
                    break;
                case "Desc_OreUranium_C":
                    result.put(item, 2100f);
                    break;
                case "Desc_Water_C":
                    result.put(item, 1000000000f);
                    break;
                case "Desc_LiquidOil_C":
                    result.put(item, 11700000f);
                    break;
                case "Desc_LiquidBiofuel_C":
                    result.put(item, 0f);
                    break;
                case "Desc_NitrogenGas_C":
                    result.put(item, 12000000f);
                    break;
                case "Desc_FluidCanister_C":
                    result.put(item, 1000000000f);
                    break;

            }
        }

        return result;
    }
    public Pair<Map<Item, Float>, Pair<NodeRecipe, Float>> getRawItems(Item item) {
        return getRawItemsRec(new NodeItem(item, null), 1, getListOfRecipesProducts(item));
    }

    public Pair<Map<Item, Float>, Pair<NodeRecipe, Float>> getRawItems(Item item, Recipe startRecipe) {
        List<Recipe> recipeList = new LinkedList<>();

        if (startRecipe == null) {
            recipeList = getListOfRecipesProducts(item);
        } else {
            recipeList.add(startRecipe);
        }
        return getRawItemsRec(new NodeItem(item, null), 1, recipeList);
    }

    private Pair<Map<Item, Float>, Pair<NodeRecipe, Float>> getRawItemsRec(NodeItem nodeItem, float amount, List<Recipe> recipeList) {
        Item item = nodeItem.getItem();
        Map<Item, Float> hashMap = new HashMap<>();
        NodeRecipe bestRecipe = null;
        float bestValue = Float.MAX_VALUE;
        for (Recipe r : recipeList) {
            Pair<Item, Float> product = r.getProducts().stream().filter(x -> x.getKey() != null).filter(x -> x.getKey().getClassName().equals(item.getClassName())).findFirst().get();

            Map<Item, Float> combinedMap = new HashMap<>();
            NodeRecipe newRecipe = new NodeRecipe(r, product, null);
            for (Pair<Item, Float> integerPair : r.getIngredients()) {
                if (integerPair.getKey() == null) {
                    continue;
                }
                Map<Item, Float> currentMap = new HashMap<>();
                NodeItem newNodeItem = new NodeItem(integerPair.getKey(), nodeItem);
                //isRawMaterial?
                if (integerPair.getKey().isRawMaterial()) {
                    combinedMap.put(integerPair.getKey(), (float) integerPair.getValue() / product.getValue() * amount);
                } else if (newNodeItem.isNewElement()) {
                    nodeItem.addLeaf(newNodeItem);
                    Pair<Map<Item, Float>, Pair<NodeRecipe, Float>> pair = getRawItemsRec(newNodeItem, (float) integerPair.getValue() / product.getValue() * amount, getListOfRecipesProducts(newNodeItem.getItem()));
                    currentMap = pair.getKey();
                    newRecipe.addLeaf(pair.getValue().getKey());
                } else {
                    Item endTerminator = new Item("null", null, null, 1000000);
                    combinedMap.put(endTerminator, (float) r.getIngredients().get(0).getValue() / product.getValue() * amount);

                }
                for (Map.Entry<Item, Float> entry : currentMap.entrySet()) {
                    float count = combinedMap.containsKey(entry.getKey()) ? combinedMap.get(entry.getKey()) : 0;
                    combinedMap.put(entry.getKey(), count + entry.getValue());
                }
            }
            float value = 0;
            for (Map.Entry<Item, Float> entry : combinedMap.entrySet()) {
                value += (float) entry.getKey().getSinkPoints() * entry.getValue();
            }

            if (value <= bestValue) {
                hashMap = combinedMap;
                bestValue = value;
                bestRecipe = newRecipe;
            }

        }
        return new ImmutablePair<>(hashMap, new ImmutablePair<>(bestRecipe, bestValue));
    }

    public List<NodeRecipe> getListOfAllRecipies(NodeRecipe nodeRecipe, int deph) {
        List<NodeRecipe> endResult = new LinkedList<>();
        List<NodeRecipe> result = new LinkedList<>();
        List<Pair<Item, Float>> itemPairList = nodeRecipe.getRecipe().getIngredients();

        for(Pair<Item, Float> item: itemPairList){
            if(!item.getLeft().isRawMaterial()){
            List<NodeRecipe> newNodeList = new LinkedList<>();

            if(result.isEmpty()) {
                result.add(nodeRecipe);
            }

            for(NodeRecipe node: result) {
                List<Recipe> recipeList = getListOfRecipesProducts(item.getLeft());

                for (Recipe recipe : recipeList) {

                    NodeRecipe newNodeRecipe = new NodeRecipe(recipe, item, nodeRecipe);

                    if (!item.getLeft().isRawMaterial() && newNodeRecipe.isNewNode()) {

                        List<NodeRecipe> floor = getListOfAllRecipies(newNodeRecipe, deph +1);

                        for (NodeRecipe r : floor) {
                            newNodeList.add(r);
                        }

                    }
                }
            }
            result = newNodeList;
            }
        }
        if(result.isEmpty())
            result.add(nodeRecipe);
        return result;
    }

    public List<Recipe> getListOfRecipesIngredient(Item item) {
        List<Recipe> result = new LinkedList<>();
        for (Recipe recipe : recipeList) {
            if (recipe.getIngredients().stream().anyMatch(x -> x.getKey().equals(item))) {
                result.add(recipe);
            }
        }
        return result;
    }

    public List<Recipe> getListOfRecipesProducts(Item item) {
        Item acid = new Item("Desc_SulfuricAcid_C", null, null, 0);
        List<Recipe> result = new LinkedList<>();
        for (Recipe recipe : recipeList) {
            if (recipe.getProducts().stream().filter(x -> x.getKey() != null).anyMatch(x -> x.getKey().equals(item))) {
                result.add(recipe);
            }
        }
        return result;
    }

    public Network loadNetworkFromFile(){
        File file = new File("savefiles/test.json");
        BufferedReader reader = null;
        List<NetworkNode> networkNodeList = new LinkedList<>();
        try {
            reader = new BufferedReader(new FileReader(file));
            String json = reader.readLine();
            JSONArray arr = new JSONArray(json);

            for(int i = 0; i < arr.length(); i++){
                String nodeString = (String) arr.get(i);
                JSONObject obj = new JSONObject(nodeString);
                Item item = itemList.stream().filter(x -> x.getClassName().equals(obj.getString("item"))).findFirst().get();

                NetworkNode networkNode = new NetworkNode(item);
                JSONArray recipes = obj.getJSONArray("recipes");
                for(int j = 0; j < recipes.length(); j++){
                    String classname = recipes.getString(j);
                    Recipe recipe = recipeList.stream().filter(x -> x.getClassName().equals(classname)).findFirst().get();
                    networkNode.addRecipe(recipe);
                }

                JSONArray weights = obj.getJSONArray("weights");
                for(int j = 0; j < weights.length(); j++){
                    Float weight = weights.getFloat(j);
                    networkNode.addWeight(weight);
                }
                networkNodeList.add(networkNode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new Network(networkNodeList);
    }
    public List<Item> getItemList() {
        return itemList;
    }

    public List<Recipe> getRecipeList() {
        return recipeList;
    }
}

