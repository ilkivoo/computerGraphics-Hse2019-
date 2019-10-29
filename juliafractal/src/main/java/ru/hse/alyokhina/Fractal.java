package ru.hse.alyokhina;

import com.jogamp.opengl.GLEventListener;

public abstract class Fractal implements GLEventListener {
    public abstract void incZoom(final float del);

    public abstract void move(final float moveX, final float moveY);

    public abstract void setMaxIter(final int maxIter);

    public abstract int getMaxIter();

    public abstract void setR(final float R);

    public abstract float getR();

    public abstract void revertAuto();

    public abstract boolean getAuto();

    public abstract void setParameterX(final float x);

    public abstract float getParameterX();

    public abstract void setParameterY(final float y);

    public abstract float getParameterY();
}
