package com.example.abhishektiwari.smsapp.back_up;

import android.app.backup.BackupAgentHelper;
import android.app.backup.BackupManager;
import android.app.backup.FileBackupHelper;
import android.content.Context;
import android.net.Uri;
import android.provider.Telephony;

/**
 * Created by abhishektiwari on 12/08/16.
 */
public class TheBackUpAgent extends BackupAgentHelper {

    static final String FILE_NAME = Uri.parse("/data/data/com.android.providers/telephony/databases/mmssms.db").getPath();

    static final String FILES_BACKUP_KEY = "smsFiles";

    @Override
    public void onCreate() {
        super.onCreate();
        FileBackupHelper helper = new FileBackupHelper(this, FILE_NAME);
        addHelper(FILES_BACKUP_KEY, helper);
    }

    public static void createBackUp(Context context) {
        BackupManager bm = new BackupManager(context);
        bm.dataChanged();
    }
}
