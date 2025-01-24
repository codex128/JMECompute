/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jmecompute.opengl.uniforms;

import codex.jmecompute.opengl.UniformBindUnits;
import com.jme3.asset.AssetManager;
import com.jme3.math.Matrix3f;
import com.jme3.util.NativeObject;
import java.io.IOException;
import org.lwjgl.opengl.GL20;

/**
 *
 * @author codex
 */
public class Matrix3Uniform extends MatrixUniform<Matrix3f> {

    public Matrix3Uniform(String name) {
        super(name, 9);
    }
    public Matrix3Uniform(String name, boolean transpose) {
        super(name, 9, transpose);
    }
    public Matrix3Uniform(String name, boolean transpose, boolean columnMajor) {
        super(name, 9, transpose, columnMajor);
    }
    
    @Override
    protected void update(NativeObject shader, UniformBindUnits units) {
        GL20.glUniformMatrix4fv(uniform, transpose, buffer);
    }
    @Override
    public boolean set(Object value) {
        if (super.set(value)) {
            this.value.fillFloatBuffer(buffer, columnMajor);
            return true;
        }
        return false;
    }
    @Override
    public Object parse(AssetManager assetManager, String[] values) throws IOException {
        float[] mat = new float[9];
        requireMinParseValues(values, mat.length);
        for (int i = 0; i < mat.length; i++) {
            mat[i] = Float.parseFloat(values[i]);
        }
        return new Matrix3f().set(mat, !columnMajor);
    }
    
}
