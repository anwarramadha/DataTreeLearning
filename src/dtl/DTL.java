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
    public static void main(String[] args) throws FileNotFoundException, IOException {
        // TODO code application logic here
        BufferedReader reader = new BufferedReader(new FileReader("E:\\Weka-3-8\\data\\iris.arff"));
        Instances i = new Instances(reader);
        System.out.println(i.instance(0).value(i.attribute(i.numAttributes() - 1)));
        
        ID3 id3 = new ID3();
        
    }
    
}
