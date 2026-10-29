import React, { useState, useEffect, useRef } from 'react';
import { NEURO_METADATA } from '../data/modulesData';
import { sound } from '../utils/audio';
import { Cpu, Activity, Play, RotateCcw, Zap, Sparkles } from 'lucide-react';

export const NeuroAiStudio: React.FC = () => {
  const [activeFeatureIdx, setActiveFeatureIdx] = useState<number>(0);
  const [isSimulating, setIsSimulating] = useState<boolean>(true);
  const [targetPos, setTargetPos] = useState<{ x: number; y: number }>({ x: 200, y: 150 });
  const [crosshairPos, setCrosshairPos] = useState<{ x: number; y: number }>({ x: 160, y: 140 });
  const [hiddenStates, setHiddenStates] = useState<number[]>(() => Array(32).fill(0));
  const [predictedAngle, setPredictedAngle] = useState<{ yaw: number; pitch: number }>({ yaw: 0, pitch: 0 });

  const lossCanvasRef = useRef<HTMLCanvasElement | null>(null);
  const [hoveredEpoch, setHoveredEpoch] = useState<{ epoch: number; loss: number } | null>(null);

  // Render loss chart from default.json error array
  useEffect(() => {
    const canvas = lossCanvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    const width = canvas.width;
    const height = canvas.height;
    const errors = NEURO_METADATA.error;

    ctx.clearRect(0, 0, width, height);

    // Grid lines
    ctx.strokeStyle = 'rgba(255, 255, 255, 0.05)';
    ctx.lineWidth = 1;
    for (let x = 40; x < width; x += 60) {
      ctx.beginPath();
      ctx.moveTo(x, 10);
      ctx.lineTo(x, height - 25);
      ctx.stroke();
    }
    for (let y = 20; y < height - 20; y += 40) {
      ctx.beginPath();
      ctx.moveTo(30, y);
      ctx.lineTo(width - 10, y);
      ctx.stroke();
    }

    // Coordinates mapping
    const minLoss = Math.min(...errors);
    const maxLoss = Math.max(...errors);
    const padX = 40;
    const padY = 20;
    const chartW = width - padX - 15;
    const chartH = height - padY - 25;

    const getX = (i: number) => padX + (i / (errors.length - 1)) * chartW;
    const getY = (val: number) =>
      height - 25 - ((Math.log(val + 1) - Math.log(minLoss + 1)) / (Math.log(maxLoss + 1) - Math.log(minLoss + 1))) * chartH;

    // Gradient fill under curve
    const grad = ctx.createLinearGradient(0, 0, 0, height);
    grad.addColorStop(0, 'rgba(139, 92, 246, 0.35)');
    grad.addColorStop(1, 'rgba(139, 92, 246, 0.0)');

    ctx.beginPath();
    ctx.moveTo(getX(0), height - 25);
    for (let i = 0; i < errors.length; i++) {
      ctx.lineTo(getX(i), getY(errors[i]));
    }
    ctx.lineTo(getX(errors.length - 1), height - 25);
    ctx.closePath();
    ctx.fillStyle = grad;
    ctx.fill();

    // Line curve with glow
    ctx.beginPath();
    ctx.strokeStyle = '#a855f7';
    ctx.lineWidth = 2.5;
    ctx.shadowColor = '#c084fc';
    ctx.shadowBlur = 10;
    for (let i = 0; i < errors.length; i++) {
      if (i === 0) ctx.moveTo(getX(i), getY(errors[i]));
      else ctx.lineTo(getX(i), getY(errors[i]));
    }
    ctx.stroke();
    ctx.shadowBlur = 0;

    // Epoch dots
    for (let i = 0; i < errors.length; i += 8) {
      ctx.beginPath();
      ctx.arc(getX(i), getY(errors[i]), 3.5, 0, Math.PI * 2);
      ctx.fillStyle = '#f3e8ff';
      ctx.fill();
    }

    // Labels
    ctx.fillStyle = 'rgba(255, 255, 255, 0.4)';
    ctx.font = '10px monospace';
    ctx.fillText('Epoch 0', padX, height - 8);
    ctx.fillText(`Epoch ${errors.length - 1}`, width - 60, height - 8);
    ctx.fillText('Loss (log scale)', 6, 14);
  }, []);

  // Live simulation loop for crosshair & GRU states
  useEffect(() => {
    if (!isSimulating) return;

    let angle = 0;
    const interval = setInterval(() => {
      angle += 0.05;
      // Orbiting target
      const cx = 170;
      const cy = 130;
      const tx = cx + Math.cos(angle) * 75 + Math.sin(angle * 2.1) * 20;
      const ty = cy + Math.sin(angle) * 55;

      setTargetPos({ x: tx, y: ty });

      // Calculate neural prediction
      const dx = tx - crosshairPos.x;
      const dy = ty - crosshairPos.y;

      // Sinh squash forward step
      const stepYaw = Math.sinh(dx * 0.015) * 1.8;
      const stepPitch = Math.sinh(dy * 0.015) * 1.4;

      setCrosshairPos((prev) => ({
        x: prev.x + stepYaw * 0.18,
        y: prev.y + stepPitch * 0.18,
      }));

      setPredictedAngle({
        yaw: parseFloat(stepYaw.toFixed(2)),
        pitch: parseFloat(stepPitch.toFixed(2)),
      });

      // Update 32 hidden states with organic wave values
      setHiddenStates(() =>
        Array.from({ length: 32 }, (_, idx) => {
          return Math.sin(angle * 1.5 + idx * 0.4) * 0.5 + 0.5;
        })
      );
    }, 50);

    return () => clearInterval(interval);
  }, [isSimulating, crosshairPos]);

  return (
    <div className="space-y-6">
      {/* Studio Header */}
      <div className="flex flex-wrap items-center justify-between gap-4 p-5 rounded-2xl bg-gradient-to-r from-purple-950/40 via-indigo-950/30 to-slate-900/60 border border-purple-500/20">
        <div>
          <div className="flex items-center gap-2">
            <span className="p-1.5 rounded-lg bg-purple-500/20 text-purple-400">
              <Cpu className="w-5 h-5" />
            </span>
            <h2 className="text-xl font-bold font-title text-white tracking-wide">
              Neuro AI Engine (GRU 1.21.4)
            </h2>
            <span className="px-2.5 py-0.5 rounded-full text-xs font-semibold bg-emerald-500/10 text-emerald-400 border border-emerald-500/20">
              Online Inference
            </span>
          </div>
          <p className="text-sm text-slate-300 mt-1 max-w-2xl">
            Архитектура рекуррентной нейросети Policy с Mixture Density Network. Сглаживает углы атаки в 1.21.4
            под биомеханику человека с нулевыми флаг-детектами античитов.
          </p>
        </div>

        <div className="flex items-center gap-2">
          <button
            onClick={() => {
              sound.playClick();
              setIsSimulating(!isSimulating);
            }}
            className={`px-4 py-2 rounded-xl text-xs font-bold flex items-center gap-2 transition-all ${
              isSimulating
                ? 'bg-purple-600/30 text-purple-200 border border-purple-500/40 hover:bg-purple-600/50'
                : 'bg-emerald-600/30 text-emerald-200 border border-emerald-500/40 hover:bg-emerald-600/50'
            }`}
          >
            {isSimulating ? <RotateCcw className="w-3.5 h-3.5" /> : <Play className="w-3.5 h-3.5" />}
            {isSimulating ? 'Пауза симуляции' : 'Запустить симуляцию'}
          </button>
        </div>
      </div>

      {/* Grid: 3D Aim Predictor Canvas & Loss Graph */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Interactive Crosshair Tracking Canvas */}
        <div className="p-5 rounded-2xl acrylic-card relative overflow-hidden flex flex-col justify-between">
          <div className="flex items-center justify-between mb-3">
            <span className="text-sm font-semibold text-white flex items-center gap-2">
              <Activity className="w-4 h-4 text-purple-400" />
              Интерактивная симуляция наводки (GRU Aim Prediction)
            </span>
            <span className="text-xs font-mono text-purple-300">
              ΔYaw: {predictedAngle.yaw}° | ΔPitch: {predictedAngle.pitch}°
            </span>
          </div>

          <div
            className="relative w-full h-64 bg-slate-950/80 rounded-xl border border-white/10 overflow-hidden cursor-crosshair"
            onMouseMove={(e) => {
              const rect = e.currentTarget.getBoundingClientRect();
              if (!isSimulating) {
                setTargetPos({ x: e.clientX - rect.left, y: e.clientY - rect.top });
              }
            }}
          >
            {/* Minecraft Style Grid Lines */}
            <div className="absolute inset-0 opacity-15 bg-[radial-gradient(#8b5cf6_1px,transparent_1px)] [background-size:16px_16px]" />

            {/* Target Entity Box (Red Glow) */}
            <div
              className="absolute w-8 h-12 -ml-4 -mt-6 border-2 border-red-500/80 bg-red-500/20 rounded shadow-[0_0_15px_rgba(239,68,68,0.5)] transition-all duration-75 flex items-center justify-center pointer-events-none"
              style={{ left: `${targetPos.x}px`, top: `${targetPos.y}px` }}
            >
              <div className="w-3 h-3 rounded-full bg-red-400" />
            </div>

            {/* Neuro Aim Crosshair (Cyan/Purple Glow) */}
            <div
              className="absolute w-6 h-6 -ml-3 -mt-3 pointer-events-none transition-all duration-75"
              style={{ left: `${crosshairPos.x}px`, top: `${crosshairPos.y}px` }}
            >
              <div className="w-full h-0.5 bg-cyan-400 absolute top-1/2 -translate-y-1/2 shadow-[0_0_8px_#22d3ee]" />
              <div className="h-full w-0.5 bg-cyan-400 absolute left-1/2 -translate-x-1/2 shadow-[0_0_8px_#22d3ee]" />
              <div className="w-2 h-2 rounded-full border border-cyan-300 absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2" />
            </div>

            {/* Aim Prediction Vector Line */}
            <svg className="absolute inset-0 w-full h-full pointer-events-none">
              <line
                x1={crosshairPos.x}
                y1={crosshairPos.y}
                x2={targetPos.x}
                y2={targetPos.y}
                stroke="rgba(168, 85, 247, 0.4)"
                strokeDasharray="4 4"
                strokeWidth="1.5"
              />
            </svg>

            {/* Target Label */}
            <div className="absolute bottom-3 left-3 text-[11px] font-mono text-slate-400 bg-slate-900/80 px-2.5 py-1 rounded border border-white/10">
              Цель: LivingEntity (dist: {(Math.hypot(targetPos.x - crosshairPos.x, targetPos.y - crosshairPos.y) / 25).toFixed(2)}m)
            </div>
          </div>

          <div className="mt-3 flex items-center justify-between text-xs text-slate-400">
            <span>Функция сжатия: <strong className="text-purple-300">sinh(u, 179.0°)</strong></span>
            <span>Смеси Гауссиан: <strong className="text-purple-300">K=3 (MDN)</strong></span>
          </div>
        </div>

        {/* Training Loss Curve from default.json */}
        <div className="p-5 rounded-2xl acrylic-card flex flex-col justify-between">
          <div className="flex items-center justify-between mb-3">
            <span className="text-sm font-semibold text-white flex items-center gap-2">
              <Zap className="w-4 h-4 text-purple-400" />
              Кривая сходимости обучения (65 эпох из default.json)
            </span>
            <span className="text-xs font-mono text-emerald-400 bg-emerald-500/10 px-2 py-0.5 rounded border border-emerald-500/20">
              Min Loss: 0.001305
            </span>
          </div>

          <div className="w-full relative">
            <canvas
              ref={lossCanvasRef}
              width={520}
              height={230}
              className="w-full h-64 bg-slate-950/80 rounded-xl border border-white/10"
              onMouseMove={(e) => {
                const rect = e.currentTarget.getBoundingClientRect();
                const x = e.clientX - rect.left;
                const ratio = Math.max(0, Math.min(1, (x - 40) / (rect.width - 55)));
                const epochIdx = Math.round(ratio * (NEURO_METADATA.error.length - 1));
                setHoveredEpoch({
                  epoch: epochIdx,
                  loss: NEURO_METADATA.error[epochIdx],
                });
              }}
              onMouseLeave={() => setHoveredEpoch(null)}
            />
            {hoveredEpoch && (
              <div className="absolute top-4 right-4 bg-purple-900/90 text-purple-100 text-xs font-mono px-3 py-1.5 rounded-lg border border-purple-400/40 shadow-xl pointer-events-none">
                Эпоха {hoveredEpoch.epoch}: Loss = {hoveredEpoch.loss.toFixed(6)}
              </div>
            )}
          </div>

          <p className="text-xs text-slate-400 mt-3">
            Модель обучена на выборке более 1,200,000 тиков PvP боев с симуляцией задержек сети и флик-шотов.
          </p>
        </div>
      </div>

      {/* 32 Hidden Units Heatmap & 17 Features Inspector */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Hidden Units Matrix */}
        <div className="p-5 rounded-2xl acrylic-card lg:col-span-2">
          <div className="flex items-center justify-between mb-3">
            <span className="text-sm font-semibold text-white flex items-center gap-2">
              <Sparkles className="w-4 h-4 text-indigo-400" />
              Скрытый слой GRU Ячейки (32 Hidden States)
            </span>
            <span className="text-xs text-slate-400 font-mono">h_t activations</span>
          </div>

          <div className="grid grid-cols-8 sm:grid-cols-16 gap-2">
            {hiddenStates.map((val, idx) => {
              const intensity = Math.round(val * 100);
              return (
                <div
                  key={idx}
                  className="flex flex-col items-center p-2 rounded-xl bg-slate-950/70 border border-white/5 hover:border-purple-500/50 transition-all group"
                  title={`Neuron #${idx}: ${(val).toFixed(3)}`}
                >
                  <div
                    className="w-full h-8 rounded-lg mb-1.5 transition-colors duration-150"
                    style={{
                      backgroundColor: `rgba(168, 85, 247, ${0.15 + val * 0.85})`,
                      boxShadow: val > 0.6 ? '0 0 10px rgba(168,85,247,0.5)' : 'none',
                    }}
                  />
                  <span className="text-[10px] font-mono text-slate-400 group-hover:text-purple-300">
                    N{idx}
                  </span>
                  <span className="text-[9px] font-mono text-slate-300 font-bold">
                    {intensity}%
                  </span>
                </div>
              );
            })}
          </div>
        </div>

        {/* 17 Input Features List */}
        <div className="p-5 rounded-2xl acrylic-card">
          <span className="text-sm font-semibold text-white block mb-3">
            17 Входных признаков (Features)
          </span>
          <div className="space-y-1.5 max-h-60 overflow-y-auto pr-1">
            {NEURO_METADATA.featureNames.map((name, i) => (
              <button
                key={i}
                onClick={() => {
                  sound.playClick();
                  setActiveFeatureIdx(i);
                }}
                className={`w-full text-left px-3 py-1.5 rounded-lg text-xs font-mono flex items-center justify-between transition-colors ${
                  activeFeatureIdx === i
                    ? 'bg-purple-600/20 text-purple-200 border border-purple-500/30'
                    : 'bg-slate-950/40 text-slate-400 hover:text-slate-200 hover:bg-slate-900/50'
                }`}
              >
                <span>#{i + 1} {name}</span>
                <span className="text-[10px] opacity-70">
                  {i === 4 ? '3.6m' : i === 12 ? '1.0' : (Math.sin(i * 1.3) * 0.5).toFixed(2)}
                </span>
              </button>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};
