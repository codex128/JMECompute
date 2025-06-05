/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jmecompute.opengl;

import codex.jmecompute.opengl.uniforms.textures.TextureUniform;
import codex.jmecompute.opengl.uniforms.textures.ImageUniform;
import codex.jmecompute.WorkSize;
import codex.jmecompute.opengl.uniforms.*;
import codex.jmecompute.opengl.uniforms.buffers.*;
import codex.jmecompute.opengl.uniforms.textures.*;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix3f;
import com.jme3.math.Matrix4f;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import com.jme3.renderer.Caps;
import com.jme3.texture.Texture;
import com.jme3.texture.TextureImage;
import com.jme3.util.NativeObject;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import jme3tools.shader.ShaderDebug;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL43.*;
import org.lwjgl.system.MemoryUtil;

/**
 *
 * @author codex
 */
public class GLComputeShader extends NativeObject {
    
    public static final Glsl MIN_VERSION = Glsl.V430;
    public static final Glsl[] DEFAULT_VERSIONS = {MIN_VERSION};
    public static final String DYNAMIC_LOCAL_SIZE_HINT = "@dynamic_local_size";
    public static final long NATIVE_BASE_ID = 12; // see NativeObject
    private static final Logger LOG = Logger.getLogger(GLComputeShader.class.getName());
    private static final HashMap<Class<?>, Function<String, GLUniform<?>>> nativeUniforms = new HashMap<>();
    
    static {
        nativeUniforms.put(Boolean.class, n -> new BooleanUniform(n));
        nativeUniforms.put(Integer.class, n -> new IntUniform(n));
        nativeUniforms.put(Float.class, n -> new FloatUniform(n));
        nativeUniforms.put(Double.class, n -> new DoubleUniform(n));
        nativeUniforms.put(Vector2f.class, n -> new Vector2Uniform(n));
        nativeUniforms.put(Vector3f.class, n -> new Vector3Uniform(n));
        nativeUniforms.put(Vector4f.class, n -> new Vector4Uniform(n));
        nativeUniforms.put(ColorRGBA.class, n -> new Vector4Uniform(n));
        nativeUniforms.put(Matrix3f.class, n -> new Matrix3Uniform(n));
        nativeUniforms.put(Matrix4f.class, n -> new Matrix4Uniform(n));
        nativeUniforms.put(Texture.class, n -> new TextureUniform(n));
        nativeUniforms.put(TextureImage.class, n -> new ImageUniform(n));
        nativeUniforms.put(Texture[].class, n -> new TextureArrayUniform(n));
        nativeUniforms.put(TextureImage[].class, n -> new ImageArrayUniform(n));
        nativeUniforms.put(int[].class, n -> new IntArrayUniform(n));
        nativeUniforms.put(float[].class, n -> new FloatArrayUniform(n));
        nativeUniforms.put(double[].class, n -> new DoubleArrayUniform(n));
        nativeUniforms.put(IntBuffer.class, n -> new IntBufferUniform(n));
        nativeUniforms.put(FloatBuffer.class, n -> new FloatBufferUniform(n));
        nativeUniforms.put(DoubleBuffer.class, n -> new DoubleBufferUniform(n));
    }
    
    private final String name;
    private int shader = -1;
    private String code;
    private final long nativeId;
    private final HashMap<String, GLUniform> uniforms = new HashMap<>();
    private final ArrayList<ComputeDefine> defines = new ArrayList<>();
    private final IntBuffer intBuf = BufferUtils.createIntBuffer(1);
    private final UniformBindUnits units = new UniformBindUnits();
    private final WorkSize work = new WorkSize();
    private Glsl[] versions;
    private Glsl version = null;
    private MemoryBarrier barrier = MemoryBarrier.All;
    private boolean dynamicLocalSize = false;
    protected boolean parseFlag = true;
    
    public GLComputeShader(String name, Glsl[] versions, String code) {
        this.name = name;
        this.code = code;
        if (versions == null || versions.length == 0) {
            this.versions = DEFAULT_VERSIONS;
        } else {
            this.versions = versions;
        }
        this.id = glCreateProgram();
        this.nativeId = GLRenderUtils.get().getUniqueNativeId();
        GLRenderUtils.get().registerNative((NativeObject) this);
    }
    
    @Override
    public void resetObject() {
        uniforms.clear();
        defines.clear();
        version = null;
        updateNeeded = true;
        parseFlag = true;
    }
    @Override
    public void deleteObject(Object rendererObject) {
        glDeleteProgram(id);
        id = -1;
    }
    @Override
    public NativeObject createDestructableClone() {
        return new NativeMirror(id, shader, nativeId);
    }
    @Override
    public long getUniqueId() {
        return nativeId;
    }

