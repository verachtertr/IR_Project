/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.project;


/**
 *
 * @author elise
 */
public class IRProject {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //SVDRecommender.booksSeperate();
        //long time = TimeIndexer.timeIndexer("./resources/books_IR_test.json");
        //long time = TimeIndexer.timeSNAPIndexer("./resources/Books_5.json");
        //System.out.println("The operation took: " + time + " ms");
        //DifferentRuns.booksSeparate();
        DifferentRuns.usingSVDbooksSeperate();
        
        //DifferentRuns.booksMultiple();
    }
}
