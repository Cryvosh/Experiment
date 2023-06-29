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
	return vec2((tmax > low) ? low : -1.0, tmax);
}

float DE_SPHERE(vec3 p) {
	return length(p) - 3.5;
}

float smin(float a, float b) {
	float k = 0.7;
    float res = exp2(-k*a) + exp2(-k*b);
    return -log2(res)/k;
}

const int ITERATIONS = 128;

struct node {
	int index;
	bool isLeaf;
	int left;
	int right;
	int operation;
	vec3 c1;
	vec3 c2;
};

//operation = 1 => smooth blend
node nodes[3] = node[3] (
	node(0, false, 1, 2, 1, vec3(0.0,0.0,0.0), vec3(20.0,20.0,20.0)),
	node(1, true, -1, -1, -1, vec3(0.0,0.0,0.0), vec3(15.0,15.0,15.0)),
	node(2, true, -1, -1, -1, vec3(5.0,5.0,5.0), vec3(20.0,20.0,20.0))
);

// enter = 0 => out, enter = 1 => enter, enter = 2 => internal
// dist2 is only used if enter=2, else dist1 stores the relevant distance
struct heap_elem {
	int enter;
	int index;
	int op;
	float dist1;
	float dist2;
};

int op_string_len = 0;
const int max_op_string_len = 16;
int op_string[max_op_string_len];

void sort_op_string() {
    int i, j;
    int key;
    for (i = 1; i < max_op_string_len; i++) {
        key = op_string[i];
        j = i - 1;
        while (j >= 0 && op_string[j] > key) {
            op_string[j + 1] = op_string[j];
            j = j - 1;
        }
        op_string[j + 1] = key; 
    }
}

int heap_len = 0;
const int max_heap_len = 16;
heap_elem heap[max_heap_len];

void sort_heap() {
    int i, j;
    heap_elem key;
    for (i = 1; i < max_heap_len; i++) {
        key = heap[i];
        j = i - 1;
        while (j >= 0 && heap[j].dist1 > key.dist1) {
            heap[j + 1] = heap[j];
            j = j - 1;
        }
        heap[j + 1] = key; 
    }
}

