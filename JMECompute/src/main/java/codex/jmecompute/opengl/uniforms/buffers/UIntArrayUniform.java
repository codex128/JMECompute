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
import org.lwjgl.opengl.GL30;

/**
 *
 * @author codex
 */
public class UIntArrayUniform extends AbstractArrayUniform<int[]> {

    public UIntArrayUniform(String name) {
        super(name);
    }
    public UIntArrayUniform(String name, Stride divType) {
        super(name, divType);
    }
    public UIntArrayUniform(String name, String[] args) {
        super(name, args);
    }

    @Override
    public void update(NativeObject shader, UniformBindUnits units) {
        switch (stride) {
            case Single: GL30.glUniform1uiv(uniform, value); break;
            case Vec2: GL30.glUniform2uiv(uniform, value); break;
            case Vec3: GL30.glUniform3uiv(uniform, value); break;
            case Vec4: GL30.glUniform4uiv(uniform, value); break;
            default: throw new UnsupportedOperationException("Unsigned int[] division size: " + stride.name());
        }
    }

    @Override
    public Object parse(AssetManager assetManager, String[] values) throws IOException {
        int[] array = new int[values.length];
        for (int i = 0; i < values.length; i++) {
            array[i] = Integer.parseUnsignedInt(values[i]);
        }
        return array;
    }
    
}
