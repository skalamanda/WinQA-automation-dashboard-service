package com.qa.automation.model;

public class TesterAssignmentRequest {
    private Long automationTesterId;
    private Long manualTesterId;

    public TesterAssignmentRequest() {
    }

    public Long getAutomationTesterId() {
        return automationTesterId;
    }

    public void setAutomationTesterId(Long automationTesterId) {
        this.automationTesterId = automationTesterId;
    }

    public Long getManualTesterId() {
        return manualTesterId;
    }

    public void setManualTesterId(Long manualTesterId) {
        this.manualTesterId = manualTesterId;
    }

    @Override
    public String toString() {
        return "TesterAssignmentRequest{" +
                "automationTesterId=" + automationTesterId +
                ", manualTesterId=" + manualTesterId +
                '}';
    }
}
