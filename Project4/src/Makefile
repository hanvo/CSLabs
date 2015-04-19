GS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	Project4.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class

run:
	sh run.sh
