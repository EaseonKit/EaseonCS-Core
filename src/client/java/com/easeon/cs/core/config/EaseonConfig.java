package com.easeon.cs.core.config;

import com.easeon.cs.core.api.EaseonFeatureType;
import com.easeon.cs.core.config.model.FeatureStruct;
import com.easeon.cs.core.config.model.HotkeyConfig;
import com.easeon.cs.core.config.model.SliderConfig;
import com.easeon.cs.core.config.model.ToggleConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;

public class EaseonConfig {
    private static final ObjectMapper mapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

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
            ObjectNode root = mapper.createObjectNode();
            if (CONFIG_FILE.exists()) {
                root = (ObjectNode) mapper.readTree(CONFIG_FILE);
            }

            for (EaseonFeatureType type : EaseonFeatureType.values()) {
                FeatureStruct struct;
                if (root.has(type.getId())) {
                    struct = mapper.treeToValue(root.get(type.getId()), type.getStructClass());
                    struct.applyDefaults();
                } else {
                    FeatureStruct defaultStruct = type.createDefault();
                    try {
                        String json = mapper.writeValueAsString(defaultStruct);
                        struct = mapper.readValue(json, type.getStructClass());
                    } catch (Exception e) {
                        struct = defaultStruct;
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

            ObjectNode root = mapper.createObjectNode();
            for (EaseonFeatureType type : EaseonFeatureType.values()) {
                FeatureStruct val = configMap.get(type);
                if (val != null) {
                    root.set(type.getId(), mapper.valueToTree(val));
                }
            }
            Files.write(CONFIG_FILE.toPath(), mapper.writeValueAsBytes(root));
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
