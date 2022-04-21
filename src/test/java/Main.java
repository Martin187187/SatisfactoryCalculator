import controller.DataController;
import model.Item;
import model.NodeRecipe;
import model.Recipe;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        DataController controller = new DataController();
        List<Item> itemList = controller.getItemList();
        Item water = itemList.stream().filter(x -> x.getClassName().equals("Desc_Water_C")).findFirst().get();
        water.setSinkPoints(0);

        Item item = new Item("Desc_SpaceElevatorPart_7_C", null, null, 1);
        //printData(item, controller);

        Item item2 = new Item("Desc_SpaceElevatorPart_6_C", null, null, 1);
        //printData(item2, controller);

        Item item3 = itemList.stream().filter(x -> x.getClassName().equals("Desc_SteelIngot_C")).findFirst().get();
        System.out.println(controller.getListOfRecipesProducts(item3).get(2).getIngredients());
//Recipe_Alternate_Plastic_1_C
//Recipe_Alternate_RecycledRubber_C
        /*
        Item sc = itemList.stream().filter(x -> x.getClassName().equals("Desc_ModularFrameHeavy_C")).findFirst().get();
        printData(sc, controller);

        List<Recipe> scRecipes = controller.getListOfRecipesProducts(sc);

        for(Recipe r: scRecipes){
            Pair<Map<Item, Float>, Pair<NodeRecipe, Float>> result = controller.getRawItems(sc, r);
            NodeRecipe nodes = result.getValue().getKey();
            float value = result.getValue().getValue();
            System.out.println(nodes);
            System.out.println(value);
            System.out.println(result.getKey());
        }
         */


    }

    public static void printData(Item item, DataController controller){
        Pair<Map<Item, Float>, Pair<NodeRecipe, Float>> result = controller.getRawItems(item);
        NodeRecipe nodes = result.getValue().getKey();
        float value = result.getValue().getValue();
        System.out.println(nodes);
        System.out.println(value);
        System.out.println(result.getKey());
    }
}
