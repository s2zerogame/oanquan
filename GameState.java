/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testSQuares2;

/**
 *
 * @author Administrator
 */
public class GameState {

    VillaSave q0Save, q6Save; // Luu Quan
    int[] houseSave;    // Luu house
    Step buocDiTruoc;  // Luu buocdi
    int diemAnDuoc;    // Diem an dc sau khi di buocDiTruoc
    PlayerSave p1Save, p2Save; // luu player
    boolean gameContinue = true;

    public GameState() {
        this.q0Save = new VillaSave(0, true);
        this.q6Save = new VillaSave(0, true);
        this.houseSave = new int[12];
        for (int i = 1; i < 12; i++) {
            if (i == 6) continue;
            this.houseSave[i] = 5;
        }
        p1Save = new PlayerSave();
        p2Save = new PlayerSave();
        this.buocDiTruoc = new Step();
        this.gameContinue = true;
    }
    
    public GameState(Step buocDiTruoc, Board board, Player p1, Player p2, boolean gameContinue) {
        
        // luu buoc di
        this.buocDiTruoc = new Step();
        this.buocDiTruoc.chose = buocDiTruoc.chose;
        this.buocDiTruoc.direc = buocDiTruoc.direc;
        
        // luu Quan
        this.q0Save = new VillaSave(board.q0.getDanSo(), board.q0.coQuan);
        this.q6Save = new VillaSave(board.q6.getDanSo(), board.q6.coQuan);

        // luu house
        houseSave = new int[12];
        for (int i = 1; i < 6; i++) {
            houseSave[i] = board.houses[i].getDanSo();
        }
        for (int i = 7; i < 12; i++) {
            houseSave[i] = board.houses[i].getDanSo();
        }
        
        // luu diem player
        p1Save = new PlayerSave(p1.soDanAnDuoc, p1.soQuanAnDuoc);
        p2Save = new PlayerSave(p2.soDanAnDuoc, p2.soQuanAnDuoc);
        this.gameContinue = gameContinue;
    }

    public GameState(Game game, Step buocDiTruoc, boolean gameContinue) {
        
        // luu buoc di
        this.buocDiTruoc = new Step();
        this.buocDiTruoc.chose = buocDiTruoc.chose;
        this.buocDiTruoc.direc = buocDiTruoc.direc;
        
        // luu Quan
        this.q0Save = new VillaSave(game.board.q0.getDanSo(), game.board.q0.coQuan);
        this.q6Save = new VillaSave(game.board.q6.getDanSo(), game.board.q6.coQuan);

        // luu house
        houseSave = new int[12];
        for (int i = 1; i < 6; i++) {
            houseSave[i] = game.board.houses[i].getDanSo();
        }
        for (int i = 7; i < 12; i++) {
            houseSave[i] = game.board.houses[i].getDanSo();
        }
        
        // luu diem player
        p1Save = new PlayerSave(game.p1.soDanAnDuoc, game.p1.soQuanAnDuoc);
        p2Save = new PlayerSave(game.p2.soDanAnDuoc, game.p2.soQuanAnDuoc);
        this.gameContinue = gameContinue;
    }
}

class VillaSave {

    int soDan;
    boolean coQuan;

    public VillaSave() {

    }

    public VillaSave(int soDan, boolean coQuan) {
        this.soDan = soDan;
        this.coQuan = coQuan;
    }

}

class PlayerSave {
    int soQuanAnDuoc;
    int soDanAnDuoc;
    
    public PlayerSave() {
        
    }

    public PlayerSave(int soDanAnDuoc, int soQuanAnDuoc) {
        this.soQuanAnDuoc = soQuanAnDuoc;
        this.soDanAnDuoc = soDanAnDuoc;

    }
 
}
