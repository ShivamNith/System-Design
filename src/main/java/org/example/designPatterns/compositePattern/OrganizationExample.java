package org.example.designPatterns.compositePattern;

import java.util.*;
import java.text.SimpleDateFormat;

/**
 * Organization Structure Composite Pattern Example
 * 
 * This example demonstrates the Composite Pattern using an organizational hierarchy.
 * Employees are leaf objects, Teams are composite objects, and both implement
 * the OrganizationComponent interface uniformly.
 * 
 * Features:
 * - Hierarchical organization structure
 * - Uniform operations on employees and teams
 * - Salary management and raises
 * - Performance evaluation
 * - Team operations and reporting
 */

// Component interface
interface OrganizationComponent {
    String getName();
    String getTitle();
    double getSalary();
    int getEmployeeCount();
    void displayHierarchy(String indent);
    void giveRaise(double percentage);
    List<OrganizationComponent> findByTitle(String title);
    double getTotalSalaryCost();
    void displayTeamSummary();
    String getId();
}

// Leaf implementation - Employee
class Employee implements OrganizationComponent {
    private String id;
    private String name;
    private String title;
    private double salary;
    private String department;
    private Date hireDate;
    private List<String> skills;
    private int performanceRating; // 1-10 scale
    private String email;
    private String manager;
    
    public Employee(String name, String title, double salary, String department) {
        this.id = "EMP-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000);
        this.name = name;
        this.title = title;
        this.salary = salary;
        this.department = department;
        this.hireDate = new Date();
        this.skills = new ArrayList<>();
        this.performanceRating = 7; // Default good performance
        this.email = name.toLowerCase().replace(" ", ".") + "@company.com";
    }
    
    @Override
    public String getId() {
        return id;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public String getTitle() {
        return title;
    }
    
    @Override
    public double getSalary() {
        return salary;
    }
    
    @Override
    public int getEmployeeCount() {
        return 1;
    }
    
    @Override
    public void displayHierarchy(String indent) {
        String icon = getEmployeeIcon();
        System.out.println(indent + icon + " " + name + " - " + title);
        System.out.println(indent + "   📧 Email: " + email);
        System.out.println(indent + "   🏢 Department: " + department);
        System.out.println(indent + "   💰 Salary: $" + String.format("%,.2f", salary));
        System.out.println(indent + "   📅 Hire Date: " + formatDate(hireDate));
        System.out.println(indent + "   ⭐ Performance: " + performanceRating + "/10");
        if (!skills.isEmpty()) {
            System.out.println(indent + "   🎯 Skills: " + String.join(", ", skills));
        }
        if (manager != null) {
            System.out.println(indent + "   👤 Manager: " + manager);
        }
    }
    
    private String getEmployeeIcon() {
        if (title.toLowerCase().contains("manager") || title.toLowerCase().contains("director")) {
            return "👨‍💼";
        } else if (title.toLowerCase().contains("developer") || title.toLowerCase().contains("engineer")) {
            return "👨‍💻";
        } else if (title.toLowerCase().contains("designer")) {
            return "🎨";
        } else if (title.toLowerCase().contains("analyst")) {
            return "📊";
        } else {
            return "👤";
        }
    }
    
    @Override
    public void giveRaise(double percentage) {
        double oldSalary = salary;
        salary *= (1 + percentage / 100);
        System.out.println("💰 " + name + " received a " + percentage + "% raise: $" + 
                         String.format("%,.2f", oldSalary) + " → $" + String.format("%,.2f", salary));
    }
    
    @Override
    public List<OrganizationComponent> findByTitle(String title) {
        List<OrganizationComponent> results = new ArrayList<>();
        if (this.title.toLowerCase().contains(title.toLowerCase())) {
            results.add(this);
        }
        return results;
    }
    
    @Override
    public double getTotalSalaryCost() {
        return salary;
    }
    
    @Override
    public void displayTeamSummary() {
        System.out.println("👤 Individual Employee: " + name + " (" + title + ")");
        System.out.println("  💰 Salary: $" + String.format("%,.2f", salary));
        System.out.println("  ⭐ Performance: " + performanceRating + "/10");
    }
    
    public String getDepartment() {
        return department;
    }
    
    public Date getHireDate() {
        return hireDate;
    }
    
    public void addSkill(String skill) {
        skills.add(skill);
        System.out.println("🎯 " + name + " learned new skill: " + skill);
    }
    
    public List<String> getSkills() {
        return new ArrayList<>(skills);
    }
    
    public void promote(String newTitle, double newSalary) {
        String oldTitle = this.title;
        double oldSalary = this.salary;
        
        this.title = newTitle;
        this.salary = newSalary;
        
        System.out.println("🎉 " + name + " promoted from " + oldTitle + " to " + newTitle);
        System.out.println("   💰 Salary increased from $" + String.format("%,.2f", oldSalary) + 
                         " to $" + String.format("%,.2f", newSalary));
    }
    
    public void setPerformanceRating(int rating) {
        this.performanceRating = Math.max(1, Math.min(10, rating));
        System.out.println("⭐ " + name + " performance rating updated to: " + this.performanceRating + "/10");
    }
    
    public int getPerformanceRating() {
        return performanceRating;
    }
    
    public void setManager(String manager) {
        this.manager = manager;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void takeTraining(String course) {
        System.out.println("📚 " + name + " completed training: " + course);
        // Could add to skills or improve performance rating
    }
    
    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }
}

// Composite implementation - Team
class Team implements OrganizationComponent {
    private String id;
    private String name;
    private String title;
    private Employee manager;
    private List<OrganizationComponent> members;
    private String department;
    private double budget;
    private String mission;
    private Map<String, Object> metrics;
    private Date establishedDate;
    
