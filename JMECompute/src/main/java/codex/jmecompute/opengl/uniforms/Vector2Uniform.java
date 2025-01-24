/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jmecompute.opengl.uniforms;

import codex.jmecompute.opengl.UniformBindUnits;
import com.jme3.asset.AssetManager;
import com.jme3.math.Vector2f;
import com.jme3.util.NativeObject;
import java.io.IOException;
import org.lwjgl.opengl.GL20;

/**
 *
 * @author codex
 */
public class Vector2Uniform extends AbstractUniform<Vector2f> {

    public Vector2Uniform(String name) {
        super(name);
        this.useEqualsMethod = true;
    }

    @Override
    protected void update(NativeObject shader, UniformBindUnits units) {
        GL20.glUniform2f(uniform, value.x, value.y);
    }

    @Override
    public Object parse(AssetManager assetManager, String[] values) throws IOException {
        requireMinParseValues(values, 2);
        return new Vector2f(Float.parseFloat(values[0]), Float.parseFloat(values[1]));
    }
    
}
