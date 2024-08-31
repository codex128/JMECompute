/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jmecompute.opengl;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import com.jme3.renderer.Caps;
import com.jme3.renderer.Renderer;
import com.jme3.renderer.TextureUnitException;
import com.jme3.shader.Uniform;
import com.jme3.shader.VarType;
import com.jme3.texture.Texture;
import static java.lang.Runtime.version;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import jme3tools.shader.ShaderDebug;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL43.*;

/**
 *
 * @author codex
 */
public class GLComputeShader {
    
    private static final Logger LOG = Logger.getLogger(GLComputeShader.class.getName());
    
    private final String name, code;
    private final int[] versions;
    private final int program;
    private final HashMap<String, ComputeUniform> uniforms = new HashMap<>();
    private final HashMap<String, ComputeDefine> defines = new HashMap<>();
    private final IntBuffer intBuf = BufferUtils.createIntBuffer(1);
    private int version = -1;
    private boolean failOnMiss = false;
    private boolean updateFlag = true;
    
    public GLComputeShader(String name, int[] versions, String code) {
        this.name = name;
        this.versions = versions;
        this.code = code;
        this.program = glCreateProgram();
        if (this.versions.length == 0) {
            throw new IllegalArgumentException("At least one version must be specified.");
        }
    }
    
    /**
     * Executes this compute shader.
     * <p>
     * The number of global executions is determined by {@code x * y * z}.
     * 
     * @param renderer
     * @param x number of executions on the X axis
     * @param y number of executions on the Y axis
     * @param z number of executions on the Z axis
     */
    public void execute(Renderer renderer, int x, int y, int z) {
        execute(GLTextureBinder.useRenderer(renderer), renderer.getCaps(), x, y, z);
    }
    
    /**
     * Executes this compute shader.
     * <p>
     * The number of global executions is determined by {@code x * y * z}.
     * 
     * @param texBind binds texture uniforms to the OpenGL context
     * @param caps hardware capabilities
     * @param x number of executions on the X axis
     * @param y number of executions on the Y axis
     * @param z number of executions on the Z axis
     */
    public void execute(GLTextureBinder texBind, EnumSet<Caps> caps, int x, int y, int z) {
        updateParameterDefines();
        compileSource(caps);
        glUseProgram(program);
        updateUniforms(texBind);
        glDispatchCompute(x, y, z);
        glMemoryBarrier(GL_SHADER_IMAGE_ACCESS_BARRIER_BIT);
        updateFlag = false;
    }
    
