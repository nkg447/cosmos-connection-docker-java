package com.cosmos.demo.example;

import com.azure.cosmos.CosmosClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.containers.BindMode;

import java.util.function.Consumer;

public class CosmosDbEmulatorTest {

    private static final Logger logger = LoggerFactory.getLogger(CosmosDbEmulatorTest.class);
    private static final String KEY = "C2y6yDjf5/R+ob0N8A7Cgv30VRDJIWEHLM+4QDU5DE2nQ9nDuVTqobD4b8mGGyPMbIZnqyMsEcaGQy67XIw/Jw==";

    public static void main(String[] args) throws InterruptedException {
        Consumer<CreateContainerCmd> cosmosPortModifier = cmd -> cmd.withPortBindings(new PortBinding(Ports.Binding.bindPort(8081), new ExposedPort(8081)), new PortBinding(Ports.Binding.bindPort(1234), new ExposedPort(1234)));

        logger.info("Starting Cosmos DB Emulator");
        GenericContainer<?> cosmosDbEmulator = new GenericContainer<>(DockerImageName.parse("mcr.microsoft.com/cosmosdb/linux/azure-cosmos-emulator:vnext-preview"))
                .withCreateContainerCmdModifier(cmd -> {
                    cmd.withName("cosmos");
                })
                .withNetworkAliases("cosmos.domain")
                .withExposedPorts(8081, 1234)
                .withCreateContainerCmdModifier(cosmosPortModifier)
                .withEnv("CERT_SECRET", "changeit")
                .withFileSystemBind("/Users/nikugupta/projects/cosmos-connection-docker-java/script/certs/localhost.pfx", "/localhost.pfx", BindMode.READ_ONLY)
                .withFileSystemBind("/Users/nikugupta/projects/cosmos-connection-docker-java/script/certs/ca-key", "/ca-key", BindMode.READ_ONLY)
                .withCommand("--protocol https --cert-path /localhost.pfx --key-file /ca-key");

        cosmosDbEmulator.start();
        logger.info("Cosmos DB Emulator started");
        // wait for the emulator to start
        Thread.sleep(5000);
        logger.info("Creating Cosmos Client");
        CosmosClient client = new CosmosConnection(
                "localhost", "8081", KEY
        ).getCosmosClient();
        logger.info("Creating database");
        client.createDatabase("test");
        logger.info("Database created");
        cosmosDbEmulator.stop();
        logger.info("Cosmos DB Emulator stopped");
    }
}
