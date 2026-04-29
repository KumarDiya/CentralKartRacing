public class Game {
    public static void main(String[] args) throws Exception {
        Map testMap = new Map("Test", "testMap");
        Player testPlayer = new Player(testMap);
        Renderer r = new Renderer();
        r.renderSetup(testMap, testPlayer);

        //Time Setup
        long startTime = System.currentTimeMillis();
        System.out.println(startTime);
        
        while (true) {
            //Player Movement here
            r.render(testMap, testPlayer);
            r.drawFrame();
        }
    }
}
