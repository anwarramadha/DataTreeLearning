/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dtl;

import java.util.ArrayList;
import java.util.Collections;
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
        List value = new ArrayList(attribute_num);
        
        for (int idx = 0; idx < i.numInstances(); idx++) {
            if (!value.contains(i.instance(idx).stringValue(attrib_idx))) {
                value.add(i.instance(idx).stringValue(attrib_idx));
            }
            
            if (value.size() == attribute_num) break;
        }
        
        return value;
    }
    
    public double calculateEntropy(Instances i, int attrib_idx, boolean root, String val) {
        
        int attribute_num = i.numDistinctValues(i.classIndex());
        int[] proportion;
        int divider;
        if (root) {
            proportion = new int[attribute_num];
            List value = attributeMember(i, attrib_idx);
            for (Instance in : i) {
                proportion[value.indexOf(in.stringValue(attrib_idx))]++;
            }
            divider = i.numInstances();
        }
        else {
            proportion = new int[attribute_num];
            List value = attributeMember(i, i.classIndex());
            for (Instance in : i) {
                if (in.stringValue(attrib_idx).equals(val)) {
                    proportion[value.indexOf(in.stringValue(i.classIndex()))]++;
                }
            }
            divider = 0;
            for (int idx = 0; idx < attribute_num; idx++) {
                divider += proportion[idx];
            }
        }
//        System.out.println(value);
        
        
        double entropy = 0;
        for (int idx = 0; idx < attribute_num; idx++) {
            double prob = (double)proportion[idx]/divider;
            entropy += (-1) * prob*log2(prob);
        }
        return entropy;
    }
    
    private int sumMatrix(int[][] m, int row, int col){
        int sum = 0;
        for (int i = 0; i < row; i ++) {
            for (int j=0; j < col; j++) sum+=m[i][j];
        }
        
        return sum;
    }
    
    private double calculateEntropyV(Instances i, int attrib_idxV, int attrib_idxA, List listOfA, String val, String valA) {
        int num_class = i.numDistinctValues(i.classIndex());
        int[] proportion = new int[num_class];
        
        for (Instance in : i) {
//            System.out.println(in.stringValue(attrib_idxA));
            if (in.stringValue(attrib_idxV).equals(val) && listOfA.contains(in.stringValue(attrib_idxA)) && in.stringValue(attrib_idxA).equals(valA)){
                proportion[(int)in.value(i.classIndex())]++;
            }
        }
        
        double entropy = 0;
        int divider = 0;
        for (int idx = 0; idx < num_class; idx++) {
            divider += proportion[idx];
        }
        for (int idx = 0; idx < num_class; idx++) {
            double prob = (double)proportion[idx]/divider;
            if (prob!=0)
            entropy += (-1) * prob*log2(prob);
        }
        
        return entropy;
    }
    
    private int calculateValueOccurence(Instances i, int attrib_idx, String value) {
        int valueOccurence = 0;
//        System.out.println();
        for (int idx = 0; idx < i.numInstances(); idx++) {
            if (i.get(idx).stringValue(attrib_idx).equals(value)) valueOccurence ++;
        }
        return valueOccurence;
    }
    public double calculateGain(Instances i, int attrib_idx, int next_attrib_idx, boolean root, String value) {
        
        double ins_entropy = calculateEntropy(i, attrib_idx, root, value);
        
        int attribute_num;// = i.numDistinctValues(next_attrib_idx);
        int class_num;// = i.numDistinctValues(attrib_idx);
        
        List class_value = attributeMember(i, attrib_idx);
        List attrib_values = new ArrayList();
        List attrib_values_temp = attributeMember(i, next_attrib_idx);
//        System.out.println(attrib_values_temp);
        int[][] proportion;
        
        double gain = ins_entropy;
        if (root) {
            attribute_num = i.numDistinctValues(next_attrib_idx);
            class_num = i.numDistinctValues(attrib_idx);
            proportion = new int[class_num][attribute_num];
            attrib_values = attrib_values_temp;
            for (Instance in : i) {
    //            System.out.println(attrib_values.indexOf(in.stringValue(next_attrib_idx)));
//                System.out.println(class_value);

                if (attrib_values.indexOf(in.stringValue(next_attrib_idx)) != -1)
                    proportion[attrib_values.indexOf(in.stringValue(next_attrib_idx))][class_value.indexOf(in.stringValue(attrib_idx))]++;
    //            proportion[value.indexOf(in.value(next_attrib_idx))]++;
            }
            for (int cls_idx = 0; cls_idx < class_num; cls_idx ++) {
                double entropy = 0;
                double num_val = 0;

                for(int att_idx = 0; att_idx < attrib_values.size(); att_idx++) {
    //                System.out.println(proportion[cls_idx][att_idx]);
                   num_val +=  proportion[cls_idx][att_idx];
                }

                for(int att_idx = 0; att_idx < attrib_values.size(); att_idx++) {

                    if (num_val != 0) {
                        double prob = (double) proportion[cls_idx][att_idx]/num_val;
                        entropy += (-1) * prob * log2(prob);
                    }
                }
                gain -= entropy * num_val/i.numInstances(); // jelas salah
            }
        }
        else {
            attribute_num = calculateValueOccurence(i, attrib_idx, value);
            class_num = 1;
            proportion = new int[class_num][attribute_num];
//            System.out.println(attribute_num);
            
//            int [] relIdx = new int[class_value.size()];
//            for (int idx = 0; idx < class_value.size(); idx ++) {
//                if (class_value.get(idx).equals(value))
//                
//                    attrib_values.add(attrib_values_temp.get(idx));
//            }
            
            for (Instance in : i) {
//                System.out.println(attrib_values.indexOf(in.stringValue(attrib_idx)));
//                System.out.println(attrib_values);
                  if (in.stringValue(attrib_idx).equals(value) && !attrib_values.contains(in.stringValue(next_attrib_idx))) 
                      attrib_values.add(in.stringValue(next_attrib_idx));
                
            }
//            attrib_values.add(attributeMember(i, next_attrib_idx).get(class_value.indexOf(value)));
//            System.out.println(attrib_values);
            for (Instance in : i) {
//                System.out.println(attrib_values.indexOf(in.stringValue(attrib_idx)));
//                System.out.println(attrib_values);

                if (attrib_values.indexOf(in.stringValue(next_attrib_idx)) != -1 && in.stringValue(attrib_idx).equals(value))
                    proportion[0][attrib_values.indexOf(in.stringValue(next_attrib_idx))]++;
    //            proportion[value.indexOf(in.value(next_attrib_idx))]++;
            }
            
            for (int cls_idx = 0; cls_idx < class_num; cls_idx ++) {
                double entropy = 0;
                double num_val = 0;

                for(int att_idx = 0; att_idx < attrib_values.size(); att_idx++) {
                   num_val +=  proportion[cls_idx][att_idx];
                }

                for(int att_idx = 0; att_idx < attrib_values.size(); att_idx++) {

                entropy = calculateEntropyV(i, 0, 
                        next_attrib_idx, attrib_values, "sunny", attrib_values.get(att_idx).toString());

                    gain -= entropy * (proportion[cls_idx][att_idx]/num_val);
                }
            }
        }
        
        return gain;
    }
     
    private int idxMax(List L) {
        return L.indexOf(Collections.max(L));
    }
     
    @Override
    public void buildClassifier(Instances i) throws Exception {
        List gains = new ArrayList();
        
        // buat root. Tidak punya value.
        for (int idx = 0; idx < i.numAttributes(); idx++) {
            gains.add(calculateGain(i, i.numAttributes() - 1, idx, true, ""));
        }
        
        DT root = new DT(null);
        root.setAttribute(idxMax(gains));
        System.out.println(idxMax(gains));
    
        // Bangun node baru
        int num_child = i.attribute((int) root.getId()).numValues(), idx=0;
        while (idx < num_child) {
            break;
        }
        
    }
    
    
}
