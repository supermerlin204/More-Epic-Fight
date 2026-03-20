package org.merlin204.mef.api.animation;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.animation.Animator;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.Objects;

public class MEFAttackAnalyzer {

    public enum AttackDirection {
        LEFT_ATTACK("left_attack"),
        RIGHT_ATTACK("right_attack"),
        LEFT_SLIGHT_ATTACK("left_slight_attack"),
        RIGHT_SLIGHT_ATTACK("right_slight_attack"),
        FRONT_ATTACK("front_attack"),
        LEFT_SIDE("left_side"),
        RIGHT_SIDE("right_side"),
        FRONT_SIDE("front_side");

        private final String displayName;
        AttackDirection(String displayName) { this.displayName = displayName; }
        public String getDisplayName() { return displayName; }
    }

    public enum AttackType {
        THRUST_FRONT("thrust_front"), THRUST_BACK("thrust_back"),
        VERTICAL_SLAM("vertical_slam"), NONE("none");

        private final String displayName;
        AttackType(String displayName) { this.displayName = displayName; }
        public String getDisplayName() { return displayName; }
    }

    public static AttackDirection analyzeAttackDirection(LivingEntityPatch<?> defenderPatch, LivingEntity targetEntity) {
        try {
            var targetPatch = targetEntity.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY).resolve().orElse(null);
            if (targetPatch instanceof LivingEntityPatch<?> livingTargetPatch) {
                var animPlayer = livingTargetPatch.getAnimator().getPlayerFor(null);
                if (animPlayer != null) {
                    var currentAnimation = animPlayer.getAnimation().get();
                    if (currentAnimation instanceof AttackAnimation attackAnimation) {
                        float elapsedTime = animPlayer.getElapsedTime();
                        var currentPhase = attackAnimation.getPhaseByTime(elapsedTime);
                        var colliderPairs = currentPhase.getColliders();
                        for (var pair : colliderPairs) {
                            var colliderJoint = pair.getFirst();
                            return determineJointAttackSide(defenderPatch, livingTargetPatch, attackAnimation, colliderJoint, elapsedTime, targetEntity);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return AttackDirection.FRONT_ATTACK;
    }

    private static AttackDirection determineJointAttackSide(LivingEntityPatch<?> defenderPatch, LivingEntityPatch<?> targetPatch,
                                                            AttackAnimation attackAnimation, Joint colliderJoint, float elapsedTime, LivingEntity targetEntity) {
        Vec3 position = defenderPatch.getOriginal().position();
        Vec3 viewVector = defenderPatch.getOriginal().getViewVector(1.0F);
        float totalTime = attackAnimation.getTotalTime();
        float startTime = Math.max(elapsedTime - 0.3F, 0F);
        float endTime = Math.min(elapsedTime + 0.2F, totalTime);
        float[] sampleTimes = {
                startTime, Math.max(elapsedTime - 0.2F, 0F), Math.max(elapsedTime - 0.1F, 0F),
                elapsedTime - 0.05F, elapsedTime, elapsedTime + 0.05F, elapsedTime + 0.1F, endTime
        };
        Vec3[] samplePositions = new Vec3[sampleTimes.length];
        for (int i = 0; i < sampleTimes.length; i++) {
            samplePositions[i] = getJointWorldRawPos(targetPatch, colliderJoint, sampleTimes[i]);
        }
        Vec3 overallMovement = samplePositions[samplePositions.length - 1].subtract(samplePositions[0]);

        if (overallMovement.lengthSqr() < 0.0001) {
            Vec3 currentPos = samplePositions[4];
            Vec3 toJoint = currentPos.subtract(position);
            return determineStaticPositionSide(viewVector, toJoint);
        }

        AttackType thrustResult = determineIfThrustAttackExtended(overallMovement, viewVector, samplePositions, targetEntity);
        if (thrustResult != null) {
            return AttackDirection.FRONT_ATTACK;
        }
        return determineSideByCrossProduct(viewVector, samplePositions[0], samplePositions[samplePositions.length - 1]);
    }

    private static AttackType determineIfThrustAttackExtended(Vec3 movement, Vec3 lookVec, Vec3[] samplePositions, LivingEntity targetEntity) {
        Vec3 movementDirection = movement.normalize();
        Vec3 lookHorizontal = new Vec3(lookVec.x, 0, lookVec.z).normalize();
        Vec3 movementHorizontal = new Vec3(movementDirection.x, 0, movementDirection.z).normalize();
        double dotProduct = movementHorizontal.dot(lookHorizontal);

        double movementLengthSqr = movement.lengthSqr();
        double stability = calculateMovementStability(samplePositions, lookHorizontal);
        double forwardDominance = calculateForwardDominance(samplePositions, lookHorizontal);
        double verticalRatio = calculateVerticalRatio(samplePositions);
        boolean isTargetAirborne = isTargetAirborne(targetEntity, samplePositions);

        if (verticalRatio > 0.9) return AttackType.VERTICAL_SLAM;

        boolean isThrust = false;
        double verticalThreshold = isTargetAirborne ? 0.4 : 0.3;
        if (verticalRatio > verticalThreshold) return null;

        boolean hasGoodDirection = Math.abs(dotProduct) > 0.75;
        boolean hasGoodStability = stability > 0.8;
        boolean hasGoodForwardDominance = forwardDominance > 0.7;
        boolean hasReasonableDistance = movementLengthSqr > 0.64;

        if (isTargetAirborne) {
            hasGoodForwardDominance = forwardDominance > 0.6;
            hasGoodStability = stability > 0.7;
        }

        if (hasGoodDirection && hasGoodStability && hasGoodForwardDominance && hasReasonableDistance) {
            double thrustScore = (Math.abs(dotProduct) * 0.25 + stability * 0.35 + forwardDominance * 0.3);
            double scoreThreshold = isTargetAirborne ? 0.75 : 0.78;
            isThrust = thrustScore > scoreThreshold;
            if (stability > 0.95 && forwardDominance > 0.8) isThrust = true;
            if (Math.abs(dotProduct) > 0.99 && stability > 0.8) isThrust = true;
        }

        if (isThrust) {
            return dotProduct > 0 ? AttackType.THRUST_FRONT : AttackType.THRUST_BACK;
        }
        return null;
    }

    private static boolean isTargetAirborne(LivingEntity targetEntity, Vec3[] samplePositions) {
        if (targetEntity == null) return false;
        if (!targetEntity.onGround()) return true;
        return analyzeAirborneFromTrajectory(samplePositions);
    }

    private static boolean analyzeAirborneFromTrajectory(Vec3[] samplePositions) {
        if (samplePositions.length < 3) return false;
        int peakIndex = findPeakIndex(samplePositions);
        if (peakIndex > 0 && peakIndex < samplePositions.length - 1) {
            double riseHeight = samplePositions[peakIndex].y - samplePositions[0].y;
            double fallHeight = samplePositions[peakIndex].y - samplePositions[samplePositions.length - 1].y;
            if (riseHeight > 0.3 && fallHeight > 0.2) return true;
        }
        double minY = samplePositions[0].y;
        double maxY = samplePositions[0].y;
        for (Vec3 pos : samplePositions) {
            minY = Math.min(minY, pos.y);
            maxY = Math.max(maxY, pos.y);
        }
        return (maxY - minY) > 1.0;
    }

    private static int findPeakIndex(Vec3[] positions) {
        int peakIndex = 0;
        double maxHeight = positions[0].y;
        for (int i = 1; i < positions.length; i++) {
            if (positions[i].y > maxHeight) {
                maxHeight = positions[i].y;
                peakIndex = i;
            }
        }
        return peakIndex;
    }

    private static double calculateVerticalRatio(Vec3[] samplePositions) {
        if (samplePositions.length < 2) return 0.0;
        double maxVerticalMovement = 0.0;
        double totalVerticalVariation = 0.0;
        for (int i = 1; i < samplePositions.length; i++) {
            double verticalChange = Math.abs(samplePositions[i].y - samplePositions[i - 1].y);
            maxVerticalMovement = Math.max(maxVerticalMovement, verticalChange);
            totalVerticalVariation += verticalChange;
        }
        double totalHeightChange = Math.abs(samplePositions[samplePositions.length - 1].y - samplePositions[0].y);
        double averageVerticalVariation = totalVerticalVariation / (samplePositions.length - 1);
        double verticalScore = (maxVerticalMovement * 0.4 + averageVerticalVariation * 0.3 + totalHeightChange * 0.3);
        return Math.min(verticalScore / 2.0, 1.0);
    }

    private static double calculateMovementStability(Vec3[] positions, Vec3 referenceDirection) {
        if (positions.length < 3) return 1.0;
        double totalStability = 0.0;
        int validSegments = 0;
        for (int i = 1; i < positions.length; i++) {
            Vec3 segmentMovement = positions[i].subtract(positions[i - 1]);
            if (segmentMovement.lengthSqr() > 0.0001) {
                Vec3 segmentDirection = segmentMovement.normalize();
                Vec3 segmentDirectionHorizontal = new Vec3(segmentDirection.x, 0, segmentDirection.z).normalize();
                double consistency = Math.abs(segmentDirectionHorizontal.dot(referenceDirection));
                totalStability += consistency;
                validSegments++;
            }
        }
        return validSegments > 0 ? totalStability / validSegments : 1.0;
    }

    private static double calculateForwardDominance(Vec3[] positions, Vec3 forwardDirection) {
        if (positions.length < 2) return 1.0;
        double totalForward = 0.0;
        double totalLateral = 0.0;
        for (int i = 1; i < positions.length; i++) {
            Vec3 segment = positions[i].subtract(positions[i - 1]);
            if (segment.lengthSqr() > 0.0001) {
                Vec3 segmentHorizontal = new Vec3(segment.x, 0, segment.z);
                double forwardComponent = segmentHorizontal.dot(forwardDirection);
                totalForward += Math.abs(forwardComponent);
                Vec3 lateralComponent = segmentHorizontal.subtract(forwardDirection.scale(forwardComponent));
                totalLateral += lateralComponent.length();
            }
        }
        double totalMovement = totalForward + totalLateral;
        return totalMovement > 0 ? totalForward / totalMovement : 1.0;
    }

    private static AttackDirection determineSideByCrossProduct(Vec3 lookVec, Vec3 startPos, Vec3 endPos) {
        Vec3 movement = endPos.subtract(startPos);
        Vec3 movementDirection = movement.normalize();
        Vec3 lookHorizontal = new Vec3(lookVec.x, 0, lookVec.z).normalize();
        Vec3 movementHorizontal = new Vec3(movementDirection.x, 0, movementDirection.z).normalize();
        double crossY = lookHorizontal.x * movementHorizontal.z - lookHorizontal.z * movementHorizontal.x;
        double dotProduct = lookHorizontal.dot(movementHorizontal);
        double adjustedThreshold = Math.abs(dotProduct) > 0.9 ? 0.1 : 0.15;

        if (crossY > adjustedThreshold) return AttackDirection.LEFT_ATTACK;
        else if (crossY < -adjustedThreshold) return AttackDirection.RIGHT_ATTACK;
        else if (crossY > adjustedThreshold * 0.5) return AttackDirection.LEFT_SLIGHT_ATTACK;
        else if (crossY < -adjustedThreshold * 0.5) return AttackDirection.RIGHT_SLIGHT_ATTACK;
        else return AttackDirection.FRONT_ATTACK;
    }

    private static AttackDirection determineStaticPositionSide(Vec3 lookVec, Vec3 toJoint) {
        Vec3 lookHorizontal = new Vec3(lookVec.x, 0, lookVec.z).normalize();
        Vec3 toJointHorizontal = new Vec3(toJoint.x, 0, toJoint.z).normalize();
        double crossY = lookHorizontal.x * toJointHorizontal.z - lookHorizontal.z * toJointHorizontal.x;

        if (crossY > 0.05) return AttackDirection.LEFT_SIDE;
        else if (crossY < -0.05) return AttackDirection.RIGHT_SIDE;
        else return AttackDirection.FRONT_SIDE;
    }

    public static Vec3 getJointWorldRawPos(LivingEntityPatch<?> entityPatch, Joint joint, float time) {
        return getJointWorldRawPos(entityPatch, joint, time, Vec3f.ZERO);
    }

    public static Vec3 getJointWorldRawPos(LivingEntityPatch<?> entityPatch, Joint joint, float time, Vec3f offset) {
        Animator animator = entityPatch.getAnimator();
        LivingEntity entity = entityPatch.getOriginal();
        Pose pose = Objects.requireNonNull(animator.getPlayerFor(null)).getAnimation().get().getRawPose(time);
        OpenMatrix4f transformMatrix = entityPatch.getArmature().getBoundTransformFor(pose, joint);
        transformMatrix.translate(offset);
        OpenMatrix4f rotation = (new OpenMatrix4f()).rotate(-((float)Math.toRadians(entityPatch.getYRot() + 180.0F)), new Vec3f(0.0F, 1.0F, 0.0F));
        OpenMatrix4f.mul(rotation, transformMatrix, transformMatrix);
        return new Vec3(transformMatrix.m30 + (float)entity.getX(), transformMatrix.m31 + (float)entity.getY(), transformMatrix.m32 + (float)entity.getZ());
    }
}