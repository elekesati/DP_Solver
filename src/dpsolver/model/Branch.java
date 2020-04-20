/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dpsolver.model;

import javafx.util.Pair;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

/**
 * Stores a branch of the target formula
 *
 * @author Elekes Attila
 */
public class Branch {

    private final Pair<String, String> mStringRepresentation;
    private final Pair<Expression, Expression> mExpressionRepresentation;

    /**
     * Constructs the branch from two input strings
     *
     * @param branch executable part of the branch
     * @param criteria criteria to specify the branch`s range of interpretation
     */
    public Branch(String branch, String criteria) {
        this.mStringRepresentation = new Pair<>(branch, criteria);
        this.mExpressionRepresentation = buildExpressions();
    }

    /**
     * Constructs the branch from a pair of strings
     *
     * @param branchString contains the executable part of the branch and the
     * criteria to specify it`s range of interpretation
     */
    public Branch(Pair<String, String> branchString) {
        this.mStringRepresentation = branchString;
        this.mExpressionRepresentation = buildExpressions();
    }

    /**
     * Builds the expressions from the given strings
     *
     * @return contains a pair of expressions built from the given strings which
     * will be used by exp4j
     */
    private Pair<Expression, Expression> buildExpressions() {
        Expression branchExpression
                = new ExpressionBuilder(mStringRepresentation.getKey())
                        .functions(AdditionalFunctions.getFunctions())
                        .operator(AdditionalOperators.getOperators())
                        .variables(Variables.getVariableNames())
                        .build()
                        .setVariables(Variables.getVariableMap());

        Expression criteriaExpression
                = new ExpressionBuilder(mStringRepresentation.getValue())
                        .functions(AdditionalFunctions.getFunctions())
                        .operator(AdditionalOperators.getOperators())
                        .variables(Variables.getVariableNames())
                        .build()
                        .setVariables(Variables.getVariableMap());

        return new Pair<>(branchExpression, criteriaExpression);
    }

    /**
     * Returns the string representation of the branch
     *
     * @return the branch in string format
     */
    public Pair<String, String> getString() {
        return mStringRepresentation;
    }

    /**
     * Returns the expression representation of the branch
     *
     * @return the branch in expression format
     */
    public Pair<Expression, Expression> getExpression() {
        return mExpressionRepresentation;
    }
}
