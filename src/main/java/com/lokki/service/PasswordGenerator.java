package com.lokki.service;

import java.security.SecureRandom;

public abstract class PasswordGenerator {

    protected static final SecureRandom RANDOM = new SecureRandom();

    public abstract String generate();

}
