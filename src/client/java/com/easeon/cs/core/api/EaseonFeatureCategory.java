package com.easeon.cs.core.api;

import java.util.LinkedHashMap;

public final class EaseonFeatureCategory {
    private final String id;
    private final String title;

    private static final LinkedHashMap<String, EaseonFeatureCategory> REGISTRY = new LinkedHashMap<>();

    private EaseonFeatureCategory(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public static EaseonFeatureCategory register(String id, String title) {
        return REGISTRY.computeIfAbsent(id, k -> new EaseonFeatureCategory(id, title));
    }

    public static EaseonFeatureCategory[] values() {
        return REGISTRY.values().toArray(new EaseonFeatureCategory[0]);
    }

    public String getTitle() {
        return title;
    }
}
