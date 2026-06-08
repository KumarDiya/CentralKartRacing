public class Game{

    static final int loadingChunks = 128;
    private Map testMap;
    private Player testPlayer;
    private Renderer r;
    private boolean isRunning = false;
    private Thread gameLoopThread;
    private String playerName = "";
    private long finishTime = 0L; //long cause of timer
    
    /**
     * constructor
     */
    public Game(String mapName, String mapFolder){
        testMap = new Map(mapName, mapFolder);
        testPlayer = new Player(testMap);
        r = new Renderer(testMap, testPlayer);
        loadMap(testPlayer, testMap, r);
    }

    public Game(String mapName, String mapFolder, int character){
        testMap = new Map(mapName, mapFolder);
        testPlayer = new Player(testMap, character);
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

                boolean wonBefore = false;
                int frameCounter = 0;
                long timeStarted = System.currentTimeMillis() + 5000;
                long timeElapsed = 0;

                while (isRunning && r.isDisplayable()) {

                    timeElapsed = System.currentTimeMillis() - timeStarted;
                    r.HUD.timeElapsed = timeElapsed;

                    long startTime = System.nanoTime();
                    
                    if (timeElapsed < 0) {
                        r.render();
                        r.requestRepaint();
                    } else {
                        timeElapsedSecond = getTimeElapsed(previousTime);

                        long timeElapsedFrameMillis = System.currentTimeMillis() - previousFrameTime;
                        double timeElapsedFrame = (double)timeElapsedFrameMillis/1000;
                        
                        if (!r.paused) {
                            testPlayer.checkDrifting(r.uDown(), r.aDown(), r.dDown(), r.iDown(), timeElapsedFrame);
                            testPlayer.acceleratePlayer(r.wDown(), r.sDown(), r.iDown(), timeElapsedFrame);
                            testPlayer.angularlyAcceleratePlayer(r.aDown(), r.dDown(), r.iDown(), timeElapsedFrame);
                            testPlayer.movePlayer(timeElapsedFrame);
                            testPlayer.turnPlayer(timeElapsedFrame);
                            testPlayer.checkCheckpoints();
                            testMap.modifyGroundTexture((int)(timeElapsedFrameMillis));
                        } else {
                            timeStarted += timeElapsedFrameMillis;
                        }

                        previousFrameTime = System.currentTimeMillis();
                        //testPlayer.printPos();
                        //testPlayer.printDirection();
                        if (testPlayer.win && !wonBefore) {
                            wonBefore = true;
                            testMap.logLeaderboard("Justin", timeElapsed);
                        }

                        if (timeElapsedSecond > 1000) {
                            timeElapsedSecond -= 1000;
                            System.out.printf("%d\n", frameCounter);
                            previousTime = System.currentTimeMillis();
                            frameCounter = 0;
                        }

                        frameCounter++;
                        
                        r.render();
                        r.requestRepaint();
                    }

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

    public void setPlayerName(String name) { //setter method
        this.playerName = name;
    }
    
    public long getFinishTime(){
        return finishTime;
    }

    public void logFinish(String name) {
        testMap.logLeaderboard(name, finishTime);
    }

    public static void loadMap(Player player, Map map, Renderer r) {
        for (int i = 0; i < loadingChunks*2; i++) {
            player.turnPlayerInstant(i*2*Math.PI/loadingChunks);
            player.teleportPlayer(map.getStartingPos().x + (i - loadingChunks)/loadingChunks, map.getStartingPos().y + (i - loadingChunks)/loadingChunks);
            r.render();
        }
    }

    public static void loadCharacters() {
        
    }

    public static int getTimeElapsed(long startTime) {
        return (int)(System.currentTimeMillis() - startTime);
    }

    
}
