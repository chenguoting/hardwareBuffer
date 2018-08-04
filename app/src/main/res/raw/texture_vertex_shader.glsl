#version 300 es

uniform mat4 uMVPMatrix;
uniform mat4 uSTMatrix;
in vec4 aPosition;
in vec4 aTextureCoord;

out vec2 vTextureCoord;

void main() {
    gl_Position = uMVPMatrix * aPosition;
    vTextureCoord = (uSTMatrix * aTextureCoord).st;
}