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

int index(vec3 p) {
	int width = 25;
	int depth = 25;
	
	int x = int(floor(p.x));
	int y = int(floor(p.y));
	int z = int(floor(p.z));
	
	if (x < 0 || y < 0 || z < 0) {
		return -1;
	}
	if (x >= width || z >= depth) {
		return -1;
	}
	
	return (width*z + x) + (depth*width*y);
}

float DE_SPHERES(vec3 p) {
	float instance = 1.0;
	p += 0.5;
	vec3 q = mod(p, instance) - 0.5*instance;
	
	return length(q);
}

float DE_PRIMES(vec3 p) {
	if (isprime(index(p))) {	
		vec3 q = floor(p)+0.5;
		return abs(length(p-q)) - 0.25;
	}
	
	return 0.5;
}

float march(vec3 origin, vec3 direction, float nearClipDist) {
	float depth = nearClipDist;
	int steps;
	for (steps = 0; steps < ITERATIONS; steps++) {
		float dist = DE_PRIMES(origin + depth * direction);	
		float epsilon = 0.01;
		
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

	float march = march(iPosition, worldDir, iNearClip);
	
	float fog = 1.0 / (1.0 + march * march * 1/ITERATIONS * 0.001);

	color = vec4(vec3(fog*march), 1.0);
}