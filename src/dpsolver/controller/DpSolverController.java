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
import java.util.Optional;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import net.objecthunter.exp4j.tokenizer.UnknownFunctionOrVariableException;

/**
 * FXML Controller class
 *
 * @author Elekes Attila
 */
public class DpSolverController implements Initializable {

    private static final int YES = 0;
    private static final int NO = 1;
    private static final int CANCEL_OPERATION = 2;
    private static final int ESC_FROM_DIALOG = 3;

    private static final String START = "Starting.";
    private static final String LOAD = "Loading model.";
    private static final String SAVE = "Saving model.";
    private static final String CREATE = "Creating model.";
    private static final String RUN = "Run.";
    private static final String DONE = "Done.";
    private static final String INPUT_ERROR = "Input error - ";
    private static final String CANCEL = "Canceled.";
    private static final String EMPTY = "";

    private static final String UNSAVED_CHANGES_TITLE = "There are unsaved changes";
    private static final String UNSAVED_CHANGES_QUESTION = "Do you want to save changes?";
    private static final String NOT_EMPTY_BRANCH_TITLE = "Input fields are not empty";
    private static final String NOT_EMPTY_BRANCH_QUESTION = "Do you want to delete the not empty fields?";

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
    private Button showVisualizationButton;
    @FXML
    private Button fullScreenButton;
    @FXML
    private Label statusBarText;
    @FXML
    private TextArea variablesTextArea;

    private Stage mStage;

