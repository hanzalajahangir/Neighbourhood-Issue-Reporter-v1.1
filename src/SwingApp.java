import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SwingApp extends JFrame {
    private static final Color PAGE_BACKGROUND = new Color(244, 247, 251);
    private static final Color CARD_BACKGROUND = Color.WHITE;
    private static final Color TEXT_MUTED = new Color(89, 99, 110);
    private static final Color PRIMARY = new Color(37, 99, 235);
    private static final Dimension AUTH_FIELD_SIZE = new Dimension(320, 34);

    private final IssueManager issueManager = new IssueManager();
    private final UserManager userManager = new UserManager();

    private User currentUser;
    private JTable issueTable;
    private DefaultTableModel issueTableModel;
    private JLabel titleLabel;
    private JLabel statsLabel;

    public SwingApp() {
        setTitle("Neighbourhood Issue Reporter");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(980, 620);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(820, 520));
        showAuthPanel();
    }

    private void showAuthPanel() {
        JPanel root = new JPanel(new BorderLayout(18, 18));
        root.setBorder(new EmptyBorder(32, 36, 32, 36));
        root.setBackground(PAGE_BACKGROUND);

        JPanel header = new JPanel(new GridLayout(0, 1, 4, 4));
        header.setOpaque(false);
        JLabel appName = new JLabel("Neighbourhood Issue Reporter");
        appName.setHorizontalAlignment(SwingConstants.CENTER);
        appName.setFont(new Font("Segoe UI", Font.BOLD, 28));
        JLabel tagline = new JLabel("Report, track, and resolve community issues.");
        tagline.setHorizontalAlignment(SwingConstants.CENTER);
        tagline.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        tagline.setForeground(TEXT_MUTED);
        header.add(appName);
        header.add(tagline);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabs.addTab("Login", createLoginPanel());
        tabs.addTab("Register", createRegisterPanel());

        root.add(header, BorderLayout.NORTH);
        root.add(tabs, BorderLayout.CENTER);
        setContentPane(root);
        revalidate();
        repaint();
    }

    private JPanel createLoginPanel() {
        JPanel panel = createFormPanel("Welcome back", "Sign in to manage neighbourhood reports.");
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JButton loginButton = new JButton("Login");
        stylePrimaryButton(loginButton);

        addAuthField(panel, "Username", usernameField, 2);
        addAuthField(panel, "Password", passwordField, 3);
        addAuthButton(panel, loginButton, 4);

        getRootPane().setDefaultButton(loginButton);
        loginButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            currentUser = userManager.login(username, password);

            if (currentUser == null) {
                showError("Invalid username or password.");
                return;
            }
            showDashboard();
        });
        return panel;
    }

    private JPanel createRegisterPanel() {
        JPanel panel = createFormPanel("Create an account", "Register as a citizen to report local issues.");
        JTextField nameField = new JTextField();
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JButton registerButton = new JButton("Register");
        stylePrimaryButton(registerButton);

        addAuthField(panel, "Full name", nameField, 2);
        addAuthField(panel, "Username", usernameField, 3);
        addAuthField(panel, "Password", passwordField, 4);
        addAuthButton(panel, registerButton, 5);

        registerButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if (name.isEmpty() || username.isEmpty() || password.isEmpty()) {
                showError("Please fill in all registration fields.");
                return;
            }
            if (!userManager.register(username, password, name)) {
                showError("That username is already taken.");
                return;
            }

            nameField.setText("");
            usernameField.setText("");
            passwordField.setText("");
            JOptionPane.showMessageDialog(this, "Registered successfully. You can now log in.");
        });
        return panel;
    }

    private JPanel createFormPanel(String heading, String subheading) {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(PAGE_BACKGROUND);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(CARD_BACKGROUND);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(222, 228, 238)),
                new EmptyBorder(28, 34, 30, 34)
        ));

        JLabel headingLabel = new JLabel(heading);
        headingLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headingLabel.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel subheadingLabel = new JLabel(subheading);
        subheadingLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subheadingLabel.setForeground(TEXT_MUTED);
        subheadingLabel.setHorizontalAlignment(SwingConstants.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 4, 0);
        form.add(headingLabel, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 18, 0);
        form.add(subheadingLabel, gbc);

        wrapper.putClientProperty("formPanel", form);
        wrapper.add(form);
        return wrapper;
    }

    private void addAuthField(JPanel panel, String label, JComponent field, int row) {
        JPanel form = getAuthForm(panel);
        JLabel jLabel = new JLabel(label);
        jLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        jLabel.setForeground(new Color(42, 51, 65));

        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(AUTH_FIELD_SIZE);
        field.setMinimumSize(AUTH_FIELD_SIZE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 6, 16);
        form.add(jLabel, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.insets = new Insets(0, 0, 6, 0);
        form.add(field, gbc);
    }

    private void addAuthButton(JPanel panel, JButton button, int row) {
        JPanel form = getAuthForm(panel);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(12, 0, 0, 0);
        form.add(button, gbc);
    }

    private JPanel getAuthForm(JPanel panel) {
        Object form = panel.getClientProperty("formPanel");
        return form instanceof JPanel ? (JPanel) form : panel;
    }

    private void stylePrimaryButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(PRIMARY);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(8, 22, 8, 22));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void addField(JPanel panel, String label, JComponent field) {
        JLabel jLabel = new JLabel(label);
        jLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(jLabel);
        panel.add(field);
    }

    private void showDashboard() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(18, 18, 18, 18));
        root.setBackground(new Color(244, 247, 251));

        titleLabel = new JLabel();
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        statsLabel = new JLabel();
        statsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statsLabel.setForeground(new Color(89, 99, 110));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JPanel titleBox = new JPanel(new GridLayout(0, 1, 2, 2));
        titleBox.setOpaque(false);
        titleBox.add(titleLabel);
        titleBox.add(statsLabel);

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            currentUser = null;
            showAuthPanel();
        });
        header.add(titleBox, BorderLayout.WEST);
        header.add(logoutButton, BorderLayout.EAST);

        issueTableModel = new DefaultTableModel(
                new String[]{"ID", "Title", "Category", "Priority", "Status", "Department", "Location", "Reported By", "Date"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        issueTable = new JTable(issueTableModel);
        issueTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        issueTable.setRowHeight(24);
        issueTable.getTableHeader().setReorderingAllowed(false);

        root.add(header, BorderLayout.NORTH);
        root.add(new JScrollPane(issueTable), BorderLayout.CENTER);
        root.add(createActionPanel(), BorderLayout.SOUTH);

        setContentPane(root);
        refreshDashboard();
        revalidate();
        repaint();
    }

    private JPanel createActionPanel() {
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        actions.setOpaque(false);

        JButton reportButton = new JButton("Report Issue");
        JButton detailsButton = new JButton("View Details");
        JButton refreshButton = new JButton("Refresh");
        reportButton.addActionListener(e -> showIssueForm());
        detailsButton.addActionListener(e -> showSelectedIssueDetails());
        refreshButton.addActionListener(e -> refreshDashboard());

        actions.add(reportButton);
        actions.add(detailsButton);
        actions.add(refreshButton);

        if (isAdmin()) {
            JButton statusButton = new JButton("Update Status");
            JButton deptButton = new JButton("Assign Department");
            JButton priorityButton = new JButton("Update Priority");
            JButton deleteButton = new JButton("Delete");
            JButton usersButton = new JButton("Users");

            statusButton.addActionListener(e -> updateSelectedIssue("Status", Issue.STATUSES));
            deptButton.addActionListener(e -> updateSelectedIssue("Department", Issue.DEPARTMENTS));
            priorityButton.addActionListener(e -> updateSelectedIssue("Priority", Issue.PRIORITIES));
            deleteButton.addActionListener(e -> deleteSelectedIssue());
            usersButton.addActionListener(e -> showUsers());

            actions.add(statusButton);
            actions.add(deptButton);
            actions.add(priorityButton);
            actions.add(deleteButton);
            actions.add(usersButton);
        }

        return actions;
    }

    private void showIssueForm() {
        JTextField titleField = new JTextField();
        JTextArea descriptionArea = new JTextArea(4, 24);
        JTextField locationField = new JTextField();
        JComboBox<String> categoryBox = new JComboBox<>(Issue.CATEGORIES);
        JComboBox<String> priorityBox = new JComboBox<>(Issue.PRIORITIES);

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        addField(form, "Title", titleField);
        addField(form, "Description", new JScrollPane(descriptionArea));
        addField(form, "Location", locationField);
        addField(form, "Category", categoryBox);
        addField(form, "Priority", priorityBox);

        int choice = JOptionPane.showConfirmDialog(this, form, "Report New Issue", JOptionPane.OK_CANCEL_OPTION);
        if (choice != JOptionPane.OK_OPTION) return;

        String title = titleField.getText().trim();
        if (title.isEmpty()) {
            showError("Issue title is required.");
            return;
        }

        Issue issue = new Issue(
                title,
                descriptionArea.getText().trim(),
                locationField.getText().trim(),
                (String) categoryBox.getSelectedItem(),
                (String) priorityBox.getSelectedItem(),
                currentUser.getUsername()
        );
        issueManager.addIssue(issue);
        refreshDashboard();
        JOptionPane.showMessageDialog(this, "Issue #" + issue.getId() + " reported.");
    }

    private void refreshDashboard() {
        titleLabel.setText("Welcome, " + currentUser.getName() + " (" + currentUser.getRole() + ")");
        List<Issue> issues = isAdmin()
                ? issueManager.getAllIssues()
                : issueManager.getByUser(currentUser.getUsername());

        issueTableModel.setRowCount(0);
        for (Issue issue : issues) {
            issueTableModel.addRow(new Object[]{
                    issue.getId(),
                    issue.getTitle(),
                    issue.getCategory(),
                    issue.getPriority(),
                    issue.getStatus(),
                    issue.getDepartment(),
                    issue.getLocation(),
                    issue.getReportedBy(),
                    issue.getDateReported()
            });
        }

        statsLabel.setText("Total: " + issueManager.getAllIssues().size()
                + "    Pending: " + countStatus("Pending")
                + "    In Progress: " + countStatus("In Progress")
                + "    Resolved: " + countStatus("Resolved"));
    }

    private int countStatus(String status) {
        int count = 0;
        for (Issue issue : issueManager.getAllIssues()) {
            if (issue.getStatus().equalsIgnoreCase(status)) {
                count++;
            }
        }
        return count;
    }

    private void showSelectedIssueDetails() {
        Issue issue = getSelectedIssue();
        if (issue == null) return;

        JTextArea details = new JTextArea(formatIssue(issue), 12, 42);
        details.setEditable(false);
        details.setFont(new Font("Consolas", Font.PLAIN, 13));
        JOptionPane.showMessageDialog(this, new JScrollPane(details), "Issue #" + issue.getId(), JOptionPane.INFORMATION_MESSAGE);
    }

    private String formatIssue(Issue issue) {
        return "Title       : " + issue.getTitle() + "\n"
                + "Description : " + issue.getDescription() + "\n"
                + "Location    : " + issue.getLocation() + "\n"
                + "Category    : " + issue.getCategory() + "\n"
                + "Priority    : " + issue.getPriority() + "\n"
                + "Status      : " + issue.getStatus() + "\n"
                + "Department  : " + issue.getDepartment() + "\n"
                + "Reported By : " + issue.getReportedBy() + "\n"
                + "Date        : " + issue.getDateReported();
    }

    private void updateSelectedIssue(String label, String[] options) {
        Issue issue = getSelectedIssue();
        if (issue == null) return;

        String selected = (String) JOptionPane.showInputDialog(
                this,
                "Select " + label.toLowerCase() + ":",
                "Update " + label,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]
        );
        if (selected == null) return;

        if (label.equals("Status")) {
            issueManager.updateStatus(issue.getId(), selected);
        } else if (label.equals("Department")) {
            issueManager.assignDepartment(issue.getId(), selected);
        } else {
            issueManager.updatePriority(issue.getId(), selected);
        }
        refreshDashboard();
    }

    private void deleteSelectedIssue() {
        Issue issue = getSelectedIssue();
        if (issue == null) return;

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete issue #" + issue.getId() + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm == JOptionPane.YES_OPTION) {
            issueManager.deleteIssue(issue.getId());
            refreshDashboard();
        }
    }

    private void showUsers() {
        StringBuilder builder = new StringBuilder();
        for (User user : userManager.getAllUsers()) {
            builder.append(user.getUsername())
                    .append("    [")
                    .append(user.getRole())
                    .append("]    ")
                    .append(user.getName())
                    .append("\n");
        }

        JTextArea usersArea = new JTextArea(builder.toString(), 10, 36);
        usersArea.setEditable(false);
        usersArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        JOptionPane.showMessageDialog(this, new JScrollPane(usersArea), "Registered Users", JOptionPane.INFORMATION_MESSAGE);
    }

    private Issue getSelectedIssue() {
        int row = issueTable.getSelectedRow();
        if (row < 0) {
            showError("Please select an issue first.");
            return null;
        }
        int modelRow = issueTable.convertRowIndexToModel(row);
        int id = (int) issueTableModel.getValueAt(modelRow, 0);
        return issueManager.getById(id);
    }

    private boolean isAdmin() {
        return currentUser != null && currentUser.getRole().equals("Admin");
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
