package com.easeon.cs.core.api;

import java.util.LinkedHashMap;

public final class EaseonFeatureSubCategory {
    private final String id;
    private final String title;

    private static final LinkedHashMap<String, EaseonFeatureSubCategory> REGISTRY = new LinkedHashMap<>();

    private EaseonFeatureSubCategory(String id, String titleKey) {
        this.id = id;
        this.title = titleKey;
    }

    public static EaseonFeatureSubCategory register(String id, String titleKey) {
        return REGISTRY.computeIfAbsent(id, k -> new EaseonFeatureSubCategory(id, titleKey));
    }

    public String getTitle() {
        return title;
    }

    public String getId() {
        return id;
    }
}
