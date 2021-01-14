package com.frank.jgamecenter.game;

public class Element {
    public double x;
    public double y;
    public double width;
    public double height;

    public boolean collides(Element element) {
        return  ((this.x >= element.x && this.x <= element.x + element.width) ||
                  element.x >= this.x && element.x <= this.x + this.width) &&
                ((this.y >= element.y && this.y <= element.y + element.height) ||
                        element.y >= this.y && element.y <= this.y + this.height);
    }
}