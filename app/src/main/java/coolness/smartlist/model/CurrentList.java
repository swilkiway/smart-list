package coolness.smartlist.model;

import java.util.ArrayList;

public class CurrentList {
    private String name;
    private ArrayList<ListItem> items;
    private boolean current;
    public CurrentList(String name) {
        this.name = name;
        items = new ArrayList<>();
        current = true;
    }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public ArrayList<ListItem> getItems() { return items; }
    public void setItems(ArrayList<ListItem> items) { this.items = items; }
    public String getInfoStringify() {
        return name + "," + (current ? "y," : "n,") + items.size() + ",";
    }
    public boolean isCurrentList() { return current; }
    public void setCurrent(boolean isCurrent) { current = isCurrent; }
    public void switchCurrent() { current = !current; }
    public void addListItem(ListItem l) { items.add(l); }
}
