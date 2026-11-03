package neuro.client.ai;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Реализация биомеханических таймингов удара и сброса спринта (W-Tap) по обученным таблицам
 * из /assets/neuro/models/neuro_clicks.json (format: neurotest-clicks-v1).
 */
public class NeuroClicks {
    private static final Logger LOGGER = LoggerFactory.getLogger("NeuroClicks");

    public static class ProfileData {
        public double[][] critP;           // [da][f] вероятности крита по фазе прыжка (1..11)
        public double[] groundHazard;      // [d] интенсивность удара на земле после готовности кулдауна
        public double[] releaseAfterTakeoff;
        public double[] repressAfterHit;
        public double holdP = 0.04;
    }

    private final Map<String, ProfileData> profiles = new HashMap<>();
    private final Random rng = new Random();

    private int airTicks = 0;
    private int groundReadyTicks = 0;
    private int chosenCritAirTick = -1;
    private int ticksSinceHit = 99;

    public void loadFromResources() {
        try (InputStream is = NeuroClicks.class.getResourceAsStream("/assets/neuro/models/neuro_clicks.json")) {
            if (is == null) {
                LOGGER.warn("[NeuroClicks] Файл neuro_clicks.json не найден, используются базовые распределения.");
                return;
            }
            JsonObject root = JsonParser.parseReader(new InputStreamReader(is, StandardCharsets.UTF_8)).getAsJsonObject();
            if (!root.has("profiles")) return;

            JsonObject profs = root.getAsJsonObject("profiles");
            for (Map.Entry<String, JsonElement> entry : profs.entrySet()) {
                String name = entry.getKey();
                JsonObject obj = entry.getValue().getAsJsonObject();
                ProfileData pd = new ProfileData();

                if (obj.has("crit") && obj.getAsJsonObject("crit").has("p")) {
                    JsonArray pArr = obj.getAsJsonObject("crit").getAsJsonArray("p");
                    pd.critP = new double[pArr.size()][];
                    for (int i = 0; i < pArr.size(); i++) {
                        pd.critP[i] = toDoubleArray(pArr.get(i).getAsJsonArray());
                    }
                }

                if (obj.has("ground") && obj.getAsJsonObject("ground").has("h")) {
                    pd.groundHazard = toDoubleArray(obj.getAsJsonObject("ground").getAsJsonArray("h"));
                }

                if (obj.has("sprint")) {
                    JsonObject sp = obj.getAsJsonObject("sprint");
                    if (sp.has("release_after_takeoff")) {
                        pd.releaseAfterTakeoff = toDoubleArray(sp.getAsJsonArray("release_after_takeoff"));
                    }
                    if (sp.has("repress_after_hit")) {
                        pd.repressAfterHit = toDoubleArray(sp.getAsJsonArray("repress_after_hit"));
                    }
                    if (sp.has("hold_p")) {
                        pd.holdP = sp.get("hold_p").getAsDouble();
                    }
                }
                profiles.put(name, pd);
            }
            LOGGER.info("[NeuroClicks] Загружено {} профилей таймингов ударов и спринта: {}",
                    profiles.size(), profiles.keySet());
        } catch (Exception e) {
            LOGGER.error("[NeuroClicks] Ошибка чтения neuro_clicks.json: {}", e.getMessage());
        }
    }

    private static double[] toDoubleArray(JsonArray arr) {
        double[] res = new double[arr.size()];
        for (int i = 0; i < arr.size(); i++) {
            res[i] = arr.get(i).getAsDouble();
        }
        return res;
    }

    private int sampleCategorical(double[] probs) {
        if (probs == null || probs.length == 0) return 6;
        double sum = 0.0;
        for (double p : probs) sum += Math.max(0.0, p);
        if (sum <= 1e-9) return 6;
        double u = rng.nextDouble() * sum;
        double acc = 0.0;
        for (int i = 0; i < probs.length; i++) {
            acc += Math.max(0.0, probs[i]);
            if (u <= acc) return i;
        }
        return probs.length - 1;
    }

    public void reset() {
        this.airTicks = 0;
        this.groundReadyTicks = 0;
        this.chosenCritAirTick = -1;
    }

    public void notifyHit(String profileName) {
        this.ticksSinceHit = 0;
        this.groundReadyTicks = 0;
        this.chosenCritAirTick = -1;
    }

    /**
     * Вычисляет, должен ли игрок нанести удар на текущем тике согласно распределению профиля.
     */
    public boolean shouldAttack(
            MinecraftClient client,
            PlayerEntity player,
            Entity target,
            String profileName,
            boolean smartCrits,
            boolean sprintReset
    ) {
        this.ticksSinceHit++;
        ProfileData pd = profiles.getOrDefault(profileName, profiles.get("strong"));
        float cooldown = player.getAttackCooldownProgress(0.5f);

        if (player.isOnGround()) {
            this.airTicks = 0;
            this.chosenCritAirTick = -1;

            if (cooldown < 0.90f) {
                this.groundReadyTicks = 0;
                return false;
            }

            if (smartCrits && !player.isTouchingWater() && !player.isSubmergedInWater() && player.getVelocity().y >= -0.08) {
                // Если игрок прыгает, ждем фазу падения
                if (client.options.jumpKey.isPressed()) {
                    return false;
                }
            }

            double hazard = 0.45;
            if (pd != null && pd.groundHazard != null && pd.groundHazard.length > 0) {
                int idx = Math.min(this.groundReadyTicks, pd.groundHazard.length - 1);
                hazard = pd.groundHazard[idx];
            }
            this.groundReadyTicks++;
            return rng.nextDouble() < hazard || this.groundReadyTicks > 5;
        } else {
            this.airTicks++;
            this.groundReadyTicks = 0;

            if (this.chosenCritAirTick < 0 && pd != null && pd.critP != null && pd.critP.length > 0) {
                int deficitIdx = Math.max(0, Math.min(pd.critP.length - 1, (int) Math.round((1.0 - cooldown) * 10.0)));
                this.chosenCritAirTick = sampleCategorical(pd.critP[deficitIdx]);
            }

            // В фазе падения (fallDistance > 0 или airTicks >= chosenCritAirTick)
            boolean falling = player.fallDistance > 0.01f || player.getVelocity().y < -0.05;
            if (smartCrits) {
                int targetTick = this.chosenCritAirTick > 0 ? this.chosenCritAirTick : 6;
                return cooldown >= 0.88f && (this.airTicks >= targetTick || (falling && cooldown >= 0.94f));
            }
            return cooldown >= 0.92f;
        }
    }
}
