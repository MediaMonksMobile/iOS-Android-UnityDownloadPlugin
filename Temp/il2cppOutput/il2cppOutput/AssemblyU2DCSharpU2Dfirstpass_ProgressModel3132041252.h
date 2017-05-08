#pragma once

#include "il2cpp-config.h"

#ifndef _MSC_VER
# include <alloca.h>
#else
# include <malloc.h>
#endif

#include <stdint.h>

#include "mscorlib_System_Object2689449295.h"
#include "AssemblyU2DCSharpU2Dfirstpass_ProgressModel_Progre2828579386.h"

// FileModel
struct FileModel_t3463631299;




#ifdef __clang__
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Winvalid-offsetof"
#pragma clang diagnostic ignored "-Wunused-variable"
#endif

// ProgressModel
struct  ProgressModel_t3132041252  : public Il2CppObject
{
public:
	// FileModel ProgressModel::mFile
	FileModel_t3463631299 * ___mFile_0;
	// ProgressModel/ProgressType ProgressModel::mProgressType
	int32_t ___mProgressType_1;
	// System.Int32 ProgressModel::mProgress
	int32_t ___mProgress_2;
	// System.Int32 ProgressModel::mGroupSize
	int32_t ___mGroupSize_3;
	// System.Int32 ProgressModel::mGroupPosition
	int32_t ___mGroupPosition_4;

public:
	inline static int32_t get_offset_of_mFile_0() { return static_cast<int32_t>(offsetof(ProgressModel_t3132041252, ___mFile_0)); }
	inline FileModel_t3463631299 * get_mFile_0() const { return ___mFile_0; }
	inline FileModel_t3463631299 ** get_address_of_mFile_0() { return &___mFile_0; }
	inline void set_mFile_0(FileModel_t3463631299 * value)
	{
		___mFile_0 = value;
		Il2CppCodeGenWriteBarrier(&___mFile_0, value);
	}

	inline static int32_t get_offset_of_mProgressType_1() { return static_cast<int32_t>(offsetof(ProgressModel_t3132041252, ___mProgressType_1)); }
	inline int32_t get_mProgressType_1() const { return ___mProgressType_1; }
	inline int32_t* get_address_of_mProgressType_1() { return &___mProgressType_1; }
	inline void set_mProgressType_1(int32_t value)
	{
		___mProgressType_1 = value;
	}

	inline static int32_t get_offset_of_mProgress_2() { return static_cast<int32_t>(offsetof(ProgressModel_t3132041252, ___mProgress_2)); }
	inline int32_t get_mProgress_2() const { return ___mProgress_2; }
	inline int32_t* get_address_of_mProgress_2() { return &___mProgress_2; }
	inline void set_mProgress_2(int32_t value)
	{
		___mProgress_2 = value;
	}

	inline static int32_t get_offset_of_mGroupSize_3() { return static_cast<int32_t>(offsetof(ProgressModel_t3132041252, ___mGroupSize_3)); }
	inline int32_t get_mGroupSize_3() const { return ___mGroupSize_3; }
	inline int32_t* get_address_of_mGroupSize_3() { return &___mGroupSize_3; }
	inline void set_mGroupSize_3(int32_t value)
	{
		___mGroupSize_3 = value;
	}

	inline static int32_t get_offset_of_mGroupPosition_4() { return static_cast<int32_t>(offsetof(ProgressModel_t3132041252, ___mGroupPosition_4)); }
	inline int32_t get_mGroupPosition_4() const { return ___mGroupPosition_4; }
	inline int32_t* get_address_of_mGroupPosition_4() { return &___mGroupPosition_4; }
	inline void set_mGroupPosition_4(int32_t value)
	{
		___mGroupPosition_4 = value;
	}
};

#ifdef __clang__
#pragma clang diagnostic pop
#endif
