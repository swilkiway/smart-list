package coolness.smartlist;

import coolness.smartlist.fragment.CurrListFragment;

public class UpdateManager {
    public UpdateManager(CurrListFragment fragment) {
        currList = fragment;
    }
    private CurrListFragment currList;
    public void listsChanged() {
        currList.updateLists();
    }
    public void listSwitched(String listName) {
        currList.switchLists(listName);
    }
}
