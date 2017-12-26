#version 460 core

layout(location = 0) out vec4 color;

uniform mat4 iViewMatrix;
uniform vec3 iPosition;
uniform vec2 iResolution;
uniform float iGlobalTime;
uniform float iNearClip;
uniform vec2 iMouse;
uniform float iVerticalFOV;
uniform int LENGTH;
uniform int WIDTH;

layout(std430, binding = 0) buffer buf
{
	uint int_buf[];
};

const int ITERATIONS = 512;

int spiralindex(int x, int y) {
	int index = 0;
	if(x*x >= y*y) {
		index = 4*x*x - x - y;
		if (x < y) {
			index -= 2 * (x-y);
		}
	} else {
		index = 4*y*y - x - y;
		if (x < y) {
			index += 2 * (x-y);
		}	
	}
	return index;
}

int index(int x, int y, int z) {
	int width = WIDTH;
	int depth = WIDTH;
	
	if (x < 0 || y < 0 || z < 0) {
		return -1;
	}
	if (x >= width || z >= depth) {
		return -1;
	}
	
	return (width*z + x) + (depth*width*y);
}

int athbitofb(int a, uint b) {
	a = min(a, 31);
	return int(b>>a & 1);
}

int tup(vec3 p) {
	int x = int(floor(p.x));
	int y = int(floor(p.y));
	int z = int(floor(p.z));

	int a = index(x,y,z);
	
	if(a<=-1){
		return 0;
	}
	
	int index = min(a/32, LENGTH-1);
	
	int bit = a-32*index;
	
	return athbitofb(bit, int_buf[index]);
}

float DE_TUP(vec3 p) {
	if (tup(p) == 1) {
		vec3 q = floor(p)+0.5;
		return abs(length(p-q)) - 0.25;
	}
	
	return 0.5;
}

vec2 march(vec3 origin, vec3 direction, float nearClipDist) {
	float depth = nearClipDist;
	int steps;
	for (steps = 0; steps < ITERATIONS; steps++) {
		float dist = DE_TUP(origin + depth * direction);
		float epsilon = 0.1;
		
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
	
	float fog = 1.0 / (1.0 + march.y * march.y * 0.0001);

	color = vec4(vec3(march.x*fog), 1.0);
}