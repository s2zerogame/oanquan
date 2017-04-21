/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testSQuares2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.ietf.jgss.GSSName;

/**
 *
 * @author Administrator
 */
public class HistoryRespository {

    protected Connection dbConn;

    public HistoryRespository() throws SQLException, ClassNotFoundException {
        this.dbConn = ConnectionUtils.getMyConnection();
    }

    
    public void saveHistory(History history) throws SQLException, ClassNotFoundException {

        GameStateRespository gsR = new GameStateRespository(this.dbConn);
        StepRepository stR = new StepRepository(this.dbConn);
        int id_game_state,id_step;
        int numRecord = 0;
        numRecord = history.stack.size();
        for (int i = 1; i < numRecord-1; i++) {
            id_game_state = gsR.checkExist(history.stack.get(i));
            if (id_game_state != 0) {
//                System.out.println("exist id_game_state = "+id_game_state);
                id_step = stR.checkExist(id_game_state, history.stack.get(i+1).buocDiTruoc); 
                if(id_step!=0){
//                    System.out.println("exist id_step = "+id_step);
                    if(history.stack.get(i+1).buocDiTruoc.win == 1){
                        stR.updateStep(id_step, history.stack.get(i+1).buocDiTruoc, true);
                    }
                    else{
                        stR.updateStep(id_step, history.stack.get(i+1).buocDiTruoc, false);
                    }
                }
                else{
                    if(history.stack.get(i+1).buocDiTruoc.win == 1){
                        stR.saveNewStep(id_game_state, history.stack.get(i+1).buocDiTruoc, true);
                    }
                    else{
                        stR.saveNewStep(id_game_state, history.stack.get(i+1).buocDiTruoc, false);
                    }
//                    System.out.println("new Step of game State: "+id_game_state);
                }
            } else {
                gsR.saveGameState(history.stack.get(i));
                id_game_state = gsR.checkExist(history.stack.get(i));
//                System.out.println("new id_game_state = "+id_game_state);
                id_step = stR.checkExist(id_game_state, history.stack.get(i+1).buocDiTruoc); 
                if(id_step!=0){
                    if(history.stack.get(i+1).buocDiTruoc.win == 1){
                        stR.updateStep(id_step, history.stack.get(i+1).buocDiTruoc, true);
                    }
                    else{
                        stR.updateStep(id_step, history.stack.get(i+1).buocDiTruoc, false);
                    }
                }
                else{
                    if(history.lastGameState().buocDiTruoc.win == 1){
                        stR.saveNewStep(id_game_state, history.stack.get(i+1).buocDiTruoc, true);
                    }
                    else{
                        stR.saveNewStep(id_game_state, history.stack.get(i+1).buocDiTruoc, false);
                    }
//                    System.out.println("new Step new game State: "+id_game_state);
                }
            }
        }
        
    }
    
    public Step ultimate_bot(GameState gameState) throws SQLException, ClassNotFoundException{
        Step bestStep = new Step();
        GameStateRespository gsR = new GameStateRespository(this.dbConn);
        StepRepository stR = new StepRepository(this.dbConn);
        int id_game_state,id_step;
        id_game_state = gsR.checkExist(gameState);
        if(id_game_state!=0){
            String sqlStatement = "SELECT *FROM step WHERE `id_game_state` = "+id_game_state;
            sqlStatement+= " AND (win/total_use)>=ALL(SELECT win/total_use FROM step WHERE `id_game_state` = "+id_game_state;
            sqlStatement+=  ")HAVING (win/total_use)>0.5;";
            PreparedStatement ps;
            ps = dbConn.prepareStatement(sqlStatement);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                System.out.println("id Step = "+rs.getInt("id"));
                bestStep.chose = rs.getInt("chose");
                bestStep.direc = rs.getInt("direc");
            }
            else bestStep = null;
            bestStep.printOut();
            return bestStep;
        }
        return null;
    }
    
//    public static void main(String arg[]) throws SQLException, ClassNotFoundException{
//       int ketqua;
//        HistoryRespository hs = new HistoryRespository();
//       History his = new History();
//       hs.saveHistory(his);
//       GameStateRespository gsr = new GameStateRespository(hs.dbConn);
//       StepRepository sr = new StepRepository(hs.dbConn);
//       ketqua = gsr.checkExist(his.stack.get(0));
//        System.out.println("ket qua = "+ketqua);
//       gsr.saveGameState(his.stack.get(0));
//       ketqua = gsr.checkExist(his.stack.get(0));
//        System.out.println("ket qua = "+ketqua);
//        sr.saveNewStep(ketqua, new Step(), true);
//        int checkStep = sr.checkExist(ketqua, new Step());
//        System.out.println("checkStep = "+checkStep);
//    }

}

class StepRepository {

    protected Connection dbConn;

    public StepRepository(Connection dbConn) {
        this.dbConn = dbConn;
    }

    public StepRepository() throws SQLException, ClassNotFoundException {
        this.dbConn = ConnectionUtils.getMyConnection();
    }

    public int checkExist(int id_game_state, Step step) throws SQLException {
        String sqlStatement = "Select * From step where ( ";
        sqlStatement += "id_game_state =" + id_game_state + " AND ";
        sqlStatement += "chose= " + step.chose + " AND ";
        sqlStatement += "direc=" + step.direc + ")";
        PreparedStatement ps;
        ps = dbConn.prepareStatement(sqlStatement);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return rs.getInt("id");
        }

