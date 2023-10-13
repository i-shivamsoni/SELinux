cmake_minimum_required(VERSION 3.4.1)

# Define the C++ source files to be compiled
add_library(selinux SHARED selinux.cpp)

# Link the necessary libraries (e.g., log library)
find_library(log-lib log)

# Link the libraries to your C++ code
target_link_libraries(selinux ${log-lib})
