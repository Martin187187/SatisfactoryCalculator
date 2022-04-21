package model;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class NetworkNode {

    private Item item;
    private List<Recipe> recipeList;
    private List<Float> weightList;

    public NetworkNode(Item item){
        this.item = item;
        this.recipeList = new LinkedList<>();
        this.weightList = new LinkedList<>();
    }
    public NetworkNode(Item item, List<Recipe> recipeList, List<Float> weightList){
        this.item = item;
        this.recipeList = recipeList;
        this.weightList = weightList;
    }
    public void addRecipe(Recipe recipe){
        recipeList.add(recipe);
    }

    public void calculateWeights(){

        List<Float> rdmList = new LinkedList<>();
        float sum = 0;
        for(Recipe recipe: recipeList){
            Random rdm = new Random();
            float n = rdm.nextFloat();
            sum += n;
            rdmList.add(n);
        }

        List<Float> newWeightList = new LinkedList<>();
        for(Float f: rdmList){
            if(sum==0)
                newWeightList.add(0f);
            else
                newWeightList.add(f/sum);
        }
        weightList = newWeightList;

    }

    public Item getItem() {
        return item;
    }

    public List<Recipe> getRecipeList() {
        return recipeList;
    }

    public List<Float> getWeightList() {
        return weightList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NetworkNode)) return false;
        NetworkNode that = (NetworkNode) o;
        return item.equals(that.item);
    }

    @Override
    public String toString() {
        return "NetworkNode{" +
                "item=" + item +
                ", recipeList=" + recipeList +
                ", weightList=" + weightList +
                "}\n";
    }
}
