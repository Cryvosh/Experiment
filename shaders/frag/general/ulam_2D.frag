#version 330 core

uniform vec2 iResolution;
uniform float iGlobalTime;
uniform vec2 iPosition;
uniform float iZoom;

layout(location = 0) out vec4 color;

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

int gridindex(int x, int y) {
	int spread = 50;
	//int width = int(sin(iGlobalTime*0.5) * spread + spread+1);
	int width = 50;

	if(x >= width || x < 0 || y < 0) {
		return -1;
	}

	int index = width*y + x;
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
	for(int i = 3; i <= sqrt(n); i+=2) {
		if(n % i == 0) {
			return false;
		}
	}
	
	return true;
}

int seqprimes(int x, int y) {
	if (!isprime(x) || !isprime(y)) {
		return 0;
	}
	if(x == 2 && y == 3) {
		return 1;
	}
	for(int i = x; i < y; i+=2) {
		if (isprime(i)) {
			return 0;
		}
	}
	
	return 1;
}

void main() {
	vec2 uv = gl_FragCoord.xy / iResolution.xy;
	uv = uv * 2.0 - 1.0;
	uv.x *= iResolution.x / iResolution.y;
	
	vec2 c = iPosition + uv * iZoom;
	
	int x = int(floor(c.x));
	int y = int(floor(c.y));
	
	int index = gridindex(x, y);
	
	bool isprime = isprime(index);
	
	float black = 0.117647;
	
	if (isprime == true) {
		color = vec4(0.784313, black, black, 1.0);
	} else {
		color = vec4(black, black, black, 1.0);
	}
}