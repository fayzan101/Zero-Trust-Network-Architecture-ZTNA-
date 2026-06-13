package com.yourname.zerotrust.auth;

import org.springframework.stereotype.Component;

@Component
public class StepUpEvaluator {

    public static final int MEDIUM_RISK_MIN = 40;
    public static final int HIGH_RISK_MIN = 70;

    public StepUpDecision evaluate(int finalRisk, boolean mfaEnabled) {
        if (finalRisk >= HIGH_RISK_MIN) {
            return new StepUpDecision(StepUpAction.DENY, "HIGH",
                    "Risk score " + finalRisk + " exceeds step-up threshold — access blocked");
        }
        if (finalRisk >= MEDIUM_RISK_MIN) {
            if (mfaEnabled) {
                return new StepUpDecision(StepUpAction.REQUIRE_MFA, "MEDIUM",
                        "Elevated risk (" + finalRisk + ") — MFA verification required");
            }
            return new StepUpDecision(StepUpAction.REQUIRE_STEP_UP, "MEDIUM",
                    "Elevated risk (" + finalRisk + ") — step-up authentication required");
        }
        return new StepUpDecision(StepUpAction.ALLOW, "LOW",
                "Risk score " + finalRisk + " within normal range");
    }
}
