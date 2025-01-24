/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jmecompute.opengl.uniforms.bufferobjects;

import com.jme3.scene.VertexBuffer;
import java.nio.Buffer;

/**
 *
 * @author codex
 */
public class VertexBufferShaderStorage extends ShaderStorageBufferObject<VertexBuffer, Buffer> {

    public VertexBufferShaderStorage(String name) {
        super(name);
    }

    @Override
    protected Buffer getData() {
        return value.getData();
    }
    
}
