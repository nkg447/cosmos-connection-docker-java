package com.cosmos.demo.example;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.compress.utils.IOUtils;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;

public class CosmosEndpointSimulator {
    private static Server server;

    public static void main(String[] args) throws Exception {
        start(8085);
    }
    public static void start(final int port) throws Exception {
        server = new Server();

        HttpConfiguration httpConfig = new HttpConfiguration();
        httpConfig.setSecureScheme("https");
        httpConfig.setSecurePort(port);
        httpConfig.setOutputBufferSize(32768);

        // HTTPS Configuration
        HttpConfiguration httpsConfig = new HttpConfiguration(httpConfig);
        httpsConfig.addCustomizer(new SecureRequestCustomizer());

        // SSL Context Factory for HTTPS
        SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();
        sslContextFactory.setKeyStorePath("/Users/nikugupta/projects/cosmos-connection-docker-java/scripts/localhost/server.keystore.jks"); // Set the path to your keystore
        sslContextFactory.setKeyStorePassword("changeit"); // Set the keystore password
        sslContextFactory.setKeyManagerPassword("changeit"); // Set the key manager password

        ServerConnector connector = new ServerConnector(
                server,
                new SslConnectionFactory(sslContextFactory, "http/1.1"),
                new org.eclipse.jetty.server.HttpConnectionFactory(httpsConfig)
        );

        connector.setPort(port);

        server.setConnectors(new Connector[] {connector});
        ServletHandler servletHandler = new ServletHandler();
        servletHandler.addServletWithMapping(MockServlet1.class, "/");
        server.setHandler(servletHandler);
        server.start();
    }

    public static final class MockServlet1 implements Servlet {

        private ServletConfig config;

        @Override
        public void init(ServletConfig config) throws ServletException {
            this.config = config;
        }

        @Override
        public ServletConfig getServletConfig() {
            return config;
        }

        @Override
        public String getServletInfo() {
            return "MockServlet1";
        }

        @Override
        public void destroy() {}

        @Override
        public void service(final ServletRequest request1, final ServletResponse response1) throws ServletException, IOException {
            final HttpServletResponse response0 = (HttpServletResponse)response1;
            response0.setStatus(200);
            response0.addHeader("Content-Type", "application/json");
            final OutputStream responseEntity = response0.getOutputStream();
            String body = "{\n" +
                    "                  \"_self\": \"\",\n" +
                    "                  \"id\": \"cosmosdev\",\n" +
                    "                  \"_rid\": \"cosmosdev\",\n" +
                    "                  \"media\": \"//media/\",\n" +
                    "                  \"addresses\": \"//addresses/\",\n" +
                    "                  \"_dbs\": \"//dbs/\",\n" +
                    "                  \"enableMultipleWriteLocations\": false,\n" +
                    "                  \"writableLocations\": [\n" +
                    "                    {\n" +
                    "                      \"name\": \"Primary\",\n" +
                    "                      \"databaseAccountEndpoint\": \"https://host.docker.internal:8081/\"\n" +
                    "                    }\n" +
                    "                  ],\n" +
                    "                  \"readableLocations\": [\n" +
                    "                    {\n" +
                    "                      \"name\": \"Primary\",\n" +
                    "                      \"databaseAccountEndpoint\": \"https://host.docker.internal:8081/\"\n" +
                    "                    }\n" +
                    "                  ],\n" +
                    "                  \"userConsistencyPolicy\": {\n" +
                    "                    \"defaultConsistencyLevel\": \"Eventual\"\n" +
                    "                  },\n" +
                    "                  \"queryEngineConfiguration\": \"{\\\"sqlAllowSubQuery\\\":true,\\\"sqlAllowTop\\\":true,\\\"sqlAllowGroupByClause\\\":true,\\\"sqlAllowLike\\\":true,\\\"sqlAllowScalarSubQuery\\\":true,\\\"sqlAllowAggregateFunctions\\\":true,\\\"maxSpatialQueryCells\\\":2147483647,\\\"maxLogicalOrPerSqlQuery\\\":2147483647,\\\"maxLogicalAndPerSqlQuery\\\":2147483647,\\\"maxInExpressionItemsCount\\\":2147483647,\\\"enableSpatialIndexing\\\":true,\\\"sqlDisableOptimizationFlags\\\":0,\\\"sqlAllowNonFiniteNumbers\\\":false,\\\"spatialMaxGeometryPointCount\\\":256,\\\"queryMaxInMemorySortDocumentCount\\\":-500,\\\"maxUdfRefPerSqlQuery\\\":10,\\\"maxSqlQueryInputLength\\\":524288,\\\"maxQueryRequestTimeoutFraction\\\":0.9,\\\"maxJoinsPerSqlQuery\\\":10,\\\"allowNewKeywords\\\":true}\"\n" +
                    "                }";
            IOUtils.copy(new ByteArrayInputStream(body.getBytes()), responseEntity);
            responseEntity.flush();
            responseEntity.close();
        }
    }
}
