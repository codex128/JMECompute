package codex.jmecompute.opengl;

import com.jme3.util.NativeObject;

import java.util.function.Consumer;

public class GraphicsObject extends NativeObject {

    private final long nativeId;
    private final Consumer<Integer> delete;

    public GraphicsObject(long nativeId, int graphicsId, Consumer<Integer> delete) {
        super(graphicsId);
        this.nativeId = nativeId;
        this.delete = delete;
    }

    @Override
    public void resetObject() {}

    @Override
    public void deleteObject(Object rendererObject) {
        delete.accept(id);
    }

    @Override
    public NativeObject createDestructableClone() {
        return new GraphicsObject(GLRenderUtils.get().getUniqueNativeId(), id, delete);
    }

    @Override
    public long getUniqueId() {
        return nativeId;
    }

    public static GraphicsObject create(int graphicsId, Consumer<Integer> delete) {
        GraphicsObject g = new GraphicsObject(GLRenderUtils.get().getUniqueNativeId(), graphicsId, delete);
        GLRenderUtils.get().registerNative(g);
        return g;
    }

}
