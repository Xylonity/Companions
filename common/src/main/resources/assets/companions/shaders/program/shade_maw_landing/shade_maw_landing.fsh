#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D DepthSampler;

uniform mat4 InverseTransformMatrix;
uniform mat4 InverseModelViewMat;
uniform vec3 CameraPosition;

uniform float time;
uniform float intensity;

uniform float waveDurationSec;
uniform float waveSpeed;

uniform float ringWidth;
uniform float glowStrength;
uniform float chromaStrength;

uniform float waveStartRadius;
uniform float waveEndRadius;

uniform vec3 ringColorA;
uniform vec3 ringColorB;

uniform float verticalColumnHeight;
uniform float flashStrength;

uniform vec3 waveOrigin0;
uniform vec3 waveOrigin1;
uniform vec3 waveOrigin2;
uniform vec3 waveOrigin3;

uniform float waveAge0;
uniform float waveAge1;
uniform float waveAge2;
uniform float waveAge3;

in vec2 texCoord;
out vec4 fragColor;

const float TWO_PI = 6.28318530718;
const float FRAGMENT_STEP = 0.78539816339;

float saturate1(float x) {
    return clamp(x, 0.0, 1.0);
}

float easeOut(float x) {
    x = saturate1(x);
    return 1.0 - pow(1.0 - x, 2.45);
}

vec3 worldPosFromDepth(vec2 uv, float depth01) {
    vec3 ndc = vec3(uv, depth01) * 2.0 - 1.0;
    vec4 homPos = InverseTransformMatrix * vec4(ndc, 1.0);
    vec3 viewPos = homPos.xyz / max(abs(homPos.w), 1e-6);
    return (InverseModelViewMat * vec4(viewPos, 1.0)).xyz + CameraPosition;
}

float groundSurfaceMask(vec3 wpos) {
    vec3 n = cross(dFdx(wpos), dFdy(wpos));
    float len = length(n);
    if (len < 1e-6) {
        return 1.0;
    }

    n /= len;
    return smoothstep(0.24, 0.82, abs(n.y));
}

float angleDistance(float a, float b) {
    return abs(atan(sin(a - b), cos(a - b)));
}

float ringBand(float dist, float radius, float width) {
    float outerFeather = max(0.08, min(0.35, width * 0.18));
    float innerFeather = max(0.15, width * 0.55);
    float innerRadius = max(radius - width, 0.0);
    float outer = 1.0 - smoothstep(radius, radius + outerFeather, dist);
    float inner = innerRadius <= 0.0 ? 1.0 : smoothstep(innerRadius, innerRadius + innerFeather, dist);
    return outer * inner;
}

float landingFragments(float angle, float dist, float radius, float width) {
    float a = mod(angle + TWO_PI, TWO_PI);
    float sector = mod(floor(a / FRAGMENT_STEP + 0.5), 8.0);
    float center = sector * FRAGMENT_STEP;
    float angularDistance = angleDistance(a, center);

    float cardinal = 1.0 - step(0.5, mod(sector, 2.0));
    float radialSize = mix(1.0, 1.42, cardinal);
    float angularSize = mix(1.0, 1.16, cardinal);
    float signedDistance = dist - radius;
    float fragmentLength = max(0.55, radius * 0.18) * radialSize;
    float outerReach = fragmentLength * 0.82;
    float innerReach = fragmentLength * 1.12;
    float tipDistance = signedDistance >= 0.0 ? signedDistance / max(outerReach, 0.001) : -signedDistance / max(innerReach, 0.001);
    float taper = saturate1(tipDistance);
    float angularLimit = mix(0.045, 0.014, taper) * angularSize;

    float angular = 1.0 - smoothstep(angularLimit * 0.35, angularLimit, angularDistance);
    float radial = signedDistance >= 0.0 ? 1.0 - smoothstep(0.0, outerReach, signedDistance) : 1.0 - smoothstep(0.0, innerReach, -signedDistance);

    return pow(saturate1(angular), 1.45) * pow(saturate1(radial), 0.78) * mix(1.12, 1.42, cardinal);
}

void accumulateRing(in vec3 origin, in float ageSec, in vec3 wpos, in float floorMask, inout float ringOut, inout float fragmentOut) {
    if (ageSec < 0.0) {
        return;
    }

    float dur = max(waveDurationSec, 0.001);
    float prog = saturate1((ageSec * max(waveSpeed, 0.05)) / dur);

    vec3 rel = wpos - origin;
    float dist = length(rel.xz);
    float angle = atan(rel.z, rel.x);

    float slabHeight = max(0.6, verticalColumnHeight);
    float slab = 1.0 - smoothstep(slabHeight * 0.25, slabHeight, abs(rel.y));
    float groundMask = slab * floorMask;

    float radius = mix(waveStartRadius, waveEndRadius, easeOut(prog));
    float widthFade = smoothstep(0.0, 1.0, prog);
    float width = max(0.08, ringWidth * mix(0.95, 0.08, widthFade));

    float ring = ringBand(dist, radius, width);
    float fragments = landingFragments(angle, dist, radius, width);

    float haloGap = width * 0.05;
    float haloThickness = width * 0.15;
    float haloInner = radius + haloGap;
    float haloOuter = haloInner + haloThickness;
    float haloFeather = haloThickness * 0.45;
    float halo = (1.0 - smoothstep(haloOuter, haloOuter + haloFeather, dist)) * smoothstep(haloInner - haloFeather, haloInner, dist);

    float life = smoothstep(0.0, 0.05, prog) * (1.0 - smoothstep(0.50, 0.95, prog)) * groundMask;
    float pulse = 1.0 + sin(time * 20.0 + radius * 1.7) * 0.035 * flashStrength;

    ringOut += ring * life * pulse;
    fragmentOut += max(fragments, halo) * life;
}

void main() {
    vec2 uv = texCoord;
    vec3 scene = texture(DiffuseSampler, uv).rgb;
    float depth = texture(DepthSampler, uv).r;

    if (depth >= 0.999999 || depth <= 0.000001) {
        fragColor = vec4(scene, 1.0);
        return;
    }

    vec3 wpos = worldPosFromDepth(uv, depth);
    float floorMask = groundSurfaceMask(wpos);

    float ringMask = 0.0;
    float fragmentMask = 0.0;

    accumulateRing(waveOrigin0, waveAge0, wpos, floorMask, ringMask, fragmentMask);
    accumulateRing(waveOrigin1, waveAge1, wpos, floorMask, ringMask, fragmentMask);
    accumulateRing(waveOrigin2, waveAge2, wpos, floorMask, ringMask, fragmentMask);
    accumulateRing(waveOrigin3, waveAge3, wpos, floorMask, ringMask, fragmentMask);

    ringMask = saturate1(ringMask);
    fragmentMask = saturate1(fragmentMask);

    float ringOpacity = saturate1(ringMask * intensity);
    float fragmentOpacity = saturate1(fragmentMask * intensity);
    if (max(ringOpacity, fragmentOpacity) < 0.001) {
        fragColor = vec4(scene, 1.0);
        return;
    }

    vec3 outColor = mix(scene, ringColorA, ringOpacity);
    outColor = mix(outColor, ringColorB, fragmentOpacity);

    fragColor = vec4(clamp(outColor, 0.0, 1.0), 1.0);
}