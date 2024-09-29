
all: lint install test

.PHONY: lint
lint:
	clj-kondo \
		--lint deed-core/src/clj \
		--lint deed-base64/src/ \
		--lint deed-vectorz/src/clj

install:
	lein sub with-profile uberjar install

.PHONY: test
test:
	lein sub test
