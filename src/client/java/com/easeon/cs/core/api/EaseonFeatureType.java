package com.easeon.cs.core.api;

import com.easeon.cs.core.config.model.FeatureStruct;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minecraft.text.Text;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.function.Supplier;

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

    public Text getSliderText(double value) {
        return Text.translatable(ValueFormat, value);
    }

    public Text getTitle() {
        return Text.translatable(title);
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

        ObjectMapper mapper = new ObjectMapper();

        // 기본값 생성
        FeatureStruct def = defaultSupplier.get();
        if (def == null) return false;

        // 두 객체를 JsonNode로 변환
        JsonNode defaultNode = mapper.valueToTree(def);
        JsonNode currentNode = mapper.valueToTree(current);

        // 깊은 비교
        return !Objects.equals(defaultNode, currentNode);
    }
}
