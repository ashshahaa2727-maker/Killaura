import React, { useState } from 'react';
import { FABRIC_1214_FILES, downloadFabricModZip, JavaFileEntry } from '../utils/javaModExporter';
import { sound } from '../utils/audio';
import confetti from 'canvas-confetti';
import { Download, Copy, Check, FileCode, FolderTree, Terminal, ShieldCheck, Box } from 'lucide-react';

export const CodeExplorer: React.FC = () => {
  const [selectedFile, setSelectedFile] = useState<JavaFileEntry>(FABRIC_1214_FILES[4]); // default to NeuroClient.java
  const [copied, setCopied] = useState<boolean>(false);
  const [isDownloading, setIsDownloading] = useState<boolean>(false);

  const handleCopy = () => {
    sound.playClick();
    navigator.clipboard.writeText(selectedFile.content);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  const handleDownloadZip = async () => {
    sound.playClick(800, 0.15);
    setIsDownloading(true);
    try {
      await downloadFabricModZip();
      confetti({
        particleCount: 80,
        spread: 70,
        origin: { y: 0.6 },
      });
    } finally {
      setIsDownloading(false);
    }
  };

  return (
    <div className="space-y-6">
      {/* Top Banner with Download Button */}
      <div className="flex flex-wrap items-center justify-between gap-4 p-5 rounded-2xl bg-gradient-to-r from-indigo-950/60 via-purple-950/40 to-slate-900 border border-indigo-500/30">
        <div>
          <div className="flex items-center gap-2">
            <span className="p-1.5 rounded-lg bg-indigo-500/20 text-indigo-400">
              <Box className="w-5 h-5" />
            </span>
            <h2 className="text-xl font-bold font-title text-white tracking-wide">
              Исходный код Fabric 1.21.4 (Java 21)
            </h2>
            <span className="px-2.5 py-0.5 rounded-full text-xs font-semibold bg-indigo-500/20 text-indigo-300 border border-indigo-500/30">
              Loom 1.8 Ready
            </span>
          </div>
          <p className="text-sm text-slate-300 mt-1 max-w-2xl">
            Код полностью переписан под стандарты Minecraft 1.21.4 Fabric: современный <code className="text-indigo-300">DrawContext</code>,
            чистый <code className="text-indigo-300">KeyBindingHelper</code> на <strong>Правый Shift</strong>, автономный GRU движок и Core Shaders.
          </p>
        </div>

        <button
          onClick={handleDownloadZip}
          disabled={isDownloading}
          className="px-5 py-3 rounded-xl bg-gradient-to-r from-indigo-600 to-purple-600 hover:from-indigo-500 hover:to-purple-500 text-white font-bold text-sm shadow-lg shadow-indigo-600/30 hover:shadow-indigo-600/50 transition-all flex items-center gap-2 transform active:scale-95 cursor-pointer"
        >
          <Download className="w-4 h-4" />
          {isDownloading ? 'Формирование архива...' : 'Скачать готовый проект (.zip)'}
        </button>
      </div>

      {/* Code Viewer Layout: Sidebar tree on left, Code view on right */}
      <div className="grid grid-cols-1 lg:grid-cols-4 gap-6">
        {/* File Tree */}
        <div className="p-4 rounded-2xl acrylic-card lg:col-span-1 space-y-3">
          <div className="flex items-center gap-2 text-xs font-bold text-slate-400 uppercase tracking-wider px-2">
            <FolderTree className="w-3.5 h-3.5" />
            Структура проекта Fabric
          </div>

          <div className="space-y-1">
            {FABRIC_1214_FILES.map((file) => {
              const isSelected = file.path === selectedFile.path;
              return (
                <button
                  key={file.path}
                  onClick={() => {
                    sound.playClick();
                    setSelectedFile(file);
                  }}
                  className={`w-full text-left px-3 py-2 rounded-xl text-xs font-mono transition-all flex items-center gap-2.5 ${
                    isSelected
                      ? 'bg-indigo-600/30 text-white border border-indigo-500/40 shadow-sm'
                      : 'text-slate-400 hover:text-slate-200 hover:bg-slate-900/60'
                  }`}
                >
                  <FileCode className={`w-3.5 h-3.5 shrink-0 ${isSelected ? 'text-indigo-400' : 'text-slate-500'}`} />
                  <span className="truncate">{file.name}</span>
                </button>
              );
            })}
          </div>

          <div className="pt-3 border-t border-white/5 text-[11px] text-slate-400 flex items-center gap-1.5">
            <ShieldCheck className="w-3.5 h-3.5 text-emerald-400" />
            <span>100% совместимо с Fabric Loader 0.16.9+</span>
          </div>
        </div>

        {/* Code Content & Header */}
        <div className="p-5 rounded-2xl acrylic-card lg:col-span-3 flex flex-col justify-between">
          <div className="flex flex-wrap items-center justify-between gap-3 pb-3 border-b border-white/10 mb-4">
            <div>
              <div className="flex items-center gap-2">
                <span className="text-xs font-mono text-indigo-400 bg-indigo-500/10 px-2 py-0.5 rounded border border-indigo-500/20">
                  {selectedFile.language.toUpperCase()}
                </span>
                <h3 className="text-sm font-bold font-mono text-white">{selectedFile.path}</h3>
              </div>
              <p className="text-xs text-slate-400 mt-1">{selectedFile.description}</p>
            </div>

            <div className="flex items-center gap-2">
              <button
                onClick={handleCopy}
                className="px-3 py-1.5 rounded-lg bg-slate-900 hover:bg-slate-800 text-slate-200 text-xs font-mono flex items-center gap-1.5 border border-white/10 transition-colors cursor-pointer"
              >
                {copied ? <Check className="w-3.5 h-3.5 text-emerald-400" /> : <Copy className="w-3.5 h-3.5" />}
                {copied ? 'Скопировано!' : 'Копировать'}
              </button>
            </div>
          </div>

          {/* Syntax Highlighted Code Area */}
          <div className="relative rounded-xl bg-slate-950 border border-white/10 p-4 overflow-x-auto max-h-[500px]">
            <pre className="text-xs font-mono text-slate-200 leading-relaxed font-mono-code">
              <code>{selectedFile.content}</code>
            </pre>
          </div>

          {/* Terminal Build Guide */}
          <div className="mt-4 p-3 rounded-xl bg-slate-950/80 border border-white/5 flex items-center gap-3 text-xs font-mono text-slate-400">
            <Terminal className="w-4 h-4 text-purple-400 shrink-0" />
            <span>
              Сборка в jar: <strong className="text-purple-300">./gradlew build</strong> &rarr;{' '}
              <span className="text-emerald-400">build/libs/neuro-fabric-1.21.4-1.0.0.jar</span>
            </span>
          </div>
        </div>
      </div>
    </div>
  );
};
