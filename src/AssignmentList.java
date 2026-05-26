import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * An ordered collection of {@link Assignment} objects.
 *
 * <p>Assignments are kept in ascending chronological order by due date at all times.
 * When two assignments share the same due date, the one with the higher point value
 * is placed first so that higher-stakes work is always visible at the top of the list.</p>
 *
 * <p>This class implements {@link Serializable} so the entire list can be written to
 * and read from disk in one operation by {@link FileManager}.</p>
 *
 * @author  Hayden
 * @version 2.0
 * @see     Assignment
 * @see     FileManager
 */
public class AssignmentList implements Serializable {

    /** Required for safe serialization across class versions. */
    private static final long serialVersionUID = 1L;

    /** The backing store for all assignments. */
    private ArrayList<Assignment> assignments;

    /**
     * Constructs an empty {@code AssignmentList}.
     */
    public AssignmentList() {
        assignments = new ArrayList<>();
    }

    /**
     * Inserts an assignment into the list in sorted order.
     *
     * <p>Sorting rules (applied in priority order):
     * <ol>
     *   <li>Earlier due date comes first (ascending YYYYMMDD).</li>
     *   <li>On a tie, higher point value comes first (descending points).</li>
     * </ol>
     * </p>
     *
     * @param assignment the {@link Assignment} to insert; must not be {@code null}
     */
    public void addAssignment(Assignment assignment) {
        int insertIndex = assignments.size();

        for (int i = 0; i < assignments.size(); i++) {
            Assignment current = assignments.get(i);

            if (assignment.getDueDate() < current.getDueDate()) {
                insertIndex = i;
                break;
            } else if (assignment.getDueDate() == current.getDueDate()) {
                if (assignment.getPointWorth() > current.getPointWorth()) {
                    insertIndex = i;
                    break;
                }
            }
        }

        assignments.add(insertIndex, assignment);
    }

    /**
     * Removes the assignment at the specified index.
     *
     * @param index the zero-based index of the assignment to remove
     * @throws IndexOutOfBoundsException if {@code index} is out of range
     */
    public void removeAssignment(int index) {
        assignments.remove(index);
    }

    /**
     * Retrieves the assignment at the specified index without removing it.
     *
     * @param index the zero-based index of the desired assignment
     * @return the {@link Assignment} at {@code index}
     * @throws IndexOutOfBoundsException if {@code index} is out of range
     */
    public Assignment getAssignment(int index) {
        return assignments.get(index);
    }

    /**
     * Returns the number of assignments currently in the list.
     *
     * @return the list size (0 or greater)
     */
    public int size() {
        return assignments.size();
    }

    /**
     * Returns a direct reference to the underlying {@link List} of assignments.
     *
     * <p><strong>Note:</strong> Callers should treat this list as read-only; mutating
     * it directly will bypass the sorted-insertion logic in {@link #addAssignment}.</p>
     *
     * @return the backing {@link ArrayList} of assignments
     */
    public List<Assignment> getAssignments() {
        return assignments;
    }
}
