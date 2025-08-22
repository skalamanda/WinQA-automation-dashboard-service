package com.qa.automation.model;

public class CombinedSaveRequest {
    private String notes;
    private Long automationTesterId;
    private Long manualTesterId;
    private Long projectId; // NEW: Added project support

    public CombinedSaveRequest() {
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    @Override
    public String toString() {
        return "CombinedSaveRequest{" +
                "notes='" + notes + '\'' +
                ", automationTesterId=" + automationTesterId +
                ", manualTesterId=" + manualTesterId +
                ", projectId=" + projectId +
                '}';
    }
}
