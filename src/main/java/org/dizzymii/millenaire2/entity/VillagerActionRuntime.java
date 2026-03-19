package org.dizzymii.millenaire2.entity;

import javax.annotation.Nullable;

public class VillagerActionRuntime {

    public enum ExecutorType {
        VILLAGER_NATIVE,
        PLAYER_PROXY,
        ADAPTER
    }

    public enum Status {
        IDLE,
        RUNNING,
        SUCCESS,
        FAILED
    }

    public interface Action {
        String key();
        ExecutorType executorType();
        default void start(MillVillager villager) {}
        default Result tick(MillVillager villager) { return Result.success(null); }
        default void stop(MillVillager villager) {}
    }

    public record Result(Status status, boolean retryable, @Nullable String detail) {
        public static Result idle() {
            return new Result(Status.IDLE, false, null);
        }

        public static Result running(@Nullable String detail) {
            return new Result(Status.RUNNING, false, detail);
        }

        public static Result success(@Nullable String detail) {
            return new Result(Status.SUCCESS, false, detail);
        }

        public static Result failure(@Nullable String detail, boolean retryable) {
            return new Result(Status.FAILED, retryable, detail);
        }
    }

    @Nullable private Action currentAction;
    @Nullable private String currentActionKey;
    @Nullable private String lastCompletedActionKey;
    private Result lastResult = Result.idle();

    public boolean hasAction() {
        return currentAction != null;
    }

    @Nullable
    public Action getCurrentAction() {
        return currentAction;
    }

    @Nullable
    public String getCurrentActionKey() {
        return currentActionKey;
    }

    @Nullable
    public String getLastCompletedActionKey() {
        return lastCompletedActionKey;
    }

    public Result getLastResult() {
        return lastResult;
    }

    public void start(String key, Action action, MillVillager villager) {
        clear(villager);
        currentActionKey = key;
        currentAction = action;
        currentAction.start(villager);
        lastCompletedActionKey = null;
        lastResult = Result.running(key);
    }

    public void tick(MillVillager villager) {
        if (currentAction == null) {
            return;
        }
        Result result = currentAction.tick(villager);
        lastResult = result;
        if (result.status() == Status.SUCCESS || (result.status() == Status.FAILED && !result.retryable())) {
            lastCompletedActionKey = currentActionKey;
            clear(villager);
            lastResult = result;
        }
    }

    public void clear(MillVillager villager) {
        if (currentAction != null) {
            currentAction.stop(villager);
        }
        currentAction = null;
        currentActionKey = null;
    }

    public void reset(MillVillager villager) {
        clear(villager);
        lastCompletedActionKey = null;
        lastResult = Result.idle();
    }
}
