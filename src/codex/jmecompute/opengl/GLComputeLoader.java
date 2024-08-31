/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jmecompute.opengl;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetLoader;
import com.jme3.asset.AssetManager;
import com.jme3.math.Matrix3f;
import com.jme3.math.Matrix4f;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import com.jme3.shader.VarType;
import com.jme3.util.blockparser.BlockLanguageParser;
import com.jme3.util.blockparser.Statement;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author codex
 */
public class GLComputeLoader implements AssetLoader {
    
    public static final int MIN_VERSION = 430;
    private static final String FILE_HEADER = "ComputeShader";
    private static final String SHADER_HEADER = "Shader";
    private static final String VERSION_HEADER = "Version";
    private static final String VERSION_PREFIX = "GLSL";
    private static final String PARAMETERS_HEADER = "Parameters";
    private static final String DEFINES_HEADER = "Defines";
    private static final String DEFINE_LITERAL = "#";
    
    @Override
    public Object load(AssetInfo assetInfo) throws IOException {
        InputStream in = assetInfo.openStream();
        GLComputeShader shader;
        try {
            shader = load(in, assetInfo.getManager(), assetInfo.getKey());
        } catch (IOException ex) {
            throw new IOException("Error loading compute shader.", ex);
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }
    
    private GLComputeShader load(InputStream in, AssetManager assetManager, AssetKey key) throws IOException {
        List<Statement> statements = BlockLanguageParser.parse(in);
        if (statements.size() != 1) {
            throw new IOException("Source file may only contain one root statement.");
        }
        Statement root = statements.get(0);
        String name = root.getLine();
        if (!name.startsWith(FILE_HEADER)) {
            throw new IOException("Must be a compute shader definition file.");
        }
        name = name.substring(FILE_HEADER.length() + 1).trim();
        String code = null;
        int[] versions = null;
        LinkedList<ComputeUniform> params = new LinkedList<>();
        LinkedList<ComputeDefine> defines = new LinkedList<>();
        for (Statement s : root.getContents()) {
            String header = s.getLine().split("[ \\{]")[0];
            switch (header) {
                case SHADER_HEADER: code = readShader(assetManager, s.getLine());
                case VERSION_HEADER: versions = readVersions(s.getLine()); break;
                case PARAMETERS_HEADER: readParameters(s, params); break;
                case DEFINES_HEADER: readDefines(s, defines); break;
            }
        }
        if (code == null) {
            throw new NullPointerException("Shader source not specified.");
        }
        if (versions == null) {
            throw new NullPointerException("Supported versions not specified.");
        }
        GLComputeShader shader = new GLComputeShader(name, versions, code);
        for (ComputeDefine d : defines) {
            shader.setDefine(d);
        }
        for (ComputeUniform u : params) {
            shader.set(u);
        }
    }
    
    private static String readShader(AssetManager assetManager, String line) throws IOException {
        String asset = line.substring(SHADER_HEADER.length() + 1).trim();
        return assetManager.loadAsset(new AssetKey<>(asset));
    }
    private static int[] readVersions(String line) throws IOException {
        String[] array = line.substring(VERSION_HEADER.length() + 1).trim().split(" ");
        int[] versions = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            String a = array[i].trim();
            if (a.startsWith(VERSION_PREFIX)) {
                int v = versions[i] = Integer.parseInt(a.substring(VERSION_PREFIX.length() + 1));
                if (v < MIN_VERSION) {
                    throw new IOException("Compute shaders are not supported before OpenGL "+MIN_VERSION);
                }
            } else {
                throw new IOException("Version number must start with "+VERSION_PREFIX);
            }
        }
        return versions;
    }
    private static void readParameters(Statement statement, LinkedList<ComputeUniform> params) throws IOException {
        for (Statement p : statement.getContents()) {
            String[] args = p.getLine().split(":", 2);
            String[] names = args[0].split(" ", 2);
            if (names.length != 2) {
                throw new IOException("Parameter must have type and name.");
            }
            ComputeUniform u = new ComputeUniform(names[1].trim(), VarType.valueOf(names[0].trim()));
            if (args.length > 1) {
                u.setValue(parseDeclaredDefaultValue(u.getType(), args[1].trim()));
            } else {
                u.setValue(getTypeDefaultValue(u.getType()));
            }
            params.add(u);
        }
    }
    private static Object parseDeclaredDefaultValue(VarType type, String value) throws IOException {
        switch (type) {
            case Boolean: return Boolean.valueOf(value);
            case Int: return Integer.valueOf(value);
            case Float: return Float.valueOf(value);
            case Vector2: return vector2(parseFloatArray(value, 2));
            case Vector3: return vector3(parseFloatArray(value, 3));
            case Vector4: return vector4(parseFloatArray(value, 4));
            case Matrix3: return new Matrix3f().set(parseFloatArray(value, 9));
            case Matrix4: return new Matrix4f(parseFloatArray(value, 16));
            case IntArray: return parseIntArray(value, 0);
            case FloatArray: return parseFloatArray(value, 0);
            case Vector2Array: return vector2Array(parseFloatArray(value, 0));
            case Vector3Array: return vector3Array(parseFloatArray(value, 0));
            case Vector4Array: return vector4Array(parseFloatArray(value, 0));
            default:
                throw new UnsupportedOperationException(type+" cannot have a declared default value.");
        }
    }
    private static Object getTypeDefaultValue(VarType type) throws IOException {
        switch (type) {
            case Boolean: return false;
            case Int:
            case Float: return 0;
            case Vector2: return Vector2f.ZERO;
            case Vector3: return Vector3f.ZERO;
            case Vector4: return Vector4f.ZERO;
            case Matrix3: return Matrix3f.IDENTITY;
            case Matrix4: return Matrix4f.IDENTITY;
            case IntArray: return new int[0];
            case FloatArray: return new float[0];
            case Vector2Array: return new Vector2f[0];
            case Vector3Array: return new Vector3f[0];
            case Vector4Array: return new Vector4f[0];
            case Matrix3Array: return new Matrix3f[0];
            case Matrix4Array: return new Matrix4f[0];
            default: return null;
        }
    }
    private static void readDefines(Statement statement, LinkedList<ComputeDefine> defines) throws IOException {
        for (Statement s : statement.getContents()) {
            String[] args = s.getLine().split(":", 2);
            String defName = args[0].trim();
            String paramName = defName;
            String value = null;
            if (args.length > 1) {
                paramName = args[1].trim();
                if (paramName.startsWith(DEFINE_LITERAL)) {
                    value = paramName.substring(1);
                    paramName = defName;
                }
            }
            defines.add(new ComputeDefine(paramName, defName, value));
        }
    }
    
