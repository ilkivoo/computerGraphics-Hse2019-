#version 330 core
out vec4 FragColor;

in VS_OUT {
    vec3 FragPos;
    vec2 TexCoords;
    vec3 Tlp;
    vec3 Tvp;
    vec3 Tfp;
} fs_in;

uniform sampler2D diffuseMap;
uniform sampler2D normalMap;
uniform sampler2D depthMap;
uniform float heightScale;
vec2 ParallaxMapping(vec2 tC, vec3 viewDir)
{ 
    const float minLayers = 8;
    const float maxLayers = 32;
    float numLayers = mix(maxLayers, minLayers, abs(dot(vec3(0.0, 0.0, 1.0), viewDir))); 
    float layerDepth = 1.0 / numLayers;
    float currentLD = 0.0;
    vec2 P = viewDir.xy / viewDir.z * heightScale; 
    vec2 deltaTC = P / numLayers;
    vec2  currentTexCoords     = tC;
    float currentDmv = texture(depthMap, currentTexCoords).r;
    while(currentLD < currentDmv)
    {
        currentTexCoords -= deltaTC;
        currentDmv = texture(depthMap, currentTexCoords).r;
        currentLD += layerDepth;
    }
    vec2 prevTC = currentTexCoords + deltaTC;
    float afterDepth  = currentDmv - currentLD;
    float beforeDepth = texture(depthMap, prevTC).r - currentLD + layerDepth;
    float weight = afterDepth / (afterDepth - beforeDepth);
    vec2 finalTexCoords = prevTC * weight + currentTexCoords * (1.0 - weight);
    return finalTexCoords;
}

void main()
{           
    vec3 viewDir = normalize(fs_in.Tvp - fs_in.Tfp);
    vec2 tC = fs_in.TexCoords;
    tC = ParallaxMapping(fs_in.TexCoords,  viewDir);
    if(tC.x > 1.0 || tC.y > 1.0 || tC.x < 0.0 || tC.y < 0.0) discard;
    vec3 normal = texture(normalMap, tC).rgb;
    normal = normalize(normal * 2.0 - 1.0);   
    vec3 color = texture(diffuseMap, tC).rgb;
    vec3 ambient = 0.1 * color;
    vec3 lightDir = normalize(fs_in.Tlp - fs_in.Tfp);
    float diff = max(dot(lightDir, normal), 0.0);
    vec3 diffuse = diff * color;
    vec3 reflectDir = reflect(-lightDir, normal);
    vec3 halfwayDir = normalize(lightDir + viewDir);  
    float spec = pow(max(dot(normal, halfwayDir), 0.0), 32.0);
    vec3 specular = vec3(0.2) * spec;
    FragColor = vec4(ambient + diffuse + specular, 1.0);
}