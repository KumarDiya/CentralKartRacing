public class Game{

    static final int loadingChunks = 128;
    private Map testMap;
    private Player testPlayer;
    private Renderer r;
    private boolean isRunning = false;
    private Thread gameLoopThread;

    /**
     * constructor
     */
    public Game(){
        testMap = new Map("Test", "sunsetMap");
        testPlayer = new Player(testMap);
        r = new Renderer(testMap, testPlayer);
        loadMap(testPlayer, testMap, r);
    }

    public void start(){

        r.setFocusable(true);
        r.requestFocusInWindow();

        if (isRunning||(gameLoopThread) != null && gameLoopThread.isAlive()){
            System.out.println("Game is running already");
            return;
        }

        System.out.println("Game starting...");
        isRunning = true;

        gameLoopThread = new Thread(new Runnable(){
            public void run(){
                long previousFrameTime = System.currentTimeMillis();
                long previousTime = System.currentTimeMillis();
                int timeElapsedSecond;

                int frameCounter = 0;

                while (isRunning && r.isDisplayable()) {
                    
                    //System.out.println("rendering");
                    timeElapsedSecond = getTimeElapsed(previousTime);

                    double timeElapsedFrame = (double)(System.currentTimeMillis() - previousFrameTime)/1000;

                    long startTime = System.nanoTime();
                    
                    if (!r.paused) {
                        testPlayer.checkDrifting(r.uDown(), r.aDown(), r.dDown());
                        testPlayer.acceleratePlayer(r.wDown(), r.sDown(), timeElapsedFrame);
                        testPlayer.angularlyAcceleratePlayer(r.aDown(), r.dDown(), timeElapsedFrame);
                        testPlayer.movePlayer(timeElapsedFrame);
                        testPlayer.turnPlayer(timeElapsedFrame);
                        testPlayer.checkCheckpoints();
                    }
                    previousFrameTime = System.currentTimeMillis();
                    //testPlayer.printPos();
                    //testPlayer.printDirection();

                    if (timeElapsedSecond > 1000) {
                        timeElapsedSecond -= 1000;
                        System.out.printf("%d\n", frameCounter);
                        previousTime = System.currentTimeMillis();
                        frameCounter = 0;
                    }
                    frameCounter++;
                    
                    r.render();
                    r.requestRepaint();
                    long imSleepy = startTime + (long)(1e9 * Renderer.TargetFrameTime);

                    while (System.nanoTime() < imSleepy) {
                        continue;
                    }
                }
            }
        });

        gameLoopThread.start();

       
    }

    /**
     * stops the game
     */
    public void stop(){
        isRunning = false;
        if (gameLoopThread != null){
            gameLoopThread.interrupt();
        }
    }

    /**
     * set the chosen player character
     * @param player   the selected player
     */
    public void setPlayerCharacter(String player) {
        r.setPlayerCharacter(player);
    }

    /**
     * get the renderer
     * @return   the renderer
     */
    public Renderer getRenderer(){
        return r;
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
