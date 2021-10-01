package model;

import org.json.JSONString;

public abstract class Entity implements JSONString {
    protected String className;
    protected String name;

    public Entity(String className, String name) {
        this.className = className;
        this.name = name;
    }

    public String getClassName() {
        return className;
    }

    public String getName() {
        return name;
    }
}
