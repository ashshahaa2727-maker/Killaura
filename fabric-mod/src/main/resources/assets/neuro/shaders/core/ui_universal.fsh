#version 150

in vec2 FragCoord;
in vec2 TexCoord;
in vec4 FragColor;

uniform vec4 Radius;
uniform float CornerSmoothness;
uniform float RectSmoothness;
uniform vec4 ColorModulator;

out vec4 OutColor;

float uiBoxSDF(vec2 p, vec2 b, vec4 r, float s) {
    r = min(r, vec4(min(b.x, b.y) * s * 0.5));
    r.xy = (p.x > 0.0) ? r.xy : r.zw;
    r.x = (p.y > 0.0) ? r.x : r.y;
    vec2 q = abs(p) - b + r.x;
    vec2 qc = max(q, 0.0);
    float len = pow(pow(qc.x, s) + pow(qc.y, s), 1.0 / s);
    return min(max(q.x, q.y), 0.0) + len - r.x;
}

float sdfAA(float dist, float smoothness) {
    return max(smoothness, fwidth(dist));
}

void main() {
    vec2 size = TexCoord;
    vec2 center = size * 0.5;
    float dist = uiBoxSDF(center - (FragCoord * size), center - 1.0, Radius, CornerSmoothness);
    float alpha = 1.0 - smoothstep(1.0 - sdfAA(dist, RectSmoothness), 1.0, dist);
    
    if (alpha <= 0.001) {
        discard;
    }
    
    OutColor = vec4(FragColor.rgb, FragColor.a * alpha) * ColorModulator;
}
