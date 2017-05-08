#pragma once

#include "il2cpp-config.h"

#ifndef _MSC_VER
# include <alloca.h>
#else
# include <malloc.h>
#endif

#include <stdint.h>

#include "AssemblyU2DCSharpU2Dfirstpass_Singleton_1_gen3288307080.h"

// MobileDownloadManager/Success
struct Success_t1862374945;
// MobileDownloadManager/Error
struct Error_t1232275708;
// MobileDownloadManager/Progress
struct Progress_t894445267;
// MobileDownloadManager/Size
struct Size_t1882781731;




#ifdef __clang__
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Winvalid-offsetof"
#pragma clang diagnostic ignored "-Wunused-variable"
#endif

// MobileDownloadManager
struct  MobileDownloadManager_t2112422693  : public Singleton_1_t3288307080
{
public:
	// MobileDownloadManager/Success MobileDownloadManager::OnSuccess
	Success_t1862374945 * ___OnSuccess_5;
	// MobileDownloadManager/Error MobileDownloadManager::OnError
	Error_t1232275708 * ___OnError_6;
	// MobileDownloadManager/Progress MobileDownloadManager::OnProgress
	Progress_t894445267 * ___OnProgress_7;
	// MobileDownloadManager/Size MobileDownloadManager::OnSize
	Size_t1882781731 * ___OnSize_8;

public:
	inline static int32_t get_offset_of_OnSuccess_5() { return static_cast<int32_t>(offsetof(MobileDownloadManager_t2112422693, ___OnSuccess_5)); }
	inline Success_t1862374945 * get_OnSuccess_5() const { return ___OnSuccess_5; }
	inline Success_t1862374945 ** get_address_of_OnSuccess_5() { return &___OnSuccess_5; }
	inline void set_OnSuccess_5(Success_t1862374945 * value)
	{
		___OnSuccess_5 = value;
		Il2CppCodeGenWriteBarrier(&___OnSuccess_5, value);
	}

	inline static int32_t get_offset_of_OnError_6() { return static_cast<int32_t>(offsetof(MobileDownloadManager_t2112422693, ___OnError_6)); }
	inline Error_t1232275708 * get_OnError_6() const { return ___OnError_6; }
	inline Error_t1232275708 ** get_address_of_OnError_6() { return &___OnError_6; }
	inline void set_OnError_6(Error_t1232275708 * value)
	{
		___OnError_6 = value;
		Il2CppCodeGenWriteBarrier(&___OnError_6, value);
	}

	inline static int32_t get_offset_of_OnProgress_7() { return static_cast<int32_t>(offsetof(MobileDownloadManager_t2112422693, ___OnProgress_7)); }
	inline Progress_t894445267 * get_OnProgress_7() const { return ___OnProgress_7; }
	inline Progress_t894445267 ** get_address_of_OnProgress_7() { return &___OnProgress_7; }
	inline void set_OnProgress_7(Progress_t894445267 * value)
	{
		___OnProgress_7 = value;
		Il2CppCodeGenWriteBarrier(&___OnProgress_7, value);
	}

	inline static int32_t get_offset_of_OnSize_8() { return static_cast<int32_t>(offsetof(MobileDownloadManager_t2112422693, ___OnSize_8)); }
	inline Size_t1882781731 * get_OnSize_8() const { return ___OnSize_8; }
	inline Size_t1882781731 ** get_address_of_OnSize_8() { return &___OnSize_8; }
	inline void set_OnSize_8(Size_t1882781731 * value)
	{
		___OnSize_8 = value;
		Il2CppCodeGenWriteBarrier(&___OnSize_8, value);
	}
};

#ifdef __clang__
#pragma clang diagnostic pop
#endif
