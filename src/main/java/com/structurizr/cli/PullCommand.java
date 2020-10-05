package com.structurizr.cli;

import com.structurizr.Workspace;
import com.structurizr.api.StructurizrClient;
import com.structurizr.documentation.AdrToolsImporter;
import com.structurizr.documentation.AutomaticDocumentationTemplate;
import com.structurizr.dsl.StructurizrDslParser;
import com.structurizr.encryption.AesEncryptionStrategy;
import com.structurizr.model.Container;
import com.structurizr.model.SoftwareSystem;
import com.structurizr.util.StringUtils;
import com.structurizr.util.WorkspaceUtils;
import com.structurizr.view.*;
import org.apache.commons.cli.*;

import java.io.File;

class PullCommand extends AbstractCommand {

    private static final String JSON_FORMAT = "json";
    private static final String DSL_FORMAT = "dsl";
    private static final String PLANTUML_FORMAT = "plantuml";
    private static final String WEBSEQUENCEDIAGRAMS_FORMAT = "websequencediagrams";
    private static final String MERMAID_FORMAT = "mermaid";
    private static final String ILOGRAPH_FORMAT = "ilograph";

    PullCommand(String version) {
        super(version);
    }

    void run(String... args) throws Exception {
        Options options = new Options();

        Option option = new Option("url", "structurizrApiUrl", true, "Structurizr API URL (default: https://api.structurizr.com");
        option.setRequired(false);
        options.addOption(option);

        option = new Option("id", "workspaceId", true, "Workspace ID");
        option.setRequired(true);
        options.addOption(option);

        option = new Option("key", "apiKey", true, "Workspace API key");
        option.setRequired(true);
        options.addOption(option);

        option = new Option("secret", "apiSecret", true, "Workspace API secret");
        option.setRequired(true);
        options.addOption(option);

        option = new Option("f", "format", true, String.format("Export format: %s", String.join(Arrays.asList(JSON_FORMAT, DSL_FORMAT), "|"));
        option.setRequired(false);
        options.addOption(option);

        CommandLineParser commandLineParser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        String apiUrl = "";
        long workspaceId = 1;
        String apiKey = "";
        String apiSecret = "";
        String exportFormat = "";

        try {
            CommandLine cmd = commandLineParser.parse(options, args);

            apiUrl = cmd.getOptionValue("structurizrApiUrl", "https://api.structurizr.com");
            workspaceId = Long.parseLong(cmd.getOptionValue("workspaceId"));
            apiKey = cmd.getOptionValue("apiKey");
            apiSecret = cmd.getOptionValue("apiSecret");
            exportFormat = cmd.getOptionValue("format");
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("pull", options);

            System.exit(1);
        }

        System.out.println("Pulling workspace " + workspaceId + " from " + apiUrl);
        StructurizrClient structurizrClient = new StructurizrClient(apiUrl, apiKey, apiSecret);
        structurizrClient.setAgent(getAgent());
        Workspace workspace = structurizrClient.getWorkspace(workspaceId);

        if (exportFormat == DSL_FORMAT) {
            File file = new File("structurizr-" + workspaceId + "-workspace.dsl");
            WorkspaceUtils.saveWorkspaceToJson(workspace, file);
            System.out.println(" - workspace saved as " + file.getCanonicalPath());
        } else {
            File file = new File("structurizr-" + workspaceId + "-workspace.json");
            WorkspaceUtils.saveWorkspaceToJson(workspace, file);
            System.out.println(" - workspace saved as " + file.getCanonicalPath());
        }

        System.out.println(" - finished");
    }
}