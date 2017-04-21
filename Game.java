package testSQuares2;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

public class Game {

    public Board board;
    //public Player p1;
    public Bot p1,p2;
    public int levelp1,levelp2;
    public TrongTai trongTai;
    public History history;
    public int turnToken;  //Dùng để chia lượt
    public int lastToken; //Kiem tra sang luot khac
    private long timeCount;
    public long timeFlag;
    public final int TIME_LIMIT = 60;
    public final int TIME_DELAY = 10;
    private BufferedImage background;
    public static BufferedImage[] soils;
    public static BufferedImage quan0, quan6, anduoc1quan, anduoc2quan;
    public static BufferedImage houseChosen, houseChosen_Bot, houseChosenp1left, houseChosenp2left, houseChosenp1right, houseChosenp2right;
    public static BufferedImage ava_bot, ava_player, turn_focus;
    public static BufferedImage[] ava_bots;
    public static BufferedImage voSoi, choTayXuong, namTay, tayKhong, voSoi1, voSoi2;
    public static BufferedImage diLai;
    public static BufferedImage dieuCay, dieuCay1, dieuCay2;
    public static BufferedImage dieuCayGiua[];
    public int index_ani;
    public static final AudioClip GAMEOVER = Applet.newAudioClip(testsquares2.Sound.class.getResource("resources/sounds/gameover.wav"));
    public static final AudioClip INGAME = Applet.newAudioClip(testsquares2.Sound.class.getResource("resources/sounds/ingame.wav"));

    public Game(int levelp1,int botLevel) {
        Framework.gameState = Framework.GameState.GAME_CONTENT_LOADING;
        this.levelp1 = levelp1;
        this.levelp2 = botLevel;
        Thread threadForInitGame = new Thread() {
            @Override
            public void run() {
                // Sets variables and objects for the game.
                Initialize();
                // Load game files (images, sounds, ...)
                LoadContent();

                Framework.gameState = Framework.GameState.PLAYING;
            }
        };
        threadForInitGame.start();
    }

    /**
     * Set variables and objects for the game.
     */
    private void Initialize() {
        if (Framework.music_on) {
            INGAME.loop();
        }
        this.board = new Board();
        //Khai bao p1
        p1 = new Bot();
        if (levelp1>0)
            p1.InitBot(this, levelp1, 1);
        else p1.initPlayer(this, "Tuyen", 1);
        //p1.InitBot(this, 3, 1);
        
        //Khai bao p2
        p2 = new Bot();
        if (levelp2>0)
            p2.InitBot(this, levelp2, 2);
        else p2.initPlayer(this, "Long", 2);
        
        trongTai = new TrongTai(this);
        this.history = new History(this);
        turnToken = 1;
        ava_bots = new BufferedImage[6];
        soils = new BufferedImage[10];
        timeFlag = Framework.gameTime;
        dieuCayGiua = new BufferedImage[8];
        index_ani = 1;
    }

    

    /**
     * Restart game - reset some variables.
     */
    public void RestartGame() {
        board.reset();
        p1.resetBot();
        p2.resetBot();
        this.history = new History(this);
        turnToken  = 1;
        timeFlag = Framework.gameTime;
        index_ani = 1;
    }
    
    public void resetGame(){
        
    }

    /**
     * Update game logic.
     *
     * @param gameTime gameTime of the game.
     * @param mousePosition current mouse position.
     */
    public void UpdateGame(long gameTime, Point mousePosition) {
        lastToken = turnToken;
        switch (turnToken) {
            case 1:
                if ((gameTime - timeFlag) / Framework.secInNanosec < TIME_LIMIT) {
                    p1.turn(gameTime, mousePosition);
                } else {
                    p1.auto();
                }
                break;
            case 2:
                if ((gameTime - timeFlag) / Framework.secInNanosec < TIME_LIMIT) {
                    p2.turn(gameTime, mousePosition);
                } else {
                    p2.auto();
                }
                break;
            case 3:
                //trongTai.quickHandle(p1.getStep());
                trongTai.handle(p1.getStep());
                break;
            case 4:
                //trongTai.quickHandle(p2.getStep());
                trongTai.handle(p2.getStep());
                break;
            case 7: 
                history.Rollback();
                break;
            case 0:
                Framework.gameState = Framework.GameState.SUMMARY;
                break;
        }
        if (lastToken != turnToken) {
            timeFlag = gameTime;
        }
    }

