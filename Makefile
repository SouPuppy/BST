.SILENT:
.PHONY: all compile recompile run time clean timer test

all: test time

compile: BST.java
	javac -encoding UTF-8 -sourcepath . BST.java

run: compile
	java -Dfile.encoding=UTF-8 -XX:+UseSerialGC -Xss64m -Xms1920m -Xmx1920m BST < Input.txt > Output.txt

recompile: 
	make clean
	make compile

test: recompile
	java -Dfile.encoding=UTF-8 -XX:+UseSerialGC -Xss64m -Xms1920m -Xmx1920m BST < Input.txt > Output.txt
	@diff -u Standard.txt Output.txt > diff_output.txt || (echo "⚠️  Error on line:" && grep -n "^-" diff_output.txt | head -n 1 | cut -d: -f1 && exit 1)
	@if [ ! -s diff_output.txt ]; then echo "✅ Correct"; else echo "❌  Incorrect"; fi
	rm -f diff_output.txt
	make clean


time: recompile
	python debug.py
	make clean

debug: compile
	java -Dfile.encoding=UTF-8 -XX:+UseSerialGC -Xss64m -Xms1920m -Xmx1920m BST < Input.txt
	make clean
	
clean:
	rm -f *.class Output.txt diff_output.txt
