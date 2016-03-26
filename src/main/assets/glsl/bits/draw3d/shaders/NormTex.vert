#version 330

uniform mat4 PROJ_VIEW_MAT;
uniform mat3 NORM_MAT;

layout( location = 0 ) in vec4 inVert;
layout( location = 1 ) in vec3 inNorm;
layout( location = 2 ) in vec4 inTex;

smooth out vec4 tex;
smooth out vec3 norm;

void main() {
	tex = inTex;
	norm = normalize( NORM_MAT * inNorm );
	gl_Position = PROJ_VIEW_MAT * inVert;
}
