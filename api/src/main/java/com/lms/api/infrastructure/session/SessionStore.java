package com.lms.api.infrastructure.session;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionStore {
    private final Map<String, SessionUser> sessions = new ConcurrentHashMap<String, SessionUser>();

    public String createSession(SessionUser user) {
        String token = UUID.randomUUID().toString();
        sessions.put(token, user);
        return token;
    }

    public SessionUser getUser(String token) {
        return sessions.get(token);
    }
}
