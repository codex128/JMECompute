/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jmecompute.opengl.uniforms.textures;

import codex.jmecompute.opengl.UniformBindUnits;
import com.jme3.asset.AssetManager;
import com.jme3.texture.Texture;
import com.jme3.texture.TextureImage;
import com.jme3.util.NativeObject;
import java.io.IOException;

/**
 *
 * @author codex
 */
public class ImageUniform extends AbstractTextureUniform<TextureImage> {

    public ImageUniform(String name) {
        super(name);
    }
    public ImageUniform(String name, String[] args) {
        super(name, args);
    }

    @Override
    protected void update(NativeObject shader, UniformBindUnits units) {
        uploadImage(value, units.pollTextureUnit());
    }
    
}
