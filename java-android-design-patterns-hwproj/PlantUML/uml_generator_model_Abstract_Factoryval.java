@startuml
note "kellene meg valahova egy builder is"

class Helper {
+ {static} ModifierToPlantUML(int modifier)
}

abstract class UMLGenerator << AbstractFactory >> {
- Element[] elements
- Set<Package> requestedPackages
- Map<Class, Integer> requestedClasses
+ addRequestedClass(Class)
+ addRequestedPackage(Package)
+ {abstract} createClass() : Class
+ {abstract} createPackage() : Package
+ {abstract} createLink() : Link
+ generateClassDiagram();
+ print()
}

class PlantUMLClassDiagramGenerator extends UMLGenerator {
+ createClass() : PuClass
+ createPackage() : PuPackage
+ createLink() : PuLink
}

interface Element << Component >> {
+ addElement(Element)
+ print()
}

class java.lang.Package {
+ getName() : String
}

class java.lang.Class {
+ isInterface() : boolean
+ isEnum() : boolean
+ getSuperClass() : Class
}

class Composite {
+ print()
}

class Package extends Composite {
    + {abstract} createClassDeclaration() : ClassDeclaration
}

class Class extends Composite {
    + {abstract} createMethod() : Method
    + {abstract} createField() : Field
}

class PuPackage {
    + createClassDeclaration() : PuClassDeclaration
}

class PuClass {
    + createMethod() : PuMethod
    + createField() : PuField
}

class ClassDeclaration << Leaf_1 >> {
+ print()
}

class Method {
+ parseGenericParamTypes(Type[] types)
}

class Link << Leaf_4 >> {
+ print()
+ setA(Element el, String label)
+ setB(Element el, String label)
+ setLinkType(ELinkType type)
}

Element <|.. Composite
Composite "1" o-- "n" Element

java.lang.Package --o Package
java.lang.Class --o Class

Element <|.. ClassDeclaration
java.lang.Class <|-- ClassDeclaration

java.lang.Field <|-- Field << Leaf_2 >>
Element <|.. Field

java.lang.Method <|-- Method << Leaf_3 >>
Element <|.. Method

Element <|.. Link

Package <|-- PuPackage << ProductB_1 >>
PuClass << ProductA_2 >> -u-|> Class
ClassDeclaration <|-- PuClassDeclaration << ProductA_3 >>
Field <|-- PuField << ProductA_4 >>
Method <|-- PuMethod << ProductA_5 >>
Link <|-- PuLink << ProductA_6 >>

UMLGenerator "1" o-- "n" Element
@enduml