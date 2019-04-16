package coolness.smartlist;

import android.app.Activity;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import coolness.smartlist.fragment.CurrListFragment;
import coolness.smartlist.model.CurrentList;
import coolness.smartlist.model.ListItem;
import coolness.smartlist.model.PreviousItem;
import coolness.smartlist.model.SuggestionItem;

public class ListManager {
    private static int expirationWeeks = 6;
    private static UpdateManager updateManager;
    private static ArrayList<CurrentList> currentLists = new ArrayList<>();
    private static String currentListName;
    private static ArrayList<ListItem> currentList = new ArrayList<>();
    private static ArrayList<PreviousItem> previousItems = new ArrayList<>();
    public static String getCurrentListName() { return currentListName; }
    public static int getExpirationWeeks() { return expirationWeeks; }
    public static ArrayList<ListItem> getCurrentList() {
        return currentList;
    }
    public static void setCurrentListName(String listName, Activity activity) {
        for (CurrentList c : currentLists) {
            if (c.getName().equals(currentListName)) {
                c.setName(listName);
            }
        }
        currentListName = listName;
        writeCurrentLists(activity);
        updateManager.listsChanged();
    }
    public static void addCurrentList(CurrentList c, Activity activity) {
        currentLists.add(c);
        currentList = new ArrayList<>();
        currentListName = c.getName();
        writeCurrentLists(activity);
        updateManager.listsChanged();
        updateManager.listSwitched(c.getName());
    }
    public static ArrayList<String> getCurrentListNames() {
        ArrayList<String> tmp = new ArrayList<>();
        for (CurrentList c : currentLists) {
            tmp.add(c.getName());
        }
        tmp.add("add new list");
        return tmp;
    }
    public static boolean alreadyUsedListName(String name) {
        for (CurrentList c : currentLists) {
            if (c.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
    public static void setCurrentList(String listName) {
        for (CurrentList c : currentLists) {
            if (c.getName().equals(currentListName)) {
                c.setItems(currentList);
                c.switchCurrent();
            }
        }
        for (CurrentList c : currentLists) {
            if (c.getName().equals(listName)) {
                currentList = c.getItems();
                currentListName = listName;
                c.switchCurrent();
            }
        }
        updateManager.listSwitched(listName);
    }
    public static void addToList(ListItem grocery) {
        currentList.add(grocery);
    }
    public static void addToLists(ArrayList<SuggestionItem> suggestions, Activity activity) {
        for (SuggestionItem s : suggestions) {
            for (CurrentList c : currentLists) {
                if (c.getName().equals(s.getListName())) {
                    c.addListItem(new ListItem(s.getName()));
                }
            }
        }
        writeCurrentLists(activity);
    }
    public static void removeFromList(ListItem grocery, Activity activity) {
        currentList.remove(grocery);
        writeCurrentLists(activity);
    }
    public static boolean listContains(String grocery) {
        for (CurrentList c : currentLists) {
            for (ListItem g : c.getItems()) {
                if (g.getName().toLowerCase().equals(grocery.toLowerCase())) {
                    return true;
                }
            }
        }
        return false;
    }
    private static void updatePrevItems(String name, long today, String listName) {
        for (PreviousItem p : previousItems) {
            if (p.getName().toLowerCase().equals(name)) {
                p.addTimeBought(today);
                return;
            }
        }
        previousItems.add(new PreviousItem(name, today, listName));
    }
    public static void setChecked(ListItem g, boolean checked) {
        int index = currentList.indexOf(g);
        currentList.get(index).setChecked(checked);
    }
    public static void clearCheckedItems(Activity activity) {
        for (int i = currentList.size() - 1; i >= 0; i--) {
            if (currentList.get(i).isChecked()) {
                updatePrevItems(currentList.get(i).getName(), new Date().getTime(), currentListName);
                currentList.remove(i);
            }
        }
        writePreviousItems(activity);
        writeCurrentLists(activity);
    }
    public static void modifyListItem(String oldName, String newName, Activity activity) {
        for (int i = 0; i < currentList.size(); i++) {
            if (currentList.get(i).getName().equals(oldName)) {
                currentList.get(i).setName(newName);
                break;
            }
        }
        writeCurrentLists(activity);
    }
    public static ArrayList<PreviousItem> getPreviousItems() { return previousItems; }
    public static void removeFromPreviousItems(PreviousItem item, Activity activity) {
        previousItems.remove(item);
        writePreviousItems(activity);
    }
    private static void removeExpiredItems(long today) {
        for (int i = previousItems.size() - 1; i >= 0; i--) {
            if (previousItems.get(i).hasExpired(today)) {
                previousItems.remove(i);
            }
        }
    }
    public static ArrayList<SuggestionItem> getSuggestions(long today) {
        removeExpiredItems(today);
        ArrayList<SuggestionItem> suggestions = new ArrayList<>();
        for (PreviousItem p : previousItems) {
            if (p.shouldBeSuggested(today)) {
                suggestions.add(new SuggestionItem(p.getName(), p.getWeeksSinceBought(today), p.getListName()));
                p.setLastSuggested(today);
            }
        }
        return (suggestions.size() > 0 ? suggestions : null);
    }
    private static int findCurrentList() {
        for (int i = 0; i < currentLists.size(); i++) {
            if (currentLists.get(i).isCurrentList()) {
                return i;
            }
        }
        return 0;
    }
    public static void deleteCurrentList(Activity activity) {
        for (int i = currentLists.size() - 1; i >= 0; i--) {
            if (currentLists.get(i).getName().equals(currentListName)) {
                currentLists.remove(i);
            }
        }
        for (int i = previousItems.size() - 1; i >= 0; i--) {
            if (previousItems.get(i).getListName().equals(currentListName)) {
                previousItems.remove(i);
            }
        }
        if (currentLists.size() > 0) {
            currentList = currentLists.get(0).getItems();
            currentListName = currentLists.get(0).getName();
        } else {
            currentList = new ArrayList<>();
            currentListName = "Unnamed list";
            currentLists.add(new CurrentList("Unnamed list"));
        }
        updateManager.listsChanged();
        updateManager.listSwitched(currentListName);
        writeCurrentLists(activity);
        writePreviousItems(activity);
    }
    private static void readPreviousItems(Activity activity) {
        File file = new File(activity.getFilesDir(), "prevItems.txt");
        try {
            Scanner scanner = new Scanner(file);
            scanner.useDelimiter(",");
            while (scanner.hasNext()) {
                PreviousItem tmp = new PreviousItem();
                tmp.setName(scanner.next());
                tmp.setListName(scanner.next());
                tmp.setMillisSinceAdded(scanner.nextLong());
                tmp.setNumTimesBought(scanner.nextInt());
                tmp.setLastSuggested(scanner.nextLong());
                tmp.setNextSuggestion(scanner.nextInt());
                tmp.setLastBought(scanner.nextLong());
                previousItems.add(tmp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void readCurrentLists(Activity activity) {
        File file = new File(activity.getFilesDir(), "currList.txt");
        try {
            Scanner scanner = new Scanner(file);
            scanner.useDelimiter(",");
            while (scanner.hasNext()) {
                CurrentList tmpList = new CurrentList(scanner.next());
                tmpList.setCurrent(scanner.next().equals("y"));
                int listSize = scanner.nextInt();
                ArrayList<ListItem> tmpItems = new ArrayList<>();
                for (int i = 0; i < listSize; i++) {
                    ListItem tmp = new ListItem();
                    tmp.setName(scanner.next());
                    tmp.setChecked(scanner.next().equals("y"));
                    tmpItems.add(tmp);
                }
                tmpList.setItems(tmpItems);
                currentLists.add(tmpList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void writePreviousItems(Activity activity) {
        File file = new File(activity.getFilesDir(), "prevItems.txt");
        try {
            FileWriter fileWriter = new FileWriter(file);
            String prevItems = stringifyPrevItems();
            fileWriter.write(prevItems);
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void writeCurrentLists(Activity activity) {
        File file = new File(activity.getFilesDir(), "currList.txt");
        try {
            FileWriter fileWriter = new FileWriter(file);
            String currLists = stringifyCurrLists();
            fileWriter.write(currLists);
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static String stringifyPrevItems() {
        StringBuilder sb = new StringBuilder();
        for (PreviousItem p : previousItems) {
            String currLine = p.getName() + "," + p.getListName() + "," + p.getMillisSinceAdded() + "," + p.getNumTimesBought() + "," + p.getLastSuggested() + "," + p.getNextSuggestion() + "," + p.getLastBought() + ",";
            sb.append(currLine);
        }
        return sb.toString();
    }

    private static String stringifyCurrLists() {
        StringBuilder sb = new StringBuilder();
        for (CurrentList c : currentLists) {
            sb.append(c.getInfoStringify());
            for (ListItem g : c.getItems()) {
                String currLine = g.getName() + "," + (g.isChecked() ? "y," : "n,");
                sb.append(currLine);
            }
        }
        return sb.toString();
    }

    public static void initialize(Activity activity) {
        readCurrentLists(activity);
        readPreviousItems(activity);
        if (currentLists.size() > 0) {
            int index = findCurrentList();
            currentListName = currentLists.get(index).getName();
            currentList = currentLists.get(index).getItems();
            currentLists.get(index).setCurrent(true);
        } else {
            currentListName = "Unnamed list";
            currentList = new ArrayList<>();
            CurrentList c = new CurrentList("Unnamed list");
            c.setCurrent(true);
            currentLists.add(c);
        }
    }
    public static void initializeUpdateManager(CurrListFragment fragment) {
        updateManager = new UpdateManager(fragment);
    }

    public static boolean isInitialized() {
        return (!currentList.isEmpty() || !previousItems.isEmpty());
    }
}
