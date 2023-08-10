package org.teamspyder.spyderlib.commands;

import edu.wpi.first.wpilibj2.command.Command;

import java.util.function.Supplier;

public class SpyderCommands {
    public static LazyInitCommand lazyInitCommand(Supplier<Command> commandSupplier) {
        return new LazyInitCommand(commandSupplier);
    }
}
