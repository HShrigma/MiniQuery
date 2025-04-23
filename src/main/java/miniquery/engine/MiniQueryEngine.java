package miniquery.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
}