package jaudioeditor;

import org.jdesktop.application.Application;

/**
 * The main class of the application.
 *
 * @author Dmitry Krivenko <dmitrykrivenko@gmal.com>
 */
public class JAudioEditorApp extends JAudioSingleFrameApp {

    /**
     * At startup create and show the main frame of the application.
     */
    @Override
    protected void startup() {
        show(new JAudioEditorView(this));
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override
    protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     *
     * @return the instance of JAudioEditorApp
     */
    public static JAudioEditorApp getApplication() {
        return Application.getInstance(JAudioEditorApp.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        launch(JAudioEditorApp.class, args);
    }
}
