package neuro.client.ai;

import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import neuro.client.NeuroClient;
import neuro.client.module.combat.AuraModule;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Перехватывает команды ".neuro ..." в чате на стороне клиента (не отправляя их на сервер).
 */
public class NeuroCommandManager {

    public void register() {
        ClientSendMessageEvents.ALLOW_CHAT.register(message -> {
            if (message == null) return true;
            String trimmed = message.trim();
            if (trimmed.startsWith(".neuro") || trimmed.startsWith(".aura")) {
                handleCommand(trimmed);
                return false; // Не отправляем команду в публичный чат сервера
            }
            return true;
        });
    }

    private void handleCommand(String raw) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) return;

        String[] parts = raw.split("\s+");
        String sub = parts.length > 1 ? parts[1].toLowerCase() : "help";

        NeuroClient nc = NeuroClient.getInstance();
        NeuroDatasetRecorder recorder = nc.getDatasetRecorder();
        NeuroTrainerManager trainer = nc.getTrainerManager();
        NeuroAuraEngine engine = nc.getNeuroEngine();
        AuraModule aura = nc.getModuleManager().getModule(AuraModule.class);

        switch (sub) {
            case "rec":
            case "record": {
                String sessionName = parts.length > 2 ? parts[2] : "session";
                recorder.startRecording(sessionName);
                if (aura != null) aura.setRecordDatasetFlag(true);
                break;
            }
            case "stop": {
                recorder.stopRecording();
                if (aura != null) aura.setRecordDatasetFlag(false);
                break;
            }
            case "train": {
                int epochs = 200;
                if (parts.length > 2) {
                    try {
                        epochs = Integer.parseInt(parts[2]);
                    } catch (NumberFormatException ignored) {
                    }
                }
                if (recorder.isRecording()) {
                    recorder.stopRecording();
                    if (aura != null) aura.setRecordDatasetFlag(false);
                }
                trainer.startTraining(epochs);
                break;
            }
            case "load": {
                String modelArg = parts.length > 2 ? parts[2].toLowerCase() : "aim";
                if ("default".equals(modelArg)) {
                    engine.loadModelFromResource("/assets/neuro/models/default.json", "default.json");
                } else if ("v6".equals(modelArg) || "neuraim".equals(modelArg)) {
                    engine.loadModelFromResource("/assets/neuro/models/neuraim_aim_v6.json", "neuraim_v6.json");
                } else {
                    engine.loadCustomOrDefaultModel();
                }
                reply(client, "§a[Neuro] §fАктивная модель Киллауры: §d" + engine.getLoadedModelName());
                break;
            }
            case "dir":
            case "list": {
                Path dataDir = client.runDirectory.toPath().resolve("neuro").resolve("data");
                List<String> files = new ArrayList<>();
                if (Files.exists(dataDir)) {
                    try (DirectoryStream<Path> ds = Files.newDirectoryStream(dataDir, "*.csv")) {
                        for (Path p : ds) files.add(p.getFileName().toString());
                    } catch (Exception ignored) {
                    }
                }
                reply(client, "§d[Neuro] §fПапка датасетов: §7" + dataDir.toAbsolutePath());
                reply(client, "§d[Neuro] §fФайлов .csv (" + files.size() + "): §e"
                        + (files.isEmpty() ? "нет записей" : String.join(", ", files)));
                break;
            }
            case "status": {
                reply(client, "§d[Neuro Status] §fМодель: §a" + engine.getLoadedModelName()
                        + " §7| Запись: " + (recorder.isRecording() ? "§cВКЛ (" + recorder.getRecordedTicks() + "t)" : "§7ВЫКЛ")
                        + " §7| Тренер: §e" + trainer.getLastStatus());
                break;
            }
            default: {
                reply(client, "§d✦ Команды обучаемой Киллауры Neuro 1.21.4:");
                reply(client, "§e.neuro record <имя> §7— начать запись движений Вашей руки в .csv");
                reply(client, "§e.neuro stop §7— остановить запись и сохранить датасет");
                reply(client, "§e.neuro train [эпох] §7— обучить нейросеть (GRU+MDN) и загрузить aim.json");
                reply(client, "§e.neuro load <default|v6|aim> §7— переключить веса нейросети");
                reply(client, "§e.neuro dir §7— показать список записанных .csv сессий");
                reply(client, "§e.neuro status §7— статус модели, записи и обучения");
                break;
            }
        }
    }

    private void reply(MinecraftClient client, String msg) {
        client.player.sendMessage(Text.literal(msg), false);
    }
}
