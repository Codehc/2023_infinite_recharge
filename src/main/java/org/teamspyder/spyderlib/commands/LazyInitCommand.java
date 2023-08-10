package org.teamspyder.spyderlib.commands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;

import java.util.function.Supplier;

public class LazyInitCommand extends CommandBase {
    private final Supplier<Command> commandSupplier;
    private Command command;

    public LazyInitCommand(Supplier<Command> commandSupplier) {
        this.commandSupplier = commandSupplier;
    }

    @Override
    public void initialize() {
        command = commandSupplier.get();
        m_requirements.addAll(command.getRequirements());
        command.initialize();
    }

    @Override
    public void execute() {
        command.execute();
    }

    @Override
    public void end(boolean interrupted) {
        command.end(interrupted);
        command = null;
        m_requirements.clear();
    }

    @Override
    public boolean isFinished() {
        return command.isFinished();
    }
}
