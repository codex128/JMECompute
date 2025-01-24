/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jmecompute;

/**
 *
 * @author codex
 */
public enum Stride {
    
    Single(1, true, true),
    Vec2(2, true, false),
    Vec3(3, true, false),
    Vec4(4, true, false),
    Mat2(4, true, false),
    Mat2x3(6, true, false),
    Mat2x4(8, true, false),
    Mat3(9, true, false),
    Mat3x2(6, true, false),
    Mat3x4(12, true, false),
    Mat4(16, true, false),
    Mat4x2(8, true, false),
    Mat4x3(12, true, false);
    
    private final int values;
    private final boolean openGl, openCl;
    
    private Stride(int values, boolean openGl, boolean openCl) {
        this.values = values;
        this.openGl = openGl;
        this.openCl = openCl;
    }

    public int getValues() {
        return values;
    }
    public boolean isOpenGl() {
        return openGl;
    }
    public boolean isOpenCl() {
        return openCl;
    }
    
    public void assertOpenGlSupport() {
        if (!openGl) {
            throw new UnsupportedOperationException("OpenGL not supported for " + name());
        }
    }
    public void assertOpenClSupport() {
        if (!openCl) {
            throw new UnsupportedOperationException("OpenCL not supported for " + name());
        }
    }
    
}
