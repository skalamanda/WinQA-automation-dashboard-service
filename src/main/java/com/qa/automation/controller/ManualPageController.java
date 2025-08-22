package com.qa.automation.controller;

import com.qa.automation.config.JiraConfig;
import com.qa.automation.dto.JiraIssueDto;
import com.qa.automation.dto.JiraTestCaseDto;
import com.qa.automation.model.AutomationFlagsRequest;
import com.qa.automation.model.Domain;
import com.qa.automation.model.GlobalKeywordSearchRequest;
import com.qa.automation.model.KeywordSearchRequest;
import com.qa.automation.model.Project;
import com.qa.automation.model.SaveTestCaseRequest;
import com.qa.automation.model.TestCaseMappingRequest;
import com.qa.automation.model.Tester;
import com.qa.automation.service.JiraIntegrationService;
import com.qa.automation.service.ManualPageService;
import com.qa.automation.service.QTestService;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/manual-page")
public class ManualPageController {

    private static final Logger logger = LoggerFactory.getLogger(ManualPageController.class);

    @Autowired
    private ManualPageService manualPageService;

    @Autowired
    private JiraIntegrationService jiraIntegrationService;

    @Autowired
    private QTestService qTestService;

    @Autowired
    private JiraConfig jiraConfig;

    @GetMapping("/sprints")
    public ResponseEntity<List<Map<String, Object>>> getAvailableSprints(
            @RequestParam(required = false) String jiraProjectKey,
            @RequestParam(required = false) String jiraBoardId) {
        try {
            logger.info("Fetching available sprints (Project: {}, Board: {})", jiraProjectKey, jiraBoardId);
            List<Map<String, Object>> sprints = manualPageService.getAvailableSprints(jiraProjectKey, jiraBoardId);
            return ResponseEntity.ok(sprints);
        }
        catch (Exception e) {
            logger.error("Error fetching sprints: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }


    @PostMapping("/sprints/{sprintId}/sync-with-mapping")
    public ResponseEntity<List<JiraIssueDto>> syncSprintIssuesWithMapping(
            @PathVariable String sprintId,
            @RequestParam(required = false) String jiraProjectKey,
            @RequestParam(required = false) String jiraBoardId,
            @RequestParam(required = false) Long domainId,
            @RequestParam(required = false) Long projectId) {
        try {
            logger.info("Syncing issues for sprint: {} with domain/project mapping (Project: {}, Board: {}, Domain: {}, Project: {})",
                    sprintId, jiraProjectKey, jiraBoardId, domainId, projectId);
            List<JiraIssueDto> issues = manualPageService.fetchAndSyncSprintIssues(
                    sprintId, jiraProjectKey, jiraBoardId, domainId, projectId);
            return ResponseEntity.ok(issues);
        }
        catch (Exception e) {
            logger.error("Error syncing sprint issues with mapping: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/sprints/{sprintId}/sync")
    public ResponseEntity<List<JiraIssueDto>> syncSprintIssues(
            @PathVariable String sprintId,
            @RequestParam(required = false) String jiraProjectKey,
            @RequestParam(required = false) String jiraBoardId) {
        try {
            logger.info("Syncing issues for sprint: {} (Project: {}, Board: {})",
                    sprintId, jiraProjectKey, jiraBoardId);
            List<JiraIssueDto> issues = manualPageService.fetchAndSyncSprintIssues(
                    sprintId, jiraProjectKey, jiraBoardId);
            return ResponseEntity.ok(issues);
        }
        catch (Exception e) {
            logger.error("Error syncing sprint issues: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/sprints/{sprintId}/issues")
    public ResponseEntity<List<JiraIssueDto>> getSprintIssues(@PathVariable String sprintId) {
        try {
            logger.info("Getting issues for sprint: {}", sprintId);
            List<JiraIssueDto> issues = manualPageService.getSprintIssues(sprintId);
            return ResponseEntity.ok(issues);
        }
        catch (Exception e) {
            logger.error("Error getting sprint issues: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/test-cases/{testCaseId}/automation-flags")
    public ResponseEntity<JiraTestCaseDto> updateAutomationFlags(
            @PathVariable("testCaseId") String testCaseIdStr,
            @RequestBody AutomationFlagsRequest request) {
        try {
            if (testCaseIdStr == null || testCaseIdStr.trim().isEmpty() || "null".equalsIgnoreCase(testCaseIdStr.trim())) {
                logger.warn("Received null/empty testCaseId in path: {}", testCaseIdStr);
                return ResponseEntity.badRequest().build();
            }
            Long testCaseId = Long.parseLong(testCaseIdStr.trim());
            logger.info("Updating automation flags for test case: {}", testCaseId);
            JiraTestCaseDto updatedTestCase = manualPageService.updateTestCaseAutomationFlags(
                    testCaseId,
                    request.getCanBeAutomated(),
                    request.getCannotBeAutomated()
            );
            return ResponseEntity.ok(updatedTestCase);
        }
        catch (NumberFormatException nfe) {
            logger.warn("Invalid testCaseId path value: {}", testCaseIdStr);
            return ResponseEntity.badRequest().build();
        }
        catch (Exception e) {
            logger.error("Error updating automation flags: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/sprints/{sprintId}/sync-and-statistics")
    public ResponseEntity<Map<String, Object>> syncAndGetStatistics(
            @PathVariable String sprintId,
            @RequestParam(required = false) String jiraProjectKey,
            @RequestParam(required = false) String jiraBoardId) {
        try {
            logger.info("Syncing sprint {} and returning statistics", sprintId);
            manualPageService.fetchAndSyncSprintIssues(sprintId, jiraProjectKey, jiraBoardId);
            Map<String, Object> stats = manualPageService.getSprintAutomationStatistics(sprintId);
            return ResponseEntity.ok(stats);
        }
        catch (Exception e) {
            logger.error("Error syncing and fetching statistics: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }


    @PutMapping("/test-cases/{testCaseId}/save")
    public ResponseEntity<JiraTestCaseDto> saveMappingAndFlags(
            @PathVariable("testCaseId") String testCaseIdStr,
            @RequestBody SaveTestCaseRequest request) {
        try {
            if (testCaseIdStr == null || testCaseIdStr.trim().isEmpty() || "null".equalsIgnoreCase(testCaseIdStr.trim())) {
                return ResponseEntity.badRequest().build();
            }
            Long testCaseId = Long.parseLong(testCaseIdStr.trim());

            // First map project/tester if provided
            JiraTestCaseDto dto = manualPageService.mapTestCaseToProject(testCaseId, request.getProjectId(), request.getTesterId());
            // Then update flags
            dto = manualPageService.updateTestCaseAutomationFlags(testCaseId, request.getCanBeAutomated(), request.getCannotBeAutomated());
            return ResponseEntity.ok(dto);
        }
        catch (NumberFormatException nfe) {
            return ResponseEntity.badRequest().build();
        }
        catch (Exception e) {
            logger.error("Error saving mapping and flags: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/test-cases/{testCaseId}/mapping")
    public ResponseEntity<JiraTestCaseDto> mapTestCase(
            @PathVariable Long testCaseId,
            @RequestBody TestCaseMappingRequest request) {
        try {
            logger.info("Mapping test case {} to project {} and tester {}",
                    testCaseId, request.getProjectId(), request.getTesterId());
            JiraTestCaseDto updatedTestCase = manualPageService.mapTestCaseToProject(
                    testCaseId,
                    request.getProjectId(),
                    request.getTesterId()
            );
            return ResponseEntity.ok(updatedTestCase);
        }
        catch (Exception e) {
            logger.error("Error mapping test case: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/issues/{jiraKey}/keyword-search")
    public ResponseEntity<JiraIssueDto> searchKeywordInComments(
            @PathVariable String jiraKey,
            @RequestBody KeywordSearchRequest request) {
        try {
            logger.info("Searching for keyword '{}' in issue: {}", request.getKeyword(), jiraKey);
            JiraIssueDto updatedIssue = manualPageService.searchKeywordInIssue(jiraKey, request.getKeyword());
            return ResponseEntity.ok(updatedIssue);
        }
        catch (Exception e) {
            logger.error("Error searching keyword in comments: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/global-keyword-search")
    public ResponseEntity<Map<String, Object>> globalKeywordSearch(
            @RequestBody GlobalKeywordSearchRequest request) {
        try {
            logger.info("Performing global keyword search for '{}' in project: {} sprint: {}",
                    request.getKeyword(), request.getJiraProjectKey(), request.getSprintId());
            Map<String, Object> searchResults = jiraIntegrationService.searchKeywordGlobally(
                    request.getKeyword(), request.getJiraProjectKey(), request.getSprintId());
            return ResponseEntity.ok(searchResults);
        }
        catch (Exception e) {
            logger.error("Error performing global keyword search: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/sprints/{sprintId}/statistics")
    public ResponseEntity<Map<String, Object>> getSprintStatistics(@PathVariable String sprintId) {
        try {
            logger.info("Getting automation statistics for sprint: {}", sprintId);
            Map<String, Object> statistics = manualPageService.getSprintAutomationStatistics(sprintId);
            return ResponseEntity.ok(statistics);
        }
        catch (Exception e) {
            logger.error("Error getting sprint statistics: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/projects")
    public ResponseEntity<List<Project>> getAllProjects() {
        try {
            logger.info("Getting all projects for mapping");
            List<Project> projects = manualPageService.getAllProjects();
            return ResponseEntity.ok(projects);
        }
        catch (Exception e) {
            logger.error("Error getting projects: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/domains")
    public ResponseEntity<List<Domain>> getAllDomains() {
        try {
            logger.info("Getting all domains for filtering");
            List<Domain> domains = manualPageService.getAllDomains();
            return ResponseEntity.ok(domains);
        }
        catch (Exception e) {
            logger.error("Error getting domains: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/testers")
    public ResponseEntity<List<Tester>> getAllTesters() {
        try {
            logger.info("Getting all testers for assignment");
            List<Tester> testers = manualPageService.getAllTesters();
            return ResponseEntity.ok(testers);
        }
        catch (Exception e) {
            logger.error("Error getting testers: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/test-connection")
    public ResponseEntity<Map<String, Object>> testConnection() {
        try {
            logger.info("Testing Jira connection");
            boolean isConnected = jiraIntegrationService.testConnection();
            Map<String, Object> response = Map.of(
                    "connected", isConnected,
                    "message", isConnected ? "Successfully connected to Jira" : "Failed to connect to Jira"
            );
            return ResponseEntity.ok(response);
        }
        catch (Exception e) {
            logger.error("Error testing connection: {}", e.getMessage(), e);
            Map<String, Object> response = Map.of(
                    "connected", false,
                    "message", "Error testing connection: " + e.getMessage()
            );
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/qtest/test-connection")
    public ResponseEntity<Map<String, Object>> testQTestConnection() {
        try {
            logger.info("Testing QTest connection");

            Map<String, Object> result = new HashMap<>();
            boolean connected = qTestService.testConnection();
            result.put("connected", connected);
            result.put("message", connected ? "QTest connection successful" : "QTest connection failed");
            result.put("timestamp", new Date());
            result.put("authenticated", qTestService.isAuthenticated());

            return ResponseEntity.ok(result);
        }
        catch (Exception e) {
            logger.error("Error testing QTest connection: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("connected", false);
            result.put("message", "Connection test failed: " + e.getMessage());
            result.put("timestamp", new Date());
            result.put("authenticated", false);
            return ResponseEntity.ok(result);
        }
    }

    @PostMapping("/qtest/retry-auth")
    public ResponseEntity<Map<String, Object>> retryQTestAuthentication() {
        try {
            logger.info("Retrying QTest authentication");

            qTestService.retryAuthentication();
            boolean connected = qTestService.testConnection();

            Map<String, Object> result = new HashMap<>();
            result.put("success", connected);
            result.put("message", connected ? "QTest authentication successful" : "QTest authentication failed");
            result.put("timestamp", new Date());
            result.put("authenticated", qTestService.isAuthenticated());

            return ResponseEntity.ok(result);
        }
        catch (Exception e) {
            logger.error("Error retrying QTest authentication: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "Authentication retry failed: " + e.getMessage());
            result.put("timestamp", new Date());
            result.put("authenticated", false);
            return ResponseEntity.ok(result);
        }
    }

    @GetMapping("/qtest/status")
    public ResponseEntity<Map<String, Object>> getQTestStatus() {
        try {
            logger.info("Checking QTest configuration status");

            Map<String, Object> result = new HashMap<>();

            // Check configuration
            boolean configured = jiraConfig.isQTestConfigured();
            result.put("configured", configured);
            result.put("authenticated", qTestService.isAuthenticated());

            // Configuration details (without sensitive info)
            Map<String, Object> config = new HashMap<>();
            config.put("url", jiraConfig.getQtestUrl());
            config.put("username", jiraConfig.getQtestUsername());
            config.put("projectId", jiraConfig.getQtestProjectId());
            config.put("hasPassword", jiraConfig.getQtestPassword() != null && !jiraConfig.getQtestPassword().isEmpty());
            config.put("hasToken", jiraConfig.getQtestToken() != null && !jiraConfig.getQtestToken().isEmpty());
            config.put("authMethod", (jiraConfig.getQtestToken() != null && !jiraConfig.getQtestToken().isEmpty()) ? "token" : "password");
            result.put("configuration", config);

            // Troubleshooting guidance
            List<String> issues = new ArrayList<>();
            List<String> recommendations = new ArrayList<>();

            if (jiraConfig.getQtestUrl() == null || jiraConfig.getQtestUrl().isEmpty()) {
                issues.add("QTest URL not configured");
                recommendations.add("Set qtest.url in application.properties");
            }

            if (jiraConfig.getQtestUsername() == null || jiraConfig.getQtestUsername().isEmpty()) {
                issues.add("QTest username not configured");
                recommendations.add("Set qtest.username in application.properties");
            }

            if ((jiraConfig.getQtestPassword() == null || jiraConfig.getQtestPassword().isEmpty()) &&
                    (jiraConfig.getQtestToken() == null || jiraConfig.getQtestToken().isEmpty())) {
                issues.add("QTest authentication not configured");
                recommendations.add("Set either qtest.password OR qtest.token in application.properties");
                recommendations.add("Token authentication is recommended for better security");
            }

            if (jiraConfig.getQtestProjectId() == null || jiraConfig.getQtestProjectId().isEmpty()) {
                issues.add("QTest project ID not configured");
                recommendations.add("Set qtest.project.id in application.properties");
            }

            if (configured && !qTestService.isAuthenticated()) {
                issues.add("Authentication failed - check credentials");
                recommendations.add("Verify username and password are correct");
                recommendations.add("Check if account requires 2FA or is locked");
                recommendations.add("Verify QTest URL is accessible");
            }

            result.put("issues", issues);
            result.put("recommendations", recommendations);
            result.put("timestamp", new Date());

            return ResponseEntity.ok(result);
        }
        catch (Exception e) {
            logger.error("Error checking QTest status: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("error", "Status check failed: " + e.getMessage());
            result.put("timestamp", new Date());
            return ResponseEntity.ok(result);
        }
    }

    @PostMapping("/maintenance/fix-orphaned-test-cases")
    public ResponseEntity<Map<String, Object>> fixOrphanedTestCases() {
        try {
            logger.info("Triggering fix for orphaned test cases");
            manualPageService.fixOrphanedTestCases();

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Orphaned test cases fix completed");
            result.put("timestamp", new Date());

            return ResponseEntity.ok(result);
        }
        catch (Exception e) {
            logger.error("Error fixing orphaned test cases: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "Fix failed: " + e.getMessage());
            result.put("timestamp", new Date());
            return ResponseEntity.ok(result);
        }
    }
}