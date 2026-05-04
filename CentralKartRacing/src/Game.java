public class Game {
    public static void main(String[] args){
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
            timeElapsed = getTimeElapsed(previousTime);

            double timeElapsedFrame = (double)(System.currentTimeMillis() - previousFrameTime)/1000;
            
            testPlayer.acceleratePlayer(r.wDown(), r.sDown(), timeElapsedFrame);
            testPlayer.angularlyAcceleratePlayer(r.aDown(), r.dDown(), timeElapsedFrame);
            testPlayer.movePlayer(timeElapsedFrame);
            testPlayer.turnPlayer(timeElapsedFrame);
            previousFrameTime = System.currentTimeMillis();
            //testPlayer.printPos();

            if (timeElapsed > 1000) {
                timeElapsed -= 1000;
                System.out.println(frameCounter);
                previousTime = System.currentTimeMillis();
                frameCounter = 0;
            }
            frameCounter++;
            try {
                r.renderScreen();
            } catch (Exception e) {
                System.out.println(e.getMessage());
                break;
            }
            try {
                Thread.sleep(0, 250);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }

    public static int getTimeElapsed(long startTime) {
        return (int)(System.currentTimeMillis() - startTime);
    }
}
