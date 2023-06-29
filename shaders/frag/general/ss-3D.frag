#version 330 core

layout(location = 0) out vec4 color;

uniform mat4 iViewMatrix;
uniform vec3 iPosition;
uniform vec2 iResolution;
uniform float iGlobalTime;
uniform float iNearClip;
uniform vec2 iMouse;
uniform float iVerticalFOV;

const int ITERATIONS = 128;

//leaf, c1, c2
vec3 nodes[3*2] = vec3[3*2](vec3(0.0,0.0,0.0), vec3(10.0,10.0,10.0),
							vec3(0.0,0.0,0.0), vec3(7.0,7.0,7.0),
							vec3(3.0,3.0,3.0), vec3(10.0,10.0,10.0));

//indx, left, right
//(negative means leaf)
int tree[3] = int[3](0, -1, -2);

vec2 intersect(vec3 o, vec3 dir, vec3 c1, vec3 c2) {
	vec3 invdir = 1.0 / dir;
	
	float t1 = (c1[0] - o[0]) * invdir[0];
	float t2 = (c2[0] - o[0]) * invdir[0];
	float tmin = min(t1, t2);
	float tmax = max(t1, t2);
	
	for (int i = 1; i < 3; i++) {
		t1 = (c1[i] - o[i]) * invdir[i];
		t2 = (c2[i] - o[i]) * invdir[i];
		tmin = max(tmin, min(t1, t2));
		tmax = min(tmax, max(t1, t2));
	}
	
	float low = max(tmin, 0.0);
	if (tmax > low) {
		return vec2(low, tmax);
	}
	return vec2(-1.0, -1.0);
}

float DE_SPHERE(vec3 p) {
	return length(p) - 3.0;
}

float DE_BOX( vec3 p)
{
	vec3 b = vec3(2.0,2.0,2.0);
  vec3 d = abs(p) - b;
  return length(max(d,0.0))
         + min(max(d.x,max(d.y,d.z)),0.0); // remove this line for an only partially signed sdf 
}

float smin(float a, float b, float k) {
	//float k = 2.0;
    float h = clamp( 0.5+0.5*(b-a)/k, 0.0, 1.0 );
    return mix( b, a, h ) - k*h*(1.0-h);
}

float smin2( float a, float b, float k)
{
	//float k = 0.5;
    float res = exp2( -k*a ) + exp2( -k*b );
    return -log2( res )/k;
}

vec2 march(vec3 origin, vec3 direction, float nearClipDist) {
	float depth = nearClipDist;
	
	
	int naive = 2;
	
	
	if (naive == 1) {
	
	/*for (int steps = 0; steps < ITERATIONS; steps++) {
		float dist1 = DE_BOX(origin + depth * direction);
		float dist2 = DE_BOX(origin + vec3(3.0,3.0,3.0) + depth * direction);
		
		float step = min(dist1, dist2) - 2.0;
		
		if (step < 0.01) {
			break;
		}
		
		depth += step;
	}
	return vec2(depth, 10.0);*/
	
	for (int steps = 0; steps < ITERATIONS; steps++) {
		float dist1 = DE_BOX(origin + depth * direction);
		float dist2 = DE_BOX(origin + vec3(3.0,3.0,3.0) + depth * direction);
		
		float step = smin(dist1, dist2, 1.0);
		
		if (step < 0.01) {
			break;
		}
		
		depth += step;
	}
	return vec2(depth, 10.0);
	}
	
	float depth2 = nearClipDist;
	float closest2 = 100000000.0;
	float closestdepth2 = 100000000.0;
	int steps2;
	for (steps2 = 0; steps2 < ITERATIONS; steps2++) {
		float dist = DE_BOX(origin + vec3(3.0,3.0,3.0) + depth2 * direction);
		
		if (dist < closest2) {
			closest2 = dist;
			closestdepth2 = depth2;
		}
		
		float step = dist;
		//float step = smin(closest1, dist);
		//float step = smin(closest1, dist) + abs(closestdepth1 - depth2)/10.0;
		
		if (step < 0.0001) {
			break;
		}
		
		depth2 += step;
	}
	
	float depth1 = nearClipDist;
	float closest1 = 100000000.0;
	float closestdepth1 = 100000000.0;
	float kek = 1000.0;
	int steps1;
	for (steps1 = 0; steps1 < ITERATIONS; steps1++) {
		float dist = DE_BOX(origin + depth1 * direction);
		
		if (dist < closest1) {
			closest1 = dist;
			closestdepth1 = depth1;
		}

		float step = dist;
		
		if (step < 0.0001) {
			kek = DE_BOX(origin + vec3(3.0,3.0,3.0) + depth1 * direction);
			break;
		}
		
		depth1 += step;
	}
	
	//return vec2(min(depth1, depth2) - 10.0/(abs(closest1-closest2)+1.0), steps1+steps2);
	
	return vec2(min(depth1, depth2)-(kek/100.0), steps1+steps2);
	//return min(depth1,depth2) - abs(closest1 - closest2);
}

vec3 rayDirection(float fieldOfView, vec2 size, vec4 fragCoord) {
    vec2 xy = fragCoord.xy - size / 2.0;
    float z = size.y / tan(radians(fieldOfView) / 2.0);
    return normalize(vec3(xy, -z));
}

void main() {
	vec3 viewDir = rayDirection(iVerticalFOV, iResolution.xy, gl_FragCoord);
	vec3 worldDir = (iViewMatrix * vec4(viewDir, 0.0)).xyz;
	
	vec2 depth = march(iPosition, worldDir, iNearClip);
	float fog = 1.0 / (1.0 + depth.x * depth.x * depth.x * depth.x * 0.0001);

	color = vec4(vec3(fog), 1.0);
	//color = vec4(vec3(depth.y/400.0), 1.0);
}

