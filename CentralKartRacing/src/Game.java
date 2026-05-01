public class Game {
    public static void main(String[] args) throws Exception {
        Map testMap = new Map("Test", "testMap");
        Player testPlayer = new Player(testMap);
        Renderer r = new Renderer();
        r.renderSetup(testMap, testPlayer);

        //Time Setup
        //long startTime = System.currentTimeMillis();
        long previousFrameTime = System.currentTimeMillis();
        long previousTime = System.currentTimeMillis();
        int timeElapsed;

        int frameCounter = 0;

        while (r.isActive()) {
            r.render();
            r.drawFrame();
            timeElapsed = getTimeElapsed(previousTime);
            
            testPlayer.acceleratePlayer(r.wDown(), r.sDown());
            testPlayer.angularlyAcceleratePlayer(r.aDown(), r.dDown());
            testPlayer.movePlayer((double)(System.currentTimeMillis() - previousFrameTime)/1000);
            testPlayer.turnPlayer((double)(System.currentTimeMillis() - previousFrameTime)/1000);
            previousFrameTime = System.currentTimeMillis();
            //testPlayer.printPos();
            System.out.printf("%.2f %.2f %.2f %.2f\n", testPlayer.pos.x, testPlayer.pos.y, testPlayer.direction.x, testPlayer.direction.y);

            if (timeElapsed > 1000) {
                timeElapsed -= 1000;
                System.out.println(frameCounter);
                previousTime = System.currentTimeMillis();
                frameCounter = 0;
            }

            frameCounter++;
        }
    }

    public static int getTimeElapsed(long startTime) {
        return (int)(System.currentTimeMillis() - startTime);
    }
}
