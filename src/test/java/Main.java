import controller.DataController;
import model.Item;

public class Main {

    public static void main(String[] args) {
        DataController controller = new DataController();
        Item item = new Item("Desc_NuclearFuelRod_C", null, null);

        controller.getListOfRecipesProducts(item).forEach(System.out::print);

    }
}
