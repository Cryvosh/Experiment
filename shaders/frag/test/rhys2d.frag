#version 460 core

uniform vec2 iResolution;
uniform float iGlobalTime;
uniform vec2 iPosition;
uniform float iZoom;

layout(location = 0) out vec4 color;

layout(std430, binding = 0) buffer buf
{
	int lst[];
};

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

void main() {
	vec2 uv = gl_FragCoord.xy / iResolution.xy;
	uv = uv * 2.0 - 1.0;
	uv.x *= iResolution.x / iResolution.y;
	
	vec2 c = iPosition + uv * iZoom;
	
	const int x = int(floor(c.x));
	const int y = int(floor(c.y));
	const int p = 3;
	
	const int len = 10;
	int lst[len];
	int rep[5] = int[5](0,1,1,1,1);
	
	for (int i = 0; i < len; i++) {
		lst[i] = rep[i%5];
	}
	
	const int loopIndex = min(max(1, y), 100);
	
	for (int i = 0; i < loopIndex; i++) {
		for (int j = 0; j < len; j++) {
			int temp = 0;
			for (int k = 0; k < len; k++) {
				temp += lst[k];
			}
			lst[j] = temp % p;
		}
	}
	
	float mono = sin(float(lst[x]));
	
	color = vec4(mono, mono, mono, 1.0);
}