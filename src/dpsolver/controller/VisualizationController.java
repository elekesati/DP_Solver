/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dpsolver.controller;

import dpsolver.model.DpData;
import dpsolver.model.DpLog;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Elekes Attila
 */
public class VisualizationController implements Initializable {
    
    private static final String PLAY = "Playing...";
    private static final String PAUSE = "Paused.";
    private static final String STOP = "Stopped.";

    @FXML
    private Label statusBarText;
    @FXML
    private GridPane space;
    @FXML
    private Button backwardButton;
    @FXML
    private Button playButton;
    @FXML
    private Button pauseButton;
    @FXML
    private Button stopButton;
    @FXML
    private Button forwardButton;
    @FXML
    private Button fullScreenButton;

    private Stage mStage;

    private DpData mDpData;
    private List<DpLog> mLog = new ArrayList<>();
    private Map<String, HashSet<int[]>> mHierarchy = new HashMap<>();

    private Timer mTimer;
    private TimerTask mTimerTask;

    private double cellWidth = 50;
    private double cellHeight = 50;

    private Background grayBackground;
    private Background lightGrayBackground;
    private Background darkGrayBackground;
    private Background whiteBackground;

    private Border solidBorder;
    private Border boldSolidBorder;
    private Border dashedBorder;
    private Border dottedBorder;

    private int actualIndex = -1;

    private boolean playing = false;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        updateStatus(STOP);
        
