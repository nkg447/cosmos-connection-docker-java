package com.cosmos.demo.example;

import com.azure.cosmos.*;

public class CosmosConnection {
    private CosmosClient cosmosClient;

    public CosmosConnection(String host, String port, String key) {
        String endpoint = "https://" + host + ":" + port + "/";

        cosmosClient = new CosmosClientBuilder()
                .endpoint(endpoint)
                .key(key)
                .directMode()
                .buildClient();
    }

    public CosmosClient getCosmosClient() {
        return cosmosClient;
    }
}
