/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testSQuares2;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

/**
 *
 * @author VPC
 */
class Board {

    final int START_X = 304;
    final int START_Y = 280;
    int x, y;
    //10 nha` dan
    House[] houses = new House[12];

    //2 nha quan
    Villa q0;
    Villa q6;

    
    public Board() {
        x = START_X;
        y = START_Y;
        for (int i = 1; i <= 5; i++) {
            houses[i] = new House();
        }
        for (int i = 7; i <= 11; i++) {
            houses[i] = new House();
        }
        q0 = new Villa();
        q6 = new Villa();
        initBoard();
    }

    /*
    * Khoi tao san va set toa do cac nha`
     */
    public void initBoard() {

        //Nha dan player2
        for (int i = 1; i <= 5; i++) {
            houses[i].setHouse(i, x, y, 5);
            x += 100;
        }

        //Nha dan player1
        x = START_X;
        y = START_Y + 102;
        for (int i = 11; i >= 7; i--) {
            houses[i].setHouse(i, x, y, 5);
            x += 100;
        }

        //Set nha Quan
        q0.setVilla(0, START_X - 100, START_Y, true);
        q6.setVilla(6, START_X + 403, START_Y, true);
        
        //Test lỗi
//        houses[1].danSo = 0;
//        houses[2].danSo = 6;
//        houses[3].danSo = 0;
//        houses[4].danSo = 1;
//        houses[5].danSo = 0;
//        houses[7].danSo = 0;
//        houses[8].danSo = 2;
//        houses[9].danSo = 1;
//        houses[10].danSo = 3;
//        houses[11].danSo = 0;
//        q6.danSo = 5;
//        q0.danSo = 5;
    }

    //In điểm số
    public void paintScore(Graphics2D g2d, int danP1, int danP2, int quanP1, int quanP2) {
        g2d.setColor(Color.MAGENTA);
        g2d.setFont(new Font("Verdana", Font.BOLD, 100));
        g2d.drawString(standScore(danP1 + quanP1 * 5), 940, 678);
        g2d.drawString(standScore(danP2 + quanP2 * 5), 185, 188);
        if (danP1 > 0 && danP1 < 9) {
            g2d.drawImage(Game.soils[danP1], 726, 612, null);
        } else if (danP1 >= 9) {
            g2d.drawImage(Game.soils[8], 726, 612, null);
        }
        if (danP2 > 0 && danP2 < 9) {
            g2d.drawImage(Game.soils[danP2], 397, 116, null);
        } else if (danP2 >= 9) {
            g2d.drawImage(Game.soils[8], 397, 116, null);
        }
        if (quanP1 == 1) {
            g2d.drawImage(Game.anduoc1quan,815,640,  null);
        }
        else if (quanP1 == 2){
            g2d.drawImage(Game.anduoc2quan,785, 608,null);
        }
        if (quanP2 == 1) {
            g2d.drawImage(Game.anduoc1quan,477,131,  null);
        }
        else if (quanP2 == 2){
            g2d.drawImage(Game.anduoc2quan,454, 106,null);
        }
//        if (scoreP1 == 0); else {
//            if ((scoreP1 <= 7) && (scoreP1 >= 1)) {
//                g2d.drawImage(Game.soils[scoreP1], 726, 612, null);
//            } else {
//                g2d.drawImage(Game.soils[8], 726, 612, null);
//            }
//        }
//        if (scoreP2 == 0); else {
//            if ((scoreP2 <= 7) && (scoreP2 >= 1)) {
//                g2d.drawImage(Game.soils[scoreP2], 397, 116, null);
//            } else {
//                g2d.drawImage(Game.soils[8], 397, 116, null);
//            }
//        }

    }

    //Chuẩn hóa điểm số 5 => 05, 0 =>00
    private String standScore(int score) {
        if (score < 10) {
            return "0" + String.valueOf(score);
        } else {
            return String.valueOf(score);
        }
    }

    public void reset(){
        for (int i=1;i<=5;i++){
            houses[i].setDanSo(5);
        }
        for (int i=7;i<=11;i++){
            houses[i].setDanSo(5);
        }
        q0.setVilla(0,true);
        q6.setVilla(0, true);
        
    }
    
    //Ve lai san
    public void paint(Graphics2D g2d) {
        for (int i = 1; i <= 5; i++) {
            houses[i].paint(g2d);
        }
        for (int i = 7; i <= 11; i++) {
            houses[i].paint(g2d);
        }
        q0.paint(g2d);
        q6.paint(g2d);
    }
}
