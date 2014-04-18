#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#ifndef LIBBUN_LIB_H
#define LIBBUN_LIB_H

#define S(TEXT) (TEXT)
#define S_TOSTRING "%s"
#define S_LF       "\n"
#define S_TRUE     "true"
#define S_FALSE    "false"

static long libbun_s2i(char *x)
{
    char *end = x + strlen(x);
    return strtol(x, &end, 10);
}

static float libbun_s2f(char *x)
{
    char *end = x + strlen(x);
    return strtof(x, &end);
}

static char *libbun_f2s(float x)
{
    char buf[128];
    snprintf(buf, 128, "%f", x);
    return strndup(buf, strlen(buf));
}

static char *libbun_i2s(long x)
{
    char buf[128];
    snprintf(buf, 128, "%ld", x);
    return strndup(buf, strlen(buf));
}

static char *libbun_concat(char *left, char *right)
{
    int leftLen  = strlen(left);
    if (leftLen == 0) {
        return right;
    }
    int rightLen = strlen(right);
    if (rightLen == 0) {
        return right;
    }

    char *newstr = (char *) malloc(leftLen + rightLen + 1);
    memcpy(newstr, left, leftLen);
    memcpy(newstr + leftLen, right, rightLen);
    newstr[leftLen + rightLen] = 0;
    return newstr;
}

static void *LibZen_Malloc(size_t size)
{
    return malloc(size);
}
#endif /* end of include guard */
