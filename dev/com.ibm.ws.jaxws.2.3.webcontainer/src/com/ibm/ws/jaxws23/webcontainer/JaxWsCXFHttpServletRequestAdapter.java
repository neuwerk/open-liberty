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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.ReadListener;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;
import javax.xml.ws.spi.http.HttpContext;
import javax.xml.ws.spi.http.HttpExchange;

/**
 * This class provides a HttpServletRequest instance using information
 * coming from the HttpExchange and HttpContext instances provided
 * by the underlying container.
 * Note: many methods' implementation still TODO.
 *
 */
class JaxWSCXFHttpServletRequestAdapter implements HttpServletRequest {

    private final HttpExchange exchange;
    private final HttpContext context;
    private String characterEncoding;
    private ServletInputStreamAdapter servletInputStreamAdapter;
    private BufferedReader reader;

    JaxWSCXFHttpServletRequestAdapter(HttpExchange exchange) {
        this.exchange = exchange;
        this.context = exchange.getHttpContext();
    }

    @Override
    public AsyncContext getAsyncContext() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getAttribute(String name) {
        return exchange.getAttribute(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return Collections.enumeration(exchange.getAttributeNames());
    }

    @Override
    public String getCharacterEncoding() {
        return characterEncoding;
    }

    @Override
    public int getContentLength() {
        return 0;
    }

    @Override
    public String getContentType() {
        return this.getHeader("Content-Type");
    }

    @Override
    public DispatcherType getDispatcherType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (servletInputStreamAdapter == null) {
            servletInputStreamAdapter = new ServletInputStreamAdapter(exchange.getRequestBody());
        }
        return servletInputStreamAdapter;
    }

    @Override
    public String getLocalAddr() {
        InetSocketAddress isa = exchange.getLocalAddress();
        if (isa != null) {
            InetAddress ia = isa.getAddress();
            if (ia != null) {
                return ia.getHostAddress();
            }
        }
        return null;
    }

    @Override
    public Locale getLocale() {
        return null;
    }

    @Override
    public Enumeration<Locale> getLocales() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getLocalName() {
        InetSocketAddress isa = exchange.getLocalAddress();
        if (isa != null) {
            InetAddress ia = isa.getAddress();
            if (ia != null) {
                return ia.getHostName();
            }
        }
        return null;
    }

    @Override
    public int getLocalPort() {
        InetSocketAddress isa = exchange.getLocalAddress();
        return isa != null ? isa.getPort() : 0;
    }

