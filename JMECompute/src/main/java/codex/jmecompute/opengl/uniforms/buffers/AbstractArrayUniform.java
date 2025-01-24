/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jmecompute.opengl.uniforms.buffers;

import codex.jmecompute.Stride;
import codex.jmecompute.opengl.uniforms.AbstractUniform;
import codex.jmecompute.opengl.uniforms.GLUniform;

/**
 *
 * @author codex
 * @param <T>
 */
public abstract class AbstractArrayUniform <T> extends AbstractUniform<T> {
    
    protected Stride stride;
    protected boolean transposeMatrices = false;
    protected boolean columnMajor = true;

    public AbstractArrayUniform(String name) {
        this(name, Stride.Single);
    }
    public AbstractArrayUniform(String name, Stride stride) {
        super(name);
        this.stride = stride;
    }
    public AbstractArrayUniform(String name, String[] args) {
        super(name);
        parser.add(v -> Enum.valueOf(Stride.class, v), "stride", "s");
        parser.parse(args);
        stride = parser.get("stride");
    }

    public void setTransposeMatrices(boolean transposeMatrices) {
        this.transposeMatrices = transposeMatrices;
    }
    
    public Stride getStride() {
        return stride;
    }

    public boolean isTransposeMatrices() {
        return transposeMatrices;
    }
    
}
