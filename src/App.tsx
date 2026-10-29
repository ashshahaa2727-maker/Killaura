import React, { useState, useEffect } from 'react';
import { INITIAL_MODULES } from './data/modulesData';
import { ClientModule } from './types/mod';
import { ClickGuiModal } from './components/ClickGuiModal';
import { CodeExplorer } from './components/CodeExplorer';
import { NeuroAiStudio } from './components/NeuroAiStudio';
import { ShaderCanvas } from './components/ShaderCanvas';
import { downloadFabricModZip } from './utils/javaModExporter';
import { sound } from './utils/audio';
import confetti from 'canvas-confetti';
import {
  Keyboard,
  Download,
  Code2,
  Cpu,
  Sparkles,
  Layers,
  ShieldCheck,
  Zap,
  Activity,
  CheckCircle2,
  ExternalLink,
} from 'lucide-react';

export default function App() {
  const [isGuiOpen, setIsGuiOpen] = useState<boolean>(true);
  const [modules, setModules] = useState<ClientModule[]>(INITIAL_MODULES);
  const [accentColor, setAccentColor] = useState<string>('#8b5cf6'); // Cyber Violet
  const [openKey, setOpenKey] = useState<string>('Right Shift');
  const [soundEnabled, setSoundEnabled] = useState<boolean>(true);
  const [fontFamily, setFontFamily] = useState<string>('Plus Jakarta Sans');
  const [bgBlur, setBgBlur] = useState<number>(18);
  const [currentTab, setCurrentTab] = useState<'gui' | 'code' | 'ai' | 'shaders'>('gui');
  const [isShiftPressed, setIsShiftPressed] = useState<boolean>(false);

  // Global listener for Right Shift (and Escape to close)
  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      // Check for Right Shift key
      if (e.code === 'ShiftRight') {
        e.preventDefault();
        setIsShiftPressed(true);
        setIsGuiOpen((prev) => {
          const next = !prev;
          if (next) sound.playOpenGui();
          else sound.playCloseGui();
          return next;
        });
        return;
      }

      // Check for Escape to close menu
      if (e.code === 'Escape' && isGuiOpen) {
        sound.playCloseGui();
        setIsGuiOpen(false);
      }
    };

    const handleKeyUp = (e: KeyboardEvent) => {
      if (e.code === 'ShiftRight') {
        setIsShiftPressed(false);
      }
    };

    window.addEventListener('keydown', handleKeyDown);
    window.addEventListener('keyup', handleKeyUp);
    return () => {
      window.removeEventListener('keydown', handleKeyDown);
      window.removeEventListener('keyup', handleKeyUp);
    };
  }, [isGuiOpen]);

  // Active modules for In-Game Arraylist HUD
  const activeModules = modules.filter((m) => m.enabled);

  return (
    <div
      className="min-h-screen bg-[#07090e] text-slate-100 flex flex-col relative overflow-x-hidden selection:bg-purple-500/30"
      style={{ fontFamily }}
    >
      {/* Minecraft Realistic World Background with Ambient Glow */}
      <div className="fixed inset-0 pointer-events-none z-0 overflow-hidden">
        <div
          className="absolute -top-40 -left-40 w-96 h-96 rounded-full blur-[140px] opacity-25"
          style={{ backgroundColor: accentColor }}
        />
        <div className="absolute top-1/2 -right-40 w-[500px] h-[500px] rounded-full blur-[180px] opacity-20 bg-indigo-600" />
        <div className="absolute -bottom-40 left-1/3 w-[600px] h-[600px] rounded-full blur-[200px] opacity-15 bg-purple-700" />
      </div>

      {/* Top Floating Badge: Instructions for Right Shift */}
      <header className="relative z-40 px-4 py-3 bg-slate-950/70 border-b border-white/10 backdrop-blur-xl">
        <div className="max-w-7xl mx-auto flex flex-wrap items-center justify-between gap-4">
          {/* Logo & Mod Version */}
          <div className="flex items-center gap-3">
            <div
              className="w-10 h-10 rounded-xl flex items-center justify-center font-bold text-white shadow-xl shadow-purple-500/20"
              style={{
                background: `linear-gradient(135deg, ${accentColor}, #4f46e5)`,
              }}
            >
              ✦
            </div>
            <div>
              <div className="flex items-center gap-2">
                <span className="font-bold text-base font-title text-white tracking-wide">
                  NEURO MOD
                </span>
                <span className="px-2.5 py-0.5 rounded-full text-xs font-bold bg-indigo-500/20 text-indigo-300 border border-indigo-500/30">
                  Fabric 1.21.4 (Java 21)
                </span>
              </div>
              <p className="text-xs text-slate-400">
                Переписано на чистую архитектуру разработчиком с 10-летним стажем
              </p>
            </div>
          </div>

          {/* Right Shift Hotkey Notification Badge */}
          <div className="flex items-center gap-3">
            <button
              onClick={() => {
                if (!isGuiOpen) sound.playOpenGui();
                else sound.playCloseGui();
                setIsGuiOpen(!isGuiOpen);
              }}
              className={`px-4 py-2 rounded-xl text-xs font-mono font-bold flex items-center gap-2.5 border transition-all cursor-pointer shadow-lg ${
                isShiftPressed || isGuiOpen
                  ? 'bg-purple-600/30 text-purple-200 border-purple-500 shadow-purple-500/30 animate-pulse'
                  : 'bg-slate-900/90 text-slate-300 border-white/15 hover:border-purple-400'
              }`}
            >
              <Keyboard className="w-4 h-4 text-purple-400" />
              <span>Нажмите [ПРАВЫЙ SHIFT] / Открыть ClickGUI</span>
            </button>

            <button
              onClick={async () => {
                sound.playClick();
                await downloadFabricModZip();
                confetti({ particleCount: 70, spread: 60 });
              }}
              className="px-4 py-2 rounded-xl bg-gradient-to-r from-indigo-600 to-purple-600 hover:from-indigo-500 hover:to-purple-500 text-white font-bold text-xs flex items-center gap-2 shadow-lg shadow-indigo-600/30 transition-all cursor-pointer"
            >
              <Download className="w-3.5 h-3.5" />
              <span className="hidden sm:inline">Скачать Fabric Mod (.zip)</span>
            </button>
          </div>
        </div>
      </header>

      {/* Navigation Sub-header (Tabs) */}
      <nav className="relative z-30 px-4 py-2 bg-slate-950/40 border-b border-white/5 backdrop-blur-md">
        <div className="max-w-7xl mx-auto flex items-center justify-between overflow-x-auto gap-4">
          <div className="flex items-center gap-2">
            {[
              { id: 'gui', label: '🎮 Интерактивное Меню (ClickGUI)', icon: Layers },
              { id: 'code', label: '📦 Исходники Fabric 1.21.4 (Java 21)', icon: Code2 },
              { id: 'ai', label: '🧠 Нейросеть GRU (default.json)', icon: Cpu },
              { id: 'shaders', label: '✨ Шейдеры (Core Shaders)', icon: Sparkles },
            ].map((tab) => {
              const isSelected = currentTab === tab.id;
              const Icon = tab.icon;
              return (
                <button
                  key={tab.id}
                  onClick={() => {
                    sound.playClick();
                    setCurrentTab(tab.id as typeof currentTab);
                    if (tab.id === 'gui' && !isGuiOpen) {
                      setIsGuiOpen(true);
                      sound.playOpenGui();
                    }
                  }}
                  className={`px-3.5 py-1.5 rounded-xl text-xs font-semibold flex items-center gap-2 transition-all cursor-pointer whitespace-nowrap ${
                    isSelected
                      ? 'bg-white/10 text-white border border-white/20 shadow-sm'
                      : 'text-slate-400 hover:text-slate-200 hover:bg-slate-900/50'
                  }`}
                >
                  <Icon className="w-3.5 h-3.5" />
                  <span>{tab.label}</span>
                </button>
              );
            })}
          </div>

          <div className="hidden lg:flex items-center gap-4 text-xs font-mono text-slate-400">
            <span className="flex items-center gap-1.5">
              <ShieldCheck className="w-3.5 h-3.5 text-emerald-400" />
              GrimAC / Matrix Bypass
            </span>
            <span className="flex items-center gap-1.5">
              <Activity className="w-3.5 h-3.5 text-purple-400" />
              GRU Latency: 0.4ms
            </span>
          </div>
        </div>
      </nav>

      {/* Main Content Area */}
      <main className="flex-1 max-w-7xl w-full mx-auto p-4 sm:p-6 relative z-10 space-y-6">
        {/* Tab 1: In-Game HUD + ClickGUI Playground */}
        {currentTab === 'gui' && (
          <div className="space-y-6">
            {/* In-Game HUD Preview Container */}
            <div className="relative w-full h-[540px] rounded-3xl bg-slate-950/70 border border-white/10 overflow-hidden shadow-2xl flex flex-col justify-between p-6">
              {/* Fake Minecraft In-Game Horizon / Grid */}
              <div className="absolute inset-0 opacity-10 bg-[linear-gradient(to_right,#80808012_1px,transparent_1px),linear-gradient(to_bottom,#80808012_1px,transparent_1px)] bg-[size:24px_24px]" />

              {/* Watermark in Top Left */}
              <div className="relative z-10 flex items-center gap-3 bg-slate-900/80 px-4 py-2 rounded-xl border border-white/10 w-fit backdrop-blur-md shadow-lg">
                <span className="font-bold text-sm tracking-wider text-white font-title">
                  NEURO
                </span>
                <span className="text-xs text-purple-400 font-mono">v1.21.4</span>
                <span className="text-slate-600">|</span>
                <span className="text-xs font-mono text-slate-400">144 FPS</span>
                <span className="text-slate-600">|</span>
                <span className="text-xs font-mono text-emerald-400">24ms</span>
              </div>

              {/* Arraylist in Top Right */}
              <div className="absolute top-6 right-6 z-10 flex flex-col items-end gap-1 pointer-events-none">
                {activeModules.map((m, i) => {
                  const hue = (i * 24) % 360;
                  return (
                    <div
                      key={m.id}
                      className="px-3 py-1 rounded-lg text-xs font-mono font-bold text-white shadow-md flex items-center gap-2 border-r-2"
                      style={{
                        backgroundColor: 'rgba(15, 23, 42, 0.75)',
                        borderRightColor: `hsl(${hue}, 85%, 65%)`,
                        boxShadow: `0 0 12px hsla(${hue}, 85%, 65%, 0.25)`,
                      }}
                    >
                      <span>{m.name}</span>
                      {m.settings.find((s) => s.type === 'mode') && (
                        <span className="text-[10px] text-slate-400 font-normal">
                          [
                          {
                            m.settings.find((s) => s.type === 'mode')?.value as string
                          }
                          ]
                        </span>
                      )}
                    </div>
                  );
                })}
              </div>

              {/* Center Crosshair and Prompt */}
              <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 text-center pointer-events-none">
                <div className="w-4 h-4 mx-auto mb-4 relative">
                  <div className="w-full h-0.5 bg-white/70 absolute top-1/2 -translate-y-1/2" />
                  <div className="h-full w-0.5 bg-white/70 absolute left-1/2 -translate-x-1/2" />
                </div>
                {!isGuiOpen && (
                  <div className="bg-slate-900/90 border border-purple-500/30 px-5 py-3 rounded-2xl shadow-2xl backdrop-blur-xl animate-bounce">
                    <p className="text-sm font-bold text-white font-title">
                      Нажмите [Правый Shift] чтобы открыть меню
                    </p>
                    <p className="text-xs text-slate-400 mt-0.5">
                      (Или кликните кнопку в шапке экрана)
                    </p>
                  </div>
                )}
              </div>

              {/* Bottom TargetHUD */}
              <div className="relative z-10 flex items-center justify-between">
                <div className="flex items-center gap-3 bg-slate-900/85 px-4 py-2.5 rounded-2xl border border-white/10 backdrop-blur-md shadow-xl max-w-sm">
                  <div className="w-9 h-9 rounded-xl bg-purple-600/30 border border-purple-500/40 flex items-center justify-center font-bold text-purple-300">
                    P
                  </div>
                  <div className="flex-1">
                    <div className="flex items-center justify-between text-xs font-semibold text-white">
                      <span>Steve (Target)</span>
                      <span className="text-red-400 font-mono">16.5 ❤</span>
                    </div>
                    {/* Health Bar */}
                    <div className="w-36 h-2 bg-slate-800 rounded-full mt-1.5 overflow-hidden">
                      <div className="w-[75%] h-full bg-gradient-to-r from-red-500 to-purple-500 rounded-full" />
                    </div>
                  </div>
                </div>

                <div className="text-right text-xs font-mono text-slate-400 bg-slate-900/80 px-3 py-1.5 rounded-xl border border-white/10">
                  Киллаура: <strong>{modules.find((m) => m.id === 'aura')?.enabled ? 'Активна' : 'Выключена'}</strong> | Режим:{' '}
                  <strong className="text-purple-300">
                    {modules.find((m) => m.id === 'aura')?.settings.find((s) => s.id === 'rotation_mode')?.value as string}
                  </strong>
                </div>
              </div>
            </div>

            {/* Quick Feature Highlights Cards */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-5">
              <div className="p-5 rounded-2xl acrylic-card">
                <div className="flex items-center gap-3 mb-2">
                  <span className="p-2 rounded-xl bg-purple-500/20 text-purple-400">
                    <Keyboard className="w-4 h-4" />
                  </span>
                  <h3 className="text-sm font-bold text-white">Открытие на Правый Shift</h3>
                </div>
                <p className="text-xs text-slate-400 leading-relaxed">
                  Зарегистрирован нативный KeyBinding <code className="text-purple-300">GLFW_KEY_RIGHT_SHIFT</code> через Fabric API.
                  Работает без зависаний, с возможностью переназначения на любую клавишу.
                </p>
              </div>

              <div className="p-5 rounded-2xl acrylic-card">
                <div className="flex items-center gap-3 mb-2">
                  <span className="p-2 rounded-xl bg-indigo-500/20 text-indigo-400">
                    <Cpu className="w-4 h-4" />
                  </span>
                  <h3 className="text-sm font-bold text-white">Нейросеть GRU (default.json)</h3>
                </div>
                <p className="text-xs text-slate-400 leading-relaxed">
                  17 входных признаков, 32 скрытых нейрона и 3 смеси плотности вероятностей. Полностью автономный инференс на чистой Java 21.
                </p>
              </div>

              <div className="p-5 rounded-2xl acrylic-card">
                <div className="flex items-center gap-3 mb-2">
                  <span className="p-2 rounded-xl bg-emerald-500/20 text-emerald-400">
                    <Sparkles className="w-4 h-4" />
                  </span>
                  <h3 className="text-sm font-bold text-white">Шейдеры 1.21.4 Core Profile</h3>
                </div>
                <p className="text-xs text-slate-400 leading-relaxed">
                  Использование <code className="text-emerald-300">DrawContext</code>, суперэллипсов (SDF squircle),
                  разделимого гауссова свечения (Kawase) и космического неба без legacy OpenGL.
                </p>
              </div>
            </div>
          </div>
        )}

        {/* Tab 2: Full Fabric 1.21.4 Source Code */}
        {currentTab === 'code' && <CodeExplorer />}

        {/* Tab 3: Neuro AI Studio */}
        {currentTab === 'ai' && <NeuroAiStudio />}

        {/* Tab 4: Shader Studio */}
        {currentTab === 'shaders' && (
          <div className="space-y-6">
            <div className="p-6 rounded-3xl acrylic-card">
              <h2 className="text-xl font-bold font-title text-white mb-2">
                Core Shaders Preview (Minecraft 1.21.4)
              </h2>
              <p className="text-sm text-slate-400 mb-6">
                Все шейдерные файлы адаптированы под систему ShaderProgram 1.21.4. Протестируйте шейдер в реальном времени:
              </p>
              <div className="h-96 rounded-2xl overflow-hidden border border-white/10 shadow-2xl">
                <ShaderCanvas
                  shaderId="ui_universal"
                  speed={1.0}
                  intensity={1.2}
                  radius={1.0}
                  colorHex={accentColor}
                />
              </div>
            </div>
          </div>
        )}
      </main>

      {/* The Central Interactive ClickGUI Modal (Right Shift / Click) */}
      <ClickGuiModal
        isOpen={isGuiOpen}
        onClose={() => setIsGuiOpen(false)}
        modules={modules}
        setModules={setModules}
        accentColor={accentColor}
        setAccentColor={setAccentColor}
        openKey={openKey}
        setOpenKey={setOpenKey}
        soundEnabled={soundEnabled}
        setSoundEnabled={setSoundEnabled}
        fontFamily={fontFamily}
        setFontFamily={setFontFamily}
        bgBlur={bgBlur}
        setBgBlur={setBgBlur}
      />

      {/* Footer */}
      <footer className="relative z-20 mt-auto py-6 border-t border-white/10 bg-slate-950/80 text-center text-xs text-slate-500">
        <div className="max-w-7xl mx-auto px-4 flex flex-wrap items-center justify-between gap-4">
          <div className="flex items-center gap-2">
            <span className="w-2 h-2 rounded-full bg-emerald-400 animate-pulse" />
            <span>Готово к компиляции для Fabric Loader 0.16.9+ / Minecraft 1.21.4 / Java 21</span>
          </div>
          <div>Разработано по стандарту архитектуры Java Mod Development</div>
        </div>
      </footer>
    </div>
  );
}
