package ru.hse.alyokhina;

import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.MouseListener;

public class FractalMouseListener implements MouseListener {
    private final Fractal fractal;

    private final int width;
    private final int height;

    private int curPositionX = 0;
    private int curPositionY = 0;

    public FractalMouseListener(final Fractal fractal,
                                final int width,
                                final int height) {
        this.fractal = fractal;
        this.width = width;
        this.height = height;
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
        float delX = (mouseEvent.getX() - curPositionX) / 2.0f;
        float delY = (mouseEvent.getY() - curPositionY) / 2.0f;
        System.out.println(mouseEvent.getX() + " " + width);
        System.out.println(mouseEvent.getY() + " " + height);
        System.out.println(curPositionX + " " + delX + " " + curPositionY + " " + delY);
        fractal.setMoveX(-1f);
        fractal.setMoveY(-1f);
        setPosition(mouseEvent.getX(), mouseEvent.getY());
    }


    private float getCoordinate(int pos, int size) {
        return (pos - size / 2.0f) / (2.0f * (float) size);
    }

    @Override
    public void mouseWheelMoved(MouseEvent mouseEvent) {
        System.out.println(1 + 0.1f * mouseEvent.getRotation()[1]);
        setPosition(mouseEvent.getX(), mouseEvent.getY());
    }


}