    public void Over() {

    }

    /**
     * Draw the game to the screen.
     *
     * @param g2d Graphics2D
     * @param mousePosition current mouse position.
     */
    public void Draw(Graphics2D g2d, Point mousePosition, long gameTime) {

        timeCount = (gameTime - timeFlag);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.drawImage(background, 0, 0, null);
        g2d.drawImage(ava_bots[levelp2], 599, 84, null);
        g2d.drawImage(ava_player, 518, 557, null);
        g2d.setColor(new Color(51, 153, 153));
        g2d.setFont(new Font("SansSerif.bold", Font.BOLD, 60));
        if ((turnToken == 1) || (turnToken == 2)) {
            g2d.drawString(String.valueOf(TIME_LIMIT - 1 - timeCount / Framework.secInNanosec), 1008, 200);
        }
        //g2d.drawString(String.valueOf("TurnToken: " + turnToken), 900, 140);
        board.paint(g2d);      //Vẽ lại sân sau khi xử lí
        trongTai.paint(g2d);
        g2d.setColor(new Color(51, 153, 153));
        board.paintScore(g2d, p1.soDanAnDuoc, p2.soDanAnDuoc, p1.soQuanAnDuoc, p2.soQuanAnDuoc);
//        history.paint(g2d);
        {
            g2d.setColor(Color.white);
            g2d.setFont(new Font("SansSerif.bold", Font.BOLD, 40));
            g2d.drawString(String.valueOf(Framework.gameState), 95, 755);
        }

        //Vẽ điếu cày
        switch (turnToken) {
            case 1:
                g2d.drawImage(turn_focus, 518 - 20, 557 - 20, null);
            case 3:
                g2d.drawImage(dieuCay1, 953, 365, null);
                break;
            case 2:
                g2d.drawImage(turn_focus, 599 - 20, 84 - 20, null);
            case 4:
                g2d.drawImage(dieuCay2, 949, 277, null);
                break;
            case 5:
                animation_1(g2d);
                break;
            case 6:
                animation_2(g2d);
                break;
        }
    }

    public void animation_1(Graphics2D g2d) {
        if (index_ani == 1) {
            g2d.drawImage(dieuCayGiua[1], 935, 366, null);
        }
        if (index_ani == 2) {
            g2d.drawImage(dieuCayGiua[2], 928, 375, null);
        }
        if (index_ani == 3) {
            g2d.drawImage(dieuCayGiua[3], 922, 375, null);
        }
        if (index_ani == 4) {
            g2d.drawImage(dieuCayGiua[4], 921, 369, null);
        }
        if (index_ani == 5) {
            g2d.drawImage(dieuCayGiua[5], 922, 346, null);
        }
        if (index_ani == 6) {
            g2d.drawImage(dieuCayGiua[6], 927, 319, null);
        }
        if (index_ani == 7) {
            g2d.drawImage(dieuCayGiua[7], 935, 296, null);
        }
        try {
            Thread.sleep(0);
        } catch (Exception e) {
        }
        if (index_ani != 7) {
            index_ani++;
        } else {
            turnToken = 2;
        }
    }

