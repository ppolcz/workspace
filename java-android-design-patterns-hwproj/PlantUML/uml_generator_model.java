@startuml

class Helper {
+ {static} ModifierToPlantUML(int modifier)
}

class UMLGenerator <<Builder.Director>>{
- builder : ElementBuilder
.. objects to process on (intput) ..
- requestedPackages : Set<Package>
- requestedClasses : Map<Class, Integer>
--
+ addRequestedClass(Class)
+ addRequestedPackage(Package)
+ generateClassDiagram() -- ALGORITHM (generate)
+ print() -- ALGORITHM (visualize)
}

abstract class ElementBuilder <<Builder.>> {
- umlmodel : Element -- COMPOSITE MODEL
+ {abstract} createClass() : Class
+ {abstract} createPackage() : Package
+ {abstract} createLink() : Link
}
note top: This is very similar to an AbstractFactory
ElementBuilder .l.> Element

class PlantUMLElementBuilder <<Builder.ConcBuilder>> extends ElementBuilder {
+ createClass() : PuClass
+ createPackage() : PuPackage
+ createLink() : PuLink
}

'UMLGenerator contains an ElementBuilder'
ElementBuilder --o UMLGenerator
'PlantUMLElementBuilder instantiates Elements'
PlantUMLElementBuilder ..> Element

interface Element << Composite.Component >> {
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

class Composite <<Composite.>> {
+ print()
}

class Leaf <<Composite.Leaf>> {
    + addElement() {}
    + {abstract} print()
}
Leaf <|.. Element

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