package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CustomWebApplicationServer {
    private final int port;

    //스레드 풀을 적용하는 방법
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

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
                 * -> 문제점이 있다. 메인 스레드가 동작이 멈추면 다음 스레드의 작업을 수행 할 수 없다.
                 * -> 그래서 따로 스레드를 만들어서 동작 처리를 수월하게 하도록 진행한다.
                 * Step2 - 사용자 요청이 들어올 때마다 Thread를 새로 생성해서 사용자 요청을 처리
                 * 하도록 한다.
                 * -> 2번의 문제점은 새로 스레드를 생성할때(독립적으로 생김)마다 스택 메모리에 쌓이면서 메모리를 사용하기 때문에
                 * -> 메모리 낭비가 생기며 Cpu의 사용량도 증가하게 된다.
                 */

                //2. 사용자의 요청이 올때마다 스레드를 새로 생성해서 처리하도록 변경
                //new Thread(new ClientRequestHandler(clientSocker)).start();

                //Step3 - Thread Pool을 적용해 안정적인 서비스가 가능하도록 한다.
                executorService.execute(new ClientRequestHandler(clientSocker));



                /*
                //----------------------------------------------- 1.
                try(InputStream in = clientSocker.getInputStream(); OutputStream out = clientSocker.getOutputStream()){
                    BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
                    DataOutputStream dos = new DataOutputStream(out);

                    //http 요청 메시지의 -리퀘스트 라인 확인 코드
                    //String line;
                    //while ((line = br.readLine()) != ""){
                    //    System.out.println(line);
                    //}

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
                */

            }
        }
    }
}
