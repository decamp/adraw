#version 330
// Note: "#version 330" MUST be changed to "#version 300" es on compile.


smooth in vec4 color;

out vec4 fragColor;

void main()  {
	fragColor = color;
	if( color.a <= 0.0 ) {
		discard;
	}
}
