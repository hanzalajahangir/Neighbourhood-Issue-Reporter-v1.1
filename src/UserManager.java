import java.util.ArrayList;
import java.util.List;

public class UserManager {

    private List<User> users = new ArrayList<>();

    public UserManager() {
        // Default admin account seeded in memory
        users.add(new Admin("admin", "admin123", "System Admin"));
    }

    // ── Register citizen ─────────────────────────────────────
    public boolean register(String username, String password, String name) {
        if (findByUsername(username) != null) {
            return false;
        }
        users.add(new Citizen(username, password, name));
        return true;
    }

    // ── Login ────────────────────────────────────────────────
    public User login(String username, String password) {
        User u = findByUsername(username);
        if (u != null && u.checkPassword(password)) {
            return u;
        }
        return null;
    }

    // ── Find by username ─────────────────────────────────────
    public User findByUsername(String username) {
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(username)) {
                return u;
            }
        }
        return null;
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    // ── List all users ───────────────────────────────────────
    public void printAllUsers() {
        System.out.println("\n  Registered Users:");
        System.out.println("  ---------------------------------");
        for (User u : users) {
            System.out.printf("  %-15s %-10s %s%n", u.getUsername(), "[" + u.getRole() + "]", u.getName());
        }
        System.out.println();
    }
}
