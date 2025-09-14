#version 410 core

in vec4 fColor;
in vec2 fTexCoord;
in float fTexID;

uniform sampler2D uTextures[8];

out vec4 color;

void main() {
    if(fTexID > 0) {
        int texID = int(fTexID);
        vec4 texColor = texture(uTextures[texID], fTexCoord);
        // Discard fully transparent fragments to avoid halos/overdraw
        if(texColor.a <= 0.1) discard;
        color = fColor * texColor;
    } else {
        color = fColor;
    }
}