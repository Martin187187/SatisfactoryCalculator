import controller.DataController;
import model.Item;
import model.NodeRecipe;
import model.Recipe;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class TestNodeRecipe {

    public static void main(String[] args) {

        DataController controller = new DataController();
        List<Item> itemList = controller.getItemList();
        Item water = itemList.stream().filter(x -> x.getClassName().equals("Desc_Water_C")).findFirst().get();
        water.setSinkPoints(0);

        Item item = new Item("Desc_IronPlateReinforced_C", null, null, 1);

        Recipe root = new Recipe("nan", "root", 0, null);
        root.addIngredient(item, 1);
        NodeRecipe node = new NodeRecipe(root);

        NodeRecipe nodeRecipe = controller.getListOfAllRecipies(node);
        System.out.println("ergebnis");
        System.out.println(nodeRecipe);

    }
}
