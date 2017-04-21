/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testSQuares2;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author VPC
 */
class TrongTai {

    private final int DUNG = 0;
    private final int DI = 1;
    private final int AN = 2;
    private final int BOC = 3;
    private final int KIEM_TRA = 4;
    private final int DOI = 5;

    private final int NUMBER_IMAGE = 25;

    private Game game;
    public Player player;
    public Board board;
    private Image hand;
    int danInHand;
    private int x, y;         //Tọa độ bàn tay
    Step buocDi;
    int selected;
    int direction;
    int state;
    boolean eating = false;
    int count = 0; // bien dem de ve ban tay
    int countAn = 0;
    int thaoTacAn = 0;
    int thaoTacLay = 0; // dem lay quan
    boolean thaoTacTha = false;
    // Short Link
    House[] houseShortLink;
    Villa q0ShortLink;
    Villa q6ShortLink;
    Player p1ShortLink;
    Player p2ShortLink;

    public TrongTai() {
        danInHand = 0;
        this.board = new Board();
        this.p1ShortLink = new Player();
        this.p2ShortLink = new Player();
        houseShortLink = this.board.houses;
        q0ShortLink = this.board.q0;
        q6ShortLink = this.board.q6;
        this.eating = false;
    }

    public TrongTai(Game game) {
        this.game = game;
        x = game.board.START_X;
        y = game.board.START_Y;
        danInHand = 0;
        houseShortLink = game.board.houses;
        q0ShortLink = game.board.q0;
        q6ShortLink = game.board.q6;
        p1ShortLink = game.p1;
        p2ShortLink = game.p2;
        this.state = DOI;
        this.count = 0;
        this.thaoTacLay = 0;
    }


    /**
     * Di Tung buoc
     */
    public void handle(Step buocDi) {
        // neu trang thai dang tu doi
        // duoc goi
        // den luot rai quan
        
        if (this.state == DOI) {
            this.selected = buocDi.chose;
            this.direction = buocDi.direc;
            setToaDoLayQuan();

            if (thaoTacLayQuan()) {
                return;
            }
            this.thaoTacLay = 0;
            this.danInHand = layQuan(this.selected);
            chuyenNhaKe(this.direction);

//            System.out.println("So Dan : " + this.danInHand + " Selected : " + this.selected + " direction : " + this.direction);
            this.state = DI;
            this.thaoTacTha = false;

            return;
        }

        if (this.count < NUMBER_IMAGE && this.state != KIEM_TRA && !(this.state == DI && this.thaoTacTha) && this.thaoTacAn != 1 && this.thaoTacAn != 2) {
            tangToaDo();
            return;
        }
        this.count = 0;

        try {
            Thread.sleep(150);
        } catch (Exception e) {
        }
        // kiem tra trang thai cua trong tai
        switch (this.state) {

            case DUNG:
                this.state = DOI;
                this.eating = false;
//                System.out.println("HistoryStack: "+game.history.stack.size());
                setTurnToken(buocDi);
                if (!checkContinueGame(board)) {
                    this.game.turnToken = 0;
                    tinhDiem();
                    return;
                }
                if (checkBoardPlayer(buocDi)) {
                    themDan(buocDi);
                }

                if (this.game.turnToken == 0) {
                    game.history.stack.add(new GameState(game, buocDi, false));
                } else {
                    game.history.stack.add(new GameState(game, buocDi, true));
                }
                
                resetBuocDi();
                break;

            case DI:
                if (this.thaoTacTha) {
                    this.thaoTacTha = false;
                    return;
                }
                this.thaoTacTha = true;
                rai1Quan();
                chuyenNhaKe(direction);
//                System.out.println("So Dan : " + this.danInHand + " Selected : " + this.selected + " direction : " + this.direction);
                setToaDo();
                break;

            case AN:
                if (this.countAn != 1) {
                    this.countAn++;
                    return;
                }
                if (this.thaoTacAn != 2) {
                    this.thaoTacAn++;
                    return;
                }
                this.thaoTacAn = 0;
                this.countAn = 0;
                An(buocDi, this.selected);
                chuyenNhaKe(direction);
                this.eating = true;
                this.state = KIEM_TRA;
                break;

            case BOC:

                break;

            case KIEM_TRA:
                int result = checkFinalHouse(this.selected, direction);
//                System.out.println("Check : Selected : " + this.selected + " Direction : " + this.direction);
//                System.out.println("Ket Qua Kiem Tra : " + result);
                switch (result) {

                    // Truong hop di tiep
                    case 1:
                        if (this.eating) {
                            this.state = DUNG;
                            break;
                        }
                        setToaDoLayQuan();
                        if (thaoTacLayQuan()) {
                            return;
                        }
                        this.thaoTacLay = 0;
                        this.danInHand = layQuan(this.selected);
                        chuyenNhaKe(direction);
                        this.state = DI;
                        this.thaoTacTha = false;
                        break;

                    // Truong hop An
                    case 2:
                        chuyenNhaKe(direction);
                        this.eating = true;
                        this.state = AN;
                        break;

                    // Truong hop dung
                    case 0:
                        this.state = DUNG;
                        break;

                    default:
                        break;
                }

                break;

        }

        return;
    }

