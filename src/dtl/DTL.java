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
import java.util.Random;
import java.util.Stack;
import weka.classifiers.Evaluation;
import weka.core.Attribute;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NumericToNominal;

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
        BufferedReader reader = new BufferedReader(new FileReader("E:\\Weka-3-8\\data\\iris.2D.arff"));
        Instances i = new Instances(reader);
        
        NumericToNominal convert= new NumericToNominal();
        String[] options= new String[2];
        options[0]="-R";
        options[1]="1-";  //range of variables to make numeric
        options[1]=options[1].concat(String.valueOf(i.numAttributes()));

        convert.setOptions(options);
        convert.setInputFormat(i);

        Instances newData=Filter.useFilter(i, convert);
        newData.setClassIndex(newData.numAttributes()-1);
        
        i.setClassIndex(i.numAttributes() - 1);
        ID3 id3 = new ID3();
        id3.buildClassifier(newData);
        Evaluation eval = new Evaluation(newData);
        eval.evaluateModel(id3, newData);
        System.out.println(eval.toSummaryString());
    }
    
}
