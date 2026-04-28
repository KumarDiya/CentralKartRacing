public class Game {
    public static void main(String[] args) throws Exception {
        Map testMap = new Map("Test", "testMap");
        Player testPlayer = new Player();
        Renderer r = new Renderer();
        long startTime = System.currentTimeMillis();
        System.out.println(startTime);
        r.renderSetup(testMap, testPlayer);
        while (true) {
            //Player Movement here
            r.render(testMap, testPlayer);
            r.drawFrame();
        }
    }
}
