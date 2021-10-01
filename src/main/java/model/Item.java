package model;

import org.json.JSONObject;

import java.util.Objects;

public class Item extends Entity  {

    private String description;
    private float sinkPoints;

    public Item(String className, String name, String description, int sinkPoints) {
        super(className, name);
        this.description = description;
        this.sinkPoints = sinkPoints;
    }

    public boolean isRawMaterial(){
        return className.equals("Desc_Stone_C")||
                className.equals("Desc_OreIron_C")||
                className.equals("Desc_OreCopper_C")||
                className.equals("Desc_OreGold_C")||
                className.equals("Desc_Coal_C")||
                className.equals("Desc_RawQuartz_C")||
                className.equals("Desc_Sulfur_C")||
                className.equals("Desc_OreBauxite_C")||
                className.equals("Desc_SAM_C")||
                className.equals("Desc_OreUranium_C")||
                className.equals("Desc_Water_C")||
                className.equals("Desc_LiquidOil_C")||
                className.equals("Desc_LiquidBiofuel_C")||
                className.equals("Desc_NitrogenGas_C");
    }

    public String getDescription() {
        return description;
    }

    public float getSinkPoints() {
        return sinkPoints;
    }

    public void setSinkPoints(float sinkPoints) {
        this.sinkPoints = sinkPoints;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return className.equals(item.className);
    }

    @Override
    public int hashCode() {
        return Objects.hash(className);
    }

    @Override
    public String toString() {
        return "Item{" +
                "className='" + className + '\'' +
                ", name='" + name + '\'' +
                ", sinkPoints=" + sinkPoints +
                '}';
    }

    @Override
    public String toJSONString() {
        JSONObject obj = new JSONObject();
        obj.put("className", className);
        obj.put("name", name);
        obj.put("description", description);
        obj.put("sinkPoints", sinkPoints);
        return obj.toString();
    }

    public static class AmountOfItems{

        private Item item;
        private int amount;

        public AmountOfItems(Item item, int amount) {
            this.item = item;
            this.amount = amount;
        }

        public Item getItem() {
            return item;
        }

        public int getAmount() {
            return amount;
        }

        @Override
        public String toString() {
            return "AmountOfItems{" +
                    "item=" + item +
                    ", amount=" + amount +
                    '}';
        }
    }
}
