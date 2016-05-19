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

        DifferentRuns.usingSVD();
    }
}
