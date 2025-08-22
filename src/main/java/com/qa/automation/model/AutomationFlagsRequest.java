package com.qa.automation.model;

public class AutomationFlagsRequest {
    private boolean canBeAutomated;
    private boolean cannotBeAutomated;

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
