package miniquery;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import miniquery.csvreader.CSVReader;

public class CSVReaderTest {
    String firstTestpath = "src/main/resources/store/keystest.csv";
    File file;

    @BeforeEach
    void init() throws IOException{
        String csvRaw = "name,age,phone,\n" + //
                        "joe,23,0288288222,\n" + //
                        "schmo,33,0288288333";

        file =  new File(firstTestpath);
        if(file.createNewFile()){
            FileWriter writer = new FileWriter(file);
            writer.write(csvRaw);
            writer.close();
        }
        
    }
    @Test
    public void CSVReaderExists() {
        assertNotNull(new CSVReader().getClass());
    }

    @Test
    public void CSVReaderGetsPath() {
        assertNotNull(CSVReader.Load(firstTestpath));
    }

    @Test
    public void CSVReaderGetsKeys() {
        var list = CSVReader.Load(firstTestpath);
        // expected: name,age,phone
        if(list.isEmpty()){
            assertTrue(false);
            return;
        }
        assertTrue(list.get(0).containsKey("name"));
        assertTrue(list.get(0).containsKey("age"));
        assertTrue(list.get(0).containsKey("phone"));
        assertTrue(list.get(0).keySet().size() == 3);
    }

    @Test
    public void CSVReaderGetsValues(){
        var list = CSVReader.Load(firstTestpath);
        Map<String,String> expected1 = new HashMap<String,String>();
        expected1.put("name","joe");
        expected1.put("age","23");
        expected1.put("phone","0288288222");
        Map<String,String> expected2 = new HashMap<String,String>();
        expected2.put("name","schmo");
        expected2.put("age","33");
        expected2.put("phone","0288288333");
        assertEquals(expected1, list.get(0));
        assertEquals(expected2, list.get(1));
        assertTrue(list.size() == 2);
    }
    @AfterEach
    void clean()
    {
        file.delete();
    }
}