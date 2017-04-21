package testSQuares2;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 * Framework that controls the game (Game.java) that created it, update it and
 * draw it on the screen.
 *
 * @author www.gametutorial.net
 */
public class Framework extends Canvas {

    /**
     * Width of the frame.
     */
    public static int frameWidth;
    /**
     * Height of the frame.
     */
    public static int frameHeight;

    /**
     * Time of one second in nanoseconds. 1 second = 1 000 000 000 nanoseconds
     */
    public static final long secInNanosec = 1000000000L;

    /**
     * Time of one millisecond in nanoseconds. 1 millisecond = 1 000 000
     * nanoseconds
     */
    public static final long milisecInNanosec = 1000000L;

    /**
     * FPS - Frames per second How many times per second the game should update?
     */
    private final int GAME_FPS = 60;
    /**
     * Pause between updates. It is in nanoseconds.
     */
    private final long GAME_UPDATE_PERIOD = secInNanosec / GAME_FPS;

    /**
     * Possible states of the game
     */
    public static enum GameState {
        STARTING, VISUALIZING, INTRODUCE, GAME_CONTENT_LOADING, MAIN_MENU,CHOOSE_MODE, 
        OPTIONS, RULES, PLAYING, GAMEOVER, DESTROYED, PAUSE,SUMMARY
    }
    /**
     * Current state of the game
     */
    public static GameState gameState;

    public static GameState preState;
    
    
    
    final int PLAYERLEVEL = 0;
    
    
    
    
    /**
     * Elapsed game time in nanoseconds.
     */
    public static long gameTime;
    // It is used for calculating elapsed time.
    private long lastTime;
    //Use for state GameOVer
    private long gameOverCount;
    int p1ScoreCount;
    int p2ScoreCount;
    int p1Score,p2Score;
    int choose_bot;
    public static boolean music_on;

    //Img background menu
    private BufferedImage bg_introduce;

    public static BufferedImage btn_pause, pause;
    //Menu chinh
    private BufferedImage bg_menu;
    private BufferedImage btn_options, btn_exit, btn_rules, btn_start;
    private BufferedImage sound_option, btn_sound_on, btn_sound_off;
    private BufferedImage ketQua, victory_icon, lose_icon, btn_playagain, btn_menu;
    int xketQua, yketQua;

    //Luat choi = hinh anh
    private BufferedImage btn_previous, btn_back, btn_next;
    public BufferedImage[] rules;
    public BufferedImage[] btn_mode;
    private int pageRule;
    public static HistoryRespository historyRespository;
    // The actual game
    private Game game;

    public Framework() {
        super();

        gameState = GameState.VISUALIZING;

        //We start game in new thread.
        Thread gameThread = new Thread() {
            @Override
            public void run() {
                GameLoop();
            }
        };
        gameThread.start();
    }

