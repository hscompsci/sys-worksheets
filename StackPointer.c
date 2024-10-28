#include <stdint.h>

uintptr_t Java_StackPointer_getStackPointer(void) {
	uintptr_t addr = (uintptr_t) &addr;
	return addr;
}