        /**
     * Thuc hien buoc di va an quan
     *
     * @param buocDi (chose, direc)
     */
    public void quickHandle(Step buocDi) {

        int soDan;
        this.selected = buocDi.chose;

//        boolean flagAn = false;
        // Kiem tra so dan trong nha cuoi de di tiep
        while (true) {
            soDan = layQuan(this.selected);

            chuyenNhaKe(buocDi.direc);

            raiQuan(soDan, buocDi.direc);

            if (checkFinalHouse(this.selected, buocDi.direc) != 1) {
                break;
            }
        }

        // TH An Dan
        while (checkFinalHouse(this.selected, buocDi.direc) == 2) {
            chuyenNhaKe(buocDi.direc);

            An(buocDi, this.selected);
            // Chuyen den o tiep theo de xet
            chuyenNhaKe(buocDi.direc);
        }

        // set token de choi tiep
        setTurnToken(buocDi);
        if (!checkContinueGame(board)) {
            this.game.turnToken = 0;
            tinhDiem();
        }

        // Trong Truong hop khong du quan de rai khi het dan trong cac o
        // Score < 5
        // thi game over
        if (this.game.turnToken != 0 && checkBoardPlayer(buocDi)) {
            themDan(buocDi);
        }
        
        if (this.game.turnToken == 0) {
            game.history.add(new GameState(game, buocDi, false));
        } else {
            game.history.add(new GameState(game, buocDi, true));
        }

        resetBuocDi();
    }
    public boolean thaoTacLayQuan() {
        if (this.thaoTacLay < 3) {
            try {
                Thread.sleep(150);
            } catch (Exception e) {
            }
            this.thaoTacLay++;
            return true;
        }
        return false;
    }

//    public boolean thaoTacThaQuan() {
//        if (this.thaoTacTha < 2) {
//            try {
//                Thread.sleep(300);
//            } catch (InterruptedException ex) {
//                Logger.getLogger(TrongTai.class.getName()).log(Level.SEVERE, null, ex);
//            }
//            this.thaoTacTha++;
//            return true;
//        }
//        return false;
//    }
    /**
     * Rai 1 Quan Neu So Quan Trong tay het Chuyen trang thai ban ve kiem tra
     */
    public void rai1Quan() {
        this.danInHand--;
        if (this.selected != 6 && this.selected != 0) {
            houseShortLink[this.selected].tangDanSo();
        } else if (this.selected == 0) {
            q0ShortLink.tangDanSo();
        } else {
            q6ShortLink.tangDanSo();
        }
        if (this.danInHand == 0) {
            this.state = KIEM_TRA;
        }
    }

