
all: test

repl:
	lein with-profile +test,+dev repl

.PHONY: test
test:
	lein test
