/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.project;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.OpenMapRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SparseRealVector;

/**
 *
 * @author elise
 */
public class TFIDFBookVector {

    private RealVector vector;
    
    private final String title;
    private final String isbn;
    private final String author;
    
    public TFIDFBookVector(int numTerms, String title, String isbn, String author) 
    {
        // Uses a sparse vector
        this.vector = new OpenMapRealVector(numTerms);
        this.title = title;
        this.isbn = isbn;
        this.author = author;
    }
    
    public String getTitle() 
    {
        return this.title;
    }

    public String getISBN()
    {
        return this.isbn;
    }
    
    public String getAuthor()
    {
        return this.author;
    }
    
    public RealVector getVector() 
    {
        return this.vector;
    }
    
    public void editValue(int index, double value) 
    {
        vector.addToEntry(index, value);
    }
    
    public double cosineSimilarity(TFIDFBookVector other) {
        RealVector otherVector = other.getVector();
        
        // get dot product
        double dotProduct = this.vector.dotProduct(otherVector);
        
        // get Euclidian norms
        double norm1 = this.vector.getNorm();
        double norm2 = otherVector.getNorm();
        
        // calculate cosine similarity
        double sim = dotProduct / (norm1 * norm2);
        
        return sim;
      
    }
    
}