    /**
     * Executes this compute shader with the given work size.
     *
     * @param workSize amount of work for the compute shader to perform, or null
     * to use the default work size
     */
    public void execute(WorkSize workSize) {
        assert workSize != null : "Compute work cannot be null.";
        updateUniformDefines();
        parseRuntimeHints();
        compileSource(workSize);
        glUseProgram(id);
        GLRenderUtils.get().setCurrentProgram(id);
        updateUniforms();
        glDispatchCompute(workSize.getGlobalX(), workSize.getGlobalY(), workSize.getGlobalZ());
        glMemoryBarrier(barrier.getBarrier());
        this.work.set(workSize);
        updateNeeded = false;
    }
    
    private void parseRuntimeHints() {
        if (!parseFlag) {
            return;
        }
        if (code.contains(DYNAMIC_LOCAL_SIZE_HINT)) {
            dynamicLocalSize = true;
            code = code.replace(DYNAMIC_LOCAL_SIZE_HINT, "");
        }
        parseFlag = false;
    }
    private void updateUniformDefines() {
        for (GLUniform u : uniforms.values()) {
            if (u.updateDefine()) {
                updateNeeded = true;
            }
        }
    }
    private void compileSource(WorkSize workSize) {
        if (shader >= 0 && !updateNeeded && (!dynamicLocalSize || this.work.equals(workSize, false, true))) {
            return;
        }
        // build source
        String source = buildSource(workSize);
        if (shader < 0) {
            // create shader, compile, and link program
            shader = glCreateShader(GL_COMPUTE_SHADER);
            glShaderSource(shader, source);
            glCompileShader(shader);
            glAttachShader(id, shader);
            glLinkProgram(id);
            glGetProgramiv(id, GL_LINK_STATUS, intBuf);
            if (intBuf.get(0) == GL_FALSE) {
                glGetProgramiv(id, GL_INFO_LOG_LENGTH, intBuf);
                ByteBuffer log = BufferUtils.createByteBuffer(intBuf.get(0));
                glGetProgramInfoLog(id, intBuf, log);
                LOG.log(Level.SEVERE, "Bad compile of\n{0}", ShaderDebug.formatShaderSource(source));
                throw new RuntimeException("Failed to link " + name + ":\n" + MemoryUtil.memASCII(log));
            }
        } else {
            // only overwrite shader source and compile
            glShaderSource(shader, source);
            glCompileShader(shader);
        }
        //checkError();
        glGetShaderiv(shader, GL_COMPILE_STATUS, intBuf);
        if (intBuf.get(0) == GL_FALSE) {
            glGetShaderiv(shader, GL_INFO_LOG_LENGTH, intBuf);
            int length = intBuf.get(0);
            String info = glGetShaderInfoLog(shader, length);
            LOG.warning(info);
            if (length > 3) {
                LOG.log(Level.SEVERE, "Bad compile of\n{0}", ShaderDebug.formatShaderSource(source));
                throw new RuntimeException("Compile error in " + name + "\n" + info);
            }
        }
    }
    private String buildSource(WorkSize workSize) {
        StringBuilder result = new StringBuilder();
        result.append("#version ")
                .append(selectVersion(GLRenderUtils.get().getRenderer().getCaps()).getVersion())
                .append(" core\n");
        if (dynamicLocalSize) {
            result.append("// local size generated by shader class\n");
            result.append("layout (local_size_x=").append(workSize.getLocalX())
                        .append(", local_size_y=").append(workSize.getLocalY())
                        .append(", local_size_z=").append(workSize.getLocalZ()).append(") in;\n");
        }
        result.append("#define COMPUTE_SHADER 1\n");
        for (ComputeDefine d : defines) {
            if (d.isEnabled()) {
                result.append("#define ")
                      .append(d.name)
                      .append(' ')
                      .append(d.getValue())
                      .append('\n');
            }
        }
        result.append(code);
        return result.toString();
    }
    private Glsl selectVersion(EnumSet<Caps> caps) {
        if (version != null) {
            return version;
        }
        for (Glsl v : versions) {
            if ((version == null || v.getVersion() > version.getVersion()) && caps.contains(v.getCap())) {
                version = v;
            }
        }
        if (version == null) {
            throw new NullPointerException("No specified OpenGL version is supported by the hardware.");
        }
        if (version.getVersion() < MIN_VERSION.getVersion()) {
            throw new NullPointerException("Maximum available OpenGL version supported "
                    + "by the hardware does not support compute shaders.");
        }
        return version;
    }
    private void updateUniforms() {
        for (GLUniform u : uniforms.values()) {
            if (updateNeeded) {
                u.setUpdateFlag();
                u.resetUniformLocation();
            }
            u.updateValue(this, units);
        }
        units.reset();
    }
    
