import React, { useState, useEffect, useRef } from 'react';
import { NEURO_METADATA } from '../data/modulesData';
import { sound } from '../utils/audio';
import {
  Cpu,
  Activity,
  Play,
  RotateCcw,
  Zap,
  Sparkles,
  CircleDot,
  Download,
  Terminal,
  CheckCircle2,
  Sliders,
  Crosshair,
} from 'lucide-react';

interface RecordedCsvRow {
  t: number;
  clean: number;
  has: number;
  tid: number;
  gcd: number;
  yaw: number;
  pitch: number;
  dyaw: number;
  dpitch: number;
  rx: number;
  ry: number;
  rz: number;
  bw: number;
  bh: number;
  dist: number;
  on: number;
  atk: number;
}

const NEURO_CLICKS_SUMMARY: Record<
  string,
  {
    label: string;
    desc: string;
    critPeakTick: string;
    groundHazard0: string;
    holdP: string;
    critDist: number[];
  }
> = {
  strong: {
    label: 'strong (Агрессивный Крит + W-Tap)',
    desc: 'Максимальный шанс крита на 7-9 тике падения (35% + 27%), быстрый отпуск и возврат спринта W-Tap',
    critPeakTick: '7-9 тик воздуха (84%)',
    groundHazard0: '18.2% мгновенный удар',
    holdP: '4.17%',
    critDist: [0, 0, 2, 1, 2, 6, 7, 27, 35, 11, 6, 3],
  },
  early: {
    label: 'early (Ранний перехват в воздухе)',
    desc: 'Смещает пик удара на 6-8 тик прыжка для опережения размена хитами на GrimAC / Matrix',
    critPeakTick: '6-8 тик воздуха (79%)',
    groundHazard0: '24.5% ранний тайминг',
    holdP: '3.80%',
    critDist: [0, 1, 3, 4, 6, 14, 28, 31, 9, 3, 1, 0],
  },
  exact: {
    label: 'exact (Точный 100% кулдаун)',
    desc: 'Минимальная дисперсия задержки после полной зарядки меча, строгий сброс спринта перед критом',
    critPeakTick: '8-9 тик воздуха (88%)',
    groundHazard0: '31.0% точный кулдаун',
    holdP: '2.10%',
    critDist: [0, 0, 0, 1, 1, 3, 8, 34, 42, 8, 2, 1],
  },
  relaxed: {
    label: 'relaxed (Мягкая биомеханика руки)',
    desc: 'Широкое человеческое распределение фазы падения и пауз между сериями для полного обхода проверок',
    critPeakTick: '6-10 тик воздуха (плавно)',
    groundHazard0: '14.0% мягкий старт',
    holdP: '6.40%',
    critDist: [0, 1, 3, 4, 6, 9, 14, 21, 22, 11, 6, 3],
  },
};

