@startuml

''a,b,c,d,e,f,g,h,i,j,k,l
''a,c,d,e,f,g,h,i,j,k,l
''a,d,e,f,g,h,i,j,k,l
''a,e,f,g,h,i,j,k,l
''a,f,g,h,i,j,k,l
''a,g,h,i,j,k,l
''a,h,i,j,k,l
''a,i,j,k,l
''a,j,k,l
''a,k,l
''a,l
''a

!define END <b>end</b>
!define THROW <b>throw</b>
!define IF <b>if</b>
!define AND <b>and</b>
!define OR <b>or</b>
!define FOR <b>for</b>
!define RETURN <b>return</b>
!define FUNCTION <b>function</b>
!define TRUE <b>true</b>
!define FALSE <b>false</b>
!define COMMENT(comment) <i><color:Gray>comment</color></i>
!define COMMENT(a,b) <i><color:Gray>a, b</color></i>
!define COMMENT(a,b,c) <i><color:Gray>a, b, c</color></i>
!define COMMENT(a,b,c,d) <i><color:Gray>a, b, c, d</color></i>
!define COMMENT(a,b,c,d,e) <i><color:Gray>a, b, c, d, e</color></i>
!define COMMENT(a,b,c,d,e,f) <i><color:Gray>a, b, c, d, e, f</color></i>

!define Item(a,b,c,d,e,f,g,h,i,j,k,l) - <i>a,b,c,d,e,f,g,h,i,j,k,l</i>
!define Item(a,c,d,e,f,g,h,i,j,k,l) - <i>a,c,d,e,f,g,h,i,j,k,l</i>
!define Item(a,d,e,f,g,h,i,j,k,l) - <i>a,d,e,f,g,h,i,j,k,l</i>
!define Item(a,e,f,g,h,i,j,k,l) - <i>a,e,f,g,h,i,j,k,l</i>
!define Item(a,f,g,h,i,j,k,l) - <i>a,f,g,h,i,j,k,l</i>
!define Item(a,g,h,i,j,k,l) - <i>a,g,h,i,j,k,l</i>
!define Item(a,h,i,j,k,l) - <i>a,h,i,j,k,l</i>
!define Item(a,i,j,k,l) - <i>a,i,j,k,l</i>
!define Item(a,j,k,l) - <i>a,j,k,l</i>
!define Item(a,k,l) - <i>a,k,l</i>
!define Item(a,l) - <i>a,l</i>
!define Item(a) - <i>a</i>

!define Title(a,b,c,d,e,f,g,h,i,j,k,l) <b><u>a,b,c,d,e,f,g,h,i,j,k,l</u></b>
!define Title(a,c,d,e,f,g,h,i,j,k,l) <b><u>a,c,d,e,f,g,h,i,j,k,l</u></b>
!define Title(a,d,e,f,g,h,i,j,k,l) <b><u>a,d,e,f,g,h,i,j,k,l</u></b>
!define Title(a,e,f,g,h,i,j,k,l) <b><u>a,e,f,g,h,i,j,k,l</u></b>
!define Title(a,f,g,h,i,j,k,l) <b><u>a,f,g,h,i,j,k,l</u></b>
!define Title(a,g,h,i,j,k,l) <b><u>a,g,h,i,j,k,l</u></b>
!define Title(a,h,i,j,k,l) <b><u>a,h,i,j,k,l</u></b>
!define Title(a,i,j,k,l) <b><u>a,i,j,k,l</u></b>
!define Title(a,j,k,l) <b><u>a,j,k,l</u></b>
!define Title(a,k,l) <b><u>a,k,l</u></b>
!define Title(a,l) <b><u>a,l</u></b>
!define Title(a) <b><u>a</u></b>

''left to right direction
''top to bottom direction
hide circle
hide methods
hide attributes

class Singleton
class Composite <<tree hierarchy>>
class Builder <<creation>>
class AbstractFactory <<product object families>>

class Command
class Memento

class Observer
class Facade
class Mediator

