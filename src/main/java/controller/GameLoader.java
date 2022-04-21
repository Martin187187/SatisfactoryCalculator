package controller;

import model.Building;
import model.Item;
import model.Recipe;
import org.json.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;


public class GameLoader {

    private final String NATIVE_CLASS = "NativeClass";
    private final String CLASSES = "Classes";

    private final String CLASS_NAME = "ClassName";
    private final String NAME = "mDisplayName";
    private final String DESCRIPTION = "mDescription";
    private final String SINK_POINTS = "mResourceSinkPoints";
    private final String INGREDIENTS = "mIngredients";
    private final String PRODUCTS = "mProduct";
    private final String DURATION = "mManufactoringDuration";

    private final String MANUFACTURING_SPEED  = "mManufacturingSpeed";
    private final String POWER_CONSUMPTION = "mPowerConsumption";
    private final String POWER_CONSUMPTION_EXPONENT = "mPowerConsumptionExponent";


    private JSONArray jsonfile;

    private List<Building> buildingList;
    private List<Item> itemList;
    private List<Recipe> recipeList;

    public GameLoader(String filepath){

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(filepath).getPath());

        StringBuilder sb = new StringBuilder();
        try {
            InputStream inputStream = new FileInputStream(file);
            Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_16);

            while(scanner.hasNext()){
                sb.append(scanner.next());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        jsonfile = new JSONArray(sb.toString());
        //printClasses();
        loadItems();
        loadBuildings();
        loadRecipes();
    }

    private void loadBuildings(){
        buildingList = new LinkedList<>();

        JSONArray arr = jsonfile.getJSONObject(getNumber("Class'/Script/FactoryGame.FGBuildableManufacturer'")).getJSONArray(CLASSES);
        for(int i = 0; i < arr.length(); i++){
            JSONObject obj = arr.getJSONObject(i);

            String classname = obj.getString(CLASS_NAME);
            String name = obj.getString(NAME);
            String description = obj.getString(DESCRIPTION);

            float speed = obj.getFloat(MANUFACTURING_SPEED);
            float power = obj.getFloat(POWER_CONSUMPTION);
            float exponent = obj.getFloat(POWER_CONSUMPTION_EXPONENT);

            Building b = new Building(classname, name, description, speed, power, exponent);
            buildingList.add(b);
        }
    }
    private void loadItems(){
        itemList = new LinkedList<>();
        collectItem(jsonfile.getJSONObject(getNumber("Class'/Script/FactoryGame.FGItemDescriptor'")).getJSONArray(CLASSES));
        collectItem(jsonfile.getJSONObject(getNumber("Class'/Script/FactoryGame.FGResourceDescriptor'")).getJSONArray(CLASSES));
        collectItem(jsonfile.getJSONObject(getNumber("Class'/Script/FactoryGame.FGItemDescriptorNuclearFuel'")).getJSONArray(CLASSES));
        collectItem(jsonfile.getJSONObject(getNumber("Class'/Script/FactoryGame.FGConsumableEquipment'")).getJSONArray(CLASSES));
        collectItem(jsonfile.getJSONObject(getNumber("Class'/Script/FactoryGame.FGItemDescriptorBiomass'")).getJSONArray(CLASSES));
        collectItem(jsonfile.getJSONObject(getNumber("Class'/Script/FactoryGame.FGConsumableDescriptor'")).getJSONArray(CLASSES));
    }

    private void collectItem(JSONArray arr) {
        for(int i = 0; i < arr.length(); i++){
            JSONObject obj = arr.getJSONObject(i);

            String classname = obj.getString(CLASS_NAME);
            String name = null;
            String description = null;
            int sinkPoints = 0;
            if(obj.has(NAME))
                name = obj.getString(NAME);
            if(obj.has(DESCRIPTION))
                description = obj.getString(DESCRIPTION);
            if(obj.has(SINK_POINTS))
                sinkPoints = obj.getInt(SINK_POINTS);

            Item item = new Item(classname, name, description, sinkPoints);
            itemList.add(item);
        }
    }

    private void loadRecipes(){
        recipeList = new LinkedList<>();
        JSONArray arr = jsonfile.getJSONObject(getNumber("Class'/Script/FactoryGame.FGRecipe'")).getJSONArray(CLASSES);

        for(int i = 0; i < arr.length(); i++) {
            JSONObject obj = arr.getJSONObject(i);



            String classname = obj.getString(CLASS_NAME);
            if(classname=="Recipe_PowerPoleWallMk3_C")
            System.out.println(classname);
            String name = obj.getString(NAME);
            String dur = obj.getString(DURATION);
            Recipe recipe = new Recipe(classname, name, Float.parseFloat(dur), null);

            String rawIng = obj.getString(INGREDIENTS);
            for(Item.AmountOfItems amountOfItems: getIngredients(rawIng)){
                recipe.addIngredient(amountOfItems.getItem(), (float) amountOfItems.getAmount());
            }
            String rawPro= obj.getString(PRODUCTS);
            for(Item.AmountOfItems amountOfItems: getIngredients(rawPro)){
                recipe.addProduct(amountOfItems.getItem(), (float) amountOfItems.getAmount());
            }
            recipeList.add(recipe);
        }

    }

    public void printClasses(){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < jsonfile.length(); i++){

            JSONObject obj = jsonfile.getJSONObject(i);
            sb.append(obj.getString(NATIVE_CLASS)).append(": ");
            JSONArray arr = obj.getJSONArray(CLASSES);

            if(arr.length()>0){
                sb.append(arr.get(0));
            }
            sb.append("\n");
        }
        System.out.println(sb.toString());
    }

    private int getNumber(String key){
        for(int i = 0; i < jsonfile.length(); i++){
            if(jsonfile.getJSONObject(i).get(NATIVE_CLASS).equals(key))
                return i;
        }
        return -1;
    }

    private String getClassName(String string){
        String[] arr = string.split(".");
        return arr[arr.length-1];
    }

    private List<Item.AmountOfItems> getIngredients(String string){
        List<Item.AmountOfItems> amountOfItemsList = new LinkedList<>();

        string = string.substring(2, string.length()-2);
        String[] matches = string.split("\\),\\(");
        for(String m: matches) {
            String[] b = m.split("\"',Amount=");

            String[] c =b[b.length-2].split("\\.");
            if(c.length <2) {
                System.out.println(m);
                Arrays.asList(c).forEach(x -> System.out.println("- "+x));
            }
            String itemName = c[c.length-1];
            String amount = b[b.length -1];
            Item item = null;
            for(Item i: itemList){
                if(i.getClassName().equals(itemName)){
                    item = i;
                    break;
                }
            }
            Item.AmountOfItems amountOfItems = new Item.AmountOfItems(item, Integer.parseInt(amount));
            amountOfItemsList.add(amountOfItems);
        }
        return amountOfItemsList;
    }

    public List<Building> getBuildingList() {
        return buildingList;
    }

    public List<Item> getItemList() {
        return itemList;
    }

    public List<Recipe> getRecipeList() {
        return recipeList;
    }

}
