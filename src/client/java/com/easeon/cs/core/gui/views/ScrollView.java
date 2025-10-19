package com.easeon.cs.core.gui.views;

import com.easeon.cs.core.config.GuiConfig;
import com.easeon.cs.core.gui.common.GuiRenderable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class ScrollView {
    private static final Logger LOGGER = LogManager.getLogger("Easeon");
    public final int x1, x2, y1, y2, height;

    public int contentHeight = 0;       // 콘텐츠 전체 높이
    public int scrollBarY = 0;          // 현재 스크롤 바 Y 위치
    public int scrollBarHeight = 0;     // 스크롤 바 높이
    public float scrollOffset = 0f;     // 현재 스크롤 위치
    public int dragClickOffset = 0;     // 드래그 클릭 오프셋
    public boolean isDragging = false;  // 드래그 상태 여부
    public int trackX1;

    public ScrollView(List<GuiRenderable> scissorWidgets) {
        var w = MinecraftClient.getInstance().getWindow().getScaledWidth();
        var h = MinecraftClient.getInstance().getWindow().getScaledHeight();

        this.x1 = GuiConfig.PADDING;
        this.x2 = w - GuiConfig.SIDE_VIEW_WIDTH - GuiConfig.PADDING * 2;
        this.y1 = GuiConfig.PADDING + 2 + GuiConfig.WIDGET_HEIGHT;
        this.y2 = h - GuiConfig.PADDING - 2;
        this.height = this.y2 - this.y1;

        this.trackX1 = this.x2 - 6;

        var widgetCount = scissorWidgets.size();
        var widgetHeight = GuiConfig.WIDGET_HEIGHT + GuiConfig.SPACING;

        contentHeight = widgetCount * widgetHeight + GuiConfig.PADDING * 2 - GuiConfig.SPACING;
//        contentHeight += (int)((scissorWidgets.stream().filter(a -> a instanceof SectionHeader).count()-1) * 20);
        contentHeight = Math.max(contentHeight, height);
        LOGGER.info("contentHeight: {}, widgetCount: {}", contentHeight, widgetCount);
    }

    public boolean isOverScrollBar(double mouseX, double mouseY) {
        if (isScrollbarHidden()) return false;

        boolean inX = mouseX >= trackX1 && mouseX < x2;
        boolean inY = mouseY >= scrollBarY && mouseY < scrollBarY + scrollBarHeight;

        return inX && inY;
    }

    public boolean isScrollbarHidden() {
        return contentHeight <= height;
    }

    public void updateScrollBarMetrics() {
        if (isScrollbarHidden()) return;

        scrollBarHeight = Math.max((int) (height * (height / (float) contentHeight)), 10);
        float scrollRatio = MathHelper.clamp(scrollOffset / (float) (contentHeight - height), 0f, 1f);
        scrollBarY = y1 + (int) ((height - scrollBarHeight) * scrollRatio);
    }

    public int getMaxScrollOffset() {
        return Math.max(0, contentHeight - height);
    }

    public void scrollBy(float amount) {
        scrollOffset = MathHelper.clamp(scrollOffset - amount, 0f, getMaxScrollOffset());
    }

    public void scrollToRatio(float ratio) {
        ratio = MathHelper.clamp(ratio, 0f, 1f);
        scrollOffset = getMaxScrollOffset() * ratio;
    }

    public float getScrollRatioFromMouse(double mouseY) {
        return (float) ((mouseY - y1 - dragClickOffset) / (float) (height - scrollBarHeight));
    }

    public void beginDragging(double mouseY) {
        isDragging = true;
        dragClickOffset = (int) mouseY - scrollBarY;
    }

    public void endDragging() {
        isDragging = false;
    }

    public int getWidth() {
        return x2 - x1;
    }
}

