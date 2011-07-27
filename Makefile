ifeq ($(origin NETLOGO), undefined)
  NETLOGO=../..
endif

ifeq ($(origin SCALA_HOME), undefined)
  SCALA_HOME=../..
endif

SRCS=$(wildcard src/*.scala)

parallel.jar: $(SRCS) manifest.txt Makefile
	mkdir -p classes
	$(SCALA_HOME)/bin/scalac -deprecation -unchecked -encoding us-ascii -classpath $(NETLOGO)/NetLogo.jar -d classes $(SRCS)
	jar cmf manifest.txt parallel.jar -C classes .

parallel.zip: parallel.jar
	rm -rf parallel
	mkdir parallel
	cp -rp parallel.jar README.md Makefile src manifest.txt tests.txt parallel
	zip -rv parallel.zip parallel
	rm -rf parallel
