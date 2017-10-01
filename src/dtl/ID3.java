/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dtl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
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
    
    public double calculateEntropy(Instances i, List<Record> records) {
        if (records.size() > 0) {
            int num_class = i.numClasses();
            int[] proportion = new int[num_class];

            Record parent = records.get(0);

            List<String> values = new ArrayList();
            int idxForSearchProportion;

            if (parent.getValue().length() !=0 ) {
                for (int idx = 0; idx < records.size(); idx++) {
                    if (records.get(idx).value.length() != 0)
                        values.add(records.get(idx).getValue());
                }
    //            System.out.println(values);
                idxForSearchProportion = values.size();
            }
            else {
                values = attributeMember(i,(int) parent.getAttribute());
    //            System.out.println(values);
                idxForSearchProportion = 1;
            }

            for(Instance in : i) {
                boolean isMatch = true;
                for (int rec_idx = 0; rec_idx < idxForSearchProportion; rec_idx++) {
                    if (!values.contains(in.stringValue((int)records.get(rec_idx).getAttribute()))) {
                        isMatch = false;
                        break;
                    }
                }

                if (isMatch) {
                    proportion[(int)in.classValue()]++;
                }
            }

            double entropy = 0;

            int divider = 0;
            for (int idx = 0; idx < num_class; idx++) {
                divider += proportion[idx];
    //            System.out.println(proportion[idx]);
            }

            for (int idx = 0; idx < num_class; idx++) {
                double prob = (double)proportion[idx]/divider;
//                System.out.println(divider);
                if (prob!=0 && divider !=0)
                    entropy += (-1) * prob*log2(prob);
            }

            return entropy;
        }
        return -1;
    }
    
    public double calculateGain(Instances i, List<Record> records) {
        Record parent = records.get(0);
        Record child = records.get(1);
        
        List<Record> core = new ArrayList(1);
        core.add(new Record(parent.getAttribute(), parent.getValue()));
        double gain = calculateEntropy(i, core);
//        System.out.println("entropy(S) : "+gain);
        int numDistinct = i.numDistinctValues((int)child.getAttribute());
        int[] distinctAttributeCount;// = i.attributeStats((int)records.get(1)
                //.getAttribute()).nominalCounts;
        
        List<String> attributeMember = attributeMember(i,(int) child.getAttribute());
        if (parent.getValue().length() == 0) {
            attributeMember = attributeMember(i,(int) parent.getAttribute());
            distinctAttributeCount = i.attributeStats((int)parent
                .getAttribute()).nominalCounts;
        }
        else {
            distinctAttributeCount = new int[numDistinct];
            attributeMember = attributeMember(i,(int) child.getAttribute());
            for(Instance in : i) {
                if (in.stringValue((int)parent.attribute).equals(parent.value)) {
                    distinctAttributeCount[(int)in.value((int)child.attribute)]++;
                }

            }
        }
        
        int divider = 0;
        for (int attrib_idx = 0; attrib_idx < numDistinct; attrib_idx++) {
            divider += distinctAttributeCount[attrib_idx];
        }
        
        double entropy = 0;
        for (int attrib_idx=0; attrib_idx < numDistinct;
                attrib_idx++) {
            boolean isEmpty = false;
            if (parent.value.length() != 0) 
                child.value = attributeMember.get(attrib_idx);
            else {
                parent.value = attributeMember.get(attrib_idx);
                isEmpty = true;
            }
            
            entropy += ((double)distinctAttributeCount[numDistinct-attrib_idx-1]/divider) * 
                    calculateEntropy(i, records);
//            System.out.println(calculateEntropy(i, records));
            if (isEmpty) parent.value = "";
        }
        
        gain-=entropy;
        
        return gain;
    }
     
    private int idxMax(List L, List usedAttribute) {
        int idxMax=0;
        
        for (int i = 0; i < L.size(); i++) {
            if (!usedAttribute.contains((double)i)) {
                idxMax = i;
                break;
            }
        }
        double max = (double)L.get(idxMax);
        
        
        for (int i  = idxMax; i  < L.size(); i++) {
            if (max < (double) L.get(i) && !usedAttribute.contains((double)i)) {
                max =  (double) L.get(i);
                idxMax = i;
            }
        }
        return idxMax;
    }
     
    @Override
    public void buildClassifier(Instances i) throws Exception {
        List gains = new ArrayList();
        
        // buat root. Tidak punya value.
        
        DT root = new DT(null);
        
        
//        records.add(new Record(0, "sunny"));
//        records.add(new Record(3, ""));
        for (int idx = 0; idx < i.numAttributes()-1; idx++) {
            List<Record> records = new ArrayList();
            records.add(new Record(idx, ""));
            records.add(new Record(3, "")); //dummy record
            gains.add(calculateGain(i, records));
        }
        root.setValue("");
        root.setAttribute(idxMax(gains, root.getUsedAttribute()));
        root.addUsedAttributeValue(root.getAttribute());
        
        // Bangun node baru
        
        Stack<DT> nodeStack = new Stack();
        nodeStack.push(root);
//        System.out.println(gains);
        List <Double> usedAttribute = new ArrayList();
        usedAttribute.add(root.getAttribute());
        
        Stack<String> values = new Stack();
        
        while (true) {
            DT parent = nodeStack.peek();
            
            List attributeMember = attributeMember(i,(int) parent.getAttribute());
//            System.out.println(parent.getAttribute());
            // push semua value pada root attribut
            if (parent.getChild().size() == 0) 
                for (int idx = attributeMember.size()-1; idx>=0; idx--) {
                    parent.getUsedValue().push(attributeMember.get(idx).toString());
                }
            
            gains.clear();
            
            // kalau udah kosong berarti semua anak sudah dibangkitkan
//            System.out.println(parent.getUsedValue());
            if (parent.getChild().size() == i.numDistinctValues((int)parent.getAttribute())) {
                nodeStack.pop();
                if (nodeStack.size() == 0) break;
            }
            else {
                String parentValue = parent.getUsedValue().pop().toString();
                parent.aaa = parentValue;
//                System.out.println(parentValue);
                // hitung gain dan dapatkan nilai terbesar untuk menentukan
                // atribute yang cocok untuk menjadi child node dengan value
                // tertentu
                /**
                 *                  outlook
                 *            sunny /
                 *                 /
                 *              humidity
                 **/
                double selectedAttribute;
                boolean isZeroEntropy = false;
                
                // Jika sebuah value mempunyai entropi = 0, maka langsung
                // masukkan atribut kelas pada node tersebut.
                
//                values.push(parentValue);
                DT node = parent;
                List<Record> listOfNodeValue = new ArrayList();
//                
//                for (int idx =0; idx < values.size(); idx++) {
//                    listOfNodeValue.add(new Record(parent.getAttribute(), values.get(idx)));
//                    System.out.println(parent.getAttribute()+" "+values.get(idx));
//                }
                if (node.getParent()!= null) {
                    while (node.getParent()!=null) {
                        String value = node.aaa;
                        double attributeIdx = node.getAttribute();
                        listOfNodeValue.add(new Record(attributeIdx, value));
//                        listOfNodeValue.add(new Record(node.getAttribute(), 
//                                parent.getUsedValue().get(0).toString()));
//                        System.out.println(node.getAttribute()+ " "+node.aaa);
                        
                        node = node.getParent();
                    }
                    listOfNodeValue.add(new Record(node.getAttribute(), node.aaa));
                }
                else {
//                    System.out.println(parent.getUsedValue().get(0).toString());
                    listOfNodeValue.add(new Record(parent.getAttribute(), parent.aaa));
                }

                Collections.reverse(listOfNodeValue);
                
                if (calculateEntropy(i, listOfNodeValue) != 0) {
                    for (int idx = 0; idx < i.numAttributes()-1; idx++) {
                            List<Record> records = new ArrayList();
                            records.add(new Record(parent.getAttribute(), parentValue));
                            records.add(new Record(idx, ""));
                            gains.add(calculateGain(i,records));
                    }
                    // setelah gain setiap attribute diperoleh, cari yang memiliki
                    // nilai paling besar. Jadikan sebagai attribut child.
                    selectedAttribute = (double) idxMax(gains, usedAttribute);
//                    System.out.println(usedAttribute);
//                    System.out.println(selectedAttribute);
                }
                else {
                    isZeroEntropy = true;
                    selectedAttribute = i.classIndex();
                }
                // setelah gain setiap attribute diperoleh, cari yang memiliki
                // nilai paling besar. Jadikan sebagai attribut child.
                DT child = DT.addChild(parent, selectedAttribute);
                child.setValue(parentValue);
                child.getUsedAttribute().addAll(parent.getUsedAttribute());
                child.addUsedAttributeValue(selectedAttribute);
//                System.out.println(selectedAttribute);
                // Kalau attribut yang terpilih adalah atribut kelas, maka
                // langsung buat child baru sejumlah attribut yang diassign dengan
                // kelasnya.
                // Jika tidak, maka push child kedalam stack untuk membangkitkan
                // childnya.
                if (isZeroEntropy) {
                    // hitung entropy setiap nilai kelas, entropy terkecil akan 
                    // dipilih sebagai kelas.
                    List<Double> entropies = new ArrayList();
                    List<String> classValues = attributeMember(i, i.classIndex());
                    for (int num_class = 0; num_class  < i.numDistinctValues(i.classIndex());
                            num_class++) {
//                        entropies.add(calculateEntropy(i,);
                    }
                    
//                    child.setClass(classValues.get(entropies
//                            .indexOf(Collections.min(entropies))));
                      child.setClass("No");
//                    nodeStack.pop();
//                    System.out.println(classValues.get(entropies
//                            .indexOf(Collections.min(entropies))));
                }
                else {
                    usedAttribute.add(selectedAttribute);
                    nodeStack.push(child);
                }
            }
//            DT.printTree(root, " ");
        }
        
        DT.printTree(root, " ");
    }
    
    
}
