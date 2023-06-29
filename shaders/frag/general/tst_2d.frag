#version 330 core

uniform vec2 iResolution;
uniform float iGlobalTime;
uniform vec2 iPosition;
uniform float iZoom;

layout(location = 0) out vec4 color;

void main() {
	vec2 uv = gl_FragCoord.xy / iResolution.xy;
	uv = uv * 2.0 - 1.0;
	uv.x *= iResolution.x / iResolution.y;
	
	vec2 p = iPosition + uv * iZoom;
	
	float dist = max(-(length(p)-0.5), length(p)-1.0);
	
	// for visualization
	dist = dist > 0 ? 1 : 0;
	
	color = vec4(vec3(dist), 1);
}