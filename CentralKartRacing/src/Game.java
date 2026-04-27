public class Game {
    public static void main(String[] args) throws Exception {
        Map testMap = new Map("Test", "testMap");
        Player testPlayer = new Player();
        Renderer r = new Renderer();
        r.renderSetup(testMap, testPlayer);
        while (true) {
            r.render(testMap, testPlayer);
            r.drawFrame();
        }
    }
}
