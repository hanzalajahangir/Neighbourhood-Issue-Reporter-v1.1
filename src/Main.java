import javax.swing.SwingUtilities;
import java.util.Scanner;

public class Main {

    private static Scanner sc = new Scanner(System.in);
    private static IssueManager issueManager = new IssueManager();
    private static UserManager userManager  = new UserManager();
    private static User currentUser  = null;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SwingApp().setVisible(true));
    }

    // ════════════════════════════════════════════════════════
    //  BANNER
    // ════════════════════════════════════════════════════════
    private static void printBanner() {
        System.out.println();
        System.out.println("  ================================================");
        System.out.println("      NEIGHBORHOOD ISSUE REPORTER  v1.0");
        System.out.println("      Report . Track . Resolve Community Issues");
        System.out.println("  ================================================");
        System.out.println();
    }

    // ════════════════════════════════════════════════════════
    //  MAIN MENU
    // ════════════════════════════════════════════════════════
    private static void mainMenu() {
        while (true) {
            System.out.println("  +-----------------------+");
            System.out.println("  |      MAIN MENU        |");
            System.out.println("  +-----------------------+");
            System.out.println("  | 1. Login              |");
            System.out.println("  | 2. Register           |");
            System.out.println("  | 3. Exit               |");
            System.out.println("  +-----------------------+");
            System.out.print("  Choose: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1": login();    break;
                case "2": register(); break;
                case "3":
                    System.out.println("\n  Goodbye!\n");
                    System.exit(0);
                default:
                    System.out.println("  [!] Invalid option. Try again.\n");
            }
        }
    }

    // ════════════════════════════════════════════════════════
    //  AUTH
    // ════════════════════════════════════════════════════════
    private static void login() {
        System.out.println("\n  -- LOGIN ------------------");
        System.out.print("  Username: ");
        String user = sc.nextLine().trim();
        System.out.print("  Password: ");
        String pass = sc.nextLine().trim();

        currentUser = userManager.login(user, pass);
        if (currentUser == null) {
            System.out.println("  [!] Invalid credentials.\n");
            return;
        }
        System.out.println("  [OK] Welcome, " + currentUser.getName() +
                " (" + currentUser.getRole() + ")!\n");

        if (currentUser.getRole().equals("Admin")) {
            adminMenu();
        } else {
            citizenMenu();
        }
    }

    private static void register() {
        System.out.println("\n  -- REGISTER ---------------");
        System.out.print("  Full Name : ");
        String name = sc.nextLine().trim();
        System.out.print("  Username  : ");
        String user = sc.nextLine().trim();
        System.out.print("  Password  : ");
        String pass = sc.nextLine().trim();

        if (name.isEmpty() || user.isEmpty() || pass.isEmpty()) {
            System.out.println("  [!] All fields are required.\n");
            return;
        }
        if (userManager.register(user, pass, name)) {
            System.out.println("  [OK] Registered! You can now log in.\n");
        } else {
            System.out.println("  [!] Username already taken.\n");
        }
    }

    // ════════════════════════════════════════════════════════
    //  CITIZEN MENU
    // ════════════════════════════════════════════════════════
    private static void citizenMenu() {
        while (true) {
            System.out.println("  +------------------------------+");
            System.out.println("  |     CITIZEN DASHBOARD        |");
            System.out.println("  +------------------------------+");
            System.out.println("  | 1. Report a New Issue        |");
            System.out.println("  | 2. View My Issues            |");
            System.out.println("  | 3. View Issue Details        |");
            System.out.println("  | 4. View All Issues           |");
            System.out.println("  | 5. Logout                    |");
            System.out.println("  +------------------------------+");
            System.out.print("  Choose: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1": reportIssue(); break;
                case "2": issueManager.printList(
                        issueManager.getByUser(currentUser.getUsername())); break;
                case "3": viewIssueDetails(); break;
                case "4": issueManager.printList(issueManager.getAllIssues()); break;
                case "5":
                    currentUser = null;
                    System.out.println("  [OK] Logged out.\n");
                    return;
                default:
                    System.out.println("  [!] Invalid option.\n");
            }
        }
    }

    // ════════════════════════════════════════════════════════
    //  ADMIN MENU
    // ════════════════════════════════════════════════════════
    private static void adminMenu() {
        while (true) {
            System.out.println("  +------------------------------+");
            System.out.println("  |      ADMIN DASHBOARD         |");
            System.out.println("  +------------------------------+");
            System.out.println("  | 1. View All Issues           |");
            System.out.println("  | 2. View Issue Details        |");
            System.out.println("  | 3. Update Issue Status       |");
            System.out.println("  | 4. Assign Department         |");
            System.out.println("  | 5. Update Priority           |");
            System.out.println("  | 6. Delete Issue              |");
            System.out.println("  | 7. View Statistics           |");
            System.out.println("  | 8. View All Users            |");
            System.out.println("  | 0. Logout                    |");
            System.out.println("  +------------------------------+");
            System.out.print("  Choose: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1": issueManager.printList(issueManager.getAllIssues()); break;
                case "2": viewIssueDetails();  break;
                case "3": updateStatus();       break;
                case "4": assignDepartment();   break;
                case "5": updatePriority();     break;
                case "6": deleteIssue();        break;
                case "7": issueManager.printStatistics(); break;
                case "8": userManager.printAllUsers();    break;
                case "0":
                    currentUser = null;
                    System.out.println("  [OK] Logged out.\n");
                    return;
                default:
                    System.out.println("  [!] Invalid option.\n");
            }
        }
    }

    // ════════════════════════════════════════════════════════
    //  REPORT ISSUE
    // ════════════════════════════════════════════════════════
    private static void reportIssue() {
        System.out.println("\n  -- REPORT A NEW ISSUE -----");

        System.out.print("  Title       : ");
        String title = sc.nextLine().trim();
        if (title.isEmpty()) {
            System.out.println("  [!] Title cannot be empty.\n");
            return;
        }

        System.out.print("  Description : ");
        String desc = sc.nextLine().trim();

        System.out.print("  Location    : ");
        String location = sc.nextLine().trim();

        String category = pickOption("Category", Issue.CATEGORIES);
        if (category == null) return;

        String priority = pickOption("Priority", Issue.PRIORITIES);
        if (priority == null) return;

        Issue issue = new Issue(title, desc, location, category, priority,
                currentUser.getUsername());
        issueManager.addIssue(issue);
        System.out.println("  [OK] Issue #" + issue.getId() + " reported!\n");
    }

    // ════════════════════════════════════════════════════════
    //  VIEW DETAILS
    // ════════════════════════════════════════════════════════
    private static void viewIssueDetails() {
        System.out.print("  Enter Issue ID: ");
        try {
            int id = Integer.parseInt(sc.nextLine().trim());
            Issue issue = issueManager.getById(id);
            if (issue == null) System.out.println("  [!] Issue not found.\n");
            else               issue.printFull();
        } catch (NumberFormatException e) {
            System.out.println("  [!] Please enter a valid number.\n");
        }
    }

    // ════════════════════════════════════════════════════════
    //  ADMIN ACTIONS
    // ════════════════════════════════════════════════════════
    private static void updateStatus() {
        System.out.print("  Enter Issue ID: ");
        try {
            int id = Integer.parseInt(sc.nextLine().trim());
            String status = pickOption("New Status", Issue.STATUSES);
            if (status == null) return;
            if (issueManager.updateStatus(id, status))
                System.out.println("  [OK] Status updated to \"" + status + "\".\n");
            else
                System.out.println("  [!] Issue not found.\n");
        } catch (NumberFormatException e) {
            System.out.println("  [!] Invalid ID.\n");
        }
    }

    private static void assignDepartment() {
        System.out.print("  Enter Issue ID: ");
        try {
            int id = Integer.parseInt(sc.nextLine().trim());
            String dept = pickOption("Department", Issue.DEPARTMENTS);
            if (dept == null) return;
            if (issueManager.assignDepartment(id, dept))
                System.out.println("  [OK] Assigned to " + dept + ".\n");
            else
                System.out.println("  [!] Issue not found.\n");
        } catch (NumberFormatException e) {
            System.out.println("  [!] Invalid ID.\n");
        }
    }

    private static void updatePriority() {
        System.out.print("  Enter Issue ID: ");
        try {
            int id = Integer.parseInt(sc.nextLine().trim());
            String priority = pickOption("New Priority", Issue.PRIORITIES);
            if (priority == null) return;
            if (issueManager.updatePriority(id, priority))
                System.out.println("  [OK] Priority updated.\n");
            else
                System.out.println("  [!] Issue not found.\n");
        } catch (NumberFormatException e) {
            System.out.println("  [!] Invalid ID.\n");
        }
    }

    private static void deleteIssue() {
        System.out.print("  Enter Issue ID to delete: ");
        try {
            int id = Integer.parseInt(sc.nextLine().trim());
            System.out.print("  Are you sure? (yes/no): ");
            String confirm = sc.nextLine().trim();
            if (!confirm.equalsIgnoreCase("yes")) {
                System.out.println("  Cancelled.\n");
                return;
            }
            if (issueManager.deleteIssue(id))
                System.out.println("  [OK] Issue #" + id + " deleted.\n");
            else
                System.out.println("  [!] Issue not found.\n");
        } catch (NumberFormatException e) {
            System.out.println("  [!] Invalid ID.\n");
        }
    }

    // ════════════════════════════════════════════════════════
    //  HELPER: numbered picker
    // ════════════════════════════════════════════════════════
    private static String pickOption(String label, String[] options) {
        System.out.println("  Select " + label + ":");
        for (int i = 0; i < options.length; i++) {
            System.out.printf("    %d. %s%n", i + 1, options[i]);
        }
        System.out.print("  Choice: ");
        try {
            int idx = Integer.parseInt(sc.nextLine().trim()) - 1;
            if (idx < 0 || idx >= options.length) {
                System.out.println("  [!] Invalid selection.\n");
                return null;
            }
            return options[idx];
        } catch (NumberFormatException e) {
            System.out.println("  [!] Please enter a number.\n");
            return null;
        }
    }
}
