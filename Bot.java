/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testSQuares2;

import java.awt.Point;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author VPC
 */
public class Bot extends Player {

    int level;
    TrongTai fakeTrongTai;
    GameState lastGameState, tempResult;
    Step tryStep, bestStep;
    ArrayList<Step> goodSteps;
    int anDuoc, maxAnDuoc;
    boolean p1_use_greedy = false;

    public Bot() {
        fakeTrongTai = new TrongTai();
        level = 0;         //Mặc định là ng chơi
    }

    public void InitBot(Game game, int botLevel, int play_side) {
        this.game = game;
        this.board = game.board;
        this.level = botLevel;
        this.playerSide = play_side;
        this.historyShape = History.shape;
        buocDi = new Step();
        soDan = 0;
        anQuan = 0;
        direction = 0;
        chosenHouse = 0;
        random = new Random();
        houseCoDan = new boolean[12];
        goodSteps = new ArrayList<Step>();
    }

    public void turn(long gameTime, Point mousePosition) {
        switch (level) {
            case 0:
                super.turn(gameTime, mousePosition);
                break;
            case 1:
                super.auto();
                break;
            case 2:
                bot_2();
                break;
            case 3:
                bot_3();
                break;
            case 4:
                bot_4();
                break;
            case 5:
                {
                    try {
                        bot_5();
                    } catch (SQLException ex) {
                        Logger.getLogger(Bot.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(Bot.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            break;
        }

    }

    //Thuat toan cho may thuong
    // Them bot2
    void bot_2() {
        GameState tempLastGameState = game.history.lastGameState();
        GameState tempResult;
        super.checkBoard(tempLastGameState);
        boolean checkHouse[] = new boolean[12];
        for (int i = 1; i <= 11; i++) {
            checkHouse[i] = this.houseCoDan[i];
        }
        int tempmaxAnDuoc = 0;
        int tempAnDuoc = 0;
        Step tempBestStep = super.randomStep();
        Step tempTryStep;
        if (this.playerSide == 2) {
            for (int i = 1; i <= 5; i++) {      //Xét từng nhà
                if (checkHouse[i]) {            //Có dân
                    for (int j = 1; j >= -1; j -= 2) {      //Chọn 2 hướng 1 và -1
                        tempTryStep = new Step(i, j);
                        tempResult = fakeTrongTai.calculateBot2(tempTryStep, tempLastGameState);
                        tempAnDuoc = (tempResult.p2Save.soDanAnDuoc + tempResult.p2Save.soQuanAnDuoc * 5)
                                - (tempLastGameState.p2Save.soDanAnDuoc + tempLastGameState.p2Save.soQuanAnDuoc * 5);
                        if (tempAnDuoc > tempmaxAnDuoc) {
                            tempmaxAnDuoc = tempAnDuoc;
                            tempBestStep = tempTryStep;
                        }
                    }
                }
            }
            System.out.println("MAx An duoc : " + tempmaxAnDuoc);
            GameState result = fakeTrongTai.calculateBot2(tempBestStep, tempLastGameState);
            System.out.println("Buoc Di : " + tempBestStep.chose + " Direction : " + tempBestStep.direc);
            System.out.println("So Dan Quan 0 last State : " + tempLastGameState.q0Save.soDan);
            System.out.println("So Dan Quan 6 last State : " + tempLastGameState.q6Save.soDan);
            System.out.println("So Dan Quan 0 sau khi di " + result.q0Save.soDan);
            System.out.println("So Dan Quan 6 sau khi di " + result.q6Save.soDan);
        } else if (this.playerSide == 1) {
            for (int i = 7; i <= 11; i++) {      //Xét từng nhà
                if (checkHouse[i]) {            //Có dân
                    for (int j = 1; j >= -1; j -= 2) {      //Chọn 2 hướng 1 và -1
                        tempTryStep = new Step(i, j);
                        tempResult = fakeTrongTai.calculateBot2(tempTryStep, tempLastGameState);
                        tempAnDuoc = (tempResult.p1Save.soDanAnDuoc + tempResult.p1Save.soQuanAnDuoc * 5)
                                - (tempLastGameState.p1Save.soDanAnDuoc + tempLastGameState.p1Save.soQuanAnDuoc * 5);
                        if (tempAnDuoc > tempmaxAnDuoc) {
                            tempmaxAnDuoc = tempAnDuoc;
                            tempBestStep = tempTryStep;
                        }
                    }
                }
            }
        }
        //bestStep = goodSteps.get(random.nextInt(goodSteps.size()));  //Random 1 step bat kì trong mảng
        this.bestStep = tempBestStep;
        thucHienBuocDi();
    }

    //Thuat toan may kho - Greedy
    void testbot_3() {
        super.checkBoard();
        maxAnDuoc = 0;
        //bestStep = super.randomStep();
        goodSteps.clear();
        goodSteps.add(super.randomStep());
        lastGameState = game.history.lastGameState();
        if (this.playerSide == 2) {
            for (int i = 1; i <= 5; i++) {      //Xét từng nhà
                if (houseCoDan[i]) {            //Có dân
                    for (int j = 1; j >= -1; j -= 2) {      //Chọn 2 hướng 1 và -1
                        tryStep = new Step(i, j);
                        tempResult = fakeTrongTai.handleCalculate(tryStep, lastGameState);
                        anDuoc = (tempResult.p2Save.soDanAnDuoc + tempResult.p2Save.soQuanAnDuoc * 5)
                                - (lastGameState.p2Save.soDanAnDuoc + lastGameState.p2Save.soQuanAnDuoc * 5);
                        if (anDuoc > maxAnDuoc) {
                            maxAnDuoc = anDuoc;
                            //bestStep = tryStep;
                            goodSteps.clear();    //Xoa ca mang dc chọn
                            goodSteps.add(tryStep);   //THêm tryStep vào mang?
                        } else if (anDuoc == maxAnDuoc) {
                            goodSteps.add(tryStep);     //Them vao`
                        }
                    }
                }
            }
//            System.out.println("MAx An duoc : " + maxAnDuoc);
//            System.out.println("Size of stack: " + goodSteps.size());

        } else if (this.playerSide == 1) {
            for (int i = 7; i <= 11; i++) {      //Xét từng nhà
                if (houseCoDan[i]) {            //Có dân
                    for (int j = 1; j >= -1; j -= 2) {      //Chọn 2 hướng 1 và -1
                        tryStep = new Step(i, j);
                        tempResult = fakeTrongTai.handleCalculate(tryStep, lastGameState);
                        anDuoc = (tempResult.p1Save.soDanAnDuoc + tempResult.p1Save.soQuanAnDuoc * 5)
                                - (lastGameState.p1Save.soDanAnDuoc + lastGameState.p1Save.soQuanAnDuoc * 5);
                        if (anDuoc > maxAnDuoc) {
                            maxAnDuoc = anDuoc;
                            //bestStep = tryStep;
                            goodSteps.clear();    //Xoa ca mang dc chọn
                            goodSteps.add(tryStep);   //THêm tryStep vào mang?
                        } else if (anDuoc == maxAnDuoc) {
                            goodSteps.add(tryStep);     //Them vao`
                        }
                    }
                }
            }
            System.out.println("MAx An duoc : " + maxAnDuoc);
            System.out.println("Size of stack: " + goodSteps.size());

        }
        bestStep = goodSteps.get(random.nextInt(goodSteps.size()));  //Random 1 step bat kì trong mảng
        this.buocDi = bestStep;
        board.houses[this.buocDi.chose].chosen = true;
        board.houses[this.buocDi.chose].chosenSide = this.buocDi.direc;
        giveTurnToken(playerSide);
    }

    public void bot_3() {
        lastGameState = game.history.lastGameState();
        goodSteps = greedy(lastGameState, this.playerSide);
        bestStep = goodSteps.get(random.nextInt(goodSteps.size()));
        thucHienBuocDi();
    }

    public Step greedyMinimax(GameState gs, int playerSide) {
        GameState tempLastGameState = gs;
        super.checkBoard(gs);
        boolean checkHouse[] = new boolean[12];
        for (int i = 1; i <= 11; i++) {
            checkHouse[i] = this.houseCoDan[i];
        }
        int anDuoc1;
        int chenhLechAnDuoc = 0;
        int tempMaxAnDuoc = -60;
        int enemyGain;
        Step greedyMinimax = this.randomStep();
        Step firstStep, enemyStep;
        ArrayList<Step> myGoodSteps, enemyGreedySteps;
        GameState tempResult1, tempResult2;
        myGoodSteps = greedy(tempLastGameState, 2);
        for (int i = 0; i < myGoodSteps.size(); i++) {
            firstStep = myGoodSteps.get(i);
            tempResult1 = fakeTrongTai.handleCalculate(firstStep, tempLastGameState);
            anDuoc1 = (tempResult1.p2Save.soDanAnDuoc + tempResult1.p2Save.soQuanAnDuoc * 5)
                    - (tempLastGameState.p2Save.soDanAnDuoc + tempLastGameState.p2Save.soQuanAnDuoc * 5);
            //Đối thủ tính toán đưa ra các bước đi tham lam
            enemyGreedySteps = greedy(tempResult1, 1);
            if (enemyGreedySteps != null) {
                enemyStep = enemyGreedySteps.get(random.nextInt(enemyGreedySteps.size()));
                tempResult2 = fakeTrongTai.handleCalculate(enemyStep, tempResult1); //TÍnh lại trạng thái sân
                enemyGain = (tempResult2.p1Save.soDanAnDuoc + tempResult2.p1Save.soQuanAnDuoc * 5)
                        - (tempResult1.p1Save.soDanAnDuoc + tempResult1.p1Save.soQuanAnDuoc * 5);
                chenhLechAnDuoc = anDuoc1 - enemyGain;
            } else {
                chenhLechAnDuoc = anDuoc1;
            }
            if (chenhLechAnDuoc > tempMaxAnDuoc) {
                tempMaxAnDuoc = chenhLechAnDuoc;
                greedyMinimax = firstStep;
            }
        }
//        System.out.println("greedyMinimax!");
        return greedyMinimax;
    }

    public void thucHienBuocDi() {
        this.buocDi = bestStep;
        board.houses[this.buocDi.chose].chosen = true;
        board.houses[this.buocDi.chose].chosenSide = this.buocDi.direc;
        giveTurnToken(playerSide);
    }

    public boolean p2_dectect_p1_greedy(History gamehistory) {
        History hs = gamehistory;
        boolean foundGreedy[] = new boolean[6];
        ArrayList<Step> greedy_Steps = new ArrayList<>();
        for (int i = 1; i <= 5; i += 2) {
            System.out.println("GameState " + i + ": " + hs.stack.get(i));
            greedy_Steps = greedy(hs.stack.get(i), 1);
//            System.out.println("Size of p1 greedy_Steps: " + greedy_Steps.size() + " GameState =" + i);
//            System.out.println("Buoc DI truoc: (" + hs.stack.get(i + 1).buocDiTruoc.chose + ", " + hs.stack.get(i + 1).buocDiTruoc.direc + ")");
            for (int j = 0; j < greedy_Steps.size(); j++) {
                if ((greedy_Steps.get(j).chose == hs.stack.get(i + 1).buocDiTruoc.chose)
                        && (greedy_Steps.get(j).direc == hs.stack.get(i + 1).buocDiTruoc.direc)) {
                    foundGreedy[i] = true;
                    System.out.println("PHAT HIEN THAM LAMMMMMM!");
                }
            }
        }
        //Xet 3 GameState 1,3,5 xem co tham lam k
        for (int i = 1; i <= 5; i += 2) {
            if (foundGreedy[i] == false) {
                return false;
            }
        }
        return true;
    }

    public boolean p2_dectect_p1_greedy_2(History gamehistory) {
        History hs = gamehistory;
        boolean foundGreedy = false;
        ArrayList<Step> greedy_Steps = new ArrayList<>();
            greedy_Steps = greedy(hs.stack.get(7), 1);
            for (int j = 0; j < greedy_Steps.size(); j++) {
                if ((greedy_Steps.get(j).chose == hs.stack.get(8).buocDiTruoc.chose)
                        && (greedy_Steps.get(j).direc == hs.stack.get(8).buocDiTruoc.direc)) {
                    foundGreedy = true;
                }
            }
        return foundGreedy;
    }
    //Bot 4 với giải thuật phát hiện đối phương dùng tham lam
    public void bot_4() {
        if (game.history.stack.size() == 7) {
            if (p2_dectect_p1_greedy(game.history)) {
                p1_use_greedy = true;
                System.out.println("phat hien greedy!");
            }
        }
        if (game.history.stack.size() == 9) {
            if (p2_dectect_p1_greedy_2(game.history)) {
                p1_use_greedy = true;
                System.out.println("phat hien greedy!");
            }
            else p1_use_greedy = false;
        }
        if (p1_use_greedy) {
            bestStep = minimax_Counter_Greedy(game.history.lastGameState(), 2);
            System.out.println("p1 tham lam:" + p1_use_greedy + ". Use: minimax_Counter_Greedy.");
        } else {
            bestStep = greedyMinimax(game.history.lastGameState(), 2);
            System.out.println("Use: greedyMinimax");
        }
        thucHienBuocDi();
    }

    public Step minimax_Counter_Greedy(GameState gs, int playerSide) {
        GameState tempLastGameState = gs;
        super.checkBoard(gs);
        boolean checkHouse[] = new boolean[12];
        for (int i = 1; i <= 11; i++) {
            checkHouse[i] = this.houseCoDan[i];
        }
        int anDuoc1;
        int chenhLechAnDuoc = 0;
        int tempMaxAnDuoc = 0;
        int enemyGain;
        Step counterGreedy = this.randomStep();
        Step firstStep, enemyStep;
        ArrayList<Step> myGoodSteps, enemyGreedySteps;
        GameState tempResult1, tempResult2;
        if (playerSide == 2) {
            for (int i = 1; i <= 5; i++) {
                if (checkHouse[i]) {
                    for (int j = 1; j >= -1; j -= 2) {
                        firstStep = new Step(i, j);
                        tempResult1 = fakeTrongTai.handleCalculate(firstStep, tempLastGameState);
                        anDuoc1 = (tempResult1.p2Save.soDanAnDuoc + tempResult1.p2Save.soQuanAnDuoc * 5)
                                - (tempLastGameState.p2Save.soDanAnDuoc + tempLastGameState.p2Save.soQuanAnDuoc * 5);

                        //Đối thủ tính toán đưa ra các bước đi tham lam
                        enemyGreedySteps = greedy(tempResult1, 1);
                        if (enemyGreedySteps != null) {
                            enemyStep = enemyGreedySteps.get(random.nextInt(enemyGreedySteps.size()));
                            tempResult2 = fakeTrongTai.handleCalculate(enemyStep, tempResult1); //TÍnh lại trạng thái sân
                            enemyGain = (tempResult2.p1Save.soDanAnDuoc + tempResult2.p1Save.soQuanAnDuoc * 5)
                                    - (tempResult1.p1Save.soDanAnDuoc + tempResult1.p1Save.soQuanAnDuoc * 5);
                            chenhLechAnDuoc = anDuoc1 - enemyGain;
                        } else {
                            chenhLechAnDuoc = anDuoc1;
                        }
                        if (chenhLechAnDuoc > tempMaxAnDuoc) {
                            tempMaxAnDuoc = chenhLechAnDuoc;
                            counterGreedy = firstStep;
                        }
                    }
                }
            }
        }
        return counterGreedy;
    }

    public ArrayList<Step> greedy(GameState gs, int playerSide) {
        GameState tempLastGameState = gs;
        GameState GtempResult = new GameState();
        super.checkBoard(gs);
        boolean checkHouse[] = new boolean[12];
        for (int i = 1; i <= 11; i++) {
            checkHouse[i] = this.houseCoDan[i];
        }
        int tempmaxAnDuoc = 0;
        int GanDuoc = 0;
        ArrayList<Step> tempGoodSteps = new ArrayList<>();
        tempGoodSteps.clear();
        tempGoodSteps.add(super.randomStep());
        Step GtryStep = new Step();
        if (playerSide == 2) {
            for (int i = 1; i <= 5; i++) {      //Xét từng nhà
                if (checkHouse[i]) {            //Có dân
                    for (int j = 1; j >= -1; j -= 2) {      //Chọn 2 hướng 1 và -1
                        GtryStep = new Step(i, j);
                        GtempResult = fakeTrongTai.handleCalculate(GtryStep, tempLastGameState);
                        GanDuoc = (GtempResult.p2Save.soDanAnDuoc + GtempResult.p2Save.soQuanAnDuoc * 5)
                                - (tempLastGameState.p2Save.soDanAnDuoc + tempLastGameState.p2Save.soQuanAnDuoc * 5);
                        if (GanDuoc > tempmaxAnDuoc) {
                            tempmaxAnDuoc = GanDuoc;
                            //bestStep = tryStep;
                            tempGoodSteps.clear();    //Xoa ca mang dc chọn
                            tempGoodSteps.add(GtryStep);   //THêm tryStep vào mang?
                        } else if (GanDuoc == tempmaxAnDuoc) {
                            tempGoodSteps.add(GtryStep);     //Them vao`
                        }
                    }
                }
            }
            System.out.println("MAx An duoc : " + tempmaxAnDuoc);
            System.out.println("Size of greedySteps: " + tempGoodSteps.size());

        } else if (playerSide == 1) {
            for (int i = 7; i <= 11; i++) {      //Xét từng nhà
                if (checkHouse[i]) {            //Có dân
                    for (int j = 1; j >= -1; j -= 2) {      //Chọn 2 hướng 1 và -1
                        GtryStep = new Step(i, j);
                        GtempResult = fakeTrongTai.handleCalculate(GtryStep, tempLastGameState);
                        GanDuoc = (GtempResult.p1Save.soDanAnDuoc + GtempResult.p1Save.soQuanAnDuoc * 5)
                                - (tempLastGameState.p1Save.soDanAnDuoc + tempLastGameState.p1Save.soQuanAnDuoc * 5);
                        if (GanDuoc > tempmaxAnDuoc) {
                            tempmaxAnDuoc = GanDuoc;
                            //bestStep = tryStep;
                            tempGoodSteps.clear();    //Xoa ca mang dc chọn
                            tempGoodSteps.add(GtryStep);   //THêm tryStep vào mang?
                        } else if (GanDuoc == tempmaxAnDuoc) {
                            tempGoodSteps.add(GtryStep);     //Them vao`
                        }
                    }
                }
            }
            System.out.println("MAx An duoc : " + tempmaxAnDuoc);
            System.out.println("Size of GreedySteps: " + tempGoodSteps.size());

        }

        return tempGoodSteps;
    }

    public ArrayList<Step> firstgreedy(GameState gs, int playerSide) {
        if (gs.gameContinue == false) {
            return null;
        }
        GameState tempLastGameState = gs;
        super.checkBoard(gs);
        boolean checkHouse[] = new boolean[12];
        for (int i = 1; i <= 11; i++) {
            checkHouse[i] = this.houseCoDan[i];
        }
        int tempmaxAnDuoc = 0;
        //bestStep = super.randomStep();
        ArrayList<Step> tempGoodSteps = new ArrayList<>();
        tempGoodSteps.clear();
        tempGoodSteps.add(super.randomStep());
        Step gTryStep;
        GameState gtempResult;
        int gAnDuoc = 0;
        if (playerSide == 2) {
            for (int i = 1; i <= 5; i++) {      //Xét từng nhà
                if (checkHouse[i]) {            //Có dân
                    for (int j = 1; j >= -1; j -= 2) {      //Chọn 2 hướng 1 và -1
                        gTryStep = new Step(i, j);
                        gtempResult = fakeTrongTai.handleCalculate(gTryStep, tempLastGameState);
                        gAnDuoc = (gtempResult.p2Save.soDanAnDuoc + gtempResult.p2Save.soQuanAnDuoc * 5)
                                - (tempLastGameState.p2Save.soDanAnDuoc + tempLastGameState.p2Save.soQuanAnDuoc * 5);
                        if (gAnDuoc > tempmaxAnDuoc) {
                            tempmaxAnDuoc = gAnDuoc;
                            //bestStep = tryStep;
                            tempGoodSteps.clear();    //Xoa ca mang dc chọn
                            tempGoodSteps.add(gTryStep);   //THêm tryStep vào mang?
                        } else if (gAnDuoc == tempmaxAnDuoc) {
                            tempGoodSteps.add(gTryStep);     //Them vao`
                        }
                    }
                }
            }
        } else if (playerSide == 1) {
            for (int i = 7; i <= 11; i++) {      //Xét từng nhà
                if (checkHouse[i]) {            //Có dân
                    for (int j = 1; j >= -1; j -= 2) {      //Chọn 2 hướng 1 và -1
                        gTryStep = new Step(i, j);
                        gtempResult = fakeTrongTai.handleCalculate(gTryStep, tempLastGameState);
                        tempmaxAnDuoc = (gtempResult.p1Save.soDanAnDuoc + gtempResult.p1Save.soQuanAnDuoc * 5)
                                - (tempLastGameState.p1Save.soDanAnDuoc + tempLastGameState.p1Save.soQuanAnDuoc * 5);
                        if (gAnDuoc > tempmaxAnDuoc) {
                            tempmaxAnDuoc = gAnDuoc;
                            //bestStep = tryStep;
                            tempGoodSteps.clear();    //Xoa ca mang dc chọn
                            tempGoodSteps.add(gTryStep);   //THêm tryStep vào mang?
                        } else if (gAnDuoc == tempmaxAnDuoc) {
                            tempGoodSteps.add(gTryStep);     //Them vao`
                        }
                    }
                }
            }
        }

        System.out.println("MAx An duoc : " + tempmaxAnDuoc);
        System.out.println("Size of stack: " + tempGoodSteps.size());
        return tempGoodSteps;
    }

    public boolean p2_dectect_p1_greedy_byGameState(History gamehistory) {
        History hs = gamehistory;
        if (hs.stack.size() < 6) {
            return false;
        }
        ArrayList<Step> greedy_Steps = new ArrayList<>();
        boolean foundGreedy[] = new boolean[6];
        for (int i = 1; i <= 5; i += 2) {
            greedy_Steps = greedy(hs.stack.get(i), 1);
            for (int j = 0; j < greedy_Steps.size(); j++) {
                if (hs.compare2GameState(fakeTrongTai.handleCalculate(greedy_Steps.get(j), hs.stack.get(i)), hs.stack.get(i + 1)) == true) {
                    foundGreedy[i] = true;
                    break;
                }
            }
        }
        for (int i = 1; i <= 5; i += 2) {
            if (foundGreedy[i]) {
                return true;
            }
        }
        return false;
    }

    public void bot_5() throws SQLException, ClassNotFoundException{
        Step stepFromDB;
        stepFromDB = Framework.historyRespository.ultimate_bot(game.history.lastGameState());
        if(stepFromDB!=null){
            this.checkBoard(game.history.lastGameState());
            if(houseCoDan[stepFromDB.chose]){
                bestStep = stepFromDB;
                thucHienBuocDi();
            }
        }
        else bot_4();
    }
    
    public void resetBot() {
        super.resetPlayer();
        this.p1_use_greedy = false;
    }
}
