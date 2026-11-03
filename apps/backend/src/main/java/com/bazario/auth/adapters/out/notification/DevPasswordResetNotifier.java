package com.bazario.auth.adapters.out.notification;

import com.bazario.auth.ports.out.PasswordResetNotifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/** Development placeholder; reset tokens are deliberately not delivered or logged. */
@Component
@Profile("dev")
public class DevPasswordResetNotifier implements PasswordResetNotifier {

    @Override
    public void sendResetToken(String email, String token) {
    }
}