/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jmecompute.opengl.uniforms;

import codex.jmecompute.opengl.UniformBindUnits;
import com.jme3.asset.AssetManager;
import com.jme3.util.NativeObject;
import java.io.IOException;
import org.lwjgl.opengl.GL30;

/**
 *
 * @author codex
 */
public class UIntUniform extends AbstractUniform<Integer> {

    public UIntUniform(String name) {
        super(name);
    }

    @Override
    protected void update(NativeObject shader, UniformBindUnits units) {
        GL30.glUniform1ui(uniform, value);
    }

    @Override
    public Object parse(AssetManager assetManager, String[] values) throws IOException {
        return Integer.parseUnsignedInt(values[0]);
    }
    
}
