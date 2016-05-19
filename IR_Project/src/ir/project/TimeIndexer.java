/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.project;

import org.apache.lucene.store.Directory;

/**
 *
 * @author robin
 */
public class TimeIndexer {
    public static long timeIndexer(String filename) {
        long startTime = System.currentTimeMillis();
        
        Indexer indexer = new Indexer();
        indexer.index(filename);
        Directory index = indexer.getIndex();

        long estimatedTime = System.currentTimeMillis() - startTime;
        
        
        return estimatedTime;
        
    }
    
    public static long timeSNAPIndexer(String filename) {
        long startTime = System.currentTimeMillis();
        
        IndexerForSNAP indexer = new IndexerForSNAP();
        indexer.index(filename);
        Directory index = indexer.getIndex();

        long estimatedTime = System.currentTimeMillis() - startTime;
        
        
        return estimatedTime;
        
    }

}
