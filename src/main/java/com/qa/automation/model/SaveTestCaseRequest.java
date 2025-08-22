package com.qa.automation.model;

public class SaveTestCaseRequest {
    private Long projectId;
    private Long testerId;
    private boolean canBeAutomated;
    private boolean cannotBeAutomated;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getTesterId() {
        return testerId;
    }

    public void setTesterId(Long testerId) {
        this.testerId = testerId;
    }

    public boolean getCanBeAutomated() {
        return canBeAutomated;
    }

    public void setCanBeAutomated(boolean canBeAutomated) {
        this.canBeAutomated = canBeAutomated;
    }

    public boolean getCannotBeAutomated() {
        return cannotBeAutomated;
    }

    public void setCannotBeAutomated(boolean cannotBeAutomated) {
        this.cannotBeAutomated = cannotBeAutomated;
    }
}