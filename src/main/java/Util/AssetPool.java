package Util;

import Audio.Track;
import Components.Sprites.Spritesheet;
import Rendering.Shader;
import Rendering.Texture;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AssetPool {

    private static final Map<String, Shader> shaders = new HashMap<>();
    private static final Map<String, Texture> textures = new HashMap<>();
    private static final Map<String, Spritesheet> spritesheets = new HashMap<>();
    private static final Map<String, Track> tracks = new HashMap<>();

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
            Texture texture = new Texture();
            texture.init(name);
            textures.put(name, texture);
            return texture;
        }
    }

    public static void addSpritesheet(String name, Spritesheet spritesheet) {
        if(!spritesheets.containsKey(name))
            spritesheets.put(name, spritesheet);
    }

    public static Spritesheet getSpritesheet(String name) {
        if(spritesheets.containsKey(name))
            return spritesheets.get(name);
        else
            assert false : "Error: Spritesheet not in AssetPool " + name + "...";
        return null;
    }

    public static Track getTrack(String filename) {
        if(tracks.containsKey(filename))
            return tracks.get(filename);
        return null;
    }

    public static Track addTrack(String filename, boolean doLoop) {
        if(tracks.containsKey(filename))
            return tracks.get(filename);
        else {
            Track track = new Track(filename, doLoop);
            tracks.put(filename, track);
            return track;
        }
    }

    public static Collection<Track> getAllTracks() {
        return tracks.values();
    }
}
