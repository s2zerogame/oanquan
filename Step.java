/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testSQuares2;

/**
 *
 * @author VPC
 */
public class Step {
        int chose;
        int direc;
        int win;
        public Step(){
            chose = 0;
            direc = 0;
            win =0;
        }
        public Step(int arg_chose,int arg_direc){
            this.chose = arg_chose;
            this.direc = arg_direc;
        }
        
        public void setwin(int win){
            this.win = win;
        }
        
        public boolean compare2Step(Step s1,Step s2){
            if((s1.chose!=s2.chose)||(s1.direc!=s2.direc))
                return false;
            else return true;
        }
        public void printOut(){
            System.out.println("Chose = "+this.chose+", direc = "+this.direc);
        }
    }