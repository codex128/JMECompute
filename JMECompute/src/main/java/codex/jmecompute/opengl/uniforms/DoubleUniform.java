/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jmecompute.opengl.uniforms;

import codex.jmecompute.opengl.UniformBindUnits;
import com.jme3.asset.AssetManager;
import com.jme3.util.NativeObject;
import org.lwjgl.opengl.GL45;

/**
 *
 * @author codex
 */
public class DoubleUniform extends AbstractUniform<Double> {

    public DoubleUniform(String name) {
        super(name);
    }

    @Override
    protected void update(NativeObject shader, UniformBindUnits units) {
        GL45.glUniform1d(uniform, value);
    }
    
}
