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
const int k = 3;

/*
//leaf, c1, c2
vec3 nodes[3*2] = vec3[3*2](vec3(0.0,0.0,0.0), 		vec3(10.0,10.0,10.0),
							vec3(0.0,0.0,0.0), 		vec3(7.0,7.0,7.0),
							vec3(3.0,3.0,3.0), 		vec3(10.0,10.0,10.0));
*/
/*
//leaf, c1, c2
vec3 nodes[11*k] = vec3[11*k](vec3(0.0,0.0,0.0), 	vec3(20.0,20.0,20.0),

							vec3(9.0,9.0,9.0), 		vec3(20.0,20.0,20.0),
							vec3(10.0,10.0,10.0),	vec3(15.0,15.0,15.0),
							vec3(15.0,15.0,15.0), 	vec3(20.0,20.0,20.0),
							
							vec3(0.0,0.0,0.0), 		vec3(10.0,10.0,10.0),
							vec3(0.0,0.0,0.0), 		vec3(2.0,2.0,2.0),
							vec3(3.0,3.0,3.0), 		vec3(10.0,10.0,10.0),
							
							vec3(3.0,3.0,3.0),		vec3(5.0,5.0,5.0),
							vec3(4.0,4.0,4.0),		vec3(10.0,10.0,10.0),
							
							vec3(3.0,3.0,3.0),		vec3(4.5,4.5,4.5),
							vec3(4.0,4.0,4.0),		vec3(5.0,5.0,5.0)	
							);

*/
vec3 nodes[3*3] = vec3[3*3](vec3(0.0,0.0,0.0), 		vec3(20.0,20.0,20.0), vec3(1.0),

							vec3(6.0,6.0,6.0), 		vec3(20.0,20.0,20.0), vec3(0.0),
							
							vec3(0.0,0.0,0.0), 		vec3(14.0,14.0,14.0), vec3(0.0)
							);


//indx, left, right
//(negative means leaf)
//int tree[3] = int[3](0, -1, -2);

/*
int tree[3*5] = int[3*5](	0, 1, 4,
						1, -2, -3,
						4, -5, 6,
						6, 7, -8,
						7, -9, -10);
*/

int tree[3*1] = int[3*1](	0, -1, -2);

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
	return length(p) - 3.5;
}

float smin(float a, float b) {
	float k = 2.0;
    float res = exp2( -k*a ) + exp2( -k*b );
    return -log2( res )/k;
}

vec2 march(vec3 origin, vec3 direction, float nearClipDist) {
	float depth = nearClipDist;
	
	int stack[16];
	int sp = 0;	
	int blend[16];
	int bp = -1;
	stack[sp] = tree[0];
	int ni = 0; //node_index
	int ti = 0; //tree_index
	
	float blendstart = 0.0;
	float blendend = 0.0;
	
	float best_depth = 1000000000000.0;
	
	int step;	
	while (step < ITERATIONS && sp >= 0) {
		ni = stack[sp];
		sp -= 1;
		
		// if internal
		if (ni >= 0) {
			vec2 sect = intersect(origin, direction, nodes[ni*k], nodes[ni*k+1]);
			
			// If hit, push children to stack
			//if (sect.x > -1.0 && sect.x < best_depth) {
			if (sect.x != -1.0) {
				stack[sp+1] = tree[ti*3 + 2];
				stack[sp+2] = tree[ti*3 + 1];
				sp += 2;
				
				// 1.0 means blend node
				if (nodes[ni+2].x == 1.0) {
					
				}
			}
			ti += 1;
		// if leaf
		} else {
			vec2 sect = intersect(origin, direction, nodes[ni*-k], nodes[ni*-k+1]);
			
			bp += 1;
			blend[bp] = ni;
			
			if (sect.x != -1.0) {
			//if (sect.x != -1 && sect.x < best_depth) {
				while (step < ITERATIONS) {
					float dist = DE_SPHERE(origin - (nodes[ni*-k] + nodes[ni*-k+1])/2 + sect.x * direction);
					
					// hit surface
					if (dist < 0.01) {
						best_depth = min(best_depth, sect.x);
						step += 1;
						break;
					}
					
					// leaving aabb
					if (sect.x + dist > sect.y) {
						step += 1;
						break;
					}
					
					sect.x += dist;
					step += 1;
				}
				step += 1;
			}
			step += 1;
		}
		step += 1;
	}
	return vec2(best_depth, step);
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
	float fog = 1.0 / (1.0 + depth.x * depth.x * depth.x * depth.x * 0.00001);

	color = vec4(vec3(sqrt(depth.y/60.0))*vec3(1.2,1.2,1.0), 1.0);
	//color = vec4(vec3(fog), 1.0);
}

