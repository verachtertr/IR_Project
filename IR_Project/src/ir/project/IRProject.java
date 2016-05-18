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

        // Index some books
        Indexer indexer = new Indexer();
        indexer.index("./resources/books_reviews.json");
        Directory index = indexer.getIndex();
        
        System.out.println("INDEXED");
            
        // Construct matrix with tf-idf vectors of our books.
        TFIDFMatrix termMatrix = new TFIDFMatrix(index);
        
        for(int i = 0; i < termMatrix.getNumDocs(); i++) {
            termMatrix.testCosineSimilarity(2, i);
        }
            
            // Lower the dimensionality, by using SVD
            /*Matrix frequencyMatrix = new Matrix(termMatrix);
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
            
            TODO Do not forget to also change the query*/
            // new q = S.inv * U.transpose * q
            
            //Matrix newFrequencyMatrix = U.times(S).times(V.transpose());
            
            // TODO use new matrix to get cosine similarity.
            //TFIDFSimilarity sim = new TFIDFSimilarity();
            /*Analyzer analyzer = new EnglishAnalyzer();  // Use EnglishAnalyzer, so that Lucene auto stems the tokens.
            
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
            }*/
            /*} catch (IOException | ParseException ex) {
            Logger.getLogger(IRProject.class.getName()).log(Level.SEVERE, null, ex);
            }*/

    }

}
