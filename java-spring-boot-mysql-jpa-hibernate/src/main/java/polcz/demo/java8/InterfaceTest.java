package polcz.demo.java8;

public class InterfaceTest {

    interface InterfaceA {
        void f();

        default void g() {
            System.out.println("function g() - from InterfaceA");
        }
    }

    interface InterfaceB {
        void f();

        default void g() {
            System.out.println("function g() - from InterfaceB");
        }

        default void h() {
            System.out.println("function h() - from InterfaceB");
        }
    }

    class ClassA implements InterfaceA, InterfaceB {

        @Override
        public void f() {
            System.out.println("function f() - from ClassA");
        }

        @Override
        public void g() {
            InterfaceA.super.g();
        }

    }

    public InterfaceTest() {

        ClassA a = new ClassA();
        a.g();
        a.f();
        a.h();
    }

    public static void main(String[] args) {
        new InterfaceTest();
    }

}
