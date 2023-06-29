#version 330 core

layout(location = 0) out vec4 color;

uniform mat4 iViewMatrix;
uniform vec3 iPosition;
uniform vec2 iResolution;
uniform float iGlobalTime;
uniform float iNearClip;
uniform vec2 iMouse;
uniform float iVerticalFOV;

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
    float res = exp2(-k*a) + exp2(-k*b);
    return -log2(res)/k;
}

int op_ptr = 0;
const int op_len = 32;
int op_stack[op_len];

void op_insert(int op_index) {
    if (op_ptr <= 0) {
        op_stack[0] = op_index;
        op_ptr += 1;
        return;
    }

	int loc = 0;
	while(op_stack[loc] > op_index) {
		loc += 1;
	}

    for (int i=op_ptr; i > loc; i--) {
        op_stack[i] = op_stack[i-1];
    }

    op_stack[loc] = op_index;
    op_ptr += 1;
}

void op_extract(int op_index) {
    int loc = 0;
    while(op_stack[loc] > op_index) {
        loc += 1;
    }
    
    for(int i=loc; i < op_ptr; i++) {
        op_stack[i] = op_stack[i+1];
    }
    
    op_ptr -= 1;    
}

struct min_node {
	int index;
	bool in;
	float in_depth;
	float ex_depth;
	int op_index;
};

int heap_ptr = 0;
const int heap_len = 32;
min_node min_heap[heap_len];

void min_heap_swap(int i, int j) {
	min_node temp = min_heap[i];
	min_heap[i] = min_heap[j];
	min_heap[j] = temp;
}

int min_heap_parent(int i) {
	return ((i-1)<<1);
}

int min_heap_left(int i) {
	return 2 * i + 1;
}

int min_heap_right(int i) {
	return 2 * i + 2;
}

int min_child(int i) {
	int left = min_heap_left(i);
	int right = min_heap_right(i);
	if (right >= heap_ptr) {
		return left;
	}
	if (min_heap[left].in_depth < min_heap[right].in_depth) {
		return left;
	} else {
		return right;
	}
}

void min_heap_insert(min_node a) {
	if (heap_ptr < heap_len) {
		min_heap[heap_ptr] = a;
		int i = heap_ptr;
		while (i > 0) {
			int parent_index = min_heap_parent(i);
			if (min_heap[parent_index].in_depth > min_heap[i].in_depth) {
				min_heap_swap(parent_index, i);
				i = parent_index;
			} else {
				break;
			}
		}
		heap_ptr += 1;
	}
}

min_node min_heap_extract() {
	if (heap_ptr > 0) {
		min_node res = min_heap[0];
		min_heap[0] = min_heap[heap_ptr - 1];
		int i = 0;		
		while (min_heap_left(i) < heap_ptr) {
			int min_child = min_child(i);
			if (min_heap[i].in_depth > min_heap[min_child].in_depth) {
				min_heap_swap(min_child, i);
			} else {
				break;
			}
			i = min_child;
		}
		heap_ptr -= 1;
		return res;
	} else {
		return min_node(-1, false, 0.0, 0.0, -1);
	}	
}

const int ITERATIONS = 128;

struct node_data {
	vec3 c1;
	vec3 c2;
	bool leaf;
	int index;
	int left_index;
	int right_index;
	int func;
};

node_data nodes[3] = node_data[3] (
	node_data(vec3(0.0,0.0,0.0), 	vec3(20.0,20.0,20.0),	false,	0,	1,	2,	1),
	node_data(vec3(6.0,6.0,6.0),	vec3(20.0,20.0,20.0), 	true,	1,	-1,	-1,	0),
	node_data(vec3(0.0,0.0,0.0), 	vec3(14.0,14.0,14.0), 	true,	2,	-1, -1,	0)
);

/*
struct node {
	int index;
	int left_index;
	int right_index;
};

node tree[1] = node[1] (
	node(0, 1, 2)
);*/

/*
idea:
if op string is empty, add to it from heap
else march up to min of mean heap
*/

vec2 march(vec3 origin, vec3 direction, float nearClipDist) {
	int step = 0;
	
	vec2 sect = intersect(origin, direction, nodes[0].c1, nodes[0].c2);
	
	if (sect.x != -1.0) {
		min_heap_insert(min_node(0, true, sect.x, sect.y, -1));
	} else {
		return vec2(0.0, 0.0);
	}
	
	while (step < ITERATIONS) {
		
	
		if (heap_ptr <= 0) {
			break;
		}
		
		// If internal
		if (nodes[min_heap[0].index].leaf == false) {
			min_node min = min_heap_extract();			
			int left_index = nodes[min.index].left_index;
			int right_index = nodes[min.index].right_index
			
			vec2 sect1 = intersect(origin, direction, nodes[left_index].c1, nodes[left_index].c2);
			vec2 sect2 = intersect(origin, direction, nodes[right_index].c1, nodes[right_index].c2);
			
			if (sect1.x != -1.0 && sect2.x != -1.0) {
				min_node n1;
				min_node n2;
				min_node n3;
				min_node n4;
				
				if (sect1.x < sect2.x) {
					n1 = min_node(left_index, true, sect1.x, sect1.y, -1);
					n2 = min_node(right_index, true, sect2.x, sect2.y, min.index);		
				} else {
					n1 = min_node(right_index, true, sect2.x, sect2.y, -1);
					n2 = min_node(left_index, true, sect1.x, sect1.y, min.index);
				}
				
				if (sect1.y < sect2.y) {
					n3 = min_node(left_index, false, sect1.x, sect1.y, min.index);
					n4 = min_node(right_index, false, sect2.x, sect2.y, -1);
				} else {
					n3 = min_node(right_index, false, sect2.x, sect2.y, min.index);
					n4 = min_node(left_index, false, sect1.x, sect1.y, -1);
				}
			}
		} else {
			op_insert(min_heap_extract().index);
		}
		
		step += 1;
	}
	return vec2(0.0, 0.0);
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

	//color = vec4(vec3(sqrt(depth.y/60.0))*vec3(1.2,1.2,1.0), 1.0);
	color = vec4(vec3(fog), 1.0);
}

