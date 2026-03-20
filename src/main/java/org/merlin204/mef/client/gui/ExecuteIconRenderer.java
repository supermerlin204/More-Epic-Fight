package org.merlin204.mef.client.gui;

import java.util.ArrayList;
import java.util.List;

public class ExecuteIconRenderer {
    private static final List<ExecuteIconRenderCommand> COMMANDS = new ArrayList<>();

    public static void addCommand(ExecuteIconRenderCommand cmd) {
        COMMANDS.add(cmd);
    }

    public static List<ExecuteIconRenderCommand> getCommands() {
        return COMMANDS;
    }

    public static void clear() {
        COMMANDS.clear();
    }
}