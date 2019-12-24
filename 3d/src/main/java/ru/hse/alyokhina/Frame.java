
package ru.hse.alyokhina;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.GLBuffers;
import com.mokiat.data.front.parser.OBJDataReference;
import com.mokiat.data.front.parser.OBJFace;

import com.mokiat.data.front.parser.OBJModel;
import com.mokiat.data.front.parser.OBJNormal;
import com.mokiat.data.front.parser.OBJParser;
import com.mokiat.data.front.parser.OBJTexCoord;
import com.mokiat.data.front.parser.OBJVertex;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import org.jetbrains.annotations.Nullable;
import uno.buffer.HelpersKt;

import static com.jogamp.opengl.GL.GL_TRIANGLES;
import static ru.hse.alyokhina.Main.*;


public class Frame extends JFrame implements GLEventListener {
    private Shader shader;
    private final Camera camera;
    private final OBJModel model;
    private float dissolveThreshold;
    private int dissolveFact;
    public static final int SCREEN_WIDTH = 700;
    public static final int SCREEN_HEIGHT = 700;
    public static final float DISSOLVE_SPEED = 0.0001F;

    public Frame(String modelFilePath) throws IOException {
        this.camera = new Camera();
        try(InputStream is = getClass().getClassLoader().getResourceAsStream(modelFilePath)) {
            assert is != null;
            model = new OBJParser().parse(is);
        }
        dissolveThreshold = 0.0f;
        dissolveFact = 1;
        GLProfile profile = GLProfile.get(GLProfile.GL2);
        GLCapabilities capabilities = new GLCapabilities(profile);
        GLCanvas canvas = new GLCanvas(capabilities);
        this.setUpCanvas(canvas);
        this.setUpWindow();
    }


    private void setUpCanvas(GLCanvas canvas) {
        canvas.addGLEventListener(this);
        canvas.addMouseWheelListener(this.camera);
        canvas.addMouseListener(this.camera);
        canvas.addMouseMotionListener(this.camera);
        this.getContentPane().add(canvas);
        canvas.requestFocusInWindow();
        FPSAnimator animator = new FPSAnimator(canvas, 60, true);
        animator.start();
    }

    private void setUpWindow() {
        this.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setResizable(false);
    }

    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();
        camera.apply(gl);
        shader.setValue("threshold", this.dissolveThreshold);
        gl.glBegin(GL_TRIANGLES);

        model.getObjects().stream()
                .flatMap(it -> it.getMeshes().stream())
                .flatMap(it -> it.getFaces().stream())
                .forEach(it -> drawFace(it, gl));

        gl.glEnd();
        gl.glFlush();
        dissolveThreshold += DISSOLVE_SPEED * dissolveFact;
        if (dissolveThreshold < 0.0) {
            dissolveFact = 1;
        }

        if (this.dissolveThreshold > 3.0) {
            this.dissolveFact = -1;
        }

    }

    private void drawFace(OBJFace face, GL2 gl) {
        List<OBJDataReference> vertices = face.getReferences();
        for (int i = 2; i < vertices.size(); i++) {
            drawVertices(Arrays.asList(vertices.get(0), vertices.get(i - 1), vertices.get(i)), gl);
        }

    }

    private void drawVertices(List<OBJDataReference> vertices, GL2 gl) {
        vertices.forEach(it -> {
            if (it.hasNormalIndex()) {
                OBJNormal normal = model.getNormals().get(it.normalIndex);
                gl.glNormal3f(normal.x, normal.y, normal.z);
            }
            if (it.hasTexCoordIndex()) {
                OBJTexCoord texCoord = model.getTexCoords().get(it.texCoordIndex);
                gl.glTexCoord2f(texCoord.u, texCoord.v);
            }
            OBJVertex vertex = model.getVertices().get(it.vertexIndex);
            gl.glVertex3f(vertex.x, vertex.y, vertex.z);
        });

    }

    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        try {
            shader = new Shader(gl, PHONG_FRAGMENT_SHADER, PHONG_VERTEX_SHADER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            setUpTexture(TEXTURE, gl);
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
        gl.glGenTextures(1, GLBuffers.newDirectIntBuffer(new int[]{ 0}));
        gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        shader.setValue("dissolve", 0);

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

    public void dispose(@Nullable GLAutoDrawable drawable) {
        shader.delete();
    }
}

