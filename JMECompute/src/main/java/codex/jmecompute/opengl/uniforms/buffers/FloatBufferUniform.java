/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jmecompute.opengl.uniforms.buffers;

import codex.jmecompute.Stride;
import codex.jmecompute.opengl.UniformBindUnits;
import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix3f;
import com.jme3.math.Matrix4f;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import com.jme3.util.BufferUtils;
import com.jme3.util.NativeObject;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL21;

/**
 *
 * @author codex
 */
public class FloatBufferUniform extends AbstractArrayUniform<FloatBuffer> {
    
    public static final BiFunction<Float, Integer, Float> floats = (f, i) -> f;
    public static final BiFunction<Vector2f, Integer, Float> vec2 = (v, i) -> i == 0 ? v.x : v.y;
    public static final BiFunction<Vector3f, Integer, Float> vec3 = (v, i) -> v.get(i);
    public static final BiFunction<Vector4f, Integer, Float> vec4 = (v, i) -> v.get(i);
    public static final BiFunction<ColorRGBA, Integer, Float> color = (c, i) -> getColorComponent(c, i);
    public static final BiFunction<Matrix3f, Integer, Float> mat3Column = (m, i) -> m.get(i % 3, i / 3);
    public static final BiFunction<Matrix3f, Integer, Float> mat3Row = (m, i) -> m.get(i / 3, i % 3);
    public static final BiFunction<Matrix4f, Integer, Float> mat4Column = (m, i) -> m.get(i % 4, i / 4);
    public static final BiFunction<Matrix4f, Integer, Float> mat4Row = (m, i) -> m.get(i / 4, i % 4);
    
    private FloatBuffer miscBuf;
    
    public FloatBufferUniform(String name) {
        super(name);
    }
    public FloatBufferUniform(String name, Stride divType) {
        super(name, divType);
    }
    public FloatBufferUniform(String name, String[] args) {
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
            default: throw new UnsupportedOperationException("FloatBuffer division size: " + stride.name());
        }
    }
    
    @Override
    public boolean set(Object value) {
        if (value == null || value instanceof FloatBuffer);
        else if (value instanceof float[]) {
            value = fillMiscBuffer((float[])value);
        } else if (value instanceof Float[]) {
            value = fillMiscBuffer((Float[])value, 1, floats);
        } else if (value instanceof Vector2f[]) {
            value = fillMiscBuffer((Vector2f[])value, 2, vec2);
        } else if (value instanceof Vector3f[]) {
            value = fillMiscBuffer((Vector3f[])value, 3, vec3);
        } else if (value instanceof Vector4f[]) {
            value = fillMiscBuffer((Vector4f[])value, 4, vec4);
        } else if (value instanceof ColorRGBA[]) {
            value = fillMiscBuffer((ColorRGBA[])value, 4, color);
        } else if (value instanceof Matrix3f[]) {
            value = fillMiscBuffer((Matrix3f[])value, 9, columnMajor ? mat3Column : mat3Row);
        } else if (value instanceof Matrix4f[]) {
            value = fillMiscBuffer((Matrix4f[])value, 16, columnMajor ? mat4Column : mat4Row);
        }
        return super.set(value);
    }
    private FloatBuffer fillMiscBuffer(float[] array) {
        miscBuf = BufferUtils.ensureLargeEnough(miscBuf, array.length);
        miscBuf.rewind();
        for (float f : array) {
            miscBuf.put(f);
        }
        miscBuf.flip();
        return miscBuf;
    }
    private <T> FloatBuffer fillMiscBuffer(T[] array, int components, BiFunction<T, Integer, Float> mapper) {
        miscBuf = BufferUtils.ensureLargeEnough(miscBuf, array.length * components);
        miscBuf.rewind();
        int i = 0;
        for (T t : array) {
            for (int j = 0; j < components; j++) {
                miscBuf.put(i++, mapper.apply(t, j));
            }
        }
        miscBuf.flip();
        return miscBuf;
    }

    @Override
    public Object parse(AssetManager assetManager, String[] values) throws IOException {
       FloatBuffer buf = BufferUtils.createFloatBuffer(values.length);
        for (int i = 0; i < values.length; i++) {
            buf.put(i, Float.parseFloat(values[i]));
        }
        return buf;
    }
    
    private static float getColorComponent(ColorRGBA color, int i) {
        switch (i) {
            case 0: return color.r;
            case 1: return color.g;
            case 2: return color.b;
            case 3: return color.a;
            default: throw new IndexOutOfBoundsException("Index " + i + " out of bounds for length 4.");
        }
    }
    
}
