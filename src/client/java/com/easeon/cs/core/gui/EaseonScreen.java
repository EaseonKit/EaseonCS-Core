package com.easeon.cs.core.gui;

import com.easeon.cs.core.api.EaseonFeatureType;
import com.easeon.cs.core.config.EaseonConfig;
import com.easeon.cs.core.config.GuiConfig;
import com.easeon.cs.core.config.StringKey;
import com.easeon.cs.core.config.model.FeatureStruct;
import com.easeon.cs.core.config.model.HotkeyConfig;
import com.easeon.cs.core.config.model.SliderConfig;
import com.easeon.cs.core.config.model.ToggleConfig;
import com.easeon.cs.core.gui.common.GuiRenderable;
import com.easeon.cs.core.gui.section.HotkeySection;
import com.easeon.cs.core.gui.section.SliderSection;
import com.easeon.cs.core.gui.section.ToggleSection;
import com.easeon.cs.core.gui.views.ScrollView;
import com.easeon.cs.core.gui.views.SideView;
import com.easeon.cs.core.gui.views.TabSection;
import com.easeon.cs.core.gui.widget.EaseonSlider;
import com.easeon.cs.core.gui.widget.SectionHeader;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EaseonScreen extends Screen
{
    public EaseonScreen() { super(Text.translatable(EaseonConfig.modName)); }

    // Tab
    public int tabIndex = 0;
    private TabSection tabSection;

    // ScrollView
    public ScrollView scrollView;

    // Widget
    public final List<GuiRenderable> widgets = new ArrayList<>();
    private SideView sideSection;

    @Override
    public void init() {
        this.children().clear();

        // Side
        sideSection = new SideView(this);

        // Features
        widgets.clear();
        String prevPanel = null;
        for (EaseonFeatureType feature : EaseonFeatureType.values()) {
            if (feature.getCategoryIndex() == tabIndex) {
                var subId = feature.getSubCategory().getId();
                var subTitle = feature.getSubCategory().getTitle();
                if (!Objects.equals(prevPanel, subId)) {
                    prevPanel = subId;
                    widgets.add(new SectionHeader(subTitle));
                }

                Class<? extends FeatureStruct> cls = feature.getStructClass();

                if (SliderConfig.class.isAssignableFrom(cls)) {
                    widgets.add(new SliderSection(this, feature));
                } else if (ToggleConfig.class.isAssignableFrom(cls)) {
                    widgets.add(new ToggleSection(this, feature));
                } else if (HotkeyConfig.class.isAssignableFrom(cls)) {
                    widgets.add(new HotkeySection(this, feature));
                }
            }
        }

        // Scroll
        scrollView = new ScrollView(widgets);

        // Tab
        tabSection = new TabSection(this);
    }

    public void refreshUI() {
        sideSection.reset.active = !EaseonConfig.allDefault();
    }

    // UI Render *******************************************************************************************************
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Render Minecraft's default background (e.g., dirt texture, screen fade)
        context.fill(0, 0, this.width, this.height, 0xC0101010);

        // Render custom UI background panels (side panel, scroll view area)
        sideSection.render(context, mouseX, mouseY, delta);
        tabSection.render(context, mouseX, mouseY, delta);

        // Contents
        context.fill(scrollView.x1, scrollView.y1 - 2, scrollView.x2, scrollView.y1 - 1, GuiConfig.SCROLL_VIEW_BORDER_COLOR1);
        context.fill(scrollView.x1, scrollView.y1 - 1, scrollView.x2, scrollView.y1, GuiConfig.SCROLL_VIEW_BORDER_COLOR2);
        context.fill(scrollView.x1, scrollView.y1, scrollView.x2, scrollView.y2, GuiConfig.SCROLL_VIEW_BACKGROUND_COLOR);
        context.fill(scrollView.x1, scrollView.y2, scrollView.x2, scrollView.y2 + 1, GuiConfig.SCROLL_VIEW_BORDER_COLOR2);
        context.fill(scrollView.x1, scrollView.y2 + 1, scrollView.x2, scrollView.y2 + 2, GuiConfig.SCROLL_VIEW_BORDER_COLOR1);

        // Apply to scissor limit drawing area to scrollable region
        context.enableScissor(scrollView.x1, scrollView.y1, scrollView.x2, scrollView.y2);

        // Render widgets inside the scrollable view (e.g., sliders, group titles)
        renderScissorWidgets(context, mouseX, mouseY, delta);

        // Render the vertical scrollbar if necessary
        renderScroll(context);

        // Disable scissor to resume normal drawing
        context.disableScissor();

        // Draw the screen title centered above the side panel
        int titleX = this.width - (GuiConfig.SIDE_VIEW_WIDTH / 2) - GuiConfig.PADDING;
        context.drawCenteredTextWithShadow(textRenderer, this.title, titleX, GuiConfig.PADDING * 4 + GuiConfig.WIDGET_HEIGHT, GuiConfig.FONT_COLOR);
    }

    private void renderScissorWidgets(DrawContext context, int mouseX, int mouseY, float delta) {
        var row = 0;
        for (GuiRenderable widget : widgets) {
            int baseY = GuiConfig.PADDING * 2 + row * (GuiConfig.WIDGET_HEIGHT + GuiConfig.SPACING)
                    - (int)scrollView.scrollOffset + GuiConfig.WIDGET_HEIGHT;

            // Y값은 항상 설정 (렌더링 여부와 무관하게)
            widget.SetY(baseY);

            // 화면에 보이는 것만 렌더링
            if (baseY + GuiConfig.WIDGET_HEIGHT >= scrollView.y1 && baseY <= scrollView.y2) {
                widget.render(context, mouseX, mouseY, delta);
            }

            row++;
        }
    }

    private void renderScroll(DrawContext context) {
        if (scrollView.isScrollbarHidden()) return;

        scrollView.updateScrollBarMetrics();

        context.fill(scrollView.trackX1, scrollView.y1, scrollView.x2, scrollView.y2, GuiConfig.SCROLL_TRACK_COLOR);
        context.fill(scrollView.trackX1, scrollView.scrollBarY, scrollView.x2, scrollView.scrollBarY + scrollView.scrollBarHeight, GuiConfig.SCROLL_BAR_BORDER);
        context.fill(scrollView.trackX1, scrollView.scrollBarY, scrollView.x2 - 1, scrollView.scrollBarY + scrollView.scrollBarHeight - 1, GuiConfig.SCROLL_BAR_INNER);
    }


    // Event Handler ***************************************************************************************************
    @Override public boolean mouseScrolled(double mouseX, double mouseY, double horizontal, double vertical) {
        scrollView.scrollBy((float)(vertical * 20));
        return true;
    }

    @Override public boolean mouseClicked(Click click, boolean doubled) {
        var mouseX = click.x();
        var mouseY = click.y();
        var button = click.button();

        if (tabSection != null && !tabSection.tabList.isEmpty()) {
            var first = tabSection.tabList.getFirst();

            // 🔹 탭 전체 y 영역 계산
            int tabY1 = first.getY();
            int tabY2 = first.getY() + first.getHeight() + 2;

            // 🔹 탭 바 전체를 화면 전체 폭으로 확장 (빈공간도 차단)
            int tabX1 = scrollView.x1;
            int tabX2 = scrollView.x2;

            // 🔹 탭 바 영역 클릭이면 scrollView 이벤트 차단
            if (mouseY >= tabY1 && mouseY <= tabY2 &&
                    mouseX >= tabX1 && mouseX <= tabX2) {

                // 버튼 안쪽이면 탭 클릭 처리
                for (var tab : tabSection.tabList) {
                    if (tab.mouseClicked(click, doubled)) return true;
                }

                // 빈 영역 클릭이면 아무것도 안 하고 소비만 함
                return true;
            }
        }
        // 🔹 스크롤뷰 아래쪽 클릭 차단
        if (mouseY > scrollView.y2) {
            return true;
        }

        if (button == 0 && scrollView.isOverScrollBar(mouseX, mouseY)) {
            scrollView.beginDragging(mouseY);
            return true;
        }
        return super.mouseClicked(click, doubled);
    }

    @Override public boolean mouseDragged(Click click, double offsetX, double offsetY) {
        var mouseY = click.y();

        if (scrollView.isDragging) {
            float ratio = scrollView.getScrollRatioFromMouse(mouseY);
            scrollView.scrollToRatio(ratio);
            return true;
        }
        return super.mouseDragged(click, offsetX, offsetY);
    }

    @Override public boolean mouseReleased(Click click) {
        scrollView.endDragging();
        return super.mouseReleased(click);
    }

    // 단축키 변경 처리
    public HotkeySection activeCaptureSection = null;

    @Override
    public boolean keyPressed(KeyInput keyInput) {
        var keyCode = keyInput.getKeycode();
        var modifiers = keyInput.modifiers();

        if (activeCaptureSection == null) {
            return super.keyPressed(keyInput);
        }
        // keyCode, scanCode, modifiers
        // ESC 키는 캡처 취소 용도로 사용
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            activeCaptureSection.handleCapturedKey(0, 0);
            activeCaptureSection.refreshUI();
            activeCaptureSection = null;
            return true;
        }

        if (keyCode == GLFW.GLFW_KEY_LEFT_SHIFT
        || keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT
        || keyCode == GLFW.GLFW_KEY_LEFT_CONTROL
        || keyCode == GLFW.GLFW_KEY_RIGHT_CONTROL
        || keyCode == GLFW.GLFW_KEY_LEFT_ALT
        || keyCode == GLFW.GLFW_KEY_RIGHT_ALT) {
            return super.keyPressed(keyInput);
        }

        // 유효한 키 입력이면 (GLFW_KEY_UNKNOWN은 제외)
        if (keyCode != GLFW.GLFW_KEY_UNKNOWN) {
            int mod = 0;
            if ((modifiers & GLFW.GLFW_MOD_SHIFT) != 0)   mod |= 1;
            if ((modifiers & GLFW.GLFW_MOD_CONTROL) != 0) mod |= 2;
            if ((modifiers & GLFW.GLFW_MOD_ALT) != 0)     mod |= 4;

            activeCaptureSection.handleCapturedKey(keyCode, mod);
            activeCaptureSection.refreshUI();
            activeCaptureSection = null;
            return true;
        }

        return true;
    }


    // UI Setup ********************************************************************************************************
    public <T extends ButtonWidget> void registerChild(T widget) { super.addDrawableChild(widget); }
    public <T extends EaseonSlider> void registerChild2(T widget) { super.addDrawableChild(widget); }
    @Override public boolean shouldPause() { return true; }
}