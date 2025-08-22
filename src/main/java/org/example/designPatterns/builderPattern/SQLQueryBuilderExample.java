package org.example.designPatterns.builderPattern;

import java.util.*;

/**
 * Demonstration of Builder Pattern with SQL Query Builder
 * 
 * This example shows how to use the Builder pattern to construct
 * complex SQL queries in a readable and type-safe manner.
 */

// Product Class - SQL Query
class SQLQuery {
    private final String select;
    private final String from;
    private final String where;
    private final String groupBy;
    private final String having;
    private final String orderBy;
    private final String limit;
    private final List<String> joins;
    private final QueryType queryType;
    private final Map<String, Object> parameters;
    
    public enum QueryType {
        SELECT, INSERT, UPDATE, DELETE
    }
    
    private SQLQuery(Builder builder) {
        this.select = builder.select;
        this.from = builder.from;
        this.where = builder.where;
        this.groupBy = builder.groupBy;
        this.having = builder.having;
        this.orderBy = builder.orderBy;
        this.limit = builder.limit;
        this.joins = new ArrayList<>(builder.joins);
        this.queryType = builder.queryType;
        this.parameters = new HashMap<>(builder.parameters);
        
        validateQuery();
    }
    
    private void validateQuery() {
        if (queryType == QueryType.SELECT && (select == null || from == null)) {
            throw new IllegalStateException("SELECT queries must have SELECT and FROM clauses");
        }
        if (queryType == QueryType.UPDATE && from == null) {
            throw new IllegalStateException("UPDATE queries must specify a table");
        }
        if (queryType == QueryType.DELETE && from == null) {
            throw new IllegalStateException("DELETE queries must specify a table");
        }
        if (having != null && groupBy == null) {
            throw new IllegalStateException("HAVING clause requires GROUP BY clause");
        }
    }
    
    public static Builder select(String columns) {
        return new Builder().select(columns);
    }
    
    public static Builder selectAll() {
        return new Builder().select("*");
    }
    
    public static Builder insert() {
        return new Builder().queryType(QueryType.INSERT);
    }
    
    public static Builder update(String table) {
        return new Builder().queryType(QueryType.UPDATE).from(table);
    }
    
    public static Builder delete() {
        return new Builder().queryType(QueryType.DELETE);
    }
    
    public String toSQL() {
        StringBuilder sql = new StringBuilder();
        
        switch (queryType) {
            case SELECT:
                sql.append("SELECT ").append(select);
                if (from != null) {
                    sql.append(" FROM ").append(from);
                }
                break;
            case INSERT:
                sql.append("INSERT INTO ").append(from);
                if (select != null) {
                    sql.append(" ").append(select); // columns and values
                }
                break;
            case UPDATE:
                sql.append("UPDATE ").append(from);
                if (select != null) {
                    sql.append(" SET ").append(select); // SET clause
                }
                break;
            case DELETE:
                sql.append("DELETE FROM ").append(from);
                break;
        }
        
        // Add joins
        for (String join : joins) {
            sql.append(" ").append(join);
        }
        
        if (where != null && !where.trim().isEmpty()) {
            sql.append(" WHERE ").append(where);
        }
        if (groupBy != null) {
            sql.append(" GROUP BY ").append(groupBy);
        }
        if (having != null) {
            sql.append(" HAVING ").append(having);
        }
        if (orderBy != null) {
            sql.append(" ORDER BY ").append(orderBy);
        }
        if (limit != null) {
            sql.append(" LIMIT ").append(limit);
        }
        
        return sql.toString();
    }
    
