package model;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class Recipe extends Entity {


    private List<Pair<Item, Integer>> ingredients;
    private List<Pair<Item, Integer>> products;
    private float duration;

    private Building producedIn;

    public Recipe(String className, String name, float duration, Building producedIn) {
        super(className, name);
        this.duration = duration;
        this.producedIn = producedIn;
        this.ingredients = new LinkedList<>();
        this.products = new LinkedList<>();
    }

    public boolean isRawMaterial(){
        return (ingredients.size()==1 && products.size()==1 &&
                ingredients.get(0).getKey().equals(products.get(0).getKey())) ||
                products.get(0).getKey().getClassName().equals("Desc_Water_C")||
                products.get(0).getKey().getClassName().equals("Desc_HeavyOilResidue_C")||
                products.get(0).getKey().getClassName().equals("Desc_LiquidOil_C");
    }

    public float scaleToProduct(Item item){
        return 1;
    }
    public void addIngredient(Item item, Integer amount){
        ingredients.add(new ImmutablePair<>(item, amount));
    }

    public void addProduct(Item item, Integer amount){
        products.add(new ImmutablePair<>(item, amount));
    }

    public List<Pair<Item, Integer>> getIngredients() {
        return ingredients;
    }

    public List<Pair<Item, Integer>> getProducts() {
        return products;
    }

    public float getDuration() {
        return duration;
    }

    public Building getProducedIn() {
        return producedIn;
    }

    @Override
    public String toString() {
        return "Recipe{" +
                "name='" + name + '\'' +
                ", ingredients=" + ingredients.toString()+
                ", products=" + products.toString() +
                ", duration=" + duration +
                "}";
    }


    @Override
    public String toJSONString() {
        JSONObject obj = new JSONObject();
        obj.put("id", className);
        obj.put("name", name);
        obj.put("duration", duration);
        obj.put("producedIn", producedIn.toJSONString());

        JSONArray ingArray = new JSONArray();
        for(Pair<Item, Integer> pair: ingredients){
            JSONObject ing = new JSONObject();
            ing.put("item", pair.getKey().toJSONString());
            ing.put("amount", pair.getValue());
            ingArray.put(ing);
        }
        obj.put("ingredients", ingArray);

        JSONArray proArray = new JSONArray();
        for(Pair<Item, Integer> pair: ingredients){
            JSONObject pro = new JSONObject();
            pro.put("item", pair.getKey().toJSONString());
            pro.put("amount", pair.getValue());
            proArray.put(pro);
        }
        obj.put("products", proArray);
        return obj.toString();
    }
}
