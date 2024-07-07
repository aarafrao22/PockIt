package com.example.pockeitt.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpandableListDataPump {

    private static HashMap<String, List<String>> expandableListDetail;

    static {
        expandableListDetail = new HashMap<>();
        List<String> bills = new ArrayList<>();
//        bills.add("➕ Add Bills here");
//        bills.add("➕ Add Bills here");
//        bills.add("➕ Add Bills here");
//        bills.add("➕ Add Bills here");

        List<String> needs = new ArrayList<>();
//        needs.add("Add Needs here");

        List<String> wants = new ArrayList<>();
//        wants.add("Add Wants here");

        List<String> savings = new ArrayList<>();
//        savings.add("Add Savings here");

        expandableListDetail.put("Bills", bills);
        expandableListDetail.put("Needs", needs);
        expandableListDetail.put("Wants", wants);
        expandableListDetail.put("Savings", savings);
    }

    public static HashMap<String, List<String>> getData() {
        return expandableListDetail;
    }

    public static List<String> getList(String key) {
        return expandableListDetail.get(key);
    }

    public static void addItem(String key, String item) {
        List<String> list = expandableListDetail.get(key);
        if (list != null) {
            list.add(item);
        } else {
            list = new ArrayList<>();
            list.add(item);
            expandableListDetail.put(key, list);
        }
    }

    public static void removeItem(String key, String item) {
        List<String> list = expandableListDetail.get(key);
        if (list != null) {
            list.remove(item);
        }
    }

    public static void updateItem(String key, int index, String newItem) {
        List<String> list = expandableListDetail.get(key);
        if (list != null && index >= 0 && index < list.size()) {
            list.set(index, newItem);
        }
    }
}
