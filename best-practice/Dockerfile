# Use a base image with Maven and Java 8
FROM maven:3.6.3-jdk-8

# Set the working directory in the container
WORKDIR /app

# Copy the pom.xml and source code to the container
COPY . .

# Build the project inside the container (this step will download all dependencies)
RUN mvn clean install -DskipTests

# The command to run the tests
CMD ["mvn", "test"]
