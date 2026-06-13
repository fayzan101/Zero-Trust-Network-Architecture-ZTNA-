package com.yourname.zerotrust.auth;

public class StepUpDecision {
    private final StepUpAction action;
    private final String level;
    private final String reason;

    public StepUpDecision(StepUpAction action, String level, String reason) {
        this.action = action;
        this.level = level;
        this.reason = reason;
    }

    public StepUpAction getAction() { return action; }
    public String getLevel() { return level; }
    public String getReason() { return reason; }
}
