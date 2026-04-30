#include <list>
#include <vector>
#include <string>
#include <pthread.h>
#include <thread>
#include <cstring>
#include <jni.h>
#include <fstream>
#include <iostream>
#include <dlfcn.h>
#include <chrono> 
#include <fcntl.h>
#include <sys/stat.h>
#include <cstddef>
#include <cstdint>
#include <semaphore.h>
#include <stdint.h>
#include <sstream>
#include <stdarg.h>
#include <stdio.h>
#include "oxorany/Tools.h" 
#include "oxorany/Logger.h"
#include "oxorany.h"
#include "esp/obfuscate.h"
#include "oxorany/Utils.h"
#include "Dobby/dobby.h"
#include "KittyMemory/MemoryPatch.h"
#include "oxorany/Macros.h"

#define targetLibName oxorany("libUE4.so")
#define targetLibName oxorany("libhdmpve.so")
#define targetLibName oxorany("libAntsVoice.so")
#define targetLibName oxorany("libanogs.so")
#define targetLibName oxorany("libUE4.so")
#define ARM64_SYSREG(reg0, reg1, reg2, reg3, op) (((reg0) & 0x1F) | (((reg1) & 0x1F) << 5) | (((reg2) & 0x7) << 10) | (((reg3) & 0xF) << 16) | (((op) & 0x7) << 20)) 


char *Offset;
#define ret_zero
#define _BYTE  uint8_t
#define _WORD  uint16_t
#define _DWORD uint32_t
#define _QWORD __int64
#define _OWORD uint64_t
#define _QWORD uint64_t
#define _BOOL8 uint64_t

typedef long long int64; 
typedef short int16;     

uintptr_t UE4;
uintptr_t ANOGS;


DWORD libanogsBase = 0;
DWORD libUE4Base = 0;
DWORD libanortBase = 0;
DWORD libEGLBase = 0;
DWORD libanogsAlloc = 0;
DWORD libUE4Alloc = 0;
DWORD libEGLAlloc = 0;
DWORD libanogsSize = 0;// 0x3856E5  3.6.0
DWORD libUE4Size = 0;// 0x7CF8F10  3.6.0
unsigned int AnogsSize = 0;
unsigned int UE4Size = 0;
uintptr_t UE4Alloc = 0;
uintptr_t AnogsAlloc = 0;

DWORD NewBase = 0;

DWORD libanogsEnd = 0;//

void FixGameCrash(){
    //system("rm -rf /data/data/com.pubg.imobile/files");
    system("rm -rf /data/data/com.pubg.imobile/files/ano_tmp");
    system("touch /data/data/com.pubg.imobile/files/ano_tmp");
    system("chmod 000 /data/data/com.pubg.imobile/files/ano_tmp");
    system("rm -rf /data/data/com.pubg.imobile/files/obblib");
    system("touch /data/data/com.pubg.imobile/files/obblib");
    system("chmod 000 /data/data/com.pubg.imobile/files/obblib");
    system("rm -rf /data/data/com.pubg.imobile/files/xlog");
    system("touch /data/data/com.pubg.imobile/files/xlog");
    system("chmod 000 /data/data/com.pubg.imobile/files/xlog");
    system("rm -rf /data/data/com.pubg.imobile/app_bugly");
    system("touch /data/data/com.pubg.imobile/app_bugly");
    system("chmod 000 /data/data/com.pubg.imobile/app_bugly");
    system("rm -rf /data/data/com.pubg.imobile/app_crashrecord");
    system("touch /data/data/com.pubg.imobile/app_crashrecord");
    system("chmod 000 /data/data/com.pubg.imobile/app_crashrecord");
    system("rm -rf /data/data/com.pubg.imobile/app_crashSight");
    system("touch /data/data/com.pubg.imobile/app_crashSight");
    system("chmod 000 /data/data/com.pubg.imobile/app_crashSight");
}

