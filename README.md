Sure, here's the README in English:

# How to Run the Application Using Docker Compose

This step-by-step guide will show you how to quickly run the "Veterinary App" application using Docker Compose. The application will run in a Docker container, along with a PostgreSQL database.

## Prerequisites

Make sure you have the following tools installed:

- [Docker](https://www.docker.com/get-started)

## Instructions

1. **Getting the Source Code:**

   Clone this repository to your computer

2. **Configuring the Database:**

   Open the `docker-compose.yml` file and update the environment variables for the database in the `db` section:

   ```yaml
   environment:
     POSTGRES_DB: mydb
     POSTGRES_USER: postgres
     POSTGRES_PASSWORD: 123
   ```

   You can change the database name, user, and password according to your preferences.

3. **Running the Application:**

   In your terminal, in the main project directory, execute the following command:

   ```bash
   docker-compose up
   ```

   Docker Compose will now pull the images and start two containers: `app` (Java application) and `db` (PostgreSQL).

4. **Accessing the Application:**

   Once the build and startup process is complete, your "Veterinary App" application will be accessible at [http://localhost:8080](http://localhost:8080).

5. **Stopping the Application:**

   To stop the application, go to the terminal where Docker Compose is running and use the `Ctrl+C` key combination.

   Alternatively, if you want to completely remove the containers, type:

   ```bash
   docker-compose down
   ```

## Summary

You should now be able to run the "Veterinary App" application in a Docker container using Docker Compose. You can edit the application code or container configurations to tailor it to your needs.

**Note:** This guide assumes you have basic knowledge of using the terminal and Docker. If you need more information about these tools, we recommend referring to their official documentation.
