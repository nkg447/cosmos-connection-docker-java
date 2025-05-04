package com.cosmos.demo.example;

import com.azure.cosmos.CosmosClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.containers.BindMode;

import java.util.function.Consumer;

public class CosmosDbEmulatorTest {
    public static void main(String[] args) throws InterruptedException {

        Consumer<CreateContainerCmd> cosmosPortModifier = cmd ->cmd.withPortBindings(new PortBinding(Ports.Binding.bindPort(8081), new ExposedPort(8081)), new PortBinding(Ports.Binding.bindPort(1234), new ExposedPort(1234)));

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
        Thread.sleep(5000);
        CosmosClient client = new CosmosConnection().getCosmosClient();
        client.createDatabase("test");
        System.out.println("done");
        cosmosDbEmulator.stop();
    }
}
