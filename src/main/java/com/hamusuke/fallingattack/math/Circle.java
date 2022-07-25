package com.hamusuke.fallingattack.math;

import net.minecraft.util.Mth;

public class Circle {
    private final Vec2d center;
    private double radius;

    public Circle(double centerX, double centerY, double radius) {
        this.center = new Vec2d(centerX, centerY);
        this.radius = radius;
    }

    public void spread(double amount) {
        this.radius += amount;
    }

    public Vec2d getCoordinates(float angRad, boolean translate) {
        return new Vec2d(this.radius * Mth.cos(angRad) + (translate ? this.center.x() : 0.0D), this.radius * Mth.sin(angRad) + (translate ? this.center.y() : 0.0D));
    }

    public double getRadius() {
        return this.radius;
    }
}
