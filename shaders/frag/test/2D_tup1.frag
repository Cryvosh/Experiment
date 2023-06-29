#version 330 core

uniform vec2 iResolution;
uniform float iGlobalTime;
uniform vec2 iPosition;
uniform float iZoom;

layout(location = 0) out vec4 color;

int b(int x, int y) {
	return int(mod(int(floor(y / pow(2, x))), 2));
}

void main() {
	vec2 uv = gl_FragCoord.xy / iResolution.xy;
	uv = uv * 2.0 - 1.0;
	uv.x *= iResolution.x / iResolution.y;
	
	vec2 c = iPosition + uv * iZoom;
	
	int x = int(floor(-c.x));
	int y = int(floor(-c.y));
	
	int bit = 1 - b(x,y);
	
	color = vec4(bit, bit, bit, 1.0);
}