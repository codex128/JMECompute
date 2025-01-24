/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jmecompute.opengl.uniforms.bufferobjects;

import codex.jmecompute.opengl.GLRenderUtils;
import codex.jmecompute.opengl.UniformBindUnits;
import codex.jmecompute.opengl.uniforms.AbstractUniform;
import com.jme3.shader.bufferobject.BufferObject;
import com.jme3.shader.bufferobject.BufferRegion;
import com.jme3.shader.bufferobject.DirtyRegionsIterator;
import com.jme3.util.NativeObject;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL45;
import org.lwjgl.system.MemoryStack;

/**
 *
 * @author codex
 */
public class UniformBufferObject extends AbstractUniform<BufferObject> {

    public UniformBufferObject(String name) {
        super(name);
        this.persistent = true;
    }

    @Override
    protected void update(NativeObject shader, UniformBindUnits units) {
        if (value.isUpdateNeeded()) {
            updateBufferData();
        }
        int block = GL45.glGetUniformBlockIndex(shader.getId(), name);
        GL45.glUniformBlockBinding(shader.getId(), block, units.peekBufferUnit());
        GLRenderUtils.get().bindBufferObject(GL45.GL_UNIFORM_BUFFER, units.pollBufferUnit(), value);
    }
    
    private void updateBufferData() {
        /*
         * Copyright (c) 2009-2024 jMonkeyEngine
         * All rights reserved.
         */
        int usage = GLRenderUtils.get().resolveUsageHint(value);
        if (usage < 0) {
            throw new IllegalArgumentException("Cannot upload CPU-only buffer as a shader uniform.");
        }
        if (value.getId() == 0) {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                IntBuffer i = stack.callocInt(1);
                GL15.glGenBuffers(i);
                value.setId(i.get(0));
                GLRenderUtils.get().registerNative(value);
            }
        }
        for (DirtyRegionsIterator it = value.getDirtyRegions(); it.hasNext();) {
            BufferRegion reg = it.next();
            GL45.glBindBuffer(GL45.GL_UNIFORM_BUFFER, value.getBinding());
            if (reg.isFullBufferRegion()) {
                ByteBuffer bbf = value.getData();
                GL45.glBufferData(GL45.GL_UNIFORM_BUFFER, bbf, usage);
                reg.clearDirty();
                break;
            } else {
                GL45.glBufferSubData(GL45.GL_UNIFORM_BUFFER, reg.getStart(), reg.getData());
                reg.clearDirty();
            }
        }
        GL45.glBindBuffer(GL45.GL_UNIFORM_BUFFER, 0);
        value.clearUpdateNeeded();
    }
    
}
