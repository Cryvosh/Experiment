#version 330 core

layout(location = 0) out vec4 color;

uniform mat4 iViewMatrix;
uniform vec3 iPosition;
uniform vec2 iResolution;
uniform float iGlobalTime;
uniform float iNearClip;
uniform vec2 iMouse;
uniform float iVerticalFOV;

const float EPSILON = 0.0001;

const int ITERATIONS = 128;

vec3 CSize;

float DE( vec3 p )
{
	CSize = vec3(1., 1., 1.3);
	p = p.xzy;
	float scale = 1.;
	for( int i=0; i < 20;i++ )
	{
		p = 2.0*clamp(p, -CSize, CSize) - p;
		//float r2 = dot(p,p);
        float r2 = dot(p,p+sin(p.z*.3));
		float k = max((2.)/(r2), .027);
		p     *= k;
		scale *= k;
	}
	float l = length(p.xy);
	float rxy = l - 4.0;
	float n = l * p.z;
	rxy = max(rxy, -(n) / 4.);
	return (rxy) / abs(scale);
}

float march(vec3 origin, vec3 direction, float nearClipDist) {
	float depth = nearClipDist;
	int steps;
	for (steps = 0; steps < ITERATIONS; steps++) {
		float dist = DE(origin + depth * direction);	
		
		if(dist < EPSILON) {
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

	float dist = march(iPosition, worldDir, iNearClip);

	color = vec4(vec3(dist), 1.0);
}