package org.merlin204.mef.api.lockon;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.client.camera.EpicFightCameraAPI;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class CameraLockUtil {

    private enum OverrideState {
        NONE, FORCING_LOCK, FORCING_UNLOCK
    }

    private static OverrideState currentState = OverrideState.NONE;
    private static boolean preOverrideLockState = false;
    private static LivingEntity preOverrideTarget = null;

    private static boolean sequenceOriginalLockState = false;
    private static boolean isSequenceActive = false;
    private static LivingEntity sequenceOriginalTarget = null;

    public static void startLockOn(float maxAngle, double maxDistance) {
        if (prepareOverride()) return;
        LocalPlayer player = Minecraft.getInstance().player;
        EpicFightCameraAPI cameraAPI = EpicFightCameraAPI.getInstance();
        LivingEntity target = cameraAPI.getFocusingEntity();

        if (player != null && target != null && !preOverrideLockState) {
            double distanceSqr = player.distanceToSqr(target);
            if (distanceSqr <= maxDistance * maxDistance) {
                Vec3 lookVec = player.getViewVector(1.0F).normalize();
                Vec3 toTarget = target.getEyePosition().subtract(player.getEyePosition()).normalize();
                double dot = Mth.clamp(lookVec.dot(toTarget), -1.0, 1.0);
                double angle = Math.toDegrees(Math.acos(dot));

                if (angle <= maxAngle) {
                    cameraAPI.setLockOn(true);
                    currentState = OverrideState.FORCING_LOCK;
                    return;
                }
            }
        }
        cancelOverride();
    }

    public static void startLockOn(double maxDistance) {
        if (prepareOverride()) return;
        LocalPlayer player = Minecraft.getInstance().player;
        EpicFightCameraAPI cameraAPI = EpicFightCameraAPI.getInstance();
        LivingEntity target = cameraAPI.getFocusingEntity();

        if (player != null && target != null && !preOverrideLockState) {
            if (player.distanceToSqr(target) <= maxDistance * maxDistance) {
                cameraAPI.setLockOn(true);
                currentState = OverrideState.FORCING_LOCK;
                return;
            }
        }
        cancelOverride();
    }

    public static void startLockOn() {
        if (prepareOverride()) return;
        if (!preOverrideLockState) {
            EpicFightCameraAPI.getInstance().setLockOn(true);
            currentState = OverrideState.FORCING_LOCK;
        } else {
            cancelOverride();
        }
    }

    public static void startUnlock() {
        if (prepareOverride()) return;
        if (preOverrideLockState) {
            EpicFightCameraAPI.getInstance().setLockOn(false);
            currentState = OverrideState.FORCING_UNLOCK;
        } else {
            cancelOverride();
        }
    }

    public static void endLockOn() {
        restoreState();
    }

    public static void endUnlock() {
        restoreState();
    }

    public static void restoreState() {
        if (currentState == OverrideState.NONE) return;

        EpicFightCameraAPI cameraAPI = EpicFightCameraAPI.getInstance();
        LocalPlayer localPlayer = Minecraft.getInstance().player;

        if (preOverrideLockState) {
            applySmartTargeting(preOverrideTarget, localPlayer, cameraAPI);
        } else {
            cameraAPI.setLockOn(false);
            if (currentState == OverrideState.FORCING_LOCK) {
                syncPlayerRotationToCamera(localPlayer, cameraAPI);
            }
        }

        cancelOverride();
    }

    private static boolean prepareOverride() {
        if (currentState != OverrideState.NONE) {
            return true;
        }
        EpicFightCameraAPI cameraAPI = EpicFightCameraAPI.getInstance();
        preOverrideLockState = cameraAPI.isLockingOnTarget();

        if (preOverrideLockState) {
            preOverrideTarget = cameraAPI.getFocusingEntity();
        } else {
            preOverrideTarget = null;
        }
        return false;
    }

    private static void cancelOverride() {
        currentState = OverrideState.NONE;
        preOverrideLockState = false;
        preOverrideTarget = null;
    }

    public static void beginSequenceAndUnlock() {
        EpicFightCameraAPI cameraAPI = EpicFightCameraAPI.getInstance();
        sequenceOriginalLockState = cameraAPI.isLockingOnTarget();
        sequenceOriginalTarget = cameraAPI.getFocusingEntity();
        isSequenceActive = true;
        cameraAPI.setLockOn(false);
    }

    public static void beginSequenceAndUnlockIfLocked() {
        EpicFightCameraAPI cameraAPI = EpicFightCameraAPI.getInstance();

        if (!cameraAPI.isLockingOnTarget()) {
            isSequenceActive = false;
            return;
        }

        sequenceOriginalLockState = true;
        sequenceOriginalTarget = cameraAPI.getFocusingEntity();
        isSequenceActive = true;
        cameraAPI.setLockOn(false);
    }

    public static void stepSequenceToLock() {
        if (!isSequenceActive) return;

        EpicFightCameraAPI cameraAPI = EpicFightCameraAPI.getInstance();
        LocalPlayer localPlayer = Minecraft.getInstance().player;
        applySmartTargeting(sequenceOriginalTarget, localPlayer, cameraAPI);
    }

    private static void restoreSequenceState() {
        if (!isSequenceActive) return;

        EpicFightCameraAPI cameraAPI = EpicFightCameraAPI.getInstance();
        LocalPlayer localPlayer = Minecraft.getInstance().player;

        if (sequenceOriginalLockState) {
            applySmartTargeting(sequenceOriginalTarget, localPlayer, cameraAPI);
        } else {
            cameraAPI.setLockOn(false);
            syncPlayerRotationToCamera(localPlayer, cameraAPI);
        }

        sequenceOriginalTarget = null;
    }

    public static void clearSequence() {
        if (isSequenceActive) {
            restoreSequenceState();
            isSequenceActive = false;
        }
        sequenceOriginalTarget = null;
    }

    private static void applySmartTargeting(@Nullable LivingEntity originalTarget, @Nullable LocalPlayer localPlayer, EpicFightCameraAPI cameraAPI) {
        LivingEntity priorityTarget = null;

        if (originalTarget != null && originalTarget.isAlive()) {
            priorityTarget = originalTarget;
        } else if (localPlayer != null) {
            LocalPlayerPatch playerPatch = EpicFightCapabilities.getEntityPatch(localPlayer, LocalPlayerPatch.class);
            if (playerPatch != null) {
                List<Entity> triedEntities = playerPatch.getCurrentlyAttackTriedEntities();
                if (triedEntities != null && !triedEntities.isEmpty()) {
                    LivingEntity closestEntity = null;
                    double minDistanceSqr = Double.MAX_VALUE;

                    for (Entity entity : triedEntities) {
                        if (entity instanceof LivingEntity livingEntity && livingEntity.isAlive()) {
                            double distSqr = localPlayer.distanceToSqr(livingEntity);
                            if (distSqr < minDistanceSqr) {
                                minDistanceSqr = distSqr;
                                closestEntity = livingEntity;
                            }
                        }
                    }

                    if (closestEntity != null) {
                        priorityTarget = closestEntity;
                    }
                }
            }

            if (priorityTarget == null && localPlayer.getLastHurtMob() != null && localPlayer.getLastHurtMob().isAlive()) {
                priorityTarget = localPlayer.getLastHurtMob();
            }
        }

        if (priorityTarget != null) {
            cameraAPI.setLockOn(true);
            ((IEpicFightCameraAPI) (Object) cameraAPI).mef$forceSetFocusingEntity(priorityTarget);
        } else {
            cameraAPI.setLockOn(true);
        }
    }

    private static void syncPlayerRotationToCamera(LocalPlayer player, EpicFightCameraAPI cameraAPI) {
        if (player != null && cameraAPI != null) {
            float yRot = cameraAPI.getCameraYRot();
            player.setYBodyRot(yRot);
            player.setYHeadRot(yRot);
            player.setYRot(yRot);
        }
    }
}