    public String toFormattedSQL() {
        StringBuilder sql = new StringBuilder();
        
        switch (queryType) {
            case SELECT:
                sql.append("SELECT\n    ").append(select.replace(", ", ",\n    "));
                if (from != null) {
                    sql.append("\nFROM\n    ").append(from);
                }
                break;
            case INSERT:
                sql.append("INSERT INTO\n    ").append(from);
                if (select != null) {
                    sql.append("\n").append(select);
                }
                break;
            case UPDATE:
                sql.append("UPDATE\n    ").append(from);
                if (select != null) {
                    sql.append("\nSET\n    ").append(select.replace(", ", ",\n    "));
                }
                break;
            case DELETE:
                sql.append("DELETE FROM\n    ").append(from);
                break;
        }
        
        // Add joins with formatting
        for (String join : joins) {
            sql.append("\n").append(join);
        }
        
        if (where != null && !where.trim().isEmpty()) {
            sql.append("\nWHERE\n    ").append(where);
        }
        if (groupBy != null) {
            sql.append("\nGROUP BY\n    ").append(groupBy);
        }
        if (having != null) {
            sql.append("\nHAVING\n    ").append(having);
        }
        if (orderBy != null) {
            sql.append("\nORDER BY\n    ").append(orderBy);
        }
        if (limit != null) {
            sql.append("\nLIMIT ").append(limit);
        }
        
        return sql.toString();
    }
    
    public static class Builder {
        private String select;
        private String from;
        private String where;
        private String groupBy;
        private String having;
        private String orderBy;
        private String limit;
        private List<String> joins = new ArrayList<>();
        private QueryType queryType = QueryType.SELECT;
        private Map<String, Object> parameters = new HashMap<>();
        
        private Builder queryType(QueryType type) {
            this.queryType = type;
            return this;
        }
        
        public Builder select(String columns) {
            this.select = columns;
            this.queryType = QueryType.SELECT;
            return this;
        }
        
        public Builder selectDistinct(String columns) {
            this.select = "DISTINCT " + columns;
            this.queryType = QueryType.SELECT;
            return this;
        }
        
        public Builder selectCount(String column) {
            this.select = "COUNT(" + column + ")";
            this.queryType = QueryType.SELECT;
            return this;
        }
        
        public Builder selectSum(String column) {
            this.select = "SUM(" + column + ")";
            this.queryType = QueryType.SELECT;
            return this;
        }
        
        public Builder selectAvg(String column) {
            this.select = "AVG(" + column + ")";
            this.queryType = QueryType.SELECT;
            return this;
        }
        
        public Builder from(String table) {
            this.from = table;
            return this;
        }
        
        public Builder where(String condition) {
            if (this.where == null) {
                this.where = condition;
            } else {
                this.where = "(" + this.where + ") AND (" + condition + ")";
            }
            return this;
        }
        
        public Builder orWhere(String condition) {
            if (this.where == null) {
                this.where = condition;
            } else {
                this.where = "(" + this.where + ") OR (" + condition + ")";
            }
            return this;
        }
        
        public Builder whereEquals(String column, Object value) {
            String condition = column + " = " + formatValue(value);
            return where(condition);
        }
        
        public Builder whereNotEquals(String column, Object value) {
            String condition = column + " != " + formatValue(value);
            return where(condition);
        }
        
        public Builder whereLike(String column, String pattern) {
            String condition = column + " LIKE '" + pattern + "'";
            return where(condition);
        }
        
        public Builder whereIn(String column, Object... values) {
            StringBuilder inClause = new StringBuilder(column + " IN (");
            for (int i = 0; i < values.length; i++) {
                if (i > 0) inClause.append(", ");
                inClause.append(formatValue(values[i]));
            }
            inClause.append(")");
            return where(inClause.toString());
        }
        
        public Builder whereBetween(String column, Object start, Object end) {
            String condition = column + " BETWEEN " + formatValue(start) + " AND " + formatValue(end);
            return where(condition);
        }
        
        public Builder whereIsNull(String column) {
            return where(column + " IS NULL");
        }
        
        public Builder whereIsNotNull(String column) {
            return where(column + " IS NOT NULL");
        }
        
        public Builder innerJoin(String table, String condition) {
            joins.add("INNER JOIN " + table + " ON " + condition);
            return this;
        }
        
        public Builder leftJoin(String table, String condition) {
            joins.add("LEFT JOIN " + table + " ON " + condition);
            return this;
        }
        
