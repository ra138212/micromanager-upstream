
AUTOMAKE_OPTIONS = foreign subdir-objects

noinst_PROGRAMS = mm_coretest
mm_coretest_SOURCES = MMCoreTest.cpp ../../MMCore/PluginManager.cpp
mm_coretest_LDFLAGS = $(SERIALFRAMEWORKS)
mm_coretest_LDADD = ../../MMCore/libMMCore.la ../../MMDevice/libMMDevice.la
