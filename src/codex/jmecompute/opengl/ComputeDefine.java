/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jmecompute.opengl;

/**
 *
 * @author codex
 */
public class ComputeDefine {
        
    public final String paramName;
    public final String defineName;
    private Object value;

    public ComputeDefine(String defineName) {
        this(null, defineName);
    }
    public ComputeDefine(String paramName, String defineName) {
        this.paramName = paramName;
        this.defineName = defineName;
    }
    public ComputeDefine(String paramName, String defineName, Object value) {
        this.paramName = paramName;
        this.defineName = defineName;
        setValue(value);
    }

    public final boolean setValue(Object val) {
        if (value != val && (value == null || !value.equals(val))) {
            boolean enabled = isEnabled();
            if (isRawType(val)) {
                value = val;
            } else {
                value = (val != null ? 1 : 0);
            }
            return enabled || isEnabled();
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
