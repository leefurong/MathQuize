package com.mathquize.app;

import java.io.Serializable;

/**
 * Created by lifurong on 15/6/22.
 */
public class Quize implements Serializable{
    private final int total;
    private final int left;
    private final int right;
    private final boolean isMinus;

    public Quize(int left, int right, boolean isMinus){
        this.left = left;
        this.right = right;
        this.isMinus = isMinus;
        this.total = left + right;
    }

    public int getAnswer(){
        if (isMinus){
            return right;
        } else{
            return left + right;
        }
    }
    
    @Override
    public String toString(){
        return isMinus? "" + total +" - " + left: ""+left+"+"+right;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Quize quize = (Quize) o;

        if (isMinus != quize.isMinus) return false;
        if (left != quize.left) return false;
        if (right != quize.right) return false;
        if (total != quize.total) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = total;
        result = 31 * result + left;
        result = 31 * result + right;
        result = 31 * result + (isMinus ? 1 : 0);
        return result;
    }
}
