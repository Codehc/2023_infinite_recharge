package org.teamspyder.frc2023;

import com.pathplanner.lib.PathPlannerTrajectory.PathPlannerState;
import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.DriverStation.Alliance;

import java.io.IOException;
import java.util.Optional;

public class FieldPositions {
    private static FieldPositions instance;

    public static FieldPositions getInstance() {
        if (instance == null) {
            instance = new FieldPositions();
        }
        return instance;
    }

    public final AprilTagFieldLayout fieldLayout;

    private static double FIELD_LENGTH = 16.54175;
    private static double FIELD_WIDTH = 8.0137;

    private FieldPositions() {
        fieldLayout = null;
        /*try {
            fieldLayout = AprilTagFields.k2023ChargedUp.loadAprilTagLayoutField();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/
    }

    public Optional<Pose3d> getTagPosition(int ID) {
        return fieldLayout.getTagPose(ID);
    }

    public static PathPlannerState flipPathPlannerState(PathPlannerState state, Alliance alliance) {
        return alliance == Alliance.Blue ? state : flipPathPlannerState(state);
    }

    public static PathPlannerState flipPathPlannerState(PathPlannerState state) {
        PathPlannerState flippedState = new PathPlannerState();

        // Fields like the velocity, time, and acceleration remain the same since they aren't flipped at all
        flippedState.holonomicAngularVelocityRadPerSec = -state.holonomicAngularVelocityRadPerSec;
        flippedState.curvatureRadPerMeter = -state.curvatureRadPerMeter;
        flippedState.holonomicRotation = Rotation2d.fromDegrees(180 - state.holonomicRotation.getDegrees());
        flippedState.poseMeters = flipPose(state.poseMeters);
        flippedState.angularVelocityRadPerSec = -state.angularVelocityRadPerSec;

        return flippedState;
    }

    public static Pose2d flipPose(Pose2d pose, Alliance alliance) {
        return alliance == Alliance.Blue ? pose : flipPose(pose);
    }

    public static Pose2d flipPose(Pose2d pose) {
        return new Pose2d(FIELD_LENGTH - pose.getX(), pose.getY(), Rotation2d.fromDegrees(180 - pose.getRotation().getDegrees()));
    }

    // Columns from bottom up
    public enum SnapPosition {
        ORIGIN(new Pose2d(0, 0, Rotation2d.fromDegrees(0)));

        private Pose2d pose;

        SnapPosition(Pose2d pose) { this.pose = pose; }

        public static void updatePoses(Alliance alliance) {
            for (SnapPosition position : values()) {
                position.pose = flipPose(position.pose, alliance);
            }
        }

        public Pose2d getPose() {
            return pose;
        }

        public static SnapPosition getNearestColumn(Pose2d currentPose) {
            SnapPosition nearestColumn = null;
            double nearestDd = 0;

            for (SnapPosition column : SnapPosition.values()) {
                Pose2d pose = column.getPose();
                double dx = pose.getX() - currentPose.getX();
                double dy = pose.getY() - currentPose.getY();

                double dd = Math.sqrt(dx * dx + dy * dy);

                if (nearestColumn == null || dd <= nearestDd) {
                    nearestColumn = column;
                    nearestDd = dd;
                }
            }

            return nearestColumn;
        }
    }
}
