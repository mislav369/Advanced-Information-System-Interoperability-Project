package com.yoanesber.backend.jwt_auth_demo.controller;


import com.yoanesber.backend.jwt_auth_demo.dto.HttpResponseDTO;
import com.yoanesber.backend.jwt_auth_demo.dto.RestoreRequestDTO;
import com.yoanesber.backend.jwt_auth_demo.service.DatabaseService;
import com.yoanesber.backend.jwt_auth_demo.util.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/database")
public class DatabaseController {
    private final DatabaseService databaseService;
    private static final String INVALID_REQUEST = "Invalid Request";
    private static final String INTERNAL_SERVER_ERROR = "Internal Server Error";
    private static final String BACKUP_CREATED_SUCCESSFULLY = "Backup created successfully";
    private static final String DATABASE_RESTORED_SUCCESSFULLY = "Database restored successfully";

    public DatabaseController(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @PostMapping("/backup")
    public ResponseEntity<HttpResponseDTO> backup(HttpServletRequest request) {
        try {
            String fileName = databaseService.createBackup();
            return ResponseUtil.buildOkResponse(request, BACKUP_CREATED_SUCCESSFULLY, fileName);
        } catch (Exception e) {
            return ResponseUtil.buildInternalServerErrorResponse(request,
                    INTERNAL_SERVER_ERROR, e.getMessage(), null);
        }
    }

    @PostMapping("/restore")
    public ResponseEntity<HttpResponseDTO> restore(@RequestBody RestoreRequestDTO restoreRequest, HttpServletRequest request) {
        if (restoreRequest == null || restoreRequest.getFileName() == null
                || restoreRequest.getFileName().isBlank()) {
            return ResponseUtil.buildBadRequestResponse(request,
                    INVALID_REQUEST, "fileName must not be null or empty", null);
        }
        try {
            databaseService.restoreBackup(restoreRequest.getFileName());
            return ResponseUtil.buildOkResponse(request,
                    DATABASE_RESTORED_SUCCESSFULLY, restoreRequest.getFileName());
        } catch (Exception e) {
            return ResponseUtil.buildInternalServerErrorResponse(request,
                    INTERNAL_SERVER_ERROR, e.getMessage(), null);
        }
    }


}
