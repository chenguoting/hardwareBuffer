#version 300 es
#extension GL_OES_EGL_image_external_essl3 : require

in vec2 vTextureCoord;
uniform samplerExternalOES uTextureSampler;

out vec4 gl_FragColor;

void main() {
    gl_FragColor = texture(uTextureSampler, vTextureCoord);
}