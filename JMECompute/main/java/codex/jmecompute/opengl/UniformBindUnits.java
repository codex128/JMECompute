/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jmecompute.opengl;

/**
 *
 * @author codex
 */
public class UniformBindUnits {
    
    private int textureUnit = 0;
    private int bufferUnit = 0;
    
    public int pollTextureUnit() {
        return textureUnit++;
    }
    public int pollBufferUnit() {
        return bufferUnit++;
    }
    
    public int peekTextureUnit() {
        return textureUnit;
    }
    public int peekBufferUnit() {
        return bufferUnit;
    }

    public void setTextureUnit(int textureUnit) {
        this.textureUnit = textureUnit;
    }
    public void setBufferUnit(int bufferUnit) {
        this.bufferUnit = bufferUnit;
    }
    
    public void reset() {
        textureUnit = bufferUnit = 0;
    }
    
}
