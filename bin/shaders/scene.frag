#version 330 core

// uniform float time;
uniform ivec2 wDimension;
in vec4 color;
in vec4 gl_FragCoord;
out vec4 fragColor;


void main()
{
    // float offset = sin(time)/2+0.5;
    // fragColor = vec4(color.z+offset,color.x+offset,color.y+offset, 1.0);
    // fragColor = vec4(gl_FragCoord.x/wDimension.x, gl_FragCoord.y/wDimension.y, 0, 1);
    fragColor = color;
}