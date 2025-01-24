/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jmecompute.opengl.uniforms;

import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;

/**
 *
 * @author codex
 * @param <T>
 */
public abstract class MatrixUniform <T> extends AbstractUniform<T> {
    
    protected final FloatBuffer buffer;
    protected boolean transpose;
    protected boolean columnMajor;
    
    public MatrixUniform(String name, int size) {
        this(name, size, false);
    }
    public MatrixUniform(String name, int size, boolean transpose) {
        this(name, size, transpose, true);
    }
    public MatrixUniform(String name, int size, boolean transpose, boolean columnMajor) {
        super(name);
        this.buffer = BufferUtils.createFloatBuffer(size);
        this.transpose = transpose;
        this.columnMajor = columnMajor;
        this.useEqualsMethod = true;
    }
    
    public void setTranspose(boolean transpose) {
        this.transpose = transpose;
    }
    public void setColumnMajor(boolean columnMajor) {
        this.columnMajor = columnMajor;
    }

    public boolean isTranspose() {
        return transpose;
    }
    public boolean isColumnMajor() {
        return columnMajor;
    }
    
}
