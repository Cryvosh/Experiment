#version 330 core

layout(location = 0) out vec4 color;

uniform mat4 iViewMatrix;
uniform vec3 iPosition;
uniform vec2 iResolution;
uniform float iGlobalTime;
uniform float iNearClip;
uniform vec2 iMouse;
uniform float iVerticalFOV;

const int ITERATIONS = 1000;

bool isprime(int n) {
	if(n<=1) {
		return false;
	}
	
	if(n==2) {
		return true;
	}
	
	if(n%2 == 0) {
		return false;
	}
	
	for(int i = 3; i <= sqrt(n); i+=2) {
		if(n % i == 0) {
			return false;
		}
	}
	
	return true;
}

float DE_PRIMES(vec3 p) {

	float size = 0.001;

	float x = p.x - mod(p.x, size);
	float y = p.y - mod(p.y, size);
	float z = p.z - mod(p.z, size);
	
	//if (abs(y - (sin(100*x)*x*100 + cos(100*z)*y*100)/200) < size/2) {
	if (abs(y - sqrt(x*x*0.1 + z*z*0.1)) < size/2) {
		vec3 q = p-mod(p,size)+size/2;
		return abs(length(p-q)) - size/4;
	}
	
	return size/4;
}

vec2 march(vec3 origin, vec3 direction, float nearClipDist) {
	float depth = nearClipDist;
	int steps;
	for (steps = 0; steps < ITERATIONS; steps++) {
		float dist = DE_PRIMES(origin + depth * direction);	
		float epsilon = 0.000001;
		
		if(dist < epsilon) {
			break;
		}
			
		depth += dist;
	}
	return vec2(1.0 - float(steps) / ITERATIONS, depth);
}

vec3 rayDirection(float fieldOfView, vec2 size, vec4 fragCoord) {
    vec2 xy = fragCoord.xy - size / 2.0;
    float z = size.y / tan(radians(fieldOfView) / 2.0);
    return normalize(vec3(xy, -z));
}

void main() {
	vec3 viewDir = rayDirection(iVerticalFOV, iResolution.xy, gl_FragCoord);
	vec3 worldDir = (iViewMatrix * vec4(viewDir, 0.0)).xyz;

	vec2 march = march(iPosition, worldDir, iNearClip);
	
	float fog = 1.0 / (1.0 + march.y * march.y * 1/ITERATIONS * 0.00001);

	color = vec4(vec3(march.x), 1.0);
}