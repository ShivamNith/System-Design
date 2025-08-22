package org.example.solidPrinciples.singleResponsibility;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Employee Management System - Single Responsibility Principle Example
 * 
 * This example demonstrates the transformation from a class with multiple
 * responsibilities to a properly designed system where each class has a
 * single, well-defined responsibility.
 * 
 * Key Concepts:
 * - Separation of concerns
 * - High cohesion within classes
 * - Clear, focused responsibilities
 * - Improved testability and maintainability
 */
public class EmployeeManagementExample {
    
    /**
     * BEFORE: Employee class violating SRP
     * This class has too many responsibilities:
     * - Data management
     * - Business logic (salary calculation)
     * - Persistence (database operations)
     * - Reporting
     * - Communication (email)
     * - Validation
     */
    public static class ViolatingEmployee {
        private Long id;
        private String name;
        private String email;
        private String department;
        private double baseSalary;
        private LocalDate hireDate;
        private String position;
        
        public ViolatingEmployee(String name, String email, String department, 
                                double baseSalary, String position) {
            this.name = name;
            this.email = email;
            this.department = department;
            this.baseSalary = baseSalary;
            this.position = position;
            this.hireDate = LocalDate.now();
        }
        
        // Responsibility 1: Data validation
        public boolean validateEmployee() {
            if (name == null || name.trim().isEmpty()) {
                return false;
            }
            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                return false;
            }
            if (baseSalary <= 0) {
                return false;
            }
            return true;
        }
        
        // Responsibility 2: Salary calculation
        public double calculateSalary() {
            double salary = baseSalary;
            
            // Apply position-based multipliers
            switch (position.toLowerCase()) {
                case "manager":
                    salary *= 1.5;
                    break;
                case "senior developer":
                    salary *= 1.3;
                    break;
                case "developer":
                    salary *= 1.1;
                    break;
            }
            
            // Apply tenure bonus
            int yearsOfService = LocalDate.now().getYear() - hireDate.getYear();
            salary += (yearsOfService * 1000);
            
            // Apply department bonus
            if (department.equals("Engineering")) {
                salary *= 1.15;
            } else if (department.equals("Sales")) {
                salary *= 1.2;
            }
            
            return salary;
        }
        
        // Responsibility 3: Tax calculation
        public double calculateTax() {
            double salary = calculateSalary();
            if (salary < 50000) {
                return salary * 0.15;
            } else if (salary < 100000) {
                return salary * 0.25;
            } else {
                return salary * 0.35;
            }
        }
        
