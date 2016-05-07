all: compile test clean

compile:
	g++ -std=c++14 -o decorator_template decorator_template.cpp
	g++ -o decorator decorator.cpp

clean:
	rm decorator_template decorator

test:
	./decorator
	./decorator_template