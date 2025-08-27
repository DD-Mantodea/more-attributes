package org.mantodea.more_attributes.ui;

import net.minecraft.client.gui.Font;

public class Coordinates {

    public int turnPagePosY;

    public int previousPagePosX;

    public int nextPagePosX;

    public int classIconBoxPosX;

    public int classIconBoxPosY;

    public int selectButtonPosX;

    public int selectButtonPosY;

    public int startItemPosX;

    public int startItemPosY;

    public int startAttributesPosX;

    public int startAttributesPosY;

    public int descriptionPosX;

    public int descriptionPosY;

    public Coordinates(int posX, int posY, int backgroundWidth, int backgroundHeight, Font font) {
        int lineHeight = font.lineHeight;

        turnPagePosY = posY + 152;
        previousPagePosX = posX + 33;
        nextPagePosX = posX + 350;

        classIconBoxPosX = posX + 48;
        classIconBoxPosY = posY + 15;

        selectButtonPosX = posX + 82;
        selectButtonPosY = posY + 158;

        startItemPosX = posX + 42;
        startItemPosY = posY + 128;

        startAttributesPosX = posX + 24 + backgroundWidth / 2;
        startAttributesPosY = posY + lineHeight * 2;

        descriptionPosX = posX + 42;
        descriptionPosY = posY + 108;
    }
}
