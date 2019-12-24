package ru.hse.alyokhina;


import com.jogamp.opengl.GL2;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.event.MouseInputAdapter;

public final class  MouseInputAdapterImpl extends MouseInputAdapter implements MouseWheelListener {
    private double zoomFactor = 5.0;
    private boolean isPressed;
    private Pair<Integer, Integer> pressPoint = new Pair<>(0, 0);
    private Pair<Integer, Integer> currentRotation = new Pair<>(0, 0);
    private Pair<Integer, Integer> accumulatedRotation = new Pair<>(0, 0);
    private static final double ZOOM_SPEED = 0.03;

    public double getZoomFactor() {
        return zoomFactor;
    }

    public Pair<Integer, Integer> getAccumulatedRotation() {
        return accumulatedRotation;
    }

    public Pair<Integer, Integer> getCurrentRotation() {
        return currentRotation;
    }

    public void mouseWheelMoved(MouseWheelEvent event) {
        this.zoomFactor += this.zoomFactor * (double) event.getWheelRotation() * ZOOM_SPEED;
    }

    public void mouseDragged(MouseEvent event) {
        if (!this.isPressed) {
            this.isPressed = true;
            this.pressPoint = this.toPoint(event);
        } else {
            this.currentRotation = Pair.minus(this.toPoint(event), this.pressPoint);
        }

    }

    public void mouseMoved(MouseEvent e) {
        this.isPressed = false;
        this.accumulatedRotation = Pair.plus(this.accumulatedRotation, this.currentRotation);
        this.currentRotation = new Pair<>(0, 0);
    }

    private Pair<Integer, Integer> toPoint(MouseEvent mouseEventPoint) {
        return new Pair<>(mouseEventPoint.getX(), mouseEventPoint.getY());
    }
}
