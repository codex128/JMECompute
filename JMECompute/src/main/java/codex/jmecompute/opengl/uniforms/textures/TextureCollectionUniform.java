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
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author codex
 */
public class TextureCollectionUniform extends AbstractTextureUniform<Collection<Texture>> {

    public TextureCollectionUniform(String name) {
        super(name);
    }
    public TextureCollectionUniform(String name, String[] args) {
        super(name, args);
    }

    @Override
    protected void update(NativeObject shader, UniformBindUnits units) {
        this.uploadTextures(value.stream(), value.size(), units);
    }

    @Override
    public Object parse(AssetManager assetManager, String[] values) throws IOException {
        ArrayList<Texture> textures = new ArrayList<>(values.length);
        for (String v : values) {
            textures.add(assetManager.loadTexture(v));
        }
        return textures;
    }
    
}
