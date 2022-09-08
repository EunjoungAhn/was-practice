package org.example;

import java.io.IOException;

/**
 * 웹에서 GET 요청이 들어올때 규칙을 정하겠다.
 * calculate?operand1=11&operator=*&operand2=55
 */
public class Main {
    public static void main(String[] args) throws IOException {

        new CustomWebApplicationServer(8080).start();

    }
}