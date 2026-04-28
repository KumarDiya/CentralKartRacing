import java.awt.Rectangle;

/**
 * CollisionBox.java
 * Justin Zhou
 * 04/28/26
 * A class similar to Rectangle, but with two major differences:
 *      1. Works with doubles
 *      2. Defined with the centre of the rectangle, rather than the top right corner.
 *          Done to make collisions in game easier, as sprites are defined by their center.
 */

public class CollisionBox {
    //The center of the box.
    double centX;
    double centY;
    
    //The width and the height of the box.
    double width;
    double height;

    /**
     * Constructs a new CollisionBox whose center is specified as (x,y) and whose width and height are specified by the arguments of the same name.
     * @param x The specified x coordinate.
     * @param y The specified y coordinate
     * @param width     The specified width.
     * @param height    The specified height.
     */
    public CollisionBox(double x, double y, double width, double height) {
        centX = x;
        centY = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Constructs a new CollisionBox whose center is specified as the endpoint of the position Vector v and whose width and height are specified by the arguments of the same name.
     * @param v The specified position vector.
     * @param width     The specified width.
     * @param height    The specified height.
     */
    public CollisionBox(Vector v, double width, double height) {
        centX = v.x;
        centY = v.y;
        this.width = width;
        this.height = height;
        Rectangle a = new Rectangle(1, 1, 1, 1);
        a.intersects(1, 1, 1, 1);
    }

    /**
     * Checks whether or not this CollisionBox contains the point at the specified location (x,y).
     * @param X The specified x coordinate.
     * @param Y The specified y coordinate.
     * @return  {@code true} if the point (x,y) is inside this CollisionBox, {@code false} otherwise.
     */
    public boolean contains(double X, double Y) {
        double w = this.width;
        double h = this.height;
        if (w < 0 || h < 0) {
            // At least one of the dimensions is negative...
            return false;
        }
        // Note: if either dimension is zero, tests below must return false...
        double x = this.centX - w/2;
        double y = this.centY - h/2;
        if (X < x || Y < y) {
            return false;
        }
        w += x;
        h += y;
        //    overflow || intersect
        return ((w < x || w > X) &&
                (h < y || h > Y));
    }

    /**
     * Checks whether or not this CollisionBox contains the point at the head of Vector v.
     * @param v The specified {@code Vector}.
     * @return  {@code true} if the point (x,y) is inside this CollisionBox, {@code false} otherwise.
     */
    public boolean contains(Vector v) {
        return this.contains(v.x, v.y);
    }

    /**
     * Checks whether or not this CollisionBox intersects a rectangle defined by point (x,y), and a width and height.
     * @param x the x coodinate of the center of the rectangle.
     * @param y the y coordinate of the center of the rectangle.
     * @param width     the width of the rectangle.
     * @param height    the height of the rectangle.
     * @return  {@code true} if the rectangle intersects this CollisionBox, {@code false} otherwise.
     */
    public boolean intersects(double x, double y, double width, double height){
        double tw = this.width;
        double th = this.height;
        double rw = width;
        double rh = width;
        if (rw <= 0 || rh <= 0 || tw <= 0 || th <= 0) {
            return false;
        }
        double tx = this.centX - tw/2;
        double ty = this.centY - th/2;
        double rx = x - rw/2;
        double ry = y - rh/2;
        rw += rx;
        rh += ry;
        tw += tx;
        th += th;
        //      overflow || intersect
        return ((rw < rx || rw > tx) &&
                (rh < ry || rh > ty) &&
                (tw < tx || tw > rx) &&
                (th < ty || th > ry));
    }

    /**
     * Checks whether or not this CollisionBox intersects another CollisionBox.
     * @param b The specified other CollisionBox
     * @return  {@code true} if the CollisionBox intersects this CollisionBox, {@code false} otherwise.
     */
    public boolean intersects(CollisionBox b) {
        return this.intersects(b.centX, b.centY, b.width, b.height);
    }


}
