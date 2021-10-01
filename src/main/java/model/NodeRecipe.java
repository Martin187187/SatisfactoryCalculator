package model;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class NodeRecipe {

    Recipe recipe;
    Pair<Item, Integer> item;
    NodeRecipe parent;
    List<NodeRecipe> leafList;
    public NodeRecipe(Recipe recipe, Pair<Item, Integer> item, NodeRecipe parent){
        this.recipe = recipe;
        this.item = item;
        this.parent = parent;
        this.leafList = new LinkedList<>();
    }


    public void addLeaf(NodeRecipe leaf){
        leafList.add(leaf);
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public NodeRecipe getParent() {
        return parent;
    }

    public List<NodeRecipe> getLeafList() {
        return leafList;
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder(50);
        print(buffer, "", "");
        return buffer.toString();
    }

    private void print(StringBuilder buffer, String prefix, String childrenPrefix) {
        buffer.append(prefix);
        buffer.append(recipe.getName());
        buffer.append('\n');
        for (Iterator<NodeRecipe> it = leafList.iterator(); it.hasNext();) {
            NodeRecipe next = it.next();
            if (it.hasNext()) {
                next.print(buffer, childrenPrefix + "├── ", childrenPrefix + "│   ");
            } else {
                next.print(buffer, childrenPrefix + "└── ", childrenPrefix + "    ");
            }
        }
    }
}
