package miniquery.csvreader;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CSVReader {

    public static List<Map<String, String>> Load(String path) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(path)));
            List<Map<String, String>> res = new ArrayList<Map<String, String>>();
            List<String> keys = new ArrayList<String>();
            boolean hasKeys = false;

            while (reader.ready()) {
                if (hasKeys) {
                    Map<String, String> map = lineToMapWithValidation(reader.readLine(), keys);
                    if(map != null){
                        res.add(map);
                    }
                } else {
                    keys = getKeys(reader.readLine());
                    hasKeys = true;
                }
            }

            return res;

        } catch (Exception e) {
            System.err.println("[ERROR] getMapFromCSV encoutnered and Exception: ");
            e.printStackTrace();
            return null;
        }

    }

    static String getLineError(String[] line) {
        StringBuilder s = new StringBuilder();
        for (String item : line) {
            s.append(item).append(",");
        }
        return s.toString();

    }

    static List<String> getKeys(String input) {
        String[] split = input.split(",");
        List<String> keys = Arrays.asList(split);
        // keys.remove(keys.size() - 1);
        return keys;
    }

    static Map<String, String> lineToMapWithValidation(String line, List<String> keys) {
        String[] values = line.split(",");
        if (values.length == keys.size() || values.length == keys.size() + 1) {
            return lineToMap(values, keys);
        }
        System.err.println("[ERRROR] Invalid Length of Values: " + values.length);
        return null;
    }

    static Map<String, String> lineToMap(String[] values, List<String> keys) {
        Map<String, String> map = new HashMap<String, String>();
        for (int i = 0; i < keys.size(); i++) {
            map.put(keys.get(i), values[i]);
        }
        return map;
    }
}