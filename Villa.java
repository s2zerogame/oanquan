/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testSQuares2;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import javafx.scene.shape.Arc;

/**
 *
 * @author VPC
 */
public class Villa extends House {

    int quanID;
    int diemNhaQuan;
    public boolean coQuan;
    Shape shape = new Rectangle2D.Double(x, y, 65, 100);

    public Villa() {

    }

    public void setVilla(int ID, int x, int y, boolean coQuan) {
        this.quanID = ID;
        this.x = x;
        this.y = y;
        this.coQuan = coQuan;

    }

    public void setVilla(int pop,boolean coQuan){
        this.danSo = pop;
        this.coQuan = coQuan;
    }
    
    public void setQuanID(int ID) {
        this.quanID = ID;
    }

    public int getDiemSo() {
        if (coQuan) {
            return danSo + 5;
        } else {
            return danSo;
        }
    }

    public void paint(Graphics2D g2d) {
        g2d.setColor(Color.gray);
        g2d.setFont(new Font("Tw Cen MT Bold Italic", Font.BOLD, 40));
        //Vẽ Nhà Quan
        if (quanID == 6) {

            //Hiển thị số dân trong quan
            g2d.drawString(String.valueOf(this.getDanSo()), 880, 300);
            if (danSo == 0); else if ((danSo <= 7) && (danSo >= 1)) {
                g2d.drawImage(Game.soils[danSo], x-5 + 100, y + 30, null);
            } else {
                g2d.drawImage(Game.soils[8], x-5 + 100, y+30, null);
            }
            if (coQuan) {
                g2d.drawImage(Game.quan6, x + 100, y, null);
            }

        } else {
            g2d.drawString(String.valueOf(this.getDanSo()), 200, 470);
            if (danSo == 0); else if ((danSo <= 7) && (danSo >= 1)) {
                g2d.drawImage(Game.soils[danSo], x-5, y + 25, null);
            } else {
                g2d.drawImage(Game.soils[8], x-5, y + 25, null);
            }
            if (coQuan) {
                g2d.drawImage(Game.quan0, x - 100, y, null);
            }
        }

    }
}