    public Team(String name, String title, Employee manager, String department) {
        this.id = "TEAM-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000);
        this.name = name;
        this.title = title;
        this.manager = manager;
        this.department = department;
        this.members = new ArrayList<>();
        this.budget = 0.0;
        this.mission = "Excellence in " + title;
        this.metrics = new HashMap<>();
        this.establishedDate = new Date();
        
        // Manager is part of the team
        if (manager != null) {
            this.members.add(manager);
            manager.setManager("Self (Team Lead)");
        }
    }
    
    @Override
    public String getId() {
        return id;
    }
    
    public void addMember(OrganizationComponent member) {
        members.add(member);
        if (member instanceof Employee && manager != null) {
            ((Employee) member).setManager(manager.getName());
        }
        System.out.println("➕ Added " + member.getName() + " to team " + this.name);
    }
    
    public void removeMember(OrganizationComponent member) {
        if (members.remove(member)) {
            System.out.println("➖ Removed " + member.getName() + " from team " + this.name);
        }
    }
    
    public void removeMember(String name) {
        members.removeIf(member -> member.getName().equals(name));
        System.out.println("➖ Removed " + name + " from team " + this.name);
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public String getTitle() {
        return title;
    }
    
    @Override
    public double getSalary() {
        return manager != null ? manager.getSalary() : 0.0;
    }
    
    @Override
    public int getEmployeeCount() {
        return members.stream()
                .mapToInt(OrganizationComponent::getEmployeeCount)
                .sum();
    }
    
    @Override
    public void displayHierarchy(String indent) {
        System.out.println(indent + "🏢 " + name + " (" + title + ")");
        System.out.println(indent + "   🏢 Department: " + department);
        System.out.println(indent + "   👤 Manager: " + (manager != null ? manager.getName() : "None"));
        System.out.println(indent + "   👥 Total Members: " + getEmployeeCount());
        System.out.println(indent + "   💰 Total Salary Cost: $" + String.format("%,.2f", getTotalSalaryCost()));
        System.out.println(indent + "   📅 Established: " + formatDate(establishedDate));
        System.out.println(indent + "   🎯 Mission: " + mission);
        
        if (budget > 0) {
            System.out.println(indent + "   💼 Budget: $" + String.format("%,.2f", budget));
            double utilization = (getTotalSalaryCost() / budget) * 100;
            System.out.println(indent + "   📊 Budget Utilization: " + String.format("%.1f%%", utilization));
        }
        
        if (!metrics.isEmpty()) {
            System.out.println(indent + "   📈 Metrics: " + metrics);
        }
        
        // Display team members with increased indentation
        for (OrganizationComponent member : members) {
            member.displayHierarchy(indent + "  ");
        }
    }
    
    @Override
    public void giveRaise(double percentage) {
        System.out.println("💰 Giving " + percentage + "% raise to all members of team: " + name);
        for (OrganizationComponent member : members) {
            member.giveRaise(percentage);
        }
    }
    
    @Override
    public List<OrganizationComponent> findByTitle(String title) {
        List<OrganizationComponent> results = new ArrayList<>();
        
        if (this.title.toLowerCase().contains(title.toLowerCase())) {
            results.add(this);
        }
        
        for (OrganizationComponent member : members) {
            results.addAll(member.findByTitle(title));
        }
        
        return results;
    }
    
    @Override
    public double getTotalSalaryCost() {
        return members.stream()
                .mapToDouble(OrganizationComponent::getTotalSalaryCost)
                .sum();
    }
    
    @Override
    public void displayTeamSummary() {
        System.out.println("🏢 Team: " + name + " (" + title + ")");
        System.out.println("  👤 Manager: " + (manager != null ? manager.getName() : "None"));
        System.out.println("  👥 Members: " + getEmployeeCount());
        System.out.println("  💰 Total Salary Cost: $" + String.format("%,.2f", getTotalSalaryCost()));
        System.out.println("  📊 Average Salary: $" + String.format("%,.2f", 
            getTotalSalaryCost() / Math.max(getEmployeeCount(), 1)));
        
        if (budget > 0) {
            System.out.println("  💼 Budget: $" + String.format("%,.2f", budget));
        }
    }
    
    public Employee getManager() {
        return manager;
    }
    
    public void setManager(Employee manager) {
        this.manager = manager;
        // Update all team members' manager reference
        for (OrganizationComponent member : members) {
            if (member instanceof Employee) {
                ((Employee) member).setManager(manager.getName());
            }
        }
        System.out.println("👤 " + manager.getName() + " is now the manager of team " + name);
    }
    
    public String getDepartment() {
        return department;
    }
    
    public double getBudget() {
        return budget;
    }
    
    public void setBudget(double budget) {
        this.budget = budget;
        System.out.println("💼 Budget set for team " + name + ": $" + String.format("%,.2f", budget));
    }
    
    public List<OrganizationComponent> getMembers() {
        return new ArrayList<>(members);
    }
    
    public void setMission(String mission) {
        this.mission = mission;
        System.out.println("🎯 Mission updated for team " + name + ": " + mission);
    }
    
    public void addMetric(String key, Object value) {
        metrics.put(key, value);
        System.out.println("📈 Metric added to team " + name + ": " + key + " = " + value);
    }
    
    public void conductTeamMeeting(String topic) {
        System.out.println("\n📅 Team Meeting: " + name);
        System.out.println("📋 Topic: " + topic);
        System.out.println("👥 Attendees: " + getEmployeeCount() + " team members");
        System.out.println("👤 Led by: " + (manager != null ? manager.getName() : "Team"));
        System.out.println("📅 Date: " + formatDate(new Date()));
    }
    
    public void evaluatePerformance() {
        System.out.println("\n📊 Performance Evaluation for team: " + name);
        System.out.println("👥 Team Size: " + getEmployeeCount() + " members");
        System.out.println("💰 Total Salary Cost: $" + String.format("%,.2f", getTotalSalaryCost()));
        
        if (budget > 0) {
            double salaryBudgetRatio = (getTotalSalaryCost() / budget) * 100;
            System.out.println("📊 Salary/Budget Ratio: " + String.format("%.1f%%", salaryBudgetRatio));
        }
        
        // Calculate average performance rating
        double avgPerformance = members.stream()
            .filter(member -> member instanceof Employee)
            .mapToInt(member -> ((Employee) member).getPerformanceRating())
            .average()
            .orElse(0.0);
        
        System.out.println("⭐ Average Performance Rating: " + String.format("%.1f/10", avgPerformance));
        System.out.println("🤝 Team Collaboration Score: " + String.format("%.1f/10", Math.random() * 2 + 8));
        System.out.println("🎯 Goal Achievement: " + String.format("%.0f%%", Math.random() * 20 + 80));
    }
    
    public void organizeTraining(String course) {
        System.out.println("\n📚 Organizing training for team " + name + ": " + course);
        for (OrganizationComponent member : members) {
            if (member instanceof Employee) {
                ((Employee) member).takeTraining(course);
            }
        }
    }
    
    public void restructure() {
        System.out.println("🔄 Restructuring team: " + name);
        // Could implement team reorganization logic
        System.out.println("✅ Team restructuring completed");
    }
    
    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }
}

// Organization Manager
class OrganizationManager {
    private Team rootTeam;
    private Map<String, OrganizationComponent> directory;
    private List<String> announcements;
    
