import React, { useEffect, useRef } from 'react';

interface ShaderCanvasProps {
  shaderId: string;
  speed: number;
  intensity: number;
  radius: number;
  colorHex: string;
}

export const ShaderCanvas: React.FC<ShaderCanvasProps> = ({
  shaderId,
  speed,
  intensity,
  radius,
  colorHex,
}) => {
  const canvasRef = useRef<HTMLCanvasElement | null>(null);

  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;

    const gl = canvas.getContext('webgl');
    if (!gl) return;

    // Convert hex to rgb normalized
    const r = parseInt(colorHex.slice(1, 3), 16) / 255;
    const g = parseInt(colorHex.slice(3, 5), 16) / 255;
    const b = parseInt(colorHex.slice(5, 7), 16) / 255;

    const vsSource = `
      attribute vec2 aPosition;
      varying vec2 vUv;
      void main() {
        vUv = aPosition * 0.5 + 0.5;
        gl_Position = vec4(aPosition, 0.0, 1.0);
      }
    `;

    // Dynamic fragment shader based on selected shader
    let fsSource = '';
    if (shaderId === 'ui_universal') {
      fsSource = `
        precision highp float;
        varying vec2 vUv;
        uniform float uTime;
        uniform vec3 uColor;
        uniform float uRadius;
        uniform float uIntensity;

        float squircleSDF(vec2 p, vec2 b, float r, float s) {
          vec2 q = abs(p) - b + r;
          vec2 qc = max(q, 0.0);
          float len = pow(pow(qc.x, s) + pow(qc.y, s), 1.0 / s);
          return min(max(q.x, q.y), 0.0) + len - r;
        }

        void main() {
          vec2 p = vUv * 2.0 - 1.0;
          float s = 4.0; // corner smoothness exponent
          float r = uRadius * 0.25;
          float d = squircleSDF(p, vec2(0.7, 0.7), r, s);
          
          float border = smoothstep(0.01, -0.01, d);
          float innerGlow = exp(-max(0.0, d) * 12.0) * uIntensity;
          
          vec3 bg = mix(vec3(0.04, 0.05, 0.09), uColor * 0.4, (1.0 - length(p)) * 0.6);
          vec3 col = mix(bg, uColor, border * 0.8 + innerGlow * 0.4);
          
          gl_FragColor = vec4(col, 1.0);
        }
      `;
    } else if (shaderId === 'sky_nebula') {
      fsSource = `
        precision highp float;
        varying vec2 vUv;
        uniform float uTime;
        uniform vec3 uColor;
        uniform float uIntensity;

        // Simplex/Perlin noise approximation
        float hash(vec2 p) {
          return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453);
        }
        float noise(vec2 p) {
          vec2 i = floor(p);
          vec2 f = fract(p);
          f = f * f * (3.0 - 2.0 * f);
          return mix(mix(hash(i + vec2(0.0, 0.0)), hash(i + vec2(1.0, 0.0)), f.x),
                     mix(hash(i + vec2(0.0, 1.0)), hash(i + vec2(1.0, 1.0)), f.x), f.y);
        }
        float fbm(vec2 p) {
          float v = 0.0;
          float a = 0.5;
          for (int i = 0; i < 4; i++) {
            v += a * noise(p);
            p = p * 2.0;
            a *= 0.5;
          }
          return v;
        }

        void main() {
          vec2 uv = (vUv - 0.5) * 2.5;
          float t = uTime * 0.2;
          
          float n1 = fbm(uv + vec2(t * 0.3, t * 0.2));
          float n2 = fbm(uv * 1.5 - vec2(t * 0.2, -t * 0.1) + n1);
          
          vec3 deepSpace = vec3(0.03, 0.04, 0.08);
          vec3 nebulaA = uColor;
          vec3 nebulaB = vec3(0.15, 0.7, 0.95);
          
          vec3 col = mix(deepSpace, nebulaA, n1 * uIntensity);
          col = mix(col, nebulaB, n2 * 0.6 * uIntensity);
          
          // Star points
          float star = pow(hash(floor(uv * 40.0)), 30.0) * 1.5;
          col += vec3(star);
          
          gl_FragColor = vec4(col, 1.0);
        }
      `;
    } else {
      // Liquid glass / Gaussian glow
      fsSource = `
        precision highp float;
        varying vec2 vUv;
        uniform float uTime;
        uniform vec3 uColor;
        uniform float uIntensity;

        void main() {
          vec2 p = vUv * 2.0 - 1.0;
          float t = uTime * 0.8;
          float wave = sin(p.x * 6.0 + t) * cos(p.y * 6.0 + t) * 0.15;
          float dist = length(p + wave);
          
          float glow = exp(-dist * 2.5) * uIntensity;
          vec3 col = mix(vec3(0.05, 0.06, 0.1), uColor, glow);
          
          gl_FragColor = vec4(col, 1.0);
        }
      `;
    }

    const compileShader = (type: number, src: string) => {
      const s = gl.createShader(type);
      if (!s) return null;
      gl.shaderSource(s, src);
      gl.compileShader(s);
      return s;
    };

    const vs = compileShader(gl.VERTEX_SHADER, vsSource);
    const fs = compileShader(gl.FRAGMENT_SHADER, fsSource);
    if (!vs || !fs) return;

    const program = gl.createProgram();
    if (!program) return;
    gl.attachShader(program, vs);
    gl.attachShader(program, fs);
    gl.linkProgram(program);
    gl.useProgram(program);

    const positionBuffer = gl.createBuffer();
    gl.bindBuffer(gl.ARRAY_BUFFER, positionBuffer);
    gl.bufferData(
      gl.ARRAY_BUFFER,
      new Float32Array([-1, -1, 1, -1, -1, 1, -1, 1, 1, -1, 1, 1]),
      gl.STATIC_DRAW
    );

    const posAttr = gl.getAttribLocation(program, 'aPosition');
    gl.enableVertexAttribArray(posAttr);
    gl.vertexAttribPointer(posAttr, 2, gl.FLOAT, false, 0, 0);

    const uTimeLoc = gl.getUniformLocation(program, 'uTime');
    const uColorLoc = gl.getUniformLocation(program, 'uColor');
    const uIntensityLoc = gl.getUniformLocation(program, 'uIntensity');
    const uRadiusLoc = gl.getUniformLocation(program, 'uRadius');

    let animId: number;
    let startTime = performance.now();

    const render = () => {
      const now = performance.now();
      const elapsed = ((now - startTime) / 1000) * speed;

      gl.viewport(0, 0, canvas.width, canvas.height);
      gl.uniform1f(uTimeLoc, elapsed);
      gl.uniform3f(uColorLoc, r, g, b);
      gl.uniform1f(uIntensityLoc, intensity);
      if (uRadiusLoc) gl.uniform1f(uRadiusLoc, radius);

      gl.drawArrays(gl.TRIANGLES, 0, 6);
      animId = requestAnimationFrame(render);
    };

    render();

    return () => {
      cancelAnimationFrame(animId);
      gl.deleteProgram(program);
      gl.deleteShader(vs);
      gl.deleteShader(fs);
      gl.deleteBuffer(positionBuffer);
    };
  }, [shaderId, speed, intensity, radius, colorHex]);

  return (
    <canvas
      ref={canvasRef}
      width={600}
      height={340}
      className="w-full h-full rounded-2xl shadow-inner border border-white/10"
    />
  );
};
