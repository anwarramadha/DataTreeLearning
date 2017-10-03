/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dtl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import weka.classifiers.AbstractClassifier;
import weka.core.Instance;
import weka.core.Instances;

/**
 *
 * @author AnwarRamadha
 */
public class ID3 extends AbstractClassifier implements Serializable{
    private DT root;
    private List<Integer>numDistinctValues = new ArrayList();
    
    private double log2(double value) {
        return Math.log(value)/Math.log(2);
    }
    
    /**
     * Menghasilkan list of attribute member yang ditunjuk oleh index tertentu
     * attribute pada list dipastikan tidak ada duplikasi
     * 
     * @param i (weka instances)
     * @param attrib_idx (index attribute dimana tempat mencari value yang di
     * inginkan.
     * @return
     */
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
    
    /**
     * Mengembalikan nilai entropy.
     * Entropy = 0 => semua examples dalam satu kelas
     * Entropy = 1 => p+ == p-
     * 
     * @param i
     * @param records
     * @return 
     */
    public double calculateEntropy(Instances i, List<Record> records) {
        int num_class = i.numClasses();
        int[] proportion = new int[num_class];

        Record parent = records.get(0);

        List<String> values = new ArrayList();
        int idxForSearchProportion;
        
        /**
         * Jika nilai S pada Gain(S,A) memiliki variable tertentu, maka
         * masukkan semua varibel tersebut kedalam suatu list. List digunakan 
         * untuk melakukan pengecekan apakah nilai pada instances cocok
         * dengan urutan tertentu.
         */
        if (parent.getValue().length() !=0 ) {
            for (int idx = 0; idx < records.size(); idx++) {
                if (records.get(idx).value.length() != 0)
                    values.add(records.get(idx).getValue());
            }

            idxForSearchProportion = values.size();
        }
        else {
            values = attributeMember(i,(int) parent.getAttribute());

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
            
            /*
                Jika semua value cocok, maka naikan nilai proporsi untuk index
                class tertentu.
            */
            if (isMatch) {
                proportion[(int)in.classValue()]++;
            }
        }
        
//        System.out.println(values);
        double entropy = 0;

        int divider = 0;
        for (int idx = 0; idx < num_class; idx++) {
            divider += proportion[idx];

        }
        
        /*
            Rumus mencari Entropy
            Entropy(S) = -p(+) log2(p(+)) - p(-) log2(p(-))
        */
        for (int idx = 0; idx < num_class; idx++) {
            double prob = (double)proportion[idx]/divider;

            if (prob!=0 && divider !=0)
                entropy += (-1) * prob*log2(prob);
        }

        return entropy;
    }
    
    private void numDistinct(Instances i) {
        List aa = new ArrayList();
        
        for (int numAttribute=0; numAttribute < i.numAttributes(); numAttribute++) {
            for (Instance in : i) {
                if (!aa.contains(in.value(numAttribute))) {
                    aa.add(in.value(numAttribute));
                }
            }
            
            numDistinctValues.add(aa.size());
        }
    }
    
    /**
     * Mengembalikan nilai gain untuk mengurangi nilai entropy.
     * untuk pemilihan atribut, pilih nilai gain yang paling besar.
     * 
     * @param i
     * @param records
     * @return 
     */
    public double calculateGain(Instances i, List<Record> records) {
        Record parent = records.get(0);
        Record child = records.get(1);
        
        List<Record> core = new ArrayList(1);
        core.add(new Record(parent.getAttribute(), parent.getValue()));
        double gain = calculateEntropy(i, core);
        
        int numDistinct;// = i.numDistinctValues((int)child.getAttribute());
        int[] distinctAttributeCount;
        
        List<String> attributeMember;
        if (parent.getValue().length() == 0) {
            attributeMember = attributeMember(i,(int) parent.getAttribute());
            distinctAttributeCount = i.attributeStats((int)parent
                .getAttribute()).nominalCounts;
            numDistinct =  i.attributeStats((int)parent
                .getAttribute()).nominalCounts.length;
        }
        else {
            attributeMember = attributeMember(i,(int) child.getAttribute());
            
            numDistinct = numDistinctValues.get(child.attribute);
            distinctAttributeCount = new int[numDistinct];
            for(Instance in : i) {
                if (in.stringValue((int)parent.attribute).equals(parent.value)) {
                    
                    distinctAttributeCount[(int)in.value(child.attribute)]++;
                }

            }
        }
        
        int divider = 0;
        for (int attrib_idx = 0; attrib_idx < numDistinct; attrib_idx++) {
            divider += distinctAttributeCount[attrib_idx];
        }
        
        /*
            Rumus mencari gain
            Gain(S,A) = Entropy(S) - sigma((Sv/S) * Entropy(Sv))
            
            v E Values(A)
        */
        double entropy = 0;
        for (int attrib_idx=0; attrib_idx < attributeMember.size();
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
            
            if (isEmpty) parent.value = "";
        }
        
        gain-=entropy;
        
        return gain;
    }
     
    /**
     * 
     * @param L
     * @param usedAttribute
     * @return 
     */
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
    
