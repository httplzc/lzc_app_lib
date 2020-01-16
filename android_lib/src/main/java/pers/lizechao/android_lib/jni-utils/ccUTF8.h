#include <vector>
#include <string>
#include <sstream>
#include <jni.h>

namespace JniUtils {
    namespace StringUtils {

        namespace UnicodeCharacters {
            const char32_t NewLine = 0x000A; // 10
            const char32_t CarriageReturn = 0x000D; // 13
            const char32_t NextCharNoChangeX = 0x0008; // 8
            const char32_t Space = 0x0020; // 32
            const char32_t NoBreakSpace = 0x00A0; // 160
        }

        namespace AsciiCharacters {
            const char NewLine = '\n';
            const char CarriageReturn = '\r';
            const char NextCharNoChangeX = '\b';
            const char Space = ' ';
        }

        template<typename T>
        std::string toString(T arg) {
            std::stringstream ss;
            ss << arg;
            return ss.str();
        }

        std::string format(const char *format, ...);

/**
 *  @brief Converts from UTF8 string to UTF16 string.
 *
 *  This function resizes \p outUtf16 to required size and
 *  fill its contents with result UTF16 string if conversion success.
 *  If conversion fails it guarantees not to change \p outUtf16.
 *
 *  @param inUtf8 The source UTF8 string to be converted from.
 *  @param outUtf16 The output string to hold the result UTF16s.
 *  @return True if succeed, otherwise false.
 *  @note Please check the return value before using \p outUtf16
 *  e.g.
 *  @code
 *    std::u16string utf16;
 *    bool ret = StringUtils::UTF8ToUTF16("你好hello", utf16);
 *    if (ret) {
 *        do_some_thing_with_utf16(utf16);
 *    }
 *  @endcode
 */
        bool UTF8ToUTF16(const std::string &inUtf8, std::u16string &outUtf16);

/**
 *  @brief Same as \a UTF8ToUTF16 but converts form UTF8 to UTF32.
 *
 *  @see UTF8ToUTF16
 */
        bool UTF8ToUTF32(const std::string &inUtf8, std::u32string &outUtf32);

/**
 *  @brief Same as \a UTF8ToUTF16 but converts form UTF16 to UTF8.
 *
 *  @see UTF8ToUTF16
 */
        bool UTF16ToUTF8(const std::u16string &inUtf16, std::string &outUtf8);

/**
 *  @brief Same as \a UTF8ToUTF16 but converts form UTF16 to UTF32.
 *
 *  @see UTF8ToUTF16
 */
        bool UTF16ToUTF32(const std::u16string &inUtf16, std::u32string &outUtf32);

/**
 *  @brief Same as \a UTF8ToUTF16 but converts form UTF32 to UTF8.
 *
 *  @see UTF8ToUTF16
 */
        bool UTF32ToUTF8(const std::u32string &inUtf32, std::string &outUtf8);

/**
 *  @brief Same as \a UTF8ToUTF16 but converts form UTF32 to UTF16.
 *
 *  @see UTF8ToUTF16
 */
        bool UTF32ToUTF16(const std::u32string &inUtf32, std::u16string &outUtf16);


/**
*  @brief convert jstring to utf8 std::string,  same function with env->getStringUTFChars. 
*         because getStringUTFChars can not pass special emoticon
*  @param env   The JNI Env
*  @param srcjStr The jstring which want to convert
*  @param ret   True if the conversion succeeds and the ret pointer isn't null
*  @returns the result of utf8 string
*/
        std::string getStringUTFCharsJNI(JNIEnv *env, jstring srcjStr, bool *ret = nullptr);

/**
*  @brief create a jstring with utf8 std::string, same function with env->newStringUTF
*         because newStringUTF can not convert special emoticon
*  @param env   The JNI Env
*  @param srcjStr The std::string which want to convert
*  @param ret     True if the conversion succeeds and the ret pointer isn't null
*  @returns the result of jstring,the jstring need to DeleteLocalRef(jstring);
*/
        jstring newStringUTFJNI(JNIEnv *env, const std::string &utf8Str, bool *ret = nullptr);

    }
}


