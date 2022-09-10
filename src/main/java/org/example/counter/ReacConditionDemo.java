package org.example.counter;

public class ReacConditionDemo {
    public static void main(String[] args) {
        //멀티 스레드 환경에서 하나의 자원을 공유하게 되면, 원치 않는 결과가 나올 수 있다.
        //그래서 동기화 처리가 안되어 있으면 우리가 원하는 결과가 나오지 않을 수 있다.
        //그러므로 싱글톤 객체에서는 상태를 유지(stateful)하게 설계하면 안된다.
        Counter counter = new Counter();
        Thread t1 = new Thread(counter, "Thread-1");
        Thread t2 = new Thread(counter, "Thread-2");
        Thread t3 = new Thread(counter, "Thread-3");

        t1.start();
        t2.start();
        t3.start();

    }
}
