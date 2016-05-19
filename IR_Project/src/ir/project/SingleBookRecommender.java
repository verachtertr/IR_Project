/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.project;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Class to make recommendations based on a single book
 * @author elise
 */
public class SingleBookRecommender {
    
    private final TFIDFMatrix termMatrix;
    
    public SingleBookRecommender(TFIDFMatrix termMatrix) {
        this.termMatrix = termMatrix;
    }
    
    public List<String> getRecommendationsISBN(String isbn, int top) {
           HashMap<String, Double> cosineSimilarities = new HashMap();
        
        for (int i = 0; i < termMatrix.getNumDocs(); i++) {
            TFIDFBookVector userBook = termMatrix.getTFIDFVector(i);
            if (userBook.getISBN().equals(isbn)) {
                
                for (int j = 0; j < termMatrix.getNumDocs(); j++) {
                    TFIDFBookVector book = termMatrix.getTFIDFVector(j);
                    if (! book.getISBN().equals(isbn)) {
                        Double cosineSimilarity = userBook.cosineSimilarity(book);
                        cosineSimilarities.put(book.getISBN(), cosineSimilarity);
                    }
                }
                break;
            }
        }
        
        HashMap<String, Double> sortedSimilarities = sortByValues(cosineSimilarities);
        List<String> recommendations = new ArrayList();
        
        int i = 0;
        for (String key : sortedSimilarities.keySet()) {
            if (i >= top) {
                break;
            }
            i++;
            recommendations.add(key);
        }
        return recommendations;
    }
    
    public List<String> getRecommendations(String isbn, int top) {
        
        HashMap<String, Double> cosineSimilarities = new HashMap();
        
        for (int i = 0; i < termMatrix.getNumDocs(); i++) {
            TFIDFBookVector userBook = termMatrix.getTFIDFVector(i);
            if (userBook.getISBN().equals(isbn)) {
                
                for (int j = 0; j < termMatrix.getNumDocs(); j++) {
                    TFIDFBookVector book = termMatrix.getTFIDFVector(j);
                    if (! book.getISBN().equals(isbn)) {
                        Double cosineSimilarity = userBook.cosineSimilarity(book);
                        cosineSimilarities.put(book.getTitle(), cosineSimilarity);
                    }
                }
                break;
            }
        }
        
        HashMap<String, Double> sortedSimilarities = sortByValues(cosineSimilarities);
        List<String> recommendations = new ArrayList();
        
        int i = 0;
        for (String key : sortedSimilarities.keySet()) {
            if (i >= top) {
                break;
            }
            i++;
            recommendations.add(key);
        }
        return recommendations;
    }
    
    private HashMap sortByValues(HashMap map) {
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
