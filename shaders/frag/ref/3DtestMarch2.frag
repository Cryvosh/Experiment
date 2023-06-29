#version 330 core

layout(location = 0) out vec4 color;

uniform mat4 iViewMatrix;
uniform vec3 iPosition;
uniform vec2 iResolution;
uniform float iGlobalTime;
uniform vec2 iMouse;
uniform float iVerticalFOV;

float map(vec3 point) {
	vec3 q = fract(point) * 2.0 - 1.0;
	return length(q) - 0.25;
}

float trace(vec3 origin, vec3 ray) {
	float depth = 0.0;
	for (int i = 0; i < 32; i++) {
		vec3 p = origin + ray * depth;
		float dist = map(p);
		depth += dist * 0.5;
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

	float t = trace(iPosition, worldDir);
	float fog = 1.0 / (1.0 + t * t * 0.1);
	vec3 fc = vec3(fog);

	color = vec4(fc, 1.0);
}