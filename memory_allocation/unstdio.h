#ifndef UNSTDIO_H_
#define UNSTDIO_H_

#include <stdarg.h>
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>

static inline void puts_noallocate(const char *restrict string, FILE *restrict stream) {
	for(const char *each = string; *each != '\0'; ++each) {
		putc_unlocked(*each, stderr);
	}
}

static inline void fprintf_noallocate(FILE *restrict stream, const char *restrict format, ...) {
	va_list args;
	va_start(args, format);
	flockfile(stream);

	for(const char *each = format; *each != '\0'; ++each) {
		if(*each != '%') {
			putc_unlocked(*each, stream);
		} else {
			++each;
			if(*each == 's') {
				puts_noallocate(va_arg(args, const char *), stream);
			} else {
				if(*each == '#') {
					putc_unlocked('0', stream);
					putc_unlocked('x', stream);
					++each;
				}

				bool wide = false;
				if(*each == 'l') {
					wide = true;
					++each;
				}

				char specifier = *each;
				unsigned long value = va_arg(args, unsigned long);
				if(specifier == 'd') {
					long reinterpreted = value;
					if(reinterpreted < 0) {
						putc_unlocked('-', stream);
						reinterpreted *= -1;
						value = reinterpreted;
					}
					specifier = 'u';
				}

				int base;
				unsigned long placeValue;
				if(specifier == 'u') {
					base = 10;
					if(wide) {
						placeValue = 10000000000000000000ul;
					} else {
						placeValue = 1000000000;
					}
				} else if(specifier == 'x') {
					base = 16;
					if(wide) {
						placeValue = 1152921504606846976ul;
					} else {
						placeValue = 268435456;
					}
				} else {
					putc_unlocked('\n', stream);

					puts_noallocate("fprintf_noallocate(): unimplemented format specifier ", stderr);
					putc_unlocked(specifier, stderr);
					putc_unlocked('\n', stderr);

					exit(1);
				}

				bool nonleading = false;
				while(placeValue != 0) {
					int digit = value / placeValue;

					nonleading = nonleading || digit != 0;
					if(nonleading || placeValue == 1) {
						char display = '0' + digit;
						if(display > '9') {
							display += 'a' - '9' - 1;
						}
						putc_unlocked(display, stream);
					}

					value %= placeValue;
					placeValue /= base;
				}
			}
		}
	}

	funlockfile(stream);
	va_end(args);
}

#endif
