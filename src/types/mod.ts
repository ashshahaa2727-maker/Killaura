export type ModuleCategory =
  | 'COMBAT'
  | 'MOVEMENT'
  | 'RENDER'
  | 'PLAYER'
  | 'WORLD'
  | 'NEURO_AI'
  | 'SHADER_STUDIO'
  | 'CONFIGS'
  | 'SETTINGS';

export interface CategoryInfo {
  id: ModuleCategory;
  name: string;
  nameRu: string;
  icon: string;
  description: string;
}

export type SettingType = 'boolean' | 'slider' | 'mode' | 'multiselect' | 'color' | 'keybind';

export interface BaseSetting {
  id: string;
  name: string;
  nameRu?: string;
  description: string;
  type: SettingType;
  dependency?: { settingId: string; expectedValue: unknown };
}

export interface BooleanSetting extends BaseSetting {
  type: 'boolean';
  value: boolean;
}

export interface SliderSetting extends BaseSetting {
  type: 'slider';
  value: number;
  min: number;
  max: number;
  step: number;
  unit?: string;
}

export interface ModeSetting extends BaseSetting {
  type: 'mode';
  value: string;
  modes: string[];
}

export interface MultiSelectSetting extends BaseSetting {
  type: 'multiselect';
  value: string[];
  options: string[];
}

export interface ColorSetting extends BaseSetting {
  type: 'color';
  value: {
    r: number;
    g: number;
    b: number;
    a: number;
    rainbow: boolean;
  };
}

export interface KeyBindSetting extends BaseSetting {
  type: 'keybind';
  value: string; // e.g. "RSHIFT", "NONE", "R", "X"
}

export type ModuleSetting =
  | BooleanSetting
  | SliderSetting
  | ModeSetting
  | MultiSelectSetting
  | ColorSetting
  | KeyBindSetting;

export interface ClientModule {
  id: string;
  name: string;
  nameRu?: string;
  category: ModuleCategory;
  description: string;
  enabled: boolean;
  keybind: string;
  settings: ModuleSetting[];
}

export interface ConfigPreset {
  id: string;
  name: string;
  description: string;
  author: string;
  version: string;
  createdAt: string;
  tags: string[];
  modules: Record<string, { enabled: boolean; keybind: string; settings: Record<string, unknown> }>;
}

export interface ShaderInfo {
  id: string;
  name: string;
  description: string;
  category: 'core' | 'sky' | 'effect';
  fragmentFile: string;
  vertexFile: string;
  jsonFile: string;
  uniforms: string[];
}

export interface NeuroModelMetadata {
  kind: string;
  features: number;
  hidden: number;
  mix: number;
  squash: string;
  units: string;
  limits: [number, number];
  knob: [number, number, number][];
  freezeCut: number;
  error: number[];
  featureNames: string[];
}
