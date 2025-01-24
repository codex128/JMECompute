/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jmecompute.opengl.uniforms.textures;

import codex.jmecompute.opengl.UniformBindUnits;
import com.jme3.asset.AssetManager;
import com.jme3.texture.Texture;
import com.jme3.util.NativeObject;
import java.io.IOException;
import java.util.Arrays;

/**
 *
 * @author codex
 */
public class TextureArrayUniform extends AbstractTextureUniform<Texture[]> {

    public TextureArrayUniform(String name) {
        super(name);
    }
    public TextureArrayUniform(String name, String[] args) {
        super(name, args);
    }

    @Override
    protected void update(NativeObject shader, UniformBindUnits units) {
        uploadTextures(Arrays.stream(value), value.length, units);
    }

    @Override
    public Object parse(AssetManager assetManager, String[] values) throws IOException {
        Texture[] textures = new Texture[values.length];
        for (int i = 0; i < values.length; i++) {
            textures[i] = assetManager.loadTexture(values[i]);
        }
        return textures;
    }
    
}
