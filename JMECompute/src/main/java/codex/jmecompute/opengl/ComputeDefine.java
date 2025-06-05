/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jmecompute.opengl;

import java.util.Objects;

/**
 *
 * @author codex
 */
public class ComputeDefine {
    
    public final String name;
    private Object value;

    public ComputeDefine(String name) {
        this(name, null);
    }
    public ComputeDefine(String name, Object value) {
        this.name = name;
        setValue(value);
    }

    public final boolean setValue(Object val) {
        if (!Objects.equals(value, val)) {
            boolean enabled = isEnabled();
            Object prevVal = value;
            if (isRawType(val)) {
                value = val;
            } else {
                value = (val != null ? 1 : 0);
            }
            return (enabled || isEnabled()) && !value.equals(prevVal);
        }
        return false;
    }

    public Object getValue() {
        return value;
    }

    public boolean isEnabled() {
        return value != null && !value.equals(false) && !value.equals(0)
                && !value.equals(0.0f) && !value.equals("");
    }

    public static boolean isRawType(Object val) {
        return val instanceof Boolean || val instanceof Integer
                || val instanceof Float || val instanceof String;
    }

}
