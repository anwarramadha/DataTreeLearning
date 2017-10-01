/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dtl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Stack;
import weka.core.Attribute;
import weka.core.Instances;

/**
 *
 * @author AnwarRamadha
 */
public class DTL {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException, Exception {
        // TODO code application logic here
        BufferedReader reader = new BufferedReader(new FileReader("E:\\Weka-3-8\\data\\weather.nominal.arff"));
        Instances i = new Instances(reader);
        i.setClassIndex(i.numAttributes() - 1);
        ID3 id3 = new ID3();
//        System.out.println(id3.calculateEntropy(i, 0, false, "rainy"));
//        System.out.println(id3.calculateGain(i, 4, 3, true, ""));//test gain againts root
//        System.out.println(id3.calculateGain(i, 0, 1, false, "sunny"));
//        System.out.println(id3.calculateGain(i, 0, 1, false, "overcast"));
        id3.buildClassifier(i);
        Stack<Record> records = new Stack();
        records.push(new Record(0, "rainy"));
        records.push(new Record(1, ""));
        
        System.out.println(id3.calculateGain(i, records));
    }
    
}
