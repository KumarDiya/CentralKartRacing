public class Game {
    static final int loadingChunks = 128;

    public static void main(String[] args){
        Map testMap = new Map("Test", "testMap");
        Player testPlayer = new Player(testMap);
        Renderer r = new Renderer(testMap, testPlayer);
        loadMap(testPlayer, testMap, r);
        r.renderSetup();

        //Time Setup
        long previousFrameTime = System.currentTimeMillis();
        long previousTime = System.currentTimeMillis();
        int timeElapsedSecond;

        int frameCounter = 0;
        long why = 0;
        System.out.println(r.isActive());

        while (r.isDisplayable()) {
            //System.out.println("rendering");
            timeElapsedSecond = getTimeElapsed(previousTime);

            double timeElapsedFrame = (double)(System.currentTimeMillis() - previousFrameTime)/1000;
            long startTime = System.nanoTime();

            testPlayer.acceleratePlayer(r.wDown(), r.sDown(), timeElapsedFrame);
            testPlayer.angularlyAcceleratePlayer(r.aDown(), r.dDown(), timeElapsedFrame);
            testPlayer.movePlayer(timeElapsedFrame);
            testPlayer.turnPlayer(timeElapsedFrame);
            testPlayer.checkCheckpoints();
            previousFrameTime = System.currentTimeMillis();
            //testPlayer.printPos();
            //testPlayer.printDirection();

            if (timeElapsedSecond > 1000) {
                timeElapsedSecond -= 1000;
                System.out.printf("%d, %d\n", frameCounter, (int)why);
                previousTime = System.currentTimeMillis();
                frameCounter = 0;
                why = 0;
            }
            frameCounter++;
            
            r.render();
            r.requestRepaint();

            long imSleepy = startTime + (long)(1e9 * Renderer.TargetFrameTime);

            while (System.nanoTime() < imSleepy) {
                why++;
                continue;
            }
        }
    }

    public static void loadMap(Player player, Map map, Renderer r) {
        for (int i = 0; i < loadingChunks*2; i++) {
            player.turnPlayerInstant(i*2*Math.PI/loadingChunks);
            player.teleportPlayer(player.StartPos.x + (i - loadingChunks)/loadingChunks, player.StartPos.y + (i - loadingChunks)/loadingChunks);
            r.render();
        }
    }

    public static int getTimeElapsed(long startTime) {
        return (int)(System.currentTimeMillis() - startTime);
    }
}