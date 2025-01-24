/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jmecompute.opengl.uniforms.bufferobjects;

import codex.jmecompute.opengl.GLRenderUtils;
import codex.jmecompute.opengl.UniformBindUnits;
import codex.jmecompute.opengl.uniforms.AbstractUniform;
import com.jme3.shader.bufferobject.BufferObject;
import com.jme3.util.NativeObject;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL45;
import org.lwjgl.system.MemoryStack;

/**
 *
 * @author codex
 * @param <T>
 * @param <R>
 */
public abstract class ShaderStorageBufferObject <T extends NativeObject, R> extends AbstractUniform<T> {
    
    protected BufferObject.AccessHint access;
    protected BufferObject.NatureHint nature;
    
    public ShaderStorageBufferObject(String name) {
        this(name, BufferObject.AccessHint.Static, BufferObject.NatureHint.Copy);
    }
    public ShaderStorageBufferObject(String name, BufferObject.AccessHint access, BufferObject.NatureHint nature) {
        super(name);
        this.access = access;
        this.nature = nature;
        this.persistent = true;
    }

    @Override
    protected void update(NativeObject shader, UniformBindUnits units) {
        if (value.isUpdateNeeded()) {
            updateBufferData();
        }
        int block = GL45.glGetProgramResourceIndex(shader.getId(), GL45.GL_SHADER_STORAGE_BLOCK, name);
        GL45.glShaderStorageBlockBinding(shader.getId(), block, units.peekBufferUnit());
        GLRenderUtils.get().bindUnknownBufferObject(GL45.GL_SHADER_STORAGE_BUFFER, units.pollBufferUnit(), value);
    }
    
    protected abstract R getData();
    
    protected void updateBufferData() {
        int usage = GLRenderUtils.get().resolveUsageHint(access, nature);
        if (usage < 0) {
            throw new IllegalArgumentException("Cannot upload CPU-only buffer as shader storage.");
        }
        if (value.getId() == 0) {
            createData();
        }
        GL45.glBindBuffer(GL45.GL_SHADER_STORAGE_BUFFER, value.getId());
        bindData(usage);
        GL45.glBindBuffer(GL45.GL_SHADER_STORAGE_BUFFER, 0);
        value.clearUpdateNeeded();
    }
    protected void createData() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer i = stack.callocInt(1);
            GL15.glGenBuffers(i);
            value.setId(i.get(0));
            GLRenderUtils.get().registerNative(value);
        }
    }
    protected void bindData(int usage) {
        R buf = getData();
        if (buf == null) {
            throw new NullPointerException("Buffer data cannot be null.");
        } else if (buf instanceof ByteBuffer) {
            GL45.glBufferData(GL45.GL_SHADER_STORAGE_BUFFER, (ByteBuffer)buf, usage);
        } else if (buf instanceof ShortBuffer) {
            GL45.glBufferData(GL45.GL_SHADER_STORAGE_BUFFER, (ShortBuffer)buf, usage);
        } else if (buf instanceof IntBuffer) {
            GL45.glBufferData(GL45.GL_SHADER_STORAGE_BUFFER, (IntBuffer)buf, usage);
        } else if (buf instanceof FloatBuffer) {
            GL45.glBufferData(GL45.GL_SHADER_STORAGE_BUFFER, (FloatBuffer)buf, usage);
        } else if (buf instanceof DoubleBuffer) {
            GL45.glBufferData(GL45.GL_SHADER_STORAGE_BUFFER, (DoubleBuffer)buf, usage);
        } else if (buf instanceof LongBuffer) {
            GL45.glBufferData(GL45.GL_SHADER_STORAGE_BUFFER, (LongBuffer)buf, usage);
        } else if (buf instanceof short[]) {
            GL45.glBufferData(GL45.GL_SHADER_STORAGE_BUFFER, (short[])buf, usage);
        } else if (buf instanceof int[]) {
            GL45.glBufferData(GL45.GL_SHADER_STORAGE_BUFFER, (int[])buf, usage);
        } else if (buf instanceof float[]) {
            GL45.glBufferData(GL45.GL_SHADER_STORAGE_BUFFER, (float[])buf, usage);
        } else if (buf instanceof double[]) {
            GL45.glBufferData(GL45.GL_SHADER_STORAGE_BUFFER, (double[])buf, usage);
        } else if (buf instanceof long[]) {
            GL45.glBufferData(GL45.GL_SHADER_STORAGE_BUFFER, (long[])buf, usage);
        } else if (buf instanceof Long) {
            GL45.glBufferData(GL45.GL_SHADER_STORAGE_BUFFER, (Long)buf, usage);
        } else {
            throw new UnsupportedOperationException("Cannot bind with " + buf.getClass().getName());
        }
    }
    
}
