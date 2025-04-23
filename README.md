# MiniQuery

MiniQuery is a lightweight, in-memory query engine built in Java 21. It's inspired by SQL-style querying but works directly on `List<Map<String, Object>>` tables.

Perfect for quick CSV inspection, small ETL tasks, and learning how query engines work under the hood — all without needing a full-blown database.

## ✨ Features

- ✅ **CSV Reader** – Read CSV files into structured tables (list of rows)
- 📄 **Schema Parser** – Analyze column types from raw CSV input
- 🔍 **Select** – Filter rows by condition and choose specific columns
- 🧮 **Limit** – Return only a fixed number of rows
- 📊 **Order By** – Sort rows by key (ascending or descending)
- 🎯 **Distinct** – Remove duplicate rows
- 🧪 **Full Unit Test Suite** – JUnit 5-powered test coverage

## 🛠️ Quick Start

### Requirements
- Java 21
- Maven

### Run via Maven
```bash
mvn exec:java -Dexec.mainClass="miniquery.MiniQuery"
```
### Run built .jar
```bash
mvn package
java -jar target/mini-query-1.0-SNAPSHOT.jar
```
## 🔍 Example Usage
```java
List<Map<String, Object>> table = CSVReader.Load("src/main/resources/data.csv");

// Filter where age > 25 and select "name"
List<Map<String, Object>> query = MiniQueryEngine.select(
    table,
    row -> Integer.parseInt(row.get("age").toString()) > 25,
    "name"
);
```
## 📂 Project Structure
``` css
src/
 └─ main/
     ├─ java/
     │   ├─ miniquery/
     │   │   ├─ MiniQuery.java
     │   │   ├─ engine
     │   │   |  └─ MiniQueryEngine.java
     │   │   └─ csvreader/
     │   │       └─ CSVReader.java
     └─ resources/
         └─ data.csv (example input)
```

## 🧪 Testing

```bash
mvn clean test
```
JUnit 5 is used for all testing. Tests cover:

- CSV parsing
- Value selection
- Ordering, limiting
- Null handling
- Type comparison errors
- Duplicate detection

## 📄 License

MIT License.

---
*Built with ☕ and way too much debugging.*
