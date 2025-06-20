/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jmecompute.opengl;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.renderer.RenderContext;
import com.jme3.renderer.TextureUnitException;
import com.jme3.renderer.opengl.GLImageFormat;
import com.jme3.renderer.opengl.GLImageFormats;
import com.jme3.renderer.opengl.GLRenderer;
import com.jme3.shader.bufferobject.BufferObject;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.util.NativeObject;
import com.jme3.util.NativeObjectManager;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL45;

/**
 * A handy renderer utility that does what you want without needing to mess
 * with GLRenderer's micromanagement of the OpenGL context.
 * 
 * @author codex
 */
public class GLRenderUtils {
    
    private static GLRenderUtils instance;

    private static HashMap<Integer, String> errorDecoder = new HashMap<>();
    static {
        errorDecoder.put(1280, "GL_INVALID_ENUM");
        errorDecoder.put(1281, "GL_INVALID_VALUE");
        errorDecoder.put(1282, "GL_INVALID_OPERATION");
        errorDecoder.put(1283, "GL_STACK_OVERFLOW");
        errorDecoder.put(1284, "GL_STACK_UNDERFLOW");
        errorDecoder.put(1285, "GL_OUT_OF_MEMORY");
        errorDecoder.put(1286, "GL_INVALID_FRAMEBUFFER_OPERATION");
    }
    private static String getDecodedErrorMessage(int flag) {
        return errorDecoder.getOrDefault(flag, "unidentified");
    }
    
    public static GLRenderUtils initialize(Application app) {
        return (instance = new GLRenderUtils(app));
    }
    public static GLRenderUtils get() {
        return instance;
    }
    public static void checkError() {
        checkError(null);
    }
    public static void checkError(String message) {
        int flag = GL11.glGetError();
        if (flag != GL11.GL_NO_ERROR) {
            throw new RuntimeException("OpenGL error: " + flag + " (" + (message != null ? message : getDecodedErrorMessage(flag)) + ")");
        }
    }
    
    private NativeObjectManager nativeManager = new NativeObjectManager();
    private final AppListener listener = new AppListener();
    private final GLRenderer renderer;
    private final RenderContext context;
    private final GLImageFormat[][] formats;
    private final AtomicLong nextNativeId = new AtomicLong(0);
    
    private GLRenderUtils(Application app) {
        if (!(app.getRenderer() instanceof GLRenderer)) {
            throw new ClassCastException("App is expected to run a GLRenderer.");
        }
        renderer = (GLRenderer)app.getRenderer();
        formats = GLImageFormats.getFormatsForCaps(renderer.getCaps());
        try {
            Field f = renderer.getClass().getDeclaredField("context");
            f.setAccessible(true);
            context = (RenderContext)f.get(renderer);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
            throw new RuntimeException("Failed to access renderer context reflectively.", ex);
        }
        app.getStateManager().attach(listener);
    }
    
    public void registerNative(NativeObject object) {
        nativeManager.registerObject(object);
    }
    public void setCurrentProgram(long id) {
        if (instance == null) {
            throw new IllegalStateException("GLComputeManager is not initialized.");
        }
        context.boundShaderProgram = (int)id;
    }
    public void setTexture(int unit, Texture texture) {
        try {
            renderer.setTexture(unit, texture);
        } catch (TextureUnitException ex) {
            throw new RuntimeException("Only " + (unit + 1) + " textures can be bound.");
        }
    }
    public void bindBufferObject(int target, int bindPoint, BufferObject buffer) {
        buffer.setBinding(bindPoint);
        if (context.boundBO[bindPoint] == null || context.boundBO[bindPoint].get() != buffer) {
            GL45.glBindBufferBase(target, bindPoint, buffer.getId());
            context.boundBO[bindPoint] = buffer.getWeakRef();
        }
    }
    public void bindUnknownBufferObject(int target, int bindPoint, NativeObject buffer) {
        context.boundBO[bindPoint] = null;
        GL45.glBindBufferBase(target, bindPoint, buffer.getId());
    }
    
    public int resolveUsageHint(BufferObject bufObj) {
        return resolveUsageHint(bufObj.getAccessHint(), bufObj.getNatureHint());
    }
    public int resolveUsageHint(BufferObject.AccessHint access, BufferObject.NatureHint nature) {
        switch (access) {
            case Dynamic: switch (nature) {
                case Draw: return GL45.GL_DYNAMIC_DRAW;
                case Read: return GL45.GL_DYNAMIC_READ;
                case Copy: return GL45.GL_DYNAMIC_COPY;
            } break;
            case Stream: switch (nature) {
                case Draw: return GL45.GL_STREAM_DRAW;
                case Read: return GL45.GL_STREAM_READ;
                case Copy: return GL45.GL_STREAM_COPY;
            } break;
            case Static: switch (nature) {
                case Draw: return GL45.GL_STATIC_DRAW;
                case Read: return GL45.GL_STATIC_READ;
                case Copy: return GL45.GL_STATIC_COPY;
            } break;
        }
        return -1;
    }
    
    public long getUniqueNativeId() {
        return nextNativeId.getAndAdd(1);
    }
    public GLImageFormat getFormat(Image.Format format, boolean srgb) {
        if (srgb) {
            return formats[1][format.ordinal()];
        } else {
            return formats[0][format.ordinal()];
        }
    }
    public boolean isImageBoundTo(Image image, int unit) {
        WeakReference<Image> bound = instance.context.boundTextures[unit];
        return bound != null && bound.get() == image.getWeakRef().get();
    }
    
    public GLRenderer getRenderer() {
        return renderer;
    }
    public RenderContext getContext() {
        return context;
    }
    
    private class AppListener extends BaseAppState {
        
        @Override
        protected void initialize(Application app) {}
        @Override
        protected void cleanup(Application app) {
            try {
                nativeManager.deleteAllObjects(renderer);
            } catch (Exception ex) {
                // catch exceptions that would otherwise cause the application to freeze
                ex.printStackTrace(System.err);
            } finally {
                nativeManager = null;
            }
        }
        @Override
        protected void onEnable() {}
        @Override
        protected void onDisable() {}
        @Override
        public void update(float tpf) {
            nativeManager.deleteUnused(renderer);
        }
    
    }
    
}
