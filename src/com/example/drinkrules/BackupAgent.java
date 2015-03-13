package com.example.drinkrules;

import android.app.backup.BackupAgentHelper;
import android.app.backup.FileBackupHelper;

/*
    Standard backup of internal storage files. For more info go to
    http://developer.android.com/training/cloudsync/backupapi.html
 */
public class BackupAgent extends BackupAgentHelper {
    static final String FAVORITES = "favorites";
    static final String FAVES_LIST = "faves list";

    // A key to uniquely identify the set of backup data
    static final String FILES_BACKUP_KEY = "myfiles";

    @Override
    public void onCreate() {
        FileBackupHelper helper = new FileBackupHelper(this, FAVORITES, FAVES_LIST);
        addHelper(FILES_BACKUP_KEY, helper);
    }//onCreate
}//BackupAgent
