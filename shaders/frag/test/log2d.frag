#version 330 core

uniform vec2 iResolution;
uniform float iGlobalTime;
uniform vec2 iPosition;
uniform float iZoom;

layout(location = 0) out vec4 color;

const float PI = 3.1415926535897932384626433832795;

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

void main() {
	vec2 uv = gl_FragCoord.xy / iResolution.xy;
	uv = uv * 2.0 - 1.0;
	uv.x *= iResolution.x / iResolution.y;
	
	vec2 c = iPosition + uv * iZoom;
	
	float length = sqrt(c.x*c.x + c.y*c.y);
	float ang = get_ang(c.x, c.y);
	
	float val = abs(ang - mod(log(length),2*PI));
	
	if (val < 0.1) {
		color = vec4(1, 1, 1, 1.0);
	} else {
		color = vec4(0, 0, 0, 1.0);
	}
}