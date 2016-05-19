/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.project;

import ir.project.helper.SVDecomposition;
import java.io.FileReader;
import java.io.IOException;
import static java.lang.Math.round;
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
import org.apache.lucene.store.Directory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import static java.lang.Math.round;

/**
 *
 * @author robin
 */
public class DifferentRuns {
    
    public static void booksSeparate() {
        try {
            // Index some books
            Indexer indexer = new Indexer();
            indexer.index("./resources/books_IR_test.json");
            Directory index = indexer.getIndex();
            
            TFIDFMatrix termMatrix = new TFIDFMatrix(index);
            System.out.println("INDEXED");
            
            SingleBookRecommender recommender = new SingleBookRecommender(termMatrix);

            JSONParser parser = new JSONParser();

            Object obj = parser.parse(new FileReader("./resources/users_test.json"));
            JSONArray array = (JSONArray) obj;
            Iterator<JSONObject> iterator = array.iterator();

            JSONObject user1 = iterator.next();
            String name = (String) user1.get("name");
            System.out.println("Hi " + name);
            JSONArray ratings = (JSONArray) user1.get("ratings");

            HashMap<String, Integer> top = new HashMap();
            Iterator<JSONObject> ratingsIterator = ratings.iterator();
            
            List<String> userProfile = new ArrayList();
            List<String> userLikes = new ArrayList();

            while (ratingsIterator.hasNext()) {
                JSONObject doc = ratingsIterator.next();

                String isbn = (String) doc.get("book_isbn");
                Long rating = (Long) doc.get("score");
                userProfile.add(isbn);
                
                if (rating >= 4) {
                    userLikes.add(isbn);
                }
            }
            
            double totalPrecision = 0;
            
            for (String isbn : userLikes) {
                //System.out.println("Recommending for " + isbn);
                List<String> recommendations = recommender.getRecommendationsISBN(isbn, 10);
                int numCorrect = 0;
                for(String rec : recommendations) {
                    if(userLikes.contains(rec)) {
                        numCorrect++;
                    }
                }
                double precisionAtTen = (double)numCorrect / 10.0;
                //System.out.println("Precision @10: " + precisionAtTen);
                totalPrecision += precisionAtTen;
            }
            
            totalPrecision /= userLikes.size();
            System.out.println(totalPrecision);

        } catch (IOException ex) {
            Logger.getLogger(DifferentRuns.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(DifferentRuns.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void usingSVDbooksSeperate() {
        // Index some books
        Indexer indexer = new Indexer();
        indexer.index("./resources/books_IR_test.json");
        Directory index = indexer.getIndex();

        TFIDFMatrix termMatrix = new TFIDFMatrix(index);
        System.out.println("INDEXED");

        // Create SVD
        SVDecomposition d = new SVDecomposition(termMatrix);
        termMatrix = d.getTfMatrix();

        System.out.println("Used SVD");

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
            List<String> userLikes = new ArrayList();
            
            Iterator<JSONObject> ratingsIterator = ratings.iterator();
            
           while (ratingsIterator.hasNext()) {
                JSONObject doc = ratingsIterator.next();

                String isbn = (String) doc.get("book_isbn");
                Long rating = (Long) doc.get("score");
                userProfile.add(isbn);
                
                if (rating >= 4) {
                    userLikes.add(isbn);
                }
            }
           
            SingleBookRecommender recommender = new SingleBookRecommender(termMatrix);
           
            double totalPrecision = 0;
            
            for (String isbn : userLikes) {
                System.out.println("Recommending for " + isbn);
                List<String> recommendations = recommender.getRecommendationsISBN(isbn, 10);
                int numCorrect = 0;
                for(String rec : recommendations) {
                    if(userLikes.contains(rec)) {
                        numCorrect++;
                    }
                }
                double precisionAtTen = (double)numCorrect / 10.0;
                System.out.println("Precision @10: " + precisionAtTen);
                totalPrecision += precisionAtTen;
            }
            
            totalPrecision /= userLikes.size();
            System.out.println(totalPrecision);

          

        } catch (IOException ex) {
            Logger.getLogger(IRProject.class.getName()).log(Level.SEVERE, null, ex);
        } catch (org.json.simple.parser.ParseException ex) {
            Logger.getLogger(IRProject.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void booksMultiple() {
        try {
            // Index some books
            Indexer indexer = new Indexer();
            indexer.index("./resources/books_IR_test.json");
            Directory index = indexer.getIndex();
            
            TFIDFMatrix termMatrix = new TFIDFMatrix(index);
            System.out.println("INDEXED");
            
            MultipleBookRecommender recommender = new MultipleBookRecommender(termMatrix);
            
            JSONParser parser = new JSONParser();
            
            Object obj = parser.parse(new FileReader("./resources/IR_test_user.json"));
            JSONArray array = (JSONArray) obj;
            Iterator<JSONObject> iterator = array.iterator();

            JSONObject user1 = iterator.next();
            String name = (String) user1.get("name");
            System.out.println("Hi " + name);
            JSONArray ratings = (JSONArray) user1.get("ratings");

            HashMap<String, Integer> top = new HashMap();
            Iterator<JSONObject> ratingsIterator = ratings.iterator();
            
            List<String> userProfile = new ArrayList();
            List<String> userLikes = new ArrayList();

            while (ratingsIterator.hasNext()) {
                JSONObject doc = ratingsIterator.next();

                String isbn = (String) doc.get("book_isbn");
                Long rating = (Long) doc.get("score");
                userProfile.add(isbn);
                
                if (rating >= 4) {
                    userLikes.add(isbn);
                }
            }
            
            int numLikes = userLikes.size();
            int numTraining = (int) round((double)numLikes * 0.8);
            int numTest = numLikes - numTraining;
            
            List<String> trainingSet = userLikes.subList(0, numTraining);
            List<String> testSet = userLikes.subList(numTraining, userLikes.size());
            
            System.out.println(trainingSet);
            System.out.println(testSet);
            
            // technique 1
            List<String> recommendationsCompareTopK = recommender.getRecommendationsCompareTopKISBN(userProfile, trainingSet, 10);
            int numCorrectCompareTopK = 0;
            for(String rec : recommendationsCompareTopK) {
                if(testSet.contains(rec)) {
                    numCorrectCompareTopK++;
                }
            }
            double precisionAtTenCompareTopK = (double)numCorrectCompareTopK / 10.0;
            System.out.println("Precision @10 for method 'Compare top k': " + precisionAtTenCompareTopK);
            
            // technique 2
            List<String> recommendationsAddBookVectors = recommender.getRecommendationsAddBookVectorsISBN(userProfile, trainingSet, 10);
            int numCorrectAddBookVectors = 0;
            for (String rec: recommendationsAddBookVectors) {
                if(testSet.contains(rec)) {
                    numCorrectAddBookVectors++;
                }
            }
            double precisionAddBookVectors = (double)numCorrectAddBookVectors / 10.0;
            System.out.println("Precision @10 for method 'Add book vectors': " + precisionAddBookVectors);
            
            // technique 3
            List<String> recommendationsAddSims = recommender.getRecommendationsAddCosineSimilaritiesISBN(userProfile, testSet, 10);
            int numCorrectAddSims = 0;
            for (String rec: recommendationsAddSims) {
                if(testSet.contains(rec)) {
                    numCorrectAddSims++;
                }
            }
            double precisionAddSims = (double)numCorrectAddSims / 10.0;
            System.out.println("Precision @10 for method 'Add cosine similarities': " + precisionAddSims);
            
        } catch (IOException ex) {
            Logger.getLogger(DifferentRuns.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(DifferentRuns.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
        
    private static HashMap sortByValues(HashMap map) {
        // credits : http://beginnersbook.com/2013/12/how-to-sort-hashmap-in-java-by-keys-and-values/

        List list = new LinkedList(map.entrySet());
        // Defined Custom Comparator here
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o2)).getValue())
                        .compareTo(((Map.Entry) (o1)).getValue());
            }
        });

        // Here I am copying the sorted list in HashMap
        // using LinkedHashMap to preserve the insertion order
        HashMap sortedHashMap = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }
        return sortedHashMap;
    }

}
