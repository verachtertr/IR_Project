/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.project;

import Jama.Matrix;
import Jama.SingularValueDecomposition;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
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
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.TFIDFSimilarity;

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

            // Index some books
            Indexer indexer = new Indexer();
            indexer.index("./resources/books_RE.json");
            Directory index = indexer.getIndex();
            
            // Calculate tf/idf weights
            IndexReader reader = DirectoryReader.open(index);
            // Create the matrix for storing the items.
            //double[][] array;
            //array = new double[(int)reader.getTermVector(0, "text").size()][reader.maxDoc()];
            //Matrix scoreMatrix = new Matrix(array);
            Map<String, Integer>termMap = new HashMap<>();  // Map used to identifie position in matrix for 
            Integer count = 0;
            
            // setup the termMap.
            for (int i = 0; i < reader.maxDoc(); i++) {
                Terms vector = reader.getTermVector(i, "text");
                if (vector == null) {
                    System.out.println("Vector is null");
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

            // construct the term matrix.
            double[][] termMatrix;
            termMatrix = new double[count][reader.maxDoc()];
            for (int i = 0; i < reader.maxDoc(); i++) {
                Terms vector = reader.getTermVector(i, "text");
                if (vector == null) {
                    System.out.println("Vector is null");
                    continue;
                }
                TermsEnum it = vector.iterator();
                
                while (it.next() != null) {
                    Term t = new Term("text", it.term().utf8ToString());
                
                    Long tf = it.totalTermFreq();
                    float idf = (float)1 / (float)reader.totalTermFreq(t);
                
                    float tfIdfWeight = tf * idf;
                    
                    //System.out.println(it.term().utf8ToString());
                    //System.out.println(tfIdfWeight);
                    termMatrix[termMap.get(it.term().utf8ToString())][i] = tfIdfWeight;
                }
            }            
            
            // Lower the dimensionality, by using SVD
            Matrix frequencyMatrix = new Matrix(termMatrix);
            SingularValueDecomposition svdComput = frequencyMatrix.svd();
            
            Matrix U = svdComput.getU();
            Matrix S = svdComput.getS();
            Matrix V = svdComput.getV();
            
            // TODO Pick a good number for max dimensions.
            int maxDimension = 8;
            Matrix reduceDimensionality = new Matrix(count, reader.maxDoc());
            for (int i=0; i<maxDimension;i++) {
                reduceDimensionality.set(i, i, 1);
            }
            
            
            Matrix newFrequencyMatrix = U.times(S).times(V.transpose());
            
            // TODO use new matrix to get cosine similarity.
            //TFIDFSimilarity sim = new TFIDFSimilarity();
            Analyzer analyzer = new EnglishAnalyzer();  // Use EnglishAnalyzer, so that Lucene auto stems the tokens.
            
            String querystr = "Desperate Adolf Hitler orders the impossible: kidnap or kill Winston Churchill. A disgraced war hero receives the suicidal mission for a commando squad. In a quiet seaside village, a beautiful widow and a cultured IRA assassin set the groundwork for the ultimate act of treachery. On 6 November 1943, Berlin gets the coded message \\\"The Eagle has landed\\\".";
            Query q = new QueryParser("text", analyzer).parse(querystr);
     
            int hitsPerPage = 10;
            IndexSearcher searcher = new IndexSearcher(reader);
            TopDocs docs = searcher.search(q, hitsPerPage);
            ScoreDoc[] hits = docs.scoreDocs;
            
            System.out.println("Found " + hits.length + " hits.");
            for(int i=0;i<hits.length;++i) {
                int docId = hits[i].doc;
                Document d = searcher.doc(docId);
                System.out.println((i + 1) + ". " + d.get("isbn") + "\t" + d.get("title"));
            }
        } catch (IOException | ParseException ex) {
            Logger.getLogger(IRProject.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
