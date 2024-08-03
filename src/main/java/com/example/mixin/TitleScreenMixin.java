package com.example.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {
    @Inject(at = @At("HEAD"), method = "render")
    public void render(PoseStack p_96739_, int p_96740_, int p_96741_, float p_96742_, CallbackInfo ci) {
        Font font = Minecraft.getInstance().font;

        // サンプルタイトルとサブタイトル
        String titleText = "勝者";
        String subtitleText = "プレイヤー名"; // 実際の勝者の名前をここにセット

        // タイトルとサブタイトルのスケール
        float titleScale = 0.5f; // タイトルのスケール
        float subtitleScale = 0.5f; // サブタイトルのスケール

        // タイトルの描画位置
        int titleX = (int) (Minecraft.getInstance().getWindow().getGuiScaledWidth() / (2 * titleScale) - font.width(titleText) / 2);
        int titleY = 40;

        // サブタイトルの描画位置
        int subtitleX = (int) (Minecraft.getInstance().getWindow().getGuiScaledWidth() / (2 * subtitleScale) - font.width(subtitleText) / 2);
        int subtitleY = 60;

        // タイトルをスケールして描画（縁取り付き）
        p_96739_.pushPose();
        p_96739_.scale(titleScale, titleScale, titleScale);
        drawOutlinedText(p_96739_,font, new TextComponent(titleText), titleX, titleY, 0xFFFFFF, 0x000000);
        p_96739_.popPose();

        // サブタイトルをスケールして描画（縁取り付き）
        p_96739_.pushPose();
        p_96739_.scale(subtitleScale, subtitleScale, subtitleScale);
        drawOutlinedText(p_96739_,font, new TextComponent(subtitleText), subtitleX, subtitleY, 0xFFFFFF, 0x000000);
        p_96739_.popPose();
    }

    private void drawOutlinedText(PoseStack p_96739_,Font font, Component text, int x, int y, int color, int outlineColor) {
        font.drawShadow(p_96739_,text, x - 1, y, outlineColor);
        font.drawShadow(p_96739_,text, x + 1, y, outlineColor);
        font.drawShadow(p_96739_,text, x, y - 1, outlineColor);
        font.drawShadow(p_96739_,text, x, y + 1, outlineColor);
        font.drawShadow(p_96739_,text, x - 1, y - 1, outlineColor);
        font.drawShadow(p_96739_,text, x + 1, y - 1, outlineColor);
        font.drawShadow(p_96739_,text, x - 1, y + 1, outlineColor);
        font.drawShadow(p_96739_,text, x + 1, y + 1, outlineColor);
        font.drawShadow(p_96739_,text, x, y, color);
    }
}