    public OrganizationManager(String organizationName, Employee ceo) {
        this.rootTeam = new Team(organizationName, "Organization", ceo, "Executive");
        this.directory = new HashMap<>();
        this.announcements = new ArrayList<>();
        buildDirectory();
    }
    
    private void buildDirectory() {
        directory.clear();
        addToDirectory(rootTeam);
    }
    
    private void addToDirectory(OrganizationComponent component) {
        directory.put(component.getId(), component);
        if (component instanceof Team) {
            Team team = (Team) component;
            for (OrganizationComponent member : team.getMembers()) {
                addToDirectory(member);
            }
        }
    }
    
    public Team getRootTeam() {
        return rootTeam;
    }
    
    public OrganizationComponent findById(String id) {
        return directory.get(id);
    }
    
    public void displayOrganizationChart() {
        System.out.println("=== 🏢 Organization Chart ===");
        rootTeam.displayHierarchy("");
        System.out.println();
    }
    
    public void showOrganizationSummary() {
        System.out.println("=== 📊 Organization Summary ===");
        System.out.println("👥 Total Employees: " + rootTeam.getEmployeeCount());
        System.out.println("💰 Total Salary Cost: $" + String.format("%,.2f", rootTeam.getTotalSalaryCost()));
        System.out.println("📊 Average Salary: $" + String.format("%,.2f", 
            rootTeam.getTotalSalaryCost() / Math.max(rootTeam.getEmployeeCount(), 1)));
        
        // Calculate departments
        Map<String, Integer> departmentCounts = new HashMap<>();
        countByDepartment(rootTeam, departmentCounts);
        System.out.println("🏢 Departments: " + departmentCounts.size());
        for (Map.Entry<String, Integer> entry : departmentCounts.entrySet()) {
            System.out.println("  - " + entry.getKey() + ": " + entry.getValue() + " employees");
        }
        System.out.println();
    }
    
