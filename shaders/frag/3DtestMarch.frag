#version 330 core

layout(location = 0) out vec4 color;

uniform mat4 test;
uniform vec2 iResolution;
uniform float iGlobalTime;
uniform vec2 iMouse;

float map(vec3 point) {
	vec3 q = fract(point) * 2.0 - 1.0;
	return length(q) - 0.25;
}

float trace(vec3 origin, vec3 ray) {
	float depth = 0.0;
	for (int i = 0; i < 32; i++) {
		vec3 p = origin + ray * depth;
		float dist = map(p);
		depth += dist * 0.5;
	}
	return depth;
}

vec2 rot2D(vec2 p, float angle) {
 
    angle = radians(angle);
    float s = sin(angle);
    float c = cos(angle);
    
    return p * mat2(c,s,-s,c);
}

void main()
{
	vec2 uv = gl_FragCoord.xy / iResolution.xy;
	uv = uv * 2.0 - 1.0;

	uv.x *= iResolution.x / iResolution.y;
	
	vec3 ray = normalize(vec3(uv, 1.0));	
	
	float x = -iMouse.x*0.01;
	float y = iMouse.y*0.01;
	
	ray.xz *= mat2(cos(x), -sin(x), sin(x), cos(x));
	ray.yz *= mat2(cos(y), -sin(y), sin(y), cos(y));

	vec3 origin = vec3(0, 0, iGlobalTime);

	float t = trace(origin, ray);
	float fog = 1.0 / (1.0 + t * t * 0.1);
	vec3 fc = vec3(fog);

	color = vec4(fc, 1.0);
}