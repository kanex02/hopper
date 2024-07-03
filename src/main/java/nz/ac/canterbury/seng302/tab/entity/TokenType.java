package nz.ac.canterbury.seng302.tab.entity;

public enum TokenType {

    REGISTRATION(60L * 60 * 2),
    PASSWORD_RESET(60L * 60);

    private final long expirationSeconds;

    TokenType(long expirationSeconds) {
        this.expirationSeconds = expirationSeconds;
    }

    public long getExpirationSeconds() {
        return expirationSeconds;
    }
}
