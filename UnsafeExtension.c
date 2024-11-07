#include "jni.h"

#include <sys/mman.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

void *Java_UnsafeExtension_allocateInitializedMemory(JNIEnv *jvm, jobject this, size_t bytes) {
	void *address = calloc(1, bytes);
	if(address == NULL) {
		perror("calloc()");
	}

	if(mlock(address, bytes)) {
		address = NULL;
	}

	return address;
}

void *Java_UnsafeExtension_mapInitializedMemory(JNIEnv *jvm, jobject this, size_t bytes) {
	void *address = mmap(NULL, bytes, PROT_READ + PROT_WRITE, MAP_ANONYMOUS + MAP_SHARED, -1, 0);
	if(address == MAP_FAILED) {
		perror("mmap()");
		address = NULL;
	}
	return address;
}

bool Java_UnsafeExtension_unmapMemory(JNIEnv *jvm, jobject this, void *address, size_t bytes) {
	bool success = munmap(address, bytes) == 0;
	if(!success) {
		perror("munmap()");
	}
	return success;
}

void *Java_UnsafeExtension_mapFileBackedMemory(JNIEnv *jvm, jobject this, jstring filename) {
	const char *name = (*jvm)->GetStringUTFChars(jvm, filename, NULL);
	int file = open(name, O_RDWR);
	(*jvm)->ReleaseStringUTFChars(jvm, filename, name);
	if(file < 0) {
		perror("open()");
		return NULL;
	}

	struct stat statistics;
	fstat(file, &statistics);

	void *address = mmap(NULL, statistics.st_size, PROT_READ + PROT_WRITE, MAP_SHARED, file, 0);
	if(address == MAP_FAILED) {
		perror("mmap()");
		address = NULL;
	}

	close(file);
	return address;
}

static jobject findBytes(JNIEnv *jvm, jstring string) {
	return (*jvm)->GetObjectField(
		jvm,
		string,
		(*jvm)->GetFieldID(
			jvm,
			(*jvm)->GetObjectClass(jvm, string),
			"value",
			"[B"
		)
	);
}

static char *getBytes(JNIEnv *jvm, jobject bytes) {
	jboolean copied;
	char *buffer = (*jvm)->GetPrimitiveArrayCritical(jvm, bytes, &copied);
	if(copied) {
		puts("Error: Could not access string's bytes without copying!");
		abort();
	}
	return buffer;
}

static void releaseBytes(JNIEnv *jvm, jobject bytes, char *buffer) {
	(*jvm)->ReleasePrimitiveArrayCritical(jvm, bytes, buffer, 0);
}

jlong Java_UnsafeExtension_getAddress(JNIEnv *jvm, jobject this, jstring string) {
	string = findBytes(jvm, string);
	char *bytes = getBytes(jvm, string);
	releaseBytes(jvm, string, bytes);
	return (jlong) bytes;
}
