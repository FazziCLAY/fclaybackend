package com.fazziclay.fclaybackend.admin;

import com.fazziclay.fclaybackend.FclaySpringApplication;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@AllArgsConstructor
public class AdminController {
    private AdminService adminService;

    @GetMapping("/reload")
    public ResponseEntity<?> reload(@RequestParam("accessToken") String accessToken) {
        return FclaySpringApplication.handle(() -> adminService.reload(accessToken), HttpStatus.OK);
    }
}
