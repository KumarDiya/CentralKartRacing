public class Testing {
    public static void main(String[] args) {
        CollisionBox a = new CollisionBox(10, 10, 10, 10);
        CollisionBox b = new CollisionBox(0, 0, 10.5,10.5);
        System.out.println(a.intersects(b));
        System.out.println(a.contains(8, 6));
    }
}
