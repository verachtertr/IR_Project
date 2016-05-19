/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.project.helper;

import ir.project.TFIDFBookVector;
import static java.lang.Long.min;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.ujmp.core.Matrix;
import org.ujmp.core.SparseMatrix;

/**
 *
 * @author robin
 */
public class SVDecomposition {
    
    private Matrix matrix;
    private Matrix lowerComplexityMatrix;
    private Matrix Uk;
    private Matrix Sk;
    private Matrix Vk;
    private long m_dimension = 2;
    
    public SVDecomposition(SparseMatrix sparseMatrix) {
        this.matrix = sparseMatrix;
        
        Matrix[] decomposition = sparseMatrix.svd();
        
        System.out.println("SAME MATRIX?");
        System.out.println(decomposition[0].mtimes(decomposition[1].mtimes(decomposition[2].transpose())));
        
        // Lower the dimension of the matrix:
        // Uk is the matrix containing the 1st k columns of U
        // Vk is the matrix conatining the 1st k rows of V
        // Sk is the kxk containing the 1st k singular values.
        
        long dimension = min(m_dimension, decomposition[1].getDimensionCount()+1);
        
        Uk = SparseMatrix.Factory.zeros(decomposition[0].getRowCount(), dimension);
        Sk = SparseMatrix.Factory.zeros(dimension, dimension);
        Vk = SparseMatrix.Factory.zeros(decomposition[2].getRowCount(), dimension);
        
        for (int i = 0; i< min(dimension, decomposition[1].getDimensionCount()+1); i++ ) {
            // set Uk and Vk values
            for (int j=0; j < Uk.getRowCount();j++) {
                Uk.setAsDouble(decomposition[0].getAsDouble(j,i), j,i);
                Vk.setAsDouble(decomposition[2].getAsDouble(j,i), j,i);
            }
            // Set the Sk Value
            Sk.setAsDouble(decomposition[1].getAsDouble(i,i),i,i);
        }    
        
        System.out.println("DECOMPOSITION");
        System.out.println(decomposition[0]);
        System.out.println(Uk);
        System.out.println(Sk);
        System.out.println(Vk);
        
        this.lowerComplexityMatrix = Uk.mtimes(Sk.mtimes(Vk.transpose()));
        System.out.println(lowerComplexityMatrix);
        // Lower the dimension of the matrix. TODO find good value.
    }
    
    public TFIDFBookVector changeQuery(TFIDFBookVector query) {
        // Perform the necessary opperations to change the vector to the LSA space
        Matrix q = SparseMatrix.Factory.zeros(query.getVector().getDimension(),1);
        for (int i=0; i<query.getVector().getDimension(); i++) {
            q.setAsDouble(query.getVector().getEntry(i), i,0);
        }
        // Now change the query:
        // new q = S.inv * U.transpose * q
        Matrix new_q = this.Sk.inv().mtimes(Uk.transpose()).mtimes(q);
        System.out.println(new_q);
        // Reconstruct the TFIDF Vector
        RealVector r = new ArrayRealVector(query.getVector().getDimension());     // TODO Make sparse (Might not be neccessary if the vector has barely any zeroes
        for (int i=0; i<new_q.getRowCount(); i++) {
                r.setEntry(i, new_q.getAsDouble(i,0));
        }
        query.setVector(r);
        return query;
    }
    
}
