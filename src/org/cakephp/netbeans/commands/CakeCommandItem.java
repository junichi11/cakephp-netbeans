package org.cakephp.netbeans.commands;

import java.util.ArrayList;
import java.util.List;

/**
 * @author junichi11
 */
public class CakeCommandItem {

    private String command;
    private String description;
    private String displayName;
    private List<CakeCommandItem> subcommands;

    public CakeCommandItem(String command, String description, String displayName) {
        this.command = command;
        this.description = description;
        this.displayName = displayName;
    }

    public String getCommand() {
        return command;
    }

    public String getDescription() {
        return description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<CakeCommandItem> getSubcommands() {
        return subcommands;
    }

    public void addSubcommand(CakeCommandItem subcommand) {
        if (this.subcommands == null) {
            this.subcommands = new ArrayList<CakeCommandItem>();
        }
        this.subcommands.add(subcommand);
    }
}
