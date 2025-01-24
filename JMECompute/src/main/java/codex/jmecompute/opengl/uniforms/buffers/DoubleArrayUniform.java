/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jmecompute.opengl.uniforms.buffers;

import codex.jmecompute.Stride;
import codex.jmecompute.opengl.UniformBindUnits;
import com.jme3.asset.AssetManager;
import com.jme3.util.NativeObject;
import java.io.IOException;
import org.lwjgl.opengl.GL40;

/**
 *
 * @author codex
 */
public class DoubleArrayUniform extends AbstractArrayUniform<double[]> {

    public DoubleArrayUniform(String name) {
        super(name);
    }
    public DoubleArrayUniform(String name, Stride divType) {
        super(name, divType);
    }
    public DoubleArrayUniform(String name, String[] args) {
        super(name, args);
    }

    @Override
    public void update(NativeObject shader, UniformBindUnits units) {
        switch (stride) {
            case Single: GL40.glUniform1dv(uniform, value); break;
            case Vec2: GL40.glUniform2dv(uniform, value); break;
            case Vec3: GL40.glUniform3dv(uniform, value); break;
            case Vec4: GL40.glUniform4dv(uniform, value); break;
            case Mat2: GL40.glUniformMatrix2dv(uniform, transposeMatrices, value); break;
            case Mat2x3: GL40.glUniformMatrix2x3dv(uniform, transposeMatrices, value); break;
            case Mat2x4: GL40.glUniformMatrix2x4dv(uniform, transposeMatrices, value); break;
            case Mat3: GL40.glUniformMatrix3dv(uniform, transposeMatrices, value); break;
            case Mat3x2: GL40.glUniformMatrix3x2dv(uniform, transposeMatrices, value); break;
            case Mat3x4: GL40.glUniformMatrix3x4dv(uniform, transposeMatrices, value); break;
            case Mat4: GL40.glUniformMatrix4dv(uniform, transposeMatrices, value); break;
            case Mat4x2: GL40.glUniformMatrix4x2dv(uniform, transposeMatrices, value); break;
            case Mat4x3: GL40.glUniformMatrix4x3dv(uniform, transposeMatrices, value); break;
            default: throw new UnsupportedOperationException("double[] division size: " + stride.name());
        }
    }

    @Override
    public Object parse(AssetManager assetManager, String[] values) throws IOException {
        double[] array = new double[values.length];
        for (int i = 0; i < values.length; i++) {
            array[i] = Double.parseDouble(values[i]);
        }
        return array;
    }
    
}
