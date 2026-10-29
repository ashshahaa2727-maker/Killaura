import React from 'react';
import { ClientModule, ModuleSetting } from '../types/mod';
import { sound } from '../utils/audio';
import { Sliders, Palette, CheckSquare, Layers, Key } from 'lucide-react';

interface ModuleSettingsDrawerProps {
  module: ClientModule;
  onUpdateSetting: (settingId: string, newValue: unknown) => void;
  accentColor: string;
}

export const ModuleSettingsDrawer: React.FC<ModuleSettingsDrawerProps> = ({
  module,
  onUpdateSetting,
  accentColor,
}) => {
  return (
    <div className="mt-4 pt-4 border-t border-white/10 space-y-4 animate-in fade-in duration-200">
      <div className="flex items-center gap-2 text-xs font-semibold text-slate-400">
        <Sliders className="w-3.5 h-3.5 text-indigo-400" />
        <span>Настройки модуля: {module.nameRu || module.name}</span>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {module.settings.map((setting) => {
          return (
            <div
              key={setting.id}
              className="p-3.5 rounded-xl bg-slate-950/60 border border-white/5 space-y-2.5"
            >
              <div className="flex items-center justify-between">
                <span className="text-xs font-semibold text-slate-200">
                  {setting.nameRu || setting.name}
                </span>
                {setting.type === 'slider' && (
                  <span className="text-xs font-mono font-bold px-2 py-0.5 rounded bg-indigo-500/10 text-indigo-300 border border-indigo-500/20">
                    {setting.value} {setting.unit || ''}
                  </span>
                )}
              </div>

              {/* Slider Component */}
              {setting.type === 'slider' && (
                <div className="space-y-1">
                  <input
                    type="range"
                    min={setting.min}
                    max={setting.max}
                    step={setting.step}
                    value={setting.value}
                    onChange={(e) => {
                      sound.playSliderTick();
                      onUpdateSetting(setting.id, parseFloat(e.target.value));
                    }}
                    className="w-full accent-indigo-500 cursor-pointer h-1.5 bg-slate-800 rounded-lg appearance-none"
                  />
                  <div className="flex justify-between text-[10px] text-slate-500 font-mono">
                    <span>{setting.min}</span>
                    <span>{setting.max}</span>
                  </div>
                </div>
              )}

              {/* Mode Selector */}
              {setting.type === 'mode' && (
                <div className="flex flex-wrap gap-1.5">
                  {setting.modes.map((mode) => {
                    const isSelected = setting.value === mode;
                    return (
                      <button
                        key={mode}
                        onClick={() => {
                          sound.playClick();
                          onUpdateSetting(setting.id, mode);
                        }}
                        className={`px-2.5 py-1 rounded-lg text-[11px] font-mono transition-all cursor-pointer ${
                          isSelected
                            ? 'bg-indigo-600 text-white font-semibold shadow-md shadow-indigo-600/30'
                            : 'bg-slate-900 text-slate-400 hover:text-slate-200 hover:bg-slate-800'
                        }`}
                      >
                        {mode}
                      </button>
                    );
                  })}
                </div>
              )}

              {/* MultiSelect Checklist */}
              {setting.type === 'multiselect' && (
                <div className="flex flex-wrap gap-1.5">
                  {setting.options.map((opt) => {
                    const isChecked = setting.value.includes(opt);
                    return (
                      <button
                        key={opt}
                        onClick={() => {
                          sound.playClick();
                          const updated = isChecked
                            ? setting.value.filter((v: string) => v !== opt)
                            : [...setting.value, opt];
                          onUpdateSetting(setting.id, updated);
                        }}
                        className={`px-2.5 py-1 rounded-lg text-[11px] flex items-center gap-1.5 transition-all cursor-pointer ${
                          isChecked
                            ? 'bg-purple-600/30 text-purple-200 border border-purple-500/40 font-medium'
                            : 'bg-slate-900/80 text-slate-500 hover:text-slate-300'
                        }`}
                      >
                        <CheckSquare className={`w-3 h-3 ${isChecked ? 'text-purple-400' : 'text-slate-600'}`} />
                        <span>{opt}</span>
                      </button>
                    );
                  })}
                </div>
              )}

              {/* Boolean Setting */}
              {setting.type === 'boolean' && (
                <div className="flex items-center justify-between">
                  <span className="text-[11px] text-slate-400">{setting.description}</span>
                  <button
                    onClick={() => {
                      sound.playToggle(!setting.value);
                      onUpdateSetting(setting.id, !setting.value);
                    }}
                    className={`w-11 h-6 rounded-full transition-colors relative cursor-pointer ${
                      setting.value ? 'bg-indigo-600 shadow-sm shadow-indigo-600/50' : 'bg-slate-800'
                    }`}
                  >
                    <div
                      className={`w-4 h-4 rounded-full bg-white absolute top-1 transition-transform ${
                        setting.value ? 'left-6' : 'left-1'
                      }`}
                    />
                  </button>
                </div>
              )}

              {/* Color Setting */}
              {setting.type === 'color' && (
                <div className="flex items-center gap-3">
                  <div
                    className="w-8 h-8 rounded-lg border border-white/20 shadow-sm"
                    style={{
                      backgroundColor: `rgba(${setting.value.r}, ${setting.value.g}, ${setting.value.b}, ${setting.value.a})`,
                    }}
                  />
                  <div className="flex items-center gap-2">
                    <input
                      type="color"
                      value={`#${((1 << 24) + (setting.value.r << 16) + (setting.value.g << 8) + setting.value.b).toString(16).slice(1)}`}
                      onChange={(e) => {
                        const hex = e.target.value;
                        const r = parseInt(hex.slice(1, 3), 16);
                        const g = parseInt(hex.slice(3, 5), 16);
                        const b = parseInt(hex.slice(5, 7), 16);
                        onUpdateSetting(setting.id, { ...setting.value, r, g, b });
                      }}
                      className="w-8 h-8 rounded cursor-pointer bg-transparent border-0"
                    />
                    <label className="text-xs text-slate-400 flex items-center gap-1.5 cursor-pointer">
                      <input
                        type="checkbox"
                        checked={setting.value.rainbow}
                        onChange={(e) => {
                          sound.playClick();
                          onUpdateSetting(setting.id, { ...setting.value, rainbow: e.target.checked });
                        }}
                        className="rounded accent-indigo-500"
                      />
                      <span>Rainbow RGB</span>
                    </label>
                  </div>
                </div>
              )}

              <p className="text-[10px] text-slate-500 leading-tight">{setting.description}</p>
            </div>
          );
        })}
      </div>
    </div>
  );
};
