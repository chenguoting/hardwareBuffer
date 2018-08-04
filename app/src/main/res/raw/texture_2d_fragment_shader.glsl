#version 300 es

in vec2 vTextureCoord;
uniform sampler2D uTextureSampler;

out vec4 gl_FragColor;

void main() {
    gl_FragColor = texture(uTextureSampler, vTextureCoord);
}