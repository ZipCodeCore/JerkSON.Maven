package io.zipcoder;

// import io.zipcoder.utils.FileReader;

// public class GroceryReporter {
//     private final String originalFileText;

//     public GroceryReporter(String jerksonFileName) {
//         this.originalFileText = FileReader.readFile(jerksonFileName);
//     }

//     @Override
//     public String toString() {
//         return null;
//     }
// }

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import io.zipcoder.utils.FileReader;
import io.zipcoder.utils.Item;

public class GroceryReporter {
    private final String originalFileText;
    private final ItemParser itemParser;
    private List<Item> items;
    private int errorCount;

    public GroceryReporter(String jerksonFileName) {
        this.originalFileText = FileReader.readFile(jerksonFileName);
        this.itemParser = new ItemParser();
        this.items = itemParser.parseItemList(originalFileText);
        this.errorCount = itemParser.getErrorCount();
    }

    @Override
    public String toString() {
        StringBuilder report = new StringBuilder();
        
        // Group items by name (case-insensitive, but store in lowercase)
        Map<String, List<Item>> itemsByName = new HashMap<>();
        for (Item item : items) {
            String name = item.getName().toLowerCase();
            if (!itemsByName.containsKey(name)) {
                itemsByName.put(name, new ArrayList<>());
            }
            itemsByName.get(name).add(item);
        }
        
        // Use TreeMap to sort alphabetically (if needed)
        TreeMap<String, List<Item>> sortedItems = new TreeMap<>(itemsByName);
        
        // Process each item group
        for (Map.Entry<String, List<Item>> entry : sortedItems.entrySet()) {
            String name = entry.getKey();
            List<Item> itemList = entry.getValue();
            
            // Skip empty names
            if (name.isEmpty()) {
                continue;
            }
            
            // Add item header (matching expected format from tests)
            report.append(String.format("name:    %s \t\t seen: %d time%s\n", 
                    name, itemList.size(), itemList.size() == 1 ? "" : "s"));
            report.append("============= \t \t =============\n");
            
            // Group by prices
            Map<Double, List<Item>> itemsByPrice = new HashMap<>();
            for (Item item : itemList) {
                Double price = item.getPrice();
                if (!itemsByPrice.containsKey(price)) {
                    itemsByPrice.put(price, new ArrayList<>());
                }
                itemsByPrice.get(price).add(item);
            }
            
            // Process each price group
            boolean firstPrice = true;
            for (Map.Entry<Double, List<Item>> priceEntry : itemsByPrice.entrySet()) {
                Double price = priceEntry.getKey();
                List<Item> priceList = priceEntry.getValue();
                
                report.append(String.format("Price: \t %.2f\t\t seen: %d time%s\n", 
                        price, priceList.size(), priceList.size() == 1 ? "" : "s"));
                
                if (!firstPrice) {
                    report.append("-------------\t\t -------------\n");
                }
                firstPrice = false;
            }
            
            report.append("\n");
        }
        
        // Add error section if there were any errors
        if (errorCount > 0) {
            report.append(String.format("Errors         \t \t seen: %d time%s\n", 
                    errorCount, errorCount == 1 ? "" : "s"));
        }
        
        return report.toString();
    }
}