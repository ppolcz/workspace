OBJ_DIR = /tmp/polcz/obj
SRC_DIR = src
TARGET_SO_ = /usr/local/lib/libpolcz.so.1.1
TARGET_SO = /usr/local/lib/libpolcz.so
TARGET_INC_DIR = /usr/local/include/polcz

INCLUDE_DIRS = .

CXX_FLAGS = -std=c++14
COMPILE_FLAGS = -c -fPIC
LINKER_FLAGS = -shared

HEADER_FILES = $(wildcard *.h) $(wildcard *.hpp)
HEADER_LINKS = $(patsubst %,$(TARGET_INC_DIR)/%,$(HEADER_FILES))
CPP_FILES = $(wildcard $(SRC_DIR)/*.cpp)
OBJ_FILES = $(patsubst $(SRC_DIR)/%.cpp,$(OBJ_DIR)/%.o,$(CPP_FILES))
INCLUDE_FLAGS = $(patsubst %,-I%,$(INCLUDE_DIRS))

$(info $(HEADER_FILES))
$(info $(HEADER_LINKS))
# $(info $(CPP_FILES))
# $(info $(OBJ_FILES))
# $(info $(INCLUDE_DIRS))
# $(info $(INCLUDE_FLAGS))

all: $(OBJ_DIR) $(TARGET_SO) $(TARGET_INC_DIR) $(HEADER_LINKS)

$(TARGET_SO): $(TARGET_SO_)
	@echo Creating symbolic link to shared library file: $@
	@ln -s -f $< $@

$(TARGET_SO_): $(OBJ_FILES)
	@echo Generating shared library file: $@
	@g++ $(LINKER_FLAGS) $(CXX_FLAGS) -o $@ $^

$(OBJ_DIR)/%.o: $(SRC_DIR)/%.cpp __FORCED
	@echo Compiling source file: $<
	@g++ $(COMPILE_FLAGS) $(CXX_FLAGS) -o $@ $< $(INCLUDE_FLAGS)

$(OBJ_DIR) $(TARGET_INC_DIR): 
	mkdir -p $@
	
$(TARGET_INC_DIR)/%.h: %.h
	@ln -s -f `pwd`/$< $@ 
	
__FORCED: