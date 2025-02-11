/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jmecompute.opengl.uniforms.buffers;

import codex.jmecompute.Stride;
import codex.jmecompute.opengl.UniformBindUnits;
import com.jme3.asset.AssetManager;
import com.jme3.util.NativeObject;
import java.io.IOException;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;

/**
 *
 * @author codex
 */
public class IntBufferUniform extends AbstractArrayUniform<IntBuffer> {

    public IntBufferUniform(String name) {
        super(name);
    }
    public IntBufferUniform(String name, Stride divType) {
        super(name, divType);
    }
    public IntBufferUniform(String name, String[] args) {
        super(name, args);
    }

    @Override
    public void update(NativeObject shader, UniformBindUnits units) {
        switch (stride) {
            case Single: GL20.glUniform1iv(uniform, value); break;
            case Vec2: GL20.glUniform2iv(uniform, value); break;
            case Vec3: GL20.glUniform3iv(uniform, value); break;
            case Vec4: GL20.glUniform4iv(uniform, value); break;
            default: throw new UnsupportedOperationException("IntBuffer division size: " + stride.name());
        }
    }
    
}
