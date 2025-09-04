package Util;

public class Time {

    public static float startTime = System.nanoTime();

    public static float getTime() {
        return (float) ((System.nanoTime() - startTime) * 1E-9);
    }
}
