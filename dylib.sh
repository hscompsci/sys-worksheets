c99() {
	cc -std=c99 -Og -Wall -Wextra -Wpedantic "$@"
}

universal() {
	local dest="$1"
	local arm64="`printf %s "$dest" | sed 's/\./-arm64&/'`"
	local x86_64="`printf %s "$dest" | sed 's/\./-x86_64&/'`"
	shift
	c99 -shared -target arm64-apple-darwin -o "$arm64" "$@"
	c99 -shared -target x86_64-apple-darwin -o "$x86_64" "$@"
	lipo "$arm64" "$x86_64" -create -output "$dest"
}

include() {
	printf "%s\n" "`printf "%s\n" /Library/Java/JavaVirtualMachines/* | tail -n1`/Contents/Home/include"
}
