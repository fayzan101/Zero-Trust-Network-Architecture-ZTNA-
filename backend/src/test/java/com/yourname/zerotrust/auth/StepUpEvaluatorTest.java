package com.yourname.zerotrust.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class StepUpEvaluatorTest {

    private final StepUpEvaluator evaluator = new StepUpEvaluator();

    @Test
    void evaluate_lowRisk_allows() {
        assertEquals(StepUpAction.ALLOW, evaluator.evaluate(25, false).getAction());
    }

    @Test
    void evaluate_mediumRisk_requiresStepUpWithoutMfa() {
        assertEquals(StepUpAction.REQUIRE_STEP_UP, evaluator.evaluate(55, false).getAction());
    }

    @Test
    void evaluate_mediumRisk_requiresMfaWhenEnabled() {
        assertEquals(StepUpAction.REQUIRE_MFA, evaluator.evaluate(55, true).getAction());
    }

    @Test
    void evaluate_highRisk_denies() {
        assertEquals(StepUpAction.DENY, evaluator.evaluate(75, false).getAction());
    }
}
