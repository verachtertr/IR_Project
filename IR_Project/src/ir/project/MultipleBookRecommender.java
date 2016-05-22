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
 *
 * @author elise
 */
public class MultipleBookRecommender {
    
    private final TFIDFMatrix termMatrix;
    
    public MultipleBookRecommender(TFIDFMatrix termMatrix) {
        this.termMatrix = termMatrix;
    }
    
    List<String> getRecommendationsCompareTopK(List<String> userProfile, List<String> userLikes, int top) {
            HashMap<String, Integer> topRecs = new HashMap();
            
            for (int i = 0; i < termMatrix.getNumDocs(); i++) {
                TFIDFBookVector userBook = termMatrix.getTFIDFVector(i);
                if (userLikes.contains(userBook.getISBN())) {
                    HashMap<String, Double> similarities = new HashMap();
                    for (int j = 0; j < termMatrix.getNumDocs(); j++) {
                        TFIDFBookVector book = termMatrix.getTFIDFVector(j);
                        if (!userProfile.contains(book.getISBN())) {
                            Double sim = userBook.cosineSimilarity(book);
                            similarities.put(book.getTitle(), sim);
                        }
                    }
                    
                    HashMap<String, Double> sortedSim = sortByValues(similarities);

                    int t = 0;
                    for (String key : sortedSim.keySet()) {
                        if (t >= top) {
                            break;
                        }
                        t++;
                        if (topRecs.containsKey(key)) {
                            topRecs.put(key, topRecs.get(key) + 1);
                        } else {
                            topRecs.put(key, 1);
                        }
                    }
                }
            }
                
        HashMap<String, Integer> sortedTop = sortByValues(topRecs);
        List<String> recommendations = new ArrayList();
        
        int i = 0;
        for (String key : sortedTop.keySet()) {
            if (i >= top) {
                break;
            }
            i++;
            recommendations.add(key);
        }
        return recommendations;

    }
    
    List<String> getRecommendationsCompareTopKISBN(List<String> userProfile, List<String> userLikes, int top) {
            HashMap<String, Integer> topRecs = new HashMap();
            
            for (int i = 0; i < termMatrix.getNumDocs(); i++) {
                TFIDFBookVector userBook = termMatrix.getTFIDFVector(i);
                if (userLikes.contains(userBook.getISBN())) {
                    HashMap<String, Double> similarities = new HashMap();
                    for (int j = 0; j < termMatrix.getNumDocs(); j++) {
                        TFIDFBookVector book = termMatrix.getTFIDFVector(j);
                        if (!userLikes.contains(book.getISBN())) {
                            Double sim = userBook.cosineSimilarity(book);
                            similarities.put(book.getISBN(), sim);
                        }
                    }
                    
                    HashMap<String, Double> sortedSim = sortByValues(similarities);

                    int t = 0;
                    for (String key : sortedSim.keySet()) {
                        if (t >= top) {
                            break;
                        }
                        t++;
                        if (topRecs.containsKey(key)) {
                            topRecs.put(key, topRecs.get(key) + 1);
                        } else {
                            topRecs.put(key, 1);
                        }
                    }
                }
            }
                
        HashMap<String, Integer> sortedTop = sortByValues(topRecs);
        List<String> recommendations = new ArrayList();
        
        int i = 0;
        for (String key : sortedTop.keySet()) {
            if (i >= top) {
                break;
            }
            i++;
            recommendations.add(key);
        }
        return recommendations;

    }
    
    List<String> getRecommendationsAddBookVectors(List<String> userProfile, List<String> userLikes, int top) {
        TFIDFBookVector userBooks = new TFIDFBookVector(termMatrix.getNumTerms());
        
        // construct vector with all books combined
        for (int i = 0; i < termMatrix.getNumDocs(); i++) {
            TFIDFBookVector book = termMatrix.getTFIDFVector(i);
            if (userLikes.contains(book.getISBN())) {
                userBooks.addVector(book);
            }
        }
        
        // treat this vector as a single 'book'
        HashMap<String, Double> cosineSimilarities = new HashMap();
        
        for (int j = 0; j < termMatrix.getNumDocs(); j++) {
            TFIDFBookVector book = termMatrix.getTFIDFVector(j);
            if (! userProfile.contains(book.getISBN())) {
                Double cosineSimilarity = userBooks.cosineSimilarity(book);
                cosineSimilarities.put(book.getTitle(), cosineSimilarity);
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
    
    List<String> getRecommendationsAddBookVectorsISBN(List<String> userProfile, List<String> userLikes, int top) {
        TFIDFBookVector userBooks = new TFIDFBookVector(termMatrix.getNumTerms());
        
        // construct vector with all books combined
        for (int i = 0; i < termMatrix.getNumDocs(); i++) {
            TFIDFBookVector book = termMatrix.getTFIDFVector(i);
            if (userLikes.contains(book.getISBN())) {
                userBooks.addVector(book);
            }
        }
        
        // treat this vector as a single 'book'
        HashMap<String, Double> cosineSimilarities = new HashMap();
        
        for (int j = 0; j < termMatrix.getNumDocs(); j++) {
            TFIDFBookVector book = termMatrix.getTFIDFVector(j);
            if (! userLikes.contains(book.getISBN())) {
                Double cosineSimilarity = userBooks.cosineSimilarity(book);
                cosineSimilarities.put(book.getISBN(), cosineSimilarity);
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
    
    List<String> getRecommendationsAddCosineSimilarities(List<String> userProfile, List<String> userLikes, int top) {
        HashMap<String, Double> cosineSimilarities = new HashMap();
        
        for (int i = 0; i < termMatrix.getNumDocs(); i++) {
            TFIDFBookVector userBook = termMatrix.getTFIDFVector(i);
            if (userLikes.contains(userBook.getISBN())) {
                for (int j = 0; j < termMatrix.getNumDocs(); j++) {
                    TFIDFBookVector book = termMatrix.getTFIDFVector(j);
                    if (! userProfile.contains(book.getISBN())) {
                        Double cosineSimilarity = userBook.cosineSimilarity(book);
                        
                        String title = book.getTitle();
                        
                        if (cosineSimilarities.containsKey(title)) {
                            cosineSimilarities.put(title, cosineSimilarities.get(title) + cosineSimilarity);
                        } else {
                            cosineSimilarities.put(title, cosineSimilarity);
                        }
                    }
                }
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
    
     List<String> getRecommendationsAddCosineSimilaritiesISBN(List<String> userProfile, List<String> userLikes, int top) {
        HashMap<String, Double> cosineSimilarities = new HashMap();
        
        for (int i = 0; i < termMatrix.getNumDocs(); i++) {
            TFIDFBookVector userBook = termMatrix.getTFIDFVector(i);
            if (userLikes.contains(userBook.getISBN())) {
                for (int j = 0; j < termMatrix.getNumDocs(); j++) {
                    TFIDFBookVector book = termMatrix.getTFIDFVector(j);
                    if (! userLikes.contains(book.getISBN())) {
                        Double cosineSimilarity = userBook.cosineSimilarity(book);
                        
                        String isbn = book.getISBN();
                        
                        if (cosineSimilarities.containsKey(isbn)) {
                            cosineSimilarities.put(isbn, cosineSimilarities.get(isbn) + cosineSimilarity);
                        } else {
                            cosineSimilarities.put(isbn, cosineSimilarity);
                        }
                    }
                }
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