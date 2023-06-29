#version 330 core

layout(location = 0) out vec4 color;

uniform mat4 iViewMatrix;
uniform vec3 iPosition;
uniform vec2 iResolution;
uniform float iGlobalTime;
uniform float iNearClip;
uniform vec2 iMouse;
uniform float iVerticalFOV;

const int ITERATIONS = 512;
const float PI = 3.1415926535;

float sphere_sdf(vec3 p, float r) {
	return length(p) - r;
}

float box_sdf(vec3 p, vec3 b) {
	vec3 q = abs(p) - b;
	return length(max(q, 0)) + min(max(q.x, max(q.y, q.z)), 0);
}



float smin(float a, float b, float k) {
	float res = exp2(-k * a) + exp2(-k * b);
	return -log2(res) / k;
}

float sdTorus( vec3 p, vec2 t )
{
  vec2 q = vec2(length(p.xy)-t.x,p.z);
  return length(q)-t.y;
}

float sdPlane( vec3 p, vec3 n, float h )
{
  // n must be normalized
  return dot(p,n) + h;
}

float box2d_sdf(vec2 p, vec2 b) {
	vec2 d = abs(p) - b;
	return length(max(d, 0.0)) + min(max(d.x, d.y), 0.0);
}

float circle_sdf(vec2 p, float r) {
	return length(p) - r;
}

float opSmoothSubtraction( float d1, float d2, float k ) {
    float h = clamp( 0.5 - 0.5*(d2+d1)/k, 0.0, 1.0 );
    return mix( d2, -d1, h ) + k*h*(1.0-h); }

float interpblend(float a, float b, float k) {
	float clearance = a+b;
	float midsurface = a-b;
	float interp = midsurface / clearance;
	return abs(interp + k);
}

float test_blend(float a, float b) {
	vec2 uv = vec2(a, b) - vec2(15);
	float c = 1.5;
	vec2 l = vec2(3);
	
	vec2 lel = uv - c * clamp(round(uv / c), -l, l);
	vec2 lelmod = mod(lel+0.5*c,c)-0.5*c;
	//float dist = max(circle_sdf(lelmod, 0.1), box2d_sdf(uv, vec2(c*5)));
	float dist = circle_sdf(lel, 0.1);
	//float dist = box2d_sdf(lel, vec2(0.2));

	return dist;
	//return min(dist, smin(a,b,2));
}


float testtest(float a, float b, float c) {
	vec3 cord = vec3(a,b,c) - vec3(3);
	float m = min(a,min(b,c));
	
	float k = 0.7;
	vec3 l = vec3(2,2,2);
	vec3 lel = cord - k * clamp(round(cord / k), -l, l);
	
	return min(m, sphere_sdf(lel, 0.1));
}

mat3 AngleAxis3x3(float angle, vec3 axis)
{
	float s = sin(angle*PI/180);
	float c = cos(angle*PI/180);

	float t = 1 - c;
	float x = axis.x;
	float y = axis.y;
	float z = axis.z;

	return mat3(
		t * x * x + c, t * x * y - s * z, t * x * z + s * y,
		t * x * y + s * z, t * y * y + c, t * y * z - s * x,
		t * x * z - s * y, t * y * z + s * x, t * z * z + c
		);
}

float MAND(vec3 pos) {
	//vec3 z = AngleAxis3x3(180, vec3(0,0,1))* pos;
	vec3 z = pos;
	float dr = 1.0;
	float r = 0.0;
	float Power = sin(iGlobalTime/3)*3+4;
	Power = 1.01 + mix(0, 1, max((iGlobalTime-3),0));
	//Power = 1.01 + 4*log(max(iGlobalTime-3,1));
	float Bailout = 5;
	float Iterations = 5;
	for (int i = 0; i < Iterations ; i++) {
		r = length(z);
		if (r>Bailout) break;
		
		// convert to polar coordinates
		float theta = acos(z.z/r);
		float phi = atan(z.y,z.x);
		dr =  pow( r, Power-1.0)*Power*dr + 1.0;
		
		// scale and rotate the point
		float zr = pow( r,Power);
		theta = theta*Power;
		phi = phi*Power;
		
		// convert back to cartesian coordinates
		z = zr*vec3(sin(theta)*cos(phi), sin(phi)*sin(theta), cos(theta));
		z+=pos;
	}
	return 0.5*log(r)*r/dr;
}