    public <T extends GLUniform> T uniform(T uniform) {
        uniforms.put(uniform.getName(), uniform);
        return uniform;
    }
    public BooleanUniform uniformBoolean(String name) {
        return uniform(new BooleanUniform(name));
    }
    public IntUniform uniformInt(String name) {
        return uniform(new IntUniform(name));
    }
    public UIntUniform uniformUInt(String name) {
        return uniform(new UIntUniform(name));
    }
    public FloatUniform uniformFloat(String name) {
        return uniform(new FloatUniform(name));
    }
    public DoubleUniform uniformDouble(String name) {
        return uniform(new DoubleUniform(name));
    }
    public Vector2Uniform uniformVector2(String name) {
        return uniform(new Vector2Uniform(name));
    }
    public Vector3Uniform uniformVector3(String name) {
        return uniform(new Vector3Uniform(name));
    }
    public Vector4Uniform uniformVector4(String name) {
        return uniform(new Vector4Uniform(name));
    }
    public Matrix3Uniform uniformMatrix3(String name) {
        return uniform(new Matrix3Uniform(name));
    }
    public Matrix4Uniform uniformMatrix4(String name) {
        return uniform(new Matrix4Uniform(name));
    }
    public TextureUniform uniformTexture(String name) {
        return uniform(new TextureUniform(name));
    }
    public ImageUniform uniformImage(String name) {
        return uniform(new ImageUniform(name));
    }
    public void uniforms(GLUniform... uniforms) {
        for (GLUniform u : uniforms) {
            uniform(u);
        }
    }
    public void uniformsBoolean(String... names) {
        for (String n : names) {
            uniform(new BooleanUniform(n));
        }
    }
    public void uniformsInt(String... names) {
        for (String n : names) {
            uniform(new IntUniform(n));
        }
    }
    public void uniformsUInt(String... names) {
        for (String n : names) {
            uniform(new UIntUniform(n));
        }
    }
    public void uniformsFloat(String... names) {
        for (String n : names) {
            uniform(new FloatUniform(n));
        }
    }
    public void uniformsDouble(String... names) {
        for (String n : names) {
            uniform(new DoubleUniform(n));
        }
    }
    public void uniformsVector2(String... names) {
        for (String n : names) {
            uniform(new Vector2Uniform(n));
        }
    }
    public void uniformsVector3(String... names) {
        for (String n : names) {
            uniform(new Vector3Uniform(n));
        }
    }
    public void uniformsVector4(String... names) {
        for (String n : names) {
            uniform(new Vector4Uniform(n));
        }
    }
    public void uniformsMatrix3(String... names) {
        for (String n : names) {
            uniform(new Matrix3Uniform(n));
        }
    }
    public void uniformsMatrix4(String... names) {
        for (String n : names) {
            uniform(new Matrix4Uniform(n));
        }
    }
    public void uniformsTexture(String... names) {
        for (String n : names) {
            uniform(new TextureUniform(n));
        }
    }
    public void uniformsImage(String... names) {
        for (String n : names) {
            uniform(new ImageUniform(n));
        }
    }
    
    private GLUniform fetchUniform(String name, Object value) {
        Class type = value != null ? value.getClass() : null;
        GLUniform u = uniforms.get(name);
        while (u == null && type != null) {
            Function<String, GLUniform<?>> factory = nativeUniforms.get(type);
            if (factory != null) {
                u = factory.apply(name);
                if (u != null) {
                    uniforms(u);
                }
            }
            type = type.getSuperclass();
        }
        return u;
    }
    
    public void uniformDefine(String defineName, String uniformName) {
        ComputeDefine define = defines.stream().filter(d -> d.name.equals(defineName)).findAny().orElse(null);
        if (define == null) {
            define = new ComputeDefine(defineName);
            getUniform(uniformName).setDefine(define);
            defines.add(define);
        }
    }
    public void define(String name, Object value) {
        ComputeDefine define = defines.stream().filter(d -> d.name.equals(name)).findAny().orElse(null);
        if (define == null) {
            define = new ComputeDefine(name);
            defines.add(define);
        }
        if (define.setValue(value)) {
            updateNeeded = true;
        }
    }
    
    public void set(String name, Object value) {
        fetchUniform(name, value).set(value);
    }
    public void setVersions(Glsl... versions) {
        this.versions = versions;
        updateNeeded = true;
        this.version = null;
    }
    public void setBarrier(MemoryBarrier barrier) {
        assert barrier != null : "Memory barrier cannot be null.";
        this.barrier = barrier;
    }
    
    public GLUniform getUniform(String name) {
        return uniforms.get(name);
    }
    public <T extends GLUniform> T getUniform(Class<T> type, String name) {
        return (T)uniforms.get(name);
    }
    
    public Glsl[] getVersions() {
        return versions;
    }
    public MemoryBarrier getBarrier() {
        return barrier;
    }
    public boolean isDynamicLocalSize() {
        return dynamicLocalSize;
    }
    
    private static class NativeMirror extends NativeObject {
        
        private int programId, shaderId;
        private final long nativeId;
        
        private NativeMirror(int programId, int shaderId, long nativeId) {
            this.programId = programId;
            this.shaderId = shaderId;
            this.nativeId = nativeId;
        }
        
        @Override
        public void resetObject() {}
        @Override
        public void deleteObject(Object rendererObject) {
            glDeleteShader(shaderId);
            glDeleteProgram(programId);
            programId = -1;
            shaderId = -1;
        }
        @Override
        public NativeObject createDestructableClone() {
            return null;
        }
        @Override
        public long getUniqueId() {
            return nativeId;
        }
        
    }
    
}
