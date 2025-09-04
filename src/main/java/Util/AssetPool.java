package Util;

import Rendering.Shader;
import Rendering.Texture;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AssetPool {

    private static Map<String, Shader> shaders = new HashMap<>();
    private static Map<String, Texture> textures = new HashMap<>();

    public static Shader getShader(String name) {
        if(shaders.containsKey(name))
            return shaders.get(name);
        else {
            Shader shader = new Shader(name);
            shaders.put(name, shader);
            return shader;
        }
    }

    public static Texture getTexture(String name) {
        if(textures.containsKey(name))
            return textures.get(name);
        else {
            Texture texture = new Texture(name);
            textures.put(name, texture);
            return texture;
        }
    }
}
