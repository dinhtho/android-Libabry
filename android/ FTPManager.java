package com.example.dinhtho.demopermission;

import android.content.Context;
import android.util.Log;

import com.example.dinhtho.demopermission.ftpclient.FTPClient;
import com.example.dinhtho.demopermission.ftpclient.FTPDataTransferListener;
import com.example.dinhtho.demopermission.ftpclient.FTPException;
import com.example.dinhtho.demopermission.ftpclient.FTPIllegalReplyException;
import com.example.dinhtho.demopermission.utils.FileUtils;

import java.io.File;
import java.io.IOException;


/**
 * Created by caoxuanphong on    10/3/16.
 */

public class FTPManager {
    private static final String TAG = "FTPManager";
    private static FTPManager instance;
    private static Context context;
    private FTPManagerListener listener;

    public interface FTPManagerListener {
        void completed();

        void aborted();

        void failed();
    }

    public static synchronized FTPManager shareInstance(Context context) {
        if (instance == null) {
            instance = new FTPManager();
            instance.context = context;
        }

        return instance;
    }

    public void download(final String ip,
                         final String username,
                         final String password,
                         final String downloadPath,
                         final FTPManagerListener listener) {
        instance.listener = listener;
        new Thread(new Runnable() {

            @Override
            public void run() {
                final FTPClient client = new FTPClient();
                try {
                    client.connect(ip);
                    client.login(username, password);
                    String appFolder = FileUtils.createAppFolder(context);
                    String fileName = downloadPath.substring(downloadPath.lastIndexOf('/') + 1);
                    client.download(downloadPath, new File(appFolder + "/" + fileName), new FTPDataTransferListener() {
                        @Override
                        public void started() {
                            Log.i(TAG, "started: ");
                        }

                        @Override
                        public void transferred(int length) {
                            Log.i(TAG, "transferred: ");
                        }

                        @Override
                        public void completed() {
                            Log.i(TAG, "completed: ");
                            close(client);
                            instance.listener.completed();
                        }

                        @Override
                        public void aborted() {
                            Log.i(TAG, "aborted: ");
                            close(client);
                            instance.listener.aborted();
                        }

                        @Override
                        public void failed() {
                            Log.i(TAG, "failed: ");
                            close(client);
                            instance.listener.failed();
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    if (listener != null) {
                        listener.failed();
                    }
                }
            }
        }).start();

    }

    public void append(final String ip, final String username, final String password, final File file,
                       final String remotePath, final FTPManagerListener listener) {
        Log.i(TAG, "append");
        instance.listener = listener;
        new Thread(new Runnable() {

            @Override
            public void run() {
                final FTPClient client = new FTPClient();
                try {
                    client.connect(ip);
                    client.login(username, password);
                    client.setType(FTPClient.TYPE_BINARY);
                    Log.i(TAG, "run: " + remotePath);
                    client.changeDirectory(remotePath);
                    client.append(file, new FTPDataTransferListener() {
                        @Override
                        public void started() {

                        }

                        @Override
                        public void transferred(int length) {

                        }

                        @Override
                        public void completed() {
                            Log.i(TAG, "completed: ");
                            if (instance.listener != null) {
                                instance.listener.completed();
                            }
                            close(client);
                        }

                        @Override
                        public void aborted() {
                            if (instance.listener != null) {
                                instance.listener.aborted();
                            }
                            Log.i(TAG, "aborted: ");
                            close(client);
                        }

                        @Override
                        public void failed() {
                            if (instance.listener != null) {
                                instance.listener.failed();
                            }
                            Log.i(TAG, "failed: ");
                            close(client);
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    if (listener != null) {
                        listener.failed();
                    }
                }
            }
        }).start();
    }

    public void upload(final String ip, final String username, final String password, final File file,
                       final String uploadPath, final FTPManagerListener listener) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                final FTPClient client = new FTPClient();
                try {
                    client.connect(ip);
                    client.login(username, password);
                    client.changeDirectory(uploadPath);
                    client.upload(file, new FTPDataTransferListener() {
                        @Override
                        public void started() {
                            Log.i(TAG, "started: ");
                        }

                        @Override
                        public void transferred(int length) {

                        }

                        @Override
                        public void completed() {
                            Log.i(TAG, "completed: ");
                            close(client);
                        }

                        @Override
                        public void aborted() {
                            Log.i(TAG, "aborted: ");
                            close(client);
                        }

                        @Override
                        public void failed() {
                            Log.i(TAG, "failed: ");
                            close(client);
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();

                    if (listener != null) {
                        listener.failed();
                    }
                }
            }
        }).start();

    }

    private void close(FTPClient client) {
        try {
            client.disconnect(false);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FTPIllegalReplyException e) {
            e.printStackTrace();
        } catch (FTPException e) {
            e.printStackTrace();
        }
    }

}

