package codex.jmecompute.opengl.uniforms.bufferobjects;

import codex.jmecompute.opengl.NativeBuffer;
import com.jme3.shader.bufferobject.BufferObject;

import java.nio.Buffer;

public class BufferShaderStorage <T extends Buffer> extends ShaderStorageBufferObject<NativeBuffer<T>, T> {

    public BufferShaderStorage(String name) {
        super(name);
    }

    public BufferShaderStorage(String name, BufferObject.AccessHint access, BufferObject.NatureHint nature) {
        super(name, access, nature);
    }

    @Override
    protected T getData() {
        return value.getBuffer();
    }

}
