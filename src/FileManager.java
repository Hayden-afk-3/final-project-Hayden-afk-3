import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * Utility class that handles persistent storage of the assignment list.
 *
 * <p>Assignments are serialized to a binary file ({@value #DATA_FILE}) in the
 * working directory using Java's built-in object serialization. The entire
 * {@link AssignmentList} is written and read as a single object.</p>
 *
 * <p>All methods are {@code static}; this class is not meant to be instantiated.</p>
 *
 * @author  Hayden
 * @version 2.0
 * @see     AssignmentList
 * @see     Assignment
 */
public class FileManager {

    /** The file name used to persist assignment data between sessions. */
    private static final String DATA_FILE = "assignments.dat";

    /** Private constructor — this is a utility class and should never be instantiated. */
    private FileManager() {}

    /**
     * Serializes the given list of assignments to {@value #DATA_FILE}.
     *
     * <p>If the file does not exist it will be created. If it already exists its
     * contents will be overwritten. Any {@link IOException} encountered during
     * writing is printed to standard error but does not propagate to the caller.</p>
     *
     * @param list the list of {@link Assignment} objects to save; must not be {@code null}
     */
    public static void save(List<Assignment> list) {
        try (ObjectOutputStream out =
                     new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            out.writeObject(list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deserializes the assignment list from {@value #DATA_FILE}.
     *
     * <p>If the file does not exist, or if any error occurs while reading it
     * (e.g. corrupted data, class mismatch), an empty {@link AssignmentList}
     * is returned so the application can start cleanly.</p>
     *
     * @return the persisted {@link AssignmentList}, or a new empty list if the
     *         file cannot be read
     */
    public static AssignmentList load() {
        try (ObjectInputStream in =
                     new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            return (AssignmentList) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new AssignmentList();
        }
    }
}
