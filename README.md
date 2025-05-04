# Cosmos Connection to a Cosmos DB running in Docker

This is a simple example of how to connect to a Cosmos DB running in Docker using Java.
<br/>
Here will also create the Cosmos DB container with certs. And then we will connect to it using Java.

## Prerequisites
- Docker
- Java 8 or higher

# Getting Started
## 1. Create certs for Cosmos DB
```bash
cd script
./generate-cert.sh
```
This will create a self-signed certificate and a private key in the `certs` directory. The certificate will be used to connect to the Cosmos DB container.
## 2. Change file path to certs in CosmosDbEmulatorTest.java file.

That's it. You can now run the test.