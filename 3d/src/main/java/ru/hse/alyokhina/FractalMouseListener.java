package ru.hse.alyokhina;

import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.MouseListener;

public class FractalMouseListener implements MouseListener {
    private final Fractal fractal;
    private int curPositionX = 0;
    private int curPositionY = 0;

    public FractalMouseListener(final Fractal fractal) {
        this.fractal = fractal;
    }

    private void setPosition(int curPositionX, int curPositionY) {
        this.curPositionX = curPositionX;
        this.curPositionY = curPositionY;
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
        setPosition(mouseEvent.getX(), mouseEvent.getY());
    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {
        setPosition(mouseEvent.getX(), mouseEvent.getY());
    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {
        setPosition(mouseEvent.getX(), mouseEvent.getY());
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        setPosition(mouseEvent.getX(), mouseEvent.getY());
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
        setPosition(mouseEvent.getX(), mouseEvent.getY());
    }

    @Override
    public void mouseMoved(MouseEvent mouseEvent) {
        setPosition(mouseEvent.getX(), mouseEvent.getY());
    }

    @Override
    public void mouseDragged(MouseEvent mouseEvent) {
        int x = mouseEvent.getX() - curPositionX;
        int y = mouseEvent.getY() - curPositionY;
        setPosition(mouseEvent.getX(), mouseEvent.getY());
        fractal.move(x, y);
    }
    @Override
    public void mouseWheelMoved(MouseEvent mouseEvent) {
        fractal.incZoom(1 + 0.1f * mouseEvent.getRotation()[1]);
        setPosition(mouseEvent.getX(), mouseEvent.getY());
    }


}
