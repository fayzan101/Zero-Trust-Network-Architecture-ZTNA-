package com.yourname.zerotrust.dto;

public class AccessComparisonResponse {
    private AccessDecisionResponse traditional;
    private AccessDecisionResponse zeroTrust;
    private boolean outcomesDiffer;

    public AccessDecisionResponse getTraditional() { return traditional; }
    public void setTraditional(AccessDecisionResponse traditional) { this.traditional = traditional; }
    public AccessDecisionResponse getZeroTrust() { return zeroTrust; }
    public void setZeroTrust(AccessDecisionResponse zeroTrust) { this.zeroTrust = zeroTrust; }
    public boolean isOutcomesDiffer() { return outcomesDiffer; }
    public void setOutcomesDiffer(boolean outcomesDiffer) { this.outcomesDiffer = outcomesDiffer; }
}
