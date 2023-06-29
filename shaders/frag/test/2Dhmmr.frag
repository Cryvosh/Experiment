#version 420 core

uniform vec2 iResolution;
uniform float iGlobalTime;
uniform vec2 iPosition;
uniform float iZoom;

layout(location = 0) out vec4 color;

int gridindex(int x, int y) {
	int width = 10000;

	if(x >= width || x < 0 || y < 0) {
		return -1;
	}

	int index = width*y + x;
	return index;
}

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
	for(int i = 3; i < sqrt(n) + 1; i+=2) {
		if(n % i == 0) {
			return false;
		}
	}
	return true;
}

int collatz(int num) {
	int res = 0;
	double n = double(num);
	while (n > 1) {
		if (mod(n,2) < 0.5) {
			n = n/2;
			res += 1;
		} else {
			n = 3*n + 1;
			res += 1;
		}
	}
	return res;
}

void main() {
	vec2 uv = gl_FragCoord.xy / iResolution.xy;
	uv = uv * 2.0 - 1.0;
	uv.x *= iResolution.x / iResolution.y;
	vec2 c = iPosition + uv * iZoom;
	
	int x = int(floor(c.x));
	int y = int(floor(c.y));
	int s = gridindex(x,y);
	
	int test = collatz(s);
	int res = int(isprime(test));
		
	color = vec4(res, res, res, 1.0);
}













