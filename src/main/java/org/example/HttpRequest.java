package org.example;

import java.io.BufferedReader;
import java.io.IOException;

public class HttpRequest {
    private final RequestLine requestLine;
    /*
    원래는 헤더와 바디도 있어야 하지만, 현재 구현하려는 계산기 프로그램에는 필요가 없어서 생략하였다.
    private final HttpHeaders httpHeaders;
    private final Body body;
     */

    public HttpRequest(BufferedReader br) throws IOException {
        this.requestLine = new RequestLine(br.readLine());
    }

    public boolean isGetRequest() {
        return requestLine.isGetRequest();
    }

    public boolean matchPath(String requestPath) {
        return requestLine.matchPath(requestPath);
    }

    public QueryStrings getQueryString() {
        return requestLine.getQueryString();
    }
}
