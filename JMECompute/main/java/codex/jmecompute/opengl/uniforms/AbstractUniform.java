/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jmecompute.opengl.uniforms;

import codex.jmecompute.assets.ArgumentParser;
import codex.jmecompute.opengl.ComputeDefine;
import codex.jmecompute.opengl.UniformBindUnits;
import com.jme3.util.NativeObject;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author codex
 * @param <T>
 */
public abstract class AbstractUniform <T> implements GLUniform<T> {
    
    private static final Logger LOG = Logger.getLogger(AbstractUniform.class.getName());
    
    protected final String name;
    protected final ArgumentParser parser = new ArgumentParser();
    protected T value;
    protected int uniform;
    protected boolean updateFlag = true;
    protected boolean useEqualsMethod = false;
    protected boolean persistent = false;
    protected ComputeDefine define;
    
    public AbstractUniform(String name) {
        this.name = name;
    }
    
    @Override
    public void updateValue(NativeObject shader, UniformBindUnits units) {
        if (value == null || (!persistent && !updateFlag) || !fetchUniformId(shader)) {
            return;
        }
        update(shader, units);
    }
    @Override
    public boolean set(Object value) {
        if (this.value != value && (!useEqualsMethod || persistent || this.value == null || !this.value.equals(value))) {
            this.value = (T)value;
            updateFlag = true;
            return true;
        }
        return false;
    }
    @Override
    public T get() {
        return value;
    }
    @Override
    public String getName() {
        return name;
    }
    @Override
    public void setUpdateFlag() {
        updateFlag = true;
    }
    @Override
    public void resetUniformLocation() {
        uniform = -1;
    }
    @Override
    public void setDefine(ComputeDefine define) {
        this.define = define;
    }
    @Override
    public ComputeDefine getDefine() {
        return define;
    }
    
    protected abstract void update(NativeObject shader, UniformBindUnits units);
    protected boolean fetchUniformId(NativeObject shader) {
        uniform = getUniformLocation(shader, name, uniform);
        if (uniform < 0) {
            if (uniform == -1) {
                LOG.log(Level.WARNING, "Unable to locate uniform \"{0}\" in shader.", name);
            }
            uniform = -2;
            return false;
        }
        return true;
    }
    
    protected void requireMinParseValues(String[] values, int minLength) throws IOException {
        if (values.length < minLength) {
            throw new IOException(getClass().getSimpleName() + " requires at least " + minLength + " values to parse.");
        }
    }
    
}
