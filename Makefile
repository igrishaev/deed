
all: install test

install:
	lein sub with-profile uberjar install

.PHONY: test
test:
	lein sub test
