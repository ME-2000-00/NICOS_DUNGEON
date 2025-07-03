#version 150

in vec3 Position;
in vec4 Color;

uniform mat4 ProjMat;
uniform mat4 ModelViewMat;

out vec4 vertColor;

void main() {
    vertColor = Color;
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
}
