/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jmecompute.assets;

import codex.jmecompute.opengl.GLComputeShader;
import codex.jmecompute.opengl.Glsl;
import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetLoader;
import com.jme3.asset.AssetManager;
import com.jme3.shader.plugins.GLSLLoader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import javax.script.CompiledScript;

/**
 *
 * @author codex
 */
public class UniversalShaderLoader implements AssetLoader {
    
    public static final String[] EXTENSIONS = {"glsl", "glsllib", "frag", "vert", "geom", "tsctrl", "tseval", "glsd"};
    private static final String WHITESPACE = "\\p{javaWhitespace}+";
    private static boolean registered = false;

    private final List<CompiledScript> shaderDefApi = new ArrayList<>();
    private GLSLLoader legacyLoader;
    
    public UniversalShaderLoader() {
        
    }
    
    @Override
    public Object load(AssetInfo assetInfo) throws IOException {
        AssetKey key = assetInfo.getKey();
        if (key instanceof ShaderKey) {
            ShaderKey shaderKey = (ShaderKey)key;
            String result = loadSourceCode(assetInfo, shaderKey);
            if (shaderKey.isRootCall() && shaderKey instanceof OpenGLComputeKey) {
                OpenGLComputeKey compute = (OpenGLComputeKey)shaderKey;
                return new GLComputeShader(compute.getName(), compute.getVersions(), result);
            }
            return result;
        }
        if (legacyLoader == null) {
            legacyLoader = new GLSLLoader();
        }
        return legacyLoader.load(assetInfo);
    }
    
    private String loadSourceCode(AssetInfo info, ShaderKey key) throws IOException {
        StringBuilder code = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(info.openStream()))) {
            int lineNumber = 1;
            for (String line; (line = br.readLine()) != null; lineNumber++) {
                String lineTrim = line.trim();
                boolean importReg = lineTrim.startsWith("#import ");
                boolean importInline = lineTrim.startsWith("#importinline ");
                if (importReg || importInline) {
                    line = line.trim();
                    String libFile = getImportedFileName(key, line, lineNumber);
                    if (key.callChainContains(libFile)) {
                        throw new IOException("Circular dependency detected involving " + libFile);
                    }
                    code.append("// ---- Begin import of ").append(key.getName()).append('\n');
                    code.append(info.getManager().loadAsset(new ShaderKey<String>(libFile, key))).append('\n');
                    code.append("// ---- End import of ").append(key.getName()).append('\n');
                } else {
                    code.append(line).append('\n');
                }
            }
        }
        return code.toString();
    }

    private String getImportedFileName(ShaderKey key, String line, int lineNumber) throws IOException {
        int i = line.indexOf('"');
        int j = line.lastIndexOf('"');
        if (i < 0 || j == i) {
            throw new IOException("Incorrect import syntax on line " + lineNumber + " in " + key.getName());
        }
        String libFile = line.substring(i + 1, j);
        if (libFile.equals(key.getName())) {
            throw new IOException("Shader cannot depend on itself (" + key.getName() + ")");
        }
        return libFile;
    }

    public static void register(AssetManager assetManager) {
        register(assetManager, EXTENSIONS);
    }
    public static void register(AssetManager assetManager, String... extensions) {
        if (!registered) {
            assetManager.registerLoader(UniversalShaderLoader.class, extensions);
            registered = true;
        }
    }
    public static void requireRegistered() {
        if (!registered) {
            throw new IllegalStateException(UniversalShaderLoader.class.getSimpleName()
                    + " has not been registered as an asset loader.");
        }
    }
    
    public static String loadShaderCode(AssetManager assetManager, String name) {
        register(assetManager);
        return assetManager.loadAsset(new CodeKey(name));
    }
    public static GLComputeShader loadComputeShader(AssetManager assetManager, String name, Glsl... versions) {
        register(assetManager);
        return assetManager.loadAsset(new OpenGLComputeKey(versions, name));
    }
    
    private static class ShaderKey <T> extends AssetKey<T> {
        
        private final ShaderKey caller;

        public ShaderKey(String name, ShaderKey caller) {
            super(name);
            this.caller = caller;
        }

        public ShaderKey getCaller() {
            return caller;
        }
        
        public boolean callChainContains(String name) {
            ShaderKey key = this;
            while (key != null) {
                if (key.getName().equals(name)) {
                    return true;
                }
                key = key.getCaller();
            }
            return false;
        }
        
        public boolean isRootCall() {
            return caller == null;
        }
        
    }
    private static class CodeKey extends ShaderKey<String> {
        
        public CodeKey(String name) {
            super(name, null);
        }
        
    }
    private static class OpenGLComputeKey extends ShaderKey<GLComputeShader> {
        
        private final Glsl[] versions;
        
        public OpenGLComputeKey(Glsl[] versions, String name) {
            super(name, null);
            this.versions = versions;
        }
        
        @Override
        public Class getCacheType() {
            return null;
        }

        public Glsl[] getVersions() {
            return versions;
        }
        
    }
    
}
