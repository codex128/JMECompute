package codex.jmecompute.tests;

import codex.jmecompute.WorkSize;
import codex.jmecompute.assets.UniversalShaderLoader;
import codex.jmecompute.opengl.GLComputeShader;
import codex.jmecompute.opengl.GLRenderUtils;
import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;
import com.jme3.system.AppSettings;
import com.jme3.texture.Image;
import com.jme3.texture.Texture2D;
import com.jme3.texture.TextureImage;

public class TestGLCompute extends SimpleApplication {

    public static void main(String[] args) {
        TestGLCompute app = new TestGLCompute();
        AppSettings settings = new AppSettings(true);
        settings.setRenderer(AppSettings.LWJGL_OPENGL45);
        app.setSettings(settings);
        app.start();
    }

    @Override
    public void simpleInitApp() {

        GLRenderUtils.initialize(this);
        GLComputeShader shader = UniversalShaderLoader.loadComputeShader(
                assetManager, "ComputeAssets/compute.glsl");

        int w = context.getFramebufferWidth();
        int h = context.getFramebufferHeight();
        Texture2D texture = new Texture2D(w, h, Image.Format.RGBA8);
        TextureImage img = new TextureImage(texture, TextureImage.Access.WriteOnly);
        shader.set("TargetImage", img);
        shader.execute(new WorkSize(w, h, 1));

        Geometry quad = new Geometry("display_quad", new Quad(w, h));
        Material quadMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        quadMat.setTexture("ColorMap", texture);
        quad.setMaterial(quadMat);
        guiNode.attachChild(quad);

    }

}
