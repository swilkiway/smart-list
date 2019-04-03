package coolness.smartlist;

public class PreviousItem {
    public PreviousItem() {}
    public PreviousItem(String name, long today) {
        this.name = name;
        numTimesBought = 1;
        millisSinceAdded = today;
        lastSuggested = today;
        lastBought = today;
        nextSuggestion = 1;
    }
    private String name;
    private long millisSinceAdded;
    private int numTimesBought;
    private long lastSuggested;
    private long lastBought;
    private int nextSuggestion;
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public long getMillisSinceAdded() { return millisSinceAdded; }
    public void setMillisSinceAdded(long day) { millisSinceAdded = day; }
    public int getDaysSinceAdded(long today) { return (int)((today - millisSinceAdded)/86400000); }
    public int getDaysSinceSuggested(long today) { return (int)((today - lastSuggested + 21600000)/86400000); }
    public int getWeeksSinceBought(long today) { return (int)((today - lastBought)/604800000L) + 1; }
    public int getNumTimesBought() { return numTimesBought; }
    public void setNumTimesBought(int num) { numTimesBought = num; }
    public long getLastBought() { return lastBought; }
    public void setLastBought(long bought) { lastBought = bought; }
    public void addTimeBought(long today) {
        numTimesBought++;
        lastBought = today;
        updateSuggestion(today);
    }
    public long getLastSuggested() { return lastSuggested; }
    public void setLastSuggested(long suggested) {
        lastSuggested = suggested;
        updateSuggestion(suggested);
    }
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

    public boolean hasExpired(long today) {
        return ((today - lastBought) > (604800000L * Settings.getExpirationWeeks()));
    }

    public void updateSuggestion(long today) {
        lastSuggested = today;
        nextSuggestion = getFrequency(today) - 1;
    }
}
