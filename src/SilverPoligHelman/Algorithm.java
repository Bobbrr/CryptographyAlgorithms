package SilverPoligHelman;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by pavlodanyliuk on 28.05.17.
 */
public class Algorithm {
    private BufferedReader reader;
    private int a;
    private int b;
    private int p;
    private Map<Integer, Integer> simpleDecompos;
    private int[][] simpleNum;
    private int[][] table;

    public Algorithm(){
        reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("a^x = b (mod p)");
        System.out.println("Please enter following variables:");
        try {
            System.out.println("a = ");
            a = Integer.parseInt(reader.readLine());
            System.out.println("b = ");
            b = Integer.parseInt(reader.readLine());
            System.out.println("p = ");
            p = Integer.parseInt(reader.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public static void main(String[] args) {
        Algorithm alg = new Algorithm();
        System.out.println(alg.vichet(10, 36, 73));
        alg.start();
    }

    public void start(){
        simpleDecompos = new TreeMap<>();

        ///////////////////////////////////
        System.out.println("Розклад на прості множники:");
        toSimpleNumber(p - 1, 2, simpleDecompos);
        //convert to array for easily working
        simpleNum = new int[simpleDecompos.size()][2];
        int ind = 0;
        System.out.print(p-1 + " = " );
        for (Map.Entry<Integer, Integer> entry : simpleDecompos.entrySet()) {
            simpleNum[ind][0] = entry.getKey();
            simpleNum[ind][1] = entry.getValue();
            System.out.print(entry.getKey() + "^" + entry.getValue() + " * ");
            ind++;
        }
        System.out.println();

        //1st step

        System.out.println("\n =========Складаємо таблицю========== \n");
        getTable();
        for(int j = 0; j < table.length; j++){
            System.out.println("  For " + simpleNum[j][0]);
            for(int k = 0; k < table[j].length; k++)
                System.out.print(table[j][k] + " ");
            System.out.println("\n");
        }

        //2nd step:
        System.out.println("=======Calculate x = x0 + x1q+...+x(a-1) * q^(a-1) (mod q^a)======\n");
        for(int i = 0; i < simpleNum.length; i++) {
            System.out.println("  ~~~For " + simpleNum[i][0] + " with alpha = " + simpleNum[i][1] + "~~~~");
            find_x_for_q(i);
        }


    }



    public void toSimpleNumber(int num, int div ,Map<Integer, Integer> map){

        while (div <= num ) {
            if ((num % div) == 0 && isSimple(div)) {
                if(map.containsKey(div))
                    map.put(div, map.get(div)+1);
                else
                    map.put(div, 1);
                toSimpleNumber(num / div, div,map);
                return;
            }
            div++;
        }
    }
    public boolean isSimple(int num){
        for (int i = 2; i < num; i++){
            if(num % i ==  0) return false;
        }
        return true;
    }

    public void getTable(){
        table = new int[simpleNum.length][];
        for(int i = 0; i < simpleNum.length; i++){
            table[i] = new int[simpleNum[i][0]];
            for(int j = 0; j < simpleNum[i][0]; j++){
                //System.out.println("i,j " + i +" " + j);
                table[i][j] = vichet(a, j*(p-1)/simpleNum[i][0], p);
            }
        }
    }

    private int find_x_for_q(int q_ind){


        int alfa = simpleNum[q_ind][1];
        int [] x = new int[alfa];

        System.out.println("\nFormula a^(xi(p-1)/q) = b*a^(-x0-x1*q-..-x(i-1) * q^(i-1))\n");
        for(int i = 0; i < alfa; i++){
            System.out.println("\n    -Find x" + i +":");
            x[i] = find_any_xi(q_ind, i, x);
            System.out.print("x = [ " );
            for(int j = 0; j < x.length; j++){
                System.out.print(x[j] + ", ");
            }
            System.out.println("]");
        }


        int res_x = 0;
        System.out.print("x = ");
        for(int i = 0; i < x.length; i++){
            System.out.print("x" + i + " * " + simpleNum[q_ind][0] + "^" + i +" + " );
            int sum = (int) (x[i]*Math.pow(simpleNum[q_ind][0], i));
            res_x += sum ;
        }
        int result = (int)(res_x%Math.pow(simpleNum[q_ind][0], alfa));
        System.out.println("Result: " + result + " (mod" + Math.pow(simpleNum[q_ind][0], alfa) + ")\n");
        return result;
    }

    private int find_any_xi(int q_ind, int i, int[] x){
        int exp = 0;
        for (int j = 0; j < i; j++){
            exp -= x[j]*Math.pow(simpleNum[q_ind][0], j);
        }
        int a_in_exp = vichet(a, -exp, p);
        System.out.println(a + " in exponenta " +(-exp) + " = " + a_in_exp);
        int ob = oberneny(a_in_exp, p);
        int pr = (b * ob) % p;
        System.out.println(b + " * (" + a_in_exp + ")^(-1) =" + b + " * " + ob + " = " + pr);
        int v = vichet(pr, (int)((p-1)/Math.pow(simpleNum[q_ind][0], i+1)), p);
        System.out.println(pr + "^( (" + p +"-1)/"+simpleNum[q_ind][0]+"^("+(i+1)+") ) = " + v );
        int with_table = find_in_table(v, q_ind);
        System.out.println("In table " + v + " means " + with_table);
        return with_table;

    }
    private int oberneny(int element, int mod){
        for(int i = 1; i<mod; i++){
            if((element*i)%mod == 1) {
                System.out.println("obernenyu " + i);
                return i;
            }
        }
        return -1;
    }

//    private int find_x0(int q_ind) {
//        int pr = vichet(b, (p-1)/simpleNum[0][0], p);
//        return find_in_table(pr, q_ind);
//    }
    private int vichet(int num, int exp, int mod){

        int res = num;
       // System.out.println(" " + res + " exp " + exp + " mod " + mod);
        if(exp == 0) return 1;
        for (int i = 0; i < exp-1; i++){
            res = (res*num) % mod;
        }
        //System.out.println(res);
        return res;
    }
    private int find_in_table(int res, int q_index){
        for(int i = 0; i < table[q_index].length; i++ ){
            if(res == table[q_index][i]) return i;
        }
        return -1;
    }
}
