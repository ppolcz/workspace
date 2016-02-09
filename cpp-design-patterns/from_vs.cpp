#include <iostream>

#define DEBUG_INFO std::string(" - in file: ") + std::string(__FILE__) + std::string(":") + std::to_string(__LINE__) + std::string(" - ") + std::string(__FUNCTION__) + std::string("()")
#define DEBUG std::cout << "In file: " << __FILE__ << ":" << __LINE__ << " - " << __FUNCTION__ << "()\n";
#define DEBUG_ std::cout << "In file: " << __FILE__ << ":" << __LINE__ << " - " << __FUNCTION__ << "  --  "
#define WATCH(watchee) std::cout << "at line: " << __LINE__ << "  " << #watchee << " = " << watchee << std::endl
#define PCZ_DEBUG std::cout << "at line: " << __LINE__  << std::endl; 

class Animal {
public:

    virtual void eat () {
        PCZ_DEBUG
    }

    virtual void problem () {
        PCZ_DEBUG
    }
};

// Two classes virtually inheriting Animal:

class Mammal : public virtual Animal {
public:

    virtual void breathe () {
        PCZ_DEBUG
    }

    virtual void problem () {
        PCZ_DEBUG
    }
};

class WingedAnimal : public virtual Animal {
public:

    virtual void flap () {
        PCZ_DEBUG
    }
    //virtual void problem() {
    //	PCZ_DEBUG
    //}
};

// A bat is still a winged mammal

class Bat : virtual public Mammal, virtual public WingedAnimal {
public:
    //virtual void problem() {
    //	WingedAnimal::problem();
    //}
};

// ------------------------------------------------------------- //

struct U16B {
    long long a, b;
};

struct U32B {
    long long a, b, c, d;
};

struct U64B {
    U32B a, b;
};

struct U128B {
    U64B a, b;
};

// ------------------------------------------------------------- //

class Abstract {
public:
    // U16B a;

    virtual void h () { }
};

class Base1 : virtual public Abstract {
public:
    // U32B b;

    virtual void h () { }
};

class Base2 : public Abstract {
public:
    U64B c;

    virtual void g () { }
};

class Child : public Base1, public Base2 {
public:
    U128B d;

    virtual void g () { }
};

// ------------------------------------------------------------- //

int vs_main (int argc, char * argv[]) {
    { // this is a solution for multiple inheritance
        Animal * animal = new Bat();
        Animal * animalwidget = new WingedAnimal();
        Animal * animalmammal = new Mammal();
        Mammal * mammal = new Bat();
        WingedAnimal * winget = new Bat();
        Bat * bat = new Bat();

        bat->breathe();
        bat->eat();
        bat->flap();
        bat->problem();
        animal->problem();
        mammal->problem();
        winget->problem();
        animalwidget->problem();
        animalmammal->problem();
    }
    {
        WATCH(sizeof (U16B));
        WATCH(sizeof (U32B));
        WATCH(sizeof (U64B));
        WATCH(sizeof (U128B));
    }
    std::cout << "-----------------------------------------\n";
    {
        Abstract * a = new Abstract();
        WATCH(a);
        // WATCH(&(a->a));
        WATCH(&Abstract::h);
    }
    std::cout << "-----------------------------------------\n";
    {
        WATCH(sizeof (Abstract));
        WATCH(sizeof (Base1));
        WATCH(sizeof (Base2));
        WATCH(sizeof (Child));
    }
    std::cout << "-----------------------------------------\n";
    {
        Child * child = new Child();
        WATCH((Child *) child);
        WATCH((Base1 *) child);
        WATCH((Base2 *) child);
        delete child;
    }
    std::cout << "-----------------------------------------\n";
    {
        std::cout << "Base2 * child = new Base2();\n";
        Base2 * child = new Base2();
        WATCH((Child *) child);
        WATCH((Base1 *) child);
        WATCH((Base2 *) child);
        std::cout << "delete : " << child << std::endl;
        delete child;
    }
    std::cout << "-----------------------------------------\n";
    {
        std::cout << "Base2 * child = new Child();\n";
        Base2 * child = new Child();
        WATCH((Child *) child);
        WATCH((Base1 *) child);
        WATCH((Base2 *) child);
        std::cout << "delete : " << (Child *) child << std::endl;
        delete (Child *) child;
    }
    std::cout << "-----------------------------------------\n";
    {
        Base1 * child = new Child();
        WATCH((Child *) child);
        WATCH((Base1 *) child);
        WATCH((Base2 *) child);
        delete (Child *) child;
    }
    std::cout << "-----------------------------------------\n";
    {
        Abstract * base = new Abstract();
        // WATCH((Base1 *) base);
        WATCH((Abstract *) base);
    }
    std::cout << "-----------------------------------------\n";
    {
        int * a = new int [10];
        ++a;
        delete [] (a - 1);
    }


    int valami;
#line 12 "valami.cpp"
    std::cout << __FILE__ << ":" << __LINE__ << std::endl;

    std::cin >> valami;
    return 0;
}
