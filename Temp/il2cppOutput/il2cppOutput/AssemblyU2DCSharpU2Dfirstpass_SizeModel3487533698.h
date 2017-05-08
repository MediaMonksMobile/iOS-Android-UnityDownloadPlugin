#pragma once

#include "il2cpp-config.h"

#ifndef _MSC_VER
# include <alloca.h>
#else
# include <malloc.h>
#endif

#include <stdint.h>

#include "mscorlib_System_Object2689449295.h"
#include "AssemblyU2DCSharpU2Dfirstpass_SizeModel_SizeType437040058.h"

// System.String
struct String_t;
// FileModel[]
struct FileModelU5BU5D_t1902434258;




#ifdef __clang__
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Winvalid-offsetof"
#pragma clang diagnostic ignored "-Wunused-variable"
#endif

// SizeModel
struct  SizeModel_t3487533698  : public Il2CppObject
{
public:
	// SizeModel/SizeType SizeModel::mSizeType
	int32_t ___mSizeType_0;
	// System.String SizeModel::mSizeMessage
	String_t* ___mSizeMessage_1;
	// FileModel[] SizeModel::mFiles
	FileModelU5BU5D_t1902434258* ___mFiles_2;

public:
	inline static int32_t get_offset_of_mSizeType_0() { return static_cast<int32_t>(offsetof(SizeModel_t3487533698, ___mSizeType_0)); }
	inline int32_t get_mSizeType_0() const { return ___mSizeType_0; }
	inline int32_t* get_address_of_mSizeType_0() { return &___mSizeType_0; }
	inline void set_mSizeType_0(int32_t value)
	{
		___mSizeType_0 = value;
	}

	inline static int32_t get_offset_of_mSizeMessage_1() { return static_cast<int32_t>(offsetof(SizeModel_t3487533698, ___mSizeMessage_1)); }
	inline String_t* get_mSizeMessage_1() const { return ___mSizeMessage_1; }
	inline String_t** get_address_of_mSizeMessage_1() { return &___mSizeMessage_1; }
	inline void set_mSizeMessage_1(String_t* value)
	{
		___mSizeMessage_1 = value;
		Il2CppCodeGenWriteBarrier(&___mSizeMessage_1, value);
	}

	inline static int32_t get_offset_of_mFiles_2() { return static_cast<int32_t>(offsetof(SizeModel_t3487533698, ___mFiles_2)); }
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
