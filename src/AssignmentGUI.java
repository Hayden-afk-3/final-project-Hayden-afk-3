import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;

/**
 * The main graphical interface for the Assignment Tracker application.
 *
 * <p>This class builds and manages the Swing window that displays all assignments
 * in sorted order (by due date, then by point value). It provides the following
 * user actions via toolbar buttons:</p>
 * <ul>
 *   <li><b>Add</b> – prompts for a name, due date, and point value, then inserts
 *       the new assignment in sorted order.</li>
 *   <li><b>Edit</b> – lets the user update the name, due date, or point value of
 *       the currently selected assignment.</li>
 *   <li><b>Delete</b> – removes the currently selected assignment after confirmation.</li>
 *   <li><b>Mark Done / Undo</b> – toggles the completion status of the selected
 *       assignment; completed entries are visually struck-through and grayed out.</li>
 * </ul>
 *
 * <p>All changes are persisted immediately to disk via {@link FileManager}.</p>
 *
 * @author  Hayden D
 * @version 2.0
 * @see     Assignment
 * @see     AssignmentList
 * @see     FileManager
 */
public class AssignmentGUI {

    // Fields

    private AssignmentList list;

    /** The list model that bridges {@link #list} to the Swing {@link JList}. */
    private DefaultListModel<String> listModel;


    private JList<String> displayList;

    // Constructor / Initialization

    /**
     * Constructs the GUI window, loads any previously saved assignments from disk,
     * and makes the frame visible.
     *
     * <p>Must be called on the Swing Event Dispatch Thread.</p>
     */
    public AssignmentGUI() {
        list = FileManager.load();

        JFrame frame = new JFrame("Assignment Tracker");
        frame.setSize(560, 460);
        frame.setMinimumSize(new Dimension(400, 300));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // --- List setup ---
        listModel = new DefaultListModel<>();
        displayList = new JList<>(listModel);
        displayList.setCellRenderer(new AssignmentCellRenderer());
        displayList.setFont(new Font("Monospaced", Font.PLAIN, 13));
        displayList.setFixedCellHeight(36);

        refreshList();

        // --- Buttons ---
        JButton addButton    = new JButton("➕  Add");
        JButton editButton   = new JButton("✏️  Edit");
        JButton deleteButton = new JButton("🗑  Delete");
        JButton doneButton   = new JButton("✔  Mark Done");

        addButton.addActionListener(e    -> addAssignment());
        editButton.addActionListener(e   -> editAssignment());
        deleteButton.addActionListener(e -> deleteAssignment());
        doneButton.addActionListener(e   -> toggleDone());

        // --- Button bar ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(doneButton);

        // --- Title label ---
        JLabel titleLabel = new JLabel("📚  Assignment Tracker", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 4, 0));

