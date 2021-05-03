package controller;

import model.Item;
import model.Recipe;

import java.util.LinkedList;
import java.util.List;

public class DataController {

    private ModelLoader loader;

    private List<Item> itemList;
    private List<Recipe> recipeList;

    public DataController(){
        loader = new ModelLoader("update4.json");
        itemList = loader.getItemList();
        recipeList = loader.getRecipeList();

    }

    public List<Recipe> getListOfRecipesIngredient(Item item){
        List<Recipe> result = new LinkedList<>();
        for( Recipe recipe: recipeList){
            if(recipe.getIngredients().containsKey(item)){
                result.add(recipe);
            }
        }
        return result;
    }

    public List<Recipe> getListOfRecipesProducts(Item item){
        List<Recipe> result = new LinkedList<>();
        for( Recipe recipe: recipeList){
            if(recipe.getProducts().containsKey(item)){
                result.add(recipe);
            }
        }
        return result;
    }
}
