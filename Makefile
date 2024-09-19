
repl:
	lein with-profile +test,+dev repl

.phony: test
test:
	lein test
