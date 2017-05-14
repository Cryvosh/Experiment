#version 330 core

layout(location = 0) out vec4 color;

uniform mat4 iViewMatrix;
uniform vec3 iPosition;
uniform vec2 iResolution;
uniform float iGlobalTime;
uniform vec2 iMouse;
uniform float iVerticalFOV;

const float EPSILON = 0.01;
const float size = 0.125;

float sphereSDF(vec3 point) {
	
	vec3 q = fract(point) - 0.5;
	
	//Equivalent to:
	//float m = 1;
	//vec3 q = mod(point, m) - 0.5*m;
	
	return length(q) - size;
}

float march(vec3 origin, vec3 direction, float nearClipDist) {
	float depth = nearClipDist;
	for (int i = 0; i < 32; i++) {
		float dist = sphereSDF(origin + depth * direction);	
		
		if(dist < EPSILON) {
			return depth;
		}
			
		depth += dist;
	}
	return depth;
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
	
	float fog = 1.0 / (1.0 + dist * dist * 0.1);
	vec3 fc = vec3(fog);

	color = vec4(fc, 1.0);
}