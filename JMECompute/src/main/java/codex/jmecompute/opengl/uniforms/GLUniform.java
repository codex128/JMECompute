/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jmecompute.opengl.uniforms;

import codex.jmecompute.opengl.ComputeDefine;
import codex.jmecompute.opengl.GLRenderUtils;
import codex.jmecompute.opengl.UniformBindUnits;
import com.jme3.asset.AssetManager;
import com.jme3.util.NativeObject;
import java.io.IOException;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;

/**
 *
 * @author codex
 * @param <T>
 */
public interface GLUniform <T> {
    
    /**
     * Updates the uniform value in the OpenGL context with this
     * uniform's internal value.
     * 
     * @param shader
     * @param units 
     */
    public void updateValue(NativeObject shader, UniformBindUnits units);
    
    /**
     * Sets the internal value of this uniform.
     * 
     * @param value
     * @return 
     */
    public boolean set(Object value);
    
    /**
     * Gets the internal value of this uniform.
     * 
     * @return 
     */
    public T get();
    
    /**
     * Gets the name of this uniform.
     * <p>
     * The name is used to locate the OpenGL uniform in a shader.
     * 
     * @return 
     */
    public String getName();
    
    /**
     * Sets the update flag of this uniform to true.
     * <p>
     * Depending on the precise implementation, this forces this uniform to
     * update the OpenGL uniform value with its internal value. Some implementations
     * persistently update the OpenGL value, and thus calling this would be useless.
     */
    public void setUpdateFlag();
    
    /**
     * Resets the OpenGL location of this uniform.
     * <p>
     * This forces this uniform to relocate the OpenGL uniform.
     */
    public void resetUniformLocation();
    
    /**
     * Sets the define attached to and maintained by this uniform.
     * <p>
     * The internal value of the define is made to reflect this uniform's
     * internal value when {@link #updateDefine()} is called.
     * 
     * @param define 
     */
    public void setDefine(ComputeDefine define);
    
    /**
     * Gets the define attached to this uniform.
     * 
     * @return 
     */
    public ComputeDefine getDefine();
    
    /**
     * Updates the internal value of the attached define, if one exists, to
     * reflect this uniform's internal value.
     * 
     * @return true if the attached define exists and was changed as a result
     * of this call
     */
    public default boolean updateDefine() {
        ComputeDefine d = getDefine();
        return d != null && d.setValue(get());
    }
    
    /**
     * Generates and returns a potential internal value from the array of
     * string values.
     * <p>
     * This method should not directly assign the generated value to this uniform.
     * That should be responsibility of the caller to perform if desired.
     * <p>
     * The default implementation throws an {@link UnsupportedOperationException}.
     * Uniform implements are expected to explicitely override this method if they
     * support parsing.
     * 
     * @param assetManager
     * @param values strings to generate the value from
     * @return generated value
     * @throws IOException 
     */
    public default Object parse(AssetManager assetManager, String[] values) throws IOException {
        throw new UnsupportedOperationException("Parsing is not supported for " + getClass().getName());
    }
    
    /**
     * Gets the OpenGL location of the named uniform in the shader.
     * 
     * @param shader
     * @param name
     * @param currentLocation
     * @return 
     */
    public default int getUniformLocation(NativeObject shader, String name, int currentLocation) {
        if (shader.isUpdateNeeded() || currentLocation == -1) {
            GLRenderUtils.checkError();
            int u = glGetUniformLocation(shader.getId(), name);
            GLRenderUtils.checkError();
            return u;
        } else {
            return currentLocation;
        }
    }
    
    public static <T extends Enum> T getEnumValue(Class<T> type, String name, T defValue) {
        for (T e : type.getEnumConstants()) {
            if (e.name().equals(name)) {
                return e;
            }
        }
        return defValue;
    }
    
}
