#pragma once

#include "il2cpp-config.h"

#ifndef _MSC_VER
# include <alloca.h>
#else
# include <malloc.h>
#endif

#include <stdint.h>

#include "mscorlib_System_Object2689449295.h"

// System.String
struct String_t;




#ifdef __clang__
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Winvalid-offsetof"
#pragma clang diagnostic ignored "-Wunused-variable"
#endif

// FileModel
struct  FileModel_t3463631299  : public Il2CppObject
{
public:
	// System.String FileModel::mFilePath
	String_t* ___mFilePath_0;
	// System.String FileModel::mFileUrl
	String_t* ___mFileUrl_1;

public:
	inline static int32_t get_offset_of_mFilePath_0() { return static_cast<int32_t>(offsetof(FileModel_t3463631299, ___mFilePath_0)); }
	inline String_t* get_mFilePath_0() const { return ___mFilePath_0; }
	inline String_t** get_address_of_mFilePath_0() { return &___mFilePath_0; }
	inline void set_mFilePath_0(String_t* value)
	{
		___mFilePath_0 = value;
		Il2CppCodeGenWriteBarrier(&___mFilePath_0, value);
	}

	inline static int32_t get_offset_of_mFileUrl_1() { return static_cast<int32_t>(offsetof(FileModel_t3463631299, ___mFileUrl_1)); }
	inline String_t* get_mFileUrl_1() const { return ___mFileUrl_1; }
	inline String_t** get_address_of_mFileUrl_1() { return &___mFileUrl_1; }
	inline void set_mFileUrl_1(String_t* value)
	{
		___mFileUrl_1 = value;
		Il2CppCodeGenWriteBarrier(&___mFileUrl_1, value);
	}
};

#ifdef __clang__
#pragma clang diagnostic pop
#endif
