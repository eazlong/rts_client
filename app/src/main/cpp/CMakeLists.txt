# Sets the minimum version of CMake required to build the native
# library. You should either keep the default value or only pass a
# value of 3.4.0 or lower.

cmake_minimum_required(VERSION 3.4.1)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code./
# You can define multiple libraries, and CMake builds it for you.
# Gradle automatically packages shared libraries with your APK.

add_library( # Sets the name of the library.
             speex

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             # Associated headers in the same location as their source
             # file are automatically included.
             speex.cpp

             libspeex/bits.c
             libspeex/cb_search.c
             libspeex/exc_5_64_table.c
             libspeex/exc_5_256_table.c
             libspeex/exc_8_128_table.c
             libspeex/exc_10_16_table.c
             libspeex/exc_10_32_table.c
             libspeex/exc_20_32_table.c
             libspeex/filters.c
             libspeex/gain_table.c
             libspeex/gain_table_lbr.c
             libspeex/hexc_10_32_table.c
             libspeex/hexc_table.c
             libspeex/high_lsp_tables.c
             libspeex/kiss_fft.c
             libspeex/kiss_fftr.c
             libspeex/lpc.c
             libspeex/lsp.c
             libspeex/lsp_tables_nb.c
             libspeex/ltp.c
             libspeex/modes.c
             libspeex/modes_wb.c
             libspeex/nb_celp.c
             libspeex/quant_lsp.c
             libspeex/sb_celp.c
             libspeex/smallft.c
             libspeex/speex.c
             libspeex/speex_callbacks.c
             libspeex/speex_header.c
             libspeex/stereo.c
             libspeex/vbr.c
             libspeex/vorbis_psy.c
             libspeex/vq.c
             libspeex/window.c
             )

			 
set(rtmp_dir ../../../../common/rtmp)
add_subdirectory(${rtmp_dir} rtmp)

# Searches for a specified prebuilt library and stores the path as a
# variable. Because system libraries are included in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

# find_library( # Sets the name of the path variable.
#               log-lib
#
#               # Specifies the name of the NDK library that
#               # you want CMake to locate.
#               log )

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in the
# build script, prebuilt third-party libraries, or system libraries.
target_include_directories(speex PRIVATE
	${rtmp_dir})

target_link_libraries( # Specifies the target library.
                       speex

                       # Links the target library to the log library
                       # included in the NDK.
                       android
#                       ${log-lib}
                       log
					   rtmp
                       )