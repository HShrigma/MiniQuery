package miniquery;

import org.junit.jupiter.api.Test;

import miniquery.engine.MiniQueryEngine;

import java.util.*;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

public class MiniQueryEngineTest {

    List<Map<String, Object>> getSampleData() {
        return List.of(
                Map.of("name", "alice", "age", 24, "member", true),
                Map.of("name", "bob", "age", 30, "member", false),
                Map.of("name", "carol", "age", 27, "member", true));
    }

    @Test
    public void query_ReturnsOnlyMatchingRows() {
        var data = getSampleData();

        Predicate<Map<String, Object>> ageOver25 = row -> (int) row.get("age") > 25;
        var result = MiniQueryEngine.query(data, ageOver25);

        assertEquals(2, result.size());
        assertEquals("bob", result.get(0).get("name"));
        assertEquals("carol", result.get(1).get("name"));
    }

    @Test
    public void query_WithAlwaysFalseReturnsEmpty() {
        var data = getSampleData();

        var result = MiniQueryEngine.query(data, row -> false);
        assertTrue(result.isEmpty());
    }

    @Test
    public void query_WithAlwaysTrueReturnsAll() {
        var data = getSampleData();

        var result = MiniQueryEngine.query(data, row -> true);
        assertEquals(data.size(), result.size());
    }

    @Test
    public void query_DoesNotModifyOriginalData() {
        var data = new ArrayList<>(getSampleData());

        var cloneBefore = new ArrayList<>(data); // shallow copy is enough here
        MiniQueryEngine.query(data, row -> (boolean) row.get("member"));

        assertEquals(cloneBefore, data); // original must stay unchanged
    }

    @Test
    public void testSelectSpecificColumns() {
        List<Map<String,Object>> data = List.of(
                Map.of("name", "Alice", "age", 25, "email", "a@a.com"),
                Map.of("name", "Bob", "age", 35, "email", "b@b.com"));

        List<Map<String,Object>> result = MiniQueryEngine.select(data, row -> true, "name", "email");

        assertEquals(2, result.size());
        assertTrue(result.get(0).containsKey("name"));
        assertTrue(result.get(0).containsKey("email"));
        assertFalse(result.get(0).containsKey("age"));
    }
    @Test
    public void testSelectAllColumns() {
        List<Map<String,Object>> data = List.of(
            Map.of("name", "Charlie", "age", 40)
        );
    
        var result = MiniQueryEngine.select(data, row -> true);
    
        assertEquals(1, result.size());
        assertEquals("Charlie", result.get(0).get("name"));
        assertEquals(40, result.get(0).get("age"));
    }
    
}
