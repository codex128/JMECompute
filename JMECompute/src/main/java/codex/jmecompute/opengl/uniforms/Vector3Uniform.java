/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jmecompute.opengl.uniforms;

import codex.jmecompute.opengl.UniformBindUnits;
import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.util.NativeObject;
import java.io.IOException;
import org.lwjgl.opengl.GL20;

/**
 *
 * @author codex
 */
public class Vector3Uniform extends AbstractUniform<Vector3f> {

    public Vector3Uniform(String name) {
        super(name);
        this.useEqualsMethod = true;
    }

    @Override
    protected void update(NativeObject shader, UniformBindUnits units) {
        GL20.glUniform3f(uniform, value.x, value.y, value.z);
    }
    
}
