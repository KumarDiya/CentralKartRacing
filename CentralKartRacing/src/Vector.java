public class Vector {
    public double x;
    public double y;

    public Vector(){
        x = 0;
        y = 0;
    }

    public Vector(double x, double y){
        this.x = x;
        this.y = y;
    }

    public Vector clone() {
        return new Vector(x, y);
    }

    public Vector addVec(Vector v){
        return new Vector(x + v.x, y + v.y);
    }

    public Vector scalMult(double a){
        return new Vector(x * a, y * a);
    }

    public double getMagnitude() {
        return Math.sqrt(x*x + y*y);
    }

    public Vector getPerpendicularRight() {
        return new Vector(y, -x);
    }

    public Vector getPerpendicularLeft() {
        return new Vector(-y, x);
    }

    public Vector floor() {
        return new Vector(Math.floor(x), Math.floor(y));
    }
}