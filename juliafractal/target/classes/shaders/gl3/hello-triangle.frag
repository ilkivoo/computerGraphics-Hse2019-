#version 330


#include semantic.glsl


in vec3 interpolatedColor;
in vec2 outPos;
layout (location = FRAG_COLOR) out vec4 outputColor;

uniform int maxIter = 100;
uniform float zoom = 1;
uniform float cX = -0.7;
uniform float cY = 0.27015;
uniform float moveX = 0;
uniform float moveY = 0;
uniform float R = 4;

uniform sampler1D colors;
uniform int colorsCount;

float make(vec2 z) {
    float zx = z.x/zoom + moveX;
    float zy = z.y/zoom + moveY;
    float i = maxIter;
    while (zx * zx + zy * zy < R && i > 0) {
        float tmp = zx * zx - zy * zy + cX;
        zy = 2.0 * zx * zy + cY;
        zx = tmp;
        i--;
    }
    return (i / maxIter);

}
void main()
{
    float val = make(outPos);
    float shift = 0.5 / colorsCount;
    outputColor = vec4(texture(colors, shift + val * (1 - 2 * shift)).rgb, 1);
}
