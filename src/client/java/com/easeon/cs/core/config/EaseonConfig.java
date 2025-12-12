package com.easeon.cs.core.config;

import com.easeon.cs.core.api.EaseonFeatureType;
import com.easeon.cs.core.config.model.FeatureStruct;
import com.easeon.cs.core.config.model.HotkeyConfig;
import com.easeon.cs.core.config.model.SliderConfig;
import com.easeon.cs.core.config.model.ToggleConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class EaseonConfig {
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    public static final Map<EaseonFeatureType, FeatureStruct> configMap = new LinkedHashMap<>();

    private static File CONFIG_FILE;
    public static String modName = "";
    public static String configFileName = "";

    public static void init(String modName, String configFileName) {
        EaseonConfig.modName = modName;
        EaseonConfig.configFileName = configFileName;
        CONFIG_FILE = new File(configFileName);
    }

    public static void load() {
        try {
            JsonObject root = new JsonObject();
            if (CONFIG_FILE.exists()) {
                try (FileReader reader = new FileReader(CONFIG_FILE)) {
                    root = JsonParser.parseReader(reader).getAsJsonObject();
                }
            }

            for (EaseonFeatureType type : EaseonFeatureType.values()) {
                FeatureStruct struct;
                if (root.has(type.getId())) {
                    struct = gson.fromJson(root.get(type.getId()), type.getStructClass());
                    struct.applyDefaults();
                } else {
                    // 익명 클래스 문제 해결: createDefault() 결과를 JsonObject로 직접 변환
                    FeatureStruct def = type.createDefault();
                    try {
                        JsonObject defObj = new JsonObject();
                        // 익명 클래스의 부모(실제 Config 클래스) 필드 추출
                        for (java.lang.reflect.Field field : def.getClass().getSuperclass().getDeclaredFields()) {
                            field.setAccessible(true);
                            defObj.add(field.getName(), gson.toJsonTree(field.get(def)));
                        }
                        struct = gson.fromJson(defObj, type.getStructClass());
                    } catch (Exception e) {
                        struct = def;
                    }
                }
                configMap.put(type, struct);
            }
        } catch (Exception e) {
            System.err.println("[Easeon] 설정 로드 실패: " + e.getMessage());
        }
    }

    public static void save() {
        try {
            File parent = CONFIG_FILE.getParentFile();
            if (parent != null && !parent.exists()) parent.mkdirs();

            JsonObject root = new JsonObject();
            for (EaseonFeatureType type : EaseonFeatureType.values()) {
                FeatureStruct val = configMap.get(type);
                if (val != null) {
                    root.add(type.getId(), gson.toJsonTree(val));
                }
            }

            try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
                gson.toJson(root, writer);
            }
        } catch (IOException e) {
            System.err.println("[Easeon] 설정 저장 실패: " + e.getMessage());
        }
    }

    public static boolean hasAnyNonDefault() {
        for (Map.Entry<EaseonFeatureType, FeatureStruct> entry : configMap.entrySet()) {
            if (entry.getKey().isChanged(entry.getValue())) {
                return true;
            }
        }
        return false;
    }

    public static boolean allDefault() {
        return !hasAnyNonDefault();
    }

    private static final Logger LOGGER = LogManager.getLogger("Easeon");
    public static void reset(EaseonFeatureType type) {
        LOGGER.info("[Easeon] reset: {}", type.getId());

        FeatureStruct target = configMap.get(type);
        if (target == null) return;
        LOGGER.info("[Easeon] reset: {}", target.getClass().getName());

        FeatureStruct defaults = type.createDefault();

        var fields = target.getClass().getDeclaredFields();
        for (var f : fields) {
            f.setAccessible(true);
            try {
                Object defaultVal = f.get(defaults);
                f.set(target, defaultVal);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to reset field " + f.getName(), e);
            }
        }
    }

    public static void resetAll() {
        for (EaseonFeatureType type : EaseonFeatureType.values()) {
            reset(type);
        }
    }

    public static SliderConfig getSliderConfig(EaseonFeatureType type) {
        var val = configMap.get(type);
        if (val instanceof SliderConfig sc) {
            return sc;
        } else if (val != null) {
            LOGGER.warn("[Easeon] Type mismatch: {} is not a SliderConfig (actual: {})",
                    type.getId(), val.getClass().getSimpleName());
        }
        return null;
    }

    public static ToggleConfig getToggleConfig(EaseonFeatureType type) {
        var val = configMap.get(type);
        if (val instanceof ToggleConfig tc) {
            return tc;
        } else if (val != null) {
            LOGGER.warn("[Easeon] Type mismatch: {} is not a ToggleConfig (actual: {})",
                    type.getId(), val.getClass().getSimpleName());
        }
        return null;
    }

    public static HotkeyConfig getHotkeyConfig(EaseonFeatureType type) {
        var val = configMap.get(type);
        if (val instanceof HotkeyConfig hc) {
            return hc;
        } else if (val != null) {
            LOGGER.warn("[Easeon] Type mismatch: {} is not a HotkeyConfig (actual: {})",
                    type.getId(), val.getClass().getSimpleName());
        }
        return null;
    }
}