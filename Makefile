
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

toc-install:
	npm install --save markdown-toc

toc-build:
	node_modules/.bin/markdown-toc -i README.md

release: install test
	lein release
