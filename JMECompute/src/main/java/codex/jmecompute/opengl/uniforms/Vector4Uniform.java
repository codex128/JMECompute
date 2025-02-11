/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jmecompute.opengl.uniforms;

import codex.jmecompute.opengl.UniformBindUnits;
import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector4f;
import com.jme3.util.NativeObject;
import java.io.IOException;
import org.lwjgl.opengl.GL20;

/**
 *
 * @author codex
 */
public class Vector4Uniform extends AbstractUniform<Vector4f> {
    
    private Vector4f colorVec;
    
    public Vector4Uniform(String name) {
        super(name);
        this.useEqualsMethod = true;
    }

    @Override
    protected void update(NativeObject shader, UniformBindUnits units) {
        GL20.glUniform4f(uniform, value.x, value.y, value.z, value.w);
    }
    
    @Override
    public boolean set(Object value) {
        if (value instanceof ColorRGBA) {
            if (colorVec == null) {
                colorVec = new Vector4f();
            }
            ColorRGBA color = (ColorRGBA)value;
            value = colorVec.set(color.r, color.g, color.b, color.a);
        }
        return super.set(value);
    }
    
}
