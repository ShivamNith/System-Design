package org.example.designPatterns.iteratorPattern;

import java.util.*;
import java.util.function.Predicate;

/**
 * Iterator Pattern Example: Data Table with Multiple Views
 * 
 * This example demonstrates the Iterator Pattern with a data table
 * that supports different iteration strategies (row-wise, column-wise, filtered).
 * This simulates database-like operations where you might need different views
 * of the same data.
 */

// Data row representing a record in the table
class DataRow {
    private Map<String, Object> data;
    
    public DataRow() {
        this.data = new HashMap<>();
    }
    
    public void put(String column, Object value) {
        data.put(column, value);
    }
    
    public Object get(String column) {
        return data.get(column);
    }
    
    public Set<String> getColumns() {
        return data.keySet();
    }
    
    public boolean hasColumn(String column) {
        return data.containsKey(column);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        data.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> sb.append(entry.getKey()).append("=").append(entry.getValue()).append(", "));
        if (sb.length() > 1) {
            sb.setLength(sb.length() - 2);
        }
        sb.append("}");
        return sb.toString();
    }
}

// Abstract base iterator for data table
abstract class DataTableIterator implements Iterator<Object> {
    protected DataTable table;
    protected int currentPosition;
    
    public DataTableIterator(DataTable table) {
        this.table = table;
        this.currentPosition = 0;
    }
    
    @Override
    public void remove() {
        throw new UnsupportedOperationException("Remove operation not supported");
    }
}

// Row iterator - iterates through complete rows
class RowIterator extends DataTableIterator {
    
    public RowIterator(DataTable table) {
        super(table);
    }
    
    @Override
    public boolean hasNext() {
        return currentPosition < table.getRowCount();
    }
    
    @Override
    public DataRow next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more rows");
        }
        return table.getRow(currentPosition++);
    }
}

// Column iterator - iterates through values of a specific column
class ColumnIterator extends DataTableIterator {
    private String columnName;
    
    public ColumnIterator(DataTable table, String columnName) {
        super(table);
        this.columnName = columnName;
    }
    
    @Override
    public boolean hasNext() {
        return currentPosition < table.getRowCount();
    }
    
    @Override
    public Object next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more values in column " + columnName);
        }
        DataRow row = table.getRow(currentPosition++);
        return row.get(columnName);
    }
}

// Filtered row iterator - iterates through rows that match a condition
class FilteredRowIterator extends DataTableIterator {
    private Predicate<DataRow> filter;
    private List<Integer> matchingIndices;
    private int filteredPosition;
    
    public FilteredRowIterator(DataTable table, Predicate<DataRow> filter) {
        super(table);
        this.filter = filter;
        this.matchingIndices = new ArrayList<>();
        this.filteredPosition = 0;
        
        // Pre-compute matching rows
        for (int i = 0; i < table.getRowCount(); i++) {
            if (filter.test(table.getRow(i))) {
                matchingIndices.add(i);
            }
        }
    }
    
    @Override
    public boolean hasNext() {
        return filteredPosition < matchingIndices.size();
    }
    
    @Override
    public DataRow next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more filtered rows");
        }
        int rowIndex = matchingIndices.get(filteredPosition++);
        return table.getRow(rowIndex);
    }
}

// Paginated iterator - iterates through rows in pages
class PaginatedIterator extends DataTableIterator {
    private int pageSize;
    private int currentPage;
    private int totalPages;
    
    public PaginatedIterator(DataTable table, int pageSize) {
        super(table);
        this.pageSize = pageSize;
        this.currentPage = 0;
        this.totalPages = (int) Math.ceil((double) table.getRowCount() / pageSize);
    }
    
    @Override
    public boolean hasNext() {
        return currentPage < totalPages;
    }
    
