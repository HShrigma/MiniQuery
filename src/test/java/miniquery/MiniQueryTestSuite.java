package miniquery;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        CSVReaderTest.class,
        CSVReaderSchemaTest.class,
        MiniQueryEngineTest.class
})
public class MiniQueryTestSuite {

}