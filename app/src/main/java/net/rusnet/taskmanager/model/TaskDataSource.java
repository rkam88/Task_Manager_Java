package net.rusnet.taskmanager.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public interface TaskDataSource {

    interface LoadTasksCallback {
        void onTasksLoaded(List<Task> tasks);
    }

    interface LoadTasksCountCallback {
        void onTasksCountLoaded(int tasksCount);
    }

    interface CreateNewTaskCallback {
        void onTaskCreated();
    }

    void loadTasks(
            @Nullable final TaskType taskType,
            final boolean isCompleted,
            @NonNull final LoadTasksCallback callback);

    void loadTasksCount(
            @Nullable final TaskType taskType,
            final boolean isCompleted,
            @NonNull final LoadTasksCountCallback callback);

    void createNewTask(
            @NonNull final Task task,
            @NonNull final CreateNewTaskCallback callback);

}