        // --- Layout ---
        frame.setLayout(new BorderLayout());
        frame.add(titleLabel, BorderLayout.NORTH);
        frame.add(new JScrollPane(displayList), BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * Clears and repopulates {@link #listModel} from the current state of
     * {@link #list}. Call this after any mutation to keep the UI in sync.
     */
    private void refreshList() {
        listModel.clear();

        for (int i = 0; i < list.size(); i++) {
            Assignment a = list.getAssignment(i);

            String status = a.isCompleted() ? "✔ " : "   ";
            String entry = String.format("%s%-22s  Due: %04d/%02d/%02d  |  %d pts",
                    status,
                    truncate(a.getName(), 22),
                    a.getDueYear(), a.getDueMonth(), a.getDueDay(),
                    a.getPointWorth());

            listModel.addElement(entry);
        }
    }

    /**
     * Prompts the user for assignment details and adds a new {@link Assignment}
     * to the list. The list is re-sorted and saved after a successful addition.
     *
     * <p>Input format for the due date is {@code YYYY/MM/DD}. Invalid input
     * displays an error dialog and cancels the operation.</p>
     */
    private void addAssignment() {
        String name = JOptionPane.showInputDialog(null,
                "Assignment name:", "Add Assignment", JOptionPane.PLAIN_MESSAGE);
        if (name == null || name.isBlank()) return;

        String date = JOptionPane.showInputDialog(null,
                "Due date (YYYY/MM/DD):", "Add Assignment", JOptionPane.PLAIN_MESSAGE);
        if (date == null) return;

        String pointsStr = JOptionPane.showInputDialog(null,
                "Points worth:", "Add Assignment", JOptionPane.PLAIN_MESSAGE);
        if (pointsStr == null) return;

        try {
            String[] parts = date.split("/");
            int year  = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int day   = Integer.parseInt(parts[2]);
            int points = Integer.parseInt(pointsStr.trim());

            Assignment a = new Assignment(name.trim(), day, month, year, points);
            list.addAssignment(a);

            FileManager.save(list.getAssignments());
            refreshList();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Invalid input. Please check the date format (YYYY/MM/DD) and that points is a number.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Opens edit dialogs pre-filled with the selected assignment's current values,
     * allowing the user to update its name, due date, and/or point value.
     * The list is re-sorted and saved after a successful edit.
     *
     * <p>If no assignment is selected, an informational dialog is shown instead.</p>
     */
    private void editAssignment() {
        int index = displayList.getSelectedIndex();
        if (index < 0) {
            JOptionPane.showMessageDialog(null, "Please select an assignment to edit.");
            return;
        }

        Assignment a = list.getAssignment(index);

        // Pre-fill each field with current value
        String name = (String) JOptionPane.showInputDialog(null,
                "Assignment name:", "Edit Assignment",
                JOptionPane.PLAIN_MESSAGE, null, null, a.getName());
        if (name == null) return;

        String date = (String) JOptionPane.showInputDialog(null,
                "Due date (YYYY/MM/DD):", "Edit Assignment",
                JOptionPane.PLAIN_MESSAGE, null, null,
                String.format("%04d/%02d/%02d", a.getDueYear(), a.getDueMonth(), a.getDueDay()));
        if (date == null) return;

        String pointsStr = (String) JOptionPane.showInputDialog(null,
                "Points worth:", "Edit Assignment",
                JOptionPane.PLAIN_MESSAGE, null, null, a.getPointWorth());
        if (pointsStr == null) return;

        try {
            String[] parts = date.split("/");
            int year   = Integer.parseInt(parts[0]);
            int month  = Integer.parseInt(parts[1]);
            int day    = Integer.parseInt(parts[2]);
            int points = Integer.parseInt(pointsStr.trim());

            // Remove, update, and re-insert so sort order is maintained
            list.removeAssignment(index);
            a.setName(name.trim());
            a.setDueDate(day, month, year);
            a.setPointWorth(points);
            list.addAssignment(a);

            FileManager.save(list.getAssignments());
            refreshList();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Invalid input. Please check the date format (YYYY/MM/DD) and that points is a number.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Asks the user to confirm, then removes the selected assignment from the list
     * and saves the updated list to disk.
     *
     * <p>If no assignment is selected, an informational dialog is shown instead.</p>
     */
    private void deleteAssignment() {
        int index = displayList.getSelectedIndex();
        if (index < 0) {
            JOptionPane.showMessageDialog(null, "Please select an assignment to delete.");
            return;
        }

        Assignment a = list.getAssignment(index);
        int confirm = JOptionPane.showConfirmDialog(null,
                "Delete \"" + a.getName() + "\"?", "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            list.removeAssignment(index);
            FileManager.save(list.getAssignments());
            refreshList();
        }
    }

    /**
     * Toggles the completion status of the currently selected assignment.
     * Completed assignments are shown with a check-mark prefix and grayed out
     * in the list. Changes are saved to disk immediately.
     *
     * <p>If no assignment is selected, an informational dialog is shown instead.</p>
     */
    private void toggleDone() {
        int index = displayList.getSelectedIndex();
        if (index < 0) {
            JOptionPane.showMessageDialog(null, "Please select an assignment to mark.");
            return;
        }

        Assignment a = list.getAssignment(index);
        a.setCompleted(!a.isCompleted());

        FileManager.save(list.getAssignments());
        refreshList();

        // Keep the same row selected after refresh
        displayList.setSelectedIndex(index);
    }

    /**
     * Truncates {@code text} to at most {@code maxLen} characters, appending
     * {@code "…"} if the string was shortened. Used to keep the list display tidy.
     *
     * @param text   the string to truncate
     * @param maxLen the maximum number of characters to allow
     * @return the original string if shorter than {@code maxLen}; otherwise a
     *         truncated string ending with {@code "…"}
     */
    private static String truncate(String text, int maxLen) {
        if (text == null) return "";
        return text.length() <= maxLen ? text : text.substring(0, maxLen - 1) + "…";
    }

    // -------------------------------------------------------------------------
    // Inner classes
    // -------------------------------------------------------------------------

    /**
     * A custom {@link ListCellRenderer} that visually distinguishes completed
     * assignments from pending ones.
     *
     * <p>Completed entries are rendered in a muted gray; pending entries use
     * the default foreground color. The renderer also alternates row background
     * colors for readability.</p>
     */
    private class AssignmentCellRenderer extends JLabel implements ListCellRenderer<String> {

        /** Color for odd-numbered rows. */
        private static final Color ROW_ODD  = new Color(245, 245, 250);
        /** Color for even-numbered rows. */
        private static final Color ROW_EVEN = Color.WHITE;
        /** Color used for completed-assignment text. */
        private static final Color DONE_FG  = new Color(160, 160, 160);
        /** Highlight color for the selected row. */
        private static final Color SEL_BG   = new Color(180, 210, 255);

        /**
         * Constructs the renderer with left-aligned text and an empty border
         * for internal padding.
         */
        public AssignmentCellRenderer() {
            setOpaque(true);
            setHorizontalAlignment(LEFT);
            setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        }

        /**
         * {@inheritDoc}
         *
         * <p>Colors the cell background based on selection state and row parity,
         * and dims the foreground for completed assignments.</p>
         */
        @Override
        public Component getListCellRendererComponent(
                JList<? extends String> list,
                String value,
                int index,
                boolean isSelected,
                boolean cellHasFocus) {

            setText(value);

            if (isSelected) {
                setBackground(SEL_BG);
            } else {
                setBackground(index % 2 == 0 ? ROW_EVEN : ROW_ODD);
            }

            // Gray out completed items
            boolean done = AssignmentGUI.this.list.size() > index
                    && AssignmentGUI.this.list.getAssignment(index).isCompleted();
            setForeground(done ? DONE_FG : Color.BLACK);

            return this;
        }
    }
}
