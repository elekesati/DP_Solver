/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dpsolver.model;

import java.util.Arrays;
import java.util.List;
import net.objecthunter.exp4j.operator.Operator;

/**
 * Stores additional operators for exp4j
 *
 * @author Elekes Attila
 */
public class AdditionalOperators {

    private static final int INDEX_EQUAL = 0;
    private static final int INDEX_NOT_EQUAL = 1;
    private static final int INDEX_LESS = 2;
    private static final int INDEX_LESS_OR_EQUAL = 3;
    private static final int INDEX_GREATER = 4;
    private static final int INDEX_GREATER_OR_EQUAL = 5;
    private static final int INDEX_AND = 6;
    private static final int INDEX_OR = 7;

    private static Operator[] mOperators = new Operator[8];

    static {
        mOperators[INDEX_EQUAL] = new Operator("=", 2, true, Operator.PRECEDENCE_ADDITION - 1) {
            @Override
            public double apply(final double... args) {
                if (args[0] == args[1]) {
                    return 1d;
                } else {
                    return 0d;
                }
            }
        };

        mOperators[INDEX_NOT_EQUAL] = new Operator("!=", 2, true, Operator.PRECEDENCE_ADDITION - 1) {
            @Override
            public double apply(final double... args) {
                if (args[0] != args[1]) {
                    return 1d;
                } else {
                    return 0d;
                }
            }
        };

        mOperators[INDEX_LESS] = new Operator("<", 2, true, Operator.PRECEDENCE_ADDITION - 1) {
            @Override
            public double apply(final double... args) {
                if (args[0] < args[1]) {
                    return 1d;
                } else {
                    return 0d;
                }
            }
        };

        mOperators[INDEX_LESS_OR_EQUAL] = new Operator("<=", 2, true, Operator.PRECEDENCE_ADDITION - 1) {
            @Override
            public double apply(final double... args) {
                if (args[0] <= args[1]) {
                    return 1d;
                } else {
                    return 0d;
                }
            }
        };

        mOperators[INDEX_GREATER] = new Operator(">", 2, true, Operator.PRECEDENCE_ADDITION - 1) {
            @Override
            public double apply(final double... args) {
                if (args[0] > args[1]) {
                    return 1d;
                } else {
                    return 0d;
                }
            }
        };

        mOperators[INDEX_GREATER_OR_EQUAL] = new Operator(">=", 2, true, Operator.PRECEDENCE_ADDITION - 1) {
            @Override
            public double apply(final double... args) {
                if (args[0] >= args[1]) {
                    return 1d;
                } else {
                    return 0d;
                }
            }
        };

        mOperators[INDEX_AND] = new Operator("&&", 2, true, Operator.PRECEDENCE_ADDITION - 1) {
            @Override
            public double apply(final double... args) {
                if ((args[0] == 1) && (args[1] == 1)) {
                    return 1d;
                } else {
                    return 0d;
                }
            }
        };

        mOperators[INDEX_OR] = new Operator("||", 2, true, Operator.PRECEDENCE_ADDITION - 1) {
            @Override
            public double apply(final double... args) {
                if ((args[0] == 1) || (args[1] == 1)) {
                    return 1d;
                } else {
                    return 0d;
                }
            }
        };
    }

    /**
     * Returns the operators
     *
     * @return list of operators
     */
    static List<Operator> getOperators() {
        return Arrays.asList(mOperators);
    }
}
