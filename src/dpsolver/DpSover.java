/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dpsolver;

import java.io.File;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author Elekes Attila
 */
public class DpSover extends Application {
    
    private static final FileChooser.ExtensionFilter EXT_FILT = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
    private static final File workDirectory = new File(System.getProperty("user.home"), "Documents/DP Models");
    private static Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/dpsolver/view/dp_solver_view.fxml"));
        Parent root = (Parent) fxmlLoader.load();

        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/dpsolver/view/dp_solver_style.css").toExternalForm());

        stage.setScene(scene);
        stage.setTitle("DP-Solver - Untitled");
        stage.show();
    }

    public static File openFile() {
        FileChooser fileChooser = new FileChooser();
        
        if (!workDirectory.exists()){
            workDirectory.mkdirs();
        }
        
        fileChooser.setInitialDirectory(workDirectory);
        fileChooser.getExtensionFilters().add(EXT_FILT);
        fileChooser.setTitle("Open Model");
        return fileChooser.showOpenDialog(stage);
    }

    public static File saveFile() {
        FileChooser fileChooser = new FileChooser();
        
        if (!workDirectory.exists()){
            workDirectory.mkdirs();
        }
        
        fileChooser.setInitialDirectory(workDirectory);
        fileChooser.getExtensionFilters().add(EXT_FILT);
        fileChooser.setTitle("Save Model");
        return fileChooser.showSaveDialog(stage);
    }
    
    public static void setFileName(String fileName){
        stage.setTitle("DP-Solver - " + fileName);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
