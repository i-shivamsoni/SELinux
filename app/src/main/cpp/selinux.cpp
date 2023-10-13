#include <jni.h>
#include <cstdio>
#include <cstdlib>
#include <cstring>
#include <android/log.h>
#include <sys/system_properties.h>
#include <stdexcept>

using namespace std;


int selinuxStatusChecker();

int checkProperty(const char *key, const char *value2check);

extern "C" JNIEXPORT jint JNICALL
Java_com_personal_selinux_MainActivity_selinuxStatusChecker(JNIEnv *env, jobject thiz) {
    // TODO: implement selinuxStatusChecker()

    selinuxStatusChecker();
}

int selinuxStatusChecker() {
    int result = 0;

        FILE *file = fopen("/sys/fs/selinux/enforce",
                           "r"); // on systems that have sysfs mounted, the mount point is /sys/fs/selinux
        char *line = (char *) calloc(50, sizeof(char));


        if (file == NULL) {
            __android_log_print(ANDROID_LOG_VERBOSE, "JNI simple app",
                                " ---- Unable to read the enforce file");
            result = 2;
            return result;
        }
        while (fgets(line, 50, file)) {
            if (strstr(line, "0")) {
                __android_log_print(ANDROID_LOG_VERBOSE, "JNI simple app", " ---- NOT ENFORCING");
                result = 1;
            } else {
                __android_log_print(ANDROID_LOG_VERBOSE, "JNI simple app", " ---- ENFORCING");
                result = 0;
            }
        }
        if (line) { free(line); }
        fclose(file);

    return result;
}

/*
 * Checking Android Properties against a "bad" value and return
 * 0 = false
 * 1 = true
 * 2 = property not found
 */int checkProperty(const char *key, const char *value2check) {
    if (value2check == NULL || key == NULL) return 0;
    char value[20];
    int length = __system_property_get(key, value);
    int result = 0;
    if (length == 0) {
        // Then we know that the property was NOT found
        result = 2;
    }
    if (length > 0) {
        if (strlen(value) >= strlen(value2check) && strstr(value, value2check) != NULL) {
            result = 1;
        }
    }
    return result;
}

extern "C" JNIEXPORT jint JNICALL
Java_com_personal_selinux_MainActivity_checkProperty(JNIEnv *env, jobject thiz, jstring method_name,
                                                     jstring mode) {
    /*
   const char *key = env->GetStringUTFChars(method_name, NULL);
   const char *value2check = env->GetStringUTFChars(mode, NULL);

   int result = checkProperty(key, value2check);

   env->ReleaseStringUTFChars(method_name, key);
   env->ReleaseStringUTFChars(mode, value2check);
*/
    int result = checkProperty(reinterpret_cast<const char *>(method_name),
                               reinterpret_cast<const char *>(mode));

    return result;
}