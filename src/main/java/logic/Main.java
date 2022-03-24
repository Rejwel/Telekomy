package logic;

public class Main {
    public static void main(String[] args) {
        Transmission T = new Transmission();


        var a = T.op();
        System.out.println(T.isCorrect(a));
    }
}
