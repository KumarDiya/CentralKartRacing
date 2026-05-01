import java.awt.Rectangle;

public class Testing {
    public static void main(String[] args) {
        CollisionBox a = new CollisionBox(3, 3, 4,4);
        CollisionBox b = new CollisionBox(5, 5, 4, 4);
        CollisionBox c = a.intersection(b);
        //System.out.printf("%d, %d, %d, %d", c.x, c.y, c.width, c.height);
        System.out.printf("%.2f, %.2f, %.2f, %.2f", c.x, c.y, c.width, c.height);
    }   
}
