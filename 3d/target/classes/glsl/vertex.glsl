#version 130

out vec3 normal;
out vec3 pos;
out vec2 tex_coord;

void main(void) {
    tex_coord = gl_MultiTexCoord0.xy;
    pos = vec3(gl_ModelViewMatrix * gl_Vertex);
    normal = normalize(gl_NormalMatrix * gl_Normal);
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
}
