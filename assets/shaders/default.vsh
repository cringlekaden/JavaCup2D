#version 410 core

uniform mat4 uProjection;
uniform mat4 uView;

layout (location = 0) in vec3 vPosition;
layout (location = 1) in vec4 vColor;

out vec4 fColor;

void main() {
    fColor = vColor;
    gl_Position = uProjection * uView * vec4(vPosition, 1.0);
}