    /**
     * Thuc hien buoc di va an quan
     *
     * @param buocDi (chose, direc)
     */
    public GameState handleCalculate(Step buocDi, GameState gameState) {

        if (gameState.gameContinue == false) return gameState;
        setBoardByGameState(gameState);
        setPlayerByGameState(gameState);
        
        int soDan;
        this.selected = buocDi.chose;
        boolean gameContinue = true;

//        boolean flagAn = false;
        // Kiem tra so dan trong nha cuoi de di tiep
        while (true) {
            soDan = layQuan(this.selected);
//            System.out.println("So Dan : " + soDan + " selected : " + selected);

            chuyenNhaKe(buocDi.direc);

            raiQuan(soDan, buocDi.direc);

//            System.out.println("Result Check : " + checkFinalHouse(this.selected, buocDi.direc));
            if (checkFinalHouse(this.selected, buocDi.direc) != 1) {
                break;
            }
        }

        // TH An Dan
        while (checkFinalHouse(this.selected, buocDi.direc) == 2) {
            this.eating = true;
            chuyenNhaKe(buocDi.direc);

            An(buocDi, this.selected);
            // Chuyen den o tiep theo de xet
            chuyenNhaKe(buocDi.direc);
//            System.out.println("Selected : " + this.selected);
        }

        this.eating = false;

//        System.out.println("Continue Check : " + checkContinueGame(board));
        if (!checkContinueGame(board)) {
            gameContinue = false;
            System.out.println("Dung choi");
        }

        // Trong Truong hop khong du quan de rai khi het dan trong cac o
        // Score < 5
        // thi game over
        // Sua Fake Token
        if (gameContinue && checkBoardPlayer(buocDi)) {
            if (!fakeThemDan(buocDi)) {
                gameContinue = false;
                System.out.println("Dung choi");

            }
        }

        return new GameState(buocDi, board, p1ShortLink, p2ShortLink, gameContinue);
    }

        /**
     * Sua Calculate Thanh CalculateBot2
     * @param buocDi
     * @param gameState
     * @return 
     */
    public GameState calculateBot2(Step buocDi, GameState gameState) {
        
        final int SO_BUOC_TINH = 17;
        int soBuocDaTinh = 0;

        setBoardByGameState(gameState);
        setPlayerByGameState(gameState);
        
        int soDan;
        this.selected = buocDi.chose;
        boolean gameContinue = true;

//        boolean flagAn = false;
        // Kiem tra so dan trong nha cuoi de di tiep
        while (true) {
            soDan = layQuan(this.selected);
//            System.out.println("So Dan : " + soDan + " selected : " + selected);

            /**
             * Them Dong nay de han che so buoc tinh cua bot
             */
            soBuocDaTinh = soDan + soBuocDaTinh;
            if (soDan + soBuocDaTinh > SO_BUOC_TINH) 
                return gameState;

            chuyenNhaKe(buocDi.direc);

            raiQuan(soDan, buocDi.direc);

//            System.out.println("Result Check : " + checkFinalHouse(this.selected, buocDi.direc));
            if (checkFinalHouse(this.selected, buocDi.direc) != 1) {
                break;
            }
        }

        // TH An Dan
        while (checkFinalHouse(this.selected, buocDi.direc) == 2) {
            this.eating = true;
            chuyenNhaKe(buocDi.direc);

            An(buocDi, this.selected);
            // Chuyen den o tiep theo de xet
            chuyenNhaKe(buocDi.direc);
//            System.out.println("Selected : " + this.selected);
        }

        this.eating = false;

//        System.out.println("Continue Check : " + checkContinueGame(board));
        if (!checkContinueGame(board)) {
            gameContinue = false;
            System.out.println("Dung choi");
        }

        // Trong Truong hop khong du quan de rai khi het dan trong cac o
        // Score < 5
        // thi game over
        // Sua Fake Token
        if (gameContinue && checkBoardPlayer(buocDi)) {
            if (!fakeThemDan(buocDi)) {
                gameContinue = false;
                System.out.println("Dung choi");

            }
        }

        return new GameState(buocDi, board, p1ShortLink, p2ShortLink, gameContinue);
    }

