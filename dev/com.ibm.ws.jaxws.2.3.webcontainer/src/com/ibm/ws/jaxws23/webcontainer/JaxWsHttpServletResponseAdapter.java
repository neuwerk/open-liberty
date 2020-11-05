/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.ibm.ws.jaxws23.webcontainer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.spi.http.HttpExchange;

/**
 * This class provides a HttpServletResponse instance using information
 * coming from the HttpExchange instance provided
 * by the underlying container.
 * Note: many methods' implementation still TODO.
 *
 */
class JaxWsHttpServletResponseAdapter implements HttpServletResponse {

    private final HttpExchange exchange;
    private String characterEncoding;
    private Locale locale;
    private boolean committed;
    private ServletOutputStreamAdapter servletOutputStream;
    private PrintWriter writer;
    private int status;

    JaxWsHttpServletResponseAdapter(HttpExchange exchange) {
        this.exchange = exchange;
    }

    @Override
    public void flushBuffer() throws IOException {
        exchange.getResponseBody().flush();
        committed = true;
    }

    @Override
    public int getBufferSize() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getCharacterEncoding() {
        return characterEncoding;
    }

    @Override
    public String getContentType() {
        return this.getHeader("Content-Type");
    }

    @Override
    public Locale getLocale() {
        return locale;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (servletOutputStream == null) {
            servletOutputStream = new ServletOutputStreamAdapter(exchange.getResponseBody());
        }
        return servletOutputStream;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (writer == null) {
            if (characterEncoding != null) {
                writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(exchange.getResponseBody(), characterEncoding)));
            } else {
                writer = new PrintWriter(exchange.getResponseBody());
            }
        }
        return writer;
    }

    @Override
    public boolean isCommitted() {
        return committed;
    }

    @Override
    public void reset() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void resetBuffer() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setBufferSize(int size) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setCharacterEncoding(String charset) {
        this.characterEncoding = charset;
    }

    @Override
    public void setContentLength(int len) {
        if (!committed) {
            exchange.getResponseHeaders().put("Content-Length",
                                              Collections.singletonList(String.valueOf(len)));
        }
    }

    @Override
    public void setContentType(String type) {
        if (!committed) {
            exchange.getResponseHeaders().put("Content-Type", Collections.singletonList(type));
        }
    }

    @Override
    public void setLocale(Locale loc) {
        this.locale = loc;
    }

    @Override
    public void addCookie(Cookie cookie) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addDateHeader(String name, long date) {
        this.addHeader(name, String.valueOf(date));
    }

    @Override
    public void addHeader(String name, String value) {
        exchange.addResponseHeader(name, value);
    }

    @Override
    public void addIntHeader(String name, int value) {
        this.addHeader(name, String.valueOf(value));
    }

    @Override
    public boolean containsHeader(String name) {
        return exchange.getResponseHeaders().containsKey(name);
    }

    @Override
    public String encodeURL(String url) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String encodeRedirectURL(String url) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    public String encodeUrl(String url) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    public String encodeRedirectUrl(String url) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getHeader(String name) {
        List<String> headers = exchange.getResponseHeaders().get(name);
        return (headers != null && !headers.isEmpty()) ? headers.get(0) : null;
    }

    @Override
    public Collection<String> getHeaderNames() {
        return exchange.getResponseHeaders().keySet();
    }

    @Override
    public Collection<String> getHeaders(String headerName) {
        return exchange.getResponseHeaders().get(headerName);
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public void sendError(int sc) throws IOException {
        this.setStatus(sc);
        this.committed = true;
    }

    @Override
    public void sendError(int sc, String msg) throws IOException {
        this.sendError(sc);
    }

    @Override
    public void sendRedirect(String location) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setDateHeader(String name, long date) {
        this.setHeader(name, String.valueOf(date));
    }

    @Override
    public void setHeader(String name, String value) {
        List<String> list = new LinkedList<>();
        list.add(value);
        exchange.getResponseHeaders().put(name, list);
    }

    @Override
    public void setIntHeader(String name, int value) {
        this.setHeader(name, String.valueOf(value));
    }

    @Override
    public void setStatus(int sc) {
        this.status = sc;
        this.exchange.setStatus(sc);
    }

    @Override
    @Deprecated
    public void setStatus(int sc, String sm) {
        this.setStatus(sc);
    }

    private static class ServletOutputStreamAdapter extends ServletOutputStream {

        private final OutputStream delegate;

        ServletOutputStreamAdapter(OutputStream delegate) {
            this.delegate = delegate;
        }

        @Override
        public void write(int b) throws IOException {
            delegate.write(b);
        }

        @Override
        public void flush() throws IOException {
            delegate.flush();
        }

        public boolean isReady() {
            throw new UnsupportedOperationException();
        }

        public void setWriteListener(WriteListener arg0) {
            throw new UnsupportedOperationException();
        }
    }

    public void setContentLengthLong(long arg0) {
        throw new UnsupportedOperationException();
    }
}
