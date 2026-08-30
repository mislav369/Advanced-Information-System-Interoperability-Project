package hr.algebra.aisi.aisijavafxclient.service;

public class Session {
    private static String accessToken;
    private static String refreshToken;
    public static String getAccessToken() { return accessToken; }
    public static void setAccessToken(String token) { accessToken = token; }
    public static String getRefreshToken() { return refreshToken; }
    public static void setRefreshToken(String token) { refreshToken = token; }
    public static void clear() {
        accessToken = null;
        refreshToken = null;
    }
}
