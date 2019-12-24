package ru.hse.alyokhina;


import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.GLBuffers;
import com.mokiat.data.front.parser.*;
import kotlin.io.TextStreamsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Charsets;
import org.jetbrains.annotations.Nullable;
import uno.buffer.HelpersKt;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.List;

import static com.jogamp.opengl.GL.GL_TRIANGLES;

public final class Main extends JFrame implements GLEventListener {
    public static final int width = 700;
    public static final int height= 700;
    public static final float speedDissolve = 0.01F;


    private final MouseInputAdapterImpl mouseInputAdapter;
    private final OBJModel objModel;
    private float threshold;
    private int dissolveFact;


    private int fragmentShaderId;
    private int vertexShaderId;
    private int programId;

    private static final double ROTATION_FACT = 0.5;

    Main() throws IOException {
        this.mouseInputAdapter = new MouseInputAdapterImpl();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("obj/taxi.obj")) {
            assert is != null;
            objModel = new OBJParser().parse(is);
        }
        threshold = 0.0f;
        dissolveFact = 1;
        GLProfile profile = GLProfile.get(GLProfile.GL2);
        GLCapabilities capabilities = new GLCapabilities(profile);
        GLCanvas canvas = new GLCanvas(capabilities);
        canvas.addGLEventListener(this);
        canvas.addMouseWheelListener(this.mouseInputAdapter);
        canvas.addMouseListener(this.mouseInputAdapter);
        canvas.addMouseMotionListener(this.mouseInputAdapter);
        this.getContentPane().add(canvas);
        canvas.requestFocusInWindow();
        FPSAnimator animator = new FPSAnimator(canvas, 60, true);
        animator.start();
        this.setSize(width, height);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setResizable(false);
    }

    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();

        double zoomFactor = mouseInputAdapter.getZoomFactor();
        Pair<Integer, Integer> accumulatedRotation = mouseInputAdapter.getAccumulatedRotation();
        Pair<Integer, Integer> currentRotation = mouseInputAdapter.getCurrentRotation();
        gl.glTranslated(0.0D, -0.5D, -zoomFactor);
        gl.glRotated((double) (accumulatedRotation.getFirst() + currentRotation.getFirst()) * ROTATION_FACT, 0.0D, 1.0D, 0.0D);
        gl.glRotated((double) -(accumulatedRotation.getSecond() + currentRotation.getSecond()) * ROTATION_FACT, 1.0D, 0.0D, 0.0D);

        setValue(gl, "threshold", this.threshold);
        gl.glBegin(GL_TRIANGLES);

        objModel.getObjects().stream()
                .flatMap(it -> it.getMeshes().stream())
                .flatMap(it -> it.getFaces().stream())
                .forEach(it -> {
                    List<OBJDataReference> vertices = it.getReferences();
                    for (int i = 2; i < vertices.size(); i++) {
                        draw(Arrays.asList(vertices.get(0), vertices.get(i - 1), vertices.get(i)), gl);
                    }
                });

        gl.glEnd();
        gl.glFlush();
        threshold += speedDissolve * dissolveFact;
        if (threshold < 0.0) {
            dissolveFact = 1;
        }

        if (this.threshold > 3.0) {
            this.dissolveFact = -1;
        }

    }

    private void draw(List<OBJDataReference> vertices, GL2 gl) {
        vertices.forEach(it -> {
            if (it.hasNormalIndex()) {
                OBJNormal normal = objModel.getNormals().get(it.normalIndex);
                gl.glNormal3f(normal.x, normal.y, normal.z);
            }
            if (it.hasTexCoordIndex()) {
                OBJTexCoord texCoord = objModel.getTexCoords().get(it.texCoordIndex);
                gl.glTexCoord2f(texCoord.u, texCoord.v);
            }
            OBJVertex vertex = objModel.getVertices().get(it.vertexIndex);
            gl.glVertex3f(vertex.x, vertex.y, vertex.z);
        });

    }


    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        this.programId = gl.glCreateProgram();

        try {
            this.setUpShader(gl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            setUpTexture("texture/texture.png", gl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        gl.glClearColor(0f, 0f, 0f, 0f);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        new GLU().gluPerspective(45.0, 1.0, 1.0, 50.0);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glEnable(GL2.GL_DEPTH_TEST);
    }

    private void setUpTexture(String sourceFile, GL2 gl) throws IOException {
        gl.glGenTextures(1, GLBuffers.newDirectIntBuffer(new int[]{0}));
        gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        setValue(gl, "dissolve", 0);

        InputStream texture = this.getClass().getClassLoader().getResourceAsStream(sourceFile);
        assert texture != null;
        BufferedImage image = ImageIO.read(texture);

        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGB, image.getWidth(), image.getHeight(), 0, GL.GL_RGB, GL.GL_UNSIGNED_BYTE, toBuffer(image));
        gl.glGenerateMipmap(GL.GL_TEXTURE_2D);
        gl.glEnable(GL.GL_TEXTURE_2D);
    }

    private static ByteBuffer toBuffer(BufferedImage bufferedImage) {
        final byte[] byteArray = ((DataBufferByte) bufferedImage.getData().getDataBuffer()).getData();
        return HelpersKt.toByteBuffer(byteArray);
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    }

    public void dispose(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        delete(gl);
    }

    private void setUpShader(GL2 gl) throws Exception {
        programId = gl.glCreateProgram();
        URL fragmentShaderUrl = this.getClass().getClassLoader().getResource("glsl/fragment.glsl");
        assert fragmentShaderUrl != null;
        String fragmentShaderCode = new String(TextStreamsKt.readBytes(fragmentShaderUrl), Charsets.UTF_8);
        URL vertexShaderUrl = this.getClass().getClassLoader().getResource("glsl/vertex.glsl");
        assert vertexShaderUrl != null;
        String vertexShaderCode = new String(TextStreamsKt.readBytes(vertexShaderUrl), Charsets.UTF_8);
        fragmentShaderId = this.createShaderProgram(gl, fragmentShaderCode, GL2.GL_FRAGMENT_SHADER);
        vertexShaderId = this.createShaderProgram(gl, vertexShaderCode, GL2.GL_VERTEX_SHADER);
        gl.glLinkProgram(this.programId);
        this.checkProgramErrors(gl, 35714);
        gl.glValidateProgram(this.programId);
        this.checkProgramErrors(gl, 35715);
        gl.glUseProgram(this.programId);
    }

    private int createShaderProgram(GL2 gl, String shaderCode, int shaderType) throws Exception {
        int shaderId = gl.glCreateShader(shaderType);
        if (shaderId == 0) {
            throw new Exception("Unable to create shader");
        } else {
            gl.glShaderSource(shaderId, 1, new String[]{shaderCode}, null);
            gl.glCompileShader(shaderId);
            this.checkShaderErrors(gl, shaderId, 35713);
            gl.glAttachShader(this.programId, shaderId);
            return shaderId;
        }
    }

    private void checkShaderErrors(GL2 gl, int shaderId, int statusToCheck) throws Exception {
        IntBuffer intBuffer = IntBuffer.allocate(1);
        gl.glGetShaderiv(shaderId, statusToCheck, intBuffer);
        if (intBuffer.get(0) != 1) {
            gl.glGetShaderiv(shaderId, 35716, intBuffer);
            int size = intBuffer.get(0);
            ByteBuffer byteBuffer = ByteBuffer.allocate(size);
            gl.glGetShaderInfoLog(shaderId, size, intBuffer, byteBuffer);
            throw new Exception("Shader error: " + new String(byteBuffer.array()));
        }
    }

    private void checkProgramErrors(GL2 gl, int statusToCheck) throws Exception {
        IntBuffer intBuffer = IntBuffer.allocate(1);
        gl.glGetProgramiv(this.programId, statusToCheck, intBuffer);
        if (intBuffer.get(0) != 1) {
            gl.glGetProgramiv(this.programId, GL2.GL_INFO_LOG_LENGTH, intBuffer);
            int size = intBuffer.get(0);
            ByteBuffer byteBuffer = ByteBuffer.allocate(size);
            gl.glGetProgramInfoLog(this.programId, size, intBuffer, byteBuffer);
            throw new Exception("Program  error: " + new String(byteBuffer.array()));
        }
    }


    public void setValue(GL2 gl, String variable, float value) {
        int location = gl.glGetUniformLocation(this.programId, variable);
        gl.glUniform1f(location, value);
    }

    public void setValue(GL2 gl, String variable, int value) {
        Intrinsics.checkParameterIsNotNull(variable, "variable");
        int location = gl.glGetUniformLocation(this.programId, variable);
        gl.glUniform1i(location, value);
    }

    public void setValue(GL2 gl, String variable, float value1, float value2) {
        int location = gl.glGetUniformLocation(this.programId, variable);
        gl.glUniform2f(location, value1, value2);
    }


    public void delete(GL2 gl) {
        gl.glDetachShader(this.programId, this.vertexShaderId);
        gl.glDetachShader(this.programId, this.fragmentShaderId);
        gl.glDeleteProgram(this.programId);
    }

    public static void main(String[] var0) throws IOException {
        new Main();
    }
}