    @Override
    public List<DataRow> next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more pages");
        }
        
        List<DataRow> page = new ArrayList<>();
        int startIndex = currentPage * pageSize;
        int endIndex = Math.min(startIndex + pageSize, table.getRowCount());
        
        for (int i = startIndex; i < endIndex; i++) {
            page.add(table.getRow(i));
        }
        
        currentPage++;
        return page;
    }
}

// Main DataTable class
class DataTable {
    private List<DataRow> rows;
    private Set<String> columns;
    
    public DataTable() {
        this.rows = new ArrayList<>();
        this.columns = new LinkedHashSet<>();
    }
    
    public void addRow(DataRow row) {
        rows.add(row);
        columns.addAll(row.getColumns());
    }
    
    public DataRow getRow(int index) {
        if (index < 0 || index >= rows.size()) {
            throw new IndexOutOfBoundsException("Row index out of bounds: " + index);
        }
        return rows.get(index);
    }
    
    public int getRowCount() {
        return rows.size();
    }
    
    public Set<String> getColumns() {
        return new LinkedHashSet<>(columns);
    }
    
    // Iterator factory methods
    public Iterator<DataRow> rowIterator() {
        return new RowIterator(this);
    }
    
    public Iterator<Object> columnIterator(String columnName) {
        if (!columns.contains(columnName)) {
            throw new IllegalArgumentException("Column does not exist: " + columnName);
        }
        return new ColumnIterator(this, columnName);
    }
    
    public Iterator<DataRow> filteredIterator(Predicate<DataRow> filter) {
        return new FilteredRowIterator(this, filter);
    }
    
    public Iterator<List<DataRow>> paginatedIterator(int pageSize) {
        if (pageSize <= 0) {
            throw new IllegalArgumentException("Page size must be positive");
        }
        return new PaginatedIterator(this, pageSize);
    }
    
    // Utility methods
    public void printTable() {
        System.out.println("=== Data Table ===");
        System.out.println("Columns: " + columns);
        System.out.println("Rows:");
        for (int i = 0; i < rows.size(); i++) {
            System.out.println("Row " + i + ": " + rows.get(i));
        }
        System.out.println();
    }
}

// Utility class for table operations
class TableUtils {
    
    public static DataTable createSampleEmployeeTable() {
        DataTable table = new DataTable();
        
        // Create sample employee data
        DataRow emp1 = new DataRow();
        emp1.put("id", 1);
        emp1.put("name", "John Doe");
        emp1.put("department", "Engineering");
        emp1.put("salary", 75000);
        emp1.put("experience", 5);
        
        DataRow emp2 = new DataRow();
        emp2.put("id", 2);
        emp2.put("name", "Jane Smith");
        emp2.put("department", "Marketing");
        emp2.put("salary", 65000);
        emp2.put("experience", 3);
        
        DataRow emp3 = new DataRow();
        emp3.put("id", 3);
        emp3.put("name", "Bob Johnson");
        emp3.put("department", "Engineering");
        emp3.put("salary", 85000);
        emp3.put("experience", 7);
        
        DataRow emp4 = new DataRow();
        emp4.put("id", 4);
        emp4.put("name", "Alice Brown");
        emp4.put("department", "HR");
        emp4.put("salary", 60000);
        emp4.put("experience", 4);
        
        DataRow emp5 = new DataRow();
        emp5.put("id", 5);
        emp5.put("name", "Charlie Wilson");
        emp5.put("department", "Engineering");
        emp5.put("salary", 90000);
        emp5.put("experience", 8);
        
        DataRow emp6 = new DataRow();
        emp6.put("id", 6);
        emp6.put("name", "Diana Davis");
        emp6.put("department", "Marketing");
        emp6.put("salary", 70000);
        emp6.put("experience", 6);
        
        table.addRow(emp1);
        table.addRow(emp2);
        table.addRow(emp3);
        table.addRow(emp4);
        table.addRow(emp5);
        table.addRow(emp6);
        
        return table;
    }
    
    public static void printIteratorResults(String title, Iterator<?> iterator) {
        System.out.println(title + ":");
        while (iterator.hasNext()) {
            System.out.println("  " + iterator.next());
        }
        System.out.println();
    }
}

