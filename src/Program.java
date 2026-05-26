import javax.swing.SwingUtilities;

/**
 * Entry point for the Assignment Tracker application.
 *
 * <p>Launches the Swing GUI on the Event Dispatch Thread (EDT) as required by
 * Swing's single-threaded model. All subsequent UI work is handled by
 * {@link AssignmentGUI}.</p>
 *
 * <p>To run the application, compile all source files and execute this class:</p>
 * <pre>{@code
 *   javac *.java
 *   java Program
 * }</pre>
 *
 * @author  Hayden Daugherty
 * @version 2.0
 * @see     AssignmentGUI
 */
public class Program {

    /**
     * Application entry point.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AssignmentGUI());
    }
}