    private int max(int[] values, int numValues) {
        int max = values[0], idx = 0;
        for (int i=1; i < numValues; i++) {
            if (max<values[i]) {
                max = values[i];
                idx = i;
            }
        }
        return idx;
    }
    /**
     * 
     * @param i
     * @param records
     * @return 
     */
    public double decideClass(Instances i, List<Record> records) {
        List<String> values = new ArrayList();
        int[] commonValue = new int[i.numClasses()];
        
        for (int idx = 0; idx < records.size(); idx++) {
            if (records.get(idx).value.length() != 0)
                values.add(records.get(idx).getValue());
        }
        double cls = 0;
        for(Instance in : i) {
            boolean isMatch = true;
            for (int rec_idx = 0; rec_idx < records.size(); rec_idx++) {
                if (!values.contains(in.stringValue((int)records.get(rec_idx).getAttribute()))) {
                    isMatch = false;
                    break;
                }
            }
            if (isMatch) {
//                cls = in.classValue();
                commonValue[(int)in.classValue()]++;
                break;
            }
        }
        
        return max(commonValue, i.numClasses());
    }
    
    @Override
    public void buildClassifier(Instances i) throws Exception {
        List gains = new ArrayList();
        numDistinct(i);
        // buat root.
        
        root = new DT(null);
        
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
        
        List <Integer> usedAttribute = new ArrayList();
        usedAttribute.add(root.getAttribute());
        
        while (true) {
            DT parent = nodeStack.peek();
            
            List attributeMember = attributeMember(i,(int) parent.getAttribute());
            
            // push semua value pada root attribut
            if (parent.getChild().isEmpty()) 
                for (int idx = attributeMember.size()-1; idx>=0; idx--) {
                    parent.getUsedValue().push(attributeMember.get(idx).toString());
                }
            
            gains.clear();
            
            // kalau udah kosong berarti semua anak sudah dibangkitkan
            if (parent.getChild().size() == i.numDistinctValues((int)parent.getAttribute()) 
                    || usedAttribute.size() == i.numAttributes()) {
                nodeStack.pop();
                usedAttribute.remove(usedAttribute.size()-1);
                if (nodeStack.isEmpty()) break;
            }
            else {
//                System.out.println(parent.getUsedValue());
                String parentValue = parent.getUsedValue().pop().toString();
                parent.tmpValue = parentValue;
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
                int selectedAttribute;
                boolean isZeroEntropy = false;
                
                // Jika sebuah value mempunyai entropi = 0, maka langsung
                // masukkan atribut kelas pada node tersebut.
                
                DT node = parent;
                List<Record> listOfNodeValue = new ArrayList();

                while (node != null) {
                    String value = node.tmpValue;
                    int attributeIdx = node.getAttribute();
                    listOfNodeValue.add(new Record(attributeIdx, value));

                    node = node.getParent();
                }

                Collections.reverse(listOfNodeValue);
                double entropy = calculateEntropy(i, listOfNodeValue);
//                System.out.println(entropy);
                if (entropy != 0 ) {
                    for (int idx = 0; idx < i.numAttributes()-1; idx++) {
                            List<Record> records = new ArrayList();
                            records.add(new Record(parent.getAttribute(), parentValue));
                            records.add(new Record(idx, ""));
                            gains.add(calculateGain(i,records));
                    }
                    // setelah gain setiap attribute diperoleh, cari yang memiliki
                    // nilai paling besar. Jadikan sebagai attribut child.
//                    System.out.println("used attribute : "+usedAttribute);
                    selectedAttribute = idxMax(gains, usedAttribute);
                }
                else {
//                    System.out.println("NOl");
                    isZeroEntropy = true;
                    selectedAttribute = i.classIndex();
                }
//                    System.out.println("used attribute : "+usedAttribute);
                // setelah gain setiap attribute diperoleh, cari yang memiliki
                // nilai paling besar. Jadikan sebagai attribut child.
                DT child = DT.addChild(parent, selectedAttribute);
                child.setValue(parentValue);
                child.getUsedAttribute().addAll(parent.getUsedAttribute());
                child.addUsedAttributeValue(selectedAttribute);
                
                // Kalau attribut yang terpilih adalah atribut kelas, maka
                // langsung buat child baru sejumlah attribut yang diassign dengan
                // kelasnya.
                // Jika tidak, maka push child kedalam stack untuk membangkitkan
                // childnya.
                if (isZeroEntropy || usedAttribute.size() == i.numAttributes()) {
                    
                    listOfNodeValue.clear();
                    node = parent;
                    while (node != null) {
                        String value = node.tmpValue;
                        int attributeIdx = node.getAttribute();
                        listOfNodeValue.add(new Record(attributeIdx, value));

                        node = node.getParent();
                    }
                    child.setAttribute(i.classIndex());
                    child.setClass(decideClass(i, listOfNodeValue));
                }
                else {
                    if (usedAttribute.size() < i.numAttributes())
                        usedAttribute.add(selectedAttribute);
                    nodeStack.push(child);
                }
            }
        }
    }
    
    @Override
    public double classifyInstance(Instance i) {
        Stack<DT> nodes = new Stack();
        
        nodes.push(root);
        
        double cls = 0;
        while(!nodes.isEmpty()) {
            DT node = nodes.pop();
            String value = i.stringValue((int)node.getAttribute());
            
            for(DT child : node.getChild()) {
                if (value.equals(child.getValue())){
                    nodes.push(child);
                    
                    if (child.getAttribute() == i.classIndex()) {
                        cls = child.getClassVal();
                    }
                }
            }
        }
        return cls;
    }
}
