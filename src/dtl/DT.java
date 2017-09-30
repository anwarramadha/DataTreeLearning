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
    private double attribute;
    private String value;
    
    
    public DT(DT parent) {
        this.parent = parent;
    }
    
    public DT getParent() {
        return parent;
    }
    
    public List<DT> getChild(){
        return child;
    }
    public double getId() {
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
    
    public static DT addChild(DT parent, double attribute, double value) {
        DT node = new DT(parent);
        node.setAttribute(attribute);
        parent.getChild().add(node);
        return node;
    }
    
    public static void printTree(DT node, int appender) {
        System.out.println(appender + node.getId());
        for (DT each : node.getChild()) {
         printTree(each, appender + appender);
        }
    }
}
