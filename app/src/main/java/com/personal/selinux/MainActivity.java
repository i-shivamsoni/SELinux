package com.personal.selinux;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {
    static {
        System.loadLibrary("selinux");
    }

    StringBuffer output;

    public static String getSelinuxFlag() {
        String selinux = null;
        String getselinux = null;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            selinux = (String) get.invoke(c, "ro.build.selinux");
            getselinux = (String) get.invoke(c, "ro.boot.selinux");

        } catch (Exception ignored) {
        }

        return selinux + "  " + getselinux;
    }

    private static String getSElinuxbySystem() {
        String value = System.getProperty("ro.build.selinux");
        String valueBuild = System.getProperty("ro.boot.selinux");
        if (value != "") {
            return value;
        } else return valueBuild;
    }

    // Native method declaration
    public native int selinuxStatusChecker();

    private native int checkProperty(String method_name, String mode);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

   /*
        this are the method where i attempted fetch it natively.
        int robuild = checkProperty("ro.build.selinux", "0");
        Log.d("robuild", "onCreate: " + robuild);
        doesn't work as method is not present anymore as this command also don't give any output
        int roboot = checkProperty("ro.boot.selinux", "permissive");
        Log.d("robuild", "onCreate: " + roboot);
        doesn't work as method is not present anymore as this adb shell getprop boot.selinux
        command also don't give any output

        int selinuxStatus = selinuxStatusChecker();
        Log.d("JNI simple app", "SELinux Status: " + selinuxStatus);

 */


        SelinuxfetchbyCat();


    }

    private String SelinuxfetchbyCat() {
        String status = null;
        status = Selinux("/system/bin/cat /sys/fs/selinux/enforce");


        Log.d("SELinux-result", "onCreate: out " + status);
        if (status.contains("0")) {
            Log.d("SELinux-result", " then SELinux is disabled / Permissive");
            status = " then SELinux is disabled / Permissive";
        } else if (status.contains("")) {
            status = " then SELinux is enabled / Enforcing";
            Log.d("SELinux-result", " then SELinux is enabled / Enforcing");
        }
        return status;
    }

    private String SELinuxfecth() {
        String result = null;

        String output = Selinux("getenforce");
        // String output = executeCommandAndGetOutput("/system/bin/cat /sys/fs/selinux/enforce");
        if (output.contains("Permissive")) {
            result = "Permissive";
            Log.d("output", "onCreate: " + "Permissive");
        } else {
            result = "Enforcing";
            Log.d("output", "onCreate: " + "Enforcing");
        }

        return result;
    }

    public final String executeCommandAndGetOutput(String command) {
        StringBuilder output = new StringBuilder();
        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    reader.close();
                    process.waitFor();
                    process.destroy();
                    return output.toString();
                }
                output.append(line);
                output.append("");
            }
        } catch (Exception ex) {
            return null;
        }
    }

    String Selinux(String Command) {
        try {
            Process process = Runtime.getRuntime().exec(Command);

            // Reads stdout.
            // NOTE: You can write to stdin of the command using
            // process.getOutputStream().
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            int read;
            char[] buffer = new char[4096];
            output = new StringBuffer();
            while ((read = reader.read(buffer)) > 0) {
                output.append(buffer, 0, read);
            }
            reader.close();
            // Waits for the command to finish.
            process.waitFor();
            return output.toString();
        } catch (Exception ex) {
            return null;
        }
    }
}