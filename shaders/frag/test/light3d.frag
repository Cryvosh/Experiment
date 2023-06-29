#version 330 core

layout(location = 0) out vec4 color;

uniform mat4 iViewMatrix;
uniform vec3 iPosition;
uniform vec2 iResolution;
uniform float iGlobalTime;
uniform vec2 iMouse;
uniform float iVerticalFOV;

const float SIZE = 0.125;

const int ITERATIONS = 64;

float DE(vec3 p) {
	
	vec3 q = fract(p) - 0.5;
	
	//Equivalent to:
	//float m = 1;
	//vec3 q = mod(point, m) - 0.5*m;
	
	return length(p) - SIZE;
}

float march(vec3 origin, vec3 direction, float nearClipDist) {
	float depth = nearClipDist;
	int steps;
	for (steps = 0; steps < ITERATIONS; steps++) {
		float dist = DE(origin + depth * direction);	
		float epsilon = depth * 0.001;
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

	float dist = march(iPosition, worldDir, 0.0);

	color = vec4(vec3(dist), 1.0);
}