        public Builder rightJoin(String table, String condition) {
            joins.add("RIGHT JOIN " + table + " ON " + condition);
            return this;
        }
        
        public Builder fullJoin(String table, String condition) {
            joins.add("FULL OUTER JOIN " + table + " ON " + condition);
            return this;
        }
        
        public Builder crossJoin(String table) {
            joins.add("CROSS JOIN " + table);
            return this;
        }
        
        public Builder groupBy(String columns) {
            this.groupBy = columns;
            return this;
        }
        
        public Builder having(String condition) {
            this.having = condition;
            return this;
        }
        
        public Builder orderBy(String columns) {
            this.orderBy = columns;
            return this;
        }
        
        public Builder orderByAsc(String columns) {
            this.orderBy = columns + " ASC";
            return this;
        }
        
        public Builder orderByDesc(String columns) {
            this.orderBy = columns + " DESC";
            return this;
        }
        
        public Builder limit(int count) {
            this.limit = String.valueOf(count);
            return this;
        }
        
        public Builder limit(int count, int offset) {
            this.limit = count + " OFFSET " + offset;
            return this;
        }
        
        // For UPDATE queries
        public Builder set(String assignments) {
            if (queryType == QueryType.UPDATE) {
                this.select = assignments; // Reuse select field for SET clause
            }
            return this;
        }
        
        public Builder setValue(String column, Object value) {
            String assignment = column + " = " + formatValue(value);
            if (this.select == null) {
                this.select = assignment;
            } else {
                this.select += ", " + assignment;
            }
            return this;
        }
        
        // For INSERT queries
        public Builder values(String columns, Object... values) {
            if (queryType == QueryType.INSERT) {
                StringBuilder valueClause = new StringBuilder("(" + columns + ") VALUES (");
                for (int i = 0; i < values.length; i++) {
                    if (i > 0) valueClause.append(", ");
                    valueClause.append(formatValue(values[i]));
                }
                valueClause.append(")");
                this.select = valueClause.toString();
            }
            return this;
        }
        
        // Common query patterns
        public Builder pagination(int pageSize, int pageNumber) {
            int offset = (pageNumber - 1) * pageSize;
            return limit(pageSize, offset);
        }
        
        public Builder searchPattern(String column, String searchTerm) {
            return whereLike(column, "%" + searchTerm + "%");
        }
        
        public Builder dateRange(String dateColumn, String startDate, String endDate) {
            return whereBetween(dateColumn, "'" + startDate + "'", "'" + endDate + "'");
        }
        
        private String formatValue(Object value) {
            if (value == null) {
                return "NULL";
            } else if (value instanceof String) {
                return "'" + value.toString().replace("'", "''") + "'";
            } else if (value instanceof Number) {
                return value.toString();
            } else if (value instanceof Boolean) {
                return ((Boolean) value) ? "1" : "0";
            } else {
                return "'" + value.toString() + "'";
            }
        }
        
        public SQLQuery build() {
            return new SQLQuery(this);
        }
        
        public String buildAndGetSQL() {
            return build().toSQL();
        }
        
        public String buildAndGetFormattedSQL() {
            return build().toFormattedSQL();
        }
    }
    
    // Getters
    public String getSelect() { return select; }
    public String getFrom() { return from; }
    public String getWhere() { return where; }
    public String getGroupBy() { return groupBy; }
    public String getHaving() { return having; }
    public String getOrderBy() { return orderBy; }
    public String getLimit() { return limit; }
    public List<String> getJoins() { return new ArrayList<>(joins); }
    public QueryType getQueryType() { return queryType; }
    public Map<String, Object> getParameters() { return new HashMap<>(parameters); }
}

// Query Director for common query patterns
class QueryDirector {
    
    public SQLQuery findUserById(int userId) {
        return SQLQuery.select("id, username, email, created_at")
                .from("users")
                .whereEquals("id", userId)
                .build();
    }
    
