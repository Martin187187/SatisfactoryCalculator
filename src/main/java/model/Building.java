package model;

import org.json.JSONObject;

import java.util.Objects;

public class Building extends Entity {

    private String description;

    private float manufacturingSpeed;
    private float powerConsumption;
    private float powerConsumptionExponent;

    public Building(String className, String name, String description, float manufacturingSpeed, float powerConsumption, float powerConsumptionExponent) {
        super(className, name);
        this.description = description;
        this.manufacturingSpeed = manufacturingSpeed;
        this.powerConsumption = powerConsumption;
        this.powerConsumptionExponent = powerConsumptionExponent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Building building = (Building) o;
        return className.equals(building.className);
    }

    @Override
    public int hashCode() {
        return Objects.hash(className);
    }

    @Override
    public String toString() {
        return "Building{" +
                "name='" + name + '\'' +
                ", powerConsumption=" + powerConsumption +
                '}';
    }

    @Override
    public String toJSONString() {
        JSONObject obj = new JSONObject();
        obj.put("classname", className);
        obj.put("name", name);
        obj.put("description", description);
        obj.put("manufacturingSpeed", manufacturingSpeed);
        obj.put("powerConsumption", powerConsumption);
        obj.put("powerConsumptionExponent", powerConsumptionExponent);
        return obj.toString();
    }

}
