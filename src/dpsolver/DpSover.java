package dpsolver;

import dpsolver.controller.DpSolverController;
import dpsolver.controller.VisualizationController;
import dpsolver.model.DpData;
import dpsolver.model.DpLog;
import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author Elekes Attila
 */
public class DpSover extends Application {

    private static final FileChooser.ExtensionFilter EXT_FILT = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
    private static final File workDirectory = new File(System.getProperty("user.home"), "Documents/DP Models");
    private static Stage mainStage;
    private static Stage visualizationStage = null;
    private static DpSolverController dpSolverController;
    private static VisualizationController visualizationController;

    @Override
    public void start(Stage primaryStage) throws Exception {
        mainStage = primaryStage;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/dpsolver/view/dp_solver_view.fxml"));
        Parent root = (Parent) fxmlLoader.load();
        Scene scene = new Scene(root);

        mainStage.setScene(scene);
        mainStage.setTitle("DP-Solver - Untitled");
        mainStage.show();

        dpSolverController = (DpSolverController) fxmlLoader.getController();
        dpSolverController.setStage(mainStage);
        dpSolverController.setStageProperties();
        dpSolverController.initializeAccelerators();
    }

    /**
     * Creates new window for the visualization.
     *
     * @param dpData DpData of the model
     * @param log sequence of the steps and log of the errors
     * @param hierarchy parent-child relations between the nodes
     * @throws Exception
     */
    public static void openVisualizationWindow(DpData dpData, List<DpLog> log, Map<String, HashSet<int[]>> hierarchy)
            throws Exception {
        visualizationStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(DpSover.class.getResource("/dpsolver/view/visualization_view.fxml"));
        Parent root = (Parent) fxmlLoader.load();
        Scene scene = new Scene(root);

        visualizationStage.setScene(scene);
        visualizationStage.setTitle(mainStage.getTitle());
        visualizationStage.setFullScreen(true);
        visualizationStage.setResizable(false);
        visualizationStage.show();

        visualizationController = (VisualizationController) fxmlLoader.getController();
        visualizationController.initializeData(dpData, log, hierarchy);
        visualizationController.setStage(visualizationStage);
        visualizationController.setStageProperties();
        visualizationController.prepareVisualization();
        visualizationController.initializeAccelerators();
    }

    /**
     * Shows a file chooser window for opening a model.
     *
     * @return selected file
     */
    public static File openFile() {
        FileChooser fileChooser = new FileChooser();

        if (!workDirectory.exists()) {
            workDirectory.mkdirs();
        }

        fileChooser.setInitialDirectory(workDirectory);
        fileChooser.getExtensionFilters().add(EXT_FILT);
        fileChooser.setTitle("Open Model");
        return fileChooser.showOpenDialog(mainStage);
    }

    /**
     * Shows a file chooser window for saving a model.
     *
     * @return selected file
     */
    public static File saveFile() {
        FileChooser fileChooser = new FileChooser();

        if (!workDirectory.exists()) {
            workDirectory.mkdirs();
        }

        fileChooser.setInitialDirectory(workDirectory);
        fileChooser.getExtensionFilters().add(EXT_FILT);
        fileChooser.setTitle("Save Model");
        return fileChooser.showSaveDialog(mainStage);
    }

    /**
     * Updates the title of the window with the name of the opened file
     *
     * @param fileName name of the opened file
     */
    public static void setFileName(String fileName) {
        mainStage.setTitle("DP-Solver - " + fileName);
    }

    /**
     * Stops all background tasks and closes all windows.
     */
    public static void closeAll() {
        if (visualizationStage != null) {
            if (visualizationStage.isShowing()) {
                visualizationStage.fireEvent(new WindowEvent(visualizationStage, WindowEvent.WINDOW_CLOSE_REQUEST));
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
