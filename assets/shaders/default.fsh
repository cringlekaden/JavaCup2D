#version 410 core

in vec4 fColor;
in vec2 fTexCoord;
in float fTexID;

uniform sampler2D uTextures[8];

out vec4 color;

void main() {
    if(fTexID > 0) {
        int texID = int(fTexID);
        color = fColor * texture(uTextures[texID], fTexCoord);
    } else {
        color = fColor;
    }
}