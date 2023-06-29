#version 460 core

uniform vec2 iResolution;
uniform float iGlobalTime;
uniform vec2 iPosition;
uniform float iZoom;

layout(location = 0) out vec4 color;

bool isprime(int n) {
	if(n<=1) {
		return false;
	}
	if(n==2) {
		return true;
	}
	if(n%2 == 0) {
		return false;
	}
	for(int i = 3; i <= sqrt(n); i+=2) {
		if(n % i == 0) {
			return false;
		}
	}	
	return true;
}

void main() {
	vec2 uv = gl_FragCoord.xy / iResolution.xy;
	uv = uv * 2.0 - 1.0;
	uv.x *= iResolution.x / iResolution.y;
	
	vec2 c = iPosition + uv * iZoom;
	
	float rot = log(length(c)) * iGlobalTime/5;
	mat2 m = mat2(cos(rot), -sin(rot), sin(rot), cos(rot));
	//c.y = c.y/10;
	c = c * m;
	
	int x = int(round(abs(c.x)));
	int y = int(round(abs(c.y)));
	
	//vec2 i = vec2(float(x),float(y));
	
	
	
	//i = i * m;
	
	//x=int(i.x);
	//y=int(i.y);
	
	//float size = 0.1;
	
	//float r = float((x%y)==0 && 0<y && length(c/iZoom - vec2(x/iZoom,y/iZoom))<0.01);
	//float g = float(abs(c.y-(sqrt(c.x))) < 0.1);
	//float g = float(abs(c.y-(c.x/3)) < 0.01);
	
	int scale = int( 1);
	
	float r = (x%(y/scale))/float(y/scale) * float(x>0 && y>0);
	float g = r;
	float b = r;
	
	float cross = 0.8*float((length(uv)<0.005));
	float grid = 0.1*mod(round(c.x/1) + round(c.y/1), 2);
	color = vec4(r, g, b, 1.0) + cross;
}