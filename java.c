// Wrapper that launches the system's java with system integrity protection disabled.
// This allows both interposing dynamic symbols via DYLD_INTERPOSE() and debugging the process.

#include <mach-o/dyld.h>
#include <dlfcn.h>
#include <stddef.h>
#include <stdio.h>

typedef int main_t(int, char **);

static main_t *find_main(const struct mach_header_64 *header) {
	const struct load_command *end = (struct load_command *) (
		(uintptr_t) (header + 1) + header->sizeofcmds
	);
	for(
		const struct load_command *command = (const struct load_command *) (header + 1);
		command != end;
		command = (const struct load_command *) ((uintptr_t) command + command->cmdsize)
	) {
		if(command->cmd == LC_MAIN) {
			const struct entry_point_command *main = (const struct entry_point_command *) command;
			return (main_t *) ((uintptr_t) header + main->entryoff);
		}
	}
	return NULL;
}

int main(int argc, char **argv) {
	// We cannot exec() the installed java binary, as that would trigger system integrity protection.
	// Instead, load it into our memory space without replacing the process image...
	uint32_t index = _dyld_image_count();
	void *java = dlopen("/usr/bin/java", RTLD_NOW);
	if(java == NULL) {
		fprintf(stderr, "%s\n", dlerror());
		return -1;
	}

	// ...then find and call its main() method.
	main_t *main = find_main((const struct mach_header_64 *) _dyld_get_image_header(index));
	int status = -2;
	if(main != NULL) {
		status = main(argc, argv);
	}

	dlclose(java);
	return status;
}
