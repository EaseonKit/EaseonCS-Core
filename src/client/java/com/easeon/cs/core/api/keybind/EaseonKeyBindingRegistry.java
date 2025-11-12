package com.easeon.cs.core.api.keybind;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import java.util.ArrayList;
import java.util.List;

public class EaseonKeyBindingRegistry {
    private static final List<KeyBinding> KEY_BINDINGS = new ArrayList<>();
    private static boolean registered = false;

    public static KeyBinding register(String translationKey, int keyCode, KeyBinding.Category category) {
        return register(translationKey, InputUtil.Type.KEYSYM, keyCode, category);
    }

    public static KeyBinding register(String translationKey, InputUtil.Type type, int keyCode, KeyBinding.Category category) {
        KeyBinding keyBinding = new KeyBinding(translationKey, type, keyCode, category);
        KEY_BINDINGS.add(keyBinding);
        return keyBinding;
    }

    public static List<KeyBinding> getKeyBindings() {
        return new ArrayList<>(KEY_BINDINGS);
    }

    public static void registerAll() {
        if (registered) return;
        registered = true;
        // KeyBinding 객체는 생성만 하면 자동으로 등록됨
        // 추가 작업 불필요
    }
}