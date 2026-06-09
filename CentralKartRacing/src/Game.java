public class Game{

    static final int loadingChunks = 128;
    private Map map;
    private Player player;
    private Renderer r;
    private boolean isRunning = false;
    private Thread gameLoopThread;
    private String playerName = "";
    private long finishTime = 0L; //long cause of timer
    
    /**
     * constructor
     */
    public Game(String mapName, String mapFolder){
        map = new Map(mapName, mapFolder);
        player = new Player(map);
        r = new Renderer(map, player);
        loadMap(player, map, r);
    }

    public Game(String mapName, String mapFolder, int character){
        map = new Map(mapName, mapFolder);
        player = new Player(map, character);
        r = new Renderer(map, player);
        loadMap(player, map, r);
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
                long timeStarted = System.currentTimeMillis() + 5500;
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
                            player.checkDrifting(r.uDown(), r.aDown(), r.dDown(), r.iDown(), timeElapsedFrame);
                            player.acceleratePlayer(r.wDown(), r.sDown(), r.iDown(), timeElapsedFrame);
                            player.angularlyAcceleratePlayer(r.aDown(), r.dDown(), r.iDown(), timeElapsedFrame);
                            player.movePlayer(timeElapsedFrame);
                            player.turnPlayer(timeElapsedFrame);
                            player.checkCheckpoints();
                            map.modifyGroundTexture((int)(timeElapsedFrameMillis));
                        } else {
                            timeStarted += timeElapsedFrameMillis;
                        }

                        previousFrameTime = System.currentTimeMillis();
                        //testPlayer.printPos();
                        //testPlayer.printDirection();
                        if (player.win && !wonBefore) {
                            stop();
                            wonBefore = true;
                            r.checkGameFinish();
                            finishTime = timeElapsed;
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
        player.stopAllSounds();
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
        map.logLeaderboard(name, finishTime);
    }

    public static void loadMap(Player player, Map map, Renderer r) {
        for (int i = 0; i < loadingChunks*2; i++) {
            player.turnPlayerInstant(i*2*Math.PI/loadingChunks);
            player.teleportPlayer(map.getStartingPos().x + (i - loadingChunks)/loadingChunks, map.getStartingPos().y + (i - loadingChunks)/loadingChunks);
            r.render();
        }
    }

    public static int getTimeElapsed(long startTime) {
        return (int)(System.currentTimeMillis() - startTime);
    }

    
}
