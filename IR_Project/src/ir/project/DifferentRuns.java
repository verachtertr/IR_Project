/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.project;

import ir.project.helper.SVDecomposition;
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
import org.apache.lucene.store.Directory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
            }

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

        List<String> userProfile = new ArrayList();

        // read in user data from JSON
        JSONParser parser = new JSONParser();

        try {
            Object obj = parser.parse(new FileReader("./resources/IR_test_user.json"));
            JSONArray array = (JSONArray) obj;
            Iterator<JSONObject> iterator = array.iterator();

            JSONObject user1 = iterator.next();
            String name = (String) user1.get("name");
            System.out.println("Hi " + name);
            JSONArray ratings = (JSONArray) user1.get("ratings");

            HashMap<String, Integer> top = new HashMap();
            int x = 0;
            Iterator<JSONObject> ratingsIterator = ratings.iterator();

            while (ratingsIterator.hasNext()) {
                x++;
                System.out.println(x);
                JSONObject doc = ratingsIterator.next();
                /*if (x >= 10) {
                    userProfile.add((String)doc.get("book_isbn"));
                    break;
                }*/

                String isbn = (String) doc.get("book_isbn");
                Long rating = (Long) doc.get("score");
                userProfile.add(isbn);
                System.out.println("Isbn " + isbn);
                if (rating >= 4) {
                    // look up tfidfvector by isbn
                    for (int i = 0; i < termMatrix.getNumDocs(); i++) {
                        TFIDFBookVector vec = termMatrix.getTFIDFVector(i);
                        if (vec.getISBN().equals(isbn)) {
                            vec.setVector(d.changeQuery(vec).getVector());
                            // get cosine similarities
                            HashMap<String, Double> similarities = new HashMap();
                            for (int j = 0; j < termMatrix.getNumDocs(); j++) {

                                TFIDFBookVector book = termMatrix.getTFIDFVector(j);
                                if (!userProfile.contains(book.getISBN())) {
                                    Double sim = vec.cosineSimilarity(book);
                                    similarities.put(book.getTitle(), sim);
                                }
                            }
                            HashMap<String, Double> sortedSim = sortByValues(similarities);

                            // show first 10
                            int t = 0;
                            for (String key : sortedSim.keySet()) {
                                if (t >= 20) {
                                    break;
                                }
                                t++;
                                if (top.containsKey(key)) {
                                    top.put(key, top.get(key) + 1);
                                } else {
                                    top.put(key, 1);
                                }
                            }
                            break;
                        }
                    }
                }

            }

            System.out.println("For you we recommend the following books:");

            HashMap<String, Integer> sortedTop = sortByValues(top);

            for (Map.Entry<String, Integer> entry : sortedTop.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }

        } catch (IOException ex) {
            Logger.getLogger(IRProject.class.getName()).log(Level.SEVERE, null, ex);
        } catch (org.json.simple.parser.ParseException ex) {
            Logger.getLogger(IRProject.class.getName()).log(Level.SEVERE, null, ex);
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
