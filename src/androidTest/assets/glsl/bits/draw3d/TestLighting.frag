#version 330
// Note: "#version 330" MUST be changed to "#version 300" es on compile.

#define LIGHT_NUM 1

uniform mat3 NORM_MAT;

layout(std140) uniform LIGHTS {
	vec3 AMBIENT;
	vec3 COLOR       [ LIGHT_NUM ];
	vec3 POS         [ LIGHT_NUM ]; // w == 0.0 if directional, 1.0 if positional.
	vec3 DIR         [ LIGHT_NUM ]; // MUST be normalized.
	vec3 ATTENUATION [ LIGHT_NUM ]; // x = constant; y = linear; z = quadratic
	vec4 SHAPE       [ LIGHT_NUM ]; // x = 0.0 if directional, 1.0 if positional
	 							    // y = cos( cutoff_ang ); 
	 							    // z = spotlight cutoff exponent
} lights;

layout(std140) uniform MATERIALS {
	vec3  AMBIENT[2];
	vec3  DIFFUSE[2];
	vec3  SPECULAR[2];
	vec3  EMISSIVE[2];
	float SHININESS[2];
	float ALPHA[2];
} materials;

smooth in vec3 vertEye;
smooth in vec3 vertNorm;
out vec4 fragColor;

void main() {
	int side = int(gl_FrontFacing);
	vec3 finalColor = materials.EMISSIVE[side] + materials.AMBIENT[side] * lights.AMBIENT;
	vec3 unitSurfaceNorm = normalize( vertNorm );
	vec3 unitEyeToFrag = normalize( vertEye  );

	for( int i = 0; i < LIGHT_NUM; i++ ) {
		// Two possible directions: 
		// 1. For spotlights and pointlights, delta is vector from light to fragment
		//    In this case, lights.POS[i].w == 1
		// 2. For directional lights, delta is direction of light.
		//    In this case, lights.POS[i].w == 0
		vec3 lightDelta = vertEye - lights.POS[i].xyz;
		vec3 unitLightDir = normalize( mix( lights.DIR[i], lightDelta, lights.SHAPE[i][0] ) );
		

		float dist  = length( lightDelta );
		float atten = 1.0 / dot( vec3( 1.0, dist, dist * dist ), lights.ATTENUATION[i] );

		// Diffuse and specular.
		float lambertTerm = dot( unitSurfaceNorm, -unitLightDir );
		vec3 lightSum = vec3( 0.0 );
		
		if( lambertTerm > 0.0 ) {
			// Spotlight cutoff or attenuation.
			float cosAng = dot( unitLightDir, lights.DIR[i] );
			lambertTerm *= step( lights.SHAPE[i][1], cosAng ) * pow( cosAng, lights.SHAPE[i][2] );
			lightSum += lambertTerm * lights.COLOR[i] * materials.DIFFUSE[side];

			float specular = dot( reflect( unitLightDir, unitSurfaceNorm ), unitEyeToFrag );
			specular = pow( max( 0.0, specular ), materials.SHININESS[side] );
			lightSum += specular    * lights.COLOR[i] * materials.SPECULAR[side];
		}

		finalColor += atten * lightSum;
	}
	
	fragColor = vec4( finalColor, materials.ALPHA[side] );
}


