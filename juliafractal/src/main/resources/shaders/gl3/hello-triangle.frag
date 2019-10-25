#version 330


#include semantic.glsl


// Incoming interpolated (between vertices) color from the vertex shader.
in vec3 interpolatedColor;

in vec2 outPos;

// Outgoing final color.
layout (location = FRAG_COLOR) out vec4 outputColor;


vec4 make(int n, vec2 z) {
    float zx = z.x;
    float zy = z.y;
    float i = n;
    float cX = -0.7;
    float cY = 0.27015;
    while (zx * zx + zy * zy < 4 && i > 0) {
        float tmp = zx * zx - zy * zy + cX;
        zy = 2.0 * zx * zy + cY;
        zx = tmp;
        i--;
    }
    return vec4((i / n), 0, 0, 0);

}

void main()
{
    outputColor  = make(300, outPos);
}
