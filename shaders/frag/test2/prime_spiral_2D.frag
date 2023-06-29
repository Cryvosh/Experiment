#version 330 core

uniform vec2 iResolution;
uniform float iGlobalTime;
uniform vec2 iPosition;
uniform float iZoom;

layout(location = 0) out vec4 color;

const float PI = 3.1415926535897932384626433832795;

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

// y comes after x
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

//Precondition: n is prime
int nextprime(int n) {
	if (n==2) {
		return 3;
	}
	
	for(int i = n+2; i < 2*n; i+=2) {
		if(isprime(i)) {
			return i;
		}
	}
}

float get_ang(float x, float y) {
	float ang = atan(y/x);
	
	if(x < 0) {
		ang += PI;
	}
	
	if(x > 0 && y < 0) {
		ang += 2*PI;
	}
	
	return ang;
}

bool is_center(vec2 c) {
	if(sqrt(c.x*c.x + c.y*c.y) < 1) {
		return true;
	}
	return false;
}

void main() {
	vec2 uv = gl_FragCoord.xy / iResolution.xy;
	uv = uv * 2.0 - 1.0;
	uv.x *= iResolution.x / iResolution.y;
	
	vec2 c = iPosition + uv * iZoom;
	
	int x = int(floor(c.x));
	int y = int(floor(c.y));
	
	float length = sqrt(c.x*c.x + c.y*c.y);	
	
	int int_length = int(floor(length));
	
	if (isprime(int_length)) {
			
		float ang = get_ang(c.x, c.y);
	
		int next = nextprime(int_length);
		
		float ideal_ang = mod(next, (2*PI));
		
		float ang_thresh = 10.0/int_length;
		
		if (abs(ang - ideal_ang) < ang_thresh) {
			color = vec4(1, int(is_center(c)), 1, 1.0);
		} else {
			color = vec4(0, int(is_center(c)), 0, 1.0);
		}
	} else {
		color = vec4(0, int(is_center(c)), 0, 1.0);
	}
}