    private void countByDepartment(OrganizationComponent component, Map<String, Integer> counts) {
        if (component instanceof Employee) {
            Employee emp = (Employee) component;
            counts.put(emp.getDepartment(), counts.getOrDefault(emp.getDepartment(), 0) + 1);
        } else if (component instanceof Team) {
            Team team = (Team) component;
            for (OrganizationComponent member : team.getMembers()) {
                countByDepartment(member, counts);
            }
        }
    }
    
    public void findEmployeesByTitle(String title) {
        List<OrganizationComponent> results = rootTeam.findByTitle(title);
        System.out.println("=== 🔍 Search Results for '" + title + "' ===");
        
        if (results.isEmpty()) {
            System.out.println("❌ No employees found with title containing: " + title);
        } else {
            System.out.println("✅ Found " + results.size() + " result(s):");
            for (OrganizationComponent result : results) {
                String type = result instanceof Team ? "🏢 TEAM" : "👤 EMPLOYEE";
                System.out.println("  " + type + " " + result.getName() + " (" + result.getTitle() + ")");
            }
        }
        System.out.println();
    }
    
    public void giveCompanyWideRaise(double percentage) {
        System.out.println("🎉 Company-wide " + percentage + "% salary increase!");
        rootTeam.giveRaise(percentage);
        System.out.println("✅ Raise applied to all " + rootTeam.getEmployeeCount() + " employees");
        
        addAnnouncement("Company-wide " + percentage + "% salary increase effective immediately");
        System.out.println();
    }
    
