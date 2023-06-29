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

void main() {
	vec2 uv = gl_FragCoord.xy / iResolution.xy;
	uv = uv * 2.0 - 1.0;
	uv.x *= iResolution.x / iResolution.y;
	
	vec2 c = iPosition + uv * iZoom;
	
	int x = int(floor(c.x));
	int y = int(floor(c.y));
	
	lst[3] = 1;
	
	float mono = float(lst[3]);
	
	color = vec4(mono, mono, mono, 1.0);
}