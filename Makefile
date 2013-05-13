CLASSPATH=-classpath .:/usr/share/java/protobuf-java.jar

PACKAGE=com/rethinkdb
JAVAFILES=$(wildcard $(PACKAGE)/*.java)
CLASSFILES=$(patsubst $(PACKAGE)/%.java,$(PACKAGE)/%.class,$(JAVAFILES))
PB_GEN=./$(PACKAGE)/Ql2.java

test: $(CLASSFILES) $(PB_GEN)
	java $(CLASSPATH) com.rethinkdb.Test

$(PACKAGE)/%.class: $(PACKAGE)/%.java
	javac $(CLASSPATH) $<

$(PB_GEN): ql2.proto
	protoc --java_out=. ql2.proto

clean:
	rm -f $(PACKAGE)/*.class
	rm -f $(PB_GEN)

.PHONY: test clean
