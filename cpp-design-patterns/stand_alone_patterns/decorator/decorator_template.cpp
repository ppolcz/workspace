#include <iostream>
// #include <type_traits> 

/**
 * using templates there is no need of virtual tables
 */

template <typename _Type>
class AbstractDecorator 
{
public:
    AbstractDecorator(_Type& decorandee)
    {
        _decorandee = &decorandee;
    }

protected:
    _Type * _decorandee;
};

template <typename _Type>
class BoldDecorator : public AbstractDecorator<_Type>
{
public:
    BoldDecorator(_Type& decorandee) : AbstractDecorator<_Type>(decorandee) { }

    void func ()
    {
        std::cout << "<b>";
        this->_decorandee->func();
        std::cout << "</b>";
    }
};

template <typename _Type>
class BarEndlineDecorator : public AbstractDecorator<_Type>
{
public:
    BarEndlineDecorator(_Type& decorandee) : AbstractDecorator<_Type>(decorandee) { }

    void func()
    {
        std::cout << "|";
        this->_decorandee->func();
        std::cout << "|";
        std::cout << std::endl;
    }
};

class Terminal 
{
public:
    void func()
    {
        std::cout << "FUNC";
    }
};

template <template<typename> class _Decorator, typename _Decorandee>
_Decorator<_Decorandee> decor(_Decorandee& decorandee)
{
    return _Decorator<_Decorandee>(decorandee);
}

int main (int argc, char** argv)
{
    Terminal t;
    t.func();

    auto bd = decor<BoldDecorator>(t); // smart way
    bd.func();

    BoldDecorator<Terminal>(t).func(); // ugly way

    decor<BarEndlineDecorator>(bd).func(); // smart way

    BarEndlineDecorator< BoldDecorator<Terminal> >(bd).func(); // ugly way
}