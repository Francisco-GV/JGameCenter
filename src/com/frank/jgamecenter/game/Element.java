package com.frank.jgamecenter.game;

public class Element {
    public double x;
    public double y;
    public double width;
    public double height;

    public Element() { }

    public Element(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public boolean collides(Element element) {
        return  ((this.x >= element.x && this.x <= element.x + element.width) ||
                  element.x >= this.x && element.x <= this.x + this.width) &&
                ((this.y >= element.y && this.y <= element.y + element.height) ||
                        element.y >= this.y && element.y <= this.y + this.height);
    }

    public String getAttributesString() {
        return "x: " + x + ", y: " + y + ", width: " + width + ", height: " + height;
    }
}