#version 150

in mat4 ProjMat;
in mat4 ModelViewMat;

in vec3 Position;
in vec4 Color;

out vec4 vertColor;

void main() {
    vertColor = Color;
    gl_Position = vec4(Position, 1.0);
}
