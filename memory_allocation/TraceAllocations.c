#ifdef __APPLE__

#include "dyld-interposing.h"

#define WRAPPER static

#else

#include <sys/types.h>

#pragma redefine_extname calloc_ calloc
#pragma redefine_extname free_ free
#pragma redefine_extname malloc_ malloc
#pragma redefine_extname mmap_ mmap
#pragma redefine_extname munmap_ munmap

void *calloc_(size_t, size_t);
void free_(void *);
void *malloc_(size_t);
void *mmap_(void *, size_t, int, int, int, off_t);
int munmap_(void *, size_t);

#pragma GCC warning "Link with -Wl,--wrap=calloc,--wrap=free,--wrap=malloc,--wrap=mmap,--wrap=munmap"

#define calloc __real_calloc
#define free __real_free
#define malloc __real_malloc
#define mmap __real_mmap
#define munmap __real_munmap

#define DYLD_INTERPOSE(no, op)
#define WRAPPER

#endif

#include "unstdio.h"

#include <sys/mman.h>
#include <limits.h>
#include <stdint.h>
#include <stdlib.h>

static const size_t REMOVE = 0;

static size_t smallest = SIZE_MAX;

static void report(const char *, ssize_t);
static size_t list(const void *, size_t);

void Java_TraceAllocations_start(uintptr_t jvm, uintptr_t this, long minimum) {
	if(minimum <= 0) {
		minimum = 1;
	}
	smallest = minimum;
}

void Java_TraceAllocations_stop(void) {
	smallest = SIZE_MAX;
	report(NULL, 0);
}

WRAPPER void *malloc_(size_t size) {
	void *result = malloc(size);
	if(size >= smallest) {
		report("allocated", size);
		list(result, size);
	}
	return result;
}
DYLD_INTERPOSE(malloc_, malloc)

WRAPPER void *calloc_(size_t count, size_t size) {
	void *result = calloc(count, size);
	if(count * size >= smallest) {
		report("allocated", count * size);
		list(result, count * size);
	}
	return result;
}
DYLD_INTERPOSE(calloc_, calloc)

WRAPPER void free_(void *ptr) {
	size_t size = list(ptr, REMOVE);
	if(ptr != NULL && size >= smallest) {
		report("freed", -size);
	}
	free(ptr);
}
DYLD_INTERPOSE(free_, free)

WRAPPER void *mmap_(void *addr, size_t len, int prot, int flags, int fd, off_t offset) {
	void *result = mmap(addr, len, prot, flags, fd, offset);
	if(len >= smallest) {
		report("allocated", len);
	}
	return result;
}
DYLD_INTERPOSE(mmap_, mmap)

WRAPPER int munmap_(void *addr, size_t len) {
	if(len >= smallest) {
		report("freed", -len);
	}
	return munmap(addr, len);
}
DYLD_INTERPOSE(munmap_, munmap)

static void report(const char *operation, ssize_t size) {
	static const char *last_operation = NULL;
	static size_t last_calls = 1;
	static ssize_t last_size = 0;
	static ssize_t net_calls = 0;
	static ssize_t net_size = 0;

	if(operation != NULL) {
		if(size > 0) {
			++net_calls;
		} else {
			--net_calls;
		}
		net_size += size;
	}

	if(operation == last_operation) {
		++last_calls;
		last_size += size;
	} else {
		if(last_operation != NULL) {
			ssize_t display_size = last_size;
			ssize_t display_net = net_size;
			const char *prefix = "";
			for(int index = 0; llabs(display_size) >= 1024; index += 5) {
				display_size /= 1024;
				display_net /= 1024;
				prefix = "kilo\0mega\0giga" + index;
			}

			fprintf_noallocate(stderr, "[libTA: JVM %s %lu times, consuming %ld %sbytes]\n", last_operation, last_calls, display_size, prefix);
			if(operation == NULL) {
				fprintf_noallocate(stderr, "[TA.stop(): %ld unfreed allocations totalling %ld %sbytes]\n", net_calls, display_net, prefix);
			}
		}
		last_operation = operation;
		last_calls = 1;
		last_size = size;
	}
}

static size_t list(const void *ptr, size_t add) {
	struct pair {
		const void *ptr;
		size_t size;
	};

	static size_t capacity = 128;
	static struct pair *array = NULL;
	static size_t length;

	if(add != REMOVE) {
		if(array == NULL) {
			array = malloc(capacity * sizeof *array);
		} else if(length == capacity) {
			capacity *= 2;
			array = realloc(array, capacity * sizeof *array);
		}

		array[length].ptr = ptr;
		array[length].size = add;
		++length;
	} else if(ptr != NULL) {
		for(size_t index = 0; index != length; ++index) {
			if(array[index].ptr == ptr) {
				array[index].ptr = NULL;
				return array[index].size;
			}
		}
	}
	return 0;
}
