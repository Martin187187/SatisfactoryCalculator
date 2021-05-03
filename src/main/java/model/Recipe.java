package model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Recipe {

    private String id;
    private String name;

    private Map<Item, Integer> ingredients;
    private Map<Item, Integer> products;
    private float duration;

    private Building producedIn;

    public Recipe(String id, String name, float duration, Building producedIn) {
        this.id = id;
        this.name = name;
        this.duration = duration;
        this.producedIn = producedIn;
        this.ingredients = new HashMap<>();
        this.products = new HashMap<>();
    }

    public void addIngredient(Item item, Integer amount){
        ingredients.put(item, amount);
    }

    public void addProduct(Item item, Integer amount){
        products.put(item, amount);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Map<Item, Integer> getIngredients() {
        return ingredients;
    }

    public Map<Item, Integer> getProducts() {
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
                "}\n";
    }
}
