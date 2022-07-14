package GUI;

import java.util.ArrayList;
import GUI.Pair;

import javax.management.MBeanParameterInfo;

public class Table {
    String name;
    String dbName;
    ArrayList<String> subTables;
    ArrayList<String> attributes;
    ArrayList<DBObject> items;

    @Override
    public String toString() {
        return name;
    }

    public Table(String myname, String mydbname){
        name = myname;
        dbName = mydbname;
        attributes = new ArrayList<>();
        subTables = new ArrayList<>();
        items = new ArrayList<>();
        if(name.equals("Player") || name.equals("Games")){
            subTables.add("Player_stats");
        }
        if(name.equals("Team")){
            subTables.add("Player");
        }
        if(name.equals("Conference") || name.equals("Games")){
            subTables.add("Team");
        }
        if(name.equals("Games") || name.equals("Team")){
            subTables.add("Team_stats");
        }
        if(name.equals("Stadium") || name.equals("Team")){
            subTables.add("Games");
        }
    }

    public void setItems(ArrayList<DBObject> items) {
        this.items = new ArrayList<>();
        this.items = items;
    }

    public static <T> ArrayList<T> removeDuplicates(ArrayList<T> list)
    {
        //Borrowed from https://www.geeksforgeeks.org/how-to-remove-duplicates-from-arraylist-in-java/
        // Create a new ArrayList
        ArrayList<T> newList = new ArrayList<T>();
        // Traverse through the first list
        for (T element : list) {
            // If this element is not present in newList
            // then add it
            if (!newList.contains(element)) {
                newList.add(element);
            }
        }
        // return the new list
        return newList;
    }
}
