
package testSQuares2;
/**
 *
 * @author VPC
 */

/**
* Định dạng của 1 encode trạng thái là 1 String cả số và chữ
* Đặc trưng cho số dân ở nhà quan, nhà dân
* "m3" = 13;    "h7"=27;     "b5"=35;    "f0"=40     "n1"=51
*  q la có Quan. Nhà quan luôn có 2 chữ số ví dụ "q14m35663f34567q24"
*/
public class State {
    String encode;
    int q0,q5; 
    int coQ0,coQ5;
    int[] house = new int[11];
    int ptr ;  //Con tro duyet xau
    int lencode;       //Do dai xau
    public State(String s){
        this.encode = s;
            
    }
    public void handle(){
    this.lencode = this.encode.length();
        ptr =0;
        if(this.encode.substring(ptr, ptr+1).equals("q")) {coQ0=5; ptr++;}
        this.q0 = Integer.parseInt(this.encode.substring(ptr, ptr+2));
        ptr+=2;
        for(int i=1; i<=10; i++){
            house[i]=0;
            if (isNumeric(this.encode.substring(ptr, ptr+1))) {ptr--;}
            else if (this.encode.substring(ptr, ptr+1).equals("m")) this.house[i]=10;
            else if (this.encode.substring(ptr, ptr+1).equals("h")) this.house[i]=20;
            else if (this.encode.substring(ptr, ptr+1).equals("b")) this.house[i]=30;
            else if (this.encode.substring(ptr, ptr+1).equals("f")) this.house[i]=40;
            else if (this.encode.substring(ptr, ptr+1).equals("n")) this.house[i]=50;
            ptr++;
            house[i] += Integer.parseInt(this.encode.substring(ptr, ptr + 1));
            ptr++;
        }
        if(this.encode.substring(ptr, ptr+1).equals("q")) {coQ5=5; ptr++;}
        this.q5 = Integer.parseInt(this.encode.substring(ptr));
    }
    //Hàm kiểm tra xem 1 xâu có phải 1 số hay k
    public static boolean isNumeric(String str)  
    {  
      try  
      {  
        double d = Double.parseDouble(str);  
      }  
      catch(NumberFormatException nfe)  
      {  
        return false;  
      }  
      return true;  
    }
    /*
    public static void main(String[] args){
        State state = new State("q14m35663f34567324");
        state.handle();
        System.out.println(state.lencode);
        System.out.println("Quan 1:"+state.q0);
        for (int i=1;i<=10;i++){
            System.out.println(state.house[i]);
        }
        System.out.println("Quan 2:"+state.q5);
        System.out.println("Q3 ="+state.coQ5);
        System.out.println("Q9 ="+state.coQ0);
    }
    */
}
