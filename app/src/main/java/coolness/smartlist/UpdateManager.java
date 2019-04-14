package coolness.smartlist;

public class UpdateManager {
    public UpdateManager(CurrListFragment fragment) {
        currList = fragment;
    }
    private CurrListFragment currList;
    public void registerCurrList(CurrListFragment c) { currList = c; }
    public CurrListFragment getCurrList() { return currList; }
    public void newListCreated() {
        currList.updateLists();
    }
    public void listSwitched(String listName) {
        currList.switchLists(listName);
    }
}
