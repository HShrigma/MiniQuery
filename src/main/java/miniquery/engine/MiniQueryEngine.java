package miniquery.engine;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public class MiniQueryEngine {

    public static List<Map<String, Object>> query(
            List<Map<String, Object>> table,
            Predicate<Map<String, Object>> condition) {

        List<Map<String, Object>> result = new ArrayList<>();

        for (Map<String, Object> row : table) {
            if (condition.test(row)) {
                result.add(row);
            }
        }
        return result;
    }

    public static List<Map<String, Object>> select(
            List<Map<String, Object>> table,
            Predicate<Map<String, Object>> condition,
            String... columns) {

        List<Map<String, Object>> result = new ArrayList<>();

        // Loop each row
        for (Map<String, Object> row : table) {
            // Check if condition is valid
            if (condition.test(row)) {
                // Get only keys that match columns
                if (columns.length == 0)
                    result.add(row); // For no columns treat it like SELECT *
                else {
                    Map<String, Object> temp = new HashMap<>();
                    for (String col : columns) {
                        if (row.containsKey(col)) {
                            temp.put(col, row.get(col));
                        }
                    }
                    result.add(temp);
                }
            }
        }
        return result;
    }

    public static List<Map<String, Object>> limit(
            List<Map<String, Object>> table,
            int entries) {
        if (entries >= table.size())
            return table;

        List<Map<String, Object>> result = new ArrayList<>();
        for (var row : table) {
            if (entries <= 0)
                break;
            result.add(row);
            entries--;
        }
        return result;
    }

    public static List<Map<String, Object>> orderBy(
            List<Map<String, Object>> table,
            String orderKey,
            boolean ascending) {

        // Create a shallow copy so we don't modify original
        List<Map<String, Object>> sorted = new ArrayList<>(table);

        // Define the comparator
        Comparator<Map<String, Object>> comparator = (row1, row2) -> {
            Object val1 = row1.get(orderKey);
            Object val2 = row2.get(orderKey);

            // Handle nulls
            if (val1 == null && val2 == null)
                return 0;
            if (val1 == null)
                return 1;
            if (val2 == null)
                return -1;

            try {
                Comparable<Object> c1 = (Comparable<Object>) val1;
                Comparable<Object> c2 = (Comparable<Object>) val2;
                return c1.compareTo(c2);
            } catch (ClassCastException e) {
                System.err.println("Values for key '" + orderKey + "' are not comparable.");
                return 0;
            }
        };

        // Apply ascending/descending
        if (!ascending) {
            comparator = comparator.reversed();
        }

        // Sort
        sorted.sort(comparator);

        return sorted;
    }

    public static List<Map<String, Object>> distinct(List<Map<String, Object>> table) {
        Set<String> seen = new HashSet<>();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Map<String, Object> row : table) {
            // Serialize row deterministically
            StringBuilder keyBuilder = new StringBuilder();
            row.keySet().stream().sorted().forEach(k -> {
                keyBuilder.append(k).append("=").append(row.get(k)).append(";");
            });
            String key = keyBuilder.toString();

            if (!seen.contains(key)) {
                seen.add(key);
                result.add(row);
            }
        }

        return result;
    }

}