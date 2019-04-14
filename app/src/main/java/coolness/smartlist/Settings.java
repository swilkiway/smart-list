package coolness.smartlist;

import android.app.Activity;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class Settings {
    private static int expirationWeeks = 6;
    private static UpdateManager updateManager;
    private static ArrayList<CurrentList> currentLists = new ArrayList<>();
    private static String currentListName;
    private static ArrayList<ListItem> currentList = new ArrayList<>();
    private static ArrayList<PreviousItem> previousItems = new ArrayList<>();
    public static int getExpirationWeeks() { return expirationWeeks; }
    public static ArrayList<ListItem> getCurrentList() {
        return currentList;
    }
    public static ArrayList<CurrentList> getCurrentLists() { return currentLists; }
    public static void addCurrentList(CurrentList c) {
        currentLists.add(c);
        currentList = new ArrayList<>();
        currentListName = c.getName();
        updateManager.newListCreated();
    }
    public static ArrayList<String> getCurrentListNames() {
        ArrayList<String> tmp = new ArrayList<>();
        for (CurrentList c : currentLists) {
            tmp.add(c.getName());
        }
        tmp.add("add new list");
        return tmp;
    }
    public static void setCurrentList(String listName) {
        for (int i = 0; i < currentLists.size(); i++) {
            if (currentLists.get(i).getName().equals(currentListName)) {
                currentLists.get(i).setItems(currentList);
            }
        }
        for (int i = 0; i < currentLists.size(); i++) {
            if (currentLists.get(i).getName().equals(listName)) {
                currentList = currentLists.get(i).getItems();
                currentListName = listName;
            }
        }
        updateManager.listSwitched(listName);
    }
    public static void addToList(ListItem grocery) {
        currentList.add(grocery);
    }
    public static void addToList(ArrayList<SuggestionItem> suggestions, Activity activity) {
        for (SuggestionItem s : suggestions) {
            addToList(new ListItem(s.getName()));
        }
        writeCurrentLists(activity);
    }
    public static void removeFromList(ListItem grocery, Activity activity) {
        currentList.remove(grocery);
        writeCurrentLists(activity);
    }
    public static boolean listContains(String grocery) {
        for (ListItem g : currentList) {
            if (g.getName().toLowerCase().equals(grocery.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
    public static void updatePrevItems(String name, long today, String listName) {
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
                suggestions.add(new SuggestionItem(p.getName(), p.getWeeksSinceBought(today)));
                p.setLastSuggested(today);
            }
        }
        return (suggestions.size() > 0 ? suggestions : null);
    }
    public static void readPreviousItems(Activity activity) {
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

    //TODO: need to redo this
    public static void readCurrentLists(Activity activity) {
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

        }
    }
    public static void writePreviousItems(Activity activity) {
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
    public static void writeCurrentLists(Activity activity) {
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
    }
    public static void initializeUpdateManager(CurrListFragment fragment) {
        updateManager = new UpdateManager(fragment);
    }
    public static void closeProgram(Activity activity) {
        writeCurrentLists(activity);
        writePreviousItems(activity);
    }
    public static boolean isInitialized() {
        return (!currentList.isEmpty() || !previousItems.isEmpty());
    }
}
