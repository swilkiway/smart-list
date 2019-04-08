package coolness.smartlist;

public class SuggestionItem {
    private String name;
    private int weeksSinceBought;
    private boolean checked;
    public SuggestionItem(String name, long millis) {
        this.name = name;
        weeksSinceBought = (int)(millis / 604800000L);
        checked = true;
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
}
