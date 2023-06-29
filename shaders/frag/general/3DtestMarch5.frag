#version 330 core

layout(location = 0) out vec4 color;

uniform mat4 iViewMatrix;
uniform vec3 iPosition;
uniform vec2 iResolution;
uniform float iGlobalTime;
uniform vec2 iMouse;
uniform float iVerticalFOV;

const float SIZE = 0.125;

const int ITERATIONS = 128;

float S(vec3 p) {
	return length(p) - 0.5;
}

float DE(vec3 p) {
	vec3 p1 = 100*vec3(-0.040906225416259809,-0.14695018939680818,0.29076565288107764);
	vec3 p2 = 100*vec3(-0.0016914014151263195,-0.18295056589513167,0.24745412841351414);
	vec3 p3 = 100*vec3(-0.044417690889990938,-0.13409330066550484,0.23882398021047019);
	vec3 p4 = 100*vec3(-0.0017993959486393906,-0.11279765434869436,0.23368404215431623);
	
	vec3 n1 = vec3(0.78374939706602387, 0.12078802229062807, 0.60921846350037911);
	vec3 n2 = vec3(-0.022078588390808330, -0.19259707222562109, -0.98102951214771728);
	vec3 n3 = vec3(-0.40951765056996170, 0.87873490380289421, 0.24519433661929688);
	vec3 n4 = vec3( -0.73764026810558581, -0.66535846176786717, -0.11482574721297135);
	
	float corners = min(min(S(p-p1), S(p-p2)), min(S(p-p3), S(p-p4)));

	float h1 = dot(p-p1, n1);
	float h2 = dot(p-p2, n2);
	float h3 = dot(p-p3, n3);
	float h4 = dot(p-p1, n4);
	float tet = max(max(h1,h2),max(h3,h4));
	
	float res = min(corners, tet);
	return res;
}

float march(vec3 origin, vec3 direction, float nearClipDist) {
	float depth = nearClipDist;
	int steps;
	for (steps = 0; steps < ITERATIONS; steps++) {
		float dist = DE(origin + depth * direction);	
		float epsilon = depth * 0.0001;
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