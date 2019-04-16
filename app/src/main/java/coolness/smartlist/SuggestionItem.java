package coolness.smartlist;

public class SuggestionItem {
    private String name;
    private int weeksSinceBought;
    private boolean checked;
    private String listName;
    public SuggestionItem(String name, long millis, String listName) {
        this.name = name;
        weeksSinceBought = (int)(millis / 604800000L);
        checked = true;
        this.listName = listName;
    }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getWeeksSinceBought() { return weeksSinceBought; }
    public void setWeeksSinceBought(long today) { weeksSinceBought = (int)(today / 604800000L); }
    public boolean isChecked() { return checked; }
    public void setChecked(boolean checked) { this.checked = checked; }
    public boolean switchChecked() {
        checked = !checked;
        return checked;
    }
    public void setListName(String listName) { this.listName = listName; }
    public String getListName() { return listName; }
}