float sdCylinder(vec3 p, vec3 c) {
	return length(p.xz-c.xy)-c.z;
}

vec3 opTwist(vec3 p)
{
    float k = -iGlobalTime*0.001; // or some other amount
    float c = cos(k*p.z);
    float s = sin(k*p.z);
    mat2  m = mat2(c,-s,s,c);
    vec3  q = vec3(m*p.xy,p.z);
    return q;
}

float FRACT(vec3 p) {
	float s = 5;
	p /= s;
	p -= vec3(-6,16,-2) / 10;
	p -= vec3(0,1,0);
	vec3 pp = p;
	vec3 CSize = vec3(1., 1., 1.3);
	p = p.xzy;
	float scale = 1.0;
	for(int i = 0; i < 15; i++)
	{
		p = 2.0*clamp(p, -CSize, CSize) - p;
		//float r2 = dot(p,p);
        float r2 = dot(p,p+sin(p.z*0.3)); // should be .3
		float k = max((2.)/(r2), .027);
		p     *= k;
		scale *= k;
	}
	float l = length(p.xy);
	float rxy = l - 4.0;
	float n = l * p.z;
	rxy = max(rxy, -(n) / 4.);
	float res = (rxy) / abs(scale);
	//res *= s;
	
	return max(res, box_sdf(pp+vec3(0,3,0), vec3(3,2,3))) * s;
}

float test_sdf(vec3 p) {

	float frac = FRACT(p);
	//float frac = FRACT(p-vec3(-6,16,-2));
	float s = sphere_sdf(p, 0.1);
	return min(s,frac);
	
	vec3 rotmand = AngleAxis3x3(180, vec3(0,1,0)) * AngleAxis3x3(-45, vec3(1,0,0)) * p;

	//float mand = MAND(rotmand/20)*3;
	//return mand;
	
	float cil = sdCylinder(p, vec3(1,1,1));
	return cil;
	
	//p = opTwist(p);

	float floor = sdPlane(p, vec3(0,1,0), 3);
	
	vec3 rotp = AngleAxis3x3(0, vec3(1,0,0)) * p;
	
	float box = box_sdf(rotp, vec3(0.05*abs(rotp.z),0.05*abs(rotp.z),300));
	float sphere = sphere_sdf(rotp,2);
	float sphere_2 = sphere_sdf(rotp+vec3(0,0,3+sin(iGlobalTime/2)*3),2);
	//float sphere_2 = sphere_sdf(p+vec3(0,-10*cos(iGlobalTime/2 + PI/2),3+sin(iGlobalTime/2)*3),2);
	//float sphere_2 = sphere_sdf(p+vec3(0,0,1.25),2);
	//float box = box_sdf(p + vec3(0,0,5+2*sin(iGlobalTime)), vec3(1,3,1));
	
	float res = test_blend(sphere, sphere_2);
	
	
	
	res = smin(res, floor, 0.6)/2;
	
	//res = test_blend(sphere_sdf(transp+vec3(0,10,0), 5), floor);
	
	//res = smin(res,box, 0.8);
	//res = smin(mand, res, 3);
	return res;// + iGlobalTime*0.5*sin(p.x)*sin(p.y)*sin(p.z);
	
	/*
	
	float box = box_sdf(p, vec3(0.5,0.5,5));
	float box2 = box_sdf(p, vec3(5,0.5,0.5));
	return min(box,box2);
	
	float object = smin(sphere,box,0.5);
	
	float res;
	res = test_blend(floor, object);
	
	return res;
	//return min(a, smin(sphere,box,0.5));
	return sphere_sdf(p, 2);
	
	float z = sphere_sdf(p+vec3(0, 0, 10*sin(iGlobalTime/2)), 4);
	float z2 = box_sdf(p, vec3(10,1,1));
	float z3 = box_sdf(p, vec3(0.01, 20,20));
	
	//return (max(z,-z2));

	float ground = sdPlane(p, vec3(0,1,0), 0);

	float s0 = sphere_sdf(p+vec3(0,-2,2), 1);
	float s3 = box_sdf(p+vec3(0,0,0), vec3(1,1,3));
	float s2 = sdTorus(p-vec3(0,0,6), vec2(1,0.2));
	
	float s1 = s3;
	
	float clearance = (s1+s2);
	float midsurface = (s1-s2);
	float interp = midsurface / clearance;
	
	float k = abs(interp + sin(iGlobalTime/2));
	float b = box_sdf(p, vec3(5,5,10));
	
	float g = box_sdf(p+vec3(0,1,0), vec3(10, 0.1, 10));
	
	//return ground;
	
	return k;
	
	return max(k,b);
	
	return smin(max(k,b),g,5);
	
	return k;
	
	return min(s1,s2) * k;
	
	return smin(s1,s2,k*10);
	
	return k;
	return abs(interp + sin(iGlobalTime));
	return mix(s1,s2, sin(iGlobalTime)*0.5+0.5);
	return abs(interp + sin(iGlobalTime));
	return sphere_sdf(p, 2);
	return sphere_sdf(p, 2) * abs(interp)*.1;
	return sphere_sdf(p, 2) + sin(iGlobalTime)*0.3*abs(interp);
	return smin(s1,s2,0.5);
	*/
}