    /**
     * Set Ban Bang Game State
     */
    public void setBoardByGameState(GameState gameState) {
        for (int i = 1; i < 12; i++) {
            if (i == 6) {
                continue;
            }
            this.houseShortLink[i].danSo = gameState.houseSave[i];
        }
        this.q0ShortLink.danSo = gameState.q0Save.soDan;
        this.q0ShortLink.coQuan = gameState.q0Save.coQuan;
        this.q6ShortLink.danSo = gameState.q6Save.soDan;
        this.q6ShortLink.coQuan = gameState.q6Save.coQuan;
    }

    /**
     * Set Player By game state
     */
    public void setPlayerByGameState(GameState gameState) {
        p1ShortLink.soQuanAnDuoc = gameState.p1Save.soQuanAnDuoc;
        p1ShortLink.soDanAnDuoc = gameState.p1Save.soDanAnDuoc;
        p2ShortLink.soQuanAnDuoc = gameState.p2Save.soQuanAnDuoc;
        p2ShortLink.soDanAnDuoc = gameState.p2Save.soDanAnDuoc;
    }

    /**
     * Them Dan Trong Ham Calcuelate
     */
    public boolean fakeThemDan(Step buocDi) {
        if (nextTurnIsPlayer1(buocDi)) {

            // Tru diem cua player
            if (p1ShortLink.soDanAnDuoc < 5 && p1ShortLink.soQuanAnDuoc == 0) {
                return false;
            } else if (p2ShortLink.soDanAnDuoc < 5 && p1ShortLink.soQuanAnDuoc != 0 && p1ShortLink.soDanAnDuoc < 5) {
                return false;
            } else if (p1ShortLink.soQuanAnDuoc > 0 && p1ShortLink.soDanAnDuoc < 5) {
                p1ShortLink.soQuanAnDuoc--;
                p2ShortLink.soQuanAnDuoc++;
                p2ShortLink.soDanAnDuoc -= 5;
//                p2ShortLink.currentScore += 5;
                p1ShortLink.soDanAnDuoc += 5;

//                p1ShortLink.currentScore -= 5;
            }
            p1ShortLink.soDanAnDuoc -= 5;

            for (int i = 7; i <= 11; i++) {
                houseShortLink[i].setDanSo(1);
            }
            return true;

        } else {

            // Tru diem cua player
            if (p2ShortLink.soDanAnDuoc < 5 && p2ShortLink.soQuanAnDuoc == 0) {
                return false;
            } else if (p1ShortLink.soDanAnDuoc < 5 && p2ShortLink.soDanAnDuoc < 5 && p2ShortLink.soQuanAnDuoc != 0) {
                return false;
            } else if (p2ShortLink.soDanAnDuoc < 5 && p2ShortLink.soQuanAnDuoc != 0) {
                p2ShortLink.soQuanAnDuoc--;
                p1ShortLink.soQuanAnDuoc++;
                p1ShortLink.soDanAnDuoc -= 5;
//                p1ShortLink.currentScore += 5;
                p2ShortLink.soDanAnDuoc += 5;

//                p2ShortLink.currentScore -= 5;
            }
            p2ShortLink.soDanAnDuoc -= 5;
            for (int i = 1; i <= 5; i++) {
                houseShortLink[i].setDanSo(1);
            }
            return true;

        }
    }

    /**
     * Lay quan va set so quan la 0
     *
     * @param selected => O Dang xet
     * @return soQuan nhan duoc
     */
    public int layQuan(int selected) {
        int danSo = houseShortLink[selected].getDanSo();
        houseShortLink[selected].setDanSo(0);
//        repaint();
        return danSo;
    }

    public void chuyenNhaKe(int direction) {
        this.selected = tangNhaKe(direction, this.selected);
    }

