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
        List<Map<String, Object>> data = List.of(
                Map.of("name", "Alice", "age", 25, "email", "a@a.com"),
                Map.of("name", "Bob", "age", 35, "email", "b@b.com"));

        List<Map<String, Object>> result = MiniQueryEngine.select(data, row -> true, "name", "email");

        assertEquals(2, result.size());
        assertTrue(result.get(0).containsKey("name"));
        assertTrue(result.get(0).containsKey("email"));
        assertFalse(result.get(0).containsKey("age"));
    }

    @Test
    public void testSelectAllColumns() {
        List<Map<String, Object>> data = List.of(
                Map.of("name", "Charlie", "age", 40));

        var result = MiniQueryEngine.select(data, row -> true);

        assertEquals(1, result.size());
        assertEquals("Charlie", result.get(0).get("name"));
        assertEquals(40, result.get(0).get("age"));
    }

    @Test
    public void testLimitBasic() {
        List<Map<String, Object>> data = List.of(
                Map.of("id", 1),
                Map.of("id", 2),
                Map.of("id", 3));

        List<Map<String, Object>> result = MiniQueryEngine.limit(data, 2);

        assertEquals(2, result.size());
        assertEquals(1, result.get(0).get("id"));
        assertEquals(2, result.get(1).get("id"));
    }

    @Test
    public void testLimitExceedsSize() {
        List<Map<String, Object>> data = List.of(
                Map.of("id", 1),
                Map.of("id", 2));

        var result = MiniQueryEngine.limit(data, 10);
        assertEquals(2, result.size());
    }

    @Test
    public void testLimitZeroOrNegative() {
        List<Map<String, Object>> data = List.of(
                Map.of("id", 1));

        var result = MiniQueryEngine.limit(data, 0);
        assertEquals(0, result.size());

        var resultNeg = MiniQueryEngine.limit(data, -5);
        assertEquals(0, resultNeg.size());
    }

    @Test
    void testOrderByAscending() {
        List<Map<String, Object>> table = new ArrayList<>();

        table.add(Map.of("name", "Joe", "age", 34));
        table.add(Map.of("name", "Ana", "age", 27));
        table.add(Map.of("name", "Zed", "age", 45));

        List<Map<String, Object>> result = MiniQueryEngine.orderBy(table, "age", true);

        assertEquals("Ana", result.get(0).get("name"));
        assertEquals("Joe", result.get(1).get("name"));
        assertEquals("Zed", result.get(2).get("name"));
    }

    @Test
    void testOrderByDescending() {
        List<Map<String, Object>> table = new ArrayList<>();

        table.add(Map.of("name", "Joe", "age", 34));
        table.add(Map.of("name", "Ana", "age", 27));
        table.add(Map.of("name", "Zed", "age", 45));

        List<Map<String, Object>> result = MiniQueryEngine.orderBy(table, "age", false);

        assertEquals("Zed", result.get(0).get("name"));
        assertEquals("Joe", result.get(1).get("name"));
        assertEquals("Ana", result.get(2).get("name"));
    }

    @Test
    void testOrderByNullsHandled() {
        Map<String, Object> joe = new HashMap<>();
        joe.put("name", "Joe");
        joe.put("age", null);

        Map<String, Object> ana = new HashMap<>();
        ana.put("name", "Ana");
        ana.put("age", 27);

        Map<String, Object> zed = new HashMap<>();
        zed.put("name", "Zed");
        zed.put("age", 45);

        List<Map<String, Object>> table = List.of(ana, zed, joe);

        List<Map<String, Object>> result = MiniQueryEngine.orderBy(table, "age", true);

        // Expect Joe (null) to come last
        assertEquals("Ana", result.get(0).get("name"));
        assertEquals("Zed", result.get(1).get("name"));
        assertEquals("Joe", result.get(2).get("name"));
    }

    @Test
    void testOrderByNonComparable() {
        List<Map<String, Object>> table = new ArrayList<>();

        table.add(Map.of("name", "Joe", "age", new Object()));
        table.add(Map.of("name", "Ana", "age", 27));

        // Shouldn't crash, prints warning, returns original
        List<Map<String, Object>> result = MiniQueryEngine.orderBy(table, "age", true);
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testDistinctRemovesDuplicates() {
        List<Map<String, Object>> table = new ArrayList<>();

        table.add(Map.of("name", "Ana", "age", 27));
        table.add(Map.of("name", "Joe", "age", 30));
        table.add(Map.of("name", "Ana", "age", 27)); // Duplicate
        table.add(Map.of("name", "Joe", "age", 30)); // Duplicate

        var result = MiniQueryEngine.distinct(table);

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(row -> row.get("name").equals("Ana")));
        assertTrue(result.stream().anyMatch(row -> row.get("name").equals("Joe")));
    }

    @Test
    void testDistinctIgnoresKeyOrder() {
        List<Map<String, Object>> table = new ArrayList<>();

        Map<String, Object> row1 = new LinkedHashMap<>();
        row1.put("name", "Ana");
        row1.put("age", 27);

        Map<String, Object> row2 = new LinkedHashMap<>();
        row2.put("age", 27); // Swapped order
        row2.put("name", "Ana");

        table.add(row1);
        table.add(row2);

        var result = MiniQueryEngine.distinct(table);
        assertEquals(1, result.size());
    }

    @Test
    void testDistinctWithNulls() {
        List<Map<String, Object>> table = new ArrayList<>();
        Map<String, Object> ana = new HashMap<>();
        ana.put("name", "Ana");
        ana.put("age", null);
        table.add(ana);
        table.add(ana);

        var result = MiniQueryEngine.distinct(table);
        assertEquals(1, result.size());
    }

}
