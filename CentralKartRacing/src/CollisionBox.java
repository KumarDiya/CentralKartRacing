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
    //The top left corner of the box.
    double x;
    double y;
    
    //The width and the height of the box.
    double width;
    double height;
    //Half the width and height of the box, used in calculations.
    double halfWidth;
    double halfHeight;
    

    /**
     * Constructs a new CollisionBox whose center is specified as (x,y) and whose width and height are specified by the arguments of the same name.
     * @param x The specified x coordinate.
     * @param y The specified y coordinate
     * @param width     The specified width.
     * @param height    The specified height.
     */
    public CollisionBox(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        halfWidth = width/2;
        halfHeight = height/2;
    }

    /**
     * Constructs a new CollisionBox whose center is specified as the endpoint of the position Vector v and whose width and height are specified by the arguments of the same name.
     * @param v The specified position vector.
     * @param width     The specified width.
     * @param height    The specified height.
     */
    public CollisionBox(Vector v, double width, double height) {
        this.x = v.x;
        this.y = v.y;
        this.width = width;
        this.height = height;
        halfWidth = width/2;
        halfHeight = height/2;
    }

    public Vector[] getCorners() {
        Vector[] corners = new Vector[4];
        corners[0] = new Vector(x, y);
        corners[1] = new Vector(x + width, y);
        corners[2] = new Vector(x, y + height);
        corners[3] = new Vector(x + width, y + height);
        return corners;
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
        double x = this.x;
        double y = this.y;
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
        double rh = height;
        if (rw <= 0 || rh <= 0 || tw <= 0 || th <= 0) {
            return false;
        }
        double tx = this.x;
        double ty = this.y;
        double rx = x;
        double ry = y;
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
        double tw = this.width;
        double th = this.height;
        double rw = b.width;
        double rh = b.height;
        if (rw <= 0 || rh <= 0 || tw <= 0 || th <= 0) {
            return false;
        }
        double tx = this.x;
        double ty = this.y;
        double rx = b.x;
        double ry = b.y;
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

    public CollisionBox intersection(CollisionBox r) {
        double tx1 = this.x;
        double ty1 = this.y;
        double rx1 = r.x;
        double ry1 = r.y;
        double tx2 = tx1 + this.width;
        double ty2 = ty1 + this.height;
        double rx2 = rx1 + r.width;
        double ry2 = ry1 + r.height;
        if (tx1 < rx1) tx1 = rx1;
        if (ty1 < ry1) ty1 = ry1;
        if (tx2 > rx2) tx2 = rx2;
        if (ty2 > ry2) ty2 = ry2;
        tx2 -= tx1;
        ty2 -= ty1;
        return new CollisionBox(tx1, ty1, tx2, ty2);
    }

    // public Rectangle intersection(Rectangle r) {
    //     int tx1 = this.x;
    //     int ty1 = this.y;
    //     int rx1 = r.x;
    //     int ry1 = r.y;
    //     long tx2 = tx1; tx2 += this.width;
    //     long ty2 = ty1; ty2 += this.height;
    //     long rx2 = rx1; rx2 += r.width;
    //     long ry2 = ry1; ry2 += r.height;
    //     if (tx1 < rx1) tx1 = rx1;
    //     if (ty1 < ry1) ty1 = ry1;
    //     if (tx2 > rx2) tx2 = rx2;
    //     if (ty2 > ry2) ty2 = ry2;
    //     tx2 -= tx1;
    //     ty2 -= ty1;
    //     // tx2,ty2 will never overflow (they will never be
    //     // larger than the smallest of the two source w,h)
    //     // they might underflow, though...
    //     if (tx2 < Integer.MIN_VALUE) tx2 = Integer.MIN_VALUE;
    //     if (ty2 < Integer.MIN_VALUE) ty2 = Integer.MIN_VALUE;
    //     return new Rectangle(tx1, ty1, (int) tx2, (int) ty2);
    // }


}
