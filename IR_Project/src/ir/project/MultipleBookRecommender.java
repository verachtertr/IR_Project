/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.project;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author elise
 */
public class MultipleBookRecommender {
    
    private final TFIDFMatrix termMatrix;
    
    public MultipleBookRecommender(TFIDFMatrix termMatrix) {
        this.termMatrix = termMatrix;
    }
    
    List<String> getRecommendationsCompareTopK(List<String> userProfile, List<String> userLikes, int top) {
        return null;
    }
    
    List<String> getRecommendationsAddBookVectors(List<String> userProfile, List<String> userLikes, int top) {
        return null;
    }
    
    List<String> getRecommendationsAddCosineSimilarities(List<String> userProfile, List<String> userLikes, int top) {
        return null;
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

/*
 //Iterator<JSONObject> ratingsIterator = ratings.iterator();
            HashMap<String, Double> similarities = new HashMap();
            //while (ratingsIterator.hasNext()) {
            //    JSONObject doc = ratingsIterator.next();
            //    String isbn = (String) doc.get("book_isbn");
            //    Long rating = (Long) doc.get("score");
            //    userProfile.add(isbn);
            //    if (rating >= 4) {
                    // look up tfidfvector by isbn
                    for (int i = 0; i < termMatrix.getNumDocs(); i++) {
                        TFIDFBookVector vec = termMatrix.getTFIDFVector(i);
                        if (likedBooks.contains(vec.getISBN())) {
                            // book that user liked
                            for (int j = 0; j < termMatrix.getNumDocs(); j++) {
                                TFIDFBookVector book = termMatrix.getTFIDFVector(j);
                                if(! userProfile.contains(book.getISBN())) {
                                    String title = book.getTitle();
                                    Double sim = vec.cosineSimilarity(book);
                                    if (similarities.containsKey(title)) {
                                        similarities.put(title, similarities.get(title) + sim);
                                    } else {
                                        similarities.put(title, sim);
                                    }
                                }
                            }
                                
                        }
                    }
              //          if (vec.getISBN().equals(isbn)) {
              //              
                            // add to query vector
                            //likedBooks.addVector(vec);
                            // get cosine similarities
                            //HashMap<String, Double> similarities = new HashMap();
                           // for (int j = 0; j < termMatrix.getNumDocs(); j++) {

                             //   TFIDFBookVector book = termMatrix.getTFIDFVector(j);
                               // if (!userProfile.contains(book.getISBN())) {
                                 //   Double sim = vec.cosineSimilarity(book);
//                                    similarities.put(book.getTitle(), sim);
//String title = book.getTitle();
//                                    if(similarities.containsKey(book.getTitle())) {
//                                        similarities.put(title, similarities.get(title) + sim);
//                                    } else {
//                                        similarities.put(title, sim);
//                                    }
//                                }
//                            }
                           // HashMap<String, Double> sortedSim = sortByValues(similarities);

                            // show first 10
//                            int t = 0;
//                            for (String key : sortedSim.keySet()) {
//                                if (t >= 20) {
//                                    break;
//                                }
//                                t++;
//                                //if (top.containsKey(key)) {
//                                //    top.put(key, top.get(key) + 1);
//                                //} else {
//                                //    top.put(key, 1);
//                                // }
//                            }
    //                        break;
  //                      }
      //              }
        //        }

          //  }
            
            // compare likedBooks vector with other books in the system
            /*HashMap<String, Double> similarities = new HashMap();
            for (int i = 0; i < termMatrix.getNumDocs(); i++) {
                TFIDFBookVector book = termMatrix.getTFIDFVector(i);
                String isbn = book.getISBN();
                if (! userProfile.contains(isbn)) {
                    double sim = likedBooks.cosineSimilarity(book);
                    similarities.put(book.getTitle(), sim);
                }
            }
            
            HashMap<String, Double> sortedSimilarities = sortByValues(similarities);

            System.out.println("For you we recommend the following books:");
            for (String title: sortedSimilarities.keySet()) {
                System.out.println(title);
            }
            //HashMap<String, Integer> sortedTop = sortByValues(top);
            //for (Map.Entry<String, Integer> entry : sortedTop.entrySet()) {
            //  System.out.println(entry.getKey()+": "+entry.getValue());
            //}
*/