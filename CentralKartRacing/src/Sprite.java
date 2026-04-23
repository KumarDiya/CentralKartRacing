import java.util.Arrays;
import java.util.Comparator;

public class Sprite {
    Vector position;
    int texture;

    Sprite (double x, double y, int texture) {
        position = new Vector(x, y);
        this.texture = texture;
    }

    Sprite(Vector v, int texture) {
        position = v;
        this.texture = texture;
    }

    public void setXY (double x, double y){
        position.x = x;
        position.y = y;
    }

    public void setXY (Vector v) {
        position = v;
    }

    public static void sortSprites(int[] order, double[] dist, int amount){
        OrderDistancePair[] sprites = new OrderDistancePair[amount];
        for (int i = 0; i < amount; i++){
            sprites[i] = new OrderDistancePair(order[i], dist[i]);
        }
        Arrays.sort(sprites, new SortByDist());
        for (int i = 0; i < amount; i++){
            order[amount - 1 - i] = sprites[i].x;
            dist[amount - 1 - i] = sprites[i].y;
        }
    }
}

class OrderDistancePair {
    public int x;
    public double y;

    public OrderDistancePair(int x, double y) {
        this.x = x;
        this.y = y; 
    }
}

class SortByDist implements Comparator<OrderDistancePair> {
    public int compare(OrderDistancePair p1, OrderDistancePair p2){
        if (p1.y > p2.y) {
            return 1;
        } else {
            return -1;
        }
    }
}
