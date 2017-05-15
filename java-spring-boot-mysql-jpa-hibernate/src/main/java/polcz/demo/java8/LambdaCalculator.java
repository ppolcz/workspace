package polcz.demo.java8;

public class LambdaCalculator {

    interface TMath<T> {
        T operation(T a, T b);
    }

    public int operateBinary(int a, int b, TMath<Integer> op) {
        return op.operation(a, b);
    }

    public int multiplication(int a, int b) {
        return a * b;
    }

    public LambdaCalculator() {
        TMath<Integer> addition = (a, b) -> a + b;
        TMath<Integer> subtraction = (a, b) -> a - b;
        TMath<Integer> multiplication = this::multiplication;
        System.out.println("40 + 2 = " + operateBinary(40, 2, addition));
        System.out.println("20 - 10 = " + operateBinary(20, 10, subtraction));
        System.out.println("20 - 10 = " + operateBinary(20, 10, multiplication));
    }

    public static void main(String... args) {
        new LambdaCalculator();
    }
}
