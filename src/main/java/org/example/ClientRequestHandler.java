package org.example;

import org.example.calculator.domain.Calculator;
import org.example.calculator.domain.PositiveNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;


public class ClientRequestHandler implements Runnable{
    private static final Logger looger = LoggerFactory.getLogger(ClientRequestHandler.class);

    private final Socket clientSocker;

    public ClientRequestHandler(Socket clientSocker) {
        this.clientSocker = clientSocker;
    }


    @Override
    public void run() {
        looger.info("[ClientRequestHandler] new client {} started", Thread.currentThread().getName());
        try(InputStream in = clientSocker.getInputStream(); OutputStream out = clientSocker.getOutputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            DataOutputStream dos = new DataOutputStream(out);

            HttpRequest httpRequest = new HttpRequest(br);

            //GET /calculate?operand1=11&operator=*&operand2=55
            // calculate가 맞다면 원하는 요청의 기능을 수행해서 결과값을 돌려준다.
            if (httpRequest.isGetRequest() && httpRequest.matchPath("/calculate")) {
                QueryStrings queryStrings = httpRequest.getQueryString();

                int operand1 = Integer.parseInt(queryStrings.getValue("operand1"));
                String operator = queryStrings.getValue("operator");
                int operand2 = Integer.parseInt(queryStrings.getValue("operand2"));

                int result = Calculator.calculate(new PositiveNumber(operand1), operator, new PositiveNumber(operand2));
                byte[] body = String.valueOf(result).getBytes();

                //응답
                HttpResponse response = new HttpResponse(dos);
                response.response200Header("application/json", body.length);
                response.responseBody(body);
            }
        } catch (IOException e) {
            looger.error(e.getMessage());
        }
    }
}
