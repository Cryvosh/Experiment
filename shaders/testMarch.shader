#version 330 core

layout(location = 0) out vec4 color;

uniform vec2 iResolution;
uniform float iGlobalTime;

float map(vec3 p) {
	vec3 q = fract(p) * 2.0 - 1.0;
	return length(q) - 0.25;
}

float trace(vec3 o, vec3 r) {
	float t = 0.0;
	for (int i = 0; i < 32; i++) {
		vec3 p = o + r * t;
		float d = map(p);
		t += d * 0.5;
	}
	return t;
}

void main()
{
	vec2 uv = gl_FragCoord.xy / iResolution.xy;
	uv = uv * 2.0 - 1.0;

	uv.x *= iResolution.x / iResolution.y;

	vec3 r = normalize(vec3(uv, 1.0));

	vec3 o = vec3(0.0, 0.0, iGlobalTime);

	float t = trace(o, r);
	float fog = 1.0 / (1.0 + t * t * 0.1);
	vec3 fc = vec3(fog);

	color = vec4(fc, 1.0);
}