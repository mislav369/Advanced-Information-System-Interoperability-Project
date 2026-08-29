package com.yoanesber.backend.jwt_auth_demo.service;

public interface DatabaseService {
    String createBackup();
    void restoreBackup(String fileName);
}
