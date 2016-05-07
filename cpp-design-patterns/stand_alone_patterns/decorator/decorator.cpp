#include <iostream>

class Type 
{
public:
    virtual void func ()
    {
        std::cout << "FUNC";
    }

    virtual ~Type () { }
};

class AbstractDecorator : public Type 
{
public:
    AbstractDecorator(Type& decorandee)
    {
        _decorandee = &decorandee;
    }

protected:
    Type * _decorandee;
};

class BoldDecorator : public AbstractDecorator 
{
public:
    BoldDecorator(Type& decorandee) : AbstractDecorator(decorandee) { }

    void func ()
    {
        std::cout << "<b>";
        _decorandee->func();
        std::cout << "</b>";
    }
};

class BarEndlineDecorator : public AbstractDecorator
{
public:
    BarEndlineDecorator(Type& decorandee) : AbstractDecorator(decorandee) { }

    void func()
    {
        std::cout << "|";
        _decorandee->func();
        std::cout << "|";
        std::cout << std::endl;
    }
};

class Terminal : public Type 
{
public:
};

int main (int argc, char** argv)
{
    Terminal t;
    t.func();
    BoldDecorator bd(t);
    bd.func();
    BarEndlineDecorator(bd).func();
}