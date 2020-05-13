/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dpsolver.helpers;

import dpsolver.model.DpData;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Saves and reloads the data of the dynamic program
 *
 * @author Elekes Attila
 */
public class FileHandler {

    /**
     * Constructor
     */
    private FileHandler() {
    }

    /**
     * Reads the program data from file
     *
     * @param file file containing data of dynamic program
     * @return the input data of the program
     * @throws FileNotFoundException
     */
    public static DpData read(File file) throws FileNotFoundException {
        Scanner scanner = new Scanner(file);

        List<String> branches = new ArrayList<>();
        List<String> criterias = new ArrayList<>();
        String[] variables;
        String targetVariable;
        String dimension;
        String startIndexes;

        int numBranches = scanner.nextInt();
        int numVariables = scanner.nextInt();

        scanner.nextLine();

        for (int i = 0; i < numBranches; ++i) {
            branches.add(scanner.nextLine());
        }

        for (int i = 0; i < numBranches; ++i) {
            criterias.add(scanner.nextLine());
        }

        variables = new String[numVariables];

        for (int i = 0; i < numVariables; ++i) {
            variables[i] = scanner.nextLine();
        }

        targetVariable = scanner.nextLine();
        dimension = scanner.nextLine();
        startIndexes = scanner.nextLine();

        scanner.close();

        return new DpData().setBranches(branches)
                .setCriterias(criterias)
                .setDimension(dimension)
                .setStartIndexes(startIndexes)
                .setTargetVariable(targetVariable)
                .setVariables(variables);
    }

    /**
     * Saves the program data
     *
     * @param file file to save data of dynamic program
     * @param data the input data of the program
     * @throws FileNotFoundException
     */
    public static void write(File file, DpData data) throws FileNotFoundException {
        PrintWriter printWriter = new PrintWriter(file);
        printWriter.print(data.toString());
        printWriter.close();
    }
}
