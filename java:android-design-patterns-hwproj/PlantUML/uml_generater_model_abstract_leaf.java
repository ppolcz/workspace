@startuml

'LightLightGreen'
!define _BUILDER #D0FFE0 

'LightLightBlue'
!define _COMPOSITE #D0D0FF 

class Helper {
+ {static} ModifierToPlantUML(int modifier)
}

class UMLGenerator <<Director>> _BUILDER {
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
UMLGenerator o-- ElementBuilder  

abstract class ElementBuilder <<Builder>> _BUILDER {
- umlmodel : Element -- COMPOSITE MODEL
+ {abstract} createClass() : Class
+ {abstract} createPackage() : Package
+ {abstract} createLink() : Link
}
note top: This is very similar to an AbstractFactory
ElementBuilder "1" o-- "n" Element

class PlantUMLElementBuilder <<ConcBuilder>> _BUILDER extends ElementBuilder {
+ createClass() : PuClass
+ createPackage() : PuPackage
+ createLink() : PuLink
}
'PlantUMLElementBuilder instantiates Elements'
PlantUMLElementBuilder ..> "ins" Element

abstract class Element << Component >> _COMPOSITE {
+ addElement(Element) {}
+ {abstract} print()
}

class Composite _COMPOSITE {
+ addElement(Element)
+ print()
}

Element <|.. Composite
Composite "1" o-- "n" Element

class Package<<ConcComposite1>> _COMPOSITE extends Composite {
    + {abstract} createClassDeclaration() : ClassDeclaration
}

class Class<<ConcComposite2>> _COMPOSITE extends Composite {
    + {abstract} createMethod() : Method
    + {abstract} createField() : Field
}

class ClassDeclaration << Leaf_1 >> _COMPOSITE extends Element {
+ print()
}

class Method << Leaf_2 >> _COMPOSITE extends Element {
+ parseGenericParamTypes(Type[] types)
}

class Field << Leaf_3 >> _COMPOSITE extends Element {
}

class Link << Leaf_4 >> _COMPOSITE extends Element {
+ print()
+ setA(Element el, String label)
+ setB(Element el, String label)
+ setLinkType(ELinkType type)
}

class PuPackage {
    + createClassDeclaration() : PuClassDeclaration
}

class PuClass {
    + createMethod() : PuMethod
    + createField() : PuField
}

Package <|-- PuPackage << ProductA_1 >>
PuClass << ProductA_2 >> -u-|> Class
ClassDeclaration <|-- PuClassDeclaration << ProductA_3 >>
Field <|-- PuField << ProductA_4 >>
Method <|-- PuMethod << ProductA_5 >>
Link <|-- PuLink << ProductA_6 >>

class java.lang.Package {
+ getName() : String
}

class java.lang.Class {
+ isInterface() : boolean
+ isEnum() : boolean
+ getSuperClass() : Class
}

java.lang.Package --o Package
java.lang.Class --o Class
java.lang.Class --o ClassDeclaration
java.lang.Field --o Field
java.lang.Method --o Method

legend right
    ProductA_x: PlantUML model products
    ProductB_x: Other UML model products
    ProductX_1: Package products
endlegend

@enduml