        grayBackground = new Background(new BackgroundFill(Color.GRAY, CornerRadii.EMPTY, Insets.EMPTY));
        lightGrayBackground = new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY));
        darkGrayBackground = new Background(new BackgroundFill(Color.DARKGRAY, CornerRadii.EMPTY, Insets.EMPTY));
        whiteBackground = new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY));

        solidBorder = new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT));
        boldSolidBorder = new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(3d)));
        dashedBorder = new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.DASHED, CornerRadii.EMPTY, new BorderWidths(2d)));
        dottedBorder = new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.DOTTED, CornerRadii.EMPTY, BorderWidths.DEFAULT));
    }

    /**
     * Starts or resumes the visualization.
     */
    @FXML
    private void playAction(ActionEvent event) {
        startVisualization();
    }

    /**
     * Pauses or resumes the visualization.
     */
    @FXML
    private void pauseAction(ActionEvent event) {
        if (playing) {
            stopVisualization();
        } else {
            startVisualization();
        }
    }

    /**
     * Stops the visualization.
     */
    @FXML
    private void stopAction(ActionEvent event) {
        stopVisualization();
        resetVisualization();
        updateStatus(STOP);
    }

    /**
     * Steps back one step.
     */
    @FXML
    private void backwardAction(ActionEvent event) {
        if (actualIndex <= -1) {
            actualIndex = - 1;
            return;
        }

        stopVisualization();

        StackPane actualCell;
        StackPane previousCell;

        try {
            actualCell = getCell(mLog.get(actualIndex).getIndexes());
        } catch (IndexOutOfBoundsException e) {
            actualCell = null;
        }

        try {
            previousCell = getCell(mLog.get(actualIndex - 1).getIndexes());
        } catch (IndexOutOfBoundsException e) {
            previousCell = null;
        }

        previousStep(actualCell, previousCell);
    }

    /**
     * Steps forward one step.
     */
    @FXML
    private void forwardAction(ActionEvent event) {
        if (actualIndex >= mLog.size()) {
            actualIndex = mLog.size();
            return;
        }

        stopVisualization();

        StackPane actualCell;
        StackPane nextCell;

        try {
            actualCell = getCell(mLog.get(actualIndex).getIndexes());
        } catch (IndexOutOfBoundsException e) {
            actualCell = null;
        }

        try {
            nextCell = getCell(mLog.get(actualIndex + 1).getIndexes());
        } catch (IndexOutOfBoundsException e) {
            nextCell = null;
        }

        nextStep(actualCell, nextCell);
    }

    /**
     * Switches to full screen and back.
     */
    @FXML
    private void fullScreenAction(ActionEvent event) {
        this.mStage.setFullScreen(!this.mStage.isFullScreen());
    }

    /**
     * Calculates the size of the cells.
     * @param bounds number of cells in each direction
     */
    private void setCellSize(int... bounds) {
        double height = space.getHeight() / (bounds[0] + 1);
        double width = space.getWidth() / (bounds[1] + 1);

        if (height < cellHeight) {
            cellHeight = height;
        }

        if (width < cellWidth) {
            cellWidth = width;
        }
    }

    /**
     * Initializes the cells.
     * @param bounds number of cells in each direction
     */
    private void createCells(int... bounds) {
        for (int i = 0; i <= bounds[0] + 1; ++i) {
            for (int j = 0; j <= bounds[1] + 1; ++j) {
                StackPane cell = new StackPane();
                cell.setAlignment(Pos.CENTER);
                cell.setPrefWidth(cellWidth);
                cell.setPrefHeight(cellHeight);
                cell.setAlignment(Pos.CENTER);
                cell.setBorder(dottedBorder);
                cell.setBackground(whiteBackground);
                cell.getChildren().add(new Text(String.valueOf(Double.NaN)));
                space.add(cell, j, i);
            }
        }
    }

    /**
     * Creates the row and column headers.
     * @param bounds number of cells in each direction
     */
    private void setHeaders(int... bounds) {
        List<Node> children = space.getChildren();

        for (Node child : children) {
            if (child instanceof StackPane) {
                StackPane cell = (StackPane) child;

                int rowIndex = GridPane.getRowIndex(cell);
                int columnIndex = GridPane.getColumnIndex(cell);

                if (rowIndex == 0 && columnIndex == 0) {
                    cell.setBackground(grayBackground);
                    cell.setBorder(solidBorder);
                    ((Text) cell.getChildren().get(0)).setText("");
                }

                if (rowIndex == 0 && columnIndex != 0) {
                    cell.setBackground(grayBackground);
                    cell.setBorder(solidBorder);
                    ((Text) cell.getChildren().get(0)).setText(String.valueOf(columnIndex - 1));
                }

                if (rowIndex != 0 && columnIndex == 0) {
                    cell.setBackground(grayBackground);
                    cell.setBorder(solidBorder);
                    ((Text) cell.getChildren().get(0)).setText(String.valueOf(rowIndex - 1));
                }
            }
        }
    }

    /**
     * Returns the cell in a specified index.
     * @param index index of the cell
     * @return the cell
     */
    private StackPane getCell(int... index) {
        List<Node> children = space.getChildren();

        for (Node child : children) {
            if (child instanceof StackPane) {
                StackPane cell = (StackPane) child;
                int[] cellIndex = {GridPane.getRowIndex(cell) - 1, GridPane.getColumnIndex(cell) - 1};
                if (Arrays.equals(cellIndex, index)) {
                    return cell;
                }
            }
        }

        return null;
    }

    /**
     * Marks the specified cell as current cell.
     * @param cell the cell
     */
    private void markAsCurrent(StackPane cell) {
        cell.setBorder(boldSolidBorder);
        if (cell.getBackground().equals(whiteBackground)) {
            cell.setBackground(grayBackground);
        }
    }

    /**
     * Unmarks the specified cell as current cell.
     * @param cell the cell
     */
    private void unmarkAsCurrent(StackPane cell) {
        cell.setBorder(dottedBorder);
        if (cell.getBackground().equals(grayBackground)) {
            cell.setBackground(lightGrayBackground);
        }
    }

    /**
     * Marks the current cell's children.
     */
    private void markChildren() {
        String key = Arrays.toString(mLog.get(actualIndex).getIndexes());
        HashSet<int[]> children = mHierarchy.get(key);

        if (children != null) {
            for (int[] childIndex : children) {
                getCell(childIndex).setBorder(dashedBorder);
            }
        }
    }

    /**
     * Unmarks the current cell's children.
     */
    private void unmarkChildren() {
        String key = Arrays.toString(mLog.get(actualIndex).getIndexes());
        HashSet<int[]> children = mHierarchy.get(key);

        if (children != null) {
            for (int[] childIndex : children) {
                StackPane child = getCell(childIndex);
                child.setBorder(dottedBorder);
            }
        }
    }

    /**
     * Sets the value of the specicied cell and marks it as closed.
     * @param cell the cell
     * @param result the cell's value
     */
    private void markAsClosed(StackPane cell, String result) {
        cell.setBorder(solidBorder);
        ((Text) cell.getChildren().get(0)).setText(result);
        cell.setBackground(darkGrayBackground);
    }

    /**
     * Sets the value of the specicied cell to NaN and unmarks it as closed.
     * @param cell the cell
     */
    private void unmarkAsClosed(StackPane cell) {
        cell.setBorder(dottedBorder);
        ((Text) cell.getChildren().get(0)).setText(String.valueOf(Double.NaN));
        cell.setBackground(lightGrayBackground);
    }

    /**
     * Resets the specicied cell's state to the default
     * @param cell the cell
     */
    private void unmark(StackPane cell) {
        cell.setBackground(whiteBackground);
        cell.setBorder(dottedBorder);
        ((Text) cell.getChildren().get(0)).setText(String.valueOf(Double.NaN));
    }

    /**
     * Steps forward one step. Marks the nextCell as current and unmarks the 
     * actualCell as current.
     * @param actualCell current cell
     * @param nextCell next cell
     */
    private void nextStep(StackPane actualCell, StackPane nextCell) {

        if (actualCell != null) {
            unmarkChildren();
            unmarkAsCurrent(actualCell);

            if (mLog.get(actualIndex).getKey().equals(DpLog.SET)) {
                markAsClosed(actualCell, mLog.get(actualIndex).getDescription());
            }
        }

        ++actualIndex;

        if (nextCell != null) {
            markChildren();
            markAsCurrent(nextCell);
        }

        if (actualIndex == mLog.size()) {
            stopVisualization();
        }
    }

    /**
     * Steps beckward one step. Marks the previousCell as current and unmarks the 
     * actualCell as current.
     * @param actualCell current cell
     * @param previousCell previous cell
     */
    private void previousStep(StackPane actualCell, StackPane previousCell) {

        if (actualCell != null) {
            unmarkAsCurrent(actualCell);
            
            if (mLog.get(actualIndex).getKey().equals(DpLog.SET)) {
                unmarkAsClosed(actualCell);
            }

            if (actualCell.getBackground().equals(lightGrayBackground)) {
                unmark(actualCell);
            }

            unmarkChildren();
        }

        --actualIndex;

        if (previousCell != null) {
            markAsCurrent(previousCell);
            markChildren();
        }
    }

    /**
     * Starts the visualization if it has has been stopped, resumes the 
     * visualization if it hs been paused, or restarts the visualization if it
     * is already started.
     */
    private void startVisualization() {
        if (playing) {
            playing = false;
            mTimer.cancel();
            resetVisualization();
            updateStatus(PAUSE);
        }
        
        updateStatus(PLAY);
        playing = true;
        mTimer = new Timer();
        
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                StackPane actualCell;
                StackPane nextCell;

                try {
                    actualCell = getCell(mLog.get(actualIndex).getIndexes());
                } catch (IndexOutOfBoundsException e) {
                    actualCell = null;
                }

                try {
                    nextCell = getCell(mLog.get(actualIndex + 1).getIndexes());
                } catch (IndexOutOfBoundsException e) {
                    nextCell = null;
                }

                nextStep(actualCell, nextCell);
            }
        };
        
        mTimer.scheduleAtFixedRate(mTimerTask, 0, 500);
    }

    /**
     * Stops the visualization.
     */
    private void stopVisualization() {
        if (playing) {
            playing = false;
            mTimer.cancel();
            updateStatus(PAUSE);
        }
    }
    
    /**
     * Updates the status bar text.
     *
     * @param text status text
     */
    private void updateStatus(String text) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                statusBarText.setText(text);
            }
        });
    }

    /**
     * Initializes the visualization: sets up the cell sizes, creates them and 
     * sets up the headers.
     */
    public void prepareVisualization() {
        int[] bounds = mDpData.getStartIndexesArray();
        setCellSize(bounds);
        createCells(bounds);
        setHeaders(bounds);
    }

    /**
     * Loades the data for he visualization.
     * @param dpData DpData of the model
     * @param log sequence of the steps and log of the errors
     * @param hierarchy parent-child relations between the nodes
     */
    public void initializeData(DpData dpData, List<DpLog> log, Map<String, HashSet<int[]>> hierarchy) {
        this.mDpData = dpData;
        this.mLog = log;
        this.mHierarchy = hierarchy;
    }

    /**
     * Sets the stage of the visualization.
     * @param visualizationStage the stage
     */
    public void setStage(Stage visualizationStage) {
        this.mStage = visualizationStage;
    }

    /**
     * Resets the visualization to the default state.
     */
    public void resetVisualization() {
        actualIndex = -1;
        List<Node> children = space.getChildren();

        for (Node child : children) {
            if (child instanceof StackPane) {
                StackPane cell = (StackPane) child;
                int[] cellIndex = {GridPane.getRowIndex(cell), GridPane.getColumnIndex(cell)};
                if (cellIndex[0] != 0 && cellIndex[1] != 0) {
                    unmark(cell);
                }
            }
        }
    }

    /**
     * Initializes the keyboard shortcuts for the buttons.
     */
    public void initializeAccelerators() {
        backwardButton.getScene().getAccelerators()
                .put(new KeyCodeCombination(KeyCode.LEFT),
                        new Runnable() {
                    @Override
                    public void run() {
                        backwardAction(null);
                    }
                });

        playButton.getScene().getAccelerators()
                .put(new KeyCodeCombination(KeyCode.ENTER),
                        new Runnable() {
                    @Override
                    public void run() {
                        playAction(null);
                    }
                });

        pauseButton.getScene().getAccelerators()
                .put(new KeyCodeCombination(KeyCode.SPACE),
                        new Runnable() {
                    @Override
                    public void run() {
                        pauseAction(null);
                    }
                });

        forwardButton.getScene().getAccelerators()
                .put(new KeyCodeCombination(KeyCode.RIGHT),
                        new Runnable() {
                    @Override
                    public void run() {
                        forwardAction(null);
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