    public SQLQuery findActiveUsersByRole(String role) {
        return SQLQuery.select("u.id, u.username, u.email, r.role_name")
                .from("users u")
                .innerJoin("user_roles ur", "u.id = ur.user_id")
                .innerJoin("roles r", "ur.role_id = r.id")
                .whereEquals("r.role_name", role)
                .whereEquals("u.status", "active")
                .orderBy("u.username")
                .build();
    }
    
    public SQLQuery getOrderSummaryReport(String startDate, String endDate) {
        return SQLQuery.select("DATE(o.order_date) as order_date, COUNT(*) as total_orders, SUM(o.total_amount) as total_revenue")
                .from("orders o")
                .dateRange("o.order_date", startDate, endDate)
                .whereEquals("o.status", "completed")
                .groupBy("DATE(o.order_date)")
                .orderByDesc("order_date")
                .build();
    }
    
    public SQLQuery getTopCustomers(int limit) {
        return SQLQuery.select("c.id, c.name, c.email, COUNT(o.id) as order_count, SUM(o.total_amount) as total_spent")
                .from("customers c")
                .leftJoin("orders o", "c.id = o.customer_id")
                .groupBy("c.id, c.name, c.email")
                .having("COUNT(o.id) > 0")
                .orderByDesc("total_spent")
                .limit(limit)
                .build();
    }
    
    public SQLQuery searchProducts(String searchTerm, String category) {
        SQLQuery.Builder builder = SQLQuery.select("id, name, description, price, stock_quantity")
                .from("products")
                .whereEquals("status", "active");
        
        if (searchTerm != null && !searchTerm.isEmpty()) {
            builder.searchPattern("name", searchTerm)
                   .orWhere("description LIKE '%" + searchTerm + "%'");
        }
        
        if (category != null && !category.isEmpty()) {
            builder.whereEquals("category", category);
        }
        
        return builder.orderBy("name").build();
    }
}

// Demonstration class
public class SQLQueryBuilderExample {
    
