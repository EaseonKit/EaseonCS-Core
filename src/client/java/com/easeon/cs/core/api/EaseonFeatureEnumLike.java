package com.easeon.cs.core.api;

public interface EaseonFeatureEnumLike {
    String id();                    // Enum id
    String title();                 // Enum title
    Object getDefaultConfig();      // 기본 Config 반환
    int getMin();                   // Slider min
    int getMax();                   // Slider max
    default String getValueFormat() { return "%d"; } // 기본값
}