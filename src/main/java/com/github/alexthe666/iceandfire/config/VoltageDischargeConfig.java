package com.github.alexthe666.iceandfire.config;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;

public class VoltageDischargeConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String CONFIG_DIR = "iceandfire";
    private static final String CONFIG_FILE = "voltage_discharge_thresholds.json";

    private static final ConcurrentHashMap<String, Integer> THRESHOLDS = new ConcurrentHashMap<>();

    private static volatile int defaultThreshold = 10;

    private VoltageDischargeConfig() {
    }

    public static int getThreshold(EntityType<?> entityType) {
        ResourceLocation key = ForgeRegistries.ENTITY_TYPES.getKey(entityType);
        if (key == null) {
            return defaultThreshold;
        }
        return THRESHOLDS.getOrDefault(key.toString(), defaultThreshold);
    }

    public static void load() {
        Path configDir = FMLPaths.CONFIGDIR.get().resolve(CONFIG_DIR);
        Path configFile = configDir.resolve(CONFIG_FILE);

        try {
            Files.createDirectories(configDir);
            if (Files.exists(configFile)) {
                readConfig(configFile);
            } else {
                generateDefault(configFile);
            }
        } catch (IOException e) {
            IceAndFire.LOGGER.error("[VoltageDischargeConfig] 配置加载失败，使用默认值", e);
        }

        IceAndFire.LOGGER.info("[VoltageDischargeConfig] 已加载 {} 条放电阈值配置，默认阈值: {}",
                THRESHOLDS.size(), defaultThreshold);
    }

    private static void readConfig(Path configFile) throws IOException {
        try (Reader reader = Files.newBufferedReader(configFile, StandardCharsets.UTF_8)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            JsonObject thresholds = root.getAsJsonObject("voltage_discharge_thresholds");
            if (thresholds == null) {
                IceAndFire.LOGGER.warn("[VoltageDischargeConfig] 配置文件缺少 voltage_discharge_thresholds 节点");
                return;
            }

            THRESHOLDS.clear();
            thresholds.entrySet().forEach(entry -> {
                String key = entry.getKey();
                int value = Math.max(1, Math.min(10, entry.getValue().getAsInt()));
                if ("_default".equals(key)) {
                    defaultThreshold = value;
                } else {
                    THRESHOLDS.put(key, value);
                }
            });
        }
    }

    private static void generateDefault(Path configFile) throws IOException {
        JsonObject root = new JsonObject();
        JsonObject thresholds = new JsonObject();

        thresholds.addProperty("minecraft:zombie", 3);
        thresholds.addProperty("minecraft:husk", 3);
        thresholds.addProperty("minecraft:drowned", 3);
        thresholds.addProperty("minecraft:skeleton", 5);
        thresholds.addProperty("minecraft:stray", 7);
        thresholds.addProperty("minecraft:wither_skeleton", 8);
        thresholds.addProperty("minecraft:blaze", 2);
        thresholds.addProperty("minecraft:magma_cube", 2);
        thresholds.addProperty("minecraft:wither", 10);
        thresholds.addProperty("minecraft:ender_dragon", 10);
        thresholds.addProperty("minecraft:warden", 10);
        thresholds.addProperty("minecraft:iron_golem", 8);
        thresholds.addProperty("minecraft:ravager", 6);
        thresholds.addProperty("_default", 10);

        root.add("voltage_discharge_thresholds", thresholds);

        try (Writer writer = Files.newBufferedWriter(configFile, StandardCharsets.UTF_8)) {
            GSON.toJson(root, writer);
        }

        readConfig(configFile);
    }
}
