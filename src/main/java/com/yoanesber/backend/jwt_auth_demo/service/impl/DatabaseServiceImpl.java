package com.yoanesber.backend.jwt_auth_demo.service.impl;


import com.yoanesber.backend.jwt_auth_demo.service.DatabaseService;
import org.hibernate.dialect.Database;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class DatabaseServiceImpl implements DatabaseService {
    @Value("${spring.datasource.username}")
    private String dbUser;
    @Value("${spring.datasource.password}")
    private String dbPassword;
    @Value("${backup.database-name}")
    private String dbName;
    @Value("${backup.container-name}")
    private String containerName;
    @Value("${backup.directory}")
    private String backupDir;

    @Override
    public String createBackup() {
        try {
            Files.createDirectories(Paths.get(backupDir));
            String fileName = "backup_"
                    + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
                    + ".sql";
            Path target = Paths.get(backupDir, fileName);
            ProcessBuilder pb = new ProcessBuilder(
                    "docker", "exec", "-e", "PGPASSWORD=" + dbPassword, containerName,
                    "pg_dump", "-U", dbUser, "-d", dbName, "--clean", "--if-exists");
            pb.redirectOutput(target.toFile());
            Process process = pb.start();
            String error = new String(process.getErrorStream().readAllBytes());
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException(error);
            }
            return fileName;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create database backup: " + e.getMessage());
        }
    }

    @Override
    public void restoreBackup(String fileName) {
        try {
            Path source = Paths.get(backupDir, fileName);
            if (!Files.exists(source)) {
                throw new RuntimeException("Backup file not found: " + fileName);
            }
            ProcessBuilder pb = new ProcessBuilder(
                    "docker", "exec", "-i", "-e", "PGPASSWORD=" + dbPassword, containerName,
                    "psql", "-U", dbUser, "-d", dbName);
            pb.redirectInput(source.toFile());
            pb.redirectErrorStream(true);
            Process process = pb.start();
            String output = new String(process.getInputStream().readAllBytes());
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException(output);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to restore database backup: " + e.getMessage());
        }


    }
}
