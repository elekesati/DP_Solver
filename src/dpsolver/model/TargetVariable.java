/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dpsolver.model;

import dpsolver.helpers.DimensionConverter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents the target variable of the dynamic program. Extends Variable
 * class.
 *
 * @author Elekes Attila
 */
public class TargetVariable extends Variable {

    public static final byte WHITE = 0;
    public static final byte GRAY = 1;
    public static final byte BLACK = 2;
    private final List<Byte> mStatus;

    public TargetVariable(String key, int... bounds) {
        super(key, bounds);
        this.mStatus = new ArrayList<>(Collections.nCopies(mValues.size(), WHITE));
    }

    public Byte getStatus(int... indexes) {
        int index = DimensionConverter.multiDimensonalToLinear(mBounds, indexes);
        return this.mStatus.get(index);
    }

    public void updateStatus(Byte status, int... indexes) {
        int index = DimensionConverter.multiDimensonalToLinear(mBounds, indexes);
        this.mStatus.set(index, status);
    }
    
    public void clearStatus(){
        for (int i=0; i<mStatus.size(); ++i){
            mStatus.set(i, WHITE);
        }
    }
    
    public void clearValues(){
        for (int i=0; i<mValues.size(); ++i){
            mValues.set(i, Double.NaN);
        }
    }
    
    public void clear(){
        clearStatus();
        clearValues();
    }
}