    public int tangNhaKe(int direction, int current) {
        if (direction == 1) {
            if (current == 11) {
                current = 0;
            } else {
                current++;
            }
        } else if (current == 0) {
            current = 11;
        } else {
            current--;
        }

        return current;
    }

    /**
     * Lay So Dan va dai den khi het
     *
     * @param soDan => So Quan de rai
     * @param direction => Huong rai quan
     * @param selected => O Duoc chon
     * @return O Cuoi Cung khi het quan (so quan con lai de rai = 0)
     */
    public void raiQuan(int soDan, int direction) {

        for (int i = soDan; i > 0; i--) {

            // neu vao quan 0
            if (this.selected == 0) {
                q0ShortLink.tangDanSo();
            } // neu vao quan 6
            else if (this.selected == 6) {
                q6ShortLink.tangDanSo();
            } // neu khong vao quan
            else {
                houseShortLink[this.selected].tangDanSo();
            }
            chuyenNhaKe(direction);
        }

    }

    /**
     * Kiem tra o ket thuc
     *
     * @param selected
     * @param direction => Huong di de xet theo truong hop
     * @return 0 => Dung (2 O Trong Hoac Quan) 1 => Choi Tiep (O Co chua soi va
     * khong la quan) 2 => An Dan (O Trong sau do la 1 o dan co quan) 3 => An
     * Quan
     */
    public int checkFinalHouse(int current, int direction) {
        int checked;
        // vao o quan
        if (current == 0 || current == 6) {
            if (this.eating) {
                checked = tangNhaKe(direction, current);
                if (checkEmpty(checked)) {
                    int checked_next = tangNhaKe(direction, checked);
                    if (checkEmpty(checked_next)) {
                        return 0;
                    }
                    return 2;
                }
                return 0;
            }
            return 0;
        }

        // Khong Gap O Quan
        // So Dan khac 0 Choi Tiep
        if (houseShortLink[current].getDanSo() != 0) {
            return 1;
        }

        // So dan o do = 0
        // So dan o tiep theo 
        checked = tangNhaKe(direction, current);
        // = 0 Dung
        // != 0 An
        if (checkEmpty(checked)) {
            return 0;
        } else {
            if (checked == 0 && q0ShortLink.coQuan && q0ShortLink.getDanSo() < 5) {
                return 0;
            }
            if (checked == 6 && q6ShortLink.coQuan && q6ShortLink.getDanSo() < 5) {
                return 0;
            }
            return 2;
        }
    }

    /**
     * Kiem tra nha trong hay khong
     *
     * @param current
     * @return
     */
    public boolean checkEmpty(int current) {
        if (current == 0) {
            if (q0ShortLink.coQuan || (q0ShortLink.getDanSo() != 0)) {
                return false;
            }
            return true;
        }
        if (current == 6) {
            if (q6ShortLink.coQuan || (q6ShortLink.getDanSo() != 0)) {
                return false;
            }
            return true;
        }
        if (houseShortLink[current].getDanSo() != 0) {
            return false;
        }
        return true;
    }

    public void An(Step buocDi, int current) {
        if (current == 0 || current == 6) {
            AnQuan(buocDi, current);
        } else {
            AnDan(buocDi, current);
        }
    }

    /**
     * An Dan
     *
     * @param selected
     * @param direction => Chieu an quan
     * @return
     */
    public void AnDan(Step buocDi, int current) {

        if (isPlayer1(buocDi)) {
            int soQuanAn = layQuan(current);
            p1ShortLink.currentScore += soQuanAn;
            p1ShortLink.soDanAnDuoc += soQuanAn;
        } else {
            int soQuanAn = layQuan(current);
            p2ShortLink.currentScore += soQuanAn;
            p2ShortLink.soDanAnDuoc += soQuanAn;
        }

    }