    private void updateParameterDefines() {
        for (ComputeDefine d : defines.values()) if (d.paramName != null) {
            ComputeUniform u = uniforms.get(d.paramName);
            if (u != null && d.setValue(u.getValue())) {
                updateFlag = true;
            }
        }
    }
    private void compileSource(EnumSet<Caps> caps) {
        
        if (!updateFlag) {
            return;
        }
        
        int shader = glCreateShader(GL_COMPUTE_SHADER);
        
        // build source
        StringBuilder builder = new StringBuilder();
        builder.append("#version ")
               .append(getVersion(caps))
               .append(" core\n");
        for (ComputeDefine d : defines.values()) {
            if (d.isEnabled()) {
                builder.append("#define ")
                       .append(d.defineName)
                       .append(' ')
                       .append(d.getValue())
                       .append('\n');
            }
        }
        builder.append(code);
        glShaderSource(shader, builder);
        
        // compile source
        glCompileShader(shader);
        glGetShaderiv(shader, GL_COMPILE_STATUS, intBuf);
        if (intBuf.get(0) == GL_FALSE) {
            glGetShaderiv(shader, GL_INFO_LOG_LENGTH, intBuf);
            int length = intBuf.get(0);
            if (length > 3) {
                String info = glGetShaderInfoLog(shader, length);
                LOG.log(Level.SEVERE, "Bad compile of\n{0}", ShaderDebug.formatShaderSource(builder.toString()));
                throw new RuntimeException("Compile error in " + name + "\n" + info);
            }
        }
        
        // attach shader
        glAttachShader(program, shader);
        glLinkProgram(program);
        glDeleteShader(shader);
        
    }
    private int getVersion(EnumSet<Caps> caps) {
        if (version >= 0) {
            return version;
        }
        for (int v : versions) {
            if (v > version && caps.contains(Caps.valueOf("GLSL"+v))) {
                version = v;
            }
        }
        if (version < GLComputeLoader.MIN_VERSION) {
            throw new NullPointerException("Minimum compute version is not supported by the graphics hardware.");
        }
        return version;
    }
    private void updateUniforms(GLTextureBinder texBind) {
        int nextTexUnit = 0;
        for (ComputeUniform u : uniforms.values()) {
            if (u.getType().isTextureType()) {
                setUniformTexture(texBind, u, nextTexUnit++);
            } else if (u.isUpdateNeeded()) {
                setUniformValue(u);
            }
            u.clearUpdateNeeded();
        }
    }
    private void setUniformValue(ComputeUniform uniform) {
        /**
         * Copyright (c) 2009-2024 jMonkeyEngine
         * All rights reserved.
         */
        FloatBuffer fb;
        IntBuffer ib;
        int id = glGetUniformLocation(program, uniform.getName());
        switch (uniform.getType()) {
            case Float:
                Float f = (Float) uniform.getValue();
                glUniform1f(id, f);
                break;
            case Vector2:
                Vector2f v2 = (Vector2f) uniform.getValue();
                //assert isValidNumber(v2) : "Invalid Vector2f value " + v2 + " for " + uniform.getBinding();
                glUniform2f(id, v2.getX(), v2.getY());
                break;
            case Vector3:
                Vector3f v3 = (Vector3f) uniform.getValue();
                //assert isValidNumber(v3) : "Invalid Vector3f value " + v3 + " for " + uniform.getBinding();
                glUniform3f(id, v3.getX(), v3.getY(), v3.getZ());
                break;
            case Vector4:
                Object val = uniform.getValue();
                if (val instanceof ColorRGBA) {
                    ColorRGBA c = (ColorRGBA) val;
                    //assert isValidNumber(c) : "Invalid ColorRGBA value " + c + " for " + uniform.getBinding();
                    glUniform4f(id, c.r, c.g, c.b, c.a);
                } else if (val instanceof Vector4f) {
                    Vector4f c = (Vector4f) val;
                    //assert isValidNumber(c) : "Invalid Vector4f value " + c + " for " + uniform.getBinding();
                    glUniform4f(id, c.x, c.y, c.z, c.w);
                } else {
                    Quaternion c = (Quaternion) uniform.getValue();
                    //assert isValidNumber(c) : "Invalid Quaternion value " + c + " for "
                    //        + uniform.getBinding();
                    glUniform4f(id, c.getX(), c.getY(), c.getZ(), c.getW());
                }
                break;
            case Boolean:
                Boolean b = (Boolean) uniform.getValue();
                glUniform1i(id, b ? GL_TRUE : GL_FALSE);
                break;
            case Matrix3:
                fb = uniform.getBuffer();
                //assert isValidNumber(fb) : "Invalid Matrix3f value " + uniform.getValue() + " for "
                //        + uniform.getBinding();
                assert fb.remaining() == 9;
                glUniformMatrix3fv(id, false, fb);
                break;
            case Matrix4:
                fb = uniform.getBuffer();
                //assert isValidNumber(fb) : "Invalid Matrix4f value " + uniform.getValue() + " for "
                //        + uniform.getBinding();
                assert fb.remaining() == 16;
                glUniformMatrix4fv(id, false, fb);
                break;
            case IntArray:
                ib = (IntBuffer) uniform.getValue();
                glUniform1iv(id, ib);
                break;
            case FloatArray:
                fb = uniform.getBuffer();
                //assert isValidNumber(fb) : "Invalid float array value "
                //        + Arrays.asList((float[]) uniform.getValue()) + " for " + uniform.getBinding();
                glUniform1fv(id, fb);
                break;
            case Vector2Array:
                fb = uniform.getBuffer();
                //assert isValidNumber(fb) : "Invalid Vector2f array value "
                //        + Arrays.deepToString((Vector2f[]) uniform.getValue()) + " for "
                //        + uniform.getBinding();
                glUniform2fv(id, fb);
                break;
            case Vector3Array:
                fb = uniform.getBuffer();
                //assert isValidNumber(fb) : "Invalid Vector3f array value "
                //        + Arrays.deepToString((Vector3f[]) uniform.getValue()) + " for "
                //        + uniform.getBinding();
                glUniform3fv(id, fb);
                break;
            case Vector4Array:
                fb = uniform.getBuffer();
                //assert isValidNumber(fb) : "Invalid Vector4f array value "
                //        + Arrays.deepToString((Vector4f[]) uniform.getValue()) + " for "
                //        + uniform.getBinding();
                glUniform4fv(id, fb);
                break;
            case Matrix4Array:
                fb = uniform.getBuffer();
                //assert isValidNumber(fb) : "Invalid Matrix4f array value "
                //        + Arrays.deepToString((Matrix4f[]) uniform.getValue()) + " for "
                //        + uniform.getBinding();
                glUniformMatrix4fv(id, false, fb);
                break;
            case Int:
                Integer i = (Integer) uniform.getValue();
                glUniform1i(id, i);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported uniform type: " + uniform.getType());
        }
    }
    private void setUniformTexture(GLTextureBinder texBind, ComputeUniform uniform, int unit) {
        glUniform1i(getUniformId(uniform.getName()), unit);
        try {
            texBind.bindTexture(unit, (Texture)uniform.getValue());
        } catch (TextureUnitException ex) {
            throw new RuntimeException("Error uploading textures.", ex);
        }
    }
    
