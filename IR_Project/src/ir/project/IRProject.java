/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.project;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import ir.project.helper.SVDecomposition;

import org.apache.lucene.store.Directory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

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

        // Index some books
        Indexer indexer = new Indexer();
        indexer.index("./resources/books_massive.json");
        Directory index = indexer.getIndex();

        TFIDFMatrix termMatrix = new TFIDFMatrix(index);

        System.out.println("INDEXED");

        // read in user data from JSON
        JSONParser parser = new JSONParser();

        try {
            Object obj = parser.parse(new FileReader("./resources/users_test.json"));
            JSONArray array = (JSONArray) obj;
            Iterator<JSONObject> iterator = array.iterator();

            JSONObject user1 = iterator.next();
            String name = (String) user1.get("name");
            System.out.println("Hi " + name);
            JSONArray ratings = (JSONArray) user1.get("ratings");
            
            List<String> userProfile = new ArrayList();
            List<String> likedBooks = new ArrayList();

            Iterator<JSONObject> ratingsIt = ratings.iterator();
            while(ratingsIt.hasNext()) {
                JSONObject bookEntry = ratingsIt.next();
                String isbn = (String)bookEntry.get("book_isbn");
                userProfile.add(isbn);
                Long rating = (Long)bookEntry.get("score");
                if (rating >= 4) {
                    likedBooks.add(isbn);
                }
            }
            
            
 
        } catch (IOException ex) {
            Logger.getLogger(IRProject.class.getName()).log(Level.SEVERE, null, ex);
        } catch (org.json.simple.parser.ParseException ex) {
            Logger.getLogger(IRProject.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
 
}
