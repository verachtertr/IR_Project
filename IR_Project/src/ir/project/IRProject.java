/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.project;

import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;


import org.json.simple.*;
import org.json.simple.parser.JSONParser;


/**
 *
 * @author elise
 */
public class IRProject {


    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        try {
            //(some of this inspired by: http://www.lucenetutorial.com/lucene-in-5-minutes.html)
            
            StandardAnalyzer analyzer = new StandardAnalyzer();
            Directory index = new RAMDirectory();
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            
            IndexWriter w = new IndexWriter(index, config);
            
            indexFromJSON(w, "./resources/books_test.json");
            
            w.close();
            
            // Calculate tf/idf weights
            IndexReader reader = DirectoryReader.open(index);
            
            for (int i = 0; i < reader.maxDoc(); i++) {
                Terms vector = reader.getTermVector(i, "text");
            
                TermsEnum it = vector.iterator();
                
                while (it.next() != null) {
                    Term t = new Term("text", it.term().utf8ToString());
                
                    Long tf = it.totalTermFreq();
                    float idf = (float)1 / (float)reader.totalTermFreq(t);
                
                    float tfIdfWeight = tf * idf;
                    
                    System.out.println(it.term().utf8ToString());
                    System.out.println(tfIdfWeight);
                }
            }
     
        } catch (IOException ex) {
            Logger.getLogger(IRProject.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    private static void indexFromJSON(IndexWriter w, String filename) {
        
        JSONParser parser = new JSONParser();
        
        try {
            
            Object obj = parser.parse(new FileReader(filename));
            
            JSONArray array = (JSONArray)obj;
            
            Iterator<JSONObject> iterator = array.iterator();
            
            while(iterator.hasNext()) 
            {
                JSONObject doc = iterator.next();
                String title = (String)doc.get("title");
                String isbn = (String)doc.get("isbn");
                String author = (String)doc.get("author");
                String text = (String)doc.get("text");
                
                // add document to index
                addDoc(w, title, isbn, author, text);
            }
            
        } catch (IOException ex) {
            Logger.getLogger(IRProject.class.getName()).log(Level.SEVERE, null, ex);
        } catch (org.json.simple.parser.ParseException ex) {
            Logger.getLogger(IRProject.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static void addDoc(IndexWriter w, String title, String isbn, String author, String text) throws IOException {
        
        Document doc = new Document();
        doc.add(new TextField("title", title, Field.Store.YES));
        doc.add(new StringField("isbn", isbn, Field.Store.YES));
        doc.add(new TextField("author", author, Field.Store.YES));
        
        FieldType type = new FieldType();
        type.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
        type.setStored(true);
        type.setStoreTermVectors(true);
        type.setTokenized(true);
        type.setStoreTermVectorOffsets(true);
        
        Field field = new Field("text", text, type);
        
        doc.add(field);
        
        w.addDocument(doc);
    }
   
}
