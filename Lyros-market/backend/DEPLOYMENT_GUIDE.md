# Documentation: Deploying a Ktor Application with a Supabase Database on Render

## **Objective**

This document provides a complete, step-by-step guide for migrating a Ktor application from a local MySQL development environment to a production-ready cloud setup. The final architecture will use:

*   **Hosting:** Render
*   **Database:** Supabase (PostgreSQL)
*   **Deployment Method:** Docker

This guide is structured to help you understand not just the steps, but *why* each step is necessary, based on real-world deployment challenges.

## **Prerequisites**

Before you begin, ensure you have the following:

1.  A working Ktor application project.
2.  A local MySQL database with existing data.
3.  Git installed and your project pushed to a GitLab (or GitHub) repository.
4.  An account on Supabase.
5.  An account on Render.

---

## **Phase 1: Database Migration (from MySQL to Supabase)**

Supabase uses PostgreSQL, which has a different SQL syntax than MySQL. You cannot directly import a MySQL data dump. This phase covers the conversion and migration of your database schema and data.

### **Step 1.1: Create a Supabase Project**

1.  Log in to your Supabase account and create a **New Project**.
2.  Assign a name and generate a strong password. **Save this password immediately** in a secure location.
3.  Select a region that is geographically close to you or your users.
4.  Wait for the project to be provisioned.

### **Step 1.2: Convert the SQL Schema**

You must manually translate your MySQL `CREATE TABLE` statements to PostgreSQL syntax.

**Key Syntax Differences:**

| MySQL                | PostgreSQL           | Description                                                        |
| :------------------- | :------------------- | :----------------------------------------------------------------- |
| `AUTO_INCREMENT`     | `SERIAL PRIMARY KEY` | For auto-incrementing integer IDs.                                 |
| `datetime`           | `TIMESTAMP`          | For date and time values.                                          |
| `tinyint(1)`         | `BOOLEAN`            | For true/false values.                                             |
| `` `backticks` ``    | `"`double quotes"`"` | PostgreSQL uses standard double quotes for identifiers if needed.  |
| `ENGINE=InnoDB;`     | (remove)             | This is a MySQL-specific clause and must be removed.               |

**Example:**

*   **Original (MySQL):**
    ```sql
    CREATE TABLE `site_user` (
      `id` int NOT NULL AUTO_INCREMENT,
      `is_active` tinyint(1) NOT NULL DEFAULT '1',
      PRIMARY KEY (`id`)
    ) ENGINE=InnoDB;
    ```

*   **Converted (PostgreSQL):**
    ```sql
    CREATE TABLE site_user (
      id SERIAL PRIMARY KEY,
      is_active BOOLEAN NOT NULL DEFAULT TRUE
    );
    ```

### **Step 1.3: Import Schema and Data into Supabase**

1.  In your Supabase project, navigate to the **SQL Editor**.
2.  Copy all of your converted PostgreSQL `CREATE TABLE` and `INSERT INTO` statements into the editor.
3.  Click **RUN**. Your database is now populated and ready.

---

## **Phase 2: Ktor Application Configuration**

Next, configure your Ktor application to connect to the new PostgreSQL database.

### **Step 2.1: Update Gradle Dependencies (`build.gradle.kts`)**

The application needs the PostgreSQL driver to communicate with the database.

1.  Open your `build.gradle.kts` file.
2.  **Add the `repositories` block** to tell Gradle where to find libraries. This is a common point of failure.
3.  **Replace the MySQL dependency** with the PostgreSQL dependency.

    ```kotlin
    // build.gradle.kts

    // ADD THIS BLOCK IF IT'S MISSING
    repositories {
        mavenCentral()
    }

    dependencies {
        // ... other dependencies

        // implementation(libs.mysql.connector.java) // REMOVE THIS
        implementation("org.postgresql:postgresql:42.6.0") // ADD THIS

        // ... other dependencies
    }
    ```

### **Step 2.2: Update the Database Connection Factory (`DatabaseFactory.kt`)**

Your connection code must be updated to use the PostgreSQL driver and read credentials securely from environment variables.

1.  Open your `DatabaseFactory.kt` file.
2.  Refactor the `init` function to remove any local development fallbacks and hardcoded passwords. The code should *only* rely on environment variables.

    ```kotlin
    // src/main/kotlin/com/example/data/database/DatabaseFactory.kt

    object DatabaseFactory {
        fun init() {
            val dbHost = System.getenv("DB_HOST")
            val dbPort = System.getenv("DB_PORT")
            val dbName = System.getenv("DB_NAME")
            val dbUser = System.getenv("DB_USER")
            val dbPassword = System.getenv("DB_PASSWORD")

            // Use the PostgreSQL driver and URL format
            val driverClassName = "org.postgresql.Driver"
            val jdbcUrl = "jdbc:postgresql://$dbHost:$dbPort/$dbName"
            
            println("Connecting to Supabase (PostgreSQL) database...")
            val dataSource = createHikariDataSource(jdbcUrl, driverClassName, dbUser, dbPassword)
            Database.connect(dataSource)

            // ... your SchemaUtils and data seeding logic
        }
        // ...
    }
    ```

---

## **Phase 3: Deployment Preparation (Docker)**

We will use Docker to package the application so Render can run it reliably.

### **Step 3.1: Create the `Dockerfile`**

This file contains the instructions to build and run your application. We use a single-stage build for simplicity and to avoid issues with build servers being unable to find base images.

*   Create a file named `Dockerfile` in your project's root directory with the following content:

    ```
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
    ```

### **Step 3.2: Create the Startup Script (`start.sh`)**

A startup script allows us to solve platform-specific networking issues. The most critical issue we faced was the server trying to use IPv6, which was incompatible with the host network. This script forces the application to use the more reliable IPv4.

*   Create a file named `start.sh` in your project's root directory:

    ```sh
    #!/bin/sh
    # start.sh

    echo "--- RUNNING APPLICATION (forcing IPv4) ---"
    # The -Djava.net.preferIPv4Stack=true flag forces the JVM to use IPv4,
    # resolving 'Network is unreachable' errors on some hosting platforms.
    java -Djava.net.preferIPv4Stack=true -jar build/libs/kijani-market-all.jar -port=8080
    ```

### **Step 3.3: Standardize the Project Name**

Project names with spaces can cause unpredictable artifact names (e.g., `"kijani market-all.jar"`). It's a best practice to use hyphens.

*   Open `settings.gradle.kts` and ensure its content is:
    ```kotlin
    rootProject.name = "kijani-market"
    ```

### **Step 3.4: Secure Your Credentials**

Never commit secret keys, passwords, or API credentials to your Git repository.

1.  **Create a `.gitignore` file** in your project root and add the following lines to ignore secrets and build files:
    ```
    # Local environment variables
    .env

    # Build outputs
    build/
    .gradle/

    # IDE files
    .idea/
    ```
2.  **Create a local `.env` file** to store your secrets. You will use this file to copy-paste variables into Render's dashboard. **Do not commit this file.**

---

## **Phase 4: Deploying to Render**

With all preparations complete, you are ready to deploy.

1.  **Push Code:** Commit all your changes (`Dockerfile`, `start.sh`, `build.gradle.kts`, etc.) and push them to your GitLab repository.
2.  **Create Render Service:**
    *   Log in to Render and click **New +** -> **Web Service**.
    *   Connect your GitLab repository and select your project.
    *   Give the service a name (e.g., `kijani-market-server`).
    *   Set the **Environment** to **Docker**. Render will automatically detect and use your `Dockerfile`.
3.  **Configure Environment Variables (Most Critical Step):**
    *   Go to the **Environment** tab for your new service.
    *   You **must** use the credentials for the **Session Pooler**, which is designed for serverless environments like Render.
    *   In your Supabase project, go to **Project Settings -> Database**, and click the **"Session pooler"** tab.
    *   Use the information from that tab to create the following environment variables in Render:

| Variable Name | Value from Supabase "Session Pooler"      |
| :------------ | :---------------------------------------- |
| `DB_HOST`     | `aws-0-[your-region].pooler.supabase.com` |
| `DB_PORT`     | `5432`                                    |
| `DB_USER`     | `postgres.[your-project-id]`              |
| `DB_PASSWORD` | `[YOUR-DATABASE-PASSWORD]`                |

4.  **Add Other Secrets:** Add your `MPESA_` and any other secret variables in the same Environment tab.
5.  **Deploy:** Click **Create Web Service**. Render will now build and deploy your application.

---

## **Phase 5: Post-Deployment**

Your service is now live and will have a public URL like `https://kijani-market-server.onrender.com`.

