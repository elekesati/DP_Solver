/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dpsolver.helpers;

import dpsolver.model.DynamicProgram;
import dpsolver.model.Variables;
import java.util.ArrayList;
import java.util.List;
import net.objecthunter.exp4j.function.Function;

/**
 * Stores additional functions for exp4j
 *
 * @author Elekes Attila
 */
public class AdditionalFunctions {

    private static List<Function> mFunctions = new ArrayList<>();

    private AdditionalFunctions() {
    }

    /**
     * Initializes with some basic functions, is called only once
     */
    public static void initialize() {
        mFunctions.add(new Function("max", 2) {
            @Override
            public double apply(double... args) {
                if (args[0] > args[1]) {
                    return args[0];
                }
                return args[1];
            }
        });

        mFunctions.add(new Function("min", 2) {
            @Override
            public double apply(double... args) {
                if (args[0] < args[1]) {
                    return args[0];
                }
                return args[1];
            }
        });
        
        mFunctions.add(new Function("avg", 2) {
            @Override
            public double apply(double... args) {
                return (args[0] + args[1]) / 2d;
            }
        });
    }

    /**
     * Adds a new function that will be used by exp4j
     *
     * @param name name of the function
     * @param numArgs number of arguments
     * @param isTarget flag for target variable
     */
    public static void addFunction(String name, int numArgs, boolean isTarget) {
        if (isTarget) {
            mFunctions.add(new Function(name, numArgs) {
                @Override
                public double apply(double... args) {
                    int[] intArgs = new int[numArgs];
                    for (int i = 0; i < numArgs; ++i) {
                        intArgs[i] = (int) args[i];
                    }
                    return DynamicProgram.solve(intArgs);
                }
            });
        } else {
            mFunctions.add(new Function(name, numArgs) {
                @Override
                public double apply(double... args) {
                    int[] intArgs = new int[numArgs];
                    for (int i = 0; i < numArgs; ++i) {
                        intArgs[i] = (int) args[i];
                    }
                    return Variables.getVector(name).getValue(intArgs);
                }
            });
        }
    }

    /**
     * Returns the functions
     *
     * @return list of functions
     */
    public static List<Function> getFunctions() {
        return mFunctions;
    }
}
