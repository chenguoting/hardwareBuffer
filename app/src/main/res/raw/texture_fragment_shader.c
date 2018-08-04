#extension GL_OES_EGL_image_external : require

varying vec2 vTextureCoord;
uniform sampler2D uTextureSampler;

void main() {
  gl_FragColor = texture2D(uTextureSampler, vTextureCoord);
}