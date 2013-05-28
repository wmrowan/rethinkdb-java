CLASSPATH=-classpath .:/usr/share/java/protobuf-java-2.4.1.jar

PACKAGE=com/rethinkdb
JAVAFILES=$(wildcard $(PACKAGE)/*.java)
CLASSFILES=$(patsubst $(PACKAGE)/%.java,$(PACKAGE)/%.class,$(JAVAFILES))
PB_GEN=./$(PACKAGE)/Ql2.java
JAR=rethinkdb.jar

test: $(CLASSFILES) $(PB_GEN)
	java $(CLASSPATH) com.rethinkdb.Test

$(PACKAGE)/%.class: $(PACKAGE)/%.java
	javac -g $(CLASSPATH) $<

$(PB_GEN): ql2.proto
	protoc --java_out=. ql2.proto

$(JAR): $(CLASSFILES) $(PB_GEN)
	jar cf $(JAR) $(foreach FILE,$(wildcard $(PACKAGE)/*.class),'$(FILE)')

clean:
	rm -f $(PACKAGE)/*.class
	rm -f $(PB_GEN)
	rm -f $(JAR)

.PHONY: test clean
