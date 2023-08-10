package org.teamspyder.frc2023;

import com.pathplanner.lib.PathConstraints;
import com.pathplanner.lib.PathPlanner;
import com.pathplanner.lib.PathPlannerTrajectory;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import org.teamspyder.spyderlib.auto.SpyderSwerveAutoBuilder;

import java.util.HashMap;
import java.util.List;

public class Autonomous {
    // Chooser to be sent to Shuffleboard so we can choose the auto on the fly
    SendableChooser<AutoPath> pathChooser;

    /**
     * Initialize the Autonomous class
     */
    public Autonomous(SendableChooser<AutoPath> pathChooser) {
        this.pathChooser = pathChooser;

        // Add paths to chooser
        AutoPath[] autoPaths = {
                AutoPath.Test
        };
        boolean first = true;
        for (AutoPath autoPath : autoPaths) {
            if (first) {
                pathChooser.setDefaultOption(autoPath.toString(), autoPath);
            } else {
                pathChooser.addOption(autoPath.toString(), autoPath);
            }
            first = false;
        }
    }

    /**
     * Get the auto command to run
     * @return The auto command to run. This will be a sequential command group that will run all the commands in the auto path chosen by the sendable chooser
     */
    public Command getAutoCommand(AutoPath path, HashMap<String, Command> eventMap) {
        // Load the path *group* from the file
        List<PathPlannerTrajectory> pathGroup = path.getTrajectory();
        return Commands.none();
    }

    /**
     * The different auto paths we can run
     */
    public enum AutoPath {
        Test("Test");

        private final String fileName;
        private final PathConstraints constraints;

        AutoPath(String fileName, PathConstraints constraints) {
            this.fileName = fileName;
            this.constraints = constraints;
        }

        AutoPath(String fileName) {
            this(fileName, new PathConstraints(0, 0));
        }

        public String getFileName() {
            return fileName;
        }

        public PathConstraints getFirstConstraint() {
            return constraints;
        }

        public List<PathPlannerTrajectory> getTrajectory() {
            return PathPlanner.loadPathGroup(getFileName(), getFirstConstraint());
        }

        /**
         * Regex to add spaces before capital letters
         * @return The name of the auto path with spaces before capital letters
         */
        @Override
        public String toString() {
            return getFileName();
        }
    }
}