    /**
     * Set variables and objects. This method is intended to set the variables
     * and objects for this class, variables and objects for the actual game can
     * be set in Game.java.
     */
    private void Initialize()  {
        rules = new BufferedImage[9];
        btn_mode = new BufferedImage[6];
        pageRule = 1;
        gameOverCount = 0;
        music_on = false;
        choose_bot = 0;
        try {
            historyRespository = new HistoryRespository();
        } catch (SQLException ex) {
            Logger.getLogger(Framework.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Framework.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void preStarting() {
        try {
            URL bg_introImgUrl = this.getClass().getResource("/testsquares2/resources/images/introduce.jpg");
            bg_introduce = ImageIO.read(bg_introImgUrl);
        } catch (IOException ex) {
            Logger.getLogger(Framework.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Load files - images, sounds, ... This method is intended to load files
     * for this class, files for the actual game can be loaded in Game.java.
     */
    private void LoadContent() {
        try {

            URL menuImgUrl = this.getClass().getResource("/testsquares2/resources/images/menu/bg_menu.jpg");
            bg_menu = ImageIO.read(menuImgUrl);

            URL startImgUrl = this.getClass().getResource("/testsquares2/resources/images/menu/btn_start.png");
            btn_start = ImageIO.read(startImgUrl);

            URL rulesImgUrl = this.getClass().getResource("/testsquares2/resources/images/menu/btn_rules.png");
            btn_rules = ImageIO.read(rulesImgUrl);

            URL optionsImgUrl = this.getClass().getResource("/testsquares2/resources/images/menu/btn_options.png");
            btn_options = ImageIO.read(optionsImgUrl);

            URL exitImgUrl = this.getClass().getResource("/testsquares2/resources/images/menu/btn_exit.png");
            btn_exit = ImageIO.read(exitImgUrl);

            for (int i = 1; i <= 8; i++) {
                URL tempRuleImgUrl = this.getClass().getResource("/testsquares2/resources/images/menu/rule_" + i + ".png");
                rules[i] = ImageIO.read(tempRuleImgUrl);
            }
            
            for (int i = 0; i <= 5; i++) {
                URL tempModeImgUrl = this.getClass().getResource("/testsquares2/resources/images/menu/bot" + i + ".png");
                btn_mode[i] = ImageIO.read(tempModeImgUrl);
            }

            URL btn_backImgUrl = this.getClass().getResource("/testsquares2/resources/images/menu/btn_back.png");
            btn_back = ImageIO.read(btn_backImgUrl);

            URL btn_previousImgUrl = this.getClass().getResource("/testsquares2/resources/images/menu/btn_previous.png");
            btn_previous = ImageIO.read(btn_previousImgUrl);

            URL btn_nextImgUrl = this.getClass().getResource("/testsquares2/resources/images/menu/btn_next.png");
            btn_next = ImageIO.read(btn_nextImgUrl);

            URL btn_pauseImgUrl = this.getClass().getResource("/testsquares2/resources/images/btn_pause.png");
            btn_pause = ImageIO.read(btn_pauseImgUrl);

            URL pauseImgUrl = this.getClass().getResource("/testsquares2/resources/images/pause.png");
            pause = ImageIO.read(pauseImgUrl);

            URL ketquaImgUrl = this.getClass().getResource("/testsquares2/resources/images/gameOver/ketqua.png");
            ketQua = ImageIO.read(ketquaImgUrl);

            URL victoryImgUrl = this.getClass().getResource("/testsquares2/resources/images/gameOver/victory.png");
            victory_icon = ImageIO.read(victoryImgUrl);

            URL loseImgUrl = this.getClass().getResource("/testsquares2/resources/images/gameOver/lose.png");
            lose_icon = ImageIO.read(loseImgUrl);

            URL playagainImgUrl = this.getClass().getResource("/testsquares2/resources/images/gameOver/playagain.png");
            btn_playagain = ImageIO.read(playagainImgUrl);

            URL menuOverImgUrl = this.getClass().getResource("/testsquares2/resources/images/gameOver/menu.png");
            btn_menu = ImageIO.read(menuOverImgUrl);

            URL sound_optionImgUrl = this.getClass().getResource("/testsquares2/resources/images/option/sound_option.png");
            sound_option = ImageIO.read(sound_optionImgUrl);

            URL sound_onImgUrl = this.getClass().getResource("/testsquares2/resources/images/option/sound_option_on.png");
            btn_sound_on = ImageIO.read(sound_onImgUrl);

            URL sound_offImgUrl = this.getClass().getResource("/testsquares2/resources/images/option/sound_option_off.png");
            btn_sound_off = ImageIO.read(sound_offImgUrl);
        } catch (IOException ex) {
            Logger.getLogger(Framework.class.getName()).log(Level.SEVERE, "Khong load dc anh from Loadcontent", ex);
        }
        //Load anh dan gian + Tri Tue nhan tao
        //Load am thanh
    }

    public void initCoordinate() {
        xketQua = (frameWidth - ketQua.getWidth()) / 2;
        yketQua = (frameHeight - ketQua.getHeight()) / 2;
    }

    /**
     * In specific intervals of time (GAME_UPDATE_PERIOD) the game/logic is
     * updated and then the game is drawn on the screen.
     */
    private void GameLoop() {
        // This two variables are used in VISUALIZING state of the game. We used them to wait some time so that we get correct frame/window resolution.
        long visualizingTime = 0, lastVisualizingTime = System.nanoTime();

        // This variables are used for calculating the time that defines for how long we should put threat to sleep to meet the GAME_FPS.
        long beginTime, timeTaken, timeLeft;

        while (true) {
            beginTime = System.nanoTime();

            switch (gameState) {
                case PLAYING:
                    gameTime += System.nanoTime() - lastTime;

                    game.UpdateGame(gameTime, mousePosition());

                    lastTime = System.nanoTime();

                    if (Canvas.mouseButtonState(1)) {
                        if (new Rectangle(frameWidth - btn_pause.getWidth() - 20, 10, btn_pause.getWidth(), btn_pause.getHeight()).contains(mousePosition())) {
                            gameState = GameState.PAUSE;
                        }
                    }
                    break;
                case MAIN_MENU:
                    gameMenu();
                    break;
                case CHOOSE_MODE:
                    choose_mode();
                break;
                case SUMMARY:
                    summary();
                    gameState = GameState.GAMEOVER;
                break;
                case GAMEOVER:
                    gameover();
                    break;
                case PAUSE:
                    pause();
                    break;

                case OPTIONS:
                    option();
                    break;
                case RULES:
                    gameRules();
                    break;
                    
                case GAME_CONTENT_LOADING:
                    //...
                    break;

                case STARTING:
                    // Sets variables and objects.
                    Initialize();
                    // Load files - images, sounds, ...
                    LoadContent();
                    initCoordinate();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Framework.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    // When all things that are called above finished, we change game status to main menu.
                    gameState = GameState.MAIN_MENU;
                    break;
                case VISUALIZING:

                    if (this.getWidth() > 1 && visualizingTime > secInNanosec) {
                        frameWidth = this.getWidth();
                        frameHeight = this.getHeight();
                        preStarting();
                        // When we get size of frame we change status.
                        gameState = GameState.STARTING;
                    } else {
                        visualizingTime += System.nanoTime() - lastVisualizingTime;
                        lastVisualizingTime = System.nanoTime();
                    }
                    break;
            }

            // Repaint the screen.
            repaint();

            // 
            timeTaken = System.nanoTime() - beginTime;
            timeLeft = (GAME_UPDATE_PERIOD - timeTaken) / milisecInNanosec; // In milliseconds
            // Thread FPS
            if (timeLeft < 10) {
                timeLeft = 10; //set a minimum
            }
            try {
                //Provides the necessary delay and also yields control so that other thread can do work.
                Thread.sleep(timeLeft);
            } catch (InterruptedException ex) {
            }
        }
    }

    /**
     * Draw the game to the screen. It is called through repaint() method in
     * GameLoop() method.
     */
    @Override
    public void Draw(Graphics2D g2d) {
        g2d.setColor(Color.MAGENTA);
        g2d.setFont(new Font("SansSerif", Font.PLAIN, 40));
        g2d.drawString(String.valueOf(gameState), 0, 40);
        switch (gameState) {
            case PLAYING:
                game.Draw(g2d, mousePosition(), gameTime);
                g2d.drawImage(btn_pause, frameWidth - btn_pause.getWidth() - 20, 10, null);
                break;

            case MAIN_MENU:
                g2d.drawImage(bg_menu, 0, 0, frameWidth, frameHeight, null);
                g2d.drawImage(btn_start, frameWidth / 2, frameHeight / 3 + 70, null);
                g2d.drawImage(btn_rules, frameWidth / 2 - 17, frameHeight / 3 + 165, null);
                g2d.drawImage(btn_options, frameWidth / 2 - 13, frameHeight / 3 + 260, null);
                g2d.drawImage(btn_exit, frameWidth / 2 + 13, frameHeight / 3 + 355, null);
                break;
            case CHOOSE_MODE:
                g2d.drawImage(bg_menu, 0, 0, frameWidth, frameHeight, null);
                for (int i = 0;i<=5;i++)
                    g2d.drawImage(btn_mode[i], (frameWidth-btn_mode[i].getWidth())/2+40,frameHeight/4+(btn_mode[0].getHeight()+30)*i ,null);
                g2d.drawImage(btn_back, 0, 0, null);
                g2d.drawString(String.valueOf(choose_bot),100,100);
            break;
            case GAMEOVER:
                game.Draw(g2d, mousePosition(), gameTime);
                g2d.setColor(new Color(100, 134, 98));
                g2d.setFont(new Font("SansSerif", Font.PLAIN, 90));
                g2d.drawImage(ketQua, xketQua, yketQua, null);
                if (gameOverCount > GAME_FPS) {
                    if (gameOverCount % 3 == 0) {
                        if (p1ScoreCount < p1Score) {
                            p1ScoreCount++;
                        }
                        if (p2ScoreCount < p2Score) {
                            p2ScoreCount++;
                        }
                    }
//                    g2d.drawString(String.valueOf(game.p1.soDanAnDuoc + game.p1.soQuanAnDuoc * 5), xketQua + ketQua.getWidth() / 2 + 30, yketQua + ketQua.getHeight() / 2 - 7);
//                    g2d.drawString(String.valueOf(game.p2.soDanAnDuoc + game.p2.soQuanAnDuoc * 5), xketQua + ketQua.getWidth() / 2 + 30, yketQua + ketQua.getHeight() * 3 / 4 - 15);
                    g2d.drawString(String.valueOf(p1ScoreCount), xketQua + ketQua.getWidth() / 2 + 30, yketQua + ketQua.getHeight() / 2 - 7);
                    g2d.drawString(String.valueOf(p2ScoreCount), xketQua + ketQua.getWidth() / 2 + 30, yketQua + ketQua.getHeight() * 3 / 4 - 15);
                }
                //Xác nhận ng chiến thắng
                if (gameOverCount > GAME_FPS * 7 / 2) {
                    if (p1Score > p2Score) {
                        g2d.drawImage(victory_icon, xketQua + 565, yketQua + 165, null);
                    } else if (p1Score < p2Score) {
                        g2d.drawImage(victory_icon, xketQua + 565, yketQua + 272, null);
                    }
                }
                //Vẽ mặt thua
                if (gameOverCount > GAME_FPS * 10 / 2) {
                    if (p1Score > p2Score) {
                        g2d.drawImage(lose_icon, xketQua + 575, yketQua + 247, null);
                    } else if (p1Score < p2Score) {
                        g2d.drawImage(lose_icon, xketQua + 575, yketQua + 128, null);
                    }
                }
                if (gameOverCount > GAME_FPS * 12 / 2) {
                    g2d.drawImage(btn_playagain, xketQua + (ketQua.getWidth() - btn_playagain.getWidth()) / 4, yketQua + 380, null);
                    g2d.drawImage(btn_menu, xketQua + (ketQua.getWidth() - btn_menu.getWidth()) * 3 / 4, yketQua + 380, null);
                }
                break;
            case PAUSE:
                game.Draw(g2d, mousePosition(), gameTime);
                g2d.drawImage(pause, 240, 176, null);
                break;

            case OPTIONS:
                //Sound UI
                g2d.drawImage(bg_menu, 0, 0, frameWidth, frameHeight, null);
                g2d.drawImage(btn_back, 0, 0, null);
                g2d.drawImage(sound_option, (frameWidth - sound_option.getWidth()) / 2, (frameHeight - sound_option.getHeight()) / 2, null);
                if (music_on) {
                    g2d.drawImage(btn_sound_on, (frameWidth + sound_option.getWidth()) / 2 + 50, (frameHeight - sound_option.getHeight()) / 2 + 27, null);
                } else {
                    g2d.drawImage(btn_sound_off, (frameWidth + sound_option.getWidth()) / 2 + 50, (frameHeight - sound_option.getHeight()) / 2 + 27, null);
                }
                break;
            case RULES:
                g2d.drawImage(rules[pageRule], 0, 0, null);
                g2d.drawImage(btn_back, 0, 0, null);
                if (pageRule > 1) {
                    g2d.drawImage(btn_previous, 10, frameHeight - btn_previous.getHeight() - 10, null);
                }
                if (pageRule < 8) {
                    g2d.drawImage(btn_next, frameWidth - btn_next.getWidth() - 10, frameHeight - btn_next.getHeight() - 10, null);
                }
                break;
            case GAME_CONTENT_LOADING:
            case STARTING:
                g2d.drawImage(bg_introduce, 0, 0, null);
                break;
            case SUMMARY:
                game.Draw(g2d, mousePosition(), gameTime);
            break;

        }
        //g2d.setColor(Color.MAGENTA);
        //g2d.drawString(String.valueOf(gameOverCount), 530, frameHeight - 5);
    }

    /**
     * Game Menu
     */
    private void gameMenu() {
        if (Canvas.mouseButtonState(MouseEvent.BUTTON1)) {
            if (new Rectangle(frameWidth / 2, frameHeight / 3 + 70, btn_start.getWidth(), btn_start.getHeight()).contains(mousePosition())) {
                gameState = GameState.CHOOSE_MODE;
            }

            if (new Rectangle(frameWidth / 2 - 17, frameHeight / 3 + 165, btn_rules.getWidth(), btn_rules.getHeight()).contains(mousePosition())) {
                gameState = GameState.RULES;
            }

            if (new Rectangle(frameWidth / 2 - 13, frameHeight / 3 + 260, btn_options.getWidth(), btn_options.getHeight()).contains(mousePosition())) {
                gameState = GameState.OPTIONS;
            }

            if (new Rectangle(frameWidth / 2 + 13, frameHeight / 3 + 355, btn_exit.getWidth(), btn_exit.getHeight()).contains(mousePosition())) {
                System.exit(0);
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(Framework.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        preState = GameState.MAIN_MENU;
    }

    private void choose_mode(){
        if (Canvas.mouseButtonState(MouseEvent.BUTTON1)) {
            for (int i = 0;i<=5;i++)
                if ( new Rectangle((frameWidth-btn_mode[i].getWidth())/2+40,frameHeight/4+(btn_mode[0].getHeight()+33)*i ,btn_mode[i].getWidth(),btn_mode[i].getHeight()).contains(mousePosition())){
                    choose_bot = i;
                    newGame();
                }
            if (new Rectangle(0, 0, btn_back.getWidth(), btn_back.getHeight()).contains(mousePosition())) {
                gameState = GameState.MAIN_MENU;
            }
                
        }
    }
    
    private void summary() {
//        try {
//            Thread.sleep(10);
//        } catch (InterruptedException ex) {
//            Logger.getLogger(Framework.class.getName()).log(Level.SEVERE, null, ex);
//        }
        p1Score = game.p1.soDanAnDuoc + game.p1.soQuanAnDuoc*5;
        p2Score = game.p2.soDanAnDuoc + game.p2.soQuanAnDuoc*5;
        p1ScoreCount = 0;
        p2ScoreCount = 0;
        if(p1Score>p2Score){
            for (int i=2;i<game.history.stack.size();i+=2){
                game.history.stack.get(i).buocDiTruoc.win = 1;
            }
            for (int i=3;i<game.history.stack.size();i+=2){
                game.history.stack.get(i).buocDiTruoc.win = -1;
            }
        }
        else if(p2Score>p1Score){
            for (int i=3;i<game.history.stack.size();i+=2){
                game.history.stack.get(i).buocDiTruoc.win = 1;
            }
            for (int i=2;i<game.history.stack.size();i+=2){
                game.history.stack.get(i).buocDiTruoc.win = -1;
            }
        }
        
        try {
            historyRespository.saveHistory(game.history);
        } catch (SQLException ex) {
            Logger.getLogger(Framework.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Framework.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if (music_on) {
            Game.INGAME.stop();
            Game.GAMEOVER.loop();
        }
    }
    private void gameover() {
        if (Canvas.mouseButtonState(MouseEvent.BUTTON1) && (gameOverCount > GAME_FPS * 12 / 2)) {
            if (new Rectangle(xketQua + (ketQua.getWidth() - btn_playagain.getWidth()) / 4, yketQua + 380, btn_playagain.getWidth(), btn_playagain.getHeight()).contains(mousePosition())) {
                Game.GAMEOVER.stop();
                restartGame();
                gameState = GameState.PLAYING;
            }
        }
//        if(gameOverCount > GAME_FPS * 4 / 2){
//            Game.GAMEOVER.stop();
//            restartGame();
//            gameState = GameState.PLAYING;
//        }
        if (Canvas.mouseButtonState(MouseEvent.BUTTON1) && (gameOverCount > GAME_FPS * 12 / 2)) {
            if (new Rectangle(xketQua + (ketQua.getWidth() - btn_menu.getWidth()) * 3 / 4, yketQua + 380, btn_menu.getWidth(), btn_menu.getHeight()).contains(mousePosition())) {
                Game.GAMEOVER.stop();
                try {
                    Thread.sleep(300);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Framework.class.getName()).log(Level.SEVERE, null, ex);
                }
                restartGame();
                gameState = GameState.MAIN_MENU;
            }
        }
        gameOverCount++;
    }

    //Luật chơi
    private void gameRules() {
        if (Canvas.keyboardKeyState(KeyEvent.VK_LEFT) && (pageRule > 1)) {
            pageRule--;
            //Tam nghi
            try {
                Thread.sleep(300);
            } catch (InterruptedException ex) {
            }
        }
        if (Canvas.keyboardKeyState(KeyEvent.VK_RIGHT) && (pageRule < 8)) {
            pageRule++;
            //Tam nghi
            try {
                Thread.sleep(300);
            } catch (InterruptedException ex) {
            }
        }
        if (Canvas.mouseButtonState(1)) {
            if (new Rectangle(0, 0, btn_back.getWidth(), btn_back.getHeight()).contains(mousePosition())) {
                gameState = preState;
                try {
                    Thread.sleep(300);
                } catch (InterruptedException ex) {
                }
            }
            if ((new Rectangle(10, frameHeight - btn_previous.getHeight() - 10, btn_previous.getWidth(), btn_previous.getHeight()).contains(mousePosition()))
                    && (pageRule > 1)) {
                pageRule--;
                try {
                    Thread.sleep(300);
                } catch (InterruptedException ex) {
                }
            }
            if ((new Rectangle(frameWidth - btn_next.getWidth() - 10, frameHeight - btn_next.getHeight() - 10, btn_next.getWidth(), btn_next.getHeight()).contains(mousePosition()))
                    && (pageRule < 8)) {
                pageRule++;
                try {
                    Thread.sleep(300);
                } catch (InterruptedException ex) {
                }
            }
        }
    }

    private void option() {
        if (Canvas.mouseButtonState(1)) {
            if (new Rectangle(0, 0, btn_back.getWidth(), btn_back.getHeight()).contains(mousePosition())) {
                gameState = GameState.MAIN_MENU;
                try {
                    Thread.sleep(300);
                } catch (InterruptedException ex) {
                }
            }
            if (new Rectangle((frameWidth + sound_option.getWidth()) / 2 + 50, (frameHeight - sound_option.getHeight()) / 2 + 27, btn_sound_on.getWidth(), btn_sound_on.getHeight()).contains(mousePosition())) {
                music_on = !music_on;
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ex) {
                }
            }
        }
    }

    private void pause() {
        if (Canvas.mouseButtonState(MouseEvent.BUTTON1)) {
            if (new Rectangle(550, 266, 205, 90).contains(mousePosition())) {
                gameState = GameState.PLAYING;
            }

            if (new Rectangle(550, 375, 205, 90).contains(mousePosition())) {
                gameState = GameState.RULES;
            }

            if (new Rectangle(550, 480, 205, 90).contains(mousePosition())) {
                Game.INGAME.stop();
                gameState = GameState.MAIN_MENU;
                try {
                    //Provides the necessary delay and also yields control so that other thread can do work.
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                }
            }
        }
        preState = GameState.PAUSE;
    }

    /**
     * Starts new game.
     */
    private void newGame() {
        // We set gameTime to zero and lastTime to current time for later calculations.
        gameTime = 0;
        lastTime = System.nanoTime();

        game = new Game(PLAYERLEVEL,choose_bot);
    }

    /**
     * Restart game - reset game time and call RestartGame() method of game
     * object so that reset some variables.
     */
    private void restartGame() {
        // We set gameTime to zero and lastTime to current time for later calculations.
        gameTime = 0;
        lastTime = System.nanoTime();
        gameOverCount = 0;
        game.RestartGame();

    }

    /**
     * Returns the position of the mouse pointer in game frame/window. If mouse
     * position is null than this method return 0,0 coordinate.
     *
     * @return Point of mouse coordinates.
     */
    private Point mousePosition() {
        try {
            Point mp = this.getMousePosition();

            if (mp != null) {
                return this.getMousePosition();
            } else {
                return new Point(0, 0);
            }
        } catch (Exception e) {
            return new Point(0, 0);
        }
    }

    /**
     * This method is called when keyboard key is released.
     *
     * @param e KeyEvent
     */
    @Override
    public void keyReleasedFramework(KeyEvent e) {
        switch (gameState) {
            case GAMEOVER:
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    System.exit(0);
                } else if (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_ENTER) {
                    restartGame();
                }
                break;
            case PLAYING:
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    gameState = GameState.PAUSE;
                }
                break;
            case PAUSE:
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    gameState = GameState.PLAYING;
                }
                break;
            case MAIN_MENU:
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    System.exit(0);
                }
                break;
            case RULES:
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    gameState = preState;
                }
                break;
            case OPTIONS:
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    gameState = GameState.MAIN_MENU;
                }
                break;
        }
    }

    /**
     * This method is called when mouse button is clicked.
     *
     * @param e MouseEvent
     */
    @Override
    public void mouseClicked(MouseEvent e) {

    }
}
