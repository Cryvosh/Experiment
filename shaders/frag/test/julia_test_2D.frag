#version 330 core

uniform vec2 iResolution;
uniform float iGlobalTime;
uniform vec2 iPosition;
uniform float iZoom;

layout(location = 0) out vec4 color;

vec2 iterate(vec2 c) {
	float r = c.x; //real
	float i = c.y; //imaginary
	
	float real = (r * r - i * i);
	float imaginary = (r * i * 2);
	
	return vec2(real, imaginary);
}

bool inSet(vec2 c) {
	float r = c.x;
	float i = c.y;
	
	return (sqrt(r*r + i*i) < 2.0);
}

void main() {
	vec2 uv = gl_FragCoord.xy / iResolution.xy;
	uv = uv * 2.0 - 1.0;
	uv.x *= iResolution.x / iResolution.y;
	
	vec2 z = iPosition + uv * iZoom;
	
	int i;
	vec2 c = vec2(iGlobalTime*0.01*cos(iGlobalTime*3), iGlobalTime*0.01*sin(iGlobalTime*3)); //seed
	
	for (i = 0; i<1000; i++) {
		z = iterate(z) + c;
		if(!inSet(z)) {
			color = vec4(vec3(sin(i)+1.8), 1.0);
			return;
		}
	}
	
	color = vec4(0.0);
}