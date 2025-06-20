package codex.jmecompute.opengl;

import com.jme3.util.NativeObject;
import org.lwjgl.opengl.GL45;

import java.nio.Buffer;

public class NativeBuffer <T extends Buffer> extends NativeObject {
    
    private final T buffer;
    private final long uniqueId = GLRenderUtils.get().getUniqueNativeId();

    public NativeBuffer(T buffer) {
        this.buffer = buffer;
    }

    @Override
    public void resetObject() {
        // nothing to do here?
    }

    @Override
    public void deleteObject(Object o) {
        GL45.glDeleteBuffers(id);
    }

    @Override
    public NativeObject createDestructableClone() {
        return new NativeClone(id);
    }

    @Override
    public long getUniqueId() {
        return uniqueId;
    }

    public T getBuffer() {
        return buffer;
    }
    
    private static class NativeClone extends NativeObject {

        private final long uniqueId = GLRenderUtils.get().getUniqueNativeId();

        public NativeClone(int id) {
            setId(id);
        }

        @Override
        public void resetObject() {}

        @Override
        public void deleteObject(Object rendererObject) {
            GL45.glDeleteBuffers(id);
        }

        @Override
        public NativeObject createDestructableClone() {
            return new NativeClone(id);
        }

        @Override
        public long getUniqueId() {
            return uniqueId;
        }

    }

}