class State
class FlyWeight

AbstractFactory "method" *-- FactoryMethod
AbstractFactory --|> Singleton : ConFactory\ncould be >

Builder --|> AbstractFactory : similiar to >
Builder ..> Composite  : creates >
Builder --|> Singleton : could be >

Command --|> Composite : macro\ncommands >
Command *-- Memento : restore\ninfo

Memento --> Iterator

Mediator --> Observer : communication\nusing >
Mediator -- Facade : similar >

Observer --> Singleton

State -- FlyWeight
State --|> Singleton

'== Composite ==============================='
note bottom of Composite
Title(Intent)
Compose objects into tree structures to represent part-whole hierarchies.
Composite lets clients treat individual objects and compositions of objects
uniformly.
Title(Consequences)
 1. Makes the client simple
 2. Makes it easier to add new kinds of components
 3. Can make your desing overly general
 4. Defines an object hierarchy (tree)
{{
    hide attributes
    class Component {
        Operation()
        Add() {}
    }

    class Leaf1 extends Component {
        Operation()
    }

    class Leaf2 extends Component {
        Operation()
    }

    class Composite extends Component {
        Operation()
        Add()
    }

    Component "1" --* "n" Composite
}}
end note

'== Builder ================================='
note top of Builder
Title(Intent)
Separatethe samethe construction of a complex object fromconstruction 
process can create differentits representation so that representations.
Title(Consequences)
 1. It lets you vary a product's internal representation
 2. It isolates code for construction and representation
 3. It gives you finer control over the construction process
Title(Implementation)
 1. Empty methods as default in Builder
end note

'== AbstractFactory ========================='
note top of AbstractFactory
Title(Intent)
Provide an interface for creating families of 
related or dependent objects without
specifying their concrete classes.
Title(Consequences)
 1. It isolates clients from implementation classes
 2. It makes exchanging product families easy
 3. It promotes consistency among products
 4. Supporting new kinds of products is difficult
end note

'== Singleton ==============================='
note bottom of Singleton
Title(Consequences)
 1. Controlled access to sole instance
 2. Reduced name space
 3. Permits refinement of operations and representation
     Item(enables subclassing)
 4. Permits a variable number of instances
     Item(and also control over the number of them)
 5. More flexible than class operations
Title(Known Uses)
 1. Metaclass - one instance only
 2. Database handler - to preserve consistency
end note

Proxy -- Adapter
Proxy -- Decorator
Proxy -- Iterator

'== Proxy ===================================='
note top of Proxy
Title(Intent)
Provide a surrogate or placeholder for another 
object to control access to it.
Title(Also Known As) 
 Item(surrogate, placeholder)
Title(Motivation)
 Item(create expansive objects only on demand)
 Item(use a proxy instead of the real object)
Title(Applicability)
 1. remote proxy - hide the fact that is in another namespace
 2. virtual proxy - creation on demand
 3. protection proxy - prevents unwanted access
 4. smart pointer (reference)
Title(Consequences)
 1. (-) introduces a level of indirection
 2. <b>copy-on-write</b>
<img:_model_proxy_small.jpg>
''{{
''    A -- B
''    scale 0.2
''}}
Title(Remarks)
 1. proxy provides the same interface as the real subject)
 2. proxy decorates the real subject's methods in a way that 
 the additionally needed operations [protection, creation, 
 release memory] are made by the proxy. 
 (<b>housekeeping tasks</b> when an object is accessed)
 3. in C++ we can overload the -> operator (smart pointer)
Title(CONTROLES ACCESS TO AN OBJECT)
end note

note bottom of Adapter
 Provides a different interface to 
 an object it adopts in order to be 
 able to work with other objects.
end note

note bottom of Decorator
 Decorators add some new 
 responsabilities to an object.
end note

'== Command ================================='

note top of Command
Title(Intent)
 Item(encapsulate a request to an object)
 Item(queue or log requests)
 Item(handling requests without knowing them exactly)
