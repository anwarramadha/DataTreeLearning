/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dtl;
import java.io.Serializable;
import java.util.*;
/**
 *
 * @author AnwarRamadha
 */
public class DT  implements Serializable
{
    private final DT parent;
    private final List<DT> child = new ArrayList();
    private final List<Double> usedAttribute = new ArrayList();
    private Stack<String> usedValue = new Stack();
    private int attribute;
    private String value;
    private double classVal = -1;
    private int numChild = 0;
    public String tmpValue;
    
    
    public DT(DT parent) {
        this.parent = parent;
    }
    
    public DT getParent() {
        return parent;
    }
    
    public List<DT> getChild(){
        return child;
    }
    public int getAttribute() {
        return attribute;
    }
    
    public void setAttribute(int attribute) {
        this.attribute = attribute;
    }
            
    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
    public double getClassVal() {
        return classVal;
    }
    
    public void setClass(double classVal) {
        this.classVal = classVal;
    }
    
    public List getUsedAttribute() {
        return usedAttribute;
    }
    
    public void addUsedAttributeValue(double value) {
        usedAttribute.add(value);
    }
    
    public Stack getUsedValue() {
        return usedValue;
    }
    
    public void setUsedValue(Stack value) {
        usedValue = value;
    }
    
    public int getNumChild() {
        return numChild;
    }
    
    public void addNumChild() {
        numChild++;
    }
    
    public static DT addChild(DT parent, int attribute) {
        DT node = new DT(parent);
        node.setAttribute(attribute);
        parent.getChild().add(node);
        return node;
    }
    
    public static void printTree(DT node, String appender) {
        System.out.println(appender + node.getAttribute());
        for (DT each : node.getChild()) {
            if (each.getClassVal() != -1)
                printTree(each, appender + appender+" "+each.getClassVal());
            else
                printTree(each, appender + appender);
        }
    }
}
