#version 330


#include semantic.glsl


layout (location = POSITION) in vec2 position;
layout (location = COLOR) in vec3 color;


uniform GlobalMatrices
{
    mat4 view;
    mat4 proj;
};

uniform mat4 model;
out vec3 interpolatedColor;

out vec2 outPos;

void main() {
    gl_Position = proj * (view * (model * vec4(position, 0, 1)));
    interpolatedColor = color;
    outPos = position;
}