        return 0;
    }

    public void updateStep(int id, Step step, boolean ifWin) throws SQLException {
        Statement statement = dbConn.createStatement();
        String sqlStatement;
        if (ifWin) {
            sqlStatement = "UPDATE `ai_oaq`.`step` SET `win` = `win`+'1', `total_use` =`total_use`+'1' WHERE `id`=" + id + ";";
//            System.out.println("Add step win "+id+" complete!");
        } else {
            sqlStatement = "UPDATE `ai_oaq`.`step` SET `total_use` =`total_use`+'1' WHERE `id`=" + id + ";";
//            System.out.println("Add step lose "+id+" done!");
        }
        PreparedStatement ps;
        ps = dbConn.prepareStatement(sqlStatement);
        int rowCount = statement.executeUpdate(sqlStatement);
    }

    public void saveNewStep(int id_game_state, Step step, boolean ifWin) throws SQLException {
        Statement statement = dbConn.createStatement();
        String sqlStatement = "Insert into `step` (id_game_state, chose,direc,win,total_use) VALUES (";
        sqlStatement += id_game_state + ", ";
        sqlStatement += step.chose + ", ";
        sqlStatement += step.direc + ", ";
        if (ifWin) {
            sqlStatement += "1, ";
        } else {
            sqlStatement += "0, ";
        }
        sqlStatement += "1);";
        PreparedStatement ps;
        ps = dbConn.prepareStatement(sqlStatement);
        int rowCount = statement.executeUpdate(sqlStatement);

    }
}

class GameStateRespository {

    protected Connection dbConn;

    public GameStateRespository(Connection dbConn) throws SQLException, ClassNotFoundException {
        this.dbConn = dbConn;
    }

    /**
     * Function get GameState by id
     *
     * @param no
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public GameState getGameState(int no) throws SQLException, ClassNotFoundException {
        GameState gs = new GameState();
        String sqlStatement = "Select * From gamestate where id = " + no;

        PreparedStatement ps;
        ps = dbConn.prepareStatement(sqlStatement);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {

            for (int i = 1; i < 12; i++) {
                String columnName = "house_" + i;
                if (i == 6) {
                    continue;
                }
                gs.houseSave[i] = rs.getInt(columnName);
            }

            gs.q0Save.soDan = rs.getInt("q0_danSo");
            if (rs.getInt("q0_coQuan") != 0) {
                gs.q0Save.coQuan = true;
            }

            gs.q6Save.soDan = rs.getInt("q6_danSo");
            if (rs.getInt("q6_coQuan") != 0) {
                gs.q6Save.coQuan = true;
            }
        }
        return gs;
    }

    public int checkExist(GameState gameState) throws SQLException {

        String sqlStatement = "Select * From gamestate where ( ";

        for (int i = 1; i < 12 && i != 6; i++) {
            sqlStatement += "house_" + i + " = " + gameState.houseSave[i] + " AND ";

        }

        sqlStatement += "q0_danSo = " + gameState.q0Save.soDan + " AND ";
        sqlStatement += "q0_coQuan = " + gameState.q0Save.coQuan + " AND ";
        sqlStatement += "q6_danSo = " + gameState.q6Save.soDan + " AND ";
        sqlStatement += "q6_coQuan = " + gameState.q6Save.coQuan + " AND ";
        sqlStatement += "p1_soDan = " + gameState.p1Save.soDanAnDuoc + " AND ";
        sqlStatement += "p1_soQuan = " + gameState.p1Save.soQuanAnDuoc + " AND ";
        sqlStatement += "p2_soDan = " + gameState.p2Save.soDanAnDuoc + " AND ";
        sqlStatement += "p2_soQuan = " + gameState.p2Save.soQuanAnDuoc;
        sqlStatement += ");";

        PreparedStatement ps;
        ps = dbConn.prepareStatement(sqlStatement);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return rs.getInt("id");
        }

        return 0;
    }

    /**
     * Function save one record
     *
     * @param gameState
     */
    public void saveGameState(GameState gameState) throws SQLException, ClassNotFoundException {

        Statement statement = dbConn.createStatement();

        String sql = "Insert into gamestate (house_1, house_2, house_3, house_4, house_5, house_7, house_8, house_9, house_10, house_11"
                + ", q0_danSo, q0_coQuan, q6_danSo, q6_coQuan, p1_soDan,p1_soQuan,p2_soDan,p2_soQuan) "
                + " values (";
        for (int i = 1; i < 12; i++) {
            if (i == 6) {
                continue;
            }
            sql += gameState.houseSave[i] + ",";
        }

        sql += gameState.q0Save.soDan + ",";
        if (gameState.q0Save.coQuan) {
            sql += "1,";
        } else {
            sql += "0,";
        }

        sql += gameState.q6Save.soDan + ",";
        if (gameState.q6Save.coQuan) {
            sql += "1,";
        } else {
            sql += "0,";
        }
        sql += gameState.p1Save.soDanAnDuoc + ",";
        sql += gameState.p1Save.soQuanAnDuoc + ",";
        sql += gameState.p2Save.soDanAnDuoc + ",";
        sql += gameState.p2Save.soQuanAnDuoc;

        sql += ");";
//        System.out.println(sql);
        // Thực thi câu lệnh.
        // executeUpdate(String) sử dụng cho các loại lệnh Insert,Update,Delete.
        int rowCount = statement.executeUpdate(sql);

        // In ra số dòng được trèn vào bởi câu lệnh trên.
//        System.out.println("Row Count affected = " + rowCount);

    }
    
    
    
}
