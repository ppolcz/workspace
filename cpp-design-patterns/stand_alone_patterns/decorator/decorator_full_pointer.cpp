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
    AbstractDecorator(Type* decorandee)
    {
        _decorandee = decorandee;
    }

    virtual ~AbstractDecorator () { }

protected:
    Type * _decorandee;
};

class BoldDecorator : public AbstractDecorator 
{
public:
    BoldDecorator(Type* decorandee) : AbstractDecorator(decorandee) { }

    virtual ~BoldDecorator () { }

    virtual void func ()
    {
        std::cout << "<b>";
        this->_decorandee->func();
        std::cout << "</b>";
    }
};

class EndlineDecorator : public AbstractDecorator
{
public:
    EndlineDecorator(Type* decorandee) : AbstractDecorator(decorandee) { }

    virtual ~EndlineDecorator () { }

    virtual void func()
    {
        std::cout << "|";
        this->_decorandee->func();
        std::cout << "|";
        std::cout << std::endl;
    }
};

class Terminal : public Type 
{
public:
    virtual ~Terminal () { }

};

int main (int argc, char** argv)
{
    Terminal* t = new Terminal();
    t->func();
    BoldDecorator* bd = new BoldDecorator(t);
    bd->func();
    EndlineDecorator* ed = new EndlineDecorator(bd);
    ed->func();
    // EndlineDecorator(bd).func();

    delete t;
    delete bd;
    delete ed;
}