
varying vec2 vTextureCoord;
uniform sampler2D uTextureSampler;

void main() {
    gl_FragColor = texture2D(uTextureSampler, vTextureCoord);
}