    private int numRows = 1;
    private DpData dpData;
    private boolean dpDone = false;
    private File model = null;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        updateStatus(START);
        AdditionalFunctions.initialize();
        Variables.initialize();
        addFormulaInputRow(EMPTY, EMPTY);
        dpData = loadInputData();
        updateStatus(DONE);
    }

    /**
     * Creates a new empty model. Clears all input fields.
     */
    @FXML
    private void newAction(ActionEvent event) {

        if (hasUnsavedChanges()) {
            switch (showConfirmDialog(UNSAVED_CHANGES_TITLE, UNSAVED_CHANGES_QUESTION, true)) {
                case CANCEL_OPERATION:
                    return;

                case YES:
                    saveAction(null);
                    break;

                case NO:
                    break;

                case ESC_FROM_DIALOG:
                    return;
            }
        }

        if (statusBarText.getText().equals(CANCEL)) {
            return;
        }

        try {
            updateStatus(CREATE);
            DpSover.setFileName("Untitled");
            resetInputFields();
            dpData = loadInputData();
            dpDone = false;
            model = null;
            updateStatus(DONE);
        } catch (NullPointerException ex) {
            updateStatus(CANCEL);
        } catch (Exception ex) {
            updateStatus(ex.getMessage());
        }
    }

    /**
     * Loads a model from file.
     */
    @FXML
    private void openAction(ActionEvent event) {

        if (hasUnsavedChanges()) {
            switch (showConfirmDialog(UNSAVED_CHANGES_TITLE, UNSAVED_CHANGES_QUESTION, true)) {
                case CANCEL_OPERATION:
                    return;

                case YES:
                    saveAction(null);
                    break;

                case NO:
                    break;

                case ESC_FROM_DIALOG:
                    return;
            }
        }

        if (statusBarText.getText().equals(CANCEL)) {
            return;
        }

        try {
            updateStatus(LOAD);
            model = DpSover.openFile();
            dpData = FileHandler.read(model);
            resetInputFields();
            updateInputFields();
            DpSover.setFileName(model.getName());
            updateStatus(DONE);
        } catch (NullPointerException ex) {
            model = null;
            updateStatus(CANCEL);
        } catch (Exception ex) {
            model = null;
            updateStatus(ex.getMessage());
        }
    }

    /**
     * Saves a model to a new file, or updates the file if the model was
     * previously loaded.
     */
    @FXML
    private void saveAction(ActionEvent event) {
        try {
            updateStatus(SAVE);

            if (model == null) {
                model = DpSover.saveFile();
            }

            dpData = loadInputData();
            FileHandler.write(model, dpData);
            DpSover.setFileName(model.getName());
            updateStatus(DONE);
        } catch (NullPointerException ex) {
            updateStatus(CANCEL);
        } catch (Exception ex) {
            updateStatus(ex.getMessage());
        }
    }

    /**
     * Saves a model to a new file.
     */
    @FXML
    private void saveAsAction(ActionEvent event) {
        try {
            updateStatus(SAVE);
            model = DpSover.saveFile();
            dpData = loadInputData();
            FileHandler.write(model, dpData);
            DpSover.setFileName(model.getName());
            updateStatus(DONE);
        } catch (NullPointerException ex) {
            updateStatus(CANCEL);
        } catch (Exception ex) {
            updateStatus(ex.getMessage());
        }
    }

    /**
     * Runs the dynamic program.
     */
    @FXML
    private void runAction(ActionEvent event) /*throws Exception*/ {
        DynamicProgram.restart();
        dpDone = false;
        try {
            updateStatus(RUN);
            resultTextField.clear();
            checkDimensionAndStartIndexInputs();
            DpData data = loadInputData();
            DynamicProgram.load(data);
            resultTextField.setText(DynamicProgram.solve(data.getStartIndexesArray()).toString());
            updateStatus(DONE);
        } catch (NumberFormatException ex) {
            updateStatus(INPUT_ERROR + ex.getMessage() + ".");
        } catch (UnknownFunctionOrVariableException ex) {
            updateStatus(INPUT_ERROR + " " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            updateStatus(INPUT_ERROR + " " + ex.getMessage());
        } catch (Exception ex) {
            updateStatus(ex.getMessage());
        } finally {
            dpDone = true;
        }
    }

    /**
     * Adds two new empty input fields for a new branch.
     */
    @FXML
    private void addBranchAction(ActionEvent event) {
        addFormulaInputRow(EMPTY, EMPTY);
    }

    /**
     * Removes a pair of input fields from the formula ecction of the UI.
     */
    @FXML
    private void removeBranchAction(ActionEvent event) {
        List children = formulaInputGridPane.getChildren();

        if (children.size() > 4) {
            TextField branch = (TextField) children.get(children.size() - 2);
            TextField criteria = (TextField) children.get(children.size() - 1);

            if (!branch.getText().isEmpty() || !criteria.getText().isEmpty()) {
                switch (showConfirmDialog(NOT_EMPTY_BRANCH_TITLE, NOT_EMPTY_BRANCH_QUESTION, false)) {
                    case YES:
                        break;

                    default:
                        return;
                }
            }

            children.remove(children.size() - 1);
            children.remove(children.size() - 1);
            --numRows;
        }
    }

    /**
     * Opens a new window for the viualization.
     */
    @FXML
    private void showVisualizationAction(ActionEvent event) {
        DpData data = loadInputData();
        if (data.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("No model loaded.");
            alert.show();
            return;
        }

        if (Integer.parseInt(data.getDimension()) > 2) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Visualization works only with 1-D & 2-D models.");
            alert.show();
            return;
        }

        if (!dpDone) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Run the program first.");
            alert.show();
            return;
        }

        try {
            dpsolver.DpSover.newWindow(data, DynamicProgram.getLog(), DynamicProgram.getHierarchy());
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

    /**
     * Switches to full screen and back.
     */
    @FXML
    private void fullScreenAction(ActionEvent event) {
        this.mStage.setFullScreen(!this.mStage.isFullScreen());
    }

    /**
     * Adds two new input fields for a new branch with text.
     *
     * @param branchText branch formula
     * @param criteriaText criteria formula
     */
    private void addFormulaInputRow(String branchText, String criteriaText) {
        List children = formulaInputGridPane.getChildren();

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

    /**
     * Reads all input fields and creates a DpData object.
     */
    private DpData loadInputData() {
        List<String> branches = new ArrayList<>();
        List<String> criterias = new ArrayList<>();
        List children = formulaInputGridPane.getChildren();

        for (int i = 2; i < children.size(); i += 2) {
            branches.add(((TextField) children.get(i)).getText().trim());
            criterias.add(((TextField) children.get(i + 1)).getText().trim());
        }

        return new DpData()
                .setBranches(branches)
                .setCriterias(criterias)
                .setDimension(dimensionTextField.getText().trim())
                .setStartIndexes(startIndexesTextField.getText().trim())
                .setTargetVariable(targetvariableTextField.getText().trim())
                .setVariables(variablesTextArea.getText().trim().split("\n"));
    }

    /**
     * Checks if the inputs in the dimension and start indexes fields are
     * correct.
     *
     * @throws Exception specifies the error in the message
     */
    private void checkDimensionAndStartIndexInputs() throws Exception {
        String dimensionFieldText = dimensionTextField.getText();

        if (!dimensionFieldText.replaceAll("[0-9]", "").trim().isEmpty()) {
            throw new IllegalArgumentException("Illegal characters in Dimension field.");
        }

        String startndexesFieldText = startIndexesTextField.getText();

        if (!startndexesFieldText.replaceAll("[0-9,]", "").trim().isEmpty()) {
            throw new IllegalArgumentException("Illegal characters in Start Indexes field.");
        }

        if (startndexesFieldText.replaceAll("[^,]", "").trim().length() != Integer.parseInt(dimensionFieldText) - 1) {
            throw new IllegalArgumentException("Illegal input.");
        }

        if (startndexesFieldText.split(",").length != Integer.parseInt(dimensionFieldText)) {
            throw new IllegalArgumentException("Start Indexes field must contain "
                    + dimensionFieldText + " indexes.");
        }

    }

    /**
     * Reads all data from a DpData object and fills the input fields with it.
     */
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

    /**
     * Clears all input fields and resets the UI.
     */
    private void resetInputFields() {
        dimensionTextField.clear();
        startIndexesTextField.clear();
        targetvariableTextField.clear();
        variablesTextArea.clear();
        resultTextField.clear();

        List children = formulaInputGridPane.getChildren();
        while (children.size() > 4) {
            children.remove(children.size() - 1);
            children.remove(children.size() - 1);
            --numRows;
        }

        ((TextField) children.get(children.size() - 1)).clear();
        ((TextField) children.get(children.size() - 2)).clear();
    }

    /**
     * Updates the status bar text.
     *
     * @param text status text
     */
    private void updateStatus(String text) {
        statusBarText.setText(text);
    }

    /**
     * Checks if the input data has been changed after the last save.
     *
     * @return true, if the input data has been changed, false otherwise
     */
    private boolean hasUnsavedChanges() {
        return !loadInputData().equals(dpData);
    }

    /**
     * Shows a confirmation type alert dialog with the specified title and
     * content.
     *
     * @param title title of the dialog
     * @param question content of the dialog
     * @param cancelable true if the dialog should contain cancel button
     * @return the user's choice
     */
    private int showConfirmDialog(String title, String question, boolean cancelable) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setContentText(question);
        alert.getButtonTypes().clear();
        if (cancelable) {
            alert.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
        } else {
            alert.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);
        }

        Optional<ButtonType> choise = alert.showAndWait();

        if (choise.isPresent()) {
            if (choise.get() == ButtonType.YES) {
                return YES;
            }
            if (choise.get() == ButtonType.NO) {
                return NO;
            }
            if (choise.get() == ButtonType.CANCEL) {
                return CANCEL_OPERATION;
            }
        }

        return ESC_FROM_DIALOG;
    }

    /**
     * Sets the stage of the visualization.
     *
     * @param visualizationStage the stage
     */
    public void setStage(Stage visualizationStage) {
        this.mStage = visualizationStage;
    }

    public void setStageProperties() {
        mStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                if (hasUnsavedChanges()) {
                    switch (showConfirmDialog(UNSAVED_CHANGES_TITLE, UNSAVED_CHANGES_QUESTION, true)) {
                        case CANCEL_OPERATION:
                            event.consume();
                            break;

                        case YES:
                            saveAction(null);
                            if (statusBarText.getText().equals(CANCEL)) {
                                event.consume();
                                break;
                            }
                            DpSover.closeAll();
                            Platform.exit();
                            break;

                        case NO:
                            DpSover.closeAll();
                            Platform.exit();
                            break;

                        case ESC_FROM_DIALOG:
                            event.consume();
                            break;
                    }
                }
                DpSover.closeAll();
            }
        });
    }

    /**
     * Initializes the keyboard shortcuts for the buttons.
     */
    public void initializeAccelerators() {
        addBranchButton.getScene().getAccelerators()
                .put(new KeyCodeCombination(KeyCode.A, KeyCombination.ALT_DOWN),
                        new Runnable() {
                    @Override
                    public void run() {
                        addFormulaInputRow(EMPTY, EMPTY);
                    }
                });

        removeBranchButton.getScene().getAccelerators()
                .put(new KeyCodeCombination(KeyCode.R, KeyCombination.ALT_DOWN),
                        new Runnable() {
                    @Override
                    public void run() {
                        removeBranchAction(null);
                    }
                });

        showVisualizationButton.getScene().getAccelerators()
                .put(new KeyCodeCombination(KeyCode.V, KeyCombination.ALT_DOWN),
                        new Runnable() {
                    @Override
                    public void run() {
                        showVisualizationAction(null);
                    }
                });

        fullScreenButton.getScene().getAccelerators()
                .put(new KeyCodeCombination(KeyCode.F11),
                        new Runnable() {
                    @Override
                    public void run() {
                        fullScreenAction(null);
                    }
                });
    }
}
