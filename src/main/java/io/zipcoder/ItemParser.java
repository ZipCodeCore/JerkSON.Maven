package io.zipcoder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.zipcoder.utils.Item;
import io.zipcoder.utils.ItemParseException;

public class ItemParser {
    
    private static final String OBJECT_DELIMITER = "##";
    private static final String PAIR_DELIMITER = ";";
    private static final String KEY_VALUE_PATTERN = "^\\s*([^:@^*%!]+)[:@^*%!](.*)$";
    
    private int errorCount = 0;
    
    /**
     * Parse a string containing multiple JerkSON items
     */
    public List<Item> parseItemList(String valueToParse) {
        List<Item> items = new ArrayList<>();
        
        String[] itemStrings = valueToParse.split(OBJECT_DELIMITER);
        
        for (String itemString : itemStrings) {
            if (itemString.trim().isEmpty()) {
                continue;
            }
            
            try {
                Item item = parseSingleItem(itemString);
                items.add(item);
            } catch (ItemParseException e) {
                errorCount++;
            }
        }
        
        return items;
    }
    
    /**
     * Parse a single JerkSON item
     */
    public Item parseSingleItem(String singleItem) throws ItemParseException {
        Map<String, String> itemData = new HashMap<>();
        
        // Remove trailing "##" if present
        if (singleItem.endsWith(OBJECT_DELIMITER)) {
            singleItem = singleItem.substring(0, singleItem.length() - OBJECT_DELIMITER.length());
        }
        
        String[] pairs = singleItem.split(PAIR_DELIMITER);
        Pattern pattern = Pattern.compile(KEY_VALUE_PATTERN);
        
        for (String pair : pairs) {
            Matcher matcher = pattern.matcher(pair);
            
            if (matcher.find()) {
                String key = matcher.group(1).trim().toLowerCase();
                String value = matcher.group(2).trim();
                
                itemData.put(key, value);
            } else {
                throw new ItemParseException();
            }
        }
        
        // Validate required fields
        validateRequiredFields(itemData);
        
        // Parse price as double
        Double price = parsePrice(itemData.get("price"));
        
        // Create Item with lowercase name and type
        return new Item(
            itemData.get("name").toLowerCase(),
            price,
            itemData.get("type").toLowerCase(),
            itemData.get("expiration")
        );
    }
    
    private void validateRequiredFields(Map<String, String> itemData) throws ItemParseException {
        if (!itemData.containsKey("name") || itemData.get("name").isEmpty()) {
            throw new ItemParseException();
        }
        
        if (!itemData.containsKey("price")) {
            throw new ItemParseException();
        }
        
        if (!itemData.containsKey("type")) {
            throw new ItemParseException();
        }
        
        if (!itemData.containsKey("expiration")) {
            throw new ItemParseException();
        }
    }
    
    private Double parsePrice(String priceString) throws ItemParseException {
        try {
            if (priceString != null && !priceString.isEmpty()) {
                return Double.parseDouble(priceString);
            } else {
                throw new ItemParseException();
            }
        } catch (NumberFormatException e) {
            throw new ItemParseException();
        }
    }
    
    public int getErrorCount() {
        return errorCount;
    }
}