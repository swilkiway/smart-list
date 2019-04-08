package coolness.smartlist;

import android.app.Activity;
import android.content.Context;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class Settings {
    private static int expirationWeeks = 6;
    private static ArrayList<ListItem> currentList = new ArrayList<>();
    private static ArrayList<PreviousItem> previousItems = new ArrayList<>();
    public static int getExpirationWeeks() { return expirationWeeks; }
    public static ArrayList<ListItem> getCurrentList() {
        return currentList;
    }
    public static void setCurrentList(ArrayList<ListItem> groceries) {
        currentList = groceries;
    }
    public static void addToList(ListItem grocery) {
        currentList.add(grocery);
    }
    public static void addToList(ArrayList<SuggestionItem> suggestions, Activity activity) {
        for (SuggestionItem s : suggestions) {
            addToList(new ListItem(s.getName()));
        }
        writeCurrentList(activity);
    }
    public static void removeFromList(ListItem grocery, Activity activity) {
        currentList.remove(grocery);
        writeCurrentList(activity);
    }
    public static boolean listContains(String grocery) {
        for (ListItem g : currentList) {
            if (g.getName().toLowerCase().equals(grocery.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
    public static void updatePrevItems(String name, long today) {
        for (PreviousItem p : previousItems) {
            if (p.getName().toLowerCase().equals(name)) {
                p.addTimeBought(today);
                return;
            }
        }
        previousItems.add(new PreviousItem(name, today));
    }
    public static void setChecked(ListItem g, boolean checked) {
        int index = currentList.indexOf(g);
        currentList.get(index).setChecked(checked);
    }
    public static void clearCheckedItems(Activity activity) {
        for (int i = currentList.size() - 1; i >= 0; i--) {
            if (currentList.get(i).isChecked()) {
                updatePrevItems(currentList.get(i).getName(), new Date().getTime());
                currentList.remove(i);
            }
        }
        writePreviousItems(activity);
        writeCurrentList(activity);
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
    public static void readCurrentList(Activity activity) {
        File file = new File(activity.getFilesDir(), "currList.txt");
        try {
            Scanner scanner = new Scanner(file);
            scanner.useDelimiter(",");
            while (scanner.hasNext()) {
                ListItem tmp = new ListItem();
                tmp.setName(scanner.next());
                tmp.setChecked(scanner.next().equals("y"));
                currentList.add(tmp);
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
    public static void writeCurrentList(Activity activity) {
        File file = new File(activity.getFilesDir(), "currList.txt");
        try {
            FileWriter fileWriter = new FileWriter(file);
            String currList = stringifyCurrList();
            fileWriter.write(currList);
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static String stringifyPrevItems() {
        StringBuilder sb = new StringBuilder();
        for (PreviousItem p : previousItems) {
            String currLine = p.getName() + "," + p.getMillisSinceAdded() + "," + p.getNumTimesBought() + "," + p.getLastSuggested() + "," + p.getNextSuggestion() + "," + p.getLastBought() + ",";
            sb.append(currLine);
        }
        return sb.toString();
    }
    private static String stringifyCurrList() {
        StringBuilder sb = new StringBuilder();
        for (ListItem g : currentList) {
            String currLine = g.getName() + "," + (g.isChecked() ? "y" : "n") + ",";
            sb.append(currLine);
        }
        return sb.toString();
    }
    public static void initialize(Activity activity) {
        readCurrentList(activity);
        readPreviousItems(activity);
    }
    public static void closeProgram(Activity activity) {
        writeCurrentList(activity);
        writePreviousItems(activity);
    }
    public static boolean isInitialized() {
        return (!currentList.isEmpty() || !previousItems.isEmpty());
    }
}