// Client code demonstrating the Iterator Pattern with data tables
public class DataTableExample {
    public static void main(String[] args) {
        System.out.println("=== Data Table Iterator Pattern Demo ===\n");
        
        // Create sample employee table
        DataTable employeeTable = TableUtils.createSampleEmployeeTable();
        employeeTable.printTable();
        
        // Demonstrate row iterator
        System.out.println("=== Row Iterator ===");
        Iterator<DataRow> rowIterator = employeeTable.rowIterator();
        TableUtils.printIteratorResults("All Employees", rowIterator);
        
        // Demonstrate column iterator
        System.out.println("=== Column Iterator ===");
        Iterator<Object> nameIterator = employeeTable.columnIterator("name");
        TableUtils.printIteratorResults("Employee Names", nameIterator);
        
        Iterator<Object> salaryIterator = employeeTable.columnIterator("salary");
        TableUtils.printIteratorResults("Employee Salaries", salaryIterator);
        
        // Demonstrate filtered iterator
        System.out.println("=== Filtered Iterator ===");
        
        // Filter by department
        Predicate<DataRow> engineeringFilter = row -> 
            "Engineering".equals(row.get("department"));
        Iterator<DataRow> engineeringIterator = employeeTable.filteredIterator(engineeringFilter);
        TableUtils.printIteratorResults("Engineering Employees", engineeringIterator);
        
        // Filter by salary
        Predicate<DataRow> highSalaryFilter = row -> 
            (Integer) row.get("salary") > 70000;
        Iterator<DataRow> highSalaryIterator = employeeTable.filteredIterator(highSalaryFilter);
        TableUtils.printIteratorResults("High Salary Employees (>70000)", highSalaryIterator);
        
        // Filter by experience
        Predicate<DataRow> experiencedFilter = row -> 
            (Integer) row.get("experience") >= 5;
        Iterator<DataRow> experiencedIterator = employeeTable.filteredIterator(experiencedFilter);
        TableUtils.printIteratorResults("Experienced Employees (>=5 years)", experiencedIterator);
        
        // Demonstrate paginated iterator
        System.out.println("=== Paginated Iterator ===");
        Iterator<List<DataRow>> paginatedIterator = employeeTable.paginatedIterator(2);
        int pageNumber = 1;
        while (paginatedIterator.hasNext()) {
            List<DataRow> page = paginatedIterator.next();
            System.out.println("Page " + pageNumber + ":");
            for (DataRow row : page) {
                System.out.println("  " + row);
            }
            System.out.println();
            pageNumber++;
        }
        
        // Demonstrate multiple simultaneous iterators
        System.out.println("=== Multiple Simultaneous Iterators ===");
        Iterator<DataRow> allRows = employeeTable.rowIterator();
        Iterator<Object> allNames = employeeTable.columnIterator("name");
        Iterator<Object> allSalaries = employeeTable.columnIterator("salary");
        
        System.out.println("Employee Summary:");
        while (allRows.hasNext() && allNames.hasNext() && allSalaries.hasNext()) {
            DataRow row = allRows.next();
            String name = (String) allNames.next();
            Integer salary = (Integer) allSalaries.next();
            String department = (String) row.get("department");
            System.out.println(String.format("  %s (%s): $%d", name, department, salary));
        }
        System.out.println();
        
        // Demonstrate complex filtering
        System.out.println("=== Complex Filtering ===");
        Predicate<DataRow> complexFilter = row -> {
            String dept = (String) row.get("department");
            Integer salary = (Integer) row.get("salary");
            Integer experience = (Integer) row.get("experience");
            return ("Engineering".equals(dept) || "Marketing".equals(dept)) 
                   && salary > 65000 
                   && experience >= 4;
        };
        
        Iterator<DataRow> complexIterator = employeeTable.filteredIterator(complexFilter);
        TableUtils.printIteratorResults("Senior Engineering/Marketing Employees", complexIterator);
    }
}

