#version 300 es
#extension GL_OES_EGL_image_external_essl3 : require
#extension GL_EXT_YUV_target : require

in vec2 vTextureCoord;
uniform samplerExternalOES uTextureSampler;

layout(yuv) out vec4 gl_FragColor;

void main() {
    gl_FragColor = texture(uTextureSampler, vTextureCoord);
}