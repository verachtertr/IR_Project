/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.project;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
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
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import org.apache.lucene.codecs.TermVectorsFormat;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.Terms;


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
            // inspired by: http://www.lucenetutorial.com/lucene-in-5-minutes.html
            
            StandardAnalyzer analyzer = new StandardAnalyzer();
            Directory index = new RAMDirectory();
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            
            IndexWriter w = new IndexWriter(index, config);
            
            indexFromJSON(w, "/home/elise/workspace/IR_Project/IR_Project/resources/books_test.json");
            
            w.close();
            
            
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
        doc.add(new TextField("author", title, Field.Store.YES));
        doc.add(new TextField("text", title, Field.Store.YES));
        
        w.addDocument(doc);
    }
   
}
