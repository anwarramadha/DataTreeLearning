/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dtl;

import java.util.ArrayList;
import java.util.List;
import weka.classifiers.AbstractClassifier;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

/**
 *
 * @author AnwarRamadha
 */
public class ID3 extends AbstractClassifier{
    
    private double log2(double value) {
        return Math.log(value)/Math.log(2);
    }
    
    private List attributeMember(Instances i, int attrib_idx) {
        
        int attribute_num = i.numDistinctValues(attrib_idx);
        List classAttribute = new ArrayList();
        List value = new ArrayList(attribute_num);
        
        for (int idx = 0; idx < i.numInstances(); idx++) {
            if (!value.contains(i.instance(idx).value(attrib_idx))) {
                value.add(i.instance(idx).value(attrib_idx));
            }
            
            if (value.size() == attribute_num) break;
        }
        
        return value;
    }
    
    private double calculateEntropy(Instances i, int attrib_idx) {
        
        int attribute_num = i.numDistinctValues(attrib_idx);
        int[] proportion = new int[attribute_num];
        List value = attributeMember(i, attrib_idx);
        
        for (Instance in : i) {
            proportion[value.indexOf(in.value(attrib_idx))]++;
        }
        
        double entropy = 0;
        for (int idx = 0; idx < attribute_num; idx++) {
            double prob = (double)proportion[idx]/i.numInstances();
            entropy += (-1) * prob*log2(prob);
        }
        return entropy;
    }
    
     private double calculateGain(Instances i, int attrib_idx, int next_attrib_idx) {
        
        double ins_entropy = calculateEntropy(i, attrib_idx);
        
        int attribute_num = i.numDistinctValues(next_attrib_idx);
        int class_num = i.numDistinctValues(attrib_idx);
        
        int[][] proportion = new int[class_num][attribute_num];
        
        List class_value = attributeMember(i, attrib_idx);
        List attrib_values = attributeMember(i, next_attrib_idx);
        
        for (Instance in : i) {
            proportion[class_value.indexOf(in.value(attrib_idx))][attrib_values.indexOf(in.value(next_attrib_idx))]++;
//            proportion[value.indexOf(in.value(next_attrib_idx))]++;
        }
        
        double gain = ins_entropy;
        for (int cls_idx = 0; cls_idx < class_num; cls_idx ++) {
            double entropy = 0;
            for(int att_idx = 0; att_idx < attribute_num; att_idx++) {
                
                double prob = (double) proportion[cls_idx][att_idx]/i.numInstances();
                if (prob != 0.0)
                    entropy += (-1) * prob * log2(prob) * prob;
            }
            gain -= entropy;
        }
        
        return gain;
    }
     
    @Override
    public void buildClassifier(Instances i) throws Exception {
        
        DT root = new DT(null);
    }
    
    
}
