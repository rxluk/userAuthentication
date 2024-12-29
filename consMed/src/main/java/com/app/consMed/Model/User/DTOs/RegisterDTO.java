package com.app.consMed.Model.User.DTOs;

import com.app.consMed.Model.User.Enums.UserRole;

public record RegisterDTO(String login, String password, UserRole role) {
}
