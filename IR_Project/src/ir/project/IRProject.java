/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.project;

import ir.project.helper.SVDecomposition;
import org.apache.lucene.store.Directory;

import org.ujmp.core.SparseMatrix;
import org.ujmp.core.Matrix;




/**
 *
 * @author elise
 */
public class IRProject {


    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        /*// Index some books
        Indexer indexer = new Indexer();
        indexer.index("./resources/books_reviews.json");
        Directory index = indexer.getIndex();
        
        System.out.println("INDEXED");
            
        // Construct matrix with tf-idf vectors of our books.
        TFIDFMatrix termMatrix = new TFIDFMatrix(index);
        
        for(int i = 0; i < termMatrix.getNumDocs(); i++) {
            termMatrix.testCosineSimilarity(2, i);
        }*/
        
        SparseMatrix sparse = SparseMatrix.Factory.zeros(4,5);
        sparse.setAsDouble(1,0,0);
        sparse.setAsDouble(2,0,4);
        sparse.setAsDouble(3,1,2);
        sparse.setAsDouble(2,3,1);
        
        SVDecomposition decomposition = new SVDecomposition(sparse);
        
        //System.out.println(sparse.toString());
        Matrix[] SVDMatrixes = sparse.svd();        // Hopefully SVDMatrixes[0] = S
        
        System.out.println("Matrix 1:");
        System.out.println(SVDMatrixes[0].toString());
        
        System.out.println("Matrix 2:");
        System.out.println(SVDMatrixes[1].toString());
        
        System.out.println("Matrix 3:");
        System.out.println(SVDMatrixes[2].toString());
        
        TFIDFBookVector query = new TFIDFBookVector(4, "test", "2", "Me");
        query.editValue(0, 1);
        query.editValue(1, 1);
        query.editValue(2, 1);
        query.editValue(3, 0);
                
        TFIDFBookVector new_query = decomposition.changeQuery(query);
                                    
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
