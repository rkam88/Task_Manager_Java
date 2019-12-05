package net.rusnet.taskmanager.model;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Arrays;
import java.util.List;

public class TasksRepository implements TaskDataSource {

    private static TasksRepository INSTANCE = null;

    private TasksDao mTasksDao;

    private TasksRepository(Application application) {
        TasksDatabase db = TasksDatabase.getDatabase(application);
        mTasksDao = db.taskDao();
    }

    public static TasksRepository getRepository(Application application) {
        if (INSTANCE == null) {
            INSTANCE = new TasksRepository(application);
        }
        return INSTANCE;
    }

    @Override
    public void loadTasks(@Nullable TaskType taskType, boolean isCompleted, @NonNull LoadTasksCallback callback) {
        new loadTasksAsyncTask(mTasksDao, taskType, isCompleted, callback).execute();
    }

    @Override
    public void loadTasksCount(@Nullable TaskType taskType, boolean isCompleted, @NonNull LoadTasksCountCallback callback) {
        new loadTasksCount(mTasksDao, taskType, isCompleted, callback).execute();
    }

    @Override
    public void createNewTask(@NonNull Task task, @NonNull CreateNewTaskCallback callback) {
        new createNewTaskAsyncTask(mTasksDao, callback).execute(task);
    }

    private static class loadTasksAsyncTask extends AsyncTask<Void, Void, List<Task>> {
        private TasksDao mTasksDao;
        private TaskType mTaskType;
        private boolean mIsCompleted;
        private LoadTasksCallback mCallback;

        loadTasksAsyncTask(TasksDao tasksDao, TaskType taskType, boolean isCompleted, LoadTasksCallback callback) {
            mTasksDao = tasksDao;
            mTaskType = taskType;
            mIsCompleted = isCompleted;
            mCallback = callback;
        }

        @Override
        protected List<Task> doInBackground(Void... voids) {
            return Arrays.asList(mTasksDao.getTasks(mTaskType.getType(), mIsCompleted));
        }

        @Override
        protected void onPostExecute(List<Task> tasks) {
            super.onPostExecute(tasks);
            mCallback.onTasksLoaded(tasks);
        }
    }

    private static class loadTasksCount extends AsyncTask<Void, Void, Integer> {
        private TasksDao mTaskDao;
        private TaskType mTaskType;
        private boolean mIsCompleted;
        private LoadTasksCountCallback mCallback;

        loadTasksCount(TasksDao taskDao, TaskType taskType, boolean isCompleted, LoadTasksCountCallback callback) {
            mTaskDao = taskDao;
            mTaskType = taskType;
            mIsCompleted = isCompleted;
            mCallback = callback;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            return mTaskDao.getTasksCount(
                    mTaskType.getType(),
                    mIsCompleted
            );
        }

        @Override
        protected void onPostExecute(Integer tasksCount) {
            mCallback.onTasksCountLoaded(tasksCount);
        }
    }

    private static class createNewTaskAsyncTask extends AsyncTask<Task, Void, Void> {
        private TasksDao mTaskDao;
        private CreateNewTaskCallback mCallback;

        createNewTaskAsyncTask(TasksDao taskDao, CreateNewTaskCallback callback) {
            mTaskDao = taskDao;
            mCallback = callback;
        }

        @Override
        protected Void doInBackground(Task... tasks) {
            mTaskDao.insertTask(tasks[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mCallback.onTaskCreated();
        }
    }

}