/*
Expected Output:
=== Data Table Iterator Pattern Demo ===

=== Data Table ===
Columns: [id, name, department, salary, experience]
Rows:
Row 0: {department=Engineering, experience=5, id=1, name=John Doe, salary=75000}
Row 1: {department=Marketing, experience=3, id=2, name=Jane Smith, salary=65000}
Row 2: {department=Engineering, experience=7, id=3, name=Bob Johnson, salary=85000}
Row 3: {department=HR, experience=4, id=4, name=Alice Brown, salary=60000}
Row 4: {department=Engineering, experience=8, id=5, name=Charlie Wilson, salary=90000}
Row 5: {department=Marketing, experience=6, id=6, name=Diana Davis, salary=70000}

=== Row Iterator ===
All Employees:
  {department=Engineering, experience=5, id=1, name=John Doe, salary=75000}
  {department=Marketing, experience=3, id=2, name=Jane Smith, salary=65000}
  {department=Engineering, experience=7, id=3, name=Bob Johnson, salary=85000}
  {department=HR, experience=4, id=4, name=Alice Brown, salary=60000}
  {department=Engineering, experience=8, id=5, name=Charlie Wilson, salary=90000}
  {department=Marketing, experience=6, id=6, name=Diana Davis, salary=70000}

=== Column Iterator ===
Employee Names:
  John Doe
  Jane Smith
  Bob Johnson
  Alice Brown
  Charlie Wilson
  Diana Davis

Employee Salaries:
  75000
  65000
  85000
  60000
  90000
  70000

=== Filtered Iterator ===
Engineering Employees:
  {department=Engineering, experience=5, id=1, name=John Doe, salary=75000}
  {department=Engineering, experience=7, id=3, name=Bob Johnson, salary=85000}
  {department=Engineering, experience=8, id=5, name=Charlie Wilson, salary=90000}

High Salary Employees (>70000):
  {department=Engineering, experience=5, id=1, name=John Doe, salary=75000}
  {department=Engineering, experience=7, id=3, name=Bob Johnson, salary=85000}
  {department=Engineering, experience=8, id=5, name=Charlie Wilson, salary=90000}

Experienced Employees (>=5 years):
  {department=Engineering, experience=5, id=1, name=John Doe, salary=75000}
  {department=Engineering, experience=7, id=3, name=Bob Johnson, salary=85000}
  {department=Engineering, experience=8, id=5, name=Charlie Wilson, salary=90000}
  {department=Marketing, experience=6, id=6, name=Diana Davis, salary=70000}

=== Paginated Iterator ===
Page 1:
  {department=Engineering, experience=5, id=1, name=John Doe, salary=75000}
  {department=Marketing, experience=3, id=2, name=Jane Smith, salary=65000}

Page 2:
  {department=Engineering, experience=7, id=3, name=Bob Johnson, salary=85000}
  {department=HR, experience=4, id=4, name=Alice Brown, salary=60000}

Page 3:
  {department=Engineering, experience=8, id=5, name=Charlie Wilson, salary=90000}
  {department=Marketing, experience=6, id=6, name=Diana Davis, salary=70000}

=== Multiple Simultaneous Iterators ===
Employee Summary:
  John Doe (Engineering): $75000
  Jane Smith (Marketing): $65000
  Bob Johnson (Engineering): $85000
  Alice Brown (HR): $60000
  Charlie Wilson (Engineering): $90000
  Diana Davis (Marketing): $70000

=== Complex Filtering ===
Senior Engineering/Marketing Employees:
  {department=Engineering, experience=5, id=1, name=John Doe, salary=75000}
  {department=Engineering, experience=7, id=3, name=Bob Johnson, salary=85000}
  {department=Engineering, experience=8, id=5, name=Charlie Wilson, salary=90000}
  {department=Marketing, experience=6, id=6, name=Diana Davis, salary=70000}
*/