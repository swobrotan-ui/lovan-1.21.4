package ru.levin.mixin.iface;

import net.minecraft.client.gui.widget.ClickableWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClickableWidget.class)
public interface ClickableWidgetAccessor {
    @Accessor("x")
    void setX(int x);

    @Accessor("y")
    void setY(int y);

    @Accessor("x")
    int getX();

    @Accessor("y")
    int getY();

    @Accessor("width")
    int getWidth();

    @Accessor("height")
    int getHeight();
}
