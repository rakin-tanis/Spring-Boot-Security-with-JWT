package com.wakeupneo.security.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.wakeupneo.security.config.prop.LoginAttemptCacheProp;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
public class LoginAttemptService {

    private final LoginAttemptCacheProp loginAttemptCacheProp;
    private final LoadingCache<String, Integer> loginAttemptCache;

    public LoginAttemptService(LoginAttemptCacheProp loginAttemptCacheProp) {
        super();
        this.loginAttemptCacheProp = loginAttemptCacheProp;
        loginAttemptCache = CacheBuilder.newBuilder()
                .expireAfterWrite(loginAttemptCacheProp.getDurationInMinutes(), TimeUnit.MINUTES)
                .maximumSize(loginAttemptCacheProp.getIncorrectAttemptCount())
                .build(
                        new CacheLoader<String, Integer>() {
                            @Override
                            public Integer load(String key) throws Exception {
                                return 0;
                            }
                        }
                );
    }

    public void evictUserFromLoginAttemptCache(String username) {
        loginAttemptCache.invalidate(username);
    }

    public void addUserToLoginAttemptCache(String username) {
        int attempts = 0;
        try {
            attempts = 1 + loginAttemptCache.get(username);
            loginAttemptCache.put(username, attempts);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public boolean hasExceededMaxAttempts(String username) {
        try {
            return loginAttemptCache.get(username) >= loginAttemptCacheProp.getIncorrectAttemptCount();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return true;
    }

}
