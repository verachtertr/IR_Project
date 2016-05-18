/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.project;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.store.Directory;

/**
 *
 * @author elise
 */
public class TFIDFMatrix {
    private HashMap<String, Integer> termMap;
    private TFIDFBookVector[] matrix;
    
    private int numTerms;
    private int numDocs;
    
    private Directory index;
    
    public TFIDFMatrix(Directory index) 
    {
        this.index = index;
        createTermMap();
        System.out.println("Created Term Map");
        createMatrix();
    }
    
    private void createTermMap() 
    {
        try {
            IndexReader reader = DirectoryReader.open(this.index);
            
            this.termMap = new HashMap<>();  // Map used to identify position in matrix for 
            this.numDocs = reader.maxDoc();
            int count = 0;
            
            // Setup the termMap
            for (int i = 0; i < numDocs; i++) {
                
                Terms vector = reader.getTermVector(i, "text");
                if (vector == null) {
                    System.err.println("Vector is null!");
                    continue;
                }
                
                TermsEnum it = vector.iterator();
                while (it.next() != null) {
                    Term t = new Term("text", it.term().utf8ToString());
                    
                    if (!termMap.containsKey(it.term().utf8ToString())) {
                        termMap.put(it.term().utf8ToString(), count);
                        count += 1;
                    }
                }
            }
            
            this.numTerms = count;
            reader.close();
            
        } catch (IOException ex) {
            Logger.getLogger(TFIDFMatrix.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void createMatrix() {
        try {
            this.matrix = new TFIDFBookVector[numDocs];

            IndexReader reader = DirectoryReader.open(this.index);
            
            for (int i = 0; i < numDocs; i++) {
                Terms vector = reader.getTermVector(i, "text");
                
                // get title
                IndexableField titleField = reader.document(i).getField("title");
                String title = titleField.stringValue();
                
                // get isbn
                IndexableField isbnField = reader.document(i).getField("isbn");
                String isbn = isbnField.stringValue();
                
                // get author
                IndexableField authorField = reader.document(i).getField("author");
                String author = authorField.stringValue();
                
                
                this.matrix[i] = new TFIDFBookVector(numTerms, title, isbn, author);
                
                if (vector == null) {
                    System.err.println("Vector is null");
                    continue;
                }
                
                TermsEnum it = vector.iterator();
                
                while(it.next() != null) {
                    Term t = new Term("text", it.term().utf8ToString());
                    
                    // TotalTermFreq returns frequency of term in document.
                    Long tf = it.totalTermFreq();
                    double idf = (double)1 / (double)reader.totalTermFreq(t);
                    
                    double tfIdfWeight = tf * idf;
                    
                    // put TF-IDF weight in matrix
                    int termIndex = this.termMap.get(it.term().utf8ToString());                    
                    this.matrix[i].editValue(termIndex, tfIdfWeight);
                }
            }
            
            reader.close();

        } catch (IOException ex) {
            Logger.getLogger(TFIDFMatrix.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public int getNumDocs()
    {
        return this.numDocs;
    }
    
    public int getNumTerms()
    {
        return this.numTerms;
    }
    
    public TFIDFBookVector getTFIDFVector(int i) {
        return matrix[i];
    }
    
    // mainly for testing, we still need a way to create/look up tf-idf vector of books like by user.
    public void testCosineSimilarity(int i1, int i2) 
    {
        TFIDFBookVector vec1 = matrix[i1];
        TFIDFBookVector vec2 = matrix[i2];
        
        double similarity = vec1.cosineSimilairty(vec2);
        
        System.out.println(vec1.getTitle());
        System.out.println(vec2.getTitle());
        System.out.println("Cosine similarity: " + similarity);
    }
}
