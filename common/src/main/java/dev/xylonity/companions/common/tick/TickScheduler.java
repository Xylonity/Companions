package dev.xylonity.companions.common.tick;

import net.minecraft.world.level.Level;

import java.util.*;

@Deprecated
public final class TickScheduler {
    private static final byte SERVER = 0;
    private static final byte CLIENT = 1;
    private static final byte BOTH = 2;

    private static final Map<Level, PriorityQueue<ScheduledTask>> SERVER_TASKS = new HashMap<>(4);
    private static final Map<Level, PriorityQueue<ScheduledTask>> CLIENT_TASKS = new HashMap<>(2);
    private static final Map<Level, PriorityQueue<ScheduledTask>> COMMON_TASKS = new HashMap<>(4);
    private static final Map<Level, Long> LEVEL_TICK_COUNTER = new HashMap<>();

    private static final List<Level> LEVELS_TO_CLEAN = new ArrayList<>();

    public static void schedule(Level level, Runnable r, int delay, byte type) {
        if (level == null || r == null || delay < 0) return;

        long cTick = getOrCreateTickCounter(level);
        long execAt = cTick + delay;

        ScheduledTask t = new ScheduledTask(execAt, r);

        switch (type) {
            case SERVER:
                if (!level.isClientSide())
                    getOrCreateQueue(SERVER_TASKS, level).add(t);

                break;
            case CLIENT:
                if (level.isClientSide())
                    getOrCreateQueue(CLIENT_TASKS, level).add(t);

                break;
            case BOTH:
                if (!level.isClientSide())
                    getOrCreateQueue(SERVER_TASKS, level).add(t);
                else
                    getOrCreateQueue(CLIENT_TASKS, level).add(t);

                getOrCreateQueue(COMMON_TASKS, level).add(t);
                break;
            default:
                break;
        }
    }

    public static void scheduleServer(Level level, Runnable r, int delay) {
        schedule(level, r, delay, SERVER);
    }

    public static void scheduleClient(Level level, Runnable r, int delay) {
        schedule(level, r, delay, CLIENT);
    }

    public static void scheduleBoth(Level level, Runnable r, int delay) {
        schedule(level, r, delay, BOTH);
    }

    public static void markLevelForCleanup(Level level) {
        if (level != null) {
            synchronized (LEVELS_TO_CLEAN) {
                LEVELS_TO_CLEAN.add(level);
            }
        }
    }

    public static void cleanMarkedLevels() {
        synchronized (LEVELS_TO_CLEAN) {
            if (!LEVELS_TO_CLEAN.isEmpty()) {
                for (Level level : LEVELS_TO_CLEAN) {
                    SERVER_TASKS.remove(level);
                    CLIENT_TASKS.remove(level);
                    COMMON_TASKS.remove(level);
                    LEVEL_TICK_COUNTER.remove(level);
                }
                LEVELS_TO_CLEAN.clear();
            }
        }
    }

    public static boolean hasTasks() {
        return !SERVER_TASKS.isEmpty() || !CLIENT_TASKS.isEmpty() || !COMMON_TASKS.isEmpty();
    }

    private static PriorityQueue<ScheduledTask> getOrCreateQueue(Map<Level, PriorityQueue<ScheduledTask>> map, Level level) {
        return map.computeIfAbsent(level, lvl -> new PriorityQueue<>());
    }

    private static long getOrCreateTickCounter(Level level) {
        return LEVEL_TICK_COUNTER.computeIfAbsent(level, lvl -> 0L);
    }

    public static void incrementTick(Level level) {
        LEVEL_TICK_COUNTER.put(level, getOrCreateTickCounter(level) + 1);
    }

    public static void processServerTasks(Level level) {
        PriorityQueue<ScheduledTask> queue = SERVER_TASKS.get(level);

        if (queue == null || queue.isEmpty()) return;

        long cTick = getOrCreateTickCounter(level);

        while (!queue.isEmpty()) {
            ScheduledTask top = queue.peek();

            if (top.executeAtTick > cTick) break;

            queue.poll();
            top.execute();
        }
    }

    public static void processClientTasks(Level level) {
        PriorityQueue<ScheduledTask> queue = CLIENT_TASKS.get(level);

        if (queue == null || queue.isEmpty()) return;

        long cTick = getOrCreateTickCounter(level);

        while (!queue.isEmpty()) {
            ScheduledTask top = queue.peek();

            if (top.executeAtTick > cTick) break;

            queue.poll();
            top.execute();
        }
    }

    public static void processCommonTasks(Level level) {
        PriorityQueue<ScheduledTask> queue = COMMON_TASKS.get(level);

        if (queue == null || queue.isEmpty()) return;

        long cTick = getOrCreateTickCounter(level);

        while (!queue.isEmpty()) {
            ScheduledTask top = queue.peek();

            if (top.executeAtTick > cTick) break;

            queue.poll();
            top.execute();
        }
    }

    public static final class ScheduledTask implements Comparable<ScheduledTask> {
        private final long executeAtTick;
        private final Runnable r;

        private ScheduledTask(long executeAtTick, Runnable r) {
            this.executeAtTick = executeAtTick;
            this.r = r;
        }

        @Override
        public int compareTo(ScheduledTask other) {
            return Long.compare(this.executeAtTick, other.executeAtTick);
        }

        public void execute() {
            r.run();
        }

    }

}