        // Responsibility 4: Database operations
        public void saveToDatabase(Connection connection) throws SQLException {
            String sql = "INSERT INTO employees (name, email, department, salary, position, hire_date) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, name);
                stmt.setString(2, email);
                stmt.setString(3, department);
                stmt.setDouble(4, baseSalary);
                stmt.setString(5, position);
                stmt.setDate(6, java.sql.Date.valueOf(hireDate));
                stmt.executeUpdate();
            }
        }
        
        // Responsibility 5: Report generation
        public String generateReport() {
            StringBuilder report = new StringBuilder();
            report.append("=== Employee Report ===\n");
            report.append("Name: ").append(name).append("\n");
            report.append("Email: ").append(email).append("\n");
            report.append("Department: ").append(department).append("\n");
            report.append("Position: ").append(position).append("\n");
            report.append("Base Salary: $").append(baseSalary).append("\n");
            report.append("Total Salary: $").append(calculateSalary()).append("\n");
            report.append("Tax: $").append(calculateTax()).append("\n");
            report.append("Net Salary: $").append(calculateSalary() - calculateTax()).append("\n");
            report.append("Hire Date: ").append(hireDate).append("\n");
            report.append("======================\n");
            return report.toString();
        }
        
        // Responsibility 6: Email notifications
        public void sendWelcomeEmail() {
            System.out.println("Sending email to " + email);
            System.out.println("Subject: Welcome to the company!");
            System.out.println("Body: Dear " + name + ", welcome aboard!");
        }
        
        // Responsibility 7: Performance evaluation
        public String evaluatePerformance() {
            int yearsOfService = LocalDate.now().getYear() - hireDate.getYear();
            if (yearsOfService < 1) {
                return "New Employee - No evaluation yet";
            } else if (yearsOfService < 3) {
                return "Junior Level Performance";
            } else if (yearsOfService < 5) {
                return "Mid Level Performance";
            } else {
                return "Senior Level Performance";
            }
        }
    }
    
    /**
     * AFTER: Properly designed classes following SRP
     * Each class has a single, well-defined responsibility
     */
    
    // Responsibility 1: Employee data model (Pure domain object)
    public static class Employee {
        private Long id;
        private String name;
        private String email;
        private String department;
        private double baseSalary;
        private LocalDate hireDate;
        private String position;
        
        public Employee(String name, String email, String department, 
                       double baseSalary, String position) {
            this.name = name;
            this.email = email;
            this.department = department;
            this.baseSalary = baseSalary;
            this.position = position;
            this.hireDate = LocalDate.now();
        }
        
        // Constructor for existing employees
        public Employee(Long id, String name, String email, String department, 
                       double baseSalary, String position, LocalDate hireDate) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.department = department;
            this.baseSalary = baseSalary;
            this.position = position;
            this.hireDate = hireDate;
        }
        
        // Only getters and setters - no business logic
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getDepartment() { return department; }
        public double getBaseSalary() { return baseSalary; }
        public LocalDate getHireDate() { return hireDate; }
        public String getPosition() { return position; }
        
        @Override
        public String toString() {
            return String.format("Employee{id=%d, name='%s', position='%s', department='%s'}", 
                               id, name, position, department);
        }
    }
    
    // Responsibility 2: Employee validation
    public static class EmployeeValidator {
        private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
        private static final Set<String> VALID_DEPARTMENTS = new HashSet<>(
            Arrays.asList("Engineering", "Sales", "HR", "Marketing", "Finance")
        );
        private static final Set<String> VALID_POSITIONS = new HashSet<>(
            Arrays.asList("Manager", "Senior Developer", "Developer", "Analyst", "Intern")
        );
        
        public ValidationResult validate(Employee employee) {
            List<String> errors = new ArrayList<>();
            
            // Validate name
            if (employee.getName() == null || employee.getName().trim().isEmpty()) {
                errors.add("Name is required");
            } else if (employee.getName().length() < 2) {
                errors.add("Name must be at least 2 characters");
            }
            
            // Validate email
            if (employee.getEmail() == null || !employee.getEmail().matches(EMAIL_REGEX)) {
                errors.add("Invalid email format");
            }
            
            // Validate department
            if (!VALID_DEPARTMENTS.contains(employee.getDepartment())) {
                errors.add("Invalid department: " + employee.getDepartment());
            }
            
            // Validate position
            if (!VALID_POSITIONS.contains(employee.getPosition())) {
                errors.add("Invalid position: " + employee.getPosition());
            }
            
            // Validate salary
            if (employee.getBaseSalary() <= 0) {
                errors.add("Salary must be positive");
            } else if (employee.getBaseSalary() < 30000) {
                errors.add("Salary below minimum wage");
            }
            
            return new ValidationResult(errors.isEmpty(), errors);
        }
        
        public static class ValidationResult {
            private final boolean valid;
            private final List<String> errors;
            
            public ValidationResult(boolean valid, List<String> errors) {
                this.valid = valid;
                this.errors = errors;
            }
            
            public boolean isValid() { return valid; }
            public List<String> getErrors() { return errors; }
        }
    }
    
    // Responsibility 3: Salary calculation
    public static class SalaryCalculator {
        private final Map<String, Double> positionMultipliers;
        private final Map<String, Double> departmentBonuses;
        private final double tenureBonus = 1000.0;
        
        public SalaryCalculator() {
            positionMultipliers = new HashMap<>();
            positionMultipliers.put("Manager", 1.5);
            positionMultipliers.put("Senior Developer", 1.3);
            positionMultipliers.put("Developer", 1.1);
            positionMultipliers.put("Analyst", 1.0);
            positionMultipliers.put("Intern", 0.7);
            
            departmentBonuses = new HashMap<>();
            departmentBonuses.put("Engineering", 1.15);
            departmentBonuses.put("Sales", 1.2);
            departmentBonuses.put("HR", 1.0);
            departmentBonuses.put("Marketing", 1.1);
            departmentBonuses.put("Finance", 1.1);
        }
        
        public double calculateGrossSalary(Employee employee) {
            double salary = employee.getBaseSalary();
            
            // Apply position multiplier
            Double positionMultiplier = positionMultipliers.get(employee.getPosition());
            if (positionMultiplier != null) {
                salary *= positionMultiplier;
            }
            
            // Apply tenure bonus
            int yearsOfService = calculateYearsOfService(employee);
            salary += (yearsOfService * tenureBonus);
            
            // Apply department bonus
            Double departmentBonus = departmentBonuses.get(employee.getDepartment());
            if (departmentBonus != null) {
                salary *= departmentBonus;
            }
            
            return Math.round(salary * 100.0) / 100.0;
        }
        
        private int calculateYearsOfService(Employee employee) {
            return LocalDate.now().getYear() - employee.getHireDate().getYear();
        }
        
        public double calculateBonus(Employee employee) {
            // Annual bonus based on position and tenure
            double bonus = 0;
            int years = calculateYearsOfService(employee);
            
            if (employee.getPosition().equals("Manager")) {
                bonus = employee.getBaseSalary() * 0.2;
            } else if (years > 5) {
                bonus = employee.getBaseSalary() * 0.15;
            } else if (years > 2) {
                bonus = employee.getBaseSalary() * 0.1;
            } else {
                bonus = employee.getBaseSalary() * 0.05;
            }
            
            return Math.round(bonus * 100.0) / 100.0;
        }
    }
    
    // Responsibility 4: Tax calculation
    public static class TaxCalculator {
        private final List<TaxBracket> taxBrackets;
        
        public TaxCalculator() {
            taxBrackets = new ArrayList<>();
            taxBrackets.add(new TaxBracket(0, 50000, 0.15));
            taxBrackets.add(new TaxBracket(50000, 100000, 0.25));
            taxBrackets.add(new TaxBracket(100000, 200000, 0.35));
            taxBrackets.add(new TaxBracket(200000, Double.MAX_VALUE, 0.45));
        }
        
        public double calculateTax(double grossSalary) {
            double tax = 0;
            double remainingSalary = grossSalary;
            
            for (TaxBracket bracket : taxBrackets) {
                if (remainingSalary <= 0) break;
                
                double taxableInBracket = Math.min(remainingSalary, bracket.getUpperLimit() - bracket.getLowerLimit());
                if (grossSalary > bracket.getLowerLimit()) {
                    tax += taxableInBracket * bracket.getRate();
                    remainingSalary -= taxableInBracket;
                }
            }
            
            return Math.round(tax * 100.0) / 100.0;
        }
        
        public double calculateNetSalary(double grossSalary) {
            return grossSalary - calculateTax(grossSalary);
        }
        
        private static class TaxBracket {
            private final double lowerLimit;
            private final double upperLimit;
            private final double rate;
            
            public TaxBracket(double lowerLimit, double upperLimit, double rate) {
                this.lowerLimit = lowerLimit;
                this.upperLimit = upperLimit;
                this.rate = rate;
            }
            
            public double getLowerLimit() { return lowerLimit; }
            public double getUpperLimit() { return upperLimit; }
            public double getRate() { return rate; }
        }
    }
    
    // Responsibility 5: Employee persistence
    public static class EmployeeRepository {
        private final Map<Long, Employee> database = new HashMap<>();
        private Long nextId = 1L;
        
        public Employee save(Employee employee) {
            if (employee.getId() == null) {
                employee.setId(nextId++);
            }
            database.put(employee.getId(), employee);
            System.out.println("✓ Employee saved to database: " + employee.getName());
            return employee;
        }
        
        public Optional<Employee> findById(Long id) {
            return Optional.ofNullable(database.get(id));
        }
        
        public Optional<Employee> findByEmail(String email) {
            return database.values().stream()
                .filter(e -> e.getEmail().equals(email))
                .findFirst();
        }
        
        public List<Employee> findByDepartment(String department) {
            return database.values().stream()
                .filter(e -> e.getDepartment().equals(department))
                .collect(ArrayList::new, (list, e) -> list.add(e), ArrayList::addAll);
        }
        
        public List<Employee> findAll() {
            return new ArrayList<>(database.values());
        }
        
        public void delete(Long id) {
            Employee removed = database.remove(id);
            if (removed != null) {
                System.out.println("✓ Employee deleted: " + removed.getName());
            }
        }
        
        public int count() {
            return database.size();
        }
    }
    
    // Responsibility 6: Report generation
    public static class EmployeeReportGenerator {
        private final SalaryCalculator salaryCalculator;
        private final TaxCalculator taxCalculator;
        private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        
        public EmployeeReportGenerator(SalaryCalculator salaryCalculator, TaxCalculator taxCalculator) {
            this.salaryCalculator = salaryCalculator;
            this.taxCalculator = taxCalculator;
        }
        
        public String generateDetailedReport(Employee employee) {
            double grossSalary = salaryCalculator.calculateGrossSalary(employee);
            double tax = taxCalculator.calculateTax(grossSalary);
            double netSalary = taxCalculator.calculateNetSalary(grossSalary);
            double bonus = salaryCalculator.calculateBonus(employee);
            
            StringBuilder report = new StringBuilder();
            report.append("\n╔════════════════════════════════════════════╗\n");
            report.append("║         EMPLOYEE DETAILED REPORT           ║\n");
            report.append("╠════════════════════════════════════════════╣\n");
            report.append(String.format("║ Name:         %-29s║\n", employee.getName()));
            report.append(String.format("║ Email:        %-29s║\n", employee.getEmail()));
            report.append(String.format("║ Department:   %-29s║\n", employee.getDepartment()));
            report.append(String.format("║ Position:     %-29s║\n", employee.getPosition()));
            report.append(String.format("║ Hire Date:    %-29s║\n", employee.getHireDate().format(dateFormatter)));
            report.append("╠════════════════════════════════════════════╣\n");
            report.append("║             COMPENSATION DETAILS           ║\n");
            report.append("╠════════════════════════════════════════════╣\n");
            report.append(String.format("║ Base Salary:  $%-28.2f║\n", employee.getBaseSalary()));
            report.append(String.format("║ Gross Salary: $%-28.2f║\n", grossSalary));
            report.append(String.format("║ Annual Bonus: $%-28.2f║\n", bonus));
            report.append(String.format("║ Tax Amount:   $%-28.2f║\n", tax));
            report.append(String.format("║ Net Salary:   $%-28.2f║\n", netSalary));
            report.append("╚════════════════════════════════════════════╝\n");
            
            return report.toString();
        }
        
        public String generateSummaryReport(List<Employee> employees) {
            StringBuilder report = new StringBuilder();
            report.append("\n═══════════════════════════════════════════════════════════════\n");
            report.append("                    EMPLOYEE SUMMARY REPORT                    \n");
            report.append("═══════════════════════════════════════════════════════════════\n");
            
            Map<String, List<Employee>> byDepartment = new HashMap<>();
            for (Employee e : employees) {
                byDepartment.computeIfAbsent(e.getDepartment(), k -> new ArrayList<>()).add(e);
            }
            
            for (Map.Entry<String, List<Employee>> entry : byDepartment.entrySet()) {
                report.append("\nDepartment: ").append(entry.getKey()).append("\n");
                report.append("─────────────────────────────────────────────────────────\n");
                
                double totalSalary = 0;
                for (Employee emp : entry.getValue()) {
                    double salary = salaryCalculator.calculateGrossSalary(emp);
                    totalSalary += salary;
                    report.append(String.format("  • %-25s %-15s $%,.2f\n", 
                        emp.getName(), emp.getPosition(), salary));
                }
                
                report.append("─────────────────────────────────────────────────────────\n");
                report.append(String.format("  Department Total: $%,.2f | Average: $%,.2f\n", 
                    totalSalary, totalSalary / entry.getValue().size()));
            }
            
            report.append("═══════════════════════════════════════════════════════════════\n");
            return report.toString();
        }
    }
    
    // Responsibility 7: Email notifications
    public static class EmailNotificationService {
        private final String smtpServer;
        private final int port;
        
        public EmailNotificationService(String smtpServer, int port) {
            this.smtpServer = smtpServer;
            this.port = port;
        }
        
        public void sendWelcomeEmail(Employee employee) {
            EmailMessage message = new EmailMessage();
            message.setTo(employee.getEmail());
            message.setSubject("Welcome to the Company, " + employee.getName() + "!");
            message.setBody(buildWelcomeEmailBody(employee));
            
            send(message);
        }
        
        public void sendPayslipEmail(Employee employee, double netSalary) {
            EmailMessage message = new EmailMessage();
            message.setTo(employee.getEmail());
            message.setSubject("Your Monthly Payslip");
            message.setBody(buildPayslipEmailBody(employee, netSalary));
            
            send(message);
        }
        
        public void sendPromotionEmail(Employee employee, String newPosition) {
            EmailMessage message = new EmailMessage();
            message.setTo(employee.getEmail());
            message.setSubject("Congratulations on Your Promotion!");
            message.setBody(buildPromotionEmailBody(employee, newPosition));
            
            send(message);
        }
        
        private String buildWelcomeEmailBody(Employee employee) {
            return String.format(
                "Dear %s,\n\n" +
                "Welcome to our %s team! We're excited to have you join us as a %s.\n\n" +
                "Your first day is %s. Please report to the HR department at 9:00 AM.\n\n" +
                "Best regards,\nHR Team",
                employee.getName(), employee.getDepartment(), employee.getPosition(),
                employee.getHireDate().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"))
            );
        }
        
        private String buildPayslipEmailBody(Employee employee, double netSalary) {
            return String.format(
                "Dear %s,\n\n" +
                "Your payslip for this month is ready.\n" +
                "Net Salary: $%,.2f\n\n" +
                "For detailed breakdown, please check the employee portal.\n\n" +
                "Best regards,\nPayroll Department",
                employee.getName(), netSalary
            );
        }
        
        private String buildPromotionEmailBody(Employee employee, String newPosition) {
            return String.format(
                "Dear %s,\n\n" +
                "Congratulations! You have been promoted from %s to %s.\n\n" +
                "Your new responsibilities will begin next month.\n\n" +
                "Best regards,\nManagement",
                employee.getName(), employee.getPosition(), newPosition
            );
        }
        
        private void send(EmailMessage message) {
            System.out.println("📧 Connecting to " + smtpServer + ":" + port);
            System.out.println("📧 To: " + message.getTo());
            System.out.println("📧 Subject: " + message.getSubject());
            System.out.println("📧 Body:\n" + message.getBody());
            System.out.println("✓ Email sent successfully\n");
        }
        
        private static class EmailMessage {
            private String to;
            private String subject;
            private String body;
            
            public String getTo() { return to; }
            public void setTo(String to) { this.to = to; }
            public String getSubject() { return subject; }
            public void setSubject(String subject) { this.subject = subject; }
            public String getBody() { return body; }
            public void setBody(String body) { this.body = body; }
        }
    }
    
    // Responsibility 8: Performance evaluation
    public static class PerformanceEvaluator {
        public enum PerformanceLevel {
            PROBATION("Probationary Period", 0),
            JUNIOR("Junior Level", 1),
            MID("Mid Level", 3),
            SENIOR("Senior Level", 5),
            EXPERT("Expert Level", 8);
            
            private final String description;
            private final int minYears;
            
            PerformanceLevel(String description, int minYears) {
                this.description = description;
                this.minYears = minYears;
            }
            
            public String getDescription() { return description; }
        }
        
        public PerformanceEvaluation evaluate(Employee employee) {
            int yearsOfService = LocalDate.now().getYear() - employee.getHireDate().getYear();
            PerformanceLevel level = determineLevel(yearsOfService);
            
            List<String> strengths = generateStrengths(employee, yearsOfService);
            List<String> improvements = generateImprovements(employee, yearsOfService);
            String recommendation = generateRecommendation(employee, level);
            
            return new PerformanceEvaluation(employee, level, strengths, improvements, recommendation);
        }
        
        private PerformanceLevel determineLevel(int yearsOfService) {
            if (yearsOfService >= 8) return PerformanceLevel.EXPERT;
            if (yearsOfService >= 5) return PerformanceLevel.SENIOR;
            if (yearsOfService >= 3) return PerformanceLevel.MID;
            if (yearsOfService >= 1) return PerformanceLevel.JUNIOR;
            return PerformanceLevel.PROBATION;
        }
        
        private List<String> generateStrengths(Employee employee, int yearsOfService) {
            List<String> strengths = new ArrayList<>();
            if (yearsOfService > 0) {
                strengths.add("Consistent attendance");
                strengths.add("Team collaboration");
            }
            if (yearsOfService > 2) {
                strengths.add("Technical expertise");
                strengths.add("Problem-solving skills");
            }
            if (yearsOfService > 5) {
                strengths.add("Leadership qualities");
                strengths.add("Mentoring junior staff");
            }
            return strengths;
        }
        
        private List<String> generateImprovements(Employee employee, int yearsOfService) {
            List<String> improvements = new ArrayList<>();
            if (yearsOfService < 1) {
                improvements.add("Learn company processes");
                improvements.add("Build domain knowledge");
            } else if (yearsOfService < 3) {
                improvements.add("Develop advanced skills");
                improvements.add("Take on more responsibilities");
            } else {
                improvements.add("Explore leadership opportunities");
                improvements.add("Share knowledge through documentation");
            }
            return improvements;
        }
        
        private String generateRecommendation(Employee employee, PerformanceLevel level) {
            switch (level) {
                case PROBATION:
                    return "Continue learning and adapting to company culture";
                case JUNIOR:
                    return "Ready for more complex assignments";
                case MID:
                    return "Consider for team lead responsibilities";
                case SENIOR:
                    return "Candidate for promotion to senior position";
                case EXPERT:
                    return "Key contributor - consider retention strategies";
                default:
                    return "Continue current development path";
            }
        }
        
        public static class PerformanceEvaluation {
            private final Employee employee;
            private final PerformanceLevel level;
            private final List<String> strengths;
            private final List<String> areasForImprovement;
            private final String recommendation;
            
            public PerformanceEvaluation(Employee employee, PerformanceLevel level,
                                        List<String> strengths, List<String> areasForImprovement,
                                        String recommendation) {
                this.employee = employee;
                this.level = level;
                this.strengths = strengths;
                this.areasForImprovement = areasForImprovement;
                this.recommendation = recommendation;
            }
            
            public String generateReport() {
                StringBuilder report = new StringBuilder();
                report.append("\n┌─────────────────────────────────────────┐\n");
                report.append("│       PERFORMANCE EVALUATION REPORT      │\n");
                report.append("├─────────────────────────────────────────┤\n");
                report.append("│ Employee: ").append(String.format("%-30s", employee.getName())).append("│\n");
                report.append("│ Level: ").append(String.format("%-33s", level.getDescription())).append("│\n");
                report.append("├─────────────────────────────────────────┤\n");
                report.append("│ Strengths:                              │\n");
                for (String strength : strengths) {
                    report.append("│  • ").append(String.format("%-37s", strength)).append("│\n");
                }
                report.append("├─────────────────────────────────────────┤\n");
                report.append("│ Areas for Improvement:                  │\n");
                for (String improvement : areasForImprovement) {
                    report.append("│  • ").append(String.format("%-37s", improvement)).append("│\n");
                }
                report.append("├─────────────────────────────────────────┤\n");
                report.append("│ Recommendation:                         │\n");
                report.append("│ ").append(String.format("%-40s", recommendation)).append("│\n");
                report.append("└─────────────────────────────────────────┘\n");
                return report.toString();
            }
        }
    }
    
    /**
     * Demonstration of the Employee Management System following SRP
     */
    public static void main(String[] args) {
        System.out.println("=== Employee Management System - SRP Demo ===\n");
        
        // Initialize all services (each with single responsibility)
        EmployeeValidator validator = new EmployeeValidator();
        SalaryCalculator salaryCalculator = new SalaryCalculator();
        TaxCalculator taxCalculator = new TaxCalculator();
        EmployeeRepository repository = new EmployeeRepository();
        EmployeeReportGenerator reportGenerator = new EmployeeReportGenerator(salaryCalculator, taxCalculator);
        EmailNotificationService emailService = new EmailNotificationService("smtp.company.com", 587);
        PerformanceEvaluator performanceEvaluator = new PerformanceEvaluator();
        
        // Create employees
        Employee emp1 = new Employee("John Smith", "john@company.com", "Engineering", 
                                    75000, "Senior Developer");
        Employee emp2 = new Employee("Jane Doe", "jane@company.com", "Sales", 
                                    65000, "Manager");
        Employee emp3 = new Employee("Bob Wilson", "bob@company.com", "Engineering", 
                                    55000, "Developer");
        
        // Set hire dates for demonstration
        emp1 = new Employee(null, emp1.getName(), emp1.getEmail(), emp1.getDepartment(),
                          emp1.getBaseSalary(), emp1.getPosition(), LocalDate.now().minusYears(4));
        emp2 = new Employee(null, emp2.getName(), emp2.getEmail(), emp2.getDepartment(),
                          emp2.getBaseSalary(), emp2.getPosition(), LocalDate.now().minusYears(6));
        emp3 = new Employee(null, emp3.getName(), emp3.getEmail(), emp3.getDepartment(),
                          emp3.getBaseSalary(), emp3.getPosition(), LocalDate.now().minusMonths(6));
        
        System.out.println("1. VALIDATING EMPLOYEES");
        System.out.println("─────────────────────────");
        for (Employee emp : Arrays.asList(emp1, emp2, emp3)) {
            EmployeeValidator.ValidationResult result = validator.validate(emp);
            if (result.isValid()) {
                System.out.println("✓ " + emp.getName() + " - Valid");
            } else {
                System.out.println("✗ " + emp.getName() + " - Invalid: " + result.getErrors());
            }
        }
        
        System.out.println("\n2. SAVING TO REPOSITORY");
        System.out.println("─────────────────────────");
        repository.save(emp1);
        repository.save(emp2);
        repository.save(emp3);
        
        System.out.println("\n3. SENDING WELCOME EMAILS");
        System.out.println("─────────────────────────");
        emailService.sendWelcomeEmail(emp3); // Only for new employee
        
        System.out.println("4. GENERATING INDIVIDUAL REPORTS");
        System.out.println("─────────────────────────────────");
        System.out.println(reportGenerator.generateDetailedReport(emp1));
        
        System.out.println("5. PERFORMANCE EVALUATIONS");
        System.out.println("─────────────────────────");
        for (Employee emp : repository.findAll()) {
            PerformanceEvaluator.PerformanceEvaluation evaluation = performanceEvaluator.evaluate(emp);
            System.out.println(evaluation.generateReport());
        }
        
        System.out.println("6. DEPARTMENT SUMMARY REPORT");
        System.out.println("─────────────────────────────");
        System.out.println(reportGenerator.generateSummaryReport(repository.findAll()));
        
        System.out.println("\n7. BENEFITS OF SRP IN THIS DESIGN:");
        System.out.println("───────────────────────────────────");
        System.out.println("• Each class has exactly one reason to change");
        System.out.println("• Easy to test individual components");
        System.out.println("• Can modify salary calculation without touching email service");
        System.out.println("• Can change report format without affecting validation");
        System.out.println("• New tax rules only require changes to TaxCalculator");
        System.out.println("• Can swap email service for SMS without changing other code");
        System.out.println("• Clear separation of concerns makes code maintainable");
        
        System.out.println("\n=== Demo Complete ===");
    }
}