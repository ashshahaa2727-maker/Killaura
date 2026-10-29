import React, { useState, useMemo } from 'react';
import {
  CATEGORIES,
  CONFIG_PRESETS,
  SHADERS_LIST,
} from '../data/modulesData';
import { ClientModule, ModuleCategory } from '../types/mod';
import { sound } from '../utils/audio';
import { ModuleSettingsDrawer } from './ModuleSettingsDrawer';
import { NeuroAiStudio } from './NeuroAiStudio';
import { ShaderCanvas } from './ShaderCanvas';
import { CodeExplorer } from './CodeExplorer';
import {
  Swords,
  Footprints,
  Eye,
  User,
  Globe,
  Cpu,
  Sparkles,
  FolderHeart,
  Sliders,
  Search,
  X,
  Code2,
  ChevronDown,
  ChevronUp,
  Download,
  Key,
  Flame,
  Volume2,
  VolumeX,
} from 'lucide-react';
import { downloadFabricModZip } from '../utils/javaModExporter';
import confetti from 'canvas-confetti';

interface ClickGuiModalProps {
  isOpen: boolean;
  onClose: () => void;
  modules: ClientModule[];
  setModules: React.Dispatch<React.SetStateAction<ClientModule[]>>;
  accentColor: string;
  setAccentColor: (color: string) => void;
  openKey: string;
  setOpenKey: (key: string) => void;
  soundEnabled: boolean;
  setSoundEnabled: (enabled: boolean) => void;
  fontFamily: string;
  setFontFamily: (font: string) => void;
  bgBlur: number;
  setBgBlur: (blur: number) => void;
}

