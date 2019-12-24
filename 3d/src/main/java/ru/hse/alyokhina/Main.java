package ru.hse.alyokhina;


import java.io.IOException;

public final class Main{

    private static final String MODEL_FILE = "obj/taxi.obj";
    public static final String PHONG_FRAGMENT_SHADER = "glsl/fragment.glsl";
    public static final String PHONG_VERTEX_SHADER = "glsl/vertex.glsl";
    public static final String TEXTURE = "texture/texture.png";

    public static void main() throws IOException {
        new Frame(MODEL_FILE);
    }

    public static void main(String[] var0) throws IOException {
        main();
    }
}
