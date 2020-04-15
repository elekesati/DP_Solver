/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dpsolver;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 *
 * @author Elekes Attila
 */
public class DpSolverController  {
    @FXML
    Button saveButton;
    @FXML
    Button openButton;
    @FXML
    Button runButton;
    
    @FXML
    private void saveAction(){
        System.out.println("saveButton pressed.");
    }
    
    @FXML
    private void openAction(){
        System.out.println("openButton pressed.");
    }
    
    @FXML
    private void runAction(){
        System.out.println("runButton pressed.");
    }
}
