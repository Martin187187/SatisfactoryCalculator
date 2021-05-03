package model;

import java.util.Objects;

public class Building {

    private String classname;
    private String name;
    private String description;

    private float manufacturingSpeed;
    private float powerConsumption;
    private float powerConsumptionExponent;

    public Building(String classname, String name, String description, float manufacturingSpeed, float powerConsumption, float powerConsumptionExponent) {
        this.classname = classname;
        this.name = name;
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
        return classname.equals(building.classname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(classname);
    }

    @Override
    public String toString() {
        return "Building{" +
                "name='" + name + '\'' +
                ", powerConsumption=" + powerConsumption +
                '}';
    }

}