vec3 march(vec3 origin, vec3 direction, float nearClipDist) {
	int step = 0;
	float dist = nearClipDist;
	vec3 color = vec3(0.0);
	
	vec2 sect = intersect(origin, direction, nodes[0].c1, nodes[0].c2);
	if (sect.x == -1.0) {
		return vec3(0.0);
	}
	color += vec3(0.1);
	
	heap[0] = heap_elem(2, 0, -1, sect.x, sect.y);
	heap_len += 1;
	
	while (step < ITERATIONS) {
		//crossed min heap element threshold
		
		//if (op_string_len <= 0 || dist > heap[0].dist1) {
		//if we just left a node, remove its ops from op stack
		if (heap[0].enter == 0) {
			/*for (int i=0; i<op_string_len; i++) {
				if (op_string[i] == heap[0].index) {
					op_string[i] = 3301;
					op_string_len -= 1;
					sort_op_string();
					break;
				}
			}
			heap[0].dist1 = 1e30;
			heap_len -= 1;
			sort_heap();*/
			//return vec3(1.0,0.0,0.0);
		} else if (heap[0].enter == 1) {
		
		//if entering new overlapped node, add its ops to the op stack
			
			op_string[op_string_len] = heap[0].index;
			op_string_len += 1;
			if (heap[0].op != -1) {
				op_string[op_string_len] = heap[0].op;
				op_string_len += 1;
				
			}
			sort_op_string();
			//return vec3(float(op_string_len==3));
			heap[0].dist1 = 1e30;
			heap_len -= 1;
			sort_heap();
			
			//return vec3(1.0,0.0,0.0);
		} else {
			/*
			op_string[op_string_len] = heap[0].index;
			op_string_len += 1;
			sort_op_string();*/
			
			/*
			op_string[op_string_len] = heap[0].op;
			op_string_len += 1;
			sort_op_string();*/
			
			//if min heap element is internal node, break it into 
			
			while (heap[0].enter == 2) {
				int left_child_index = nodes[heap[0].index].left;
				int right_child_index = nodes[heap[0].index].right;
			
				vec2 sect1 = intersect(origin, direction, nodes[left_child_index].c1, nodes[left_child_index].c2);
				vec2 sect2 = intersect(origin, direction, nodes[right_child_index].c1, nodes[right_child_index].c2);
				
				if (sect1.x != -1) {
					color += vec3(0.0,0.0,0.5);
					//color += vec3(10.0/sect1.x) + vec3(0.0,0.0,0.5);
				}
				if (sect2.x != -1) {
					color += vec3(0.0,0.5,0.0);
					//color += vec3(10.0/sect2.x) + vec3(0.0,0.5,0.0);
				}
				
				//&& (sect1.x <= sect2.y && sect2.x <= sect1.y)
				if (sect1.x != -1.0 && sect2.x != -1.0 && (max(sect1.x, sect2.x) <= min(sect1.y, sect2.y))) {
					//return vec3(1.0,0.0,1.0);
					heap[heap_len] = heap_elem(1, left_child_index, sect1.x > sect2.x ? heap[0].index : -1, sect1.x, -1.0);
					heap[heap_len+1] = heap_elem(0, left_child_index, sect1.y < sect2.y ? heap[0].index : -1, sect1.y, -1.0);
					
					heap[heap_len+2] = heap_elem(1, right_child_index, sect1.x <= sect2.x ? heap[0].index : -1, sect2.x, -1.0);
					heap[heap_len+3] = heap_elem(0, right_child_index, sect1.y >= sect2.y ? heap[0].index : -1, sect2.y, -1.0);				
				} else {
					//if (sect1.x != -1) {
						heap[heap_len] = heap_elem(1, left_child_index, -1, sect1.x, -1.0);
						heap[heap_len+3] = heap_elem(0, right_child_index, -1, sect2.y, -1.0);
					//}
					
					//if (sect2.x != -1) {
						heap[heap_len+1] = heap_elem(0, left_child_index, -1, sect1.y, -1.0);
						heap[heap_len+2] = heap_elem(1, right_child_index, -1, sect2.x, -1.0);
					//}
				}
				heap[0].dist1 = 1e30;
				heap_len += 3;
				sort_heap();
			}
			//heap[0] is now a leaf, so add its op to the op string and remove it from the heap
			//WHAT IF WE'RE LEAVING? WE WOULD NEED TO REMOVE SOMETHING FROM THE OP STRING
			//if (heap[0].enter == 1) {
				op_string[op_string_len] = heap[0].index;
				op_string_len += 1;
				sort_op_string();
			//}
			
			heap[0].dist1 = 1e30;
			heap_len -= 1;
			sort_heap();
		}
		
		// so as to not overstep into potentially new node
		//dist = heap[0].dist1;
		//}
		
		//march the op string
		
		/*
		op_string_len = 3;
		op_string[0] = 0;
		op_string[1] = 1;
		op_string[2] = 2;
		*/
		
		const int max_eval_stack_len = 16;
		float eval_stack[max_eval_stack_len];
		
		while (step < ITERATIONS) {
			int eval_stack_ptr = 0;
			
			for (int i=0; i<op_string_len; i++) {
				int index = op_string[op_string_len - i - 1];
				if (op_string_len==3) {
					//return vec3(1.0,0.0,0.0);
				}
				if (nodes[index].isLeaf) {
					eval_stack[eval_stack_ptr] = DE_SPHERE(origin - (nodes[index].c1 + nodes[index].c2)/2.0 + dist*direction);
					eval_stack_ptr += 1;
				} else {
					color += vec3(0.2,0.0,0.0);
					eval_stack[eval_stack_ptr-2] = smin(eval_stack[eval_stack_ptr-1], eval_stack[eval_stack_ptr-2]);
					eval_stack_ptr -= 1;
				}
			}
			
			if (eval_stack[0] < 0.1) {
				return color + vec3(10.0/dist);
			}
			
			dist += eval_stack[0];
			
			if (dist > heap[0].dist1) {
				break;
			}
						
			step += 1;
		}
	
	step += 1;
	}
	
	return color;
	//return vec3(0.0);
	return vec3(10.0/dist)*vec3(10.0,1.0,1.0);
}


vec3 rayDirection(float fieldOfView, vec2 size, vec4 fragCoord) {
    vec2 xy = fragCoord.xy - size / 2.0;
    float z = size.y / tan(radians(fieldOfView) / 2.0);
    return normalize(vec3(xy, -z));
}

void main() {
	vec3 viewDir = rayDirection(iVerticalFOV, iResolution.xy, gl_FragCoord);
	vec3 worldDir = (iViewMatrix * vec4(viewDir, 0.0)).xyz;
	
	color = vec4(march(iPosition, worldDir, iNearClip), 1.0);
	
	//float fog = 1.0 / (1.0 + depth.x * depth.x * depth.x * depth.x * 0.00001);
	//color = vec4(vec3(sqrt(depth.y/60.0))*vec3(1.2,1.2,1.0), 1.0);
	//color = vec4(vec3(fog), 1.0);
}

