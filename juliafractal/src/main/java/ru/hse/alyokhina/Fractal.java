package ru.hse.alyokhina;

import com.jogamp.opengl.GLEventListener;

public abstract class Fractal implements GLEventListener {
    public abstract void incZoom(final float del);

    public abstract void setMoveX(final float moveX);

    public abstract void setMoveY(final float moveY);
}
