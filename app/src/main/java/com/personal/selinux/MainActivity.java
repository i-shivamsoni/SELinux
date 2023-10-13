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

setenforce 0
output
2023-10-13 20:32:14.799 16806-16806 JNI simple app           V   ---- NOT ENFORCING
2023-10-13 20:32:14.799 16806-16806 libc                     A  Fatal signal 4 (SIGILL), code 2 (ILL_ILLOPN), fault addr 0xc9cd27dc in tid 16806 (m.c0c0n.selinux), pid 16806 (m.c0c0n.selinux)
2023-10-13 20:32:14.797 16806-16806 m.c0c0n.selinux          I  type=1400 audit(0.0:950): avc: denied { read } for name="enforce" dev="selinuxfs" ino=4 scontext=u:r:untrusted_app:s0:c156,c256,c512,c768 tcontext=u:object_r:selinuxfs:s0 tclass=file permissive=1 app=com.c0c0n.selinux

setenforce 1
2023 - 10 - 13 20:41:16.549 0 JNI simple app V ----Unable to read the enforce file
2023 - 10 - 13 20:41:16.549 0 JNI E Unable to open enforce file
2023 - 10 - 13 20:41:16.549 0 libc A Fatal signal 4 (SIGILL), code 2(ILL_ILLOPN), fault addr 0xc98977dc in tid 17166 (m.c0c0n.selinux), pid 17166
        (m.c0c0n.selinux)
 */


//        String status = executeCommandAndGetOutput("/system/bin/cat /sys/fs/selinux/enforce");
        /*

        String status = null;
        try {
            status = Selinux("/system/bin/cat /sys/fs/selinux/enforce");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        Log.d("SELinux-result", "onCreate: out " + status);
        if (status.contains("0")) {
            Log.d("SELinux-result", " then SELinux is disabled");
        } else if (status.contains("")) {
            Log.d("SELinux-result", " then SELinux is enabled");
        }
         */

        try {
            String result = Selinux("getenforce");
            if (result.contains("Permissive")) {
                Log.d("result", "onCreate: " + "Permissive");
            } else {
                Log.d("result", "onCreate: " + "Enforcing");
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

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

    String Selinux(String Command) throws IOException, InterruptedException {
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
    }
}