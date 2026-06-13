package com.yourname.zerotrust.dto;

public class CredentialTheftRequest {
    private String username;
    private String stolenFromIp;
    private String attackerDeviceId;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getStolenFromIp() { return stolenFromIp; }
    public void setStolenFromIp(String stolenFromIp) { this.stolenFromIp = stolenFromIp; }
    public String getAttackerDeviceId() { return attackerDeviceId; }
    public void setAttackerDeviceId(String attackerDeviceId) { this.attackerDeviceId = attackerDeviceId; }
}
