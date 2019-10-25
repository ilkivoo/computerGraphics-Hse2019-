package ru.hse.alyokhina.juliafractal;

import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.FPSAnimator;

public class FractalPainter {
    private static String TITLE = "Fractal";
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;
    private static final int FPS = 60;

    static {
        GLProfile.initSingleton();
    }

    public static void main(String[] args) {
        GLProfile glProfile = GLProfile.get(GLProfile.GL3);
        GLCapabilities glCapabilities = new GLCapabilities(glProfile);

        GLWindow window = GLWindow.create(glCapabilities);

        window.setTitle("Julia Fractal");
        window.setSize(1024, 768);

        window.setVisible(true);

        window.addGLEventListener(new JuliaFractal());

        Animator animator = new Animator(window);
        animator.start();

        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowDestroyed(WindowEvent e) {
                animator.stop();
                System.exit(1);
            }
        });
    }
}