    private int getUniformId(String name) {
        return glGetUniformLocation(program, name);
    }
    private ComputeUniform getUniform(String name, VarType type) {
        ComputeUniform u = uniforms.get(name);
        if (u == null) {
            if (failOnMiss) {
                throw new NullPointerException("Uniform[name:"+name+", type:"+type+"] does not exist.");
            }
            u = new ComputeUniform(name, type);
            uniforms.put(name, u);
        }
        return u;
    }
    private ComputeUniform getUniform(String name) {
        ComputeUniform u = uniforms.get(name);
        if (u == null) {
            throw new NullPointerException("Uniform \""+name+"\" does not exist.");
        }
        return u;
    }
    
    public void set(ComputeUniform uniform) {
        uniforms.put(uniform.getName(), uniform);
    }
    
    /**
     * Sets the uniform at the name.
     * <p>
 If not failOnMiss a uniform does not exist at
 the name, a new uniform is created.
     * 
     * @param name
     * @param type
     * @param value 
     */
    public void set(String name, VarType type, Object value) {
        getUniform(name, type).setValue(type, value);
    }
    
    /**
     * Sets the uniform at the name.
     * 
     * @param name
     * @param value 
     */
    public void set(String name, Object value) {
        getUniform(name).setValue(value);
    }
    
    /**
     * 
     * @param define 
     */
    public void setDefine(ComputeDefine define) {
        defines.put(define.defineName, define);
    }
    
    /**
     * Sets the define at the name.
     * <p>
     * If not failOnMiss and a define does not exist at
     * the name, a new define is created.
     * 
     * @param name
     * @param value 
     */
    public void setDefine(String name, Object value) {
        ComputeDefine d = defines.get(name);
        if (d == null) {
            if (failOnMiss) {
                throw new NullPointerException("Define \""+name+"\" does not exist.");
            }
            d = new ComputeDefine(name);
            defines.put(name, d);
        }
        if (d.setValue(value)) {
            updateFlag = true;
        }
    }
    
    /**
     * Sets this as failOnMiss, so references to non-existent
 uniforms or defines will throw exceptions instead of
 creating new uniforms and defines.
     * 
     * @param failOnMiss 
     */
    public void setFailOnMiss(boolean failOnMiss) {
        this.failOnMiss = failOnMiss;
    }
    
    /**
     * 
     * @param name 
     */
    public void delete(String name) {
        getUniform(name).setValue(null);
        setDefine(name, null);
    }
    
    /**
     * Gets the uniform value at the name.
     * 
     * @param <T>
     * @param name
     * @return 
     */
    public <T> T get(String name) {
        return (T)getUniform(name).getValue();
    }
    
    /**
     * Returns true if a uniform exists at the name.
     * 
     * @param name
     * @return 
     */
    public boolean exists(String name) {
        return uniforms.containsKey(name);
    }
    
    /**
     * Returns true if this shader is failOnMiss.
     * 
     * @return 
     * @see #setFailOnMiss(boolean)
     */
    public boolean isFailOnMiss() {
        return failOnMiss;
    }
    
}
