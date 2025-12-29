#version 330 core

uniform float time;
in vec4 color;
out vec4 fragColor;


void main()
{
    float offset = sin(time)/2+0.5;
    fragColor = vec4(color.z+offset,color.x+offset,color.y+offset, 1.0);
}