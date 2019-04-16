package coolness.smartlist.model;

import coolness.smartlist.Constants;
import coolness.smartlist.ListManager;

public class PreviousItem {
    public PreviousItem() {}
    public PreviousItem(String name, long today, String listName) {
        this.name = name;
        this.listName = listName;
        numTimesBought = 1;
        millisSinceAdded = today;
        lastSuggested = today;
        lastBought = today;
        nextSuggestion = Constants.initialSuggestion;
    }
    private String name;
    private long millisSinceAdded;
    private int numTimesBought;
    private long lastSuggested;
    private long lastBought;
    private int nextSuggestion;
    private String listName;
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public long getMillisSinceAdded() { return millisSinceAdded; }
    public void setMillisSinceAdded(long day) { millisSinceAdded = day; }
    private int getDaysSinceAdded(long today) { return (int)((today - millisSinceAdded)/Constants.millisOneDay); }
    private int getDaysSinceSuggested(long today) { return (int)((today - lastSuggested + Constants.millisEightHours)/Constants.millisOneDay); }
    public int getWeeksSinceBought(long today) { return (int)((today - lastBought)/Constants.millisOneWeek) + 1; }
    public int getNumTimesBought() { return numTimesBought; }
    public void setNumTimesBought(int num) { numTimesBought = num; }
    public long getLastBought() { return lastBought; }
    public void setLastBought(long bought) { lastBought = bought; }
    public void addTimeBought(long today) {
        numTimesBought++;
        lastBought = today;
        updateSuggestion(today);
    }
    public String getListName() { return listName; }
    public void setListName(String listName) { this.listName = listName; }
    public long getLastSuggested() { return lastSuggested; }
    public void setLastSuggested(long suggested) {
        lastSuggested = suggested;
        updateSuggestion(suggested);
    }
    public int getNextSuggestion() { return nextSuggestion; }
    public void setNextSuggestion(int suggestion) { nextSuggestion = suggestion; }
    public int getFrequency(long today) { return getDaysSinceAdded(today) / numTimesBought; }
    public boolean shouldBeSuggested(long today) {
        if (!ListManager.listContains(name)) {
            return (getDaysSinceSuggested(today) >= nextSuggestion);
        } else {
            return false;
        }
    }

    public boolean hasExpired(long today) {
        return ((today - lastBought) > (604800000L * ListManager.getExpirationWeeks()));
    }

    private void updateSuggestion(long today) {
        lastSuggested = today;
        nextSuggestion = getFrequency(today);
    }
}
