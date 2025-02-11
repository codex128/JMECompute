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
import org.lwjgl.opengl.GL30;

/**
 *
 * @author codex
 */
public class UIntBufferUniform extends AbstractArrayUniform<IntBuffer> {

    public UIntBufferUniform(String name) {
        super(name);
    }
    public UIntBufferUniform(String name, Stride divType) {
        super(name, divType);
    }
    public UIntBufferUniform(String name, String[] args) {
        super(name, args);
    }

    @Override
    public void update(NativeObject shader, UniformBindUnits units) {
        switch (stride) {
            case Single: GL30.glUniform1uiv(uniform, value); break;
            case Vec2: GL30.glUniform2uiv(uniform, value); break;
            case Vec3: GL30.glUniform3uiv(uniform, value); break;
            case Vec4: GL30.glUniform4uiv(uniform, value); break;
            default: throw new UnsupportedOperationException("Unsigned IntBuffer division size: " + stride.name());
        }
    }
    
}
