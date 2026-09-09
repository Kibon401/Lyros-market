# Use a single Gradle image to build and run the application.
FROM gradle:8.5.0-jdk17

# Set the working directory inside the container
WORKDIR /app

# Copy the entire project into the container
COPY . .

# Make the gradlew and start scripts executable
RUN chmod +x ./gradlew
RUN chmod +x ./start.sh

# Run the shadowJar task to build the executable JAR
RUN ./gradlew shadowJar --no-daemon

# Expose the port the application will run on.
EXPOSE 8080

# The command to run the application when the container starts
CMD ["./start.sh"]