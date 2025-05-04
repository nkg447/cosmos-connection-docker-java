package com.cosmos.demo.example;

import com.azure.cosmos.*;

public class CosmosConnection {
    private static final String COSMOS_DB_URI = "https://localhost:8081/";
    private static final String PRIMARY_KEY = "C2y6yDjf5/R+ob0N8A7Cgv30VRDJIWEHLM+4QDU5DE2nQ9nDuVTqobD4b8mGGyPMbIZnqyMsEcaGQy67XIw/Jw==";

    private CosmosClient cosmosClient;

    public CosmosConnection() {
        cosmosClient = new CosmosClientBuilder()
                .endpoint(COSMOS_DB_URI)
                .key(PRIMARY_KEY)
                .directMode()
                .buildClient();
    }

    public CosmosClient getCosmosClient() {
        return cosmosClient;
    }
}