1.  **Update M-Pesa Callback URL:** Go back to the **Environment** tab in Render. Update the `MPESA_CALLBACK_URL` variable with your new public URL.
2.  Save the changes. Render will automatically restart your service with the new value.

**Your application is now fully deployed and operational.**

---

## **Troubleshooting Guide: Common Deployment Errors**

This section summarizes the challenges faced during this deployment and their solutions.

| Error Message                               | Root Cause                                                                                                                              | Solution                                                                                                                                                           |
| :------------------------------------------ | :-------------------------------------------------------------------------------------------------------------------------------------- | :----------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `image: not found` (e.g., `openjdk:17`)     | The build server (on Railway or Render) could not find or download the requested base Docker image.                                     | Use a single, highly reliable base image for both building and running. We used `gradle:8.5.0-jdk17`.                                                           |
| `./gradlew: Permission denied`              | The `gradlew` script lost its "executable" permission inside the Docker container.                                                      | Add `RUN chmod +x ./gradlew` to your `Dockerfile` before the script is run.                                                                                        |
| `Unable to access jarfile`                  | The filename of the JAR in the `CMD` instruction did not match the actual file created by the build.                                    | Fix the project name in `settings.gradle.kts` to remove spaces. Ensure the `CMD` path is correct.                                                                  |
| `Connection refused`                        | The application was using the wrong database driver (e.g., MySQL driver for a PostgreSQL database).                                     | Change the dependency in `build.gradle.kts` and the driver class name in `DatabaseFactory.kt` to PostgreSQL.                                                       |
| `Network is unreachable`                    | The JVM tried to use IPv6, but the hosting platform's network did not support it for outbound connections.                              | Force the JVM to use IPv4 by adding `-Djava.net.preferIPv4Stack=true` to the `java` command in `start.sh`.                                                          |
| `UnknownHostException` or `(ENOIDENTIFIER)` | The application was using Supabase's direct connection endpoint, which is incompatible with Render's DNS, or was missing the project identifier for the pooler. | Use the **Session Pooler** credentials from Supabase, which provide a different hostname, port, and user format (`postgres.[project-id]`).                     |
