/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jmecompute.opengl.uniforms.textures;

import codex.jmecompute.opengl.UniformBindUnits;
import com.jme3.asset.AssetManager;
import com.jme3.texture.TextureImage;
import com.jme3.util.NativeObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author codex
 */
public class ImageCollectionUniform extends AbstractTextureUniform<Collection<TextureImage>> {

    public ImageCollectionUniform(String name) {
        super(name);
    }
    public ImageCollectionUniform(String name, String[] args) {
        super(name, args);
    }

    @Override
    protected void update(NativeObject shader, UniformBindUnits units) {
        uploadImages(value.stream(), value.size(), units);
    }
    
}
