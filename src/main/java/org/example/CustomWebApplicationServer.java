package org.example;

import org.example.calculator.domain.Calculator;
import org.example.calculator.domain.PositiveNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class CustomWebApplicationServer {
    private final int port;

    private static final Logger logger = LoggerFactory.getLogger(CustomWebApplicationServer.class);

    public CustomWebApplicationServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        try(ServerSocket serverSocket = new ServerSocket(port)){
            logger.info("[CustomWebApplicationServer] started {} port.", port);

            Socket clientSocker;
            logger.info("[CustomWebApplicationServer] waiting for client.", port);

            while ((clientSocker = serverSocket.accept()) != null){
                logger.info("[CustomWebApplicationServer] client connected!", port);

                /**
                 * Step1 - 사용자 요청을 메인 Thread가 처리하도록 한다.
                 */

                try(InputStream in = clientSocker.getInputStream(); OutputStream out = clientSocker.getOutputStream()){
                    BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
                    DataOutputStream dos = new DataOutputStream(out);

                    /*
                    //http 요청 메시지의 -리퀘스트 라인 확인 코드
                    String line;
                    while ((line = br.readLine()) != ""){
                        System.out.println(line);
                    }
                     */

                    //요청
                    HttpRequest httpRequest = new HttpRequest(br);


                    //GET /calculate?operand1=11&operator=*&operand2=55
                    // calculate가 맞다면 원하는 요청의 기능을 수행해서 결과값을 돌려준다.
                    if(httpRequest.isGetRequest() && httpRequest.matchPath("/calculate")){
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
                }

            }
        }
    }
}
