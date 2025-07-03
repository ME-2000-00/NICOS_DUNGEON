#version 150

uniform mat4 ProjMat;
uniform mat4 ModelViewMat;

in vec4 Position;
in vec4 Color;

out vec4 vertexColor;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position,1.0);
    vertexColor = Color;
}
