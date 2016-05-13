package polcz.demo.java8;

public class LambdaFunctionalInterface {

    @FunctionalInterface
    public interface SimpleFuncInterface {
        /* can have ONLY one abstract method */
        public void doWork(int a);

        default public void f() {
            System.out.println("Function f() from the function interface [default]");
        }
    }

    public static void main(String[] args) {
        carryOutWork(
                /* what does a lambda expression mean */
                new SimpleFuncInterface() {
                    @Override
                    public void doWork(int a) {
                        if (a > 0) System.out.println("This parameter is positive");
                        else System.out.println("This parameter is negative");
                    }
                });
        carryOutWork((a) -> {
            if (a > 0) System.out.println("This parameter is positive");
            else System.out.println("This parameter is negative");
        });
    }

    public static void carryOutWork(SimpleFuncInterface sfi) {
        sfi.doWork(12);
        sfi.doWork(-12);
        sfi.f();
    }

}