    public void AnQuan(Step buocDi, int current) {

        // Quan 0
        if (current == 0) {
            if (isPlayer1(buocDi)) {
                p1ShortLink.currentScore = p1ShortLink.currentScore + q0ShortLink.getDanSo();
                p1ShortLink.soDanAnDuoc += q0ShortLink.getDanSo();
                q0ShortLink.setDanSo(0);
                if (q0ShortLink.coQuan) {
                    q0ShortLink.coQuan = false;
                    p1ShortLink.currentScore += 10;
                    p1ShortLink.soQuanAnDuoc++;
                }
            } else {
                p2ShortLink.currentScore = p2ShortLink.currentScore + q0ShortLink.getDanSo();
                p2ShortLink.soDanAnDuoc += q0ShortLink.getDanSo();
                q0ShortLink.setDanSo(0);
                if (q0ShortLink.coQuan) {
                    q0ShortLink.coQuan = false;
                    p2ShortLink.currentScore += 10;
                    p2ShortLink.soQuanAnDuoc++;
                }
            }
        } // Quan 6
        else if (isPlayer1(buocDi)) {
            p1ShortLink.currentScore = p1ShortLink.currentScore + q6ShortLink.getDanSo();
            p1ShortLink.soDanAnDuoc += q6ShortLink.getDanSo();

            q6ShortLink.setDanSo(0);
            if (q6ShortLink.coQuan) {
                q6ShortLink.coQuan = false;
                p1ShortLink.currentScore += 10;
                p1ShortLink.soQuanAnDuoc++;
            }
        } else {
            p2ShortLink.currentScore = p2ShortLink.currentScore + q6ShortLink.getDanSo();
            p2ShortLink.soDanAnDuoc += q6ShortLink.getDanSo();

            q6ShortLink.setDanSo(0);
            if (q6ShortLink.coQuan) {
                q6ShortLink.coQuan = false;
                p2ShortLink.currentScore += 10;
                p2ShortLink.soQuanAnDuoc++;
            }
        }

    }

    public boolean checkContinueGame(Board board) {
        if (checkEmpty(0) && checkEmpty(6)) {
            return false;
        }
        return true;
    }

    public void tinhDiem() {
        for (int i = 1; i <= 5; i++) {
            p2ShortLink.soDanAnDuoc += houseShortLink[i].getDanSo();
        }

        for (int i = 7; i <= 11; i++) {
            p1ShortLink.soDanAnDuoc += houseShortLink[i].getDanSo();
        }
    }

    public boolean isPlayer1(Step buocDi) {
        if (buocDi.chose > 6) {
            return true;
        }
        return false;
    }

    /**
     * Turn tiep theo la player 1
     *
     * @param token
     * @return true neu dung false neu sai
     */
    public boolean nextTurnIsPlayer1(Step buocDi) {
        return buocDi.chose < 6;
    }

    // set token cho luot tiep theo
    public void setTurnToken(Step buocDi) {

        if (isPlayer1(buocDi)) {
            game.turnToken = 5;
        } else {
            game.turnToken = 6;
        }
    }

    public int setFakeTurnToken(Step buocDi) {
        if (buocDi.chose < 6) {
            return 2;
        }
        return 1;
    }

    // kiem tra 
    // neu so quan tren ban het thi them
    public boolean checkBoardPlayer(Step buocDi) {
        boolean allEmpty = true;
        if (nextTurnIsPlayer1(buocDi)) {
            for (int i = 7; i <= 11; i++) {
                if (houseShortLink[i].getDanSo() != 0) {
                    allEmpty = false;
                }
            }
        } else {
            for (int i = 1; i < 6; i++) {
                if (houseShortLink[i].getDanSo() != 0) {
                    allEmpty = false;
                }
            }
        }
        return allEmpty;
    }

