import controller.DataController;
import model.Recipe;
import org.json.JSONArray;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class SelectAndSaveRecipes {
    public static void main(String[] args) {
        DataController controller = new DataController();

        List<Recipe> newRecipeList = new LinkedList<>();
        List<Recipe> recipeList = controller.getRecipeList();
        System.out.println(recipeList);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        for(Recipe recipe: recipeList){
            System.out.println(recipe);
            try {
                String input = reader.readLine();
                if(!input.equals("d")) {
                    System.out.println("item got added!");
                    newRecipeList.add(recipe);
                }
            } catch (IOException e){
                e.printStackTrace();
            }

        }

        System.out.println(newRecipeList);
        System.out.println("-----------");
        System.out.println("size: " + newRecipeList.size());

        JSONArray arr = new JSONArray();

        for(Recipe recipe: newRecipeList){
            arr.put(recipe.toJSONString());
        }
        try {
            FileWriter  file = new FileWriter("selectedRecipeList.json");
            file.write((arr.toString()));
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("finished!");
    }
}
