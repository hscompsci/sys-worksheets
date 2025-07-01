#include <sys/time.h>
#include <stdbool.h>
#include <stdint.h>
#include <string.h>

bool Java_Timer_set(uintptr_t jvm, uintptr_t this, long milliseconds, bool repeating, int identifier) {
	struct itimerval config;
	config.it_value.tv_sec = milliseconds / 1000;
	config.it_value.tv_usec = milliseconds % 1000 * 1000;
	if(repeating) {
		memcpy(&config.it_interval, &config.it_value, sizeof config.it_interval);
	} else {
		memset(&config.it_interval, 0, sizeof config.it_interval);
	}
	return setitimer(identifier, &config, NULL) == 0;
}