    @Override
    public String getParameter(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Enumeration<String> getParameterNames() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String[] getParameterValues(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getProtocol() {
        return exchange.getProtocol();
    }

    @Override
    public BufferedReader getReader() throws IOException {
        if (reader == null) {
            Reader isr = characterEncoding == null ? new InputStreamReader(exchange.getRequestBody()) : new InputStreamReader(exchange.getRequestBody(), characterEncoding);
            reader = new BufferedReader(isr);
        }
        return reader;
    }

    @Override
    @Deprecated
    public String getRealPath(String path) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getRemoteAddr() {
        InetSocketAddress isa = exchange.getRemoteAddress();
        if (isa != null) {
            InetAddress ia = isa.getAddress();
            if (ia != null) {
                return ia.getHostAddress();
            }
        }
        return null;
    }

    @Override
    public String getRemoteHost() {
        InetSocketAddress isa = exchange.getRemoteAddress();
        if (isa != null) {
            InetAddress ia = isa.getAddress();
            if (ia != null) {
                return ia.getHostName();
            }
        }
        return null;
    }

    @Override
    public int getRemotePort() {
        InetSocketAddress isa = exchange.getRemoteAddress();
        return isa != null ? isa.getPort() : 0;
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getScheme() {
        return exchange.getScheme();
    }

    @Override
    public String getServerName() {
        return null;
    }

    @Override
    public int getServerPort() {
        return 0;
    }

    @Override
    public ServletContext getServletContext() {
        return null;
    }

    @Override
    public boolean isAsyncStarted() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isAsyncSupported() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isSecure() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeAttribute(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAttribute(String name, Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
        this.characterEncoding = env;
    }

    @Override
    public AsyncContext startAsync() {
        throw new UnsupportedOperationException();
    }

    @Override
    public AsyncContext startAsync(ServletRequest request, ServletResponse response) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getAuthType() {
        return null;
    }

    @Override
    public String getContextPath() {
        return exchange.getContextPath();
    }

    @Override
    public Cookie[] getCookies() {
        return null;
    }

    @Override
    public long getDateHeader(String name) {
        String s = this.getHeader(name);
        return s != null ? Long.parseLong(s) : 0;
    }

    @Override
    public String getHeader(String name) {
        return exchange.getRequestHeader(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return Collections.enumeration(exchange.getRequestHeaders().keySet());
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        List<String> list = exchange.getRequestHeaders().get(name);
        return list != null ? Collections.enumeration(list) : null;
    }

    @Override
    public int getIntHeader(String name) {
        String s = this.getHeader(name);
        return s != null ? Integer.parseInt(s) : 0;
    }

    @Override
    public String getMethod() {
        return exchange.getRequestMethod();
    }

    @Override
    public Part getPart(String name) throws IOException, ServletException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<Part> getParts() throws IOException, ServletException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getPathInfo() {
        return exchange.getPathInfo();
    }

    @Override
    public String getPathTranslated() {
        return null;
    }

    @Override
    public String getQueryString() {
        return exchange.getQueryString();
    }

    @Override
    public String getRemoteUser() {
        return null;
    }

    @Override
    public String getRequestedSessionId() {
        return null;
    }

    @Override
    public String getRequestURI() {
        return exchange.getRequestURI();
    }

    @Override
    public StringBuffer getRequestURL() {
        StringBuffer sb = new StringBuffer();
        sb.append(exchange.getScheme());
        sb.append("://");
        String host = this.getHeader("Host");
        if (host != null) {
            sb.append(host);
        } else {
            InetSocketAddress la = exchange.getLocalAddress();
            if (la != null) {
                sb.append(la.getHostName());
                if (la.getPort() > 0) {
                    sb.append(':');
                    sb.append(la.getPort());
                }
            } else {
                sb.append("localhost");
            }
        }
        sb.append(exchange.getContextPath());
        sb.append(context.getPath());
        return sb;
    }

    @Override
    public String getServletPath() {
        return null;
    }

    @Override
    public HttpSession getSession() {
        return null;
    }

    @Override
    public HttpSession getSession(boolean create) {
        return null;
    }

    @Override
    public Principal getUserPrincipal() {
        return exchange.getUserPrincipal();
    }

    @Override
    public boolean isRequestedSessionIdFromCookie() {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    public boolean isRequestedSessionIdFromUrl() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isRequestedSessionIdFromURL() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        return false;
    }

    @Override
    public boolean isUserInRole(String role) {
        return exchange.isUserInRole(role);
    }

    @Override
    public void login(String username, String password) throws ServletException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void logout() throws ServletException {
        throw new UnsupportedOperationException();
    }

    private static class ServletInputStreamAdapter extends ServletInputStream {

        private final InputStream delegate;

        ServletInputStreamAdapter(InputStream delegate) {
            this.delegate = delegate;
        }

        @Override
        public int read() throws IOException {
            return delegate.read();
        }

        public boolean isFinished() {
            throw new UnsupportedOperationException();
        }

        public boolean isReady() {
            throw new UnsupportedOperationException();
        }

        public void setReadListener(ReadListener arg0) {
            throw new UnsupportedOperationException();

        }
    }

    public long getContentLengthLong() {
        throw new UnsupportedOperationException();
    }

    public String changeSessionId() {
        throw new UnsupportedOperationException();
    }

    public <T extends HttpUpgradeHandler> T upgrade(Class<T> cls) throws IOException, ServletException {
        throw new UnsupportedOperationException();
    }
}