    public void addAnnouncement(String message) {
        announcements.add(new Date() + ": " + message);
    }
    
    public void showAnnouncements() {
        System.out.println("=== 📢 Company Announcements ===");
        if (announcements.isEmpty()) {
            System.out.println("No announcements at this time.");
        } else {
            for (String announcement : announcements) {
                System.out.println("📢 " + announcement);
            }
        }
        System.out.println();
    }
    
    public void conductAllHandsMeeting(String topic) {
        System.out.println("=== 📅 All-Hands Meeting ===");
        System.out.println("📋 Topic: " + topic);
        System.out.println("👥 Attendees: All " + rootTeam.getEmployeeCount() + " employees");
        System.out.println("📅 Date: " + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));
        addAnnouncement("All-hands meeting conducted: " + topic);
        System.out.println();
    }
    
    public void performanceReview() {
        System.out.println("=== 📊 Annual Performance Review ===");
        performanceReviewRecursive(rootTeam, "");
        System.out.println();
    }
    
    private void performanceReviewRecursive(OrganizationComponent component, String indent) {
        if (component instanceof Team) {
            Team team = (Team) component;
            System.out.println(indent + "🏢 Reviewing team: " + team.getName());
            team.evaluatePerformance();
            
            for (OrganizationComponent member : team.getMembers()) {
                performanceReviewRecursive(member, indent + "  ");
            }
        } else if (component instanceof Employee) {
            Employee emp = (Employee) component;
            System.out.println(indent + "👤 Reviewing employee: " + emp.getName() + 
                             " (Performance: " + emp.getPerformanceRating() + "/10)");
        }
    }
}

