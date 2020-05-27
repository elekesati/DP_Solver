/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dpsolver.model;

/**
 *
 * @author Elekes Attila
 */
public class DpLog {

    public static final String GET = "get";
    public static final String SET = "set";
    public static final String ERROR = "error";

    private final String mKey;
    private final String mDescription;
    private final int[] mIndexes;
    private final int[] mParent;

    public DpLog(String key, String description, int[] indexes, int[] parent) {
        this.mKey = key;
        this.mDescription = description;
        this.mIndexes = indexes;
        this.mParent = parent;
    }

    public String getKey() {
        return mKey;
    }

    public String getDescription() {
        return mDescription;
    }

    public int[] getIndexes() {
        return mIndexes;
    }

    public int[] getParent() {
        return mParent;
    }

    @Override
    public String toString() {
        String indexString = "{";

        for (int i = 0; i < mIndexes.length - 1; ++i) {
            indexString += mIndexes[i] + ", ";
        }

        indexString += mIndexes[mIndexes.length - 1] + "}";

        return "Index = " + indexString + ": " + mKey + " - " + mDescription;
    }

}
