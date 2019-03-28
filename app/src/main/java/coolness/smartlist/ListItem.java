package coolness.smartlist;

public class ListItem {
    public ListItem() {}
    public ListItem(String name) { this.name = name; }
    private String name;
    private boolean checked;
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public boolean isChecked() { return checked; }
    public void setChecked(boolean checked) { this.checked = checked; }
    public boolean switchChecked() {
        checked = !checked;
        return checked;
    }
}
