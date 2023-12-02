package me.memorial.utils;

public final class MouseUtils {
    public static boolean isHovering(float x, float y, float width, float height, int mouseX, int mouseY) {
        return (float)mouseX >= x && (float)mouseY >= y && (float)mouseX < x + width && (float)mouseY < y + height;
    }

    private MouseUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}