package com.easeon.cs.core;

import com.easeon.cs.core.api.EaseonFeatureCategory;
import com.easeon.cs.core.api.EaseonFeatureEnumLike;
import com.easeon.cs.core.api.EaseonFeatureSubCategory;
import com.easeon.cs.core.api.EaseonFeatureType;
import com.easeon.cs.core.config.EaseonConfig;
import com.easeon.cs.core.config.model.HotkeyConfig;
import com.easeon.cs.core.config.model.ToggleConfig;
import com.easeon.cs.core.config.model.SliderConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

import java.util.function.Supplier;

public class EaseonClientCore {

    private EaseonClientCore() {}

    // -------------------------------
    // 초기화 및 로드
    // -------------------------------
    public static EaseonCategoryBuilder init(String modName, String configPath) {
        if (modName == null || modName.trim().isEmpty()) {
            throw new IllegalArgumentException("Mod ID cannot be null, empty, or whitespace.");
        }
        if (configPath == null || configPath.trim().isEmpty()) {
            throw new IllegalArgumentException("Config path cannot be null, empty, or whitespace.");
        }

        EaseonConfig.init(modName, configPath);

        return new EaseonCategoryBuilder(new EaseonClientCore());
    }

    // -------------------------------
    // 설정 화면 관련
    // -------------------------------
    public static Screen getSettingsScreen() {
        return new com.easeon.cs.core.gui.EaseonScreen();
    }

    public static void openSettingsScreen() {
        Minecraft client = Minecraft.getInstance();
        client.execute(() -> client.setScreen(getSettingsScreen()));
    }

    // -------------------------------
    // Config getter
    // -------------------------------
    public static SliderConfig getSliderConfig(EaseonFeatureEnumLike feature) {
        return EaseonConfig.getSliderConfig(EaseonFeatureType.valueOf(feature.id().toLowerCase()));
    }

    public static ToggleConfig getToggleConfig(EaseonFeatureEnumLike feature) {
        return EaseonConfig.getToggleConfig(EaseonFeatureType.valueOf(feature.id().toLowerCase()));
    }

    public static HotkeyConfig getHotkeyConfig(EaseonFeatureEnumLike feature) {
        return EaseonConfig.getHotkeyConfig(EaseonFeatureType.valueOf(feature.id().toLowerCase()));
    }

    // -------------------------------
    // DSL 빌더
    // -------------------------------
    public EaseonCategoryBuilder category(String id, String title) {
        var category = EaseonFeatureCategory.register(id, title);
        return new EaseonCategoryBuilder(this, category);
    }

    public static class EaseonCategoryBuilder {
        private final EaseonClientCore core;
        private final EaseonFeatureCategory category;

        public EaseonCategoryBuilder(EaseonClientCore core) {
            this.core = core;
            this.category = null;
        }

        public EaseonCategoryBuilder(EaseonClientCore core, EaseonFeatureCategory category) {
            this.core = core;
            this.category = category;
        }

        public EaseonSubCategoryBuilder section(String id, String title) {
            var sub = EaseonFeatureSubCategory.register(id, title);
            return new EaseonSubCategoryBuilder(core, category, sub);
        }

        public EaseonCategoryBuilder category(String id, String title) {
            return core.category(id, title);
        }

        public void build() {
            EaseonConfig.load();
        }
    }

    public static class EaseonSubCategoryBuilder {
        private final EaseonClientCore core;
        private final EaseonFeatureCategory category;
        private final EaseonFeatureSubCategory subCategory;

        public EaseonSubCategoryBuilder(EaseonClientCore core, EaseonFeatureCategory category, EaseonFeatureSubCategory subCategory) {
            this.core = core;
            this.category = category;
            this.subCategory = subCategory;
        }

        // -------------------------------
        // 기존 DSL
        // -------------------------------
        public EaseonSubCategoryBuilder slider(String id, String name, Supplier<SliderConfig> supplier, int min, int max, String ValueFormat) {
            EaseonFeatureType.registerSlider(id.toLowerCase(), name, category, subCategory, supplier, min, max, ValueFormat);
            return this;
        }

        public EaseonSubCategoryBuilder toggle(String id, String name, Supplier<ToggleConfig> supplier) {
            EaseonFeatureType.registerToggle(id.toLowerCase(), name, category, subCategory, supplier);
            return this;
        }

        public EaseonSubCategoryBuilder hotkey(String id, String name, Supplier<HotkeyConfig> supplier) {
            EaseonFeatureType.registerHotkey(id.toLowerCase(), name, category, subCategory, supplier);
            return this;
        }

        // -------------------------------
        // ⚡ Enum-like DSL 추가
        // -------------------------------
        public EaseonSubCategoryBuilder slider(EaseonFeatureEnumLike feature) {
            return slider(feature.id(), feature.title(), () -> (SliderConfig) feature.getDefaultConfig(), feature.getMin(), feature.getMax(), feature.getValueFormat());
        }

        public EaseonSubCategoryBuilder toggle(EaseonFeatureEnumLike feature) {
            return toggle(feature.id(), feature.title(), () -> (ToggleConfig) feature.getDefaultConfig());
        }

        public EaseonSubCategoryBuilder hotkey(EaseonFeatureEnumLike feature) {
            return hotkey(feature.id(), feature.title(), () -> (HotkeyConfig) feature.getDefaultConfig());
        }

        // -------------------------------
        // 이동 메서드
        // -------------------------------
        public EaseonSubCategoryBuilder section(String id, String title) {
            var sub = EaseonFeatureSubCategory.register(id, title);
            return new EaseonSubCategoryBuilder(core, category, sub);
        }

        public EaseonCategoryBuilder category(String id, String title) {
            return core.category(id, title);
        }

        public void build() {
            EaseonConfig.load();
        }
    }
}
