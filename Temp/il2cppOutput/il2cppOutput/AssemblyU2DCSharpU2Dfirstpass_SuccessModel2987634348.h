#pragma once

#include "il2cpp-config.h"

#ifndef _MSC_VER
# include <alloca.h>
#else
# include <malloc.h>
#endif

#include <stdint.h>

#include "mscorlib_System_Object2689449295.h"
#include "AssemblyU2DCSharpU2Dfirstpass_SuccessModel_Success1018401676.h"

// System.String
struct String_t;
// FileModel[]
struct FileModelU5BU5D_t1902434258;




#ifdef __clang__
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Winvalid-offsetof"
#pragma clang diagnostic ignored "-Wunused-variable"
#endif

// SuccessModel
struct  SuccessModel_t2987634348  : public Il2CppObject
{
public:
	// SuccessModel/SuccessType SuccessModel::mSuccessType
	int32_t ___mSuccessType_0;
	// System.String SuccessModel::mSuccessMessage
	String_t* ___mSuccessMessage_1;
	// FileModel[] SuccessModel::mFiles
	FileModelU5BU5D_t1902434258* ___mFiles_2;

public:
	inline static int32_t get_offset_of_mSuccessType_0() { return static_cast<int32_t>(offsetof(SuccessModel_t2987634348, ___mSuccessType_0)); }
	inline int32_t get_mSuccessType_0() const { return ___mSuccessType_0; }
	inline int32_t* get_address_of_mSuccessType_0() { return &___mSuccessType_0; }
	inline void set_mSuccessType_0(int32_t value)
	{
		___mSuccessType_0 = value;
	}

	inline static int32_t get_offset_of_mSuccessMessage_1() { return static_cast<int32_t>(offsetof(SuccessModel_t2987634348, ___mSuccessMessage_1)); }
	inline String_t* get_mSuccessMessage_1() const { return ___mSuccessMessage_1; }
	inline String_t** get_address_of_mSuccessMessage_1() { return &___mSuccessMessage_1; }
	inline void set_mSuccessMessage_1(String_t* value)
	{
		___mSuccessMessage_1 = value;
		Il2CppCodeGenWriteBarrier(&___mSuccessMessage_1, value);
	}

	inline static int32_t get_offset_of_mFiles_2() { return static_cast<int32_t>(offsetof(SuccessModel_t2987634348, ___mFiles_2)); }
	inline FileModelU5BU5D_t1902434258* get_mFiles_2() const { return ___mFiles_2; }
	inline FileModelU5BU5D_t1902434258** get_address_of_mFiles_2() { return &___mFiles_2; }
	inline void set_mFiles_2(FileModelU5BU5D_t1902434258* value)
	{
		___mFiles_2 = value;
		Il2CppCodeGenWriteBarrier(&___mFiles_2, value);
	}
};

#ifdef __clang__
#pragma clang diagnostic pop
#endif
