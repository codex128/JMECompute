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
    
    private final HashMap<String, DependencyNode> dependencies = new HashMap<>();
    private final List<CompiledScript> shaderDefApi = new ArrayList<>();
    private GLSLLoader legacyLoader;
    
    public UniversalShaderLoader() {
        
    }
    
    @Override
    public Object load(AssetInfo assetInfo) throws IOException {
        AssetKey key = assetInfo.getKey();
        if (key instanceof ShaderKey) {
            ShaderKey shaderKey = (ShaderKey)key;
            Object result = loadSourceCode(assetInfo, shaderKey);
            if (shaderKey.isRootCall()) {
                String code = resolveDependencies();
                if (shaderKey instanceof OpenGLComputeKey) {
                    OpenGLComputeKey compute = (OpenGLComputeKey)shaderKey;
                    result = new GLComputeShader(compute.getName(), compute.getVersions(), code);
                } else if (shaderKey instanceof CodeKey) {
                    result = code;
                }
            }
            return result;
        }
        if (key instanceof OpenGLComputeDefKey) {
            return loadShaderDef(assetInfo, (OpenGLComputeDefKey)key);
        }
        if (legacyLoader == null) {
            legacyLoader = new GLSLLoader();
        }
        return legacyLoader.load(assetInfo);
    }
    
    private DependencyNode loadSourceCode(AssetInfo info, ShaderKey key) throws IOException {
        DependencyNode d = new DependencyNode(key.getName());
        dependencies.put(d.file, d);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(info.openStream()))) {
            int lineNumber = 1;
            for (String line; (line = br.readLine()) != null; lineNumber++) {
                if (line.trim().startsWith("#import ")) {
                    line = line.trim();
                    int i = line.indexOf('"');
                    int j = line.lastIndexOf('"');
                    if (i < 0 || j == i) {
                        throw new IOException("Incorrect import syntax on line " + lineNumber + " in " + d.file);
                    }
                    String libFile = line.substring(i + 1, j);
                    if (libFile.equals(d.file)) {
                        throw new IOException("Shader cannot depend on itself (" + d.file + ")");
                    }
                    if (key.callChainContains(libFile)) {
                        throw new IOException("Circular dependency detected involving " + libFile);
                    }
                    DependencyNode lib = dependencies.get(libFile);
                    if (lib == null) {
                        lib = info.getManager().loadAsset(new ShaderKey<>(libFile, key));
                    }
                    lib.addImporter(d);
                } else {
                    d.contents.append(line).append('\n');
                }
            }
        }
        return d;
    }
    private String resolveDependencies() {
        LinkedList<DependencyNode> ready = new LinkedList<>();
        for (DependencyNode n : dependencies.values()) {
            if (n.unresolved <= 0) {
                ready.addLast(n);
            }
        }
        StringBuilder code = new StringBuilder();
        while (!ready.isEmpty()) {
            DependencyNode n = ready.pollFirst();
            code.append("// ---- begin import of ").append(n.file).append('\n');
            code.append(n.contents).append('\n');
            code.append("// ---- end import of ").append(n.file).append('\n');
            for (DependencyNode i : n.importers) {
                if (i.resolveDependency()) {
                    ready.addLast(i);
                }
            }
        }
        dependencies.clear();
        return code.toString();
    }
    
    private HashMap<String, GLComputeShader> loadShaderDef(AssetInfo info, OpenGLComputeDefKey key) {
        throw new UnsupportedOperationException();
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
    public static ComputeShaderMap loadComputeShaderDef(AssetManager assetManager, String name) {
        return assetManager.loadAsset(new OpenGLComputeDefKey(name));
    }
    
    public static class ComputeShaderMap extends HashMap<String, List<GLComputeShader>> {
        
        public static final String DEFAULT = "DEFAULT_SHADER_DEF";
        
        public GLComputeShader get(String name, int index) {
            List<GLComputeShader> list = get(name);
            if (list == null) {
                return null;
            }
            return list.get(index);
        }
        public GLComputeShader get(int index) {
            return get(DEFAULT, index);
        }
        public GLComputeShader getFirst(String name) {
            return get(name, 0);
        }
        public GLComputeShader getFirst() {
            return get(DEFAULT, 0);
        }
        public GLComputeShader getSupported(String name, Glsl supportedVersion) {
            List<GLComputeShader> list = get(name);
            if (list == null) {
                return null;
            }
            for (GLComputeShader s : list) {
                for (Glsl v : s.getVersions()) {
                    if (v == supportedVersion) {
                        return s;
                    }
                }
            }
            return null;
        }
        public GLComputeShader getSupported(Glsl supportedVersion) {
            return getSupported(DEFAULT, supportedVersion);
        }
        
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
    private static class OpenGLComputeDefKey extends AssetKey<ComputeShaderMap> {

        public OpenGLComputeDefKey(String name) {
            super(name);
        }
        
    }
    private static class DependencyNode {
        
        public final String file;
        public final StringBuilder contents = new StringBuilder();
        private final ArrayList<DependencyNode> importers = new ArrayList<>();
        private int unresolved = 0;
        
        public DependencyNode(String fileName) {
            this.file = fileName;
        }
        
        public void addImporter(DependencyNode n) {
            importers.add(n);
            n.unresolved++;
        }
        
        public boolean resolveDependency() {
            return --unresolved <= 0;
        }
        
        public boolean isResolved() {
            return unresolved <= 0;
        }
        
    }
    
}