    /**
     * Them 1 dan vao moi o cua player co luot tiep
     *
     * @param token
     */
    public void themDan(Step buocDi) {
        if (nextTurnIsPlayer1(buocDi)) {

            // Tru diem cua player
            if (p1ShortLink.soDanAnDuoc < 5 && p1ShortLink.soQuanAnDuoc == 0) {
                this.game.turnToken = 0;
                return;
            } else if (p2ShortLink.soDanAnDuoc < 5 && p1ShortLink.soQuanAnDuoc != 0 && p1ShortLink.soDanAnDuoc < 5) {
                this.game.turnToken = 0;
                return;
            } else if (p1ShortLink.soQuanAnDuoc > 0 && p1ShortLink.soDanAnDuoc < 5) {
                p1ShortLink.soQuanAnDuoc--;
                p2ShortLink.soQuanAnDuoc++;
                p2ShortLink.soDanAnDuoc -= 5;
//                p2ShortLink.currentScore += 5;
                p1ShortLink.soDanAnDuoc += 5;

//                p1ShortLink.currentScore -= 5;
            }
            p1ShortLink.soDanAnDuoc -= 5;

            for (int i = 7; i <= 11; i++) {
                houseShortLink[i].setDanSo(1);
            }

        } else {

            // Tru diem cua player
            if (p2ShortLink.soDanAnDuoc < 5 && p2ShortLink.soQuanAnDuoc == 0) {
                this.game.turnToken = 0;
                return;
            } else if (p1ShortLink.soDanAnDuoc < 5 && p2ShortLink.soDanAnDuoc < 5 && p2ShortLink.soQuanAnDuoc != 0) {
                this.game.turnToken = 0;
                return;
            } else if (p2ShortLink.soDanAnDuoc < 5 && p2ShortLink.soQuanAnDuoc != 0) {
                p2ShortLink.soQuanAnDuoc--;
                p1ShortLink.soQuanAnDuoc++;
                p1ShortLink.soDanAnDuoc -= 5;
//                p1ShortLink.currentScore += 5;
                p2ShortLink.soDanAnDuoc += 5;

//                p2ShortLink.currentScore -= 5;
            }
            p2ShortLink.soDanAnDuoc -= 5;
            for (int i = 1; i <= 5; i++) {
                houseShortLink[i].setDanSo(1);
            }

        }
    }

    public void resetBuocDi() {
        p1ShortLink.resetBuocDi();
        p2ShortLink.resetBuocDi();
    }

    public void setToaDo() {
        if (this.direction == 1) {
            if (this.selected < 6 && this.selected != 0) {
                this.x = game.board.START_X + (this.selected - 2) * 100;
                this.y = game.board.START_Y;

            } else if (this.selected > 6) {
                this.x = game.board.START_X + (11 - this.selected + 1) * 100;
                this.y = game.board.START_Y + 102;
            } else if (this.selected == 0) {
                this.x = game.board.START_X;
                this.y = game.board.START_Y + 102;
//                this.x = game.board.START_X - 100;
//                this.y = game.board.START_Y + 50;
            } else {
                this.x = game.board.START_X + 400;
                this.y = game.board.START_Y;
//                this.x = game.board.START_X + 503;
//                this.y = game.board.START_Y + 50;
            }
        } else if (this.selected < 6 && this.selected != 0) {
            this.x = game.board.START_X + this.selected * 100;
            this.y = game.board.START_Y;

        } else if (this.selected > 6) {
            this.x = game.board.START_X + (11 - this.selected - 1) * 100;
            this.y = game.board.START_Y + 102;
        } else if (this.selected == 0) {
            this.x = game.board.START_X;
            this.y = game.board.START_Y;
//            this.x = game.board.START_X - 100;
//            this.y = game.board.START_Y + 50;
        } else {
            this.x = game.board.START_X + 400;
            this.y = game.board.START_Y + 102;
//            this.x = game.board.START_X + 503;
//            this.y = game.board.START_Y + 50;
        }
    }

    public void setToaDoLayQuan() {
        setToaDo();
        if (this.direction == 1) {
            if (this.selected < 6) {
                this.x += 100;
            } else {
                this.x -= 100;
            }
        } else if (this.selected > 6) {
            this.x += 100;
        } else {
            this.x -= 100;
        }
    }

