/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jmecompute.opengl;

import com.jme3.renderer.Renderer;
import com.jme3.renderer.TextureUnitException;
import com.jme3.texture.Texture;

/**
 *
 * @author codex
 */
public interface GLTextureBinder {
    
    public void bindTexture(int unit, Texture texture) throws TextureUnitException;
    
    public static GLTextureBinder useRenderer(Renderer renderer) {
        return new RendererTextureBinder(renderer);
    }
    
    public static class RendererTextureBinder implements GLTextureBinder {

        private final Renderer renderer;

        public RendererTextureBinder(Renderer renderer) {
            this.renderer = renderer;
        }
        
        @Override
        public void bindTexture(int unit, Texture texture) throws TextureUnitException {
            renderer.setTexture(unit, texture);
        }
        
    }
    
}
