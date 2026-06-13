package com.yourname.zerotrust.risk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.yourname.zerotrust.entity.Device;
import com.yourname.zerotrust.entity.User;

public class RiskBreakdown {
    private int userRisk;
    private int deviceRisk;
    private int contextRisk;
    private int finalRisk;
    private List<String> reasons = new ArrayList<>();

    public int getUserRisk() { return userRisk; }
    public void setUserRisk(int userRisk) { this.userRisk = userRisk; }
    public int getDeviceRisk() { return deviceRisk; }
    public void setDeviceRisk(int deviceRisk) { this.deviceRisk = deviceRisk; }
    public int getContextRisk() { return contextRisk; }
    public void setContextRisk(int contextRisk) { this.contextRisk = contextRisk; }
    public int getFinalRisk() { return finalRisk; }
    public void setFinalRisk(int finalRisk) { this.finalRisk = finalRisk; }
    public List<String> getReasons() { return reasons; }
    public void setReasons(List<String> reasons) { this.reasons = reasons; }
    public void addReason(String reason) { this.reasons.add(reason); }

    public static RiskBreakdown empty() {
        RiskBreakdown breakdown = new RiskBreakdown();
        breakdown.setReasons(Collections.emptyList());
        return breakdown;
    }
}