    public void tangToaDo() {
        if (this.direction == -1) {
            this.count++;
            if (this.selected > 6) {
                this.x += 4;
            } else if (this.selected < 6 && this.selected != 0) {
                this.x -= 4;
            } else if (this.selected == 0 && !this.eating) {
                this.y += 4;
                this.x = game.board.START_X - (int) Math.sqrt(100 * 100 - Math.pow((this.y - game.board.START_Y - 102), 2));
            } else if (!this.eating && this.selected == 6) {
                this.y -= 4;
                this.x = game.board.START_X + 400 + (int) Math.sqrt(100 * 100 - Math.pow((this.y - game.board.START_Y), 2));
            } else if (this.selected == 0) {
                if (this.countAn == 0) {
                    this.x -= 4;
                } else {
                    this.y += 4;
                    this.x = game.board.START_X - (int) Math.sqrt(100 * 100 - Math.pow((this.y - game.board.START_Y - 102), 2));
                }
            } else if (this.selected == 6) {
                if (this.countAn == 0) {
                    this.x += 4;
                } else {
                    this.y -= 4;
                    this.x = game.board.START_X + 400 + (int) Math.sqrt(100 * 100 - Math.pow((this.y - game.board.START_Y), 2));
                }
            }
        } else {
            this.count++;
            if (this.selected > 6) {
                this.x -= 4;
            } else if (this.selected < 6 && this.selected != 0) {
                this.x += 4;
            } else if (this.selected == 0 && !this.eating) {
                this.y -= 4;
                this.x = game.board.START_X - (int) Math.sqrt(100 * 100 - Math.pow((this.y - game.board.START_Y), 2));
            } else if (!this.eating && this.selected == 6) {
                this.y += 4;
                this.x = game.board.START_X + 400 + (int) Math.sqrt(100 * 100 - Math.pow((this.y - game.board.START_Y - 102), 2));
            } else if (this.selected == 0) {
                if (this.countAn == 0) {
                    this.x -= 4;
                } else {
                    this.y -= 4;
                    this.x = game.board.START_X - (int) Math.sqrt(100 * 100 - Math.pow((this.y - game.board.START_Y), 2));
                }
            } else if (this.selected == 6) {
                if (this.countAn == 0) {
                    this.x += 4;
                } else {
                    this.y += 4;
                    this.x = game.board.START_X + 400 + (int) Math.sqrt(100 * 100 - Math.pow((this.y - game.board.START_Y - 102), 2));
                }
            }
        }
    }

    public void paint(Graphics2D g2d) {

        if (this.state == DOI) {
            if (this.thaoTacLay == 1) {
                g2d.drawImage(Game.voSoi, this.x, this.y - 50, null);
            } else if (this.thaoTacLay == 2) {
                g2d.drawImage(Game.choTayXuong, this.x, this.y - 50, null);
            }
        } else if (this.state == DI) {
            if (this.thaoTacTha) {
                g2d.drawImage(Game.voSoi, this.x, this.y - 50, null);
            } else {
                g2d.drawImage(Game.namTay, this.x, this.y - 50, null);
                g2d.setColor(Color.yellow);
                g2d.drawString(String.valueOf(danInHand), this.x + 43, this.y - 50 + 38);
            }

        } else if (this.state == KIEM_TRA) {
            if (this.thaoTacLay == 1) {
                g2d.drawImage(Game.voSoi, this.x, this.y - 50, null);
            } else if (this.thaoTacLay == 2) {
                g2d.drawImage(Game.choTayXuong, this.x, this.y - 50, null);
            }
        } else if (this.state == AN) {
            if (this.countAn == 1 && this.thaoTacAn != 0) {
                if (this.thaoTacAn == 1) {
                    g2d.drawImage(Game.voSoi1, this.x, this.y - 50, null);
                } else if (this.thaoTacAn == 2) {
                    g2d.drawImage(Game.voSoi2, this.x, this.y - 40, null);
                }
            } else {
                g2d.drawImage(Game.tayKhong, this.x, this.y - 50, null);
            }
        }

    }

}