    public static void main(String[] args) {
        System.out.println("=== Builder Pattern Demonstration - SQL Query Builder ===\n");
        
        // Example 1: Simple SELECT query
        System.out.println("1. Simple SELECT Query:");
        SQLQuery simpleQuery = SQLQuery.select("id, name, email")
                .from("users")
                .whereEquals("status", "active")
                .orderBy("name")
                .build();
        
        System.out.println("SQL: " + simpleQuery.toSQL());
        System.out.println("Formatted SQL:");
        System.out.println(simpleQuery.toFormattedSQL());
        System.out.println();
        
        // Example 2: Complex query with joins
        System.out.println("2. Complex Query with Joins:");
        SQLQuery complexQuery = SQLQuery.select("u.username, p.title, c.content, c.created_at")
                .from("users u")
                .innerJoin("posts p", "u.id = p.author_id")
                .leftJoin("comments c", "p.id = c.post_id")
                .whereEquals("u.status", "active")
                .whereBetween("p.created_at", "'2024-01-01'", "'2024-12-31'")
                .orderByDesc("c.created_at")
                .limit(50)
                .build();
        
        System.out.println("Formatted SQL:");
        System.out.println(complexQuery.toFormattedSQL());
        System.out.println();
        
        // Example 3: Aggregate query with GROUP BY and HAVING
        System.out.println("3. Aggregate Query with GROUP BY and HAVING:");
        SQLQuery aggregateQuery = SQLQuery.select("department, COUNT(*) as employee_count, AVG(salary) as avg_salary")
                .from("employees")
                .whereIsNotNull("department")
                .whereEquals("status", "active")
                .groupBy("department")
                .having("COUNT(*) > 5")
                .orderByDesc("avg_salary")
                .build();
        
        System.out.println("Formatted SQL:");
        System.out.println(aggregateQuery.toFormattedSQL());
        System.out.println();
        
        // Example 4: UPDATE query
        System.out.println("4. UPDATE Query:");
        SQLQuery updateQuery = SQLQuery.update("users")
                .setValue("last_login", "NOW()")
                .setValue("login_count", "login_count + 1")
                .whereEquals("id", 123)
                .build();
        
        System.out.println("SQL: " + updateQuery.toSQL());
        System.out.println();
        
        // Example 5: INSERT query
        System.out.println("5. INSERT Query:");
        SQLQuery insertQuery = SQLQuery.insert()
                .from("users")
                .values("username, email, password, created_at", 
                       "john_doe", "john@example.com", "hashed_password", "NOW()")
                .build();
        
        System.out.println("SQL: " + insertQuery.toSQL());
        System.out.println();
        
        // Example 6: DELETE query
        System.out.println("6. DELETE Query:");
        SQLQuery deleteQuery = SQLQuery.delete()
                .from("temporary_data")
                .whereBetween("created_at", "'2024-01-01'", "'2024-01-31'")
                .whereEquals("processed", true)
                .build();
        
        System.out.println("SQL: " + deleteQuery.toSQL());
        System.out.println();
        
        // Example 7: Using convenience methods
        System.out.println("7. Query with Convenience Methods:");
        SQLQuery convenienceQuery = SQLQuery.select("*")
                .from("products")
                .whereIn("category", "electronics", "books", "clothing")
                .whereBetween("price", 10.00, 100.00)
                .whereIsNotNull("description")
                .searchPattern("name", "smart")
                .orderByDesc("rating")
                .pagination(20, 2) // 20 items per page, page 2
                .build();
        
        System.out.println("Formatted SQL:");
        System.out.println(convenienceQuery.toFormattedSQL());
        System.out.println();
        
        // Example 8: Using QueryDirector for common patterns
        System.out.println("8. Using QueryDirector for Common Patterns:");
        QueryDirector director = new QueryDirector();
        
        System.out.println("Find User by ID:");
        SQLQuery userQuery = director.findUserById(123);
        System.out.println(userQuery.toSQL());
        System.out.println();
        
        System.out.println("Find Active Users by Role:");
        SQLQuery roleQuery = director.findActiveUsersByRole("admin");
        System.out.println(roleQuery.toFormattedSQL());
        System.out.println();
        
        System.out.println("Order Summary Report:");
        SQLQuery reportQuery = director.getOrderSummaryReport("2024-01-01", "2024-12-31");
        System.out.println(reportQuery.toFormattedSQL());
        System.out.println();
        
        System.out.println("Top Customers:");
        SQLQuery topCustomersQuery = director.getTopCustomers(10);
        System.out.println(topCustomersQuery.toFormattedSQL());
        System.out.println();
        
        // Example 9: Search with dynamic conditions
        System.out.println("9. Dynamic Search Query:");
        SQLQuery searchQuery = director.searchProducts("smartphone", "electronics");
        System.out.println("Search for 'smartphone' in 'electronics':");
        System.out.println(searchQuery.toFormattedSQL());
        System.out.println();
        
        // Example 10: Error handling
        System.out.println("10. Error Handling Demonstration:");
        try {
            SQLQuery invalidQuery = SQLQuery.select("name")
                    .having("COUNT(*) > 5") // HAVING without GROUP BY
                    .build();
        } catch (IllegalStateException e) {
            System.out.println("Caught expected error: " + e.getMessage());
        }
        
        try {
            SQLQuery anotherInvalidQuery = SQLQuery.select("name")
                    // Missing FROM clause
                    .build();
        } catch (IllegalStateException e) {
            System.out.println("Caught expected error: " + e.getMessage());
        }
        System.out.println();
        
        // Example 11: Method chaining flexibility
        System.out.println("11. Flexible Method Chaining:");
        String finalQuery = SQLQuery.selectDistinct("category")
                .from("products p")
                .innerJoin("categories c", "p.category_id = c.id")
                .whereEquals("p.status", "active")
                .whereIsNotNull("c.name")
                .orderBy("category")
                .buildAndGetFormattedSQL();
        
        System.out.println(finalQuery);
        System.out.println();
        
        System.out.println("=== Demonstration Complete ===");
    }
}