Title(Also Known As) - <i>Action, Transaction</i>
Title(Applicability)
 1. support undo, logging
 2. requests with parameters
 3. queue requests and run on background thread
Title(Consequences)
 1. decouples the invoker from the performer
 2. commands can be extanded
 3. can assamble commands to a composite command
 4. easy to add new commands
<img:_model_command.jpg>
Title(Remarks)
SimpleCommand : Command 
    typedef void (Receiver::* Action)();
    --
    Action a;
    Receiver* r;
    --
    SimpleCommand(Receiver* r, Action a) : r(r), a(a) { }
    virtual void Execute() { <b>(r->*a)()</b>; }
END
command = new Command(receiver, &MyClass::Action);
command.Execute()
Item(ez veszesen hasonlit a Qt slotok megadasahoz)
Item(mi a helyzet a signalokkal?)
end note

'== Memento =============================='
note bottom of Memento
~= Token
Title(Intent)
Without violating encapsulation, capture and externalize an 
object's internal state so that the object can be restored 
to this state later.
<img:_model_memento.jpg>
 - Memento: 
   - stores internal state of Originator
   - protect against access by others than the Originator
   (it has actually two interfaces). Ideally only the Originator 
   would be permitted to access the memento's internal state
 - Originator: creates the memento (snapshot of its state)
   - uses memento to restore its previous internal state
 - Caretaker: just stores and passes the mementos to Originator
<img:_model_memento_sd.jpg>
Title(Consequences)
1. Preserving encapsulation bounds
2. It simplifies Originator
3. (-) Using mementos might be expansive
4. (-) Two interfaces: C++ no problem
end note

note top of Mediator
Title(Intent)
 1. Encapsulate interractions between sevaral objects 
into a single object.
 2. Make the object the most reusable and keep project 
specific interractions be defined in the Mediator.
Title(Applicability)
 1. well defined but difficult communication
Title(Consequences)
 1. it limits subclassing
 2. it decouples colleagues
 3. it simplifies object protocols
 4. it abstracts how objects cooperate
 5. centralized control (this could be +/-)
 6. Mediator could be also complex
Title(Remarks)
 1. Qt singal-slots are connected using Mediator and
communication is implemented using Observer.
end note

note bottom of Observer
~= Listener, Dependents, Publish-Subscribe
Title(Intent)
  Define 1-n dependency between objects so that when one object
  changes its state, all its dependents are notified and updated
  automatically.
Title(Consequences)
 1. abstract decoupling between Subject and Observer
 2. support for broadcast communication
 3. (-) unexpected updates //(Qt: tul sok signal ==> lassunak tunt,//
//pedig a lenyeg gyorsan lefutott)// ==> ChangeManager. Az Observer 
semmit nem tud arrol, hogy a Subject mikor fog frissiteni.
{{
    class Subject {
        - s : State
        - l : Listener
        + addListener(Listener)
        + operation() { s++; l.notify(); }
    }

    class Listener {
        + notify();
    }

    Subject -r-> Listener 
    Subject o-- Listener

    legend left
    ""**main**() {""
    ""    Subject s = new Subject()""
    ""    s.addListener(new Listener() {""
    ""        void notify() {}""
    ""    })""
    ""}""
    endlegend
}}
end note

note top of State
Title(Intent)
Allow an object to alter its behavior when its internal 
state changes.The object will appear to change its class.
Title(Consequences)
 1. replace if/else statement with object assigned to each
state
 2. it makes state transition explicit
 3. state objects can be shared
 4. ha nagyon sok allapot van (pl. 10), akkor lehet egyszerubb
lenne irni egy switch-case-t mint mit tiz osztalyt konstruktorokkal
metodusokkal stb. (Javaban az Enum egyszerusit)
end note

note bottom of FactoryMethod
Title(Intent)
Define an interface for creating an object, 
but let subclasses decide which class to 
instantiate. Factory Method lets a class 
defer instantiation to subclasses.
end note

@enduml