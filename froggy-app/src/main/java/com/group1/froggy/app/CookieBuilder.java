package com.group1.froggy.app;

import lombok.NonNull;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public final class CookieBuilder {
    private final Map.Entry<String, String> cookie;
    private final Map<String, String> fields = new HashMap<>();

    private CookieBuilder(Map.Entry<String, String> cookie) {
        this.cookie = cookie;
    }


    /**
     * Creates a new CookieBuilder instance with the specified name and value.
     * You can then chain methods to set additional properties for the cookie.
     * Note: Calling the same method multiple times will overwrite the previous value.
     *
     * Example:
     * <pre>
     * CookieBuilder cookieBuilder = CookieBuilder.of("sessionId", "abc123")
     *    .withPath("/path")
     *    .withPath("/newPath")
     *    .build();
     * </pre>
     * The resulting cookie string will be: `"sessionId=abc123; Path=/newPath"`
     *
     * @param name The name/key of the cookie.
     * @param value The value of the cookie.
     * @return A new CookieBuilder instance.
     */
    public static CookieBuilder of(@NonNull String name, @NonNull String value) {
        return new CookieBuilder(Map.entry(name, value));
    }

    /**
     * Sets the path for which the cookie is valid.
     * @param path The path for which the cookie is valid. The cookie will be sent to the server only if the request URL path starts with this value.
     */
    public CookieBuilder withPath(@NonNull String path) {
        fields.put("Path", path);
        return this;
    }

    /**
     * Sets the domain for which the cookie is valid.
     * @param domain The domain for which the cookie is valid. The cookie will be sent to the server only if the request URL domain matches this value.
     */
    public CookieBuilder withDomain(@NonNull String domain) {
        fields.put("Domain", domain);
        return this;
    }

    /**
     * Sets the maximum age of the cookie in seconds.
     * @param maxAge The maximum age of the cookie in seconds. After this time, the cookie will be deleted.
     */
    public CookieBuilder withMaxAge(@NonNull Integer maxAge) {
        fields.put("Max-Age", maxAge.toString());
        return this;
    }

    /**
     * Sets the maximum age of the cookie.
     * @param maxAge The maximum age of the cookie. After this time, the cookie will be deleted.
     */
    public CookieBuilder withMaxAge(@NonNull Duration maxAge) {
        fields.put("Max-Age", String.valueOf(maxAge.toSeconds()));
        return this;
    }


    /**
     * Sets whether the cookie should be marked as secure (HTTPS only).
     */
    public CookieBuilder withSecure() {
        fields.put("Secure", "");
        return this;
    }


    /**
     * Sets whether the cookie should be marked as HttpOnly (not accessible via JavaScript).
     */
    public CookieBuilder withHttpOnly() {
        fields.put("HttpOnly", "");
        return this;
    }

    /**
     * @param sameSite The SameSite attribute of the cookie. This attribute can be used to control whether the cookie is sent with cross-site requests.
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Set-Cookie/SameSite">MDN Web Docs - SameSite</a>
     */
    public CookieBuilder withSameSite(@NonNull SameSite sameSite) {
        fields.put("SameSite", sameSite.toString());
        return this;
    }

    /**
     * Builds the cookie string that can be used in the Set-Cookie header.
     * @return The cookie string that can be used in the Set-Cookie header.
     */
    public String build() {
        StringBuilder cookieStringBuilder = new StringBuilder();
        cookieStringBuilder.append(cookie.getKey()).append("=").append(cookie.getValue()).append("; ");
        fields.forEach((key, value) -> {
            if (value.isEmpty()) {
                cookieStringBuilder.append(key).append("; ");
            } else {
                cookieStringBuilder.append(key).append("=").append(value).append("; ");
            }
        });
        // Remove the last "; "
        if (!cookieStringBuilder.isEmpty()) {
            cookieStringBuilder.setLength(cookieStringBuilder.length() - 2);
        }
        return cookieStringBuilder.toString();
    }

    public enum SameSite {
        LAX("Lax"),
        STRICT("Strict"),
        NONE("None");

        private final String value;

        SameSite(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }
}
