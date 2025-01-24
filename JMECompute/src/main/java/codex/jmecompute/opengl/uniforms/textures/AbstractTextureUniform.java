/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jmecompute.opengl.uniforms.textures;

import codex.jmecompute.opengl.GLRenderUtils;
import codex.jmecompute.opengl.UniformBindUnits;
import codex.jmecompute.opengl.uniforms.AbstractUniform;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.TextureImage;
import com.jme3.texture.image.ColorSpace;
import com.jme3.util.BufferUtils;
import java.nio.IntBuffer;
import java.util.stream.Stream;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL42;

/**
 *
 * @author codex
 * @param <T>
 */
public abstract class AbstractTextureUniform <T> extends AbstractUniform<T> {
    
    protected IntBuffer unitBuf;
    protected ColorSpace colorSpace;
    
    public AbstractTextureUniform(String name) {
        super(name);
        this.persistent = true;
    }
    public AbstractTextureUniform(String name, String[] args) {
        this(name);
        if (args.length >= 1) {
            colorSpace = Enum.valueOf(ColorSpace.class, args[0]);
        }
    }
    
    protected void uploadTexture(Texture texture, int unit) {
        updateColorSpace(texture.getImage());
        GLRenderUtils.get().setTexture(unit, texture);
        GL20.glUniform1i(uniform, unit);
    }
    protected void uploadImage(TextureImage texture, int unit) {
        Image img = texture.getTexture().getImage();
        if (!GLRenderUtils.get().isImageBoundTo(img, unit)) {
            texture.setUpdateNeeded();
        }
        updateColorSpace(texture.getImage());
        uploadTexture(texture.getTexture(), unit);
        if (texture.clearUpdateNeeded()) {
            GL42.glBindImageTexture(unit, texture.getImage().getId(), texture.getLevel(),
                    texture.isLayered(), Math.max(0, texture.getLayer()), texture.getAccess().getGlEnum(),
                    GLRenderUtils.get().getFormat(img.getFormat(), false).internalFormat);
        }
    }
    protected void uploadTextures(Stream<Texture> textures, int size, UniformBindUnits units) {
        unitBuf = BufferUtils.ensureLargeEnough(unitBuf, size);
        unitBuf.rewind();
        textures.forEachOrdered(t -> {
            updateColorSpace(t.getImage());
            unitBuf.put(units.peekTextureUnit());
            GLRenderUtils.get().setTexture(units.pollTextureUnit(), t);
        });
        unitBuf.flip();
        GL20.glUniform1iv(uniform, unitBuf);
    }
    protected void uploadImages(Stream<TextureImage> textures, int size, UniformBindUnits units) {
        unitBuf = BufferUtils.ensureLargeEnough(unitBuf, size);
        unitBuf.rewind();
        textures.forEachOrdered(t -> {
            updateColorSpace(t.getImage());
            unitBuf.put(units.peekTextureUnit());
            GLRenderUtils.get().setTexture(units.pollTextureUnit(), t.getTexture());
        });
        unitBuf.flip();
        GL20.glUniform1iv(uniform, unitBuf);
    }
    
    protected void updateColorSpace(Image image) {
        if (colorSpace != null && image.getColorSpace() != colorSpace) {
            image.setColorSpace(colorSpace);
        }
    }

    public void setColorSpace(ColorSpace colorSpace) {
        this.colorSpace = colorSpace;
    }
    public ColorSpace getColorSpace() {
        return colorSpace;
    }
    
}
