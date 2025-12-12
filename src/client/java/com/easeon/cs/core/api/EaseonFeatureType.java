package com.easeon.cs.core.api;

import com.easeon.cs.core.config.model.FeatureStruct;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.network.chat.Component;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.function.Supplier;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

public final class EaseonFeatureType {
    private final String id;
    private final String title;
    private final Class<? extends FeatureStruct> type;
    private final EaseonFeatureCategory category;
    private final EaseonFeatureSubCategory subCategory;
    private final Supplier<FeatureStruct> defaultSupplier;
    public final int MinValue;
    public final int MaxValue;
    public final String ValueFormat;

    private static final LinkedHashMap<String, EaseonFeatureType> REGISTRY = new LinkedHashMap<>();

    private EaseonFeatureType(String id,
                              String title,
                              Class<? extends FeatureStruct> type,
                              EaseonFeatureCategory category,
                              EaseonFeatureSubCategory subCategory,
                              Supplier<FeatureStruct> defaultSupplier,
                              int sliderMinValue,
                              int sliderMaxValue,
                              String ValueFormat) {

        if (REGISTRY.containsKey(id)) {
            throw new IllegalArgumentException("[Easeon] FeatureType name '" + id + "' already registered!");
        }

        this.id = id;
        this.title = title;
        this.type = type;
        this.category = category;
        this.subCategory = subCategory;
        this.defaultSupplier = defaultSupplier;
        this.MinValue = sliderMinValue;
        this.MaxValue = sliderMaxValue;
        this.ValueFormat = ValueFormat;
    }

    // Hotkey 전용 등록
    public static void registerHotkey(
            String id,
            String name,
            EaseonFeatureCategory category,
            EaseonFeatureSubCategory subCategory,
            Supplier<com.easeon.cs.core.config.model.HotkeyConfig> defaultSupplier) {

        REGISTRY.computeIfAbsent(id,
                k -> new EaseonFeatureType(
                        id,
                        name,
                        com.easeon.cs.core.config.model.HotkeyConfig.class,
                        category,
                        subCategory,
                        defaultSupplier::get,
                        0, 0, null));
    }


    // Toggle 전용 등록
    public static void registerToggle(
            String id,
            String name,
            EaseonFeatureCategory category,
            EaseonFeatureSubCategory subCategory,
            Supplier<com.easeon.cs.core.config.model.ToggleConfig> defaultSupplier) {

        REGISTRY.computeIfAbsent(id,
                k -> new EaseonFeatureType(
                        id,
                        name,
                        com.easeon.cs.core.config.model.ToggleConfig.class,
                        category,
                        subCategory,
                        defaultSupplier::get,
                        0, 0, null));
    }

    // Slider 전용 등록
    public static void registerSlider(
            String id,
            String name,
            EaseonFeatureCategory category,
            EaseonFeatureSubCategory subCategory,
            Supplier<com.easeon.cs.core.config.model.SliderConfig> defaultSupplier,
            int sliderMinValue,
            int sliderMaxValue,
            String ValueFormat) {

        REGISTRY.computeIfAbsent(id,
                k -> new EaseonFeatureType(
                        id,
                        name,
                        com.easeon.cs.core.config.model.SliderConfig.class,
                        category,
                        subCategory,
                        defaultSupplier::get,
                        sliderMinValue,
                        sliderMaxValue,
                        ValueFormat));
    }


    public static EaseonFeatureType[] values() {
        return REGISTRY.values().toArray(new EaseonFeatureType[0]);
    }

    public static EaseonFeatureType valueOf(String id) {
        return REGISTRY.get(id);
    }

    public String getId() {
        return id;
    }

    public EaseonFeatureSubCategory getSubCategory() {
        return subCategory;
    }

    public Component getSliderText(double value) {
        return Component.translatable(ValueFormat, value);
    }

    public Component getTitle() {
        return Component.translatable(title);
    }

    public int getCategoryIndex() {
        EaseonFeatureCategory[] categories = EaseonFeatureCategory.values();
        for (int i = 0; i < categories.length; i++) {
            if (categories[i].equals(category)) {
                return i;
            }
        }
        return -1;
    }

    public Class<? extends FeatureStruct> getStructClass() {
        return this.type;
    }

    public FeatureStruct createDefault() {
        if (defaultSupplier == null) return null;
        FeatureStruct fs = defaultSupplier.get();
        if (fs != null) fs.applyDefaults();
        return fs;
    }

    public boolean isChanged(FeatureStruct current) {
        if (current == null || defaultSupplier == null) return false;

        Gson gson = new Gson();
        FeatureStruct def = defaultSupplier.get();
        if (def == null) return false;

        // 익명 클래스 필드 강제 추출 (Gson 우회)
        JsonObject defaultJsonObj = new JsonObject();
        try {
            for (Field field : def.getClass().getSuperclass().getDeclaredFields()) {
                field.setAccessible(true);
                Object value = field.get(def);
                defaultJsonObj.add(field.getName(), gson.toJsonTree(value));
            }
        } catch (Exception e) {
            // 리플렉션 실패 시 기본 방식 사용
            return false;
        }

        JsonElement currentElement = gson.toJsonTree(current);

        System.out.println("Default JSON: " + gson.toJson(defaultJsonObj));
        System.out.println("Current JSON: " + gson.toJson(currentElement));

        return !Objects.equals(defaultJsonObj, currentElement);
    }
}