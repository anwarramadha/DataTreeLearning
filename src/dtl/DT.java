/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dtl;
import java.util.*;
/**
 *
 * @author AnwarRamadha
 */
public class DT
{
    private final DT parent;
    private final List<DT> child = new ArrayList();
    private List<Double> usedAttribute = new ArrayList();
    private Stack<String> usedValue = new Stack();
    private double attribute;
    private String value;
    private String classVal;
    private int numChild = 0;
    public String aaa;
    
    
    public DT(DT parent) {
        this.parent = parent;
    }
    
    public DT getParent() {
        return parent;
    }
    
    public List<DT> getChild(){
        return child;
    }
    public double getAttribute() {
        return attribute;
    }
    
    public void setAttribute(double attribute) {
        this.attribute = attribute;
    }
            
    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
    public String getClassVal() {
        return classVal;
    }
    
    public void setClass(String classVal) {
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
    
    public static DT addChild(DT parent, double attribute) {
        DT node = new DT(parent);
        node.setAttribute(attribute);
        parent.getChild().add(node);
        return node;
    }
    
    public static void printTree(DT node, String appender) {
        System.out.println(appender + node.getAttribute());
        for (DT each : node.getChild()) {
            if (each.getClassVal() != null)
                printTree(each, appender + appender+" "+each.getClassVal());
            else
                printTree(each, appender + appender);
        }
    }
}
