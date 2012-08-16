package org.whitesource.teamcity.server;

/**
 * POJO for storing proxy settings.
 *
 * @author Edo.Shor
 */
public class ProxySettings {

    /* --- Members --- */

    private String host;

    private int port;

    private String username;

    private String password;

    /* --- Constructors --- */

    /**
     * Default constructor
     */
    public ProxySettings() {
        port = -1;
    }

    /* --- Getters / Setters --- */

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
