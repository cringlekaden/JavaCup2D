#version 410 core

uniform sampler2D texSampler;

in vec4 fColor;

out vec4 color;

void main() {
    color = fColor;
}