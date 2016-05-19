/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.project.helper;

import ir.project.TFIDFBookVector;
import ir.project.TFIDFMatrix;
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
    
    private SparseMatrix Uk;
    private SparseMatrix Sk;
    private SparseMatrix Vk;
    private long m_dimension = 8;
    private TFIDFMatrix termMatrix;
    
    public SVDecomposition(TFIDFMatrix tfMatrix) {
        this.termMatrix = tfMatrix;
        Matrix newMatrix = SparseMatrix.Factory.zeros(tfMatrix.getNumTerms(), tfMatrix.getNumDocs());
        System.out.println("New Matrix dimensions");
        System.out.println(newMatrix.getRowCount());
        System.out.println(newMatrix.getColumnCount());
        for (int i=0; i < newMatrix.getRowCount();i++) {
            for (int j=0; j< newMatrix.getColumnCount();j++) {
                newMatrix.setAsDouble(tfMatrix.getTFIDFVector(j).getVector().getEntry(i), i,j);
            }
        }
        SVDecomposition(newMatrix);
    }
    
    public final void SVDecomposition(Matrix sparseMatrix) {
        
        Matrix[] decomposition = sparseMatrix.svd();    
                
        // Lower the dimension of the matrix:
        // Uk is the matrix containing the 1st k columns of U
        // Vk is the matrix conatining the 1st k rows of V
        // Sk is the kxk containing the 1st k singular values.
        
        long dimension = min(m_dimension, decomposition[1].getRowCount());
        
        Uk = SparseMatrix.Factory.zeros(decomposition[0].getRowCount(), dimension);
        Sk = SparseMatrix.Factory.zeros(dimension, dimension);
        Vk = SparseMatrix.Factory.zeros(decomposition[2].getRowCount(), dimension);
        
        System.out.println("Factorisation sizes");
        System.out.println(Uk.getRowCount());
        System.out.println(Vk.getRowCount());
        
        for (int i = 0; i< dimension; i++ ) {
            // set Uk and Vk values
            for (int j=0; j < Uk.getRowCount();j++) {
                Uk.setAsDouble(decomposition[0].getAsDouble(j,i), j,i);
            }
            for (int j=0; j < Vk.getRowCount(); j++) {
                Vk.setAsDouble(decomposition[2].getAsDouble(j,i), j,i);
            }
            // Set the Sk Value
            Sk.setAsDouble(decomposition[1].getAsDouble(i,i),i,i);
        }    
        
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
        // Reconstruct the TFIDF Vector
        RealVector r = new ArrayRealVector(query.getVector().getDimension());     // TODO Make sparse (Might not be neccessary if the vector has barely any zeroes
        for (int i=0; i<new_q.getRowCount(); i++) {
                r.setEntry(i, new_q.getAsDouble(i,0));
        }
        query.setVector(r);
        return query;
    }
    
    public TFIDFMatrix getTfMatrix() {
        
        Matrix lowerComplexityMatrix = Uk.mtimes(Sk.mtimes(Vk.transpose()));

        // Create a TFIDF Matrix from the available Matrix.
        for (int i=0; i < lowerComplexityMatrix.getRowCount();i++) {
            for (int j=0; j< lowerComplexityMatrix.getColumnCount();j++) {
                termMatrix.getTFIDFVector(j).getVector().setEntry(i, lowerComplexityMatrix.getAsDouble(i,j));
            }
        }
        return termMatrix;
    }
    
}
