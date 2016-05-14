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
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Class that creates an index from JSON file.
 * @author elise
 */
public class Indexer {

    private Directory index;
    
    /**
     * Default constructor.
     */
    public Indexer() 
    {
        this.index = new RAMDirectory();
    }
    
    /**
     * 
     * @return 
     */
    public Directory getIndex() {
        return this.index;
    }
    
    /**
     * 
     * @param filename 
     */
    public void index(String filename) {
        try {
            Analyzer analyzer = new EnglishAnalyzer();  // Use EnglishAnalyzer, so that Lucene auto stems the tokens.
            
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            IndexWriter w = new IndexWriter(this.index, config);
            
            indexFromJSON(w, filename);  // Start the indexing process, the results will be in the index variable.
            
            w.close();
            
        } catch (IOException ex) {
            Logger.getLogger(Indexer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * 
     * @param w
     * @param filename 
     */
    private void indexFromJSON(IndexWriter w, String filename) 
    {
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
    
    /**
     * 
     * @param w
     * @param title
     * @param isbn
     * @param author
     * @param text
     * @throws IOException 
     */
    private void addDoc(IndexWriter w, String title, String isbn, String author, String text) throws IOException 
    {   
        //(some of this inspired by: http://www.lucenetutorial.com/lucene-in-5-minutes.html)
        
        Document doc = new Document();
        doc.add(new TextField("title", title, Field.Store.YES));        // The title field, we do want to tokenize this, we never know why.
        doc.add(new StringField("isbn", isbn, Field.Store.YES));        // String field because we don't need to index it.
        doc.add(new StringField("author", author, Field.Store.YES));    // Same as above
        
        FieldType type = new FieldType();                               // Field for full-text or review: we want to store term vectors.
        
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