    public void animation_2(Graphics2D g2d) {
        if (index_ani == 1) {
            g2d.drawImage(dieuCayGiua[1], 935, 366, null);
        }
        if (index_ani == 2) {
            g2d.drawImage(dieuCayGiua[2], 928, 375, null);
        }
        if (index_ani == 3) {
            g2d.drawImage(dieuCayGiua[3], 922, 375, null);
        }
        if (index_ani == 4) {
            g2d.drawImage(dieuCayGiua[4], 921, 369, null);
        }
        if (index_ani == 5) {
            g2d.drawImage(dieuCayGiua[5], 922, 346, null);
        }
        if (index_ani == 6) {
            g2d.drawImage(dieuCayGiua[6], 927, 319, null);
        }
        if (index_ani == 7) {
            g2d.drawImage(dieuCayGiua[7], 935, 296, null);
        }
        try {
            Thread.sleep(0);
        } catch (Exception e) {
        }
        if (index_ani != 1) {
            index_ani--;
        } else {
            turnToken = 1;
        }
    }
    private void LoadContent() {
        try {
            URL bgImgUrl = this.getClass().getResource("/testsquares2/resources/images/background.jpg");
            background = ImageIO.read(bgImgUrl);
            for (int i=1;i<=5;i++){
                URL ava_botImgUrl = this.getClass().getResource("/testsquares2/resources/images/avatar/bot"+i+".png");
                ava_bots[i] = ImageIO.read(ava_botImgUrl);
            }
            URL ava_playerImgUrl = this.getClass().getResource("/testsquares2/resources/images/avatar/player.png");
            ava_player = ImageIO.read(ava_playerImgUrl);

            URL turn_focusImgUrl = this.getClass().getResource("/testsquares2/resources/images/avatar/turn_focus.png");
            turn_focus = ImageIO.read(turn_focusImgUrl);

            URL quan0ImgUrl = this.getClass().getResource("/testsquares2/resources/images/soil/quan0.png");
            quan0 = ImageIO.read(quan0ImgUrl);

            URL quan6ImgUrl = this.getClass().getResource("/testsquares2/resources/images/soil/quan6.png");
            quan6 = ImageIO.read(quan6ImgUrl);

            URL anduoc1quanImgUrl = this.getClass().getResource("/testsquares2/resources/images/soil/anduoc1quan.png");
            anduoc1quan = ImageIO.read(anduoc1quanImgUrl);

            URL anduoc2quanImgUrl = this.getClass().getResource("/testsquares2/resources/images/soil/anduoc2quan.png");
            anduoc2quan = ImageIO.read(anduoc2quanImgUrl);

            URL soil_1ImgUrl = this.getClass().getResource("/testsquares2/resources/images/soil/soil_1.png");
            soils[1] = ImageIO.read(soil_1ImgUrl);

            URL soil_2ImgUrl = this.getClass().getResource("/testsquares2/resources/images/soil/soil_2.png");
            soils[2] = ImageIO.read(soil_2ImgUrl);

            URL soil_3ImgUrl = this.getClass().getResource("/testsquares2/resources/images/soil/soil_3.png");
            soils[3] = ImageIO.read(soil_3ImgUrl);

            URL soil_4ImgUrl = this.getClass().getResource("/testsquares2/resources/images/soil/soil_4.png");
            soils[4] = ImageIO.read(soil_4ImgUrl);

            URL soil_5ImgUrl = this.getClass().getResource("/testsquares2/resources/images/soil/soil_5.png");
            soils[5] = ImageIO.read(soil_5ImgUrl);

            URL soil_6ImgUrl = this.getClass().getResource("/testsquares2/resources/images/soil/soil_6.png");
            soils[6] = ImageIO.read(soil_6ImgUrl);

            URL soil_7ImgUrl = this.getClass().getResource("/testsquares2/resources/images/soil/soil_7.png");
            soils[7] = ImageIO.read(soil_7ImgUrl);

            URL soilsImgUrl = this.getClass().getResource("/testsquares2/resources/images/soil/soils.png");
            soils[8] = ImageIO.read(soilsImgUrl);

            URL chosenImgUrl = this.getClass().getResource("/testsquares2/resources/images/chosen/chosen.png");
            houseChosen = ImageIO.read(chosenImgUrl);

            URL chosenp1leftImgUrl = this.getClass().getResource("/testsquares2/resources/images/chosen/chosenp1left.png");
            houseChosenp1left = ImageIO.read(chosenp1leftImgUrl);

            URL chosenp1rightImgUrl = this.getClass().getResource("/testsquares2/resources/images/chosen/chosenp1right.png");
            houseChosenp1right = ImageIO.read(chosenp1rightImgUrl);

            URL chosen_BotImgUrl = this.getClass().getResource("/testsquares2/resources/images/chosen/chosen_Bot.png");
            houseChosen_Bot = ImageIO.read(chosen_BotImgUrl);

            URL chosenp2leftImgUrl = this.getClass().getResource("/testsquares2/resources/images/chosen/chosenp2left.png");
            houseChosenp2left = ImageIO.read(chosenp2leftImgUrl);

            URL chosenp2rightImgUrl = this.getClass().getResource("/testsquares2/resources/images/chosen/chosenp2right.png");
            houseChosenp2right = ImageIO.read(chosenp2rightImgUrl);

            URL voSoiImgUrl = this.getClass().getResource("/testsquares2/resources/images/tay/voSoi.png");
            voSoi = ImageIO.read(voSoiImgUrl);

            URL choTayXuongImgUrl = this.getClass().getResource("/testsquares2/resources/images/tay/choTayXuong.png");
            choTayXuong = ImageIO.read(choTayXuongImgUrl);

            URL namTayImgUrl = this.getClass().getResource("/testsquares2/resources/images/tay/namTay.png");
            namTay = ImageIO.read(namTayImgUrl);
            
            URL tayKhongImgUrl = this.getClass().getResource("/testsquares2/resources/images/tay/taykhong.png");
            tayKhong = ImageIO.read(tayKhongImgUrl);

            URL voSoi1ImgUrl = this.getClass().getResource("/testsquares2/resources/images/tay/tayvo1.png");
            voSoi1 = ImageIO.read(voSoi1ImgUrl);

            URL voSoi2ImgUrl = this.getClass().getResource("/testsquares2/resources/images/tay/tayvo2.png");
            voSoi2 = ImageIO.read(voSoi2ImgUrl);
            
            URL diLaiImgUrl = this.getClass().getResource("/testsquares2/resources/images/DiLai.png");
            diLai = ImageIO.read(diLaiImgUrl);

            URL dieuCay_1ImgUrl = this.getClass().getResource("/testsquares2/resources/images/dieucay/dieucay_1.png");
            dieuCay1 = ImageIO.read(dieuCay_1ImgUrl);

            URL dieuCay_2ImgUrl = this.getClass().getResource("/testsquares2/resources/images/dieucay/dieucay_2.png");
            dieuCay2 = ImageIO.read(dieuCay_2ImgUrl);

            URL dieuCayGiua_1ImgUrl = this.getClass().getResource("/testsquares2/resources/images/dieucay/dieucaygiua_1.png");
            dieuCayGiua[1] = ImageIO.read(dieuCayGiua_1ImgUrl);

            URL dieuCayGiua_2ImgUrl = this.getClass().getResource("/testsquares2/resources/images/dieucay/dieucaygiua_2.png");
            dieuCayGiua[2] = ImageIO.read(dieuCayGiua_2ImgUrl);

            URL dieuCayGiua_3ImgUrl = this.getClass().getResource("/testsquares2/resources/images/dieucay/dieucaygiua_3.png");
            dieuCayGiua[3] = ImageIO.read(dieuCayGiua_3ImgUrl);

            URL dieuCayGiua_4ImgUrl = this.getClass().getResource("/testsquares2/resources/images/dieucay/dieucaygiua_4.png");
            dieuCayGiua[4] = ImageIO.read(dieuCayGiua_4ImgUrl);

            URL dieuCayGiua_5ImgUrl = this.getClass().getResource("/testsquares2/resources/images/dieucay/dieucaygiua_5.png");
            dieuCayGiua[5] = ImageIO.read(dieuCayGiua_5ImgUrl);

            URL dieuCayGiua_6ImgUrl = this.getClass().getResource("/testsquares2/resources/images/dieucay/dieucaygiua_6.png");
            dieuCayGiua[6] = ImageIO.read(dieuCayGiua_6ImgUrl);

            URL dieuCayGiua_7ImgUrl = this.getClass().getResource("/testsquares2/resources/images/dieucay/dieucaygiua_7.png");
            dieuCayGiua[7] = ImageIO.read(dieuCayGiua_7ImgUrl);

        } catch (IOException ex) {
            Logger.getLogger(Framework.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
