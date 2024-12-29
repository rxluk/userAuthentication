package com.app.consMed.Model.User.DTOs;

import com.app.consMed.Model.User.Enums.UserRole;

public record DetailUserDTO(String login, UserRole role) {
}
