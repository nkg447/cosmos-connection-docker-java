FROM openjdk:11-jre-slim

# Set the working directory in the container
WORKDIR /opt/certs

# Copy the bash script into the container
COPY generate-certs.sh /opt/certs/setup.sh
COPY trustprompt /opt/certs/trustprompt

# Install necessary utilities
RUN apt-get update && \
    apt-get install -y openssl

# Run the specified command within the container
CMD ["bash", "/opt/certs/setup.sh"]
