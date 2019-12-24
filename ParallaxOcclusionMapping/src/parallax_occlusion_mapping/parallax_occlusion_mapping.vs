#version 330 core
layout (location = 0) in vec3 aPos;
layout (location = 1) in vec3 aNormal;
layout (location = 2) in vec2 aTexCoords;
layout (location = 3) in vec3 aT;
layout (location = 4) in vec3 aB;

out VS_OUT {
    vec3 FragPos;
    vec2 TexCoords;
    vec3 Tlp;
    vec3 Tvp;
    vec3 Tfp;
} vs_out;

uniform mat4 projection;
uniform mat4 view;
uniform mat4 model;

uniform vec3 lightPos;
uniform vec3 viewPos;

void main()
{
    vs_out.FragPos = vec3(model * vec4(aPos, 1.0));   
    vs_out.TexCoords = aTexCoords;
    vec3 T = normalize(mat3(model) * aT);
    vec3 B = normalize(mat3(model) * aB);
    vec3 N = normalize(mat3(model) * aNormal);
    mat3 TBN = transpose(mat3(T, B, N));
    vs_out.Tlp = TBN * lightPos;
    vs_out.Tvp  = TBN * viewPos;
    vs_out.Tfp  = TBN * vs_out.FragPos;
    gl_Position = projection * view * model * vec4(aPos, 1.0);
}