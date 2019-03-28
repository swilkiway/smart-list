package coolness.smartlist;

public class PreviousItem {
    public PreviousItem() {}
    public PreviousItem(String name, long today) {
        this.name = name;
        numTimesBought = 1;
        millisSinceAdded = today;
        lastSuggested = today;
        nextSuggestion = 1;
    }
    private String name;
    private long millisSinceAdded;
    private int numTimesBought;
    private long lastSuggested;
    private int nextSuggestion;
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public long getMillisSinceAdded() { return millisSinceAdded; }
    public void setMillisSinceAdded(long day) { millisSinceAdded = day; }
    public int getDaysSinceAdded(long today) { return (int)((today - millisSinceAdded)/86400000); }
    public int getDaysSinceSuggested(long today) { return (int)((today - lastSuggested)/86400000); }
    public int getNumTimesBought() { return numTimesBought; }
    public void setNumTimesBought(int num) { numTimesBought = num; }
    public void addTimeBought(long today) {
        numTimesBought++;
        updateSuggestion(today);
    }
    public long getLastSuggested() { return lastSuggested; }
    public void setLastSuggested(long suggested) { lastSuggested = suggested; }
    public int getNextSuggestion() { return nextSuggestion; }
    public void setNextSuggestion(int suggestion) { nextSuggestion = suggestion; }
    public int getFrequency(long today) { return getDaysSinceAdded(today) / numTimesBought; }
    public boolean shouldBeSuggested(long today) {
        if (!Settings.listContains(name)) {
            return (getDaysSinceSuggested(today) >= nextSuggestion);
        } else {
            return false;
        }
    }

    public void updateSuggestion(long today) {
        lastSuggested = today;
        nextSuggestion = getFrequency(today);
    }
}
