/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dtl;

import java.io.Serializable;

/**
 *
 * @author AnwarRamadha
 */
public class Record  implements Serializable
{
    int attribute;
    String value;
    
    public Record (int attribute, String value) {
        this.attribute = attribute;
        this.value = value;
    }
    
    public int getAttribute() {
        return attribute;
    }
    
    public String getValue() {
        return value;
    }
}
