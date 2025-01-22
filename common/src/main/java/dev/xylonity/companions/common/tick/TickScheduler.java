package dev.xylonity.companions.common.tick;

import net.minecraft.server.level.ServerLevel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Deprecated
public class TickScheduler {

    public static final Map<ServerLevel, List<ScheduledTask>> TASKS = new HashMap<>();

    public static void schedule(ServerLevel level, Runnable runnable, int delayTicks) {
        long runAt = level.getGameTime() + delayTicks;
        TASKS.computeIfAbsent(level, k -> new ArrayList<>())
                .add(new ScheduledTask(runAt, runnable));
    }

    public static class ScheduledTask {
        public long runAt;
        public Runnable runnable;

        private ScheduledTask(long runAt, Runnable runnable) {
            this.runAt = runAt;
            this.runnable = runnable;
        }
    }

}
