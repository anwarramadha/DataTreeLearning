/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dtl;
import java.util.*;
import weka.core.Instance;
/**
 *
 * @author AnwarRamadha
 */
public class DT
{
    Instance ins;
    private final DT parent;
    private final List<DT> child = new ArrayList();
    private String id;
    
    public DT(DT parent) {
        this.parent = parent;
    }
    
    public DT getParent() {
        return parent;
    }
    
    public List<DT> getChild(){
        return child;
    }
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
            
    public static DT addChild(DT parent, String id) {
        DT node = new DT(parent);
        node.setId(id);
        parent.getChild().add(node);
        return node;
    }
    
    public static void printTree(DT node, String appender) {
        System.out.println(appender + node.getId());
        for (DT each : node.getChild()) {
         printTree(each, appender + appender);
        }
    }
}
