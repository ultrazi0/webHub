-- Properties are injected by Gradle when running processResources
CREATE USER "$databaseUsername" WITH PASSWORD '$databasePassword' LOGIN INHERIT;

CREATE DATABASE "$databaseName" OWNER "$databaseUsername"
