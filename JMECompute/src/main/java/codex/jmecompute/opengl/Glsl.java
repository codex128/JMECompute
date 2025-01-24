/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package codex.jmecompute.opengl;

import com.jme3.renderer.Caps;

/**
 *
 * @author codex
 */
public enum Glsl {
    
    V100(100, Caps.GLSL100),
    V110(110, Caps.GLSL110),
    V120(120, Caps.GLSL120),
    V130(130, Caps.GLSL130),
    V140(140, Caps.GLSL140),
    V150(150, Caps.GLSL150),
    V300(300, Caps.GLSL300),
    V310(310, Caps.GLSL310),
    V320(320, Caps.GLSL320),
    V330(330, Caps.GLSL330),
    V400(400, Caps.GLSL400),
    V410(410, Caps.GLSL410),
    V420(420, Caps.GLSL420),
    V430(430, Caps.GLSL430),
    V440(440, Caps.GLSL440),
    V450(450, Caps.GLSL450);
    
    private final int version;
    private final Caps cap;

    private Glsl(int version, Caps cap) {
        this.version = version;
        this.cap = cap;
    }

    public int getVersion() {
        return version;
    }
    public Caps getCap() {
        return cap;
    }
    
    public static Glsl getVersion(int version) {
        for (Glsl v : Glsl.values()) {
            if (v.getVersion() == version) {
                return v;
            }
        }
        return null;
    }
    
}
