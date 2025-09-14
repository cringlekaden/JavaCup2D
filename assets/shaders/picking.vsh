#version 410 core

layout (location = 0) in vec3 vPosition;
layout (location = 1) in vec4 vColor;
layout (location = 2) in vec2 vTexCoord;
layout (location = 3) in float vTexID;
layout (location = 4) in float vEntityID;

uniform mat4 uProjection;
uniform mat4 uView;

out vec4 fColor;
out vec2 fTexCoord;
out float fTexID;
out float fEntityID;

void main() {
    fColor = vColor;
    fTexCoord = vTexCoord;
    fTexID = vTexID;
    fEntityID = vEntityID;
    gl_Position = uProjection * uView * vec4(vPosition, 1.0);
}