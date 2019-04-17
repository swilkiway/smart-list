package coolness.smartlist.model;

import java.util.ArrayList;

public class CurrentList {
    private String name;
    private ArrayList<ListItem> items;
    private boolean current;
    private boolean oneTimeTrip;
    public CurrentList(String name) {
        this.name = name;
        items = new ArrayList<>();
        current = true;
        this.oneTimeTrip = false;
    }
    public CurrentList(String name, boolean oneTimeTrip) {
        this.name = name;
        items = new ArrayList<>();
        current = true;
        this.oneTimeTrip = oneTimeTrip;
    }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public ArrayList<ListItem> getItems() { return items; }
    public void setItems(ArrayList<ListItem> items) { this.items = items; }
    public String getInfoStringify() {
        return name + "," + (current ? "y," : "n,") + items.size() + "," + (oneTimeTrip ? "y," : "n,");
    }
    public boolean isCurrentList() { return current; }
    public void setCurrent(boolean isCurrent) { current = isCurrent; }
    public void switchCurrent() { current = !current; }
    public void addListItem(ListItem l) { items.add(l); }
    public void removeItem(ListItem l) { items.remove(l); }
    public boolean isOneTimeTrip() { return oneTimeTrip; }
    public void setOneTimeTrip(boolean trip) { oneTimeTrip = trip; }
    public void setItemChecked(ListItem l, boolean checked) {
        int index = items.indexOf(l);
        items.get(index).setChecked(checked);
    }
    public void modifyItem(String oldName, String newName) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getName().equals(oldName)) {
                items.get(i).setName(newName);
                return;
            }
        }
    }
}
