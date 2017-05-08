#pragma once

#include "il2cpp-config.h"

#ifndef _MSC_VER
# include <alloca.h>
#else
# include <malloc.h>
#endif

#include <stdint.h>

#include "mscorlib_System_Object2689449295.h"
#include "AssemblyU2DCSharpU2Dfirstpass_ErrorModel_ErrorType2339525818.h"

// System.String
struct String_t;
// FileModel[]
struct FileModelU5BU5D_t1902434258;




#ifdef __clang__
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Winvalid-offsetof"
#pragma clang diagnostic ignored "-Wunused-variable"
#endif

// ErrorModel
struct  ErrorModel_t3145739141  : public Il2CppObject
{
public:
	// ErrorModel/ErrorType ErrorModel::mErrorType
	int32_t ___mErrorType_0;
	// System.String ErrorModel::mErrorMessage
	String_t* ___mErrorMessage_1;
	// FileModel[] ErrorModel::mFiles
	FileModelU5BU5D_t1902434258* ___mFiles_2;

public:
	inline static int32_t get_offset_of_mErrorType_0() { return static_cast<int32_t>(offsetof(ErrorModel_t3145739141, ___mErrorType_0)); }
	inline int32_t get_mErrorType_0() const { return ___mErrorType_0; }
	inline int32_t* get_address_of_mErrorType_0() { return &___mErrorType_0; }
	inline void set_mErrorType_0(int32_t value)
	{
		___mErrorType_0 = value;
	}

	inline static int32_t get_offset_of_mErrorMessage_1() { return static_cast<int32_t>(offsetof(ErrorModel_t3145739141, ___mErrorMessage_1)); }
	inline String_t* get_mErrorMessage_1() const { return ___mErrorMessage_1; }
	inline String_t** get_address_of_mErrorMessage_1() { return &___mErrorMessage_1; }
	inline void set_mErrorMessage_1(String_t* value)
	{
		___mErrorMessage_1 = value;
		Il2CppCodeGenWriteBarrier(&___mErrorMessage_1, value);
	}

	inline static int32_t get_offset_of_mFiles_2() { return static_cast<int32_t>(offsetof(ErrorModel_t3145739141, ___mFiles_2)); }
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
