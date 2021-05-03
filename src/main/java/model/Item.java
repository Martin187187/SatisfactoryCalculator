package model;

import java.util.Objects;

public class Item {

    private String className;
    private String name;

    private String description;

    public Item(String className, String name, String description) {
        this.className = className;
        this.name = name;
        this.description = description;
    }

    public String getClassName() {
        return className;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
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
                "name='" + name + '\'' +
                '}';
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
