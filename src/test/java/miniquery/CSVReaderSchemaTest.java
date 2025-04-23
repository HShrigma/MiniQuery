package miniquery;

import org.junit.jupiter.api.Test;

import miniquery.csvreader.CSVReader;

import java.util.*;
import java.util.function.Function;
import static org.junit.jupiter.api.Assertions.*;

public class CSVReaderSchemaTest {

    @Test
    public void testApplySchema_CorrectlyCastsValues() {
        List<Map<String, String>> input = List.of(
            Map.of("name", "joe", "age", "23", "member", "true"),
            Map.of("name", "sue", "age", "30", "member", "false")
        );

        Map<String, Function<String, Object>> schema = Map.of(
            "name", s -> s,
            "age", Integer::parseInt,
            "member", Boolean::parseBoolean
        );

        List<Map<String, Object>> result = CSVReader.ApplySchemaToRows(input, schema);

        assertEquals(2, result.size());

        assertEquals("joe", result.get(0).get("name"));
        assertEquals(23, result.get(0).get("age"));
        assertEquals(true, result.get(0).get("member"));

        assertEquals("sue", result.get(1).get("name"));
        assertEquals(30, result.get(1).get("age"));
        assertEquals(false, result.get(1).get("member"));
    }

    @Test
    public void testApplySchema_FallbackToStringIfNoSchema() {
        List<Map<String, String>> input = List.of(
            Map.of("foo", "123", "bar", "test")
        );

        Map<String, Function<String, Object>> schema = Map.of(
            "foo", Integer::parseInt
        );

        List<Map<String, Object>> result = CSVReader.ApplySchemaToRows(input, schema);

        assertEquals(123, result.get(0).get("foo"));
        assertEquals("test", result.get(0).get("bar")); // no schema for bar = string fallback
    }

    @Test
    public void testApplySchema_BadConversionGracefulFallback() {
        List<Map<String, String>> input = List.of(
            Map.of("age", "not_a_number")
        );

        Map<String, Function<String, Object>> schema = Map.of(
            "age", Integer::parseInt
        );

        List<Map<String, Object>> result = CSVReader.ApplySchemaToRows(input, schema);

        assertEquals("not_a_number", result.get(0).get("age")); // fallback to string if parsing fails
    }
}
