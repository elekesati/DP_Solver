/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dpsolver.controller;

import dpsolver.helpers.FileHandler;
import dpsolver.helpers.AdditionalFunctions;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.control.TextArea;
import java.util.ArrayList;
import java.util.List;
import dpsolver.model.*;
import dpsolver.DpSover;
import java.io.File;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

/**
 * FXML Controller class
 *
 * @author Elekes Attila
 */
public class DpSolverController implements Initializable {

    private static final String START = "Starting.";
    private static final String LOAD = "Loading model.";
    private static final String SAVE = "Saving model.";
    private static final String CREATE = "Creating model.";
    private static final String RUN = "Run.";
    private static final String DONE = "Done.";
    private static final String INPUT_ERROR = "Input error ";
    private static final String CANCEL = "Canceled.";
    private static final String EMPTY = "";

    @FXML
    private GridPane formulaInputGridPane;
    @FXML
    private TextField targetvariableTextField;
    @FXML
    private TextField dimensionTextField;
    @FXML
    private TextField startIndexesTextField;
    @FXML
    private TextField resultTextField;
    @FXML
    private Button addBranchButton;
    @FXML
    private Button removeBranchButton;
    @FXML
    private Label statusBarText;
    @FXML
    private TextArea variablesTextArea;

    private int numRows = 1;
    private DpData dpData;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        updateStatus(START);
        AdditionalFunctions.initialize();
        Variables.initialize();
        dpData = new DpData();
        addFormulaInputRow(EMPTY, EMPTY);
        updateStatus(DONE);
    }
    
    @FXML
    private void newAction(ActionEvent event) {
        try {
            updateStatus(CREATE);
            DpSover.setFileName("Untitled");
            resetInputFields();
            updateStatus(DONE);
        } catch (NullPointerException ex) {
            updateStatus(CANCEL);
        } catch (Exception ex) {
            updateStatus(ex.getMessage());
        }
    }
    @FXML
    private void openAction(ActionEvent event) {
        try {
            updateStatus(LOAD);
            File model = DpSover.openFile();
            dpData = FileHandler.read(model);
            updateInputFields();
            DpSover.setFileName(model.getName());
            updateStatus(DONE);
        } catch (NullPointerException ex) {
            updateStatus(CANCEL);
        } catch (Exception ex) {
            updateStatus(ex.getMessage());
        }
    }

    @FXML
    private void saveAction(ActionEvent event) {
        try {
            updateStatus(SAVE);
            loadInputData();
            File model = DpSover.saveFile();
            FileHandler.write(model, dpData);
            DpSover.setFileName(model.getName());
            updateStatus(DONE);
        } catch (NullPointerException ex) {
            updateStatus(CANCEL);
        } catch (Exception ex) {
            updateStatus(ex.getMessage());
        }
    }

    @FXML
    private void runAction(ActionEvent event) {
        DynamicProgram.restart();
        try {
            updateStatus(RUN);
            loadInputData();
            DynamicProgram.load(dpData);
            
            resultTextField.setText(DynamicProgram.solve(dpData.getStartIndexesArray()).toString());
            DynamicProgram.printLog();
            System.out.println("");
            DynamicProgram.printHierarchy();
            updateStatus(DONE);
        } catch (NumberFormatException ex) {
            updateStatus(INPUT_ERROR + ex.getMessage() + ".");
        } catch (Exception ex) {
            updateStatus(ex.toString() + " " + ex.getMessage());
        }
    }

    @FXML
    private void addBranchAction(ActionEvent event) {
        addFormulaInputRow(EMPTY, EMPTY);
    }
    
    @FXML
    private void removeBranchAction(ActionEvent event) {
        List children = formulaInputGridPane.getChildren();
        if (children.size() > 4){
            children.remove(children.size() - 1);
            children.remove(children.size() - 1);
        }
    }

    private void addFormulaInputRow(String branchText, String criteriaText) {
        List children = formulaInputGridPane.getChildren();
        int numChildren = children.size();
        
        TextField branchTextField = new TextField();
        branchTextField.setFont(Font.font("monospace"));
        branchTextField.setPrefColumnCount(25);
        branchTextField.setText(branchText);

        TextField criteriaTextField = new TextField();
        criteriaTextField.setFont(Font.font("monospace"));
        criteriaTextField.setPrefColumnCount(25);
        criteriaTextField.setText(criteriaText);

        formulaInputGridPane.add(branchTextField, 0, numRows);
        formulaInputGridPane.add(criteriaTextField, 1, numRows);

        ++numRows;
    }

    private void loadInputData() {
        List<String> branches = new ArrayList<>();
        List<String> criterias = new ArrayList<>();
        List children = formulaInputGridPane.getChildren();

        for (int i = 2; i < children.size(); i += 2) {
            branches.add(((TextField) children.get(i)).getText().trim());
            criterias.add(((TextField) children.get(i + 1)).getText().trim());
        }
        
        dpData.setBranches(branches)
                .setCriterias(criterias)
                .setDimension(dimensionTextField.getText().trim())
                .setStartIndexes(startIndexesTextField.getText().trim())
                .setTargetVariable(targetvariableTextField.getText().trim())
                .setVariables(variablesTextArea.getText().trim().split("\n"));
    }

    private void updateInputFields() {
        dimensionTextField.setText(dpData.getDimension());
        startIndexesTextField.setText(dpData.getStartIndexes());
        targetvariableTextField.setText(dpData.getTargetVariable());

        List<String> branches = dpData.getBranches();
        List<String> criterias = dpData.getCriterias();
        String[] variables = dpData.getVariables();

        int textFieldIndex = 2;
        
        for (int i = 0; i < branches.size(); ++i) {
            if (textFieldIndex < formulaInputGridPane.getChildren().size()) {
                ((TextField) formulaInputGridPane.getChildren().get(textFieldIndex)).setText(branches.get(i));
                ++textFieldIndex;
                ((TextField) formulaInputGridPane.getChildren().get(textFieldIndex)).setText(criterias.get(i));
                ++textFieldIndex;
            } else {
                addFormulaInputRow(branches.get(i), criterias.get(i));
                textFieldIndex += 2;
            }
        }

        variablesTextArea.clear();
        for (int i = 0; i < variables.length; ++i) {
            variablesTextArea.appendText(variables[i]);
            variablesTextArea.appendText("\n");
        }
    }
    
    private void resetInputFields() {
        dimensionTextField.clear();
        startIndexesTextField.clear();
        targetvariableTextField.clear();
        variablesTextArea.clear();

        List children = formulaInputGridPane.getChildren();
        while (children.size() > 4){
            children.remove(children.size() - 1);
            children.remove(children.size() - 1);
        }
        
        ((TextField) children.get(children.size() - 1)).clear();
        ((TextField) children.get(children.size() - 2)).clear();
    }

    private void updateStatus(String text) {
        statusBarText.setText(text);
    }

    public void initializeAccelerators() {
        System.out.println(addBranchButton);
        System.out.println(addBranchButton.getScene());
        System.out.println(addBranchButton.getScene().getAccelerators());
        addBranchButton.getScene().getAccelerators()
            .put(new KeyCodeCombination(KeyCode.A, KeyCombination.ALT_DOWN),
                    new Runnable() {
                @Override
                public void run() {
                    addFormulaInputRow(EMPTY, EMPTY);
                }
            });
    }
}