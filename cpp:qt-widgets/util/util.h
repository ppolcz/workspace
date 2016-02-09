/* 
 * File:   util.h
 * Author: deva
 *
 * Created on February 12, 2014, 3:30 PM
 * 
 * --
 * EZ IGY MOST JO AHOGY VAN
 * --
 * 
 */

#ifndef UTIL_H
#define	UTIL_H

#include "values.h"
#include "debug.h"

#include <memory>
#define ADD_SHARED_PTR(classname) typedef std::shared_ptr<classname> Ptr;

#include "qt_includes.h"
namespace util {
    template <typename _QSpinBox, typename _Type >
    _QSpinBox * setupSpinBox (_QSpinBox * sb, _Type min, _Type step, _Type max, _Type value = 0);
}
#define SET_OBJ_NAME(obj) obj->setObjectName(QString::fromUtf8( #obj ))

#include <functional>
typedef std::function<void(const QString &, int) > fun_ChangeStatusCallback;

#define ADD_CHANGE_STATUS_CALLBACK \
public: \
    void callChangeStatus (const fun_ChangeStatusCallback & callback) { \
        cb_changeStatus = callback; \
    } \
protected: \
    fun_ChangeStatusCallback cb_changeStatus = [](const QString & msg, int) { \
        std::cout << msg.toStdString() << std::endl; \
    }; 


#endif	/* UTIL_H */