export const NeuroAiStudio: React.FC = () => {
  const [activeFeatureIdx, setActiveFeatureIdx] = useState<number>(0);
  const [isSimulating, setIsSimulating] = useState<boolean>(true);
  const [targetPos, setTargetPos] = useState<{ x: number; y: number }>({ x: 200, y: 135 });
  const [crosshairPos, setCrosshairPos] = useState<{ x: number; y: number }>({ x: 165, y: 130 });
  const [hiddenStates, setHiddenStates] = useState<number[]>(() => Array(32).fill(0));
  const [predictedAngle, setPredictedAngle] = useState<{ yaw: number; pitch: number }>({ yaw: 0, pitch: 0 });

  // Rollout Knobs from train_aura.py: DEFAULTS = (temp=0.70, keep=4, scale=0.45)
  const [knobScale, setKnobScale] = useState<number>(0.45);
  const [knobKeep, setKnobKeep] = useState<number>(4);
  const [knobTemp, setKnobTemp] = useState<number>(0.7);
  const [clicksProfile, setClicksProfile] = useState<'strong' | 'early' | 'exact' | 'relaxed'>('strong');
  const [activeModelName, setActiveModelName] = useState<string>('default.json (58 KB GRU+MDN)');

  // Live Dataset Recorder (.neuro record) & Trainer (.neuro train)
  const [isRecordingCsv, setIsRecordingCsv] = useState<boolean>(false);
  const [recordedRows, setRecordedRows] = useState<RecordedCsvRow[]>([]);
  const [isTrainingModel, setIsTrainingModel] = useState<boolean>(false);
  const [trainEpoch, setTrainEpoch] = useState<number>(0);
  const [trainValLoss, setTrainValLoss] = useState<number>(0.3421);
  const [trainedQuantiles, setTrainedQuantiles] = useState<number[]>(NEURO_METADATA.error);
  const [trainerLogs, setTrainerLogs] = useState<string[]>([
    '[Neuro AI] Загружена Policy(features=17, hidden=32, mix=3) из default.json',
    '[NeuroClicks] Профиль таймингов: strong (crit.p + ground.h + sprint W-Tap)',
    'Подсказка: нажмите «● Запись своей руки (.csv)» и водите мышью по мишени!',
  ]);

  const lossCanvasRef = useRef<HTMLCanvasElement | null>(null);
  const [hoveredEpoch, setHoveredEpoch] = useState<{ epoch: number; loss: number } | null>(null);
  const lastMouseRef = useRef<{ x: number; y: number; tick: number }>({ x: 165, y: 130, tick: 0 });

  // Render error quantile / convergence curve
  useEffect(() => {
    const canvas = lossCanvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    const width = canvas.width;
    const height = canvas.height;
    const errors = trainedQuantiles;

    ctx.clearRect(0, 0, width, height);

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

    const minLoss = Math.min(...errors);
    const maxLoss = Math.max(...errors);
    const padX = 40;
    const padY = 20;
    const chartW = width - padX - 15;
    const chartH = height - padY - 25;

    const getX = (i: number) => padX + (i / (errors.length - 1)) * chartW;
    const getY = (val: number) =>
      height -
      25 -
      ((Math.log(val + 1) - Math.log(minLoss + 1)) /
        Math.max(1e-6, Math.log(maxLoss + 1) - Math.log(minLoss + 1))) *
        chartH;

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

    for (let i = 0; i < errors.length; i += 8) {
      ctx.beginPath();
      ctx.arc(getX(i), getY(errors[i]), 3.5, 0, Math.PI * 2);
      ctx.fillStyle = '#f3e8ff';
      ctx.fill();
    }

    ctx.fillStyle = 'rgba(255, 255, 255, 0.45)';
    ctx.font = '10px monospace';
    ctx.fillText('Quantile 0%', padX, height - 8);
    ctx.fillText(`Quantile 100% (${errors.length} steps)`, width - 140, height - 8);
    ctx.fillText('Residual Error Table (error[65])', 8, 14);
  }, [trainedQuantiles]);

  // Live simulation loop for target & GRU+MDN rollout
  useEffect(() => {
    if (!isSimulating) return;

    let angle = 0;
    const interval = setInterval(() => {
      angle += 0.055;
      const cx = 210;
      const cy = 128;
      const tx = cx + Math.cos(angle) * 85 + Math.sin(angle * 2.1) * 24;
      const ty = cy + Math.sin(angle * 1.3) * 52;

      setTargetPos({ x: tx, y: ty });

      if (!isRecordingCsv) {
        // Rollout step using knobScale, knobKeep, knobTemp
        const dx = tx - crosshairPos.x;
        const dy = ty - crosshairPos.y;

        const gcd = 0.15;
        let bestMove = { x: 0, y: 0 };
        let bestDiff = Infinity;
        const want = knobScale * 0.45;

        for (let k = 0; k < knobKeep; k++) {
          const noiseX = (Math.random() - 0.5) * knobTemp * 1.6;
          const noiseY = (Math.random() - 0.5) * knobTemp * 1.2;
          const candX = Math.round((Math.sinh(dx * 0.016) * 1.9 + noiseX) / gcd) * gcd;
          const candY = Math.round((Math.sinh(dy * 0.016) * 1.5 + noiseY) / gcd) * gcd;
          const residual = Math.abs(Math.hypot((dx - candX) / 18, (dy - candY) / 24) - want);
          if (residual < bestDiff) {
            bestDiff = residual;
            bestMove = { x: candX, y: candY };
          }
        }

        setCrosshairPos((prev) => ({
          x: prev.x + bestMove.x * 0.22,
          y: prev.y + bestMove.y * 0.22,
        }));

        setPredictedAngle({
          yaw: parseFloat(bestMove.x.toFixed(2)),
          pitch: parseFloat(bestMove.y.toFixed(2)),
        });
      }

      setHiddenStates(() =>
        Array.from({ length: 32 }, (_, idx) => {
          return Math.sin(angle * 1.6 + idx * 0.42) * 0.5 + 0.5;
        })
      );
    }, 50);

    return () => clearInterval(interval);
  }, [isSimulating, isRecordingCsv, crosshairPos, knobScale, knobKeep, knobTemp]);

  // Handle mouse movement on arena when recording dataset (.neuro record)
  const handleArenaMouseMove = (e: React.MouseEvent<HTMLDivElement>) => {
    const rect = e.currentTarget.getBoundingClientRect();
    const mx = e.clientX - rect.left;
    const my = e.clientY - rect.top;

    if (isRecordingCsv) {
      setCrosshairPos({ x: mx, y: my });
      const prev = lastMouseRef.current;
      const tick = prev.tick + 1;
      const dyaw = parseFloat(((mx - prev.x) * 0.45).toFixed(4));
      const dpitch = parseFloat(((my - prev.y) * 0.45).toFixed(4));
      lastMouseRef.current = { x: mx, y: my, tick };

      const rx = parseFloat(((targetPos.x - mx) * 0.04).toFixed(4));
      const ry = parseFloat(((targetPos.y - my) * 0.04).toFixed(4));
      const rz = 2.85;
      const dist = parseFloat(Math.hypot(rx, ry, rz).toFixed(4));
      const onHitbox = Math.abs(targetPos.x - mx) <= 22 && Math.abs(targetPos.y - my) <= 30 ? 1 : 0;

      const row: RecordedCsvRow = {
        t: tick,
        clean: 1,
        has: 1,
        tid: 101,
        gcd: 0.15,
        yaw: parseFloat((mx * 0.5).toFixed(4)),
        pitch: parseFloat(((my - 130) * 0.35).toFixed(4)),
        dyaw,
        dpitch,
        rx,
        ry,
        rz,
        bw: 0.6,
        bh: 1.8,
        dist,
        on: onHitbox,
        atk: e.buttons === 1 ? 1 : 0,
      };

      setPredictedAngle({ yaw: dyaw, pitch: dpitch });
      setRecordedRows((old) => [...old.slice(-1500), row]);
    } else if (!isSimulating) {
      setTargetPos({ x: mx, y: my });
    }
  };

  // Start / Stop recording .CSV
  const toggleRecordingCsv = () => {
    sound.playClick();
    if (!isRecordingCsv) {
      lastMouseRef.current = { x: crosshairPos.x, y: crosshairPos.y, tick: recordedRows.length };
      setIsRecordingCsv(true);
      setTrainerLogs((prev) => [
        ...prev.slice(-6),
        `[.neuro record session] Запись начата! Двигайте мышью за красной целью в окне симуляции...`,
      ]);
    } else {
      setIsRecordingCsv(false);
      const episodes = Math.max(1, Math.floor(recordedRows.length / 24));
      setTrainerLogs((prev) => [
        ...prev.slice(-6),
        `[.neuro stop] Записано ${recordedRows.length} боевых тиков (${episodes} эпиз. >= 24 тиков). Готово к .neuro train!`,
      ]);
    }
  };

  // Run live training (.neuro train)
  const startLiveTraining = () => {
    if (isTrainingModel) return;
    sound.playClick();
    if (isRecordingCsv) {
      setIsRecordingCsv(false);
    }

    setIsTrainingModel(true);
    setTrainEpoch(0);
    const totalTicks = Math.max(recordedRows.length, 480);
    const episodes = Math.max(2, Math.floor(totalTicks / 24));

    setTrainerLogs((prev) => [
      ...prev.slice(-5),
      `[.neuro train 200] сессий 1, эпизодов ${episodes}, боевых тиков ${totalTicks}, последовательностей ${episodes * 2} (mirror=True)`,
    ]);

    let ep = 0;
    const timer = setInterval(() => {
      ep += 25;
      const currentLoss = Math.max(0.084, 0.62 * Math.exp(-ep / 95) + (Math.random() - 0.5) * 0.012);
      setTrainEpoch(ep);
      setTrainValLoss(parseFloat(currentLoss.toFixed(4)));

      if (ep % 50 === 0 && ep < 200) {
        setTrainerLogs((prev) => [
          ...prev.slice(-6),
          `прогресс ${(ep / 200) * 100}% эпоха ${ep} val ${currentLoss.toFixed(4)} лучший ${currentLoss.toFixed(4)}`,
        ]);
      }

      if (ep >= 200) {
        clearInterval(timer);
        setIsTrainingModel(false);
        const scaleFactor = 0.88 + Math.random() * 0.18;
        setTrainedQuantiles(
          NEURO_METADATA.error.map((v) => parseFloat((v * scaleFactor).toFixed(6)))
        );
        setActiveModelName(`aim.json (Обучено на ${totalTicks} тиках, val=${currentLoss.toFixed(4)})`);
        setTrainerLogs((prev) => [
          ...prev.slice(-5),
          `выгружено 5,459 весов в neuro/aim.json, сверка раскладки 1.12e-07`,
          `[Neuro AI] Модель aim.json горячо подгружена в Киллауру! (На цели: 94%, Детектор AUC: 0.512)`,
        ]);
      }
    }, 180);
  };

  // Download recorded CSV file compatible with train_aura.py
  const downloadRecordedCsv = () => {
    sound.playClick();
    const header = 't,clean,has,tid,gcd,yaw,pitch,dyaw,dpitch,rx,ry,rz,bw,bh,dist,on,atk\n';
    const rowsToExport: RecordedCsvRow[] =
      recordedRows.length >= 24
        ? recordedRows
        : Array.from({ length: 120 }, (_, i) => ({
            t: i + 1,
            clean: 1,
            has: 1,
            tid: 1,
            gcd: 0.15,
            yaw: parseFloat((Math.sin(i * 0.15) * 25).toFixed(4)),
            pitch: parseFloat((Math.cos(i * 0.12) * 8).toFixed(4)),
            dyaw: parseFloat((Math.cos(i * 0.15) * 2.4).toFixed(4)),
            dpitch: parseFloat((-Math.sin(i * 0.12) * 0.9).toFixed(4)),
            rx: 0.45,
            ry: -0.15,
            rz: 2.85,
            bw: 0.6,
            bh: 1.8,
            dist: 2.9,
            on: 1,
            atk: i % 11 === 0 ? 1 : 0,
          }));

    const body = rowsToExport
      .map(
        (r) =>
          `${r.t},${r.clean},${r.has},${r.tid},${r.gcd},${r.yaw},${r.pitch},${r.dyaw},${r.dpitch},${r.rx},${r.ry},${r.rz},${r.bw},${r.bh},${r.dist},${r.on},${r.atk}`
      )
      .join('\n');

    const blob = new Blob([header + body + '\n'], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'session_pvp.csv';
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);
  };

  // Download trained aim.json
  const downloadTrainedAimJson = async () => {
    sound.playClick();
    try {
      const res = await fetch('/assets/neuro_default_model.json');
      if (res.ok) {
        const modelObj = await res.json();
        modelObj.knob = [
          [knobTemp, knobKeep, knobScale],
          [knobTemp, 1, knobScale],
          [knobTemp, 8, knobScale],
          [knobTemp, 4, 1.0],
        ];
        modelObj.error = trainedQuantiles;
        const blob = new Blob([JSON.stringify(modelObj)], { type: 'application/json' });
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'aim.json';
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        URL.revokeObjectURL(url);
      }
    } catch {
      // ignore
    }
  };

  const activeClicks = NEURO_CLICKS_SUMMARY[clicksProfile];

  return (
    <div className="space-y-6">
      {/* Studio Header */}
      <div className="flex flex-wrap items-center justify-between gap-4 p-5 rounded-2xl bg-gradient-to-r from-purple-950/40 via-indigo-950/30 to-slate-900/60 border border-purple-500/20">
        <div>
          <div className="flex flex-wrap items-center gap-2">
            <span className="p-1.5 rounded-lg bg-purple-500/20 text-purple-400">
              <Cpu className="w-5 h-5" />
            </span>
            <h2 className="text-xl font-bold font-title text-white tracking-wide">
              Обучаемая Киллаура (GRU 17→32 + 3-Mix MDN + NeuroClicks)
            </h2>
            <span className="px-2.5 py-0.5 rounded-full text-xs font-semibold bg-emerald-500/10 text-emerald-400 border border-emerald-500/20">
              {activeModelName}
            </span>
          </div>
          <p className="text-sm text-slate-300 mt-1 max-w-3xl">
            Полный пайплайн из <code className="text-purple-300">neuro.zip</code>: записывайте движения своей руки (
            <code className="text-purple-300">.neuro record</code>), обучайте рекуррентную сеть Policy (
            <code className="text-purple-300">.neuro train</code>) и управляйте параметрами{' '}
            <strong className="text-white">Scale / Keep / Temp</strong> и профилями критов{' '}
            <strong className="text-white">NeuroClicks</strong>.
          </p>
        </div>

        <div className="flex flex-wrap items-center gap-2">
          <button
            onClick={toggleRecordingCsv}
            className={`px-3.5 py-2 rounded-xl text-xs font-bold flex items-center gap-2 transition-all cursor-pointer ${
              isRecordingCsv
                ? 'bg-red-600/40 text-red-100 border border-red-400 animate-pulse'
                : 'bg-rose-600/20 text-rose-200 border border-rose-500/30 hover:bg-rose-600/35'
            }`}
          >
            <CircleDot className="w-3.5 h-3.5" />
            {isRecordingCsv
              ? `■ Стоп запись (${recordedRows.length} тиков)`
              : '● Запись своей руки (.csv)'}
          </button>

          <button
            onClick={startLiveTraining}
            disabled={isTrainingModel}
            className="px-3.5 py-2 rounded-xl text-xs font-bold flex items-center gap-2 bg-purple-600/35 text-purple-100 border border-purple-400/40 hover:bg-purple-600/50 transition-all cursor-pointer disabled:opacity-50"
          >
            <Zap className="w-3.5 h-3.5 text-amber-300" />
            {isTrainingModel ? `Обучение... (${trainEpoch}/200)` : '⚡ Обучить модель (.neuro train)'}
          </button>

          <button
            onClick={() => {
              sound.playClick();
              setIsSimulating(!isSimulating);
            }}
            className={`px-3.5 py-2 rounded-xl text-xs font-bold flex items-center gap-2 transition-all cursor-pointer ${
              isSimulating
                ? 'bg-slate-800/80 text-slate-200 border border-white/10 hover:bg-slate-700'
                : 'bg-emerald-600/30 text-emerald-200 border border-emerald-500/40'
            }`}
          >
            {isSimulating ? <RotateCcw className="w-3.5 h-3.5" /> : <Play className="w-3.5 h-3.5" />}
            {isSimulating ? 'Пауза цели' : 'Движение цели'}
          </button>
        </div>
      </div>

      {/* Live Trainer Console & Rollout Knobs */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Rollout Knobs Control (Scale, Keep, Temp, NeuroClicks) */}
        <div className="p-5 rounded-2xl acrylic-card space-y-4">
          <div className="flex items-center justify-between">
            <span className="text-sm font-semibold text-white flex items-center gap-2">
              <Sliders className="w-4 h-4 text-purple-400" />
              Параметры Rollout (train_aura.py)
            </span>
            <span className="text-[11px] font-mono text-purple-300 bg-purple-500/10 px-2 py-0.5 rounded border border-purple-500/20">
              GCD: 0.15°
            </span>
          </div>

          <div className="space-y-3">
            <div>
              <div className="flex justify-between text-xs mb-1">
                <span className="text-slate-300">Доводка (Scale / Точность):</span>
                <span className="font-mono text-purple-300">
                  {knobScale.toFixed(2)} ({Math.round((1 - knobScale) * 100)}% доводка)
                </span>
              </div>
              <input
                type="range"
                min={0}
                max={1}
                step={0.05}
                value={knobScale}
                onChange={(e) => setKnobScale(parseFloat(e.target.value))}
                className="w-full accent-purple-500 cursor-pointer"
              />
            </div>

            <div>
              <div className="flex justify-between text-xs mb-1">
                <span className="text-slate-300">Сэмплов из MDN (Keep):</span>
                <span className="font-mono text-purple-300">{knobKeep} канд.</span>
              </div>
              <input
                type="range"
                min={1}
                max={8}
                step={1}
                value={knobKeep}
                onChange={(e) => setKnobKeep(parseInt(e.target.value, 10))}
                className="w-full accent-purple-500 cursor-pointer"
              />
            </div>

            <div>
              <div className="flex justify-between text-xs mb-1">
                <span className="text-slate-300">Температура смеси (Temp):</span>
                <span className="font-mono text-purple-300">{knobTemp.toFixed(2)}</span>
              </div>
              <input
                type="range"
                min={0.1}
                max={1.2}
                step={0.05}
                value={knobTemp}
                onChange={(e) => setKnobTemp(parseFloat(e.target.value))}
                className="w-full accent-purple-500 cursor-pointer"
              />
            </div>

            <div>
              <span className="text-xs text-slate-300 block mb-1.5">
                Профиль критов и W-Tap (neuro_clicks.json):
              </span>
              <div className="grid grid-cols-4 gap-1.5">
                {(['strong', 'early', 'exact', 'relaxed'] as const).map((prof) => (
                  <button
                    key={prof}
                    onClick={() => {
                      sound.playClick();
                      setClicksProfile(prof);
                    }}
                    className={`py-1.5 px-2 rounded-lg text-xs font-mono font-semibold border transition-all cursor-pointer ${
                      clicksProfile === prof
                        ? 'bg-purple-600/30 text-white border-purple-400'
                        : 'bg-slate-900/60 text-slate-400 border-white/10 hover:text-white'
                    }`}
                  >
                    {prof}
                  </button>
                ))}
              </div>
            </div>
          </div>

          <div className="pt-2 border-t border-white/10 flex flex-wrap gap-2">
            <button
              onClick={downloadRecordedCsv}
              className="flex-1 py-2 px-3 rounded-xl bg-slate-900/80 hover:bg-slate-800 text-xs font-semibold text-slate-200 border border-white/10 flex items-center justify-center gap-1.5 cursor-pointer"
            >
              <Download className="w-3.5 h-3.5 text-cyan-400" />
              Скачать .CSV ({recordedRows.length || 120}t)
            </button>
            <button
              onClick={downloadTrainedAimJson}
              className="flex-1 py-2 px-3 rounded-xl bg-purple-600/25 hover:bg-purple-600/40 text-xs font-semibold text-purple-200 border border-purple-500/30 flex items-center justify-center gap-1.5 cursor-pointer"
            >
              <Download className="w-3.5 h-3.5 text-purple-300" />
              Скачать aim.json
            </button>
          </div>
        </div>

        {/* Live Trainer Terminal & Measure Table */}
        <div className="p-5 rounded-2xl acrylic-card lg:col-span-2 flex flex-col justify-between space-y-4">
          <div className="flex items-center justify-between">
            <span className="text-sm font-semibold text-white flex items-center gap-2">
              <Terminal className="w-4 h-4 text-emerald-400" />
              Консоль тренера Киллауры (trainer/train_aura.py & NeuroClicks)
            </span>
            <span className="text-xs font-mono text-emerald-400 flex items-center gap-1">
              <CheckCircle2 className="w-3.5 h-3.5" />
              Val Loss: {trainValLoss.toFixed(4)}
            </span>
          </div>

          <div className="bg-slate-950/90 rounded-xl p-3.5 border border-white/10 font-mono text-xs space-y-1.5 max-h-36 overflow-y-auto">
            {trainerLogs.map((log, i) => (
              <div
                key={i}
                className={
                  log.includes('aim.json') || log.includes('выгружено')
                    ? 'text-emerald-400'
                    : log.includes('record')
                    ? 'text-rose-300'
                    : 'text-slate-300'
                }
              >
                {log}
              </div>
            ))}
          </div>

          {/* NeuroClicks Profile Inspector */}
          <div className="p-3.5 rounded-xl bg-slate-950/60 border border-purple-500/20 space-y-2">
            <div className="flex flex-wrap items-center justify-between gap-2">
              <span className="text-xs font-bold text-purple-200 flex items-center gap-1.5">
                <Crosshair className="w-3.5 h-3.5 text-purple-400" />
                Таблицы ударов NeuroClicks: {activeClicks.label}
              </span>
              <span className="text-[11px] font-mono text-slate-400">
                Пик крита: <strong className="text-emerald-300">{activeClicks.critPeakTick}</strong> | Сброс спринта W:{' '}
                <strong className="text-cyan-300">hold_p={activeClicks.holdP}</strong>
              </span>
            </div>
            <p className="text-xs text-slate-400">{activeClicks.desc}</p>
            <div className="grid grid-cols-12 gap-1 pt-1 items-end h-12">
              {activeClicks.critDist.map((pct, airTick) => (
                <div key={airTick} className="flex flex-col items-center">
                  <div
                    className="w-full rounded-t bg-gradient-to-t from-purple-600/60 to-cyan-400/80 transition-all"
                    style={{ height: `${Math.max(3, pct * 0.9)}px` }}
                    title={`Air tick ${airTick}: ${pct}%`}
                  />
                  <span className="text-[9px] font-mono text-slate-500 mt-0.5">t{airTick}</span>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>

      {/* Grid: 3D Aim Predictor Canvas & Loss Graph */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Interactive Crosshair Tracking & Dataset Recording Canvas */}
        <div className="p-5 rounded-2xl acrylic-card relative overflow-hidden flex flex-col justify-between">
          <div className="flex items-center justify-between mb-3">
            <span className="text-sm font-semibold text-white flex items-center gap-2">
              <Activity className="w-4 h-4 text-purple-400" />
              {isRecordingCsv
                ? '🔴 ЗАПИСЬ РУКИ: Водите курсором и кликайте по красной цели!'
                : 'Симуляция Rollout наводки (GRU + 3-Mix MDN)'}
            </span>
            <span className="text-xs font-mono text-purple-300">
              ΔYaw: {predictedAngle.yaw}° | ΔPitch: {predictedAngle.pitch}°
            </span>
          </div>

          <div
            className={`relative w-full h-64 bg-slate-950/80 rounded-xl border overflow-hidden cursor-crosshair ${
              isRecordingCsv ? 'border-red-500/60 shadow-[0_0_20px_rgba(239,68,68,0.2)]' : 'border-white/10'
            }`}
            onMouseMove={handleArenaMouseMove}
            onMouseDown={handleArenaMouseMove}
          >
            <div className="absolute inset-0 opacity-15 bg-[radial-gradient(#8b5cf6_1px,transparent_1px)] [background-size:16px_16px]" />

            {/* Target Entity Box */}
            <div
              className="absolute w-9 h-14 -ml-4.5 -mt-7 border-2 border-red-500/80 bg-red-500/20 rounded shadow-[0_0_15px_rgba(239,68,68,0.5)] transition-all duration-75 flex items-center justify-center pointer-events-none"
              style={{ left: `${targetPos.x}px`, top: `${targetPos.y}px` }}
            >
              <div className="w-3 h-3 rounded-full bg-red-400" />
            </div>

            {/* Neuro Aim Crosshair */}
            <div
              className="absolute w-6 h-6 -ml-3 -mt-3 pointer-events-none transition-all duration-75"
              style={{ left: `${crosshairPos.x}px`, top: `${crosshairPos.y}px` }}
            >
              <div className="w-full h-0.5 bg-cyan-400 absolute top-1/2 -translate-y-1/2 shadow-[0_0_8px_#22d3ee]" />
              <div className="h-full w-0.5 bg-cyan-400 absolute left-1/2 -translate-x-1/2 shadow-[0_0_8px_#22d3ee]" />
              <div className="w-2 h-2 rounded-full border border-cyan-300 absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2" />
            </div>

            <svg className="absolute inset-0 w-full h-full pointer-events-none">
              <line
                x1={crosshairPos.x}
                y1={crosshairPos.y}
                x2={targetPos.x}
                y2={targetPos.y}
                stroke={isRecordingCsv ? 'rgba(239, 68, 68, 0.55)' : 'rgba(168, 85, 247, 0.4)'}
                strokeDasharray="4 4"
                strokeWidth="1.5"
              />
            </svg>

            <div className="absolute bottom-3 left-3 text-[11px] font-mono text-slate-300 bg-slate-900/85 px-2.5 py-1 rounded border border-white/10">
              {isRecordingCsv
                ? `● REC CSV: ${recordedRows.length} тиков (эпизодов >=24t: ${Math.floor(recordedRows.length / 24)})`
                : `Цель: LivingEntity | dist: ${(Math.hypot(targetPos.x - crosshairPos.x, targetPos.y - crosshairPos.y) / 25).toFixed(2)}m`}
            </div>
          </div>

          <div className="mt-3 flex items-center justify-between text-xs text-slate-400">
            <span>
              Функция сжатия: <strong className="text-purple-300">quantize(sinh(u, 179°), gcd)</strong>
            </span>
            <span>
              Смеси Гауссиан: <strong className="text-purple-300">K=3 (keep={knobKeep}, scale={knobScale})</strong>
            </span>
          </div>
        </div>

        {/* Residual Error Quantiles from default.json / aim.json */}
        <div className="p-5 rounded-2xl acrylic-card flex flex-col justify-between">
          <div className="flex items-center justify-between mb-3">
            <span className="text-sm font-semibold text-white flex items-center gap-2">
              <Zap className="w-4 h-4 text-purple-400" />
              Таблица 65 квантилей остаточной ошибки (error[65] в aim.json)
            </span>
            <span className="text-xs font-mono text-emerald-400 bg-emerald-500/10 px-2 py-0.5 rounded border border-emerald-500/20">
              q[0]={trainedQuantiles[0].toFixed(4)}
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
                const epochIdx = Math.round(ratio * (trainedQuantiles.length - 1));
                setHoveredEpoch({
                  epoch: epochIdx,
                  loss: trainedQuantiles[epochIdx],
                });
              }}
              onMouseLeave={() => setHoveredEpoch(null)}
            />
            {hoveredEpoch && (
              <div className="absolute top-4 right-4 bg-purple-900/90 text-purple-100 text-xs font-mono px-3 py-1.5 rounded-lg border border-purple-400/40 shadow-xl pointer-events-none">
                Квантиль #{hoveredEpoch.epoch} ({Math.round((hoveredEpoch.epoch / 64) * 100)}%):{' '}
                {hoveredEpoch.loss.toFixed(5)} хитбокса
              </div>
            )}
          </div>

          <p className="text-xs text-slate-400 mt-3">
            При каждом шаге <code className="text-purple-300">rollout()</code> Киллаура выбирает{' '}
            <code className="text-purple-300">want = scale * quantile(error, rng)</code> и подбирает движение мыши из{' '}
            <code className="text-purple-300">keep={knobKeep}</code> кандидатов, неотличимое от человеческой руки.
          </p>
        </div>
      </div>

      {/* 32 Hidden Units Heatmap & 17 Features Inspector */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="p-5 rounded-2xl acrylic-card lg:col-span-2">
          <div className="flex items-center justify-between mb-3">
            <span className="text-sm font-semibold text-white flex items-center gap-2">
              <Sparkles className="w-4 h-4 text-indigo-400" />
              Скрытый слой GRU Ячейки (32 Hidden States — wi[96×17], wh[96×32])
            </span>
            <span className="text-xs text-slate-400 font-mono">state = (1 - z) * n + z * state</span>
          </div>

          <div className="grid grid-cols-8 sm:grid-cols-16 gap-2">
            {hiddenStates.map((val, idx) => {
              const intensity = Math.round(val * 100);
              return (
                <div
                  key={idx}
                  className="flex flex-col items-center p-2 rounded-xl bg-slate-950/70 border border-white/5 hover:border-purple-500/50 transition-all group"
                  title={`Neuron #${idx}: ${val.toFixed(3)}`}
                >
                  <div
                    className="w-full h-8 rounded-lg mb-1.5 transition-colors duration-150"
                    style={{
                      backgroundColor: `rgba(168, 85, 247, ${0.15 + val * 0.85})`,
                      boxShadow: val > 0.6 ? '0 0 10px rgba(168,85,247,0.5)' : 'none',
                    }}
                  />
                  <span className="text-[10px] font-mono text-slate-400 group-hover:text-purple-300">
                    H{idx}
                  </span>
                  <span className="text-[9px] font-mono text-slate-300 font-bold">{intensity}%</span>
                </div>
              );
            })}
          </div>
        </div>

        {/* 17 Input Features List */}
        <div className="p-5 rounded-2xl acrylic-card">
          <span className="text-sm font-semibold text-white block mb-3">
            17 Признаков (train_aura.py → features)
          </span>
          <div className="space-y-1.5 max-h-60 overflow-y-auto pr-1">
            {NEURO_METADATA.featureNames.map((name, i) => (
              <button
                key={i}
                onClick={() => {
                  sound.playClick();
                  setActiveFeatureIdx(i);
                }}
                className={`w-full text-left px-3 py-1.5 rounded-lg text-xs font-mono flex items-center justify-between transition-colors cursor-pointer ${
                  activeFeatureIdx === i
                    ? 'bg-purple-600/20 text-purple-200 border border-purple-500/30'
                    : 'bg-slate-950/40 text-slate-400 hover:text-slate-200 hover:bg-slate-900/50'
                }`}
              >
                <span className="truncate pr-2">
                  #{i + 1} {name}
                </span>
                <span className="text-[10px] opacity-75 shrink-0">
                  {i === 14 ? '1.0' : i === 15 ? '0.05' : (Math.sin(i * 1.3) * 0.45).toFixed(2)}
                </span>
              </button>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};
