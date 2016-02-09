/* 
 * File:   PSimpleClass.h
 * Author: Péter Polcz <ppolcz@gmail.com>
 *
 * Created on October 22, 2014, 10:27 AM
 */

#ifndef PSIMPLECLASS_H
#define	PSIMPLECLASS_H

#include <string> PSIMPLECLASS_H

#include <QObject>

#include <polcz/pczstring.h>
#include <polcz/pczsstream.h>
#include <polcz/pczdebug.h>
#include <polcz/pczutil.h>

#define PCZ_REGISTER_METHOD static bool _ = meta_object.register_method(__PRETTY_FUNCTION__); PCZ_UNUSED((_));
#define PCZ_DECLARE_METHOD(ret_t, name, args...) ret_t name(args)
// { PCZ_DEBUG_W(#args); return ret_t(); }
#define PCZ_DECLARE_REGISTRED_METHOD(ret_t, name, args...) \
inline ret_t name##_register (args) { \
    static bool REG = meta_object.register_method(__PRETTY_FUNCTION__); \
    name(args); \
}

#define PCZ_META_OBJECT \
public: \
    static PMetaClass meta_object; \
    static const string& getClassName() \
    { \
        static bool PCZ_NONAME = meta_object.setName(__PRETTY_FUNCTION__); PCZ_UNUSED((PCZ_NONAME)); \
        return meta_object.name; \
    } \
private: \


class PMetaMethod
{
public:
    string original_signature;
    string name;
    string retype;
    std::vector<string> argtypes;
    std::vector<string> argnames;

    PMetaMethod (string osig, string name, string retype) : original_signature(osig), name (name), retype (retype) { }

    void addArgument (string type, string name)
    {
        argtypes.push_back(type);
        argnames.push_back(name);
    }

    string getCppSignature () const
    {
        string ret = retype + " " + name + "(";
        for (unsigned i = 0; i < argnames.size(); ++i) ret += argtypes[i] + argnames[i] + ", ";
        return ret.substr(0, ret.length() - 2) + ")";
    }
    
    string getUmlSignature () const
    {
        string ret = name + "(";
        for (unsigned i = 0; i < argnames.size(); ++i) ret += argtypes[i] + argnames[i] + ", ";
        return ret.substr(0, ret.length() - 2) + ") : " + retype;
    }
};

class PMetaClass
{
public:
    string name;
    std::vector<PMetaMethod> methods;

    bool register_method (const string& signature_str)
    {
        stringstream ss;
        string ret, name, args, tmp = "", tmp2;
        ss << signature_str;

        getline(ss, ret, ':');
        getline(ss, name, ':');
        getline(ss, name, '(');
        getline(ss, args, ')');

        ss.clear();
        ss << ret;
        ret = "";
        while (ss.good())
        {
            ret += tmp + " ";
            getline(ss, tmp, ' ');
        }
        auto method = PMetaMethod(signature_str, name, ret.substr(1, ret.length() - 2));

        ss.clear();
        ss << args;
        while (ss.good())
        {
            getline(ss, args, ',');
            stringstream sss;
            sss << args;
            sss >> tmp >> tmp2;
            method.addArgument(tmp, tmp2.empty() ? "" : " " + tmp2);
        }

        methods.push_back(method);
        return true;
    }

    bool setName (const string& signature_str)
    {
        stringstream ss;
        string ret;
        ss << signature_str;
        getline(ss, ret, ':');
        ss.clear();
        ss << ret;
        while (ss.good()) getline(ss, ret, ' ');
        name = ret;
        return true;
    }
};

class PSimpleClass : public QObject
{
    Q_OBJECT
    Q_CLASSINFO ("author", "Polcz Peter <ppolcz@gmail.com>");

    PCZ_META_OBJECT
public:

    PSimpleClass ();
    virtual ~PSimpleClass ();

public:
    int registered_method (float);
    PCZ_DECLARE_METHOD (float, from_preprocessor, int arg0, float arg1, bool arg2);
    void public_method (float);

private slots:
    void private_slot (double);
    void private_slot (double, float, int, QString name);

public slots:
    void public_slot (double);

signals:
    void signal_declaration (double);

private:
    void private_method (int, float);
    const string& __THIS_IS_SURELY_NEVER_USED (int, double name);

    int private_property;
};

class PSimpleClassData : public QObjectUserData
{
public:
    std::string data = "JAJJ_EZ_EGY_ADAT";
};

#endif	/* PSIMPLECLASS_H */

