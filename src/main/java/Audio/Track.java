package Audio;

import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_decode_filename;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.libc.LibCStdlib.free;

public class Track {

    private String filename;
    private int bufferID, sourceID;
    private boolean isPlaying;

    public Track(String filename, boolean doLoop) {
        this.filename = filename;
        stackPush();
        IntBuffer channelsBuffer = stackMallocInt(1);
        stackPush();
        IntBuffer sampleRateBuffer = stackMallocInt(1);
        ShortBuffer rawAudioBuffer = stb_vorbis_decode_filename("./assets/audio/" + filename, channelsBuffer, sampleRateBuffer);
        if(rawAudioBuffer == null) {
            System.out.println("Error loading track: " + filename + "...");
            stackPop();
            stackPop();
            return;
        }
        int channels = channelsBuffer.get();
        int sampleRate = sampleRateBuffer.get();
        stackPop();
        stackPop();
        int format = -1;
        if(channels == 1)
            format = AL_FORMAT_MONO16;
        if(channels == 2)
            format = AL_FORMAT_STEREO16;
        bufferID = alGenBuffers();
        alBufferData(bufferID, format, rawAudioBuffer, sampleRate);
        sourceID = alGenSources();
        alSourcei(sourceID, AL_BUFFER, bufferID);
        alSourcei(sourceID, AL_LOOPING, doLoop ? 1 : 0);
        alSourcei(sourceID, AL_POSITION, 0);
        alSourcef(sourceID, AL_GAIN, 0.3f);
        free(rawAudioBuffer);
    }

    public void play() {
        int state = alGetSourcei(sourceID, AL_SOURCE_STATE);
        if(state == AL_STOPPED) {
            isPlaying = false;
            alSourcei(sourceID, AL_POSITION, 0);
        }
        if(!isPlaying) {
            alSourcePlay(sourceID);
            isPlaying = true;
        }
    }

    public void stop() {
        if(isPlaying) {
            alSourceStop(sourceID);
            isPlaying = false;
        }
    }

    public void delete() {
        alDeleteSources(sourceID);
        alDeleteBuffers(bufferID);
    }

    public String getFilename() {
        return filename;
    }

    public boolean isPlaying() {
        int state = alGetSourcei(sourceID, AL_SOURCE_STATE);
        if(state == AL_PLAYING)
            isPlaying = true;
        else if(state == AL_STOPPED)
            isPlaying = false;
        return isPlaying;
    }
}
