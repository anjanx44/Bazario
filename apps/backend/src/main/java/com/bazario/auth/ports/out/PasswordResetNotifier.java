package com.bazario.auth.ports.out;

public interface PasswordResetNotifier {
    void sendResetToken(String email, String token);
}