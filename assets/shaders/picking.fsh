#version 410 core

in vec4 fColor;
in vec2 fTexCoord;
in float fTexID;
in float fEntityID;

uniform sampler2D uTextures[8];

layout(location=0) out int outEntityID;

void main() {
    vec4 texColor = vec4(1, 1, 1, 1);
    if(fTexID > 0) {
        int texID = int(fTexID);
        texColor = fColor * texture(uTextures[texID], fTexCoord);
    }
    if(texColor.a < 0.5) {
        discard;
    }
    outEntityID = int(fEntityID);
}