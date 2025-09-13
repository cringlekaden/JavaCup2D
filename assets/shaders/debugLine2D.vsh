#version 410 core

layout (location = 0) in vec3 vPosition;
layout (location = 1) in vec3 vColor;

uniform mat4 uProjection;
uniform mat4 uView;

out vec4 fColor;

void main() {
    fColor = vec4(vColor, 1.0);
    gl_Position = uProjection * uView * vec4(vPosition, 1.0);
}