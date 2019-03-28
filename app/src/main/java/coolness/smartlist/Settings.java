package coolness.smartlist;

import android.app.Activity;
import android.content.Context;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class Settings {
    private static ArrayList<ListItem> currentList = new ArrayList<>();
    private static ArrayList<PreviousItem> previousItems = new ArrayList<>();
    public static ArrayList<ListItem> getCurrentList() {
        return currentList;
    }
    public static void setCurrentList(ArrayList<ListItem> groceries) {
        currentList = groceries;
    }
    public static void addToList(ListItem grocery) {
        currentList.add(grocery);
    }
    public static void removeFromList(ListItem grocery) {
        currentList.remove(grocery);
    }
    public static boolean listContains(String grocery) {
        for (ListItem g : currentList) {
            if (g.getName().toLowerCase().equals(grocery.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
    public static void setChecked(ListItem g, boolean checked) {
        int index = currentList.indexOf(g);
        currentList.get(index).setChecked(checked);
    }
    public static void clearCheckedItems(Activity activity) {
        for (int i = currentList.size() - 1; i >= 0; i--) {
            if (currentList.get(i).isChecked()) {
                previousItems.add(new PreviousItem(currentList.get(i).getName(), new Date().getTime()));
                currentList.remove(i);
            }
        }
        writePreviousItems(activity);
        writeCurrentList(activity);
    }
    public static ArrayList<PreviousItem> getPreviousItems() { return previousItems; }
    public static void removeFromPreviousItems(PreviousItem item) {
        previousItems.remove(item);
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
            String currLine = p.getName() + "," + p.getMillisSinceAdded() + "," + p.getNumTimesBought() + "," + p.getLastSuggested() + "," + p.getNextSuggestion() + ",";
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
