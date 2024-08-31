/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jmecompute.opengl;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix3f;
import com.jme3.math.Matrix4f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import com.jme3.shader.VarType;
import static com.jme3.shader.VarType.FloatArray;
import static com.jme3.shader.VarType.IntArray;
import static com.jme3.shader.VarType.Matrix3;
import static com.jme3.shader.VarType.Matrix3Array;
import static com.jme3.shader.VarType.Matrix4;
import static com.jme3.shader.VarType.Matrix4Array;
import static com.jme3.shader.VarType.Vector2;
import static com.jme3.shader.VarType.Vector2Array;
import static com.jme3.shader.VarType.Vector3;
import static com.jme3.shader.VarType.Vector3Array;
import static com.jme3.shader.VarType.Vector4;
import static com.jme3.shader.VarType.Vector4Array;
import com.jme3.util.BufferUtils;
import com.jme3.util.TempVars;
import java.lang.reflect.InvocationTargetException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 *
 * @author codex
 */
public class ComputeUniform {
    
    private final String name;
    private final VarType type;
    private Object value;
    private FloatBuffer buffer;
    private boolean updateFlag = true;

    public ComputeUniform(String name, VarType type) {
        this.name = name;
        this.type = type;
    }
    
    public void setValue(Object val) {
        setValue(type, val);
    }
    public void setValue(VarType type, Object val) {
        /**
         * Copyright (c) 2009-2024 jMonkeyEngine
         * All rights reserved.
         */
        if (val == null) {
            if (value != null) {
                updateFlag = true;
            }
            value = null;
            return;
        }
        if (this.type != type) {
            throw new IllegalArgumentException("Expected "+this.type+", recieved "+type);
        }
        switch (this.type) {
            case Matrix3:
                if (val.equals(this.value)) {
                    return;
                }
                Matrix3f m3 = (Matrix3f) val;
                if (buffer == null) {
                    buffer = BufferUtils.createFloatBuffer(9);
                }
                m3.fillFloatBuffer(buffer, true);
                buffer.clear();
                if (this.value == null) {
                    this.value = new Matrix3f(m3);
                } else {
                    ((Matrix3f)this.value).set(m3);
                }
                updateFlag = true;
                break;
            case Matrix4:
                if (val.equals(this.value)) {
                    return;
                }
                Matrix4f m4 = (Matrix4f) val;
                if (buffer == null) {
                    buffer = BufferUtils.createFloatBuffer(16);
                }
                m4.fillFloatBuffer(buffer, true);
                buffer.clear();
                if (this.value == null) {
                    this.value = new Matrix4f(m4);
                } else {
                    ((Matrix4f)this.value).copy(m4);
                }
                updateFlag = true;
                break;
            case IntArray:
                int[] ia = (int[]) val;
                if (this.value == null) {
                    this.value = BufferUtils.createIntBuffer(ia);
                } else {
                    this.value = BufferUtils.ensureLargeEnough((IntBuffer)this.value, ia.length);
                    ((IntBuffer)this.value).put(ia);
                }
                ((IntBuffer)this.value).clear();
                updateFlag = true;
                break;
            case FloatArray:
                float[] fa = (float[]) val;
                if (buffer == null) {
                    buffer = BufferUtils.createFloatBuffer(fa);
                } else {
                    buffer = BufferUtils.ensureLargeEnough(buffer, fa.length);
                    buffer.put(fa);
                }
                buffer.clear();
                updateFlag = true;
                break;
            case Vector2Array:
                Vector2f[] v2a = (Vector2f[]) val;
                if (buffer == null) {
                    buffer = BufferUtils.createFloatBuffer(v2a);
                } else {
                    buffer = BufferUtils.ensureLargeEnough(buffer, v2a.length * 2);
                    for (int i = 0; i < v2a.length; i++) {
                        BufferUtils.setInBuffer(v2a[i], buffer, i);
                    }
                }
                buffer.clear();
                updateFlag = true;
                break;
            case Vector3Array:
                Vector3f[] v3a = (Vector3f[]) val;
                if (buffer == null) {
                    buffer = BufferUtils.createFloatBuffer(v3a);
                } else {
                    buffer = BufferUtils.ensureLargeEnough(buffer, v3a.length * 3);
                    for (int i = 0; i < v3a.length; i++) {
                        BufferUtils.setInBuffer(v3a[i], buffer, i);
                    }
                }
                buffer.clear();
                updateFlag = true;
                break;
            case Vector4Array:
                Vector4f[] v4a = (Vector4f[]) val;
                if (buffer == null) {
                    buffer = BufferUtils.createFloatBuffer(v4a);
                } else {
                    buffer = BufferUtils.ensureLargeEnough(buffer, v4a.length * 4);
                    for (int i = 0; i < v4a.length; i++) {
                        BufferUtils.setInBuffer(v4a[i], buffer, i);
                    }
                }
                buffer.clear();
                updateFlag = true;
                break;
            case Matrix3Array:
                Matrix3f[] m3a = (Matrix3f[]) val;
                if (buffer == null) {
                    buffer = BufferUtils.createFloatBuffer(m3a.length * 9);
                } else {
                    buffer = BufferUtils.ensureLargeEnough(buffer, m3a.length * 9);
                }
                for (int i = 0; i < m3a.length; i++) {
                    m3a[i].fillFloatBuffer(buffer, true);
                }
                buffer.clear();
                break;
            case Matrix4Array:
                Matrix4f[] m4a = (Matrix4f[]) val;
                if (buffer == null) {
                    buffer = BufferUtils.createFloatBuffer(m4a.length * 16);
                } else {
                    buffer = BufferUtils.ensureLargeEnough(buffer, m4a.length * 16);
                }
                for (int i = 0; i < m4a.length; i++) {
                    m4a[i].fillFloatBuffer(buffer, true);
                }
                buffer.clear();
                updateFlag = true;
                break;
            case Vector2:
                if (val.equals(this.value)) {
                    return;
                }
                if (this.value == null) {
                    this.value = new Vector2f((Vector2f) val);
                } else {
                    ((Vector2f) this.value).set((Vector2f) val);
                }
                break;
            case Vector3:
                if (val.equals(this.value)) {
                    return;
                }
                if (this.value == null) {
                    this.value = new Vector3f((Vector3f) val);
                } else {
                    ((Vector3f) this.value).set((Vector3f) val);
                }
                updateFlag = true;
                break;
            case Vector4:
                if (val.equals(this.value)) {
                    return;
                }
                TempVars vars = TempVars.get();
                Vector4f vec4 = vars.vect4f1;
                //handle the null case
                if (this.value == null) {
                    try {
                        this.value = val.getClass().getDeclaredConstructor().newInstance();
                    } catch (InstantiationException | IllegalAccessException
                            | IllegalArgumentException | InvocationTargetException
                            | NoSuchMethodException | SecurityException e) {
                        throw new IllegalArgumentException("Cannot instantiate param of class " + val.getClass().getCanonicalName(), e);
                    }
                }
                //feed the pivot vec 4 with the correct value
                if (val instanceof ColorRGBA) {
                    ColorRGBA c = (ColorRGBA) val;
                    vec4.set(c.r, c.g, c.b, c.a);
                } else if (val instanceof Vector4f) {
                    vec4.set((Vector4f) val);
                } else {
                    Quaternion q = (Quaternion) val;
                    vec4.set(q.getX(), q.getY(), q.getZ(), q.getW());
                }

                //feed this.value with the collected values.
                if (this.value instanceof ColorRGBA) {
                    ((ColorRGBA) this.value).set(vec4.x, vec4.y, vec4.z, vec4.w);
                } else if (this.value instanceof Vector4f) {
                    ((Vector4f) this.value).set(vec4);
                } else {
                    ((Quaternion) this.value).set(vec4.x, vec4.y, vec4.z, vec4.w);
                }
                vars.release();
                updateFlag = true;
                break;
            case Boolean:
            case Int:
            case Float:
                if (value != val) {
                    value = val;
                    updateFlag = true;
                }
                break;
            default:
                this.value = val;
                updateFlag = true;
        }
    }
    public void clearUpdateNeeded() {
        updateFlag = false;
    }
    
    public String getName() {
        return name;
    }
    public VarType getType() {
        return type;
    }
    public Object getValue() {
        return value;
    }
    public FloatBuffer getBuffer() {
        return buffer;
    }
    public boolean isUpdateNeeded() {
        return updateFlag;
    }
    
}
