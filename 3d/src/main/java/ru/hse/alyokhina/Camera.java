package ru.hse.alyokhina;


import com.jogamp.opengl.GL2;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.event.MouseInputAdapter;

public final class Camera extends MouseInputAdapter implements MouseWheelListener {
    private double zoomFactor = 5.0D;
    private boolean isPressed;
    private Pair<Integer, Integer> pressPoint = new Pair<>(0, 0);
    private Pair<Integer, Integer> currentRotation = new Pair<>(0, 0);
    private Pair<Integer, Integer> accumulatedRotation = new Pair<>(0, 0);
    private double x = 3.0D;
    private double y = 3.0D;
    private double z = 3.0D;
    private static final double ZOOM_SPEED = 0.03D;
    private static final double ROTATION_FACT = 0.5D;

    public final void apply(GL2 gl) {
        gl.glTranslated(0.0D, -0.5D, -this.zoomFactor);
        gl.glRotated((double) (accumulatedRotation.getFirst() + currentRotation.getFirst()) * ROTATION_FACT, 0.0D, 1.0D, 0.0D);
        gl.glRotated((double) -(accumulatedRotation.getSecond() + currentRotation.getSecond()) * ROTATION_FACT, 1.0D, 0.0D, 0.0D);
    }

    public void mouseWheelMoved(MouseWheelEvent event) {
        this.zoomFactor += this.zoomFactor * (double) event.getWheelRotation() * ZOOM_SPEED;
        this.x += this.x * (double) event.getWheelRotation() * ZOOM_SPEED;
        this.y += this.y * (double) event.getWheelRotation() * ZOOM_SPEED;
        this.z += this.z * (double) event.getWheelRotation() * ZOOM_SPEED;
    }

    public void mouseDragged(MouseEvent event) {
        if (!this.isPressed) {
            this.isPressed = true;
            this.pressPoint = this.toPoint(event);
        } else {
            this.currentRotation = this.minus(this.toPoint(event), this.pressPoint);
        }

    }

    public void mouseMoved(MouseEvent e) {
        this.isPressed = false;
        this.accumulatedRotation = this.plus(this.accumulatedRotation, this.currentRotation);
        this.currentRotation = new Pair<>(0, 0);
    }

    private final Pair<Integer, Integer> toPoint(MouseEvent mouseEventPoint) {
        return new Pair<>(mouseEventPoint.getX(), mouseEventPoint.getY());
    }

    private Pair<Integer, Integer> minus(Pair<Integer, Integer> pair1, Pair<Integer, Integer> pair2) {
        return new Pair<>(pair1.getFirst() - pair2.getFirst(), pair1.getSecond() - pair2.getSecond());
    }

    private Pair<Integer, Integer> plus(Pair<Integer, Integer> pair1, Pair<Integer, Integer> pair2) {
        return new Pair<>(pair1.getFirst() + pair2.getFirst(), pair1.getSecond() + pair2.getSecond());
    }
}
