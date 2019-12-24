#version 130

uniform sampler2D dissolve;
uniform float threshold;
in vec3 normal;
in vec3 pos;
in vec2 tex_coord;

void main (void) {
    vec4 v1 = clamp(gl_FrontLightProduct[0].diffuse * max(dot(normal, normalize(vec3(-3, -3, -3) - pos)), 0.0), 0.0, 1.0);
    vec4 spec= gl_FrontLightProduct[0].specular * pow(max(dot(normalize(-reflect(normalize(vec3(-3, -3, -3) - pos), normal)), normalize(-pos)), 0.0), 0.3 * gl_FrontMaterial.shininess);
    gl_FragColor = gl_FrontLightModelProduct.sceneColor + gl_FrontLightProduct[0].ambient + v1 + clamp(spec, 0.0, 1.0);

    if (texture(dissolve, tex_coord).rgb[0] + texture(dissolve, tex_coord).rgb[1] + texture(dissolve, tex_coord).rgb[2] < threshold) discard;
    if (texture(dissolve, tex_coord).rgb[0] + texture(dissolve, tex_coord).rgb[1] + texture(dissolve, tex_coord).rgb[2] < threshold + 0.1)  gl_FragColor = vec4(0, 0, 1, 0);

}
