package com.example.onlinequiz.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DebugController {
    
    private static final Logger logger = LoggerFactory.getLogger(DebugController.class);
    
    @GetMapping("/debug/auth")
    public String debugAuth(Authentication authentication, Model model) {
        if (authentication != null) {
            logger.info("=== DEBUG AUTHENTICATION ===");
            logger.info("Name: {}", authentication.getName());
            logger.info("Authenticated: {}", authentication.isAuthenticated());
            logger.info("Authorities: {}", authentication.getAuthorities());
            
            model.addAttribute("name", authentication.getName());
            model.addAttribute("authenticated", authentication.isAuthenticated());
            model.addAttribute("authorities", authentication.getAuthorities());
        } else {
            logger.info("=== DEBUG: NO AUTHENTICATION ===");
            model.addAttribute("authenticated", false);
        }
        return "debug/auth";
    }
}