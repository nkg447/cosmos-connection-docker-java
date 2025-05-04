package com.cosmos.demo.example;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.containers.BindMode;

public class CosmosDbEmulatorTest {
    public static void main(String[] args) throws InterruptedException {
        GenericContainer<?> cosmosDbEmulator = new GenericContainer<>(DockerImageName.parse("mcr.microsoft.com/cosmosdb/linux/azure-cosmos-emulator:vnext-preview"))
                .withExposedPorts(8081, 1234)
                .withEnv("CERT_SECRET", "changeit")
                .withFileSystemBind("/Users/nikugupta/projects/cosmos-connection-docker-java/script/certs/localhost.pfx", "/localhost.pfx", BindMode.READ_ONLY)
                .withFileSystemBind("/Users/nikugupta/projects/cosmos-connection-docker-java/script/certs/ca-key", "/ca-key", BindMode.READ_ONLY)
                .withCommand("--protocol https --cert-path /localhost.pfx --key-file /ca-key");

        cosmosDbEmulator.start();
        Thread.sleep(30000);
        new CosmosConnection().getCosmosClient();
        System.out.println("done");
        cosmosDbEmulator.stop();
    }
}
