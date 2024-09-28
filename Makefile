
all: test

repl:
	lein with-profile +test,+dev repl

.PHONY: clean
clean:
	rm -rf target

.PHONY: test
test: clean
	lein test