// Demo class
public class OrganizationExample {
    public static void main(String[] args) {
        System.out.println("=== 🏢 Composite Pattern: Organization Structure Example ===\n");
        
        // Create CEO
        Employee ceo = new Employee("Alice Johnson", "Chief Executive Officer", 250000, "Executive");
        ceo.addSkill("Leadership");
        ceo.addSkill("Strategic Planning");
        ceo.addSkill("Business Development");
        ceo.setPerformanceRating(9);
        
        // Create organization
        OrganizationManager orgManager = new OrganizationManager("TechCorp Inc.", ceo);
        Team organization = orgManager.getRootTeam();
        organization.setBudget(2000000);
        organization.setMission("Innovating technology solutions for tomorrow");
        
        // Create department managers
        Employee ctoDepartmentHead = new Employee("Bob Smith", "Chief Technology Officer", 180000, "Technology");
        Employee hrDepartmentHead = new Employee("Carol Davis", "HR Director", 120000, "Human Resources");
        Employee salesDepartmentHead = new Employee("David Wilson", "Sales Director", 140000, "Sales");
        
        // Add skills to managers
        ctoDepartmentHead.addSkill("Software Architecture");
        ctoDepartmentHead.addSkill("Team Leadership");
        ctoDepartmentHead.setPerformanceRating(8);
        
        hrDepartmentHead.addSkill("People Management");
        hrDepartmentHead.addSkill("Recruitment");
        hrDepartmentHead.setPerformanceRating(9);
        
        salesDepartmentHead.addSkill("Sales Strategy");
        salesDepartmentHead.addSkill("Client Relations");
        salesDepartmentHead.setPerformanceRating(8);
        
        // Create teams
        Team techTeam = new Team("Technology Department", "Technology Operations", ctoDepartmentHead, "Technology");
        Team hrTeam = new Team("Human Resources", "People Operations", hrDepartmentHead, "Human Resources");
        Team salesTeam = new Team("Sales Department", "Revenue Generation", salesDepartmentHead, "Sales");
        
        // Set team budgets and missions
        techTeam.setBudget(800000);
        techTeam.setMission("Building cutting-edge software solutions");
        hrTeam.setBudget(300000);
        hrTeam.setMission("Attracting and retaining top talent");
        salesTeam.setBudget(500000);
        salesTeam.setMission("Driving revenue growth and client satisfaction");
        
        // Add teams to organization
        organization.addMember(techTeam);
        organization.addMember(hrTeam);
        organization.addMember(salesTeam);
        
        // Create sub-teams within Technology
        Employee devManager = new Employee("Eve Brown", "Development Manager", 110000, "Technology");
        Employee qaManager = new Employee("Frank Miller", "QA Manager", 105000, "Technology");
        
        devManager.addSkill("Java");
        devManager.addSkill("Project Management");
        devManager.setPerformanceRating(8);
        
        qaManager.addSkill("Test Automation");
        qaManager.addSkill("Quality Assurance");
        qaManager.setPerformanceRating(9);
        
        Team devTeam = new Team("Development Team", "Software Development", devManager, "Technology");
        Team qaTeam = new Team("QA Team", "Quality Assurance", qaManager, "Technology");
        
        devTeam.setBudget(400000);
        qaTeam.setBudget(200000);
        
        techTeam.addMember(devTeam);
        techTeam.addMember(qaTeam);
        
        // Create individual employees
        Employee dev1 = new Employee("Grace Lee", "Senior Developer", 95000, "Technology");
        Employee dev2 = new Employee("Henry Chen", "Software Engineer", 80000, "Technology");
        Employee dev3 = new Employee("Ivy Martinez", "Junior Developer", 65000, "Technology");
        
        Employee qa1 = new Employee("Jack Taylor", "Senior QA Engineer", 85000, "Technology");
        Employee qa2 = new Employee("Kate Anderson", "QA Engineer", 70000, "Technology");
        
        Employee hr1 = new Employee("Liam Thompson", "HR Specialist", 60000, "Human Resources");
        Employee hr2 = new Employee("Mia Garcia", "Recruiter", 55000, "Human Resources");
        
        Employee sales1 = new Employee("Noah Rodriguez", "Senior Sales Rep", 90000, "Sales");
        Employee sales2 = new Employee("Olivia Williams", "Sales Representative", 75000, "Sales");
        Employee sales3 = new Employee("Paul Jones", "Sales Associate", 60000, "Sales");
        
        // Add skills to employees
        dev1.addSkill("Java"); dev1.addSkill("Spring"); dev1.addSkill("Microservices");
        dev2.addSkill("Python"); dev2.addSkill("Django"); dev2.addSkill("Docker");
        dev3.addSkill("JavaScript"); dev3.addSkill("React"); dev3.addSkill("Node.js");
        
        qa1.addSkill("Selenium"); qa1.addSkill("API Testing");
        qa2.addSkill("Manual Testing"); qa2.addSkill("Bug Tracking");
        
        hr1.addSkill("Employee Relations"); hr1.addSkill("Benefits Administration");
        hr2.addSkill("Talent Sourcing"); hr2.addSkill("Interview Coordination");
        
        sales1.addSkill("Enterprise Sales"); sales1.addSkill("Negotiation");
        sales2.addSkill("CRM"); sales2.addSkill("Lead Generation");
        sales3.addSkill("Customer Support"); sales3.addSkill("Product Demos");
        
        // Set performance ratings
        dev1.setPerformanceRating(9); dev2.setPerformanceRating(8); dev3.setPerformanceRating(7);
        qa1.setPerformanceRating(8); qa2.setPerformanceRating(7);
        hr1.setPerformanceRating(8); hr2.setPerformanceRating(9);
        sales1.setPerformanceRating(9); sales2.setPerformanceRating(7); sales3.setPerformanceRating(6);
        
        // Add employees to teams
        devTeam.addMember(dev1);
        devTeam.addMember(dev2);
        devTeam.addMember(dev3);
        
        qaTeam.addMember(qa1);
        qaTeam.addMember(qa2);
        
        hrTeam.addMember(hr1);
        hrTeam.addMember(hr2);
        
        salesTeam.addMember(sales1);
        salesTeam.addMember(sales2);
        salesTeam.addMember(sales3);
        
        // Add team metrics
        devTeam.addMetric("velocity", "85 story points/sprint");
        devTeam.addMetric("code_coverage", "92%");
        qaTeam.addMetric("bug_detection_rate", "96%");
        qaTeam.addMetric("test_automation", "78%");
        hrTeam.addMetric("employee_satisfaction", "4.2/5");
        hrTeam.addMetric("time_to_hire", "21 days");
        salesTeam.addMetric("quarterly_revenue", "$1.2M");
        salesTeam.addMetric("conversion_rate", "18%");
        
        // Display organization chart
        orgManager.displayOrganizationChart();
        
        // Show organization summary
        orgManager.showOrganizationSummary();
        
        // Demonstrate uniform operations
        System.out.println("=== 🔄 Uniform Operations Demo ===");
        
        // Search operations
        System.out.println("\n--- 🔍 Search Operations ---");
        orgManager.findEmployeesByTitle("Developer");
        orgManager.findEmployeesByTitle("Manager");
        orgManager.findEmployeesByTitle("Engineer");
        
        // Team operations
        System.out.println("--- 🏢 Team Operations ---");
        devTeam.conductTeamMeeting("Sprint Planning Session");
        qaTeam.conductTeamMeeting("Test Strategy Review");
        salesTeam.conductTeamMeeting("Q4 Sales Targets");
        
        // Training operations
        System.out.println("\n--- 📚 Training Operations ---");
        techTeam.organizeTraining("Advanced Java Programming");
        hrTeam.organizeTraining("Diversity & Inclusion Workshop");
        
        // Performance evaluations
        System.out.println("--- 📊 Performance Evaluations ---");
        devTeam.evaluatePerformance();
        qaTeam.evaluatePerformance();
        
        // Salary operations
        System.out.println("--- 💰 Salary Operations ---");
        System.out.println("Individual raise:");
        dev1.giveRaise(10); // Individual employee raise
        
        System.out.println("\nTeam raise:");
        qaTeam.giveRaise(7); // Entire team raise
        
        System.out.println("\nCompany-wide raise:");
        orgManager.giveCompanyWideRaise(5); // Company-wide raise
        
        // Promotions
        System.out.println("--- 🎉 Promotions ---");
        dev3.promote("Software Engineer", 75000);
        qa2.promote("Senior QA Engineer", 85000);
        
        // Company announcements
        System.out.println("--- 📢 Company Communications ---");
        orgManager.addAnnouncement("New office opening in Seattle");
        orgManager.addAnnouncement("Employee wellness program launching next month");
        orgManager.conductAllHandsMeeting("Q4 Company Strategy and Goals");
        orgManager.showAnnouncements();
        
        // Performance review cycle
        orgManager.performanceReview();
        
        // Final organization state
        System.out.println("=== 📊 Final Organization State ===");
        orgManager.displayOrganizationChart();
        orgManager.showOrganizationSummary();
        
        System.out.println("=== ✅ Demo Complete ===");
        
        // Key benefits demonstrated
        System.out.println("\n=== 🎯 Key Benefits Demonstrated ===");
        System.out.println("✅ Uniform interface for employees and teams");
        System.out.println("✅ Hierarchical organization structure");
        System.out.println("✅ Recursive operations (raises, evaluations, training)");
        System.out.println("✅ Easy to reorganize and restructure teams");
        System.out.println("✅ Scalable organization management");
        System.out.println("✅ Complex operations applied to entire organization tree");
    }
}