/*
	float floor = sdPlane(p, vec3(0,1,0), 3);
	//float t1 = sphere_sdf(p, 1);
	float t2 = sdTorus(p + vec3(0,0.5+sin(iGlobalTime)+0.5,0), vec2(1,0.2));
	float t3 = sdTorus(p + vec3(0,0,5+3*sin(iGlobalTime)), vec2(1,0.2));
	
	//float res = test_blend(t1,t2);
	//res = test_blend(res,t3);
	float res = testtest(floor,t2,t3);
	
	return res;
*/

float f(vec3 p) {
	return test_sdf(p);
	float s = sphere_sdf(p, 4);
	float b = box_sdf(p+vec3(4,4,4), vec3(1,1,1));
	return smin(s,b,0.9);
}

vec3 calcnormal( in vec3 p ) // for function f(p)
{
    const float eps = 0.0001; // or some other value
    const vec2 h = vec2(eps,0);
    return normalize( vec3(f(p+h.xyy) - f(p-h.xyy),
                           f(p+h.yxy) - f(p-h.yxy),
                           f(p+h.yyx) - f(p-h.yyx) ) );
}

vec3 rayDirection(float fieldOfView, vec2 size, vec4 fragCoord) {
    vec2 xy = fragCoord.xy - size / 2.0;
    float z = size.y / tan(radians(fieldOfView) / 2.0);
    return normalize(vec3(xy, -z));
}

void main() {
	vec3 viewDir = rayDirection(iVerticalFOV, iResolution.xy, gl_FragCoord);
	vec3 worldDir = (iViewMatrix * vec4(viewDir, 0.0)).xyz;

	float depth = 0.01;
	int steps;
	bool hit = false;
	for (steps = 0; steps < ITERATIONS; steps++) {
		float dist = f(iPosition + depth * worldDir);
		float epsilon = depth * 0.00001;
		
		if(abs(dist) < epsilon) {
			hit = true;
			break;
		}
		
		depth += dist;
		
		if(depth > 1000) {
			break;
		}
	}

	if (!hit) {
		color = vec4(0,0,0,1);
		//return;
	}


	color = vec4(vec3(1.0 - float(steps) / ITERATIONS), 1);
	return;
	
	vec3 c = calcnormal(iPosition + depth * worldDir)*0.5 + 0.5;
	c *= pow(1/depth, 0.2);
	color = vec4(c,1);
	return;
}