package com.cosmos.demo.example;

import com.azure.cosmos.*;
import com.google.common.collect.Lists;

public class CosmosConnection {
    private static final String COSMOS_DB_URI = "https://localhost:8081/";
    private static final String PRIMARY_KEY = "C2y6yDjf5/R+ob0N8A7Cgv30VRDJIWEHLM+4QDU5DE2nQ9nDuVTqobD4b8mGGyPMbIZnqyMsEcaGQy67XIw/Jw==";

    private CosmosClient cosmosClient;

    public CosmosConnection() {
        ConnectionPolicy defaultPolicy = ConnectionPolicy.getDefaultPolicy();
        defaultPolicy.setUserAgentSuffix("CosmosDBJavaQuickstart");
        //  Setting the preferred location to Cosmos DB Account region
        //  West US is just an example. User should set preferred location to the Cosmos DB region closest to the application
        defaultPolicy.setPreferredLocations(Lists.newArrayList("West US"));
        //  Create sync client
        //  <CreateSyncClient>
        cosmosClient = new CosmosClientBuilder()
                .setEndpoint(COSMOS_DB_URI)
                .setKey(PRIMARY_KEY)
                .setConnectionPolicy(defaultPolicy)
                .setConsistencyLevel(ConsistencyLevel.EVENTUAL)
                .buildClient();
    }

    public CosmosClient getCosmosClient() {
        return cosmosClient;
    }
}
