/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dpsolver;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Elekes Attila
 */
public class DpSover extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("dp_solver.fxml"));
        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("dp_solver_style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("DP-Solver");
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
