all:
	mkdir -p bin
	javac -J-Xmx256m -d bin -sourcepath src src/abscon/instance/intension/*/*.java src/csp/CSP.java
	jar -J-Xmx256m cfm csp.jar src/META-INF/MANIFEST.MF -C bin .
clean:
	rm -rf bin/* csp.jar