    private static int[] parseIntArray(String value, int length) throws IOException {
        String[] args = value.split(" ", length);
        if (length > 0 && args.length != length) {
            throw new IOException("Not enough arguments (requires: "+length+", found: "+args.length+')');
        }
        length = args.length;
        int[] values = new int[length];
        for (length--; length >= 0; length--) {
            values[length] = Integer.parseInt(args[length].trim());
        }
        return values;
    }
    private static float[] parseFloatArray(String value, int length) throws IOException {
        String[] args = value.split(" ", length);
        if (length > 0 && args.length != length) {
            throw new IOException("Not enough arguments (requires: "+length+", found: "+args.length+')');
        }
        length = args.length;
        float[] values = new float[length];
        for (length--; length >= 0; length--) {
            values[length] = Float.parseFloat(args[length].trim());
        }
        return values;
    }
    private static Vector2f vector2(float[] values) {
        return new Vector2f(values[0], values[1]);
    }
    private static Vector3f vector3(float[] values) {
        return new Vector3f(values[0], values[1], values[2]);
    }
    private static Vector4f vector4(float[] values) {
        return new Vector4f(values[0], values[1], values[2], values[3]);
    }
    private static Vector2f[] vector2Array(float[] values) {
        Vector2f[] array = new Vector2f[values.length >> 1];
        for (int i = 0; i < array.length; i++) {
            int j = i << 1;
            array[i] = new Vector2f(values[j], values[j + 1]);
        }
        return array;
    }
    private static Vector3f[] vector3Array(float[] values) {
        Vector3f[] array = new Vector3f[values.length / 3];
        for (int i = 0; i < array.length; i++) {
            int j = i * 3;
            array[i] = new Vector3f(values[j], values[j + 1], values[j + 2]);
        }
        return array;
    }
    private static Vector4f[] vector4Array(float[] values) {
        Vector4f[] array = new Vector4f[values.length >> 2];
        for (int i = 0; i < array.length; i++) {
            int j = i << 2;
            array[i] = new Vector4f(values[j], values[j + 1], values[j + 2], values[j + 3]);
        }
        return array;
    }
    
}
