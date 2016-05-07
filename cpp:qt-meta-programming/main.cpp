/*
 * File:   main.cpp
 * Author: Péter Polcz <ppolcz@gmail.com>
 *
 * Created on October 22, 2014, 10:26 AM
 */

#include <type_traits>

#include <QObject>
#include <QCoreApplication>
#include <QMetaObject>
#include <QMetaMethod>
#include <QMetaClassInfo>
#include <QThread>

#include <polcz/pczdebug.h>
#include <qt5/QtCore/qmetaobject.h>
#include <boost/mpl/at.hpp>

#include "PSimpleClass.h"

template <typename _Function>
void f_info(_Function f)
{
    PCZ_DEBUG(typeid (f).name());
}

void main_reflector_demo();

int main(int argc, char *argv[])
{
    // intresting
    int i = 0;
    __typeof__(&i) j = new int[12];
    delete [] j;

    QCoreApplication app(argc, argv);

    f_info(f_info<int>);
    PCZ_DEBUG(QMetaMethod::fromSignal(&PSimpleClass::signal_declaration).name());

    PSimpleClass object;
    object.setObjectName("my_object");
    object.setUserData(1, (QObjectUserData*) (new PSimpleClassData()));
    object.from_preprocessor(0, 12, 12);
    object.registered_method(12);
    PCZ_DEBUG(object.staticMetaObject.className());
    PCZ_DEBUG_W(object.staticMetaObject.constructorCount());
    PCZ_DEBUG_W(object.staticMetaObject.enumeratorCount());
    PCZ_DEBUG_W(object.staticMetaObject.methodOffset());
    PCZ_DEBUG_W(object.staticMetaObject.methodCount());
    PCZ_DEBUG_W(object.staticMetaObject.classInfoCount());
    PCZ_DEBUG_W(object.staticMetaObject.propertyCount());
    PCZ_DEBUG_W(object.staticMetaObject.propertyOffset());
    PCZ_DEBUG_W(object.staticMetaObject.superClass()->className());
    PCZ_DEBUG_W(object.staticMetaObject.d.superdata->className());
    PCZ_DEBUG_W(object.staticMetaObject.d.static_metacall); // ennek segitsegevel mukodik az invokeMethod?
    PCZ_DEBUG_W((char*) object.staticMetaObject.d.stringdata[0].data());
    PCZ_DEBUG_W((char*) object.staticMetaObject.d.stringdata[1].data());
    PCZ_DEBUG_W((char*) object.staticMetaObject.d.stringdata[2].data());
    PCZ_DEBUG_W((char*) object.staticMetaObject.d.stringdata[3].data());
    PCZ_DEBUG_W((char*) object.staticMetaObject.d.stringdata[4].data()); // ezek szerepelnek a moc_ fileban

    PCZ_DEBUG_W(object.dynamicPropertyNames().size());
    PCZ_DEBUG_W(object.dynamicPropertyNames().count());
    PCZ_DEBUG_W(((PSimpleClassData*) object.userData(1))->data);
    //    object.dumpObjectInfo();
    //    object.dumpObjectTree();
    PCZ_DEBUG_W(object.metaObject()->methodOffset());
    PCZ_DEBUG_W(object.metaObject()->methodCount());
    PCZ_DEBUG_W(object.thread()->objectName().toStdString());
    PCZ_DEBUG_W(object.objectName().toStdString());

    for (int i = object.staticMetaObject.classInfoOffset(); i < object.staticMetaObject.classInfoCount(); ++i)
    {
        PCZ_DEBUG(object.staticMetaObject.classInfo(i).name());
        PCZ_DEBUG(object.staticMetaObject.classInfo(i).value());
    }

    for (int i = object.staticMetaObject.methodOffset(); i < object.staticMetaObject.methodCount(); ++i)
    {
        PCZ_DEBUG(object.staticMetaObject.method(i).name());
    }

    for (int i = 0; i < object.staticMetaObject.methodCount(); ++i)
    {
        auto m = object.staticMetaObject.method(i);
        PCZ_DEBUG(m.name());
        PCZ_DEBUG_W(m.attributes());
        PCZ_DEBUG_W(m.methodSignature().constData());
        PCZ_DEBUG_W(m.typeName());
    }

    auto& methods = object.meta_object.methods;
    for (auto& m : methods)
    {
        //        PCZ_DEBUG("%s %s()", m.retype, m.name);
        PCZ_DEBUG("%s -- %s -- %s", m.original_signature, m.getCppSignature(), m.getUmlSignature());
    }

    PCZ_DEBUG(PSimpleClass::getClassName());

    main_reflector_demo();

    QThread::sleep(1);
    return 0;
}

#include "PMyClass.h"

void main_reflector_demo()
{
    PMyClass myclass;
    auto fpointer = PMyClass::template method_data<-3, PMyClass>::fpointer;
    PCZ_DEBUG("return = %d", (myclass.*fpointer)(4, 2));
    PCZ_DEBUG((PMyClass::template method_data<-3, PMyClass>::data(0)));

    auto function = reflector::method<-3>(myclass);
    PCZ_DEBUG("reflector::method...call = %d", function.call(2, 4));
    PCZ_DEBUG("reflector::method...call = %s", function.data(1));
    PCZ_DEBUG("reflector::method...call = %s", function.name);

    auto function2 = reflector::method<1>(myclass);
    PCZ_DEBUG("reflector::method...call = %d", function2.call(2, "asdas"));
    PCZ_DEBUG("reflector::name = %s", function2.data(1));
    PCZ_DEBUG("reflector::arg_nr = %d", function2.arg_nr);
    PCZ_DEBUG("reflector::arg_0 = %d", function2.data(2));
    PCZ_DEBUG("reflector::signature = %s", function2.get_signature());
    PCZ_DEBUG("reflector::signature = %s", function2.get_uml_signature());
    
    auto field = reflector::field<0>(myclass);
    PCZ_DEBUG("fields: %s = %d", field.name(), field.get());
    auto field2 = reflector::field<1>(myclass);
    PCZ_DEBUG("fields: %s = %d", field2.name(), field2.get());
}
