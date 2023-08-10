package org.teamspyder.spyderlib.wrappers;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Transform3d;
import org.photonvision.EstimatedRobotPose;
import org.photonvision.PhotonCamera;
import org.photonvision.PhotonPoseEstimator;
import org.photonvision.PhotonPoseEstimator.PoseStrategy;
import org.photonvision.targeting.PhotonPipelineResult;
import org.photonvision.targeting.PhotonTrackedTarget;

import java.util.Optional;

public class PhotonPoseCamera {
    private final PhotonCamera camera;
    private final Transform3d ROBOT_TO_CAMERA_POSE;
    private final PhotonPoseEstimator photonPoseEstimator;

    public PhotonPoseCamera(String cameraName, int pipeline, Transform3d robotToCameraPose, AprilTagFieldLayout layout, PoseStrategy mainStrategy, PoseStrategy fallbackStrategy) {
        this.camera = new PhotonCamera(cameraName);
        this.camera.setPipelineIndex(pipeline);

        this.ROBOT_TO_CAMERA_POSE = robotToCameraPose;

        this.photonPoseEstimator = new PhotonPoseEstimator(layout, mainStrategy, this.camera, this.ROBOT_TO_CAMERA_POSE);
        this.photonPoseEstimator.setMultiTagFallbackStrategy(fallbackStrategy);
    }

    public PhotonPoseCamera(String cameraName, Transform3d robotToCameraPose, AprilTagFieldLayout layout) {
        this(cameraName, 0, robotToCameraPose, layout, PoseStrategy.LOWEST_AMBIGUITY, PoseStrategy.LOWEST_AMBIGUITY);
    }

    public Optional<VisionResult> getRobotLocation() {
        PhotonPipelineResult result = camera.getLatestResult();
        Optional<EstimatedRobotPose> estimatedRobotPose = photonPoseEstimator.update(result);

        if (estimatedRobotPose.isPresent()) {
            EstimatedRobotPose pose = estimatedRobotPose.get();
            VisionResult visionResult = new VisionResult(pose);

            return Optional.of(visionResult);
        } else {
            return Optional.empty();
        }
    }

    public Optional<VisionResult> getRobotLocation(Pose2d reference) {
        photonPoseEstimator.setReferencePose(reference);
        return getRobotLocation();
    }

    public static class VisionResult {
        public EstimatedRobotPose pose;
        public double avgDistanceToTags;
        public int numTags;

        public VisionResult(EstimatedRobotPose pose) {
            this.pose = pose;
            this.numTags = pose.targetsUsed.size();

            for (PhotonTrackedTarget target : pose.targetsUsed) {
                this.avgDistanceToTags += target.getBestCameraToTarget().getTranslation().getNorm();
            }

            this.avgDistanceToTags /= this.numTags;
        }
    }
}
