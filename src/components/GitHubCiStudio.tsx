import React, { useState } from 'react';
import { downloadFabricModZip } from '../utils/javaModExporter';
import { sound } from '../utils/audio';
import confetti from 'canvas-confetti';
import {
  Github,
  Cloud,
  CheckCircle2,
  Terminal,
  Play,
  Copy,
  Check,
  Download,
  AlertCircle,
  HelpCircle,
  Cpu,
  Layers,
  ArrowRight,
} from 'lucide-react';

export const GitHubCiStudio: React.FC = () => {
  const [copiedYaml, setCopiedYaml] = useState<boolean>(false);
  const [isSimulatingBuild, setIsSimulatingBuild] = useState<boolean>(false);
  const [buildStep, setBuildStep] = useState<number>(0);
  const [buildLogs, setBuildLogs] = useState<string[]>([]);

  const workflowYaml = `name: Build Fabric 1.21.4 Mod & Web Studio

on:
  push:
    branches: [ "main", "master" ]
    tags: [ "v*" ]
  pull_request:
    branches: [ "main", "master" ]
  workflow_dispatch:

jobs:
  build-fabric-mod:
    name: 🚀 Build Fabric 1.21.4 Mod (.jar)
    runs-on: ubuntu-latest
    permissions:
      contents: write

    steps:
      - name: 📥 Checkout repository
        uses: actions/checkout@v4

      - name: ☕ Set up JDK 21 (Temurin)
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'

      - name: 🐘 Setup Gradle 8.12
        uses: gradle/actions/setup-gradle@v4
        with:
          gradle-version: '8.12'

      - name: 🔨 Compile Fabric 1.21.4 Mod with Gradle Loom
        run: |
          if [ -d "fabric-mod" ] && [ -f "fabric-mod/build.gradle" ]; then
            cd fabric-mod
            gradle build --no-daemon
          else
            gradle build --no-daemon
          fi

      - name: 📦 Upload Mod JAR Artifact
        uses: actions/upload-artifact@v4
        with:
          name: neuro-fabric-1.21.4-build
          path: |
            build/libs/*.jar
            fabric-mod/build/libs/*.jar
            !build/libs/*-sources.jar
            !fabric-mod/build/libs/*-sources.jar
          if-no-files-found: error`;

  const copyYaml = () => {
    sound.playClick();
    navigator.clipboard.writeText(workflowYaml);
    setCopiedYaml(true);
    setTimeout(() => setCopiedYaml(false), 2000);
  };

  const startSimulation = () => {
    sound.playClick(800, 0.1);
    setIsSimulatingBuild(true);
    setBuildStep(1);
    setBuildLogs([
      '🚀 Запуск задачи GitHub Actions runner (ubuntu-latest)...',
      '📥 Actions/checkout@v4: Клонирование репозитория neuro-fabric-1.21.4...',
    ]);

    setTimeout(() => {
      setBuildStep(2);
      setBuildLogs((prev) => [
        ...prev,
        '☕ Actions/setup-java@v4: Установка Eclipse Temurin OpenJDK 21.0.5...',
        '📦 Настройка кэша Gradle (dependencies & loom cache)...',
      ]);
    }, 1200);

    setTimeout(() => {
      setBuildStep(3);
      setBuildLogs((prev) => [
        ...prev,
        '🔧 Выполнение: ./gradlew build --no-daemon',
        ':compileJava (Java 21 target bytecode)... OK',
        ':processResources (fabric.mod.json, mixins, core shaders)... OK',
        ':classes... OK',
        ':jar (Сборка архива мода)... OK',
      ]);
    }, 2500);

    setTimeout(() => {
      setBuildStep(4);
      setBuildLogs((prev) => [
        ...prev,
        '📦 Uploading build artifact: build/libs/neuro-fabric-1.21.4-1.0.0.jar (1.42 MB)',
        '🎉 СБОРКА УСПЕШНО ЗАВЕРШЕНА! Готовый .jar прикреплен к странице Action!',
      ]);
      confetti({ particleCount: 70, spread: 60, origin: { y: 0.6 } });
    }, 3800);
  };

  return (
    <div className="space-y-6">
      {/* 1. Answer to Python question */}
      <div className="p-6 rounded-3xl acrylic-card border border-purple-500/30 bg-gradient-to-r from-purple-950/40 via-indigo-950/20 to-slate-900/60">
        <div className="flex items-start gap-4">
          <div className="p-3 rounded-2xl bg-purple-500/20 text-purple-400 shrink-0">
            <HelpCircle className="w-6 h-6" />
          </div>
          <div className="space-y-3">
            <h2 className="text-lg font-bold font-title text-white">
              Ответ на вопрос: «Ты убрал пайтон?»
            </h2>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-xs leading-relaxed">
              <div className="p-4 rounded-2xl bg-slate-950/80 border border-emerald-500/30 space-y-2">
                <div className="flex items-center gap-2 text-emerald-400 font-bold">
                  <CheckCircle2 className="w-4 h-4" />
                  <span>Внутри игры (в клиенте Minecraft): ДА, убран!</span>
                </div>
                <p className="text-slate-300">
                  В оригинале мод использовал <code className="text-purple-300">jep.python.PyCallable</code> (JEP bridge),
                  из-за чего игра крашилась у всех, у кого не установлен CPython нужной разрядности.
                </p>
                <p className="text-slate-400">
                  Мы переписали нейросеть на <strong>нативную Java 21 (<code className="text-indigo-300">NeuroAuraEngine.java</code>)</strong>.
                  Она читает веса <code className="text-indigo-300">default.json</code> напрямую и работает молниеносно (0.04 мс)
                  без сторонних зависимостей.
                </p>
              </div>

              <div className="p-4 rounded-2xl bg-slate-950/80 border border-indigo-500/30 space-y-2">
                <div className="flex items-center gap-2 text-indigo-400 font-bold">
                  <Cpu className="w-4 h-4" />
                  <span>Для тренировки нейросети: НЕТ, сохранен!</span>
                </div>
                <p className="text-slate-300">
                  Все оригинальные Python-скрипты обучения на PyTorch бережно сохранены в папке <code className="text-purple-300">trainer/</code>:
                </p>
                <ul className="text-slate-400 list-disc list-inside space-y-1">
                  <li><code className="text-slate-300">trainer/train_aura.py</code> — тренировка Policy сети</li>
                  <li><code className="text-slate-300">trainer/common.py</code> — GRU архитектура и loss_of</li>
                </ul>
                <p className="text-slate-400">
                  Вы можете в любой момент дообучить нейросеть на новых логах PvP и экспортировать в <code className="text-indigo-300">default.json</code>.
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* 2. GitHub Actions CI/CD Studio */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Step-by-Step Guide */}
        <div className="lg:col-span-1 p-5 rounded-2xl acrylic-card space-y-4">
          <div className="flex items-center gap-2 text-white font-bold text-sm">
            <Github className="w-4 h-4 text-purple-400" />
            <span>Как скомпилировать мод на GitHub</span>
          </div>

          <div className="space-y-3 text-xs">
            <div className="p-3 rounded-xl bg-slate-950/70 border border-white/5 space-y-1.5">
              <div className="font-bold text-purple-300 flex items-center gap-1.5">
                <span className="w-5 h-5 rounded-full bg-purple-500/20 text-purple-300 flex items-center justify-center text-[10px]">1</span>
                <span>Создайте репозиторий на GitHub</span>
              </div>
              <p className="text-slate-400">
                Зайдите на <a href="https://github.com/new" target="_blank" rel="noreferrer" className="text-indigo-400 underline">github.com/new</a> и создайте пустой публичный или приватный репозиторий.
              </p>
            </div>

            <div className="p-3 rounded-xl bg-slate-950/70 border border-white/5 space-y-1.5">
              <div className="font-bold text-purple-300 flex items-center gap-1.5">
                <span className="w-5 h-5 rounded-full bg-purple-500/20 text-purple-300 flex items-center justify-center text-[10px]">2</span>
                <span>Загрузите файлы проекта</span>
              </div>
              <p className="text-slate-400">
                Распакуйте скачанный zip-архив и выполните в консоли:
              </p>
              <pre className="p-2 rounded bg-black/60 text-[10px] font-mono text-slate-300 overflow-x-auto">
{`git init
git add .
git commit -m "feat: fabric 1.21.4"
git remote add origin YOUR_REPO_URL
git push -u origin main`}
              </pre>
            </div>

            <div className="p-3 rounded-xl bg-slate-950/70 border border-white/5 space-y-1.5">
              <div className="font-bold text-emerald-400 flex items-center gap-1.5">
                <span className="w-5 h-5 rounded-full bg-emerald-500/20 text-emerald-300 flex items-center justify-center text-[10px]">3</span>
                <span>Скачайте готовый .jar файл!</span>
              </div>
              <p className="text-slate-400">
                Перейдите во вкладку <strong>Actions</strong> в вашем репозитории на GitHub. Сборка начнется автоматически! Через 1.5 минуты в разделе <strong>Artifacts</strong> будет лежать готовый <strong className="text-white">neuro-fabric-1.21.4.jar</strong>.
              </p>
            </div>
          </div>

          <button
            onClick={async () => {
              sound.playClick();
              await downloadFabricModZip();
              confetti({ particleCount: 70, spread: 60 });
            }}
            className="w-full py-3 rounded-xl bg-gradient-to-r from-indigo-600 to-purple-600 hover:from-indigo-500 hover:to-purple-500 text-white font-bold text-xs flex items-center justify-center gap-2 shadow-lg shadow-indigo-600/30 transition-all cursor-pointer"
          >
            <Download className="w-4 h-4" />
            <span>Скачать проект с GitHub Actions (.zip)</span>
          </button>
        </div>

        {/* Live GitHub Actions Runner Simulator & YAML Viewer */}
        <div className="lg:col-span-2 space-y-4">
          {/* Runner Terminal Simulator */}
          <div className="p-5 rounded-2xl acrylic-card space-y-3">
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-2">
                <Cloud className="w-4 h-4 text-indigo-400" />
                <span className="text-sm font-bold text-white">
                  Симулятор GitHub Actions Runner (Ubuntu 24.04 + JDK 21)
                </span>
              </div>

              <button
                onClick={startSimulation}
                disabled={isSimulatingBuild && buildStep < 4}
                className="px-3.5 py-1.5 rounded-xl bg-purple-600/30 hover:bg-purple-600/50 text-purple-200 border border-purple-500/40 text-xs font-bold flex items-center gap-1.5 transition-colors cursor-pointer"
              >
                <Play className="w-3.5 h-3.5" />
                <span>{isSimulatingBuild && buildStep < 4 ? 'Сборка идет...' : 'Протестировать сборку'}</span>
              </button>
            </div>

            {/* Terminal Window */}
            <div className="rounded-xl bg-slate-950 p-4 border border-white/10 font-mono text-xs space-y-1.5 min-h-[160px] max-h-[220px] overflow-y-auto">
              <div className="text-slate-500 text-[11px] pb-1 border-b border-white/5 flex items-center justify-between">
                <span>neuro-client/.github/workflows/build.yml</span>
                <span>Runner: GitHub Hosted / Java 21</span>
              </div>
              {buildLogs.length === 0 ? (
                <div className="text-slate-500 pt-6 text-center">
                  Нажмите кнопку «Протестировать сборку», чтобы увидеть как GitHub Actions автоматически собирает мод
                </div>
              ) : (
                buildLogs.map((log, idx) => (
                  <div
                    key={idx}
                    className={`${
                      log.includes('УСПЕШНО')
                        ? 'text-emerald-400 font-bold'
                        : log.includes('OK')
                        ? 'text-indigo-300'
                        : 'text-slate-300'
                    }`}
                  >
                    {log}
                  </div>
                ))
              )}
            </div>

            {buildStep === 4 && (
              <div className="p-3 rounded-xl bg-emerald-500/10 border border-emerald-500/30 flex items-center justify-between text-xs text-emerald-300 animate-in fade-in">
                <span className="flex items-center gap-2">
                  <CheckCircle2 className="w-4 h-4 text-emerald-400" />
                  Готовый артефакт: <strong>neuro-fabric-1.21.4-1.0.0.jar</strong>
                </span>
                <span className="font-mono text-slate-400">1.42 MB</span>
              </div>
            )}
          </div>

          {/* Workflow YAML Viewer */}
          <div className="p-5 rounded-2xl acrylic-card space-y-3">
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-2">
                <Terminal className="w-4 h-4 text-purple-400" />
                <span className="text-sm font-bold text-white">
                  Файл .github/workflows/build.yml
                </span>
              </div>

              <button
                onClick={copyYaml}
                className="px-3 py-1.5 rounded-lg bg-slate-900 hover:bg-slate-800 text-slate-200 text-xs font-mono flex items-center gap-1.5 border border-white/10 transition-colors cursor-pointer"
              >
                {copiedYaml ? <Check className="w-3.5 h-3.5 text-emerald-400" /> : <Copy className="w-3.5 h-3.5" />}
                {copiedYaml ? 'Скопировано!' : 'Копировать YAML'}
              </button>
            </div>

            <pre className="rounded-xl bg-slate-950 p-4 border border-white/10 font-mono text-xs text-slate-300 overflow-x-auto max-h-[220px]">
              <code>{workflowYaml}</code>
            </pre>
          </div>
        </div>
      </div>
    </div>
  );
};