bool (*ouserinfo)(int a1 , int a2);
bool userinfo(int a1 , int a2)
{
   if(a1 == 3 || a1 == 4){
       return 0LL;
    }
    return ouserinfo(a1,a2);
}


int __fastcall (*osub_1DEDE8)(int a1, int a2, _DWORD *a3, _DWORD *a4);
int __fastcall hsub_1DEDE8(int a1, int a2, _DWORD *a3, _DWORD *a4)
{
    printf("[HOOK] sub_1DEDE8 called with a1=%d, a2=%d, a3=%p, a4=%p\n", a1, a2, a3, a4);
    if (!a3 || !a4)
    {
        printf("[HOOK] Skipping original function because a3 or a4 is NULL.\n");
        return 0; 
    }
    printf("[HOOK] Adding a delay of 1 second...\n");
    sleep(10000);
    int result = hsub_1DEDE8(a1, a2, a3, a4);
    printf("[HOOK] Original function returned: %d\n", result);
    if (result != 0)
    {
        printf("[HOOK] Modifying the result...\n");
        result = result + 1;
    }
    return result;
}



void *anogs_thread(void *){
    FixGameCrash();
    ANOGS = Tools::GetBaseAddress(oxorany("libanogs.so"));
    while (!ANOGS) {
        ANOGS = Tools::GetBaseAddress(oxorany("libanogs.so"));
        sleep(1);
    }
    while (!isLibraryLoaded(oxorany("libanogs.so"))){
        sleep(1);
    }
    LOGI("RIYAZ CORE 4.5 BGMI BYPASS LIBRAY");
    /*
MemoryPatch::createWithHex(ANOGS + oxorany(0x48E800), oxorany(" 51 00 00 58 20 02 1F D6")).Modify(); // HOOK_SIGNATURE
MemoryPatch::createWithHex(ANOGS + oxorany(0x10C350), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x10C364), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x10C378), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x10C38C), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x111CE8), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x124218), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x126FBC), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x13BCD4), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x1426BC), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x144798), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x14A7B0), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x14AA2C), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x14AA3C), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x14AA48), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x14AA54), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x15002C), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x150FD8), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x151140), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x15139C), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x152504), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x152520), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x15253C), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x152558), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x152570), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x152584), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x152598), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x152D9C), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x152DB8), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x15747C), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x157490), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x1574A4), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x1574BC), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x15757C), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x157590), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x1607E4), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x171AF0), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x17B94C), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x184B54), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x184EF0), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x184F88), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x184F9C), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x188B88), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x18A220), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x18A234), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x18A3DC), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x18A3F0), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x1A2EAC), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x1B4F58), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x1B4F60), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x1B4F68), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x1B4F70), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x1BB258), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x1C4490), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x1D0C38), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x1D36C8), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x1D36DC), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x1F3D38), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x1F3D74), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x1F3D94), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x1F4104), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x1F4134), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x1F6B0C), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x1F6B20), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x1F6B34), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x1F6B48), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x1F6B5C), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x1F6B70), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x1F6BAC), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x1F6BCC), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x1F6C74), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x1F9A8C), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x1FB03C), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x1FB05C), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x1FC5DC), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x1FD2E8), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x200D38), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x203250), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x203264), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x205DD8), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x206438), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x206464), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x20662C), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x206644), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x214580), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x21E9B8), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x2239AC), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x2240C8), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x22431C), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x224728), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x23055C), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x231D9C), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x257100), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x257114), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x257138), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x2571C8), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x2571E0), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x2571F4), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x157474), oxorany("20 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x160828), oxorany("20 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
MemoryPatch::createWithHex(ANOGS + oxorany(0x2239B4), oxorany("20 00 80 D2 C0 03 5F D6")).Modify();//@By Nikhil
 */
 
 
   // DobbyHook((void *) DobbySymbolResolver(OBFUSCATE("/apex/com.android.runtime/lib64/bionic/libc.so"), OBFUSCATE("AnoSDKSetUserInfo")), (void *) userinfo, (void **) &ouserinfo);
   // DobbyHook((void *) DobbySymbolResolver(oxorany("/system/lib64/libc.so"), oxorany("memcpy")), (void *) hook_memcpy, (void **) &orig_memcpy);
    
    //Tools::Hook((void*)(ANOGS + oxorany(0x139560)), (void*) hook_memcpy_anogs, nullptr); // Fix 1da
    
    //HOOK_LIB("libanogs.so","0x1EB7BC",hsub_1EB7BC, sub_1EB7BC); // c16  
  //  HOOK_LIB("libanogs.so", "0x1DEDE8", hsub_1DEDE8, osub_1DEDE8);
    /*
    Tools::Hook((void*)(ANOGS + oxorany(0x4A2E7C)), (void*) sub_4A2E7C, nullptr); // pointing call
    Tools::Hook((void*)(ANOGS + oxorany(0x4CAD08)), (void*) sub_4CAD08, nullptr); // Maybe flag fix caller
    Tools::Hook((void*)(ANOGS + oxorany(0x4CABE8)), (void*) sub_4CABE8, nullptr); // Maybe flag fix
    
    
    
    
    MemoryPatch::createWithHex(ANOGS + oxorany(0x2A3FE4), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();
    MemoryPatch::createWithHex(ANOGS + oxorany(0x366FE4), oxorany("00 00 80 D2 C0 03 5F D6")).Modify();
    MemoryPatch::createWithHex(ANOGS + oxorany(0x155718), oxorany("00 00 80 D2 C0 03 5F D6")).Modify(); 
    // Anogs MemPatch
    MemoryPatch::createWithHex(ANOGS + oxorany(0x1DDE64), oxorany("00 00 80 D2 C0 03 5F D6")).Modify(); // case 38
    //MemoryPatch::createWithHex(ANOGS + oxorany(0x3601C4), oxorany("00 00 80 D2 C0 03 5F D6")).Modify(); // 10y caller
    MemoryPatch::createWithHex(ANOGS + oxorany(0x3997C8), oxorany("00 00 80 D2 C0 03 5F D6")).Modify(); // 10y maybe termination
    MemoryPatch::createWithHex(ANOGS + oxorany(0x3D1190), oxorany("00 00 80 D2 C0 03 5F D6")).Modify(); // 10y maybe termination
    
    
    MemoryPatch::createWithHex(ANOGS + oxorany(0x3ECBB4), oxorany("00 00 80 D2 C0 03 5F D6")).Modify(); // 10y maybe termination
    
    MemoryPatch::createWithHex(ANOGS + oxorany(0x49D8D0), oxorany("00 00 80 D2 C0 03 5F D6")).Modify(); // case 38 caller
    MemoryPatch::createWithHex(ANOGS + oxorany(0x4D88F4), oxorany("00 00 80 D2 C0 03 5F D6")).Modify(); // caller of id=℅d
    MemoryPatch::createWithHex(ANOGS + oxorany(0x4DACB0), oxorany("00 00 80 D2 C0 03 5F D6")).Modify(); // Report Ban Fix
    MemoryPatch::createWithHex(ANOGS + oxorany(0x4DACB4), oxorany("1F 20 03 D5")).Modify(); // Report Ban Fix
    
    
    */
    
    
    
    
    
    return NULL;
}

void *ue4_thread(void *) {
    do {
    sleep(1);
    } while (!isLibraryLoaded("libUE4.so"));
  // MemoryPatch::createWithHex(UE4 + oxorany(0x7767798), oxorany("00 00 80 D2 C0 03 5F D6")).Modify(); // 10y maybe termination

   return NULL;
}


__attribute__((constructor)) void mainload() {
    pthread_t ptid;
	pthread_create(&ptid, NULL, ue4_thread, NULL);
    pthread_create(&ptid, NULL, anogs_thread, NULL);
}
