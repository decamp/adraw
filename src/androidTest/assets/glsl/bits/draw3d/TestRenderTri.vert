#version 330
// Note: "#version 330" MUST be changed to "#version 300" es on compile.

uniform mat4 VIEW_MAT;
uniform mat4 PROJ_VIEW_MAT;

layout( std140 ) uniform FOG {
	vec4 COLOR;
	vec4 PARAMS; // (density, startDist)
} fog;

layout( location = 0 ) in vec4 inPos;
layout( location = 1 ) in vec4 inColor;

smooth out vec4 color;

vec4 applyFog( vec4 eyeVert, vec4 color ) {
	float fogCoord = length( eyeVert.xyz ) / eyeVert.w;
	float fogFactor = exp( -fog.PARAMS.x * ( fogCoord - fog.PARAMS.y ) );
	fogFactor = clamp( fogFactor, 0.0, 1.0 );
	return mix( fog.COLOR, color, fogFactor );
}


void main() {
	gl_Position = PROJ_VIEW_MAT * inPos;
	color = applyFog( VIEW_MAT * inPos, inColor );
	//color = inColor;
}
