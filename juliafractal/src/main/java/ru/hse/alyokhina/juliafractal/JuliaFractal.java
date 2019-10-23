package ru.hse.alyokhina.juliafractal;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.util.GLBuffers;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.IntBuffer;
import java.nio.FloatBuffer;

import static com.jogamp.opengl.GL.GL_ARRAY_BUFFER;
import static com.jogamp.opengl.GL.GL_COLOR_BUFFER_BIT;
import static com.jogamp.opengl.GL.GL_DEPTH_BUFFER_BIT;
import static com.jogamp.opengl.GL.GL_STATIC_DRAW;
import static com.jogamp.opengl.GL.GL_TRIANGLES;
import static com.jogamp.opengl.fixedfunc.GLPointerFunc.GL_VERTEX_ARRAY;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class JuliaFractal implements GLEventListener {
    private double theta = 0.0f;

    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;
    private final int maxIter = 300;
    private final double zoom = 1;
    private double cX = -0.7;
    private double cY = 0.27015;

    @Override
    public void init(GLAutoDrawable glAutoDrawable) {

    }

    @Override
    public void dispose(GLAutoDrawable glAutoDrawable) {

    }

    @Override
    public void display(GLAutoDrawable drawable) {

        GL2 gl = drawable.getGL().getGL2();

        int w = WINDOW_WIDTH;
        int h = WINDOW_HEIGHT;
        BufferedImage image = new BufferedImage(w, h,
                BufferedImage.TYPE_INT_RGB);

        double moveX = 0, moveY = 0;
        double zx, zy;
        gl.glBegin(GL.GL_POINTS);
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                zx = 1.5 * (x - w / 2) / (0.5 * zoom * w) + moveX;
                zy = (y - h / 2) / (0.5 * zoom * h) + moveY;
                float i = maxIter;
                while (zx * zx + zy * zy < 4 && i > 0) {
                    double tmp = zx * zx - zy * zy + cX;
                    zy = 2.0 * zx * zy + cY;
                    zx = tmp;
                    i--;
                }
                int c = Color.HSBtoRGB((maxIter / i) % 1, 1, i > 0 ? 1 : 0);
                image.setRGB(x, y, c);
                Color color = new Color(c);
                gl.glColor3f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f);

                gl.glVertex2d(getCoordinates(x, w), getCoordinates(y, h));
            }
        }
        gl.glEnd();
        update();

    }

    double getCoordinates(int value, int maxValue) {
        return ((double) value - ((double) maxValue) / 2) / (((double) maxValue) / 2);
    }

    @Override
    public void reshape(GLAutoDrawable glAutoDrawable, int i, int i1, int i2, int i3) {

    }

    private void update() {
        theta += 0.01;
        cX = sin(theta);
        cY = cos(theta);
    }

}