export const ClickGuiModal: React.FC<ClickGuiModalProps> = ({
  isOpen,
  onClose,
  modules,
  setModules,
  accentColor,
  setAccentColor,
  openKey,
  setOpenKey,
  soundEnabled,
  setSoundEnabled,
  fontFamily,
  setFontFamily,
  bgBlur,
  setBgBlur,
}) => {
  const [activeCategory, setActiveCategory] = useState<ModuleCategory>('COMBAT');
  const [searchQuery, setSearchQuery] = useState<string>('');
  const [expandedModuleId, setExpandedModuleId] = useState<string | null>('aura');
  const [rebindingModuleId, setRebindingModuleId] = useState<string | null>(null);
  const [rebindingOpenKey, setRebindingOpenKey] = useState<boolean>(false);
  const [activeShaderId, setActiveShaderId] = useState<string>('ui_universal');
  const [shaderSpeed, setShaderSpeed] = useState<number>(1.0);
  const [shaderIntensity, setShaderIntensity] = useState<number>(1.2);
  const [shaderRadius, setShaderRadius] = useState<number>(1.0);

  // Quick preset loading
  const handleApplyPreset = (presetId: string) => {
    sound.playClick(750, 0.08);
    const preset = CONFIG_PRESETS.find((p) => p.id === presetId);
    if (!preset) return;

    setModules((prev) =>
      prev.map((mod) => {
        const presetMod = preset.modules[mod.id];
        if (!presetMod) return mod;

        const updatedSettings = mod.settings.map((s) => {
          if (presetMod.settings && presetMod.settings[s.id] !== undefined) {
            return { ...s, value: presetMod.settings[s.id] } as typeof s;
          }
          return s;
        });

        return {
          ...mod,
          enabled: presetMod.enabled,
          keybind: presetMod.keybind || mod.keybind,
          settings: updatedSettings,
        };
      })
    );

    confetti({
      particleCount: 50,
      spread: 60,
      origin: { y: 0.4 },
    });
  };

  // Toggle module on/off
  const handleToggleModule = (modId: string) => {
    setModules((prev) =>
      prev.map((mod) => {
        if (mod.id === modId) {
          const nextState = !mod.enabled;
          sound.playToggle(nextState);
          return { ...mod, enabled: nextState };
        }
        return mod;
      })
    );
  };

  // Update a specific setting of a module
  const handleUpdateSetting = (modId: string, settingId: string, newValue: unknown) => {
    setModules((prev) =>
      prev.map((mod) => {
        if (mod.id === modId) {
          return {
            ...mod,
            settings: mod.settings.map((s) =>
              s.id === settingId ? ({ ...s, value: newValue } as typeof s) : s
            ),
          };
        }
        return mod;
      })
    );
  };

  // Filtered modules
  const filteredModules = useMemo(() => {
    return modules.filter((m) => {
      const matchesCategory = m.category === activeCategory;
      const q = searchQuery.toLowerCase().trim();
      if (!q) return matchesCategory;

      const matchesQuery =
        m.name.toLowerCase().includes(q) ||
        (m.nameRu && m.nameRu.toLowerCase().includes(q)) ||
        m.description.toLowerCase().includes(q);

      return matchesQuery;
    });
  }, [modules, activeCategory, searchQuery]);

  // Key listener when rebinding a module or open key
  React.useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (rebindingOpenKey) {
        e.preventDefault();
        sound.playClick(900, 0.05);
        const keyName =
          e.code === 'ShiftRight'
            ? 'Right Shift'
            : e.code === 'ShiftLeft'
            ? 'Left Shift'
            : e.code.replace('Key', '').replace('Digit', '');
        setOpenKey(keyName);
        setRebindingOpenKey(false);
        return;
      }

      if (rebindingModuleId) {
        e.preventDefault();
        sound.playClick(900, 0.05);
        const keyName =
          e.code === 'Escape'
            ? 'NONE'
            : e.code === 'ShiftRight'
            ? 'RSHIFT'
            : e.code.replace('Key', '').replace('Digit', '');

        setModules((prev) =>
          prev.map((mod) => (mod.id === rebindingModuleId ? { ...mod, keybind: keyName } : mod))
        );
        setRebindingModuleId(null);
      }
    };

    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [rebindingModuleId, rebindingOpenKey, setModules, setOpenKey]);

  if (!isOpen) return null;

  return (
    <div
      className="fixed inset-0 z-50 flex items-center justify-center p-3 sm:p-6 overflow-y-auto"
      style={{
        backdropFilter: `blur(${bgBlur}px)`,
        WebkitBackdropFilter: `blur(${bgBlur}px)`,
        backgroundColor: 'rgba(5, 7, 14, 0.72)',
      }}
    >
      {/* ClickGUI Window */}
      <div
        className="w-full max-w-5xl rounded-3xl acrylic-popup overflow-hidden flex flex-col border border-white/15 shadow-2xl relative animate-in zoom-in-95 duration-200"
        style={{
          boxShadow: `0 0 50px ${accentColor}25, 0 25px 60px rgba(0,0,0,0.8)`,
        }}
      >
        {/* Header Bar */}
        <div className="flex flex-wrap items-center justify-between gap-3 px-6 py-4 border-b border-white/10 bg-slate-950/70">
          <div className="flex items-center gap-3">
            <div
              className="w-9 h-9 rounded-xl flex items-center justify-center font-bold text-white shadow-lg"
              style={{
                background: `linear-gradient(135deg, ${accentColor}, #4f46e5)`,
                boxShadow: `0 0 20px ${accentColor}60`,
              }}
            >
              ✦
            </div>
            <div>
              <div className="flex items-center gap-2">
                <h1 className="text-base font-bold font-title tracking-wider text-white">
                  NEURO CLIENT
                </h1>
                <span className="px-2 py-0.5 rounded-full text-[10px] font-bold bg-indigo-500/20 text-indigo-300 border border-indigo-500/30">
                  Fabric 1.21.4
                </span>
              </div>
              <span className="text-[11px] text-slate-400">
                Открыто на: <strong className="text-indigo-400">[{openKey}]</strong>
              </span>
            </div>
          </div>

          {/* Search Bar */}
          <div className="relative flex-1 max-w-xs mx-2">
            <Search className="w-3.5 h-3.5 text-slate-400 absolute left-3 top-1/2 -translate-y-1/2" />
            <input
              type="text"
              placeholder="Поиск модулей (Aura, Speed...)"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="w-full bg-slate-900/90 border border-white/10 rounded-xl pl-8 pr-3 py-1.5 text-xs text-white placeholder-slate-500 focus:outline-none focus:border-indigo-500 transition-colors"
            />
            {searchQuery && (
              <button
                onClick={() => setSearchQuery('')}
                className="absolute right-2.5 top-1/2 -translate-y-1/2 text-slate-500 hover:text-white"
              >
                <X className="w-3 h-3" />
              </button>
            )}
          </div>

          {/* Quick Actions */}
          <div className="flex items-center gap-2">
            <button
              onClick={() => {
                sound.playClick();
                setSoundEnabled(!soundEnabled);
                sound.enabled = !soundEnabled;
              }}
              title={soundEnabled ? 'Звуковые клики вкл' : 'Звуковые клики выкл'}
              className="p-2 rounded-xl bg-slate-900/80 hover:bg-slate-800 text-slate-300 border border-white/10 transition-colors cursor-pointer"
            >
              {soundEnabled ? <Volume2 className="w-4 h-4 text-emerald-400" /> : <VolumeX className="w-4 h-4 text-slate-500" />}
            </button>

            <button
              onClick={async () => {
                sound.playClick();
                await downloadFabricModZip();
                confetti({ particleCount: 70, spread: 60 });
              }}
              className="px-3 py-1.5 rounded-xl bg-indigo-600/20 hover:bg-indigo-600/40 text-indigo-200 border border-indigo-500/30 text-xs font-semibold flex items-center gap-1.5 transition-colors cursor-pointer"
            >
              <Download className="w-3.5 h-3.5" />
              <span>Скачать Mod .zip</span>
            </button>

            <button
              onClick={() => {
                sound.playCloseGui();
                onClose();
              }}
              className="p-2 rounded-xl bg-slate-900/80 hover:bg-red-500/20 hover:text-red-300 text-slate-400 border border-white/10 transition-colors cursor-pointer"
              title="Закрыть меню (ESC / Right Shift)"
            >
              <X className="w-4 h-4" />
            </button>
          </div>
        </div>

        {/* Preset Quick Bar */}
        <div className="px-6 py-2 bg-slate-950/40 border-b border-white/5 flex items-center justify-between text-xs text-slate-400 overflow-x-auto gap-4">
          <div className="flex items-center gap-2 shrink-0">
            <Flame className="w-3.5 h-3.5 text-amber-400" />
            <span className="font-semibold text-slate-300">Быстрые конфиги серверов:</span>
          </div>

          <div className="flex items-center gap-2 shrink-0">
            {CONFIG_PRESETS.map((p) => (
              <button
                key={p.id}
                onClick={() => handleApplyPreset(p.id)}
                className="px-2.5 py-1 rounded-lg text-[11px] bg-slate-900/80 hover:bg-slate-800 text-slate-300 hover:text-white border border-white/5 transition-colors cursor-pointer"
              >
                {p.name.split(' (')[0]}
              </button>
            ))}
          </div>
        </div>

        {/* Main Body (Sidebar + Content) */}
        <div className="flex flex-col md:flex-row flex-1 min-h-[520px] max-h-[72vh]">
          {/* Categories Sidebar */}
          <div className="w-full md:w-56 p-4 border-r border-white/10 bg-slate-950/60 flex flex-row md:flex-col gap-1.5 overflow-x-auto md:overflow-y-auto shrink-0">
            <span className="hidden md:block text-[10px] font-bold text-slate-500 uppercase tracking-wider px-3 mb-1">
              Категории мода
            </span>

            {CATEGORIES.map((cat) => {
              const isSelected = activeCategory === cat.id;
              const IconComp =
                cat.id === 'COMBAT'
                  ? Swords
                  : cat.id === 'MOVEMENT'
                  ? Footprints
                  : cat.id === 'RENDER'
                  ? Eye
                  : cat.id === 'PLAYER'
                  ? User
                  : cat.id === 'WORLD'
                  ? Globe
                  : cat.id === 'NEURO_AI'
                  ? Cpu
                  : cat.id === 'SHADER_STUDIO'
                  ? Sparkles
                  : cat.id === 'CONFIGS'
                  ? FolderHeart
                  : Sliders;

              return (
                <button
                  key={cat.id}
                  onClick={() => {
                    sound.playClick();
                    setActiveCategory(cat.id);
                  }}
                  className={`px-3 py-2.5 rounded-xl text-xs font-semibold flex items-center gap-3 transition-all cursor-pointer whitespace-nowrap ${
                    isSelected
                      ? 'text-white shadow-md'
                      : 'text-slate-400 hover:text-slate-200 hover:bg-slate-900/60'
                  }`}
                  style={
                    isSelected
                      ? {
                          backgroundColor: `${accentColor}25`,
                          border: `1px solid ${accentColor}50`,
                          color: '#ffffff',
                        }
                      : {}
                  }
                >
                  <IconComp
                    className="w-4 h-4 shrink-0"
                    style={{ color: isSelected ? accentColor : undefined }}
                  />
                  <span>{cat.nameRu || cat.name}</span>
                </button>
              );
            })}

            <div className="hidden md:block mt-auto pt-4 border-t border-white/5 text-[11px] text-slate-500 px-2 space-y-1">
              <div>Minecraft: <strong>1.21.4</strong></div>
              <div>Fabric Loader: <strong>0.16.9</strong></div>
              <div>Модель: <strong>GRU Policy (v2)</strong></div>
            </div>
          </div>

          {/* Content Pane */}
          <div className="flex-1 p-6 overflow-y-auto bg-slate-950/30">
            {/* 1. If Neuro AI category is selected */}
            {activeCategory === 'NEURO_AI' && <NeuroAiStudio />}

            {/* 2. If Shader Studio category is selected */}
            {activeCategory === 'SHADER_STUDIO' && (
              <div className="space-y-6">
                <div className="flex flex-wrap items-center justify-between gap-4 p-5 rounded-2xl bg-gradient-to-r from-purple-950/40 to-slate-900 border border-purple-500/30">
                  <div>
                    <h2 className="text-xl font-bold font-title text-white">
                      Core Shaders 1.21.4 Studio
                    </h2>
                    <p className="text-sm text-slate-300 mt-1 max-w-xl">
                      Интерактивный WebGL просмотрщик шейдеров из мода (SDF сквирклы, акриловое стекло,
                      космические туманности и свечение).
                    </p>
                  </div>

                  <div className="flex items-center gap-2">
                    {SHADERS_LIST.map((s) => (
                      <button
                        key={s.id}
                        onClick={() => {
                          sound.playClick();
                          setActiveShaderId(s.id);
                        }}
                        className={`px-3 py-1.5 rounded-xl text-xs font-mono transition-all cursor-pointer ${
                          activeShaderId === s.id
                            ? 'bg-purple-600 text-white font-bold shadow-md shadow-purple-600/30'
                            : 'bg-slate-900 text-slate-400 hover:text-white'
                        }`}
                      >
                        {s.name.split(' ')[0]}
                      </button>
                    ))}
                  </div>
                </div>

                <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
                  <div className="lg:col-span-2 h-80 rounded-2xl overflow-hidden border border-white/10 shadow-xl bg-black">
                    <ShaderCanvas
                      shaderId={activeShaderId}
                      speed={shaderSpeed}
                      intensity={shaderIntensity}
                      radius={shaderRadius}
                      colorHex={accentColor}
                    />
                  </div>

                  <div className="p-5 rounded-2xl acrylic-card space-y-4">
                    <span className="text-sm font-bold text-white block">
                      Униформы шейдера (GLSL Uniforms)
                    </span>

                    <div className="space-y-3">
                      <div>
                        <div className="flex justify-between text-xs text-slate-300 mb-1">
                          <span>Скорость анимации (uTime)</span>
                          <span className="font-mono">{shaderSpeed.toFixed(1)}x</span>
                        </div>
                        <input
                          type="range"
                          min={0.2}
                          max={3.0}
                          step={0.1}
                          value={shaderSpeed}
                          onChange={(e) => setShaderSpeed(parseFloat(e.target.value))}
                          className="w-full accent-purple-500 cursor-pointer h-1.5 bg-slate-800 rounded-lg"
                        />
                      </div>

                      <div>
                        <div className="flex justify-between text-xs text-slate-300 mb-1">
                          <span>Интенсивность свечения (uIntensity)</span>
                          <span className="font-mono">{shaderIntensity.toFixed(1)}</span>
                        </div>
                        <input
                          type="range"
                          min={0.5}
                          max={2.5}
                          step={0.1}
                          value={shaderIntensity}
                          onChange={(e) => setShaderIntensity(parseFloat(e.target.value))}
                          className="w-full accent-purple-500 cursor-pointer h-1.5 bg-slate-800 rounded-lg"
                        />
                      </div>

                      <div>
                        <div className="flex justify-between text-xs text-slate-300 mb-1">
                          <span>Скругление углов (uRadius)</span>
                          <span className="font-mono">{shaderRadius.toFixed(1)}</span>
                        </div>
                        <input
                          type="range"
                          min={0.2}
                          max={2.0}
                          step={0.1}
                          value={shaderRadius}
                          onChange={(e) => setShaderRadius(parseFloat(e.target.value))}
                          className="w-full accent-purple-500 cursor-pointer h-1.5 bg-slate-800 rounded-lg"
                        />
                      </div>
                    </div>

                    <div className="pt-3 border-t border-white/10 text-xs text-slate-400">
                      Шейдер компилируется в игре через <code className="text-purple-300">ShaderProgram</code> с Core Profile 150.
                    </div>
                  </div>
                </div>
              </div>
            )}

            {/* 3. If Configs category is selected */}
            {activeCategory === 'CONFIGS' && (
              <div className="space-y-6">
                <div>
                  <h2 className="text-xl font-bold font-title text-white">
                    Управление конфигами (Profiles)
                  </h2>
                  <p className="text-sm text-slate-400 mt-1">
                    Готовые пресеты настроек под популярные серверы и защита от проверок.
                  </p>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-3 gap-5">
                  {CONFIG_PRESETS.map((p) => (
                    <div
                      key={p.id}
                      className="p-5 rounded-2xl acrylic-card flex flex-col justify-between space-y-4 hover:border-purple-500/40 transition-colors"
                    >
                      <div>
                        <div className="flex items-center justify-between mb-2">
                          <span className="text-xs font-mono text-purple-400 bg-purple-500/10 px-2 py-0.5 rounded">
                            {p.version}
                          </span>
                          <span className="text-[11px] text-slate-500">{p.createdAt}</span>
                        </div>
                        <h3 className="text-base font-bold text-white">{p.name}</h3>
                        <p className="text-xs text-slate-400 mt-2 leading-relaxed">
                          {p.description}
                        </p>
                        <div className="flex flex-wrap gap-1 mt-3">
                          {p.tags.map((t) => (
                            <span
                              key={t}
                              className="text-[10px] font-mono px-2 py-0.5 rounded bg-slate-900 text-slate-400"
                            >
                              #{t}
                            </span>
                          ))}
                        </div>
                      </div>

                      <button
                        onClick={() => handleApplyPreset(p.id)}
                        className="w-full py-2.5 rounded-xl bg-purple-600/30 hover:bg-purple-600/50 text-purple-200 border border-purple-500/40 text-xs font-bold transition-colors cursor-pointer"
                      >
                        Применить конфиг
                      </button>
                    </div>
                  ))}
                </div>
              </div>
            )}

            {/* 4. If Settings category is selected */}
            {activeCategory === 'SETTINGS' && (
              <div className="space-y-6 max-w-2xl">
                <div>
                  <h2 className="text-xl font-bold font-title text-white">
                    Настройки интерфейса и биндов
                  </h2>
                  <p className="text-sm text-slate-400 mt-1">
                    Персонализация клавиши открытия (Правый Shift), палитры и эффектов.
                  </p>
                </div>

                <div className="p-5 rounded-2xl acrylic-card space-y-5">
                  {/* Rebind ClickGUI Key */}
                  <div className="flex items-center justify-between">
                    <div>
                      <span className="text-sm font-semibold text-white block">
                        Клавиша открытия меню (ClickGUI Key)
                      </span>
                      <span className="text-xs text-slate-400">
                        По умолчанию установлен <strong>Правый Shift</strong>
                      </span>
                    </div>

                    <button
                      onClick={() => setRebindingOpenKey(true)}
                      className={`px-4 py-2 rounded-xl text-xs font-mono font-bold transition-all cursor-pointer ${
                        rebindingOpenKey
                          ? 'bg-amber-500/30 text-amber-300 border border-amber-500 animate-pulse'
                          : 'bg-indigo-600/30 text-indigo-200 border border-indigo-500/40 hover:bg-indigo-600/50'
                      }`}
                    >
                      {rebindingOpenKey ? 'Нажмите любую клавишу...' : `[${openKey}]`}
                    </button>
                  </div>

                  {/* Accent Color Palette */}
                  <div className="space-y-2 pt-3 border-t border-white/5">
                    <span className="text-xs font-semibold text-slate-300 block">
                      Акцентный цвет оформления
                    </span>
                    <div className="flex flex-wrap gap-3">
                      {[
                        { name: 'Cyber Violet', hex: '#8b5cf6' },
                        { name: 'Matrix Emerald', hex: '#10b981' },
                        { name: 'Neon Cyan', hex: '#06b6d4' },
                        { name: 'Sunset Amber', hex: '#f59e0b' },
                        { name: 'Sakura Pink', hex: '#ec4899' },
                      ].map((c) => (
                        <button
                          key={c.hex}
                          onClick={() => {
                            sound.playClick();
                            setAccentColor(c.hex);
                          }}
                          className={`flex items-center gap-2 px-3 py-1.5 rounded-xl text-xs font-semibold border transition-all cursor-pointer ${
                            accentColor === c.hex
                              ? 'border-white text-white shadow-lg'
                              : 'border-white/10 text-slate-400 hover:text-white'
                          }`}
                        >
                          <div
                            className="w-3.5 h-3.5 rounded-full shadow-sm"
                            style={{ backgroundColor: c.hex }}
                          />
                          <span>{c.name}</span>
                        </button>
                      ))}
                    </div>
                  </div>

                  {/* Background Blur */}
                  <div className="space-y-2 pt-3 border-t border-white/5">
                    <div className="flex justify-between text-xs text-slate-300">
                      <span>Сила акрилового размытия (Backdrop Blur)</span>
                      <span className="font-mono">{bgBlur}px</span>
                    </div>
                    <input
                      type="range"
                      min={0}
                      max={35}
                      step={2}
                      value={bgBlur}
                      onChange={(e) => setBgBlur(parseInt(e.target.value))}
                      className="w-full accent-indigo-500 cursor-pointer h-1.5 bg-slate-800 rounded-lg"
                    />
                  </div>

                  {/* Typography */}
                  <div className="space-y-2 pt-3 border-t border-white/5">
                    <span className="text-xs font-semibold text-slate-300 block">Шрифт интерфейса</span>
                    <div className="flex gap-2">
                      {['Plus Jakarta Sans', 'Fira Code', 'Unbounded'].map((font) => (
                        <button
                          key={font}
                          onClick={() => {
                            sound.playClick();
                            setFontFamily(font);
                          }}
                          className={`px-3 py-1.5 rounded-xl text-xs transition-all cursor-pointer ${
                            fontFamily === font
                              ? 'bg-indigo-600 text-white font-bold'
                              : 'bg-slate-900 text-slate-400 hover:text-white'
                          }`}
                        >
                          {font}
                        </button>
                      ))}
                    </div>
                  </div>
                </div>
              </div>
            )}

            {/* 5. Standard Module Categories (Combat, Movement, Render, Player, World) */}
            {['COMBAT', 'MOVEMENT', 'RENDER', 'PLAYER', 'WORLD'].includes(activeCategory) && (
              <div className="space-y-4">
                <div className="flex items-center justify-between pb-2">
                  <h2 className="text-lg font-bold font-title text-white">
                    {CATEGORIES.find((c) => c.id === activeCategory)?.nameRu || activeCategory}
                  </h2>
                  <span className="text-xs text-slate-400 font-mono">
                    {filteredModules.filter((m) => m.enabled).length} / {filteredModules.length} включено
                  </span>
                </div>

                {filteredModules.length === 0 ? (
                  <div className="text-center py-12 text-slate-500 text-sm">
                    Модули не найдены по запросу "{searchQuery}"
                  </div>
                ) : (
                  <div className="space-y-3">
                    {filteredModules.map((mod) => {
                      const isExpanded = expandedModuleId === mod.id;
                      const isRebinding = rebindingModuleId === mod.id;

                      return (
                        <div
                          key={mod.id}
                          className={`p-4 rounded-2xl transition-all duration-200 border ${
                            mod.enabled
                              ? 'acrylic-card-active'
                              : 'acrylic-card hover:border-white/15'
                          }`}
                        >
                          <div className="flex items-center justify-between gap-3">
                            {/* Module Title & Description */}
                            <div className="flex items-center gap-3">
                              <button
                                onClick={() => handleToggleModule(mod.id)}
                                className={`w-12 h-6 rounded-full transition-colors relative cursor-pointer shrink-0 ${
                                  mod.enabled ? 'bg-indigo-600 shadow-md shadow-indigo-600/40' : 'bg-slate-800'
                                }`}
                              >
                                <div
                                  className={`w-4 h-4 rounded-full bg-white absolute top-1 transition-transform ${
                                    mod.enabled ? 'left-7' : 'left-1'
                                  }`}
                                />
                              </button>

                              <div>
                                <div className="flex items-center gap-2">
                                  <h3 className="text-sm font-bold text-white tracking-wide">
                                    {mod.name}
                                  </h3>
                                  {mod.nameRu && (
                                    <span className="text-xs text-slate-400 font-medium">
                                      ({mod.nameRu})
                                    </span>
                                  )}
                                  {mod.id === 'aura' && (
                                    <span className="text-[10px] font-bold px-2 py-0.2 rounded-full bg-purple-500/20 text-purple-300 border border-purple-500/30">
                                      AI GRU
                                    </span>
                                  )}
                                </div>
                                <p className="text-xs text-slate-400 mt-0.5 line-clamp-1">
                                  {mod.description}
                                </p>
                              </div>
                            </div>

                            {/* Right Controls: Keybind and Settings Toggle */}
                            <div className="flex items-center gap-2 shrink-0">
                              <button
                                onClick={() => setRebindingModuleId(mod.id)}
                                title="Кликните чтобы привязать клавишу"
                                className={`px-2.5 py-1 rounded-lg text-[11px] font-mono transition-all cursor-pointer ${
                                  isRebinding
                                    ? 'bg-amber-500/20 text-amber-300 border border-amber-500 animate-pulse'
                                    : 'bg-slate-900/80 text-slate-400 hover:text-slate-200 hover:bg-slate-800 border border-white/5'
                                }`}
                              >
                                <span className="flex items-center gap-1">
                                  <Key className="w-3 h-3 opacity-60" />
                                  {isRebinding ? 'Нажмите...' : mod.keybind}
                                </span>
                              </button>

                              {mod.settings.length > 0 && (
                                <button
                                  onClick={() => {
                                    sound.playClick();
                                    setExpandedModuleId(isExpanded ? null : mod.id);
                                  }}
                                  className={`p-1.5 rounded-lg border transition-colors cursor-pointer ${
                                    isExpanded
                                      ? 'bg-indigo-600/30 border-indigo-500/40 text-indigo-300'
                                      : 'bg-slate-900/60 border-white/5 text-slate-400 hover:text-white'
                                  }`}
                                  title="Настройки модуля"
                                >
                                  {isExpanded ? (
                                    <ChevronUp className="w-4 h-4" />
                                  ) : (
                                    <ChevronDown className="w-4 h-4" />
                                  )}
                                </button>
                              )}
                            </div>
                          </div>

                          {/* Subsettings Drawer */}
                          {isExpanded && mod.settings.length > 0 && (
                            <ModuleSettingsDrawer
                              module={mod}
                              onUpdateSetting={(settingId, newVal) =>
                                handleUpdateSetting(mod.id, settingId, newVal)
                              }
                              accentColor={accentColor}
                            />
                          )}
                        </div>
                      );
                    })}
                  </div>
                )}
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};
