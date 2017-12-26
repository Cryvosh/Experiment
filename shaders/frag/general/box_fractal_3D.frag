#version 330 core

layout(location = 0) out vec4 color;

uniform mat4 iViewMatrix;
uniform vec3 iPosition;
uniform vec2 iResolution;
uniform float iGlobalTime;
uniform float iNearClip;
uniform vec2 iMouse;
uniform float iVerticalFOV;

const int ITERATIONS = 128;
const int FRAC_ITER = 25;
const float SCALE = 2.8;

vec3 CSize;

float DE(vec3 p) {

	// vec4 q0 = vec4 (mod(p, 10)-5, 1.);
	vec4 q0 = vec4 (p, 1.);
  	vec4 q = q0;

	 for (int n = 0; n < FRAC_ITER; n++) {
	    q.xyz = clamp (q.xyz, -1., 1.) * 2. - q.xyz;
	    q *= SCALE / clamp (dot (q.xyz, q.xyz), 0.5, 1.);
	    q += q0;
  	}

	return length (q.xyz) / abs (q.w);
}

float march(vec3 origin, vec3 direction, float nearClipDist) {
	float depth = nearClipDist;
	int steps;
	for (steps = 0; steps < ITERATIONS; steps++) {
		float dist = DE(origin + depth * direction);	
		float epsilon = depth * 0.002;
		
		if(dist < epsilon) {
			break;
		}
			
		depth += dist;
	}
	return 1.0 - float(steps) / ITERATIONS;
}

vec3 rayDirection(float fieldOfView, vec2 size, vec4 fragCoord) {
    vec2 xy = fragCoord.xy - size / 2.0;
    float z = size.y / tan(radians(fieldOfView) / 2.0);
    return normalize(vec3(xy, -z));
}

void main() {
	vec3 viewDir = rayDirection(iVerticalFOV, iResolution.xy, gl_FragCoord);
	vec3 worldDir = (iViewMatrix * vec4(viewDir, 0.0)).xyz;
	
	vec3 start = vec3(2.0, 0.0, 1.0);
	float dist = march(iPosition+start, worldDir, iNearClip);

	color = vec4(vec3(dist), 1.0);
}