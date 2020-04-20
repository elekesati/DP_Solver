/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dpsolver.controller;

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
        dpData = new DpData();
        addFormulaInputRow(EMPTY, EMPTY);
        updateStatus(DONE);
    }

    @FXML
    private void openAction(ActionEvent event) {
        FileHandler fileHandler = new FileHandler();
        try {
            updateStatus(LOAD);
            dpData = fileHandler.read(DpSover.openFile());
            updateInputFields();
            updateStatus(DONE);
        } catch (NullPointerException ex) {
            updateStatus(CANCEL);
        } catch (Exception ex) {
            updateStatus(ex.getMessage());
        }
    }

    @FXML
    private void saveAction(ActionEvent event) {
        FileHandler fileHandler = new FileHandler();
        try {
            updateStatus(SAVE);
            loadInputData();
            fileHandler.write(DpSover.saveFile(), dpData);
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
            AdditionalFunctions.initialize();
            DynamicProgram.load(dpData);

            resultTextField.setText(DynamicProgram.solve(dpData.getStartIndexesArray()).toString());
            updateStatus(DONE);
        } catch (NumberFormatException ex) {
            updateStatus(INPUT_ERROR + ex.getMessage() + ".");
        } catch (Exception ex) {
            updateStatus(ex.getMessage());
        }
    }

    @FXML
    private void addBranchAction(ActionEvent event) {
        addFormulaInputRow(EMPTY, EMPTY);
    }

    private void addFormulaInputRow(String branchText, String criteriaText) {
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
            branches.add(((TextField) children.get(i)).getText());
            criterias.add(((TextField) children.get(i + 1)).getText());
        }

        dpData.setBranches(branches)
                .setCriterias(criterias)
                .setDimension(dimensionTextField.getText())
                .setStartIndexes(startIndexesTextField.getText())
                .setTargetVariable(targetvariableTextField.getText())
                .setVariables(variablesTextArea.getText().split("\n"));
    }

    private void updateInputFields() {
        dimensionTextField.setText(dpData.getDimension());
        startIndexesTextField.setText(dpData.getStartIndexes());
        targetvariableTextField.setText(dpData.getTargetVariable());

        List<String> branches = dpData.getBranches();
        List<String> criterias = dpData.getCriterias();
        String[] variables = dpData.getVariables();

        for (int i = 0; i < branches.size(); ++i) {
            if (i == 0) {
                ((TextField) formulaInputGridPane.getChildren().get(2)).setText(branches.get(i));
                ((TextField) formulaInputGridPane.getChildren().get(3)).setText(criterias.get(i));
            } else {
                addFormulaInputRow(branches.get(i), criterias.get(i));
            }
        }

        for (int i = 0; i < variables.length; ++i) {
            variablesTextArea.appendText(variables[i]);
            variablesTextArea.appendText("\n");
        }
    }

    private void updateStatus(String text) {
        statusBarText.setText(text);
    }

    public void initializeAccelerators() {
        System.out.println(addBranchButton);
        System.out.println(addBranchButton.getScene());
        System.out.println(addBranchButton.getScene().getAccelerators());
        addBranchButton.getScene().getAccelerators()
                .put(new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN),
                        new Runnable() {
                    @Override
                    public void run() {
                        addFormulaInputRow(EMPTY, EMPTY);
                    }
                });
    }
}
