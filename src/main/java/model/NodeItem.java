package model;


import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class NodeItem {

    Item item;
    NodeItem parent;
    List<NodeItem> leafList;
    public NodeItem(Item item, NodeItem parent){
        this.item = item;
        this.parent = parent;
        this.leafList = new LinkedList<>();

    }

    public boolean isNewElement(){
        NodeItem nodeItem = this;
        List<Item> producedItems = new LinkedList<>();
        while(nodeItem !=null){
            if(producedItems.contains(nodeItem.item))
                return false;
            producedItems.add(nodeItem.item);
            nodeItem = nodeItem.parent;

        }
        return true;
    }

    public void addLeaf(NodeItem leaf){
        leafList.add(leaf);
    }

    public Item getItem() {
        return item;
    }

    public NodeItem getParent() {
        return parent;
    }

    public List<NodeItem> getLeafList() {
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
        buffer.append(item.getName());
        buffer.append('\n');
        for (Iterator<NodeItem> it = leafList.iterator(); it.hasNext();) {
            NodeItem next = it.next();
            if (it.hasNext()) {
                next.print(buffer, childrenPrefix + "├── ", childrenPrefix + "│   ");
            } else {
                next.print(buffer, childrenPrefix + "└── ", childrenPrefix + "    ");
            }
        }
    }
}
