import java.io.Serializable;

/**
 * Represents a single assignment with a name, point value, due date, and completion status.
 *
 * <p>Due dates are stored as three separate integer fields (day, month, year) and
 * combined into a single {@code int} in YYYYMMDD format for easy chronological sorting.</p>
 *
 * <p>This class implements {@link Serializable} so that assignments can be persisted
 * to disk via {@link FileManager}.</p>
 *
 * @author  Hayden Daugherty
 * @version 2.0
 * @see     AssignmentList
 * @see     FileManager
 */
public class Assignment implements Serializable {

    /** Required for safe serialization across class versions. */
    private static final long serialVersionUID = 2L;

    /** title of this assignment. */
    private String name;

    /** The number of points this assignment is worth. */
    private int pointWorth;

    /** The day component of the due date (1–31). */
    private int dueDay;

    /** The month component of the due date (1–12). */
    private int dueMonth;

    /** The four-digit year component of the due date (e.g. 2025). */
    private int dueYear;

    /** Whether this assignment has been marked as completed. */
    private boolean completed;

    /**
     * Constructs a new {@code Assignment} with the given name, due date, and point value.
     * The assignment is created in an incomplete state by default.
     *
     * @param name       the title / name of the assignment (e.g. "Lab 3", "Essay Draft")
     * @param theDay     the day of the month the assignment is due (1–31)
     * @param theMonth   the month the assignment is due (1–12)
     * @param theYear    the four-digit year the assignment is due
     * @param pointsWorth the number of points this assignment is worth
     */
    public Assignment(String name, int theDay, int theMonth, int theYear, int pointsWorth) {
        this.name      = name;
        this.pointWorth = pointsWorth;
        this.dueDay    = theDay;
        this.dueMonth  = theMonth;
        this.dueYear   = theYear;
        this.completed = false;
    }

    // Getters

    /**
     * Returns the name / title of this assignment.
     *
     * @return the assignment name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the number of points this assignment is worth.
     *
     * @return point value (positive integer)
     */
    public int getPointWorth() {
        return pointWorth;
    }

    /**
     * Returns the due date as a single integer in {@code YYYYMMDD} format.
     * This representation makes chronological comparison trivial with standard
     * integer comparison operators.
     *
     * <p>Example: a due date of March 5, 2025 returns {@code 20250305}.</p>
     *
     * @return the due date encoded as {@code YYYYMMDD}
     */
    public int getDueDate() {
        return dueYear * 10000 + dueMonth * 100 + dueDay;
    }

    /**
     * Returns the day component of the due date.
     *
     * @return day of the month (1–31)
     */
    public int getDueDay() {
        return dueDay;
    }

    /**
     * Returns the month component of the due date.
     *
     * @return month number (1–12)
     */
    public int getDueMonth() {
        return dueMonth;
    }

    /**
     * Returns the four-digit year component of the due date.
     *
     * @return the due year (e.g. 2025)
     */
    public int getDueYear() {
        return dueYear;
    }

    /**
     * Returns whether this assignment has been marked as completed.
     *
     * @return {@code true} if the assignment is done; {@code false} otherwise
     */
    public boolean isCompleted() {
        return completed;
    }

    // Setters

    /**
     * Updates the name / title of this assignment.
     *
     * @param name the new assignment name; must not be {@code null}
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Updates the due date of this assignment.
     *
     * @param newDay   the new day of the month (1–31)
     * @param newMonth the new month (1–12)
     * @param newYear  the new four-digit year
     */
    public void setDueDate(int newDay, int newMonth, int newYear) {
        this.dueDay   = newDay;
        this.dueMonth = newMonth;
        this.dueYear  = newYear;
    }

    /**
     * Updates the point value of this assignment.
     *
     * @param newPoints the new point worth (must be a positive integer)
     */
    public void setPointWorth(int newPoints) {
        this.pointWorth = newPoints;
    }

    /**
     * Marks this assignment as completed or incomplete.
     *
     * @param completed {@code true} to mark as done; {@code false} to mark as incomplete
     */
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    /**
     * Returns a concise string representation of this assignment, useful for
     * debugging and logging.
     *
     * @return a string in the format {@code Assignment[name, YYYY/MM/DD, Xpts, done/pending]}
     */
    @Override
    public String toString() {
        return String.format("Assignment[\"%s\", %04d/%02d/%02d, %dpts, %s]",
                name, dueYear, dueMonth, dueDay, pointWorth,
                completed ? "done" : "pending");
    }
}
