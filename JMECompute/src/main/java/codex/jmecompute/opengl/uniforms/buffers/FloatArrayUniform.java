/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jmecompute.opengl.uniforms.buffers;

import codex.jmecompute.Stride;
import codex.jmecompute.opengl.GLRenderUtils;
import codex.jmecompute.opengl.UniformBindUnits;
import static codex.jmecompute.opengl.uniforms.buffers.FloatBufferUniform.*;
import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix3f;
import com.jme3.math.Matrix4f;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import com.jme3.util.NativeObject;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.function.BiFunction;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL21;

/**
 *
 * @author codex
 */
public class FloatArrayUniform extends AbstractArrayUniform<float[]> {
    
    private float[] miscArray;
    
    public FloatArrayUniform(String name) {
        super(name);
    }
    public FloatArrayUniform(String name, Stride divType) {
        super(name, divType);
    }
    public FloatArrayUniform(String name, String[] args) {
        super(name, args);
    }

    @Override
    public void update(NativeObject shader, UniformBindUnits units) {
        switch (stride) {
            case Single: GL20.glUniform1fv(uniform, value); break;
            case Vec2: GL20.glUniform2fv(uniform, value); break;
            case Vec3: GL20.glUniform3fv(uniform, value); break;
            case Vec4: GL20.glUniform4fv(uniform, value); break;
            case Mat2: GL20.glUniformMatrix2fv(uniform, transposeMatrices, value); break;
            case Mat2x3: GL21.glUniformMatrix2x3fv(uniform, transposeMatrices, value); break;
            case Mat2x4: GL21.glUniformMatrix2x4fv(uniform, transposeMatrices, value); break;
            case Mat3: GL20.glUniformMatrix3fv(uniform, transposeMatrices, value); break;
            case Mat3x2: GL21.glUniformMatrix3x2fv(uniform, transposeMatrices, value); break;
            case Mat3x4: GL21.glUniformMatrix3x4fv(uniform, transposeMatrices, value); break;
            case Mat4: GL20.glUniformMatrix4fv(uniform, transposeMatrices, value); break;
            case Mat4x2: GL21.glUniformMatrix4x2fv(uniform, transposeMatrices, value); break;
            case Mat4x3: GL21.glUniformMatrix4x3fv(uniform, transposeMatrices, value); break;
            default: throw new UnsupportedOperationException("float[] division size: " + stride.name());
        }
    }
    
    @Override
    public boolean set(Object value) {
        if (value == null || value instanceof float[]);
        else if (value instanceof FloatBuffer) {
            value = fillMiscArray((FloatBuffer)value);
        } else if (value instanceof Float[]) {
            value = fillMiscArray((Float[])value, 1, floats);
        } else if (value instanceof Vector2f[]) {
            value = fillMiscArray((Vector2f[])value, 2, vec2);
        } else if (value instanceof Vector3f[]) {
            value = fillMiscArray((Vector3f[])value, 3, vec3);
        } else if (value instanceof Vector4f[]) {
            value = fillMiscArray((Vector4f[])value, 4, vec4);
        } else if (value instanceof ColorRGBA[]) {
            value = fillMiscArray((ColorRGBA[])value, 4, color);
        } else if (value instanceof Matrix3f[]) {
            value = fillMiscArray((Matrix3f[])value, 9, columnMajor ? mat3Column : mat3Row);
        } else if (value instanceof Matrix4f[]) {
            value = fillMiscArray((Matrix4f[])value, 16, columnMajor ? mat4Column : mat4Row);
        }
        return super.set(value);
    }
    
    private float[] ensureArrayLargeEnough(float[] array, int length) {
        if (array == null || array.length < length) {
            array = new float[length];
        }
        return array;
    }
    private <T> float[] fillMiscArray(T[] array, int components, BiFunction<T, Integer, Float> mapper) {
        miscArray = ensureArrayLargeEnough(miscArray, array.length * components);
        int i = 0;
        for (T t : array) {
            for (int j = 0; j < components; j++) {
                miscArray[i++] = mapper.apply(t, j);
            }
        }
        return miscArray;
    }
    private <T> float[] fillMiscArray(FloatBuffer buffer) {
        miscArray = ensureArrayLargeEnough(miscArray, buffer.limit());
        for (int i = 0; i < buffer.limit(); i++) {
            miscArray[i] = buffer.get(i);
        }
        return miscArray